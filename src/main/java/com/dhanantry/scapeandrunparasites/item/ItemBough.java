package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.client.ClientQlipShake;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPDamageTypes;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Bough: holding right click makes the player invulnerable and frozen for one second while a sepeku-style ritual plays; when it
 * finishes the player dies (sepeku damage, ignoring everything) and a replacement mob is spawned.
 */
public class ItemBough extends ItemBase {
    private static final int USE_TICKS = 20;
    private static final int COOLDOWN_TICKS = 60;
    private static final float VOL_START = 3.5f;
    private static final float VOL_HURT = 3.0f;
    private static final float VOL_MID = 3.5f;
    private static final float VOL_DEATH = 3.5f;
    private static final String NBT_ACTIVE = "srp_bough_active";
    private static final String NBT_PREV_NODMG = "srp_bough_prev_nodmg";
    private static final String NBT_ADAPT_PLAYED = "srp_bough_adapt_played";

    public ItemBough(String name, int maxStack, int id) {
        super(new Item.Properties().stacksTo(maxStack), id);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_TICKS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        player.getPersistentData().putBoolean(NBT_ADAPT_PLAYED, false);
        player.playSound(SRPSounds.ADAPTED_V.get(), VOL_START, 1.0f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, VOL_HURT, 1.0f);
        if (level.isClientSide && player.isLocalPlayer()) {
            triggerShakeClient();
        }
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            setHoldInvuln(sp, true);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity living, ItemStack stack, int count) {
        if (!(living instanceof Player player)) {
            return;
        }
        if (level.isClientSide) {
            if (player.isLocalPlayer()) {
                if ((count & 1) == 0) {
                    triggerShakeClient();
                }
                Mot.setX(player, 0.0);
                Mot.setZ(player, 0.0);
            }
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 6, 255, false, false));
        Mot.setX(player, 0.0);
        Mot.setY(player, 0.0);
        Mot.setZ(player, 0.0);
        player.hurtMarked = true;
        player.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 6, 0, false, false));
        if (count == 1 && !player.getPersistentData().getBoolean(NBT_ADAPT_PLAYED)) {
            player.playSound(SRPSounds.ADAPTATION_P.get(), VOL_MID, 1.0f);
            player.getPersistentData().putBoolean(NBT_ADAPT_PLAYED, true);
        }
        if (level instanceof ServerLevel serverLevel && (count & 1) == 0) {
            spawnBloodSpray(serverLevel, player);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity living, int timeLeft) {
        if (!level.isClientSide && living instanceof ServerPlayer p) {
            setHoldInvuln(p, false);
            p.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            p.removeEffect(SRPPotions.RAGE_E);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity living) {
        if (level.isClientSide || !(living instanceof ServerPlayer p)) {
            return stack;
        }
        p.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
        p.removeEffect(SRPPotions.RAGE_E);
        p.playSound(SRPSounds.ALAFHA_SHOOTING.get(), VOL_DEATH, 1.0f);
        p.playSound(SRPSounds.ALAFHA_HURT.get(), VOL_DEATH, 1.0f);
        spawnReplacement(level, p);
        if (level instanceof ServerLevel serverLevel) {
            spawnBloodBurst(serverLevel, p);
        }
        grantSepekuAdvancement(p);
        setHoldInvuln(p, false);
        if (!p.getAbilities().instabuild) {
            stack.shrink(1);
        }
        killPlayerSepeku(p);
        return stack;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide || !(entity instanceof ServerPlayer p)) {
            return;
        }
        if (!p.getPersistentData().getBoolean(NBT_ACTIVE)) {
            return;
        }
        boolean stillUsing = p.isUsingItem() && !p.getUseItem().isEmpty() && p.getUseItem().getItem() == this;
        if (!stillUsing) {
            setHoldInvuln(p, false);
            p.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
            p.removeEffect(SRPPotions.RAGE_E);
        }
    }

    private static void triggerShakeClient() {
        ClientQlipShake.INSTANCE.triggerDelayed(20, 0, true, true, 4.0f);
    }

    private static void setHoldInvuln(ServerPlayer p, boolean enable) {
        if (enable) {
            p.getPersistentData().putBoolean(NBT_ACTIVE, true);
            p.getPersistentData().putBoolean(NBT_PREV_NODMG, p.getAbilities().invulnerable);
            p.getAbilities().invulnerable = true;
            p.onUpdateAbilities();
        } else {
            if (!p.getPersistentData().getBoolean(NBT_ACTIVE)) {
                return;
            }
            p.getPersistentData().putBoolean(NBT_ACTIVE, false);
            p.getAbilities().invulnerable = p.getPersistentData().getBoolean(NBT_PREV_NODMG);
            p.onUpdateAbilities();
        }
    }

    private static void spawnReplacement(Level world, Player player) {
        if (world.isClientSide || !SRPConfigWorld.boughSpawnReplacement) {
            return;
        }
        String raw = SRPConfigWorld.boughReplacementMobId;
        if (raw == null) {
            return;
        }
        raw = raw.trim();
        if (raw.isEmpty() || "none".equalsIgnoreCase(raw)) {
            return;
        }
        ResourceLocation id;
        try {
            id = raw.indexOf(':') >= 0 ? ResourceLocation.parse(raw) : ResourceLocation.fromNamespaceAndPath("srparasites", raw);
        } catch (Exception e) {
            id = ResourceLocation.fromNamespaceAndPath("srparasites", "sim_adventurer");
        }
        Entity ent = SRPEntityUtil.create(id, world);
        if (ent == null) {
            ent = SRPEntityUtil.create(ResourceLocation.fromNamespaceAndPath("srparasites", "sim_adventurer"), world);
            if (ent == null) {
                return;
            }
        }
        ent.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        world.addFreshEntity(ent);
    }

    private static void grantSepekuAdvancement(ServerPlayer p) {
        MinecraftServer srv = p.getServer();
        if (srv == null) {
            return;
        }
        AdvancementHolder adv = srv.getAdvancements().get(ResourceLocation.fromNamespaceAndPath("srparasites", "sepeku"));
        if (adv == null) {
            return;
        }
        p.getAdvancements().award(adv, "done");
    }

    private static void spawnBloodSpray(ServerLevel world, Player player) {
        BlockState red = Blocks.REDSTONE_BLOCK.defaultBlockState();
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, red), player.getX(), player.getY() + (double) player.getBbHeight() * 0.55, player.getZ(), 12, 0.25, 0.2, 0.25, 0.18);
    }

    private static void spawnBloodBurst(ServerLevel world, Player player) {
        BlockState red = Blocks.REDSTONE_BLOCK.defaultBlockState();
        world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, red), player.getX(), player.getY() + (double) player.getBbHeight() * 0.6, player.getZ(), 90, 0.35, 0.35, 0.35, 0.35);
        world.sendParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY() + (double) player.getBbHeight() * 0.7, player.getZ(), 25, 0.25, 0.25, 0.25, 0.1);
    }

    private static void killPlayerSepeku(ServerPlayer p) {
        DamageSource sepeku = new DamageSource(p.level().registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE).getHolderOrThrow(SRPDamageTypes.SEPEKU));
        boolean wasCreative = p.getAbilities().instabuild;
        boolean wasNoDmg = p.getAbilities().invulnerable;
        p.getAbilities().instabuild = false;
        p.getAbilities().invulnerable = false;
        p.onUpdateAbilities();
        p.invulnerableTime = 0;
        p.hurtTime = 0;
        p.hurtDuration = 0;
        p.getCombatTracker().recheckStatus();
        boolean applied = p.hurt(sepeku, Float.MAX_VALUE);
        if (!applied || !p.isRemoved() && p.getHealth() > 0.0f) {
            try {
                p.getCombatTracker().recordDamage(sepeku, Float.MAX_VALUE);
            } catch (Throwable ignored) {
                // combat tracking is cosmetic
            }
            p.setHealth(0.0f);
            p.die(sepeku);
            p.discard();
        }
        if (!p.isRemoved() && p.getHealth() > 0.0f) {
            p.getAbilities().instabuild = wasCreative;
            p.getAbilities().invulnerable = wasNoDmg;
            p.onUpdateAbilities();
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.bough.line1").withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tooltip.srparasites.bough.line2").withStyle(ChatFormatting.GOLD));
    }
}
