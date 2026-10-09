package com.dhanantry.scapeandrunparasites.util;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SpongeBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The 1.12 {@code Material} as a query: the world generation and conversion code of the original asks a block state for
 * its material. {@link #of} maps a 1.21 state to the closest 1.12 material (there is no Material class in 1.21).
 */
public enum LegacyMaterial {
    air, grass, ground, wood, rock, iron, anvil, water, lava, leaves, plants, vine, sponge, cloth, fire, sand, circuits, carpet, glass,
    redstoneLight, tnt, coral, ice, packedIce, snow, craftedSnow, cactus, clay, gourd, dragonEgg, portal, cake, web, piston, barrier, structureVoid;

    public static LegacyMaterial of(BlockState s) {
        if (s.isAir()) {
            return air;
        }
        if (s.getBlock() instanceof LiquidBlock) {
            return s.getFluidState().is(FluidTags.LAVA) ? lava : water;
        }
        if (s.is(BlockTags.LEAVES)) return leaves;
        if (s.is(BlockTags.LOGS) || s.is(BlockTags.PLANKS) || s.is(BlockTags.WOODEN_STAIRS) || s.is(BlockTags.WOODEN_SLABS) || s.is(BlockTags.WOODEN_FENCES) || s.is(BlockTags.WOODEN_DOORS)) return wood;
        if (s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.MYCELIUM)) return grass;
        if (s.is(BlockTags.DIRT)) return ground;
        if (s.is(BlockTags.SAND)) return sand;
        if (s.is(Blocks.CLAY)) return clay;
        if (s.is(Blocks.SNOW_BLOCK)) return craftedSnow;
        if (s.is(Blocks.SNOW)) return snow;
        if (s.is(Blocks.PACKED_ICE)) return packedIce;
        if (s.is(BlockTags.ICE)) return ice;
        if (s.getBlock() instanceof AbstractGlassBlock || s.getBlock() instanceof IronBarsBlock && s.is(BlockTags.IMPERMEABLE)) return glass;
        if (s.getBlock() instanceof VineBlock) return vine;
        if (s.getBlock() instanceof CactusBlock) return cactus;
        if (s.getBlock() instanceof SpongeBlock) return sponge;
        if (s.getBlock() instanceof WebBlock) return web;
        if (s.getBlock() instanceof BushBlock) return plants;
        if (s.is(Blocks.IRON_BLOCK) || s.is(Blocks.IRON_BARS) || s.is(Blocks.IRON_DOOR) || s.is(Blocks.IRON_TRAPDOOR)) return iron;
        if (s.is(Blocks.FIRE) || s.is(Blocks.SOUL_FIRE)) return fire;
        if (s.is(Blocks.TNT)) return tnt;
        if (s.is(BlockTags.WOOL)) return cloth;
        if (s.is(BlockTags.WOOL_CARPETS)) return carpet;
        if (s.is(Blocks.PUMPKIN) || s.is(Blocks.MELON)) return gourd;
        if (s.is(Blocks.NETHER_PORTAL) || s.is(Blocks.END_PORTAL)) return portal;
        if (s.is(Blocks.BARRIER)) return barrier;
        if (s.is(Blocks.STRUCTURE_VOID)) return structureVoid;
        if (s.is(Blocks.PISTON) || s.is(Blocks.STICKY_PISTON) || s.is(Blocks.PISTON_HEAD)) return piston;
        if (s.is(BlockTags.MINEABLE_WITH_SHOVEL)) return ground;
        return rock;
    }

    public boolean isLiquid() {
        return this == water || this == lava;
    }
}
