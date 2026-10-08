package com.dhanantry.scapeandrunparasites.item.hijacked;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

/** Hijacked iron hoe. */
public class ItemHijackedIronHoe extends HoeItem {
    private final String name;

    public ItemHijackedIronHoe(String name, Tier mat) {
        super(mat, new Item.Properties().attributes(HoeItem.createAttributes(mat, -2.0f, -1.0f)));
        this.name = name;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        HijackedHitEffects.apply(attacker, target);
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ItemHijackedBase.describe(this.name, tooltip);
    }
}
