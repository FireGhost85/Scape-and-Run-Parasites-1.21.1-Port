package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.property.Properties;

/** Bookshelf with enchanting power bonus 1 (block tag {@code minecraft:enchantment_power_provider}). */
public class BlockSRPBookshelf extends Block {
    public static final MapCodec<BlockSRPBookshelf> CODEC = simpleCodec(BlockSRPBookshelf::new);

    public BlockSRPBookshelf(Properties properties) {
        super(properties);
    }

    public BlockSRPBookshelf() {
        this(SRPMaterial.WOOD.props(1.5f, 7.5f).sound(SoundType.WOOD));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }
}
