package com.dhanantry.scapeandrunparasites.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;

public class BlockAssimilatedPumpkin extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<BlockAssimilatedPumpkin> CODEC = MapCodec.unit(() -> new BlockAssimilatedPumpkin(false));

    public BlockAssimilatedPumpkin(boolean glowing) {
        super(properties(glowing));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    private static BlockBehaviour.Properties properties(boolean glowing) {
        BlockBehaviour.Properties p = BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).strength(1.0f).sound(SoundType.WOOD).noOcclusion();
        return glowing ? p.lightLevel(s -> 15) : p;
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    /** Block item that can be worn on the head; right-clicking with an empty head slot equips it. */
    public static class ItemAssimilatedPumpkin extends BlockItem implements Equipable {
        public ItemAssimilatedPumpkin(Block block, Item.Properties properties) {
            super(block, properties);
        }

        @Override
        public EquipmentSlot getEquipmentSlot() {
            return EquipmentSlot.HEAD;
        }

        @Override
        public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
            ItemStack held = player.getItemInHand(hand);
            ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
            if (head.isEmpty()) {
                ItemStack one = held.copy();
                one.setCount(1);
                player.setItemSlot(EquipmentSlot.HEAD, one);
                held.shrink(1);
                return InteractionResultHolder.success(held);
            }
            return super.use(level, player, hand);
        }
    }
}
