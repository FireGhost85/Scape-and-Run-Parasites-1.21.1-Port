package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

/**
 * Living / sentient bow: arrows get a bleed + dod-smoke tip, damage scales with the time the bow was drawn
 * (seconds drawn times {@code bonus}, capped at {@code damage * damageCap}) plus the flat {@code damage}.
 */
public class WeaponToolRangeBase extends BowItem {
    private final int bonus;
    private final int damage;
    private final int damageCap;
    private final boolean calling;
    private final byte idTool;

    public WeaponToolRangeBase(String name, int durability, int bonus, int damageC, int damage, boolean fear, int id) {
        super(new Item.Properties().stacksTo(1).durability(durability));
        this.bonus = bonus;
        this.damageCap = damageC;
        this.damage = damage;
        this.calling = fear;
        this.idTool = (byte) id;
    }

    protected ItemStack findAmmo(Player player) {
        if (this.isArrow(player.getItemInHand(InteractionHand.OFF_HAND))) {
            return player.getItemInHand(InteractionHand.OFF_HAND);
        }
        if (this.isArrow(player.getItemInHand(InteractionHand.MAIN_HAND))) {
            return player.getItemInHand(InteractionHand.MAIN_HAND);
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            ItemStack itemstack = player.getInventory().getItem(i);
            if (!this.isArrow(itemstack)) continue;
            return itemstack;
        }
        return ItemStack.EMPTY;
    }

    protected boolean isArrow(ItemStack stack) {
        return stack.getItem() instanceof ArrowItem;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (!(entityLiving instanceof Player player)) {
            return;
        }
        boolean infinite = player.getAbilities().instabuild
                || EnchantmentHelper.getItemEnchantmentLevel(level.registryAccess().holderOrThrow(Enchantments.INFINITY), stack) > 0;
        ItemStack ammo = this.findAmmo(player);
        int i = this.getUseDuration(stack, entityLiving) - timeLeft;
        i = EventHooks.onArrowLoose(stack, level, player, i, !ammo.isEmpty() || infinite);
        if (i < 0) {
            return;
        }
        double seconds = (72000 - timeLeft) / 20;
        double bonusD = seconds * (double) this.bonus;
        if (ammo.isEmpty() && !infinite) {
            return;
        }
        if (ammo.isEmpty()) {
            ammo = new ItemStack(Items.ARROW);
        }
        float f = getPowerForTime(i);
        if ((double) f < 0.1) {
            return;
        }
        boolean flag1 = player.getAbilities().instabuild || ammo.getItem() instanceof ArrowItem arrowItem && arrowItem.isInfinite(ammo, stack, player);
        if (!level.isClientSide) {
            ArrowItem arrowItem = ammo.getItem() instanceof ArrowItem a ? a : (ArrowItem) Items.ARROW;
            AbstractArrow arrow = arrowItem.createArrow(level, ammo, player, stack);
            if (arrow instanceof Arrow tipped) {
                tipped.addEffect(new MobEffectInstance(SRPPotions.BLEED_E, 200, 0, false, true));
                tipped.addEffect(new MobEffectInstance(SRPPotions.DOD_SMOKE_TRAIL_E, 200, 0, false, true));
            }
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, f * 4.4f, 0.0f);
            if (f == 1.0f) {
                arrow.setCritArrow(true);
            }
            // power / punch are applied by the arrow itself (it carries the bow); flame and the other spawn effects here
            if (level instanceof ServerLevel serverLevel) {
                EnchantmentHelper.onProjectileSpawned(serverLevel, stack, arrow, item -> { });
            }
            stack.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
            if (flag1 || player.getAbilities().instabuild && (ammo.getItem() == Items.SPECTRAL_ARROW || ammo.getItem() == Items.TIPPED_ARROW)) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            if (bonusD > (double) (this.damage * this.damageCap)) {
                bonusD = this.damage * this.damageCap;
            }
            arrow.setBaseDamage(arrow.getBaseDamage() * bonusD);
            arrow.setBaseDamage(arrow.getBaseDamage() + (double) this.damage);
            level.addFreshEntity(arrow);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f / (level.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);
        if (!flag1 && !player.getAbilities().instabuild) {
            ammo.shrink(1);
            if (ammo.isEmpty()) {
                player.getInventory().removeItem(ammo);
            }
        }
        player.awardStat(Stats.ITEM_USED.get(this));
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (!level.isClientSide && this.calling && SRPConfigSystems.useScent && level.random.nextInt(10) == 0 && entity.tickCount % 40 == 0
                && SRPSaveData.get(level).getDeveLevel() >= SRPConfigSystems.deveScentUse && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(SRPPotions.PREY_E, 1200, 0, false, false));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tootip.srparasites.weaponr." + this.idTool).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tootip.srparasites.weaponr." + this.idTool * 10).withStyle(ChatFormatting.RED));
        if (this.calling && SRPConfigSystems.useScent) {
            tooltip.add(Component.translatable("tootip.srparasites.weaponr." + this.idTool * 100).withStyle(ChatFormatting.BLACK));
        }
    }
}
