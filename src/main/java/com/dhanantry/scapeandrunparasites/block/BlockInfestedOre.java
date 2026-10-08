package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** Infested ore (eight variants); drops its own variant item. */
public class BlockInfestedOre extends BlockBase implements IVariantBlock<BlockInfestedOre.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockInfestedOre(SRPMaterial material, float hardness, boolean tickRandom) {
        super(prop(material.props(hardness).sound(SRPSoundTypes.INFEST), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.CO));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    protected boolean raisesBreakEvent() {
        return false;
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
        if (!level.getBlockState(pos.above()).isAir() && !level.getBlockState(pos.above()).is(SRPBlocks.ParasiteBush.get())) {
            return;
        }
        if (rand.nextDouble() <= (double) SRPConfigSystems.rsBlockParticleS) {
            double d0 = pos.getX() + rand.nextDouble();
            double d1 = pos.getY() + 2.5;
            double d2 = pos.getZ() + rand.nextDouble();
            BlockClientHooks.particle(SRPEnumParticle.SPORE, d0, d1, d2, 0.0, 0.0, 0.0, 0, 0, 0);
        }
    }

    public enum EnumType implements StringRepresentable {
        CO,
        DIA,
        EME,
        GOL,
        IRO,
        LAP,
        RED,
        UN;

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
