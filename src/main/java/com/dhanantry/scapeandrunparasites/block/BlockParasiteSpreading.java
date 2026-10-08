package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/** Block that converts its surroundings or spreads the parasite biome on random ticks. */
public class BlockParasiteSpreading extends BlockBase {
    public static int blockParasiteCount;
    public boolean isInfestedBlock;

    public BlockParasiteSpreading(BlockBehaviour.Properties properties, boolean isInfested) {
        super(properties.randomTicks());
        this.isInfestedBlock = isInfested;
    }

    public BlockParasiteSpreading(SRPMaterial material, float hardness, boolean isInfested) {
        this(material.props(hardness), isInfested);
    }

    public BlockParasiteSpreading(SRPMaterial material, float hardness, boolean isInfested, float resistance) {
        this(material.props(hardness, resistance), isInfested);
    }

    /** The 1.12 metadata of the state: the infestation stage, or the variant ordinal. */
    public int getMetaFromState(BlockState state) {
        if (this instanceof IStagedBlock staged && state.hasProperty(staged.getStageProperty())) {
            return state.getValue(staged.getStageProperty());
        }
        if (this instanceof IVariantBlock<?> variant) {
            return state.getValue(variant.getVariantProperty()).ordinal();
        }
        return 0;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        BlockState aboveState = level.getBlockState(pos.above());
        boolean hasSnowAbove = isSnow(aboveState);
        if (this == SRPBlocks.HarlequinnGrass.get()) {
            if (hasSnowAbove) {
                level.setBlock(pos, SRPBlocks.LocsBlock.get().defaultBlockState(), 3);
                return;
            }
        } else if (this == SRPBlocks.HarleskinnBlock.get() && aboveState.is(SRPBlocks.LocsBlock.get())) {
            level.setBlock(pos, SRPBlocks.HarlequinnGrass.get().defaultBlockState(), 3);
            return;
        }
        if (!this.isInfestedBlock) {
            this.tick(state, level, pos, rand);
        } else if (rand.nextDouble() < 0.5) {
            BiomeParasiteBase biomePara = SRPBlockLinks.parasiteBiomeAt(level, pos);
            if (biomePara != null) {
                level.setBlock(pos, BlockIds.parse(biomePara.getDirt()), 3);
            } else {
                int heart = ParasiteEventWorld.canBiomeStillExist(level, pos, true);
                if (heart > 0) {
                    BlockParasiteSpreading.SpreadBiome(level, pos, heart, ParasiteEventWorld.canBiomeStillExistType(level, pos, true));
                }
            }
        } else if (rand.nextDouble() <= 0.05) {
            this.tick(state, level, pos, rand);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (this.isInfestedBlock) {
            BlockParasiteSpreading.canInfestBlock(level, pos, rand, this.getMetaFromState(state), true);
        } else if (SRPBlockLinks.isParasiteBiome(level, pos)) {
            BlockParasiteSpreading.spreadBiomeBlockStain(level, pos, rand);
        } else {
            int heart = ParasiteEventWorld.canBiomeStillExist(level, pos, true);
            if (heart > 0) {
                BlockParasiteSpreading.SpreadBiome(level, pos, heart, ParasiteEventWorld.canBiomeStillExistType(level, pos, true));
            }
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean flag = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (level.isClientSide) {
            return flag;
        }
        if (SRPConfigSystems.useEvolution && this.getMetaFromState(state) > 0) {
            SRPSaveData data = SRPSaveData.get(level);
            data.setTotalKills(DimKeys.of(level), -SRPConfigSystems.valueLossBlockStain, true, level, false, 19);
        }
        return flag;
    }

    public static void SpreadBiome(Level level, BlockPos pos, int age, int type) {
        if (!SRPConfigWorld.nodesActivated || !SRPConfigWorld.biomeRegster) {
            return;
        }
        SRPWorldData data = SRPWorldData.get(level);
        int distance = data.getDistanceSpreadByAge(age, false);
        for (int x = pos.getX() - 4; x <= pos.getX() + 4; ++x) {
            for (int z = pos.getZ() - 4; z <= pos.getZ() + 4; ++z) {
                BlockPos convert = new BlockPos(x, pos.getY(), z);
                if (data.isInRangeOfHeart(convert, distance) == -1
                        || ParasiteEventEntity.checkName(SRPBlockLinks.biomeName(level, convert), SRPConfigWorld.biomeBlackList, SRPConfigWorld.biomeBlackListInverted)
                        || SRPBlockLinks.isParasiteBiome(level, convert)) {
                    continue;
                }
                BlockParasiteSpreading.positionToParasiteBiome(level, convert, type);
            }
        }
    }

    public static void spreadBiomeBlockStain(Level level, BlockPos pos, RandomSource rand) {
        BiomeParasiteBase biomePara = SRPBlockLinks.parasiteBiomeAt(level, pos);
        if (biomePara == null) {
            return;
        }
        BlockPos helper = pos;
        int covertedBlocks = 0;
        for (int dir = 0; dir <= 5; ++dir) {
            helper = BlockParasiteSpreading.directionToSpread(pos, dir);
            BlockParasiteSpreading.spreadToAir(level, helper);
            blockParasiteCount += (covertedBlocks += biomePara.convertBlock(helper, level, rand));
        }
        if (covertedBlocks != 0 && SRPConfigSystems.useEvolution) {
            SRPSaveData.get(level).setTotalKills(DimKeys.of(level), SRPConfigSystems.valueBlock * covertedBlocks, true, level, true, 20);
        }
    }

    public static void spreadBiomeBlockTrunk(Level level, BlockPos pos, RandomSource rand) {
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        int posx = pos.getX();
        int posz = pos.getZ();
        int posy = pos.getY();
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = -1; y <= 1; ++y) {
                    BlockPos helper = new BlockPos(posx + x, posy + y, posz + z);
                    BlockState lookingState = level.getBlockState(helper);
                    if (!lookingState.is(BlockTags.LOGS_THAT_BURN) || lookingState.is(SRPBlocks.ParasiteTrunk.get())) {
                        continue;
                    }
                    level.setBlock(helper, SRPBlocks.ParasiteTrunk.get().defaultBlockState(), 3);
                    BlockParasiteSpreading.spreadToAir(level, helper);
                    if (!SRPConfigSystems.useEvolution) {
                        continue;
                    }
                    SRPSaveData.get(level).setTotalKills(DimKeys.of(level), SRPConfigSystems.valueBlock, true, level, true, 21);
                }
            }
        }
    }

    /** Sets the biome of the column (4x4 biome cell) at the position to the parasite biome of the given type. */
    public static void positionToParasiteBiome(Level level, BlockPos pos, int type) {
        SRPBlockLinks.setParasiteBiome(level, pos, type);
    }

    public static BlockPos directionToSpread(BlockPos atm, int choice) {
        switch (choice) {
            case 0:
                atm = atm.north();
                break;
            case 1:
                atm = atm.east();
                break;
            case 2:
                atm = atm.west();
                break;
            case 3:
                atm = atm.south();
                break;
            case 4:
                atm = atm.above();
                break;
            default:
                atm = atm.below();
        }
        return atm;
    }

    public static void spreadToAir(Level level, BlockPos atm) {
    }

    public static void canInfestBlock(Level level, BlockPos pos, RandomSource rand, int stage, boolean fromVenkrol) {
        BeckonBlockInfestation.beckonInfestation(level, pos, rand, stage, fromVenkrol);
    }
}
