package com.dhanantry.scapeandrunparasites.block.slabs;

import com.dhanantry.scapeandrunparasites.block.SRPMaterial;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** Parasite stain slab ({@code parasitestainslabhalf}). Hardness 0.8, shovel 0, infestation sound type. */
public class BlockSlabStain extends BlockSlabBase<BlockSlabStain.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockSlabStain(SRPMaterial material, float hardness) {
        super(material.props(hardness).sound(SRPSoundTypes.INFEST));
        this.registerDefaultState(this.defaultBlockState().setValue(VARIANT, EnumType.DIRT));
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
        DIRT,
        MUD,
        SFLESH,
        FEELER,
        SPORE,
        RED,
        SACKFLESH;

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
