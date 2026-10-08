package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** Fishlin: eatable (3 / 0.2) but the eater takes 8 magic damage and receives COTH for 4800 ticks (amplifier 1). */
public class ItemFishlin extends ItemBase {
    private static final int HUNGER = 3;
    private static final float SATURATION = 0.2f;

    public ItemFishlin(String name) {
        super(name, 64, 0);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity eater) {
        if (eater instanceof Player p) {
            if (!p.getAbilities().instabuild) {
                stack.shrink(1);
            }
            p.getFoodData().eat(HUNGER, SATURATION);
            world.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.8f, 1.0f);
            p.hurt(p.damageSources().magic(), 8.0f);
            p.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 4800, 1, false, false));
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.fishlin"));
    }
}
