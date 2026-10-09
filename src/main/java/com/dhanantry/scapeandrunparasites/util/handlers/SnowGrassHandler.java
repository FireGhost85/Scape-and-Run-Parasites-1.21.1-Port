package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class SnowGrassHandler {
    private static final int NATURAL_CHECK_RADIUS = 32;
    private static final int NATURAL_CHECKS_PER_PLAYER = 96;

    @SubscribeEvent
    public static void onSnowPlaced(BlockEvent.PlaceEvent event) {
        Level world = event.getLevel();
        if (world.isClientSide) {
            return;
        }
        if (event.getPlacedBlock().getBlock() != Blocks.SNOW) {
            return;
        }
        BlockPos pos = event.getPos();
        BlockState replaced = event.getBlockSnapshot().getReplacedBlock();
        if (SnowGrassHandler.isShortGrass(replaced)) {
            world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 3);
            return;
        }
        if (SnowGrassHandler.isTallGrass(replaced)) {
            SnowGrassHandler.convertTallGrass(world, pos, replaced);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Level world = event.world;
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
            SnowGrassHandler.checkNaturalSnowAroundPlayer(world, player);
        }
    }

    private static void checkNaturalSnowAroundPlayer(Level world, Player player) {
        int centerX = (int)Math.floor(player.getX());
        int centerZ = (int)Math.floor(player.getZ());
        for (int i = 0; i < 96; ++i) {
            BlockState state;
            BlockPos pos;
            int z;
            int x = centerX + world.random.nextInt(65) - 32;
            BlockPos column = BlockPos.containing(x, 0, z = centerZ + world.random.nextInt(65) - 32);
            if (!world.hasChunkAt(column) || !world.hasChunkAt(pos = world.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column)) || !SnowGrassHandler.isShortGrass(state = world.getBlockState(pos)) && !SnowGrassHandler.isTallGrass(state) || !SnowGrassHandler.canSnowHere(world, pos)) continue;
            if (SnowGrassHandler.isShortGrass(state)) {
                world.setBlock(pos, SRPBlocks.SnowShortGrass.get().defaultBlockState(), 3);
                continue;
            }
            SnowGrassHandler.convertTallGrass(world, pos, state);
        }
    }

    private static boolean canSnowHere(Level world, BlockPos pos) {
        if (!world.canSeeSky(pos.above())) {
            return false;
        }
        if (world.getBiome(pos).value().getFloatTemperature(pos) > 0.15f) {
            return false;
        }
        if (world.getBrightness(LightLayer.BLOCK, pos) >= 10) {
            return false;
        }
        return world.dimensionType().canDoRainSnowIce(world.getChunkFromBlockCoords(pos));
    }

    private static boolean isShortGrass(BlockState state) {
        return state.getBlock() == Blocks.SHORT_GRASS && state.getValue((Property)BlockTallGrass.TYPE) == BlockTallGrass.EnumType.GRASS;
    }

    private static boolean isTallGrass(BlockState state) {
        if (state.getBlock() != Blocks.TALL_GRASS) {
            return false;
        }
        if (state.getValue((Property)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            return true;
        }
        return state.getValue((Property)BlockDoublePlant.VARIANT) == BlockDoublePlant.EnumPlantType.GRASS;
    }

    private static void convertTallGrass(Level world, BlockPos pos, BlockState state) {
        BlockPos lowerPos = pos;
        BlockState lowerState = state;
        if (state.getValue((Property)BlockDoublePlant.HALF) == BlockDoublePlant.EnumBlockHalf.UPPER) {
            lowerPos = pos.below();
            lowerState = world.getBlockState(lowerPos);
        }
        if (lowerState.getBlock() != Blocks.TALL_GRASS) {
            return;
        }
        if (lowerState.getValue((Property)BlockDoublePlant.HALF) != BlockDoublePlant.EnumBlockHalf.LOWER) {
            return;
        }
        if (lowerState.getValue((Property)BlockDoublePlant.VARIANT) != BlockDoublePlant.EnumPlantType.GRASS) {
            return;
        }
        world.removeBlock(lowerPos.above(), false);
        world.setBlock(lowerPos, SRPBlocks.SnowTallGrass.get().defaultBlockState(), 3);
    }
}

