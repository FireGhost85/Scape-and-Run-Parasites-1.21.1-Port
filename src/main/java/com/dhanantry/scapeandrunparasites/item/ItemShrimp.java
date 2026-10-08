package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Shrimp: food (4 / 0.4, always edible); right click on an infected enderman makes it "ariral". */
public class ItemShrimp extends Item {
    public ItemShrimp(String name) {
        super(new Item.Properties().stacksTo(64).food(new FoodProperties.Builder().nutrition(4).saturationModifier(0.4f).alwaysEdible().build()));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.shrimp.desc").withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.translatable("tooltip.srparasites.shrimp.arrow").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.srparasites.shrimp.ariral").withStyle(ChatFormatting.DARK_AQUA));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide && entity instanceof Player player) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SRPSounds.SHRIMP_EAT.get(), SoundSource.PLAYERS, 0.8f, 0.95f + level.random.nextFloat() * 0.1f);
        }
        return result;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof EntityInfEnderman enderman)) {
            return InteractionResult.PASS;
        }
        if (enderman.isAriral()) {
            return InteractionResult.SUCCESS;
        }
        if (!player.level().isClientSide) {
            enderman.setAriral(true);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.level().playSound(null, enderman.getX(), enderman.getY(), enderman.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 0.6f, 0.9f + player.level().random.nextFloat() * 0.2f);
        }
        return InteractionResult.SUCCESS;
    }
}
