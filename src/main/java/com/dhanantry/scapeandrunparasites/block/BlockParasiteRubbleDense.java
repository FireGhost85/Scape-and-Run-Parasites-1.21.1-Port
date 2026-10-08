package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** Dense parasite rubble (wall, biome and colony variants). */
public class BlockParasiteRubbleDense extends BlockBase implements IVariantBlock<BlockParasiteRubbleDense.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockParasiteRubbleDense(SRPMaterial material, float hardness, boolean tickRandom) {
        super(material, hardness, tickRandom);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.WALL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
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

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return this.variantStack(state);
    }

    public enum EnumType implements StringRepresentable {
        WALL,
        BIOME,
        COLONY;

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
