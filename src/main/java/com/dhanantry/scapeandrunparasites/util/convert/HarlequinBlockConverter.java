package com.dhanantry.scapeandrunparasites.util.convert;

import java.util.Collection;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public final class HarlequinBlockConverter {
    public static Block convert(Level world, BlockPos pos, BlockState state, boolean blotchHere, RandomSource rand, Config cfg) {
        Block b = state.getBlock();
        Material mat = state.getMaterial();
        if (mat == Material.air || mat == Material.water || mat == Material.lava) {
            return null;
        }
        if (b instanceof BlockLeaves) {
            BlockPos below;
            world.setBlock(pos, cfg.ALVEOLI.defaultBlockState(), 2);
            if (rand.nextInt(100) < 30 && world.isEmptyBlock(below = pos.below())) {
                BlockState growth = HarlequinBlockConverter.setFacingIfPresent(cfg.ALVEOLI_GROWTH.defaultBlockState(), Direction.DOWN);
                world.setBlock(below, growth, 2);
            }
            return cfg.ALVEOLI;
        }
        if (b == Blocks.OAK_PLANKS || b == Blocks.COBBLESTONE || b == Blocks.MOSSY_COBBLESTONE) {
            world.setBlock(pos, cfg.HARLESKINN.defaultBlockState(), 2);
            return cfg.HARLESKINN;
        }
        if (b == Blocks.GRASS_BLOCK) {
            if (blotchHere) {
                world.setBlock(pos, cfg.HARLESKINN.defaultBlockState(), 2);
                BlockPos up = pos.above();
                Block above = world.getBlockState(up).getBlock();
                if (above == cfg.TRESSES || above == cfg.HIRSUTE) {
                    world.removeBlock(up, false);
                }
                return cfg.HARLESKINN;
            }
            world.setBlock(pos, cfg.HARLEQUINN_GRASS.defaultBlockState(), 2);
            HarlequinBlockConverter.maybePlaceHair(world, pos, rand, cfg);
            return cfg.HARLEQUINN_GRASS;
        }
        if (HarlequinBlockConverter.isTopSunlit(world, pos) && (HarlequinBlockConverter.isAnySand(state) || HarlequinBlockConverter.isAnySandstone(state) || b == Blocks.STONE)) {
            if (blotchHere) {
                world.setBlock(pos, cfg.HARLESKINN.defaultBlockState(), 2);
                return cfg.HARLESKINN;
            }
            world.setBlock(pos, cfg.HARLEQUINN_GRASS.defaultBlockState(), 2);
            HarlequinBlockConverter.maybePlaceHair(world, pos, rand, cfg);
            return cfg.HARLEQUINN_GRASS;
        }
        if (HarlequinBlockConverter.isAnySand(state) || HarlequinBlockConverter.isAnySandstone(state) || b == Blocks.STONE) {
            world.setBlock(pos, cfg.HARLESKINN.defaultBlockState(), 2);
            return cfg.HARLESKINN;
        }
        return null;
    }

    private static void maybePlaceHair(Level world, BlockPos pos, RandomSource rand, Config cfg) {
        BlockPos up = pos.above();
        if (world.isEmptyBlock(up) && HarlequinBlockConverter.isTopSunlit(world, pos)) {
            int roll = rand.nextInt(100);
            boolean canTresses = HarlequinBlockConverter.canSustain(world, cfg.HARLEQUINN_GRASS.defaultBlockState(), pos, cfg.TRESSES);
            boolean canHirsute = HarlequinBlockConverter.canSustain(world, cfg.HARLEQUINN_GRASS.defaultBlockState(), pos, cfg.HIRSUTE);
            if (roll < 60 && canTresses && world.isEmptyBlock(up.above())) {
                HarlequinBlockConverter.placeDoublePlant(world, up, cfg.TRESSES);
            } else if (roll < 80 && canHirsute) {
                world.setBlock(up, cfg.HIRSUTE.defaultBlockState(), 2);
            }
        }
    }

    private static boolean isTopSunlit(Level world, BlockPos pos) {
        return world.canSeeSky(pos.above());
    }

    private static boolean isAnySand(BlockState s) {
        return s.getBlock() instanceof BlockSand;
    }

    private static boolean isAnySandstone(BlockState s) {
        return s.getBlock() instanceof BlockSandStone || s.getBlock() == Blocks.RED_SANDSTONE;
    }

    private static boolean canSustain(Level world, BlockState ground, BlockPos pos, Block plant) {
        if (!(plant instanceof IPlantable)) {
            return true;
        }
        try {
            return ground.getBlock().canSustainPlant(ground, (BlockGetter)world, pos, Direction.UP, (IPlantable)plant);
        }
        catch (Throwable t) {
            return true;
        }
    }

    private static BlockState setFacingIfPresent(BlockState state, Direction face) {
        Collection props = state.getPropertyNames();
        for (Property p : props) {
            Property pf;
            if (!p.getName().equalsIgnoreCase("facing") || p.getValueClass() != Direction.class || !(pf = p).getAllowedValues().contains(face)) continue;
            return state.setValue(pf, face);
        }
        return state;
    }

    private static void placeDoublePlant(Level world, BlockPos pos, Block plant) {
        try {
            if (plant instanceof BlockDoublePlant) {
                BlockDoublePlant bdp = (BlockDoublePlant)plant;
                world.setBlock(pos, bdp.defaultBlockState().setValue((Property)BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.LOWER), 2);
                world.setBlock(pos.above(), bdp.defaultBlockState().setValue((Property)BlockDoublePlant.HALF, BlockDoublePlant.EnumBlockHalf.UPPER), 2);
            } else {
                world.setBlock(pos, plant.defaultBlockState(), 2);
            }
        }
        catch (Throwable ignored) {
            world.setBlock(pos, plant.defaultBlockState(), 2);
        }
    }

    private HarlequinBlockConverter() {
    }

    public static final class Config {
        public final Block HARLESKINN;
        public final Block HARLEQUINN_GRASS;
        public final Block ALVEOLI;
        public final Block ALVEOLI_GROWTH;
        public final Block LIPOMA;
        public final Block TRESSES;
        public final Block HIRSUTE;

        public Config(Block harleskinn, Block harlequinnGrass, Block alveoli, Block alveoliGrowth, Block lipoma, Block tresses, Block hirsute) {
            this.HARLESKINN = harleskinn;
            this.HARLEQUINN_GRASS = harlequinnGrass;
            this.ALVEOLI = alveoli;
            this.ALVEOLI_GROWTH = alveoliGrowth;
            this.LIPOMA = lipoma;
            this.TRESSES = tresses;
            this.HIRSUTE = hirsute;
        }
    }
}

