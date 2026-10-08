package com.dhanantry.scapeandrunparasites.item.hijacked;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/** On-hit effects of the hijacked iron tools: bleed, +3 damage against parasites, rage when the target is nearly dead. */
public final class HijackedHitEffects {
    public static final int BLEED_TICKS = 100;
    public static final int BLEED_AMP = 0;
    public static final int RAGE_TICKS = 60;
    public static final int RAGE_AMP = 0;
    public static final float PARASITE_BONUS_DAMAGE = 3.0f;

    private HijackedHitEffects() {}

    public static void apply(LivingEntity attacker, LivingEntity target) {
        if (target == null || target.isRemoved()) {
            return;
        }
        target.addEffect(new MobEffectInstance(SRPPotions.BLEED_E, BLEED_TICKS, BLEED_AMP, false, true));
        if (target instanceof EntityParasiteBase) {
            DamageSource src = attacker instanceof Player player ? attacker.damageSources().playerAttack(player) : attacker.damageSources().mobAttack(attacker);
            target.hurt(src, PARASITE_BONUS_DAMAGE);
        }
        float max = target.getMaxHealth();
        if (max > 0.0f && target.getHealth() <= max * 0.1f) {
            target.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, RAGE_TICKS, RAGE_AMP, false, true));
        }
    }
}
