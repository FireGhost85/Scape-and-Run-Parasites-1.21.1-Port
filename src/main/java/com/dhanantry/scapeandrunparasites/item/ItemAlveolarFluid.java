package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.client.ClientHooks;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Bottle of alveolar fluid: drink for haste, speed and VIRA III (600 ticks); returns an empty bottle. */
public class ItemAlveolarFluid extends Item {
    private static final int DURATION = 600;

    public ItemAlveolarFluid(String name) {
        super(new Item.Properties().stacksTo(1));
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
    public ItemStack finishUsingItem(ItemStack stack, Level w, LivingEntity entity) {
        if (!w.isClientSide) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, DURATION, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURATION, 0));
            entity.addEffect(new MobEffectInstance(SRPPotions.VIRA_E, DURATION, 2));
        }
        if (w.isClientSide && entity instanceof Player) {
            ClientHooks.enableBreathe(DURATION);
        }
        Player player = entity instanceof Player p ? p : null;
        if (player == null || !player.getAbilities().instabuild) {
            stack.shrink(1);
            if (player != null) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (stack.isEmpty()) {
                    return bottle;
                }
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
            }
        }
        return stack;
    }
}
