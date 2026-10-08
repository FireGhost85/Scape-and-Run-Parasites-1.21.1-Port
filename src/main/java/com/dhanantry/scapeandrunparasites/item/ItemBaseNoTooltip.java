package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Same as {@link ItemBase} but the numbered tooltip line is always shown. */
public class ItemBaseNoTooltip extends Item {
    protected byte version;

    public ItemBaseNoTooltip(String name, int maxStack, int id) {
        super(new Item.Properties().stacksTo(maxStack));
        this.version = (byte) id;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tootip.srparasites.item." + this.version).withStyle(ChatFormatting.WHITE));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if ((Object) this == SRPItems.itemAssimilate.get()) {
            ParasiteEventEntity.convertEntity(target, target.getPersistentData(), true, SRPConfigSystems.COTHVictimParasite);
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
