package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

public class EntityAIGiveEffectsArea
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    EntityParasiteBase parent;
    private int attackTimer = 0;
    private int bRange;
    private int bCooldown;
    private String[] effects;

    public EntityAIGiveEffectsArea(EntityParasiteBase parent, int cooldown, int range, String[] in) {
        this.parent = parent;
        this.bRange = range;
        this.bCooldown = cooldown;
        this.effects = in;
    }

    public boolean canUse() {
        return true;
    }

    public void tick() {
        ++this.attackTimer;
        if (this.attackTimer >= 60) {
            this.BuffParasites();
            this.attackTimer -= this.bCooldown;
        }
    }

    protected void BuffParasites() {
        String[] here = new String[3];
        AABB axisalignedbb = new AABB(this.parent.blockPosition()).inflate((double)this.bRange);
        List<? extends EntityParasiteBase> moblist = this.parent.level().getEntitiesOfClass(EntityParasiteBase.class, axisalignedbb);
        for (String i : this.effects) {
            here = i.split(";");
            Holder<MobEffect> potionE = SRPEntityUtil.effect(here[2]);
            if (potionE == null) continue;
            int duration = Integer.parseInt(here[0]) * 20;
            int amp = Integer.parseInt(here[1]);
            for (EntityParasiteBase mob : moblist) {
                if (mob == this.parent) continue;
                mob.addEffect(new MobEffectInstance(potionE, duration, amp, false, false));
            }
        }
    }
}

