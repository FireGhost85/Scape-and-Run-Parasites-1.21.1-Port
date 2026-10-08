package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/** False apple: eat it for 4 food (0.3), nausea and hunger, and five buglins crawl out. */
public class ItemFalseApple extends ItemBase {
    private static final int HUNGER = 4;
    private static final float SATURATION = 0.3f;
    private static final int BUGLIN_COUNT = 5;

    public ItemFalseApple(String name) {
        super(name, 64, 0);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.false_apple"));
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
        if (eater instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getFoodData().eat(HUNGER, SATURATION);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.8f, 1.0f);
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 0));
            if (!world.isClientSide) {
                for (int i = 0; i < BUGLIN_COUNT; ++i) {
                    Entity e = SRPEntities.BUGLIN.get().create(world);
                    if (e == null) continue;
                    double offX = (world.random.nextDouble() - 0.5) * 0.8;
                    double offZ = (world.random.nextDouble() - 0.5) * 0.8;
                    e.moveTo(player.getX() + offX, player.getY(), player.getZ() + offZ, world.random.nextFloat() * 360.0f, 0.0f);
                    world.addFreshEntity(e);
                }
            }
        }
        return stack;
    }
}
