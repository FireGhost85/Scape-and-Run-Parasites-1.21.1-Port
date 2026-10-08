package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WebBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

/** SRP web: a cobweb that vanishes on a random tick; the higher variants hurt like a cactus. */
public class BlockWebBase extends WebBlock implements IVariantBlock<BlockWebBase.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockWebBase(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.ONE));
    }

    public BlockWebBase() {
        this(SRPMaterial.WEB.props(4.0f).sound(SoundType.STONE).noCollission().randomTicks());
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            if (entity.tickCount % 20 != 0) {
                return;
            }
            if (!(entity instanceof EntityParasiteBase) && entity instanceof LivingEntity target) {
                switch (state.getValue(VARIANT)) {
                    case ONE:
                        break;
                    case THREE:
                        target.hurt(level.damageSources().cactus(), 8.0f);
                        break;
                    case TWO:
                        target.hurt(level.damageSources().cactus(), 4.0f);
                        break;
                }
            }
        }
        super.entityInside(state, level, pos, entity);
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
        ONE,
        TWO,
        THREE;

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
