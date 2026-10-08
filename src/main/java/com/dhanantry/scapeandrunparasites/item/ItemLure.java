package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Lure components (lurecomponent1 to 10). Right click (server) also bumps the update counter, as in 1.10.9. */
public class ItemLure extends ItemBase {
    int sound = 0;

    public ItemLure(String name, int maxStack, int version) {
        super(name, maxStack, version);
        this.version = (byte) version;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && level.getServer() != null) {
            SRPSaveData dat = SRPSaveData.get(level);
            if (dat != null) {
                dat.addUpdateNumber(1, level.getServer());
            }
        }
        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tootip.srparasites.lurecomp." + this.version).withStyle(ChatFormatting.AQUA));
    }
}
