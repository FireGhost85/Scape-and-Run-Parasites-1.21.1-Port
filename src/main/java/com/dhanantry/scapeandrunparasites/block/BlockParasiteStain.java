package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** Parasite stain ground with seven variants. */
public class BlockParasiteStain extends BlockParasiteSpreading implements IVariantBlock<BlockParasiteStain.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockParasiteStain(SRPMaterial material, float hardness, boolean infested) {
        super(material.props(hardness).sound(SRPSoundTypes.FLESH), infested);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.DIRT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity) {
        switch (state.getValue(VARIANT)) {
            case DIRT:
                return SoundType.GRAVEL;
            case MUD:
                return SoundType.SLIME_BLOCK;
            case SPORE:
                return SoundType.GRASS;
            default:
                return SRPSoundTypes.FLESH;
        }
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

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        BlockClientHooks.spore(level, pos, rand);
    }

    public enum EnumType implements StringRepresentable {
        DIRT,
        MUD,
        FLESH,
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
