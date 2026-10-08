package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPDamageTypes;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Hungry mouth block: whatever stands on it takes 1 damage every 10 ticks plus corrosion and viral infection. */
public class BlockParasiteMouth extends BlockBase {
    private static final float DAMAGE_PER_HIT = 1.0f;
    private static final int APPLY_COOLDOWN_TICKS = 10;
    private static final String NBT_KEY_NEXT_APPLY = "srp_mouth_next_apply";

    public BlockParasiteMouth(SRPMaterial material, float hardness, boolean tickRandom) {
        super(prop(material.props(hardness).sound(SRPSoundTypes.FLESH).noOcclusion(), tickRandom));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide && this.isOnTop(pos, entity)) {
            this.tryAffect(level, pos, entity);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (!level.isClientSide && this.isOnTop(pos, entity)) {
            this.tryAffect(level, pos, entity);
        }
        super.stepOn(level, pos, state, entity);
    }

    private boolean isOnTop(BlockPos pos, Entity e) {
        return e.getBoundingBox().minY >= pos.getY() + 0.999 && e.getX() >= pos.getX() && e.getX() < pos.getX() + 1
                && e.getZ() >= pos.getZ() && e.getZ() < pos.getZ() + 1;
    }

    private void tryAffect(Level level, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity mob)) {
            return;
        }
        if (entity instanceof EntityParasiteBase) {
            return;
        }
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return;
        }
        long now = level.getGameTime();
        long nextAllowed = mob.getPersistentData().getLong(NBT_KEY_NEXT_APPLY);
        if (now < nextAllowed) {
            return;
        }
        mob.getPersistentData().putLong(NBT_KEY_NEXT_APPLY, now + APPLY_COOLDOWN_TICKS);
        mob.hurt(new DamageSource(level.registryAccess().holderOrThrow(SRPDamageTypes.PARASITE_MOUTH)), DAMAGE_PER_HIT);
        mob.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, 100, 0, false, false));
        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, mob, 200, 1);
    }
}
