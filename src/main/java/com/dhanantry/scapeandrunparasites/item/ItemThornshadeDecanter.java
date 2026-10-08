package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Thornshade decanter: drink for the thornshade thorns effect (400 ticks). */
public class ItemThornshadeDecanter extends ItemBase {
    private static final int EFFECT_DURATION_TICKS = 400;

    public ItemThornshadeDecanter() {
        super("thornshade_decanter", 16, 29);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(SRPPotions.THORNSHADE_THORNS_E, EFFECT_DURATION_TICKS, 0, false, true));
            }
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return stack;
    }
}
