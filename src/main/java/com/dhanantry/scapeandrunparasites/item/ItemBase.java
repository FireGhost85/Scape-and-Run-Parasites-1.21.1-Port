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

/** Plain SRP item with an optional numbered tooltip line ({@code tootip.srparasites.item.N}, the typo is the original key). */
public class ItemBase extends Item {
    protected byte version;

    public ItemBase(String name, int maxStack, int id) {
        this(new Item.Properties().stacksTo(maxStack), id);
    }

    protected ItemBase(Item.Properties properties, int id) {
        super(properties);
        this.version = (byte) id;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (this.version != -1) {
            tooltip.add(Component.translatable("tootip.srparasites.item." + this.version).withStyle(ChatFormatting.WHITE));
        }
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (this == SRPItems.itemAssimilate.get()) {
            ParasiteEventEntity.convertEntity(target, target.getPersistentData(), true, SRPConfigSystems.COTHVictimParasite);
        }
        return super.hurtEnemy(stack, target, attacker);
    }
}
