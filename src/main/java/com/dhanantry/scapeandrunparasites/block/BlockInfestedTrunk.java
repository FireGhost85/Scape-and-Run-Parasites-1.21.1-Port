package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

/** Infested trunk: turns into stain in parasite biomes, otherwise infests its surroundings. */
public class BlockInfestedTrunk extends RotatedPillarBlock {
    public BlockInfestedTrunk(SRPMaterial material, float hardness, boolean tickRandom) {
        super(BlockBase.prop(material.props(hardness).sound(SoundType.WOOD).noOcclusion(), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean flag = super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
        if (level.isClientSide) {
            return flag;
        }
        if (SRPConfigSystems.useEvolution && state.getValue(AXIS) != Direction.Axis.Y) {
            SRPSaveData data = SRPSaveData.get(level);
            data.setTotalKills(DimKeys.of(level), -SRPConfigSystems.valueLossBlockTrunk, true, level, false, 22);
        }
        return flag;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        if (rand.nextDouble() < 0.5) {
            if (SRPBlockLinks.isParasiteBiome(level, pos)) {
                level.setBlock(pos, SRPBlocks.ParasiteStain.get().defaultBlockState(), 3);
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
        if (!level.hasChunksAt(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            return;
        }
        BlockParasiteSpreading.canInfestBlock(level, pos, rand, 2, true);
    }
}
