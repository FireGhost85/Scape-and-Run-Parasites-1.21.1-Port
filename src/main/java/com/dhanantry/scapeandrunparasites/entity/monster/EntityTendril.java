package com.dhanantry.scapeandrunparasites.entity.monster;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityTendril
extends EntityParasiteBase {
    public EntityTendril(EntityType<? extends EntityTendril> type, Level worldIn) {
        super(type, worldIn);
        this.goalSelector.removeGoal(this.aiWander);
        this.goalSelector.removeGoal(this.folow);
        this.killcount = -10.0;
    }

    @Override
    public int getParasiteIDRegister() {
        return 202;
    }

    public EntityTendril(EntityType<? extends EntityTendril> type, Level world, float width, float height) {
        super(type, world);
    }

    public void tick() {
        super.tick();
    }

    @Override
    public void setSkin(int texture) {
        super.setSkin(texture);
    }

    @Override
    public int getSkin() {
        return super.getSkin();
    }
}

