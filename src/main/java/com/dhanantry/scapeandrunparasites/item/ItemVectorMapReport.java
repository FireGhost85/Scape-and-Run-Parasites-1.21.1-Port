package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.client.ClientHooks;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Printed vector map: right click opens the vector map screen. */
public class ItemVectorMapReport extends Item {
    public static final String NBT_CX = "CenterX";
    public static final String NBT_CZ = "CenterZ";
    public static final String NBT_VX = "VectorX";
    public static final String NBT_VZ = "VectorZ";
    public static final String NBT_R = "Radius";
    public static final String NBT_DAY = "Day";
    public static final String NBT_INDEX = "Index";
    public static final String NBT_TOTAL = "Total";
    public static final String NBT_PRINT_DAY = "PrintDay";
    public static final String NBT_PRINT_TIME = "PrintTime";

    public ItemVectorMapReport(String name) {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ClientHooks.openVectorMap(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.vector_map").withStyle(ChatFormatting.GRAY));
        CompoundTag tag = ReportData.read(stack);
        if (tag.contains(NBT_PRINT_DAY)) {
            tooltip.add(Component.translatable("tooltip.srparasites.printed", tag.getInt(NBT_PRINT_DAY), tag.getInt(NBT_PRINT_TIME)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
