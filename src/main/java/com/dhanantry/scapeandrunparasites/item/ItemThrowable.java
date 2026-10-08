package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.entity.projectile.EntityThrowableAntiInfestedBlock;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Quench flask: throws an anti-infested-block projectile (cooldown 20 ticks). */
public class ItemThrowable extends Item {
    public ItemThrowable(String name, int maxStack) {
        super(new Item.Properties().stacksTo(maxStack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (!player.getAbilities().instabuild) {
            itemstack.shrink(1);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EGG_THROW, SoundSource.PLAYERS, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!level.isClientSide) {
            EntityThrowableAntiInfestedBlock entityegg = new EntityThrowableAntiInfestedBlock(SRPEntities.ANTIINFESTEDBLOCK.get(), level, player);
            entityegg.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, 0.5f, 5.0f);
            player.getCooldowns().addCooldown(this, 20);
            level.addFreshEntity(entityegg);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tootip.srparasites.quench").withStyle(ChatFormatting.AQUA));
    }
}
