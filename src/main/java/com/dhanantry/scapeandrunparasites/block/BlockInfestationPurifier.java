package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.PureParticlesPayload;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Vector3f;

/**
 * Infestation purifier: right click with a diamond on top of SRP blocks starts the purification of the connected infested
 * blocks ({@link TileEntityInfestationPurifier}). When Beckons stand on SRP blocks connected to the one below the purifier (within
 * 200 blocks) it only marks them with glowing instead. Hardness 2, sound type purifier.
 */
public class BlockInfestationPurifier extends BlockBase implements EntityBlock {
    private static final int ENTITY_SEARCH_RADIUS = 200;
    private static final int CONNECT_MAX_NODES = 40000;
    private static final String MAPPING_FILE = "srparasites_purify_mappings.txt";
    private static final ResourceLocation[] BECKON_IDS = new ResourceLocation[]{
            ResourceLocation.fromNamespaceAndPath("srparasites", "beckon_si"), ResourceLocation.fromNamespaceAndPath("srparasites", "beckon_sii"),
            ResourceLocation.fromNamespaceAndPath("srparasites", "beckon_siii"), ResourceLocation.fromNamespaceAndPath("srparasites", "beckon_siv")};

    public BlockInfestationPurifier() {
        super(SRPMaterial.ROCK.props(2.0f).sound(SRPSoundTypes.PURIFIER));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityInfestationPurifier(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != SRPBlockEntities.INFESTATION_PURIFIER.get() ? null
                : (lvl, pos, st, be) -> ((TileEntityInfestationPurifier) be).update();
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        boolean hasDiamond = !held.isEmpty() && held.is(Items.DIAMOND);
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        PurifyMappings.ensureLoaded(level, MAPPING_FILE);
        if (!hasDiamond) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.srparasites.purifier_use_diamond"), true);
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (!PurifyMappings.isSrp(belowState)) {
            if (!level.isClientSide) {
                player.displayClientMessage(Component.translatable("message.srparasites.purifier_not_over_srp"), true);
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        int connectedCount = this.checkAndGlowConnectedBeckons(level, pos, below);
        if (connectedCount > 0) {
            player.displayClientMessage(Component.translatable("message.srparasites.purifier_entities_marked", connectedCount), true);
            return ItemInteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof TileEntityInfestationPurifier te && level instanceof ServerLevel server) {
            level.playSound(null, pos, SRPSounds.PURIFIER_USE.get(), SoundSource.BLOCKS, 0.9f, 1.0f);
            double cx = pos.getX() + 0.5;
            double cy = pos.getY() + 0.9;
            double cz = pos.getZ() + 0.5;
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersNear(server, null, cx, cy, cz, 64.0, new PureParticlesPayload(cx, cy, cz, 24, 1));
            this.spawnStartPulse(server, pos);
            te.startAt(below, player.getUUID());
            player.displayClientMessage(Component.translatable("message.srparasites.purifier_started"), true);
        }
        return ItemInteractionResult.SUCCESS;
    }

    private int checkAndGlowConnectedBeckons(Level level, BlockPos pos, BlockPos startSrp) {
        AABB box = new AABB(pos.getX() - ENTITY_SEARCH_RADIUS, pos.getY() - 96, pos.getZ() - ENTITY_SEARCH_RADIUS,
                pos.getX() + ENTITY_SEARCH_RADIUS + 1, pos.getY() + 96, pos.getZ() + ENTITY_SEARCH_RADIUS + 1);
        Map<BlockPos, List<LivingEntity>> beckonsByFoot = new HashMap<>();
        level.getEntities((Entity) null, box, e -> {
            if (!(e instanceof LivingEntity)) {
                return false;
            }
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType());
            boolean isBeckon = false;
            for (ResourceLocation beckon : BECKON_IDS) {
                isBeckon |= beckon.equals(id);
            }
            if (!isBeckon) {
                return false;
            }
            BlockPos under = new BlockPos(Mth.floor(e.getX()), Mth.floor(e.getY()) - 1, Mth.floor(e.getZ()));
            if (!PurifyMappings.isSrp(level.getBlockState(under))) {
                return false;
            }
            beckonsByFoot.computeIfAbsent(under, k -> new ArrayList<>()).add((LivingEntity) e);
            return true;
        });
        if (beckonsByFoot.isEmpty()) {
            return 0;
        }
        return this.bfsGlowConnected(level, startSrp, beckonsByFoot);
    }

    private int bfsGlowConnected(Level level, BlockPos start, Map<BlockPos, List<LivingEntity>> beckonsByFoot) {
        ArrayDeque<BlockPos> q = new ArrayDeque<>();
        HashSet<Long> seen = new HashSet<>();
        int maxDistSq = 40000;
        q.add(start);
        seen.add(start.asLong());
        HashSet<Long> targets = new HashSet<>();
        for (BlockPos bp : beckonsByFoot.keySet()) {
            targets.add(bp.asLong());
        }
        int nodes = 0;
        int connected = 0;
        while (!q.isEmpty() && nodes < CONNECT_MAX_NODES && !targets.isEmpty()) {
            BlockPos cur = q.poll();
            ++nodes;
            long curKey = cur.asLong();
            if (targets.contains(curKey)) {
                List<LivingEntity> here = beckonsByFoot.get(cur);
                if (here != null) {
                    for (LivingEntity elb : here) {
                        elb.addEffect(new MobEffectInstance(MobEffects.GLOWING, 600, 0, false, true));
                    }
                    connected += here.size();
                }
                targets.remove(curKey);
            }
            for (Direction f : Direction.values()) {
                BlockPos nxt = cur.relative(f);
                long k = nxt.asLong();
                if (!seen.add(k) || start.distSqr(nxt) > maxDistSq || !PurifyMappings.isSrp(level.getBlockState(nxt))) {
                    continue;
                }
                q.add(nxt);
            }
        }
        return connected;
    }

    /** Copies every property that both states have (facing, half, shape, axis...) from the infested to the vanilla state. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static BlockState tryCopyCommonProps(BlockState from, BlockState to) {
        BlockState out = to;
        for (Property propTo : to.getProperties()) {
            if (!from.getProperties().contains(propTo)) {
                continue;
            }
            try {
                Comparable val = from.getValue(propTo);
                if (val == null || !propTo.getPossibleValues().contains(val)) {
                    continue;
                }
                out = out.setValue(propTo, val);
            } catch (Exception ignored) {
            }
        }
        return out;
    }

    private void spawnStartPulse(ServerLevel level, BlockPos pos) {
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.8;
        double cz = pos.getZ() + 0.5;
        DustParticleOptions blue = new DustParticleOptions(new Vector3f(0.1f, 0.35f, 1.0f), 1.0f);
        for (int i = 0; i < 28; ++i) {
            double angle = Math.PI * 2 * i / 28.0;
            double radius = 0.9;
            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;
            level.sendParticles(blue, x, cy, z, 1, 0.0, 0.0, 0.0, 0.0);
            level.sendParticles(ParticleTypes.PORTAL, x, cy + 0.05, z, 1, 0.0, 0.01, 0.0, 0.0);
        }
    }
}
