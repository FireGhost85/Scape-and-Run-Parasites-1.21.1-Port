package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;

/** Parasite loot chest: only SRP items fit in; the contents are destroyed when it is broken. */
public class BlockParasiteLoot extends BlockBase implements EntityBlock, IVariantBlock<BlockParasiteLoot.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockParasiteLoot(SRPMaterial material, float hardness, boolean tickRandom) {
        super(prop(material.props(hardness).sound(SRPSoundTypes.FLESH).lightLevel(s -> 10), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.COMMON));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityParasiteLoot(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityParasiteLoot loot) {
            player.openMenu(loot);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityParasiteLoot chest) {
                chest.clearContent();
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.parasite_loot").withStyle(ChatFormatting.GREEN));
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
        COMMON,
        UNCOMMON,
        RARE;

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
