package com.dhanantry.scapeandrunparasites.block.slabs;

import com.dhanantry.scapeandrunparasites.block.SRPMaterial;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** Parasite rubble slab ({@code parasiterubbleslabhalf}; the double slab is the {@code double} slab type). Hardness 2.3, pickaxe 1. */
public class BlockSlabRubble extends BlockSlabBase<BlockSlabRubble.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockSlabRubble(SRPMaterial material, float hardness) {
        super(material.props(hardness));
        this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, EnumType.BONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VARIANT);
    }

    @Override
    public EnumType[] getVariants() {
        return EnumType.values();
    }

    @Override
    public EnumProperty<EnumType> getVariantProperty() {
        return VARIANT;
    }

    public enum EnumType implements StringRepresentable {
        FLESH,
        BONE,
        STONE,
        STONEDEBRIS,
        WOOD,
        BRICKS,
        METAL,
        OBSIDIAN;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        @Override
        public String toString() {
            return this.getSerializedName();
        }
    }
}
