package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.AABB;

/**
 * Scythe: every hit also strikes all other living things within 4 blocks of the target (sentient: and within 8 blocks of the
 * player). 1.12 used a copy of the player attack code for these extra hits; here the extra hits are real player attacks
 * ({@code Player.attack}), guarded against recursion. Mobs killed by an extra hit add the max health of the original target to
 * the kill counter (as in 1.10.9).
 */
public class WeaponMeleeScythe extends WeaponToolMeleeBase {
    private static boolean inAreaHit;

    public WeaponMeleeScythe(Tier material, String name, double attackspeed, float range, float attackD, boolean fear, int id) {
        super(material, name, attackspeed, range, attackD, fear, id);
    }

    @Override
    public Item getNext() {
        return this == SRPItems.weapon_scythe.get() ? SRPItems.weapon_scytheSentient.get() : null;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        if (flag && !inAreaHit && attacker instanceof Player player) {
            inAreaHit = true;
            try {
                int aoe = 4;
                if (this.calling) {
                    aoe = 8;
                    this.sweep(stack, target, player, new AABB(player.getX(), player.getY(), player.getZ(), player.getX() + 1.0, player.getY() + 1.0, player.getZ() + 1.0).inflate(aoe));
                }
                this.sweep(stack, target, player, new AABB(target.getX(), target.getY(), target.getZ(), target.getX() + 1.0, target.getY() + 1.0, target.getZ() + 1.0).inflate(aoe));
            } finally {
                inAreaHit = false;
            }
        }
        return flag;
    }

    private void sweep(ItemStack stack, LivingEntity target, Player player, AABB area) {
        for (LivingEntity mob : target.level().getEntitiesOfClass(LivingEntity.class, area)) {
            if (mob == target || mob == player) continue;
            player.attack(mob);
            if (mob.getHealth() <= 0.0f) {
                addKills(stack, target.getMaxHealth());
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        this.addWeaponLines(tooltip);
    }
}
