package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockSickAlveoli extends BlockBase {
    public BlockSickAlveoli() {
        super(prop(SRPMaterial.CLAY.props(1.0f).sound(SRPSoundTypes.FLESH).noOcclusion(), true));
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 0));
        }
    }
}
