package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPAttributes;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/** Base of the SRP blocks. Mirrors 1.12 {@code BlockBase}: parasite block-break dislodgment event and spawn restriction. */
public class BlockBase extends Block {
    /** Entity types that may spawn on SRP blocks (1.12 {@code canEntitySpawn} was true only for {@code EntityParasiteBase}). */
    public static final TagKey<EntityType<?>> PARASITE_ENTITIES = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "parasites"));

    public static final MapCodec<BlockBase> CODEC = simpleCodec(BlockBase::new);

    public BlockBase(BlockBehaviour.Properties properties) {
        super(properties.isValidSpawn((state, level, pos, type) -> type.is(PARASITE_ENTITIES)));
    }

    public BlockBase(SRPMaterial material, float hardness, boolean tickRandom) {
        this(prop(material.props(hardness), tickRandom));
    }

    public BlockBase(SRPMaterial material, float hardness, boolean tickRandom, float resistance) {
        this(prop(material.props(hardness, resistance), tickRandom));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    /** 1.12 {@code Blocks.SNOW} (full block) and {@code Blocks.SNOW_LAYER} are {@code SNOW_BLOCK} and {@code SNOW} now. */
    public static boolean isSnow(BlockState state) {
        return state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK);
    }

    /**
     * Properties of a block copying another block's material, hardness, resistance and sound (1.12 stairs and walls
     * copied their model block). Random ticking is not copied.
     */
    public static BlockBehaviour.Properties modelProps(Block model) {
        BlockState modelState = model.defaultBlockState();
        BlockBehaviour.Properties p = BlockBehaviour.Properties.of().mapColor(model.defaultMapColor())
                .strength(model.defaultDestroyTime(), model.getExplosionResistance()).sound(modelState.getSoundType());
        if (modelState.requiresCorrectToolForDrops()) {
            p.requiresCorrectToolForDrops();
        }
        if (modelState.ignitedByLava()) {
            p.ignitedByLava();
        }
        return p;
    }

    protected static BlockBehaviour.Properties prop(BlockBehaviour.Properties p, boolean tickRandom) {
        return tickRandom ? p.randomTicks() : p;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        if (this.raisesBreakEvent()) {
            parasiteBlockBreak(level, pos, true);
        }
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    /** False for the SRP blocks that replaced {@code removedByPlayer} without calling the base class. */
    protected boolean raisesBreakEvent() {
        return true;
    }

    /**
     * The block-break dislodgment event of 1.12 {@code BlockBase.removedByPlayer}: a chance to spawn a parasite where an SRP
     * block was broken. {@code capCheck} is false for the falling blocks, which never tested the mob cap.
     */
    public static void parasiteBlockBreak(Level level, BlockPos pos, boolean capCheck) {
        if (level.isClientSide) {
            return;
        }
        ParasiteEventWorld.setDisloWorldPhase(level, SRPAttributes.EVENTPARABLOCKBR, SRPConfigSystems.chanceEventParaBlockB, 0, null);
        SRPSaveData dataLol = SRPSaveData.get(level);
        int goo = SRPConfigSystems.disloParasiteBlock ? dataLol.getCurrentCode(DimKeys.of(level), 25) : 0;
        if (goo != 0 && level.random.nextDouble() < SRPConfigSystems.disloParasiteBlockChance && level instanceof ServerLevel server) {
            if (capCheck) {
                int count = 0;
                for (net.minecraft.world.entity.Entity e : server.getAllEntities()) {
                    if (e instanceof EntityParasiteBase) {
                        ++count;
                    }
                }
                if (count > SRPConfig.worldMobCap) {
                    return;
                }
            }
            EntityParasiteBase halo = ParasiteEventEntity.getRandomFeral(level);
            if (goo >= SRPConfigSystems.disloParasiteBlockValue1) {
                halo = ParasiteEventEntity.getRandomPrimitive(level);
            }
            if (goo >= SRPConfigSystems.disloParasiteBlockValue2) {
                halo = ParasiteEventEntity.getRandomAdapted(level);
            }
            if (goo >= SRPConfigSystems.disloParasiteBlockValue3) {
                halo = ParasiteEventEntity.getRandomPure(level);
            }
            halo.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            halo.finalizeSpawn(server, level.getCurrentDifficultyAt(halo.blockPosition()), MobSpawnType.EVENT, null);
            server.addFreshEntity(halo);
            level.levelEvent(1026, halo.blockPosition(), 0);
            halo.particleStatus((byte) 7);
            halo.addEffect(new MobEffectInstance(SRPPotions.EPEL_E, 600, 0, false, false));
        }
    }
}
