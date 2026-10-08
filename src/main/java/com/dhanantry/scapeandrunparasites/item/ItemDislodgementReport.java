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

/** Printed dislodgement report: right click opens the report screen. */
public class ItemDislodgementReport extends ItemBase {
    public static final String NBT_PRINT_DAY = "PrintDay";
    public static final String NBT_PRINT_TIME = "PrintTime";
    public static final String NBT_CODE = "DislodgementCode";

    public ItemDislodgementReport(String name, int maxStack, int id) {
        super(new Item.Properties().stacksTo(1), id);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ClientHooks.openDislodgementReport(stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.dislodgement_report").withStyle(ChatFormatting.GRAY));
        CompoundTag tag = ReportData.read(stack);
        if (tag.contains(NBT_PRINT_DAY)) {
            tooltip.add(Component.translatable("tooltip.srparasites.printed", tag.getInt(NBT_PRINT_DAY), tag.getInt(NBT_PRINT_TIME)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static int safeParseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        } catch (Throwable t) {
            return fallback;
        }
    }

    private static String getDislodgementEventKey(int event) {
        switch (event) {
            case 0, 1, 2, 3, 4, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 25:
                return "srparasites.dislodgement.event." + event;
            default:
                return "srparasites.dislodgement.event.unknown";
        }
    }

    /** The event text of the report screen: events without a value take no argument. */
    public static Component formatDislodgementEventMeaningClient(int event, String valueStr) {
        String key = getDislodgementEventKey(event);
        switch (event) {
            case 0, 11, 15, 16, 17, 18, 20:
                return Component.translatable(key);
            default:
                return Component.translatable(key, valueStr);
        }
    }
}
