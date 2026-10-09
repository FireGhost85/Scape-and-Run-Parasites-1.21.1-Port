package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Snow that falls (or is placed) on short and tall grass turns it into the snowy grass blocks of the mod. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class SnowGrassHandler {
    private static final int NATURAL_CHECK_RADIUS = 32;
    private static final int NATURAL_CHECKS_PER_PLAYER = 96;

    @SubscribeEvent
    public static void onSnowPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof Level world) || world.isClientSide) {
            return;
        }
        if (event.getPlacedBlock().getBlock() != Blocks.SNOW) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState replaced = event.getBlockSnapshot().getState();
        if (isShortGrass(replaced)) {
            world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 3);
            return;
        }
        if (isTallGrass(replaced)) {
            convertTallGrass(world, pos, replaced);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event) {
        Level world = event.getLevel();
        if (world.isClientSide) {
            return;
        }
        if (!world.isRaining()) {
            return;
        }
        if (world.getGameTime() % 10L != 0L) {
            return;
        }
        for (Player player : world.players()) {
            checkNaturalSnowAroundPlayer(world, player);
        }
    }

    private static void checkNaturalSnowAroundPlayer(Level world, Player player) {
        int centerX = (int)Math.floor(player.getX());
        int centerZ = (int)Math.floor(player.getZ());
        for (int i = 0; i < NATURAL_CHECKS_PER_PLAYER; ++i) {
            int x = centerX + world.random.nextInt(NATURAL_CHECK_RADIUS * 2 + 1) - NATURAL_CHECK_RADIUS;
            int z = centerZ + world.random.nextInt(NATURAL_CHECK_RADIUS * 2 + 1) - NATURAL_CHECK_RADIUS;
            BlockPos column = new BlockPos(x, 0, z);
            if (!world.hasChunkAt(column)) continue;
            BlockPos pos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column);
            if (!world.hasChunkAt(pos)) continue;
            BlockState state = world.getBlockState(pos);
            if (!isShortGrass(state) && !isTallGrass(state) || !canSnowHere(world, pos)) continue;
            if (isShortGrass(state)) {
                world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 3);
                continue;
            }
            convertTallGrass(world, pos, state);
        }
    }

    private static boolean canSnowHere(Level world, BlockPos pos) {
        if (!world.canSeeSky(pos.above())) {
            return false;
        }
        if (!world.getBiome(pos).value().coldEnoughToSnow(pos)) {
            return false;
        }
        return world.getBrightness(LightLayer.BLOCK, pos) < 10;
    }

    private static boolean isShortGrass(BlockState state) {
        return state.is(Blocks.SHORT_GRASS);
    }

    private static boolean isTallGrass(BlockState state) {
        return state.is(Blocks.TALL_GRASS);
    }

    private static void convertTallGrass(Level world, BlockPos pos, BlockState state) {
        BlockPos lowerPos = pos;
        BlockState lowerState = state;
        if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
            lowerPos = pos.below();
            lowerState = world.getBlockState(lowerPos);
        }
        if (!lowerState.is(Blocks.TALL_GRASS) || lowerState.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }
        world.removeBlock(lowerPos.above(), false);
        world.setBlock(lowerPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 3);
    }
}
