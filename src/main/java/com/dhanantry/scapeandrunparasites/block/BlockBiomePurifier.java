package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationaryArchitect;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.PureParticlesPayload;
import com.dhanantry.scapeandrunparasites.network.registration.BlocksPayloads;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

/** Biome purifier: used with an empty hand it restores the natural biome around it; ticking it enrages nearby parasites and boosts infested blocks. */
public class BlockBiomePurifier extends BlockBase {
    public BlockBiomePurifier(SRPMaterial material, float hardness, boolean tickRandom, float resistance) {
        super(prop(material.props(hardness, resistance).sound(SRPSoundTypes.PURIFIER), tickRandom));
    }

    @Override
    protected boolean raisesBreakEvent() {
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.PASS;
        }
        if (!player.getMainHandItem().isEmpty()) {
            return InteractionResult.PASS;
        }
        ServerLevel server = (ServerLevel) level;
        level.playSound(null, pos, SRPSounds.PURIFIER_USE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        BlockBiomePurifier.killBiome(level, pos, 16);
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        double ax = pos.getX() + 0.5;
        double ay = pos.getY() + 0.1;
        double az = pos.getZ() + 0.5;
        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersNear(server, null, ax, ay, az, 64.0, new PureParticlesPayload(ax, ay, az, 24, 0));
        double bx = pos.getX() + 0.5;
        double by = pos.getY() + 0.9;
        double bz = pos.getZ() + 0.5;
        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersNear(server, null, bx, by, bz, 64.0, new PureParticlesPayload(bx, by, bz, 24, 1));
        double cx = pos.getX() + 0.5;
        double cy = pos.getY() + 0.5;
        double cz = pos.getZ() + 0.5;
        com.dhanantry.scapeandrunparasites.network.SRPSend.sendToPlayersNear(server, null, cx, cy, cz, 64.0, new BlocksPayloads.Flash(cx, cy, cz, 0, 20, 20));
        return InteractionResult.PASS;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        ParasiteEventWorld.setDisloWorldPhase(level, SRPAttributes.EVENTPARAPURIFIER, SRPConfigSystems.chanceEventParaPurifier, 0, null);
        int raan = 32;
        AABB axisalignedbb = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(raan);
        List<EntityParasiteBase> moblist = level.getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        boolean oneTime = true;
        if (!moblist.isEmpty()) {
            for (EntityParasiteBase mob : moblist) {
                if (oneTime && mob instanceof EntityPStationaryArchitect) {
                    mob.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, false, false));
                    oneTime = false;
                }
                mob.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 600, 1, false, false));
            }
        }
        int i1 = pos.getY();
        double l1 = pos.getX();
        double i2 = pos.getZ();
        int BGrange = raan = 5;
        int BGheight = raan;
        for (int k2 = -1 * BGrange; k2 <= BGrange; ++k2) {
            for (int l2 = -1 * BGrange; l2 <= BGrange; ++l2) {
                for (int j = -1 * BGheight; j <= BGheight; ++j) {
                    double i3 = l1 + (double) k2;
                    double k = i1 + j;
                    double l = i2 + (double) l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = level.getBlockState(blockpos);
                    if (iblockstate.is(SRPBlocks.InfestedStain.get())) {
                        BlockState staged = SRPBlocks.InfestedStain.get().defaultBlockState().setValue(BlockInfestedStain.STAGE, 5);
                        level.setBlock(blockpos, staged, 3);
                        level.scheduleTick(blockpos, staged.getBlock(), 40);
                    }
                    if (!iblockstate.is(SRPBlocks.InfestedRubble.get())) {
                        continue;
                    }
                    BlockState staged = SRPBlocks.InfestedRubble.get().defaultBlockState().setValue(BlockInfestedRubble.STAGE, 5);
                    level.setBlock(blockpos, staged, 3);
                    level.scheduleTick(blockpos, staged.getBlock(), 40);
                }
            }
        }
    }

    public static void killBiome(Level level, BlockPos pos, int range) {
        for (int x = pos.getX() - range; x <= pos.getX() + range; ++x) {
            for (int z = pos.getZ() - range; z <= pos.getZ() + range; ++z) {
                BlockPos convert = new BlockPos(x, pos.getY(), z);
                if (!SRPBlockLinks.isParasiteBiome(level, convert)) {
                    continue;
                }
                SRPBlockLinks.restoreNaturalBiome(level, convert, pos);
            }
        }
    }
}
