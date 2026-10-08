package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;

/** Living axe: 25% (sentient 50%, amplifier 1) to corrode the target for 100 ticks. */
public class WeaponMeleeAxe extends WeaponToolMeleeBase {
    public WeaponMeleeAxe(Tier material, String name, double attackspeed, float range, float attackD, boolean fear, int id) {
        super(material, name, attackspeed, range, attackD, fear, id);
    }

    @Override
    public Item getNext() {
        return this == SRPItems.weapon_axe.get() ? SRPItems.weapon_axeSentient.get() : null;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        if (flag) {
            double chance = 0.25;
            int amp = 0;
            if (this.calling) {
                chance = 0.5;
                amp = 1;
            }
            if (attacker.level().random.nextDouble() < chance) {
                SRPPotions.applyStackPotion(SRPPotions.CORRO_E, target, 100, amp);
            }
        }
        return flag;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        this.addWeaponLines(tooltip);
    }
}
