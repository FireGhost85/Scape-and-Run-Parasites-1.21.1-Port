package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.ClientHooks;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPArmorMaterials;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Armor pieces that belong to a set ("hijacked_iron"): the full hijacked set removes Bleed and weakens fire damage taken
 * (config multiplier), the boots absorb one long fall every 2400 ticks (blood impact, slowness and weakness for 10 s).
 * The cooldowns live in the player's persistent data ("SRPArmorCD").
 */
public class SRPArmorSetBase extends ArmorItem {
    public static final int BOOTS_SAVE_COOLDOWN_TICKS = 2400;
    public static final int DEBUFF_TICKS = 200;
    public static final int BOOTS_MIN_FALL_DIST = 6;
    private static final String NBT_ROOT = "SRPArmorCD";
    private static final String NBT_BOOT_CD = "bootsCD";
    private static final String NBT_SET_CD = "setCD";
    private static final String NBT_BOOT_READY = "bootsReadyPlayed";
    private static final String NBT_SET_READY = "setReadyPlayed";
    private final String setKey;

    public SRPArmorSetBase(String registryName, Holder<ArmorMaterial> material, ArmorItem.Type type, String setKey) {
        super(material, type, new Item.Properties().durability(type.getDurability(40)));
        this.setKey = setKey;
    }

    public SRPArmorSetBase(String registryName) {
        this(registryName, inferMaterial(registryName), inferType(registryName), inferSetKey(registryName));
    }

    public String getSetKey() {
        return this.setKey;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        // 1.12 onArmorTick: only while worn
        if (level.isClientSide || !(entity instanceof Player player) || player.getItemBySlot(this.getEquipmentSlot()) != stack) {
            return;
        }
        if (isWearingFullSet(player, this.setKey) && player.hasEffect(SRPPotions.BLEED_E)) {
            player.removeEffect(SRPPotions.BLEED_E);
        }
    }

    public static boolean isWearingFullSet(Player player, String setKey) {
        if (player == null || setKey == null || setKey.isEmpty()) {
            return false;
        }
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack st = player.getItemBySlot(slot);
            if (st.isEmpty() || !(st.getItem() instanceof SRPArmorSetBase set)) {
                return false;
            }
            if (!set.setKey.equals(setKey)) {
                return false;
            }
        }
        return true;
    }

    public static String getAnySetKey(Player p) {
        if (p == null) {
            return "";
        }
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack st = p.getItemBySlot(slot);
            if (!st.isEmpty() && st.getItem() instanceof SRPArmorSetBase set) {
                return set.getSetKey();
            }
        }
        return "";
    }

    private static Holder<ArmorMaterial> inferMaterial(String regName) {
        // every set of the mod is the hijacked one (the 1.12 fallbacks to vanilla materials were never used)
        return SRPArmorMaterials.HIJACKED;
    }

    private static ArmorItem.Type inferType(String regName) {
        String s = regName.toLowerCase(Locale.ROOT);
        if (s.endsWith("_helmet") || s.endsWith("_helm")) {
            return ArmorItem.Type.HELMET;
        }
        if (s.endsWith("_chestpiece") || s.endsWith("_chestplate") || s.endsWith("_chest")) {
            return ArmorItem.Type.CHESTPLATE;
        }
        if (s.endsWith("_leggings") || s.endsWith("_legs")) {
            return ArmorItem.Type.LEGGINGS;
        }
        if (s.endsWith("_boots")) {
            return ArmorItem.Type.BOOTS;
        }
        throw new IllegalArgumentException("Cannot infer armor slot from registry name: " + regName);
    }

    private static String inferSetKey(String regName) {
        int i = regName.lastIndexOf('_');
        return i > 0 ? regName.substring(0, i) : regName;
    }

    private static CompoundTag getCDTag(Player p) {
        CompoundTag root = p.getPersistentData();
        if (!root.contains(NBT_ROOT)) {
            root.put(NBT_ROOT, new CompoundTag());
        }
        return root.getCompound(NBT_ROOT);
    }

    private static int getBootsCD(Player p) {
        return getCDTag(p).getInt(NBT_BOOT_CD);
    }

    private static void setBootsCD(Player p, int v) {
        CompoundTag t = getCDTag(p);
        t.putInt(NBT_BOOT_CD, v);
        t.putBoolean(NBT_BOOT_READY, false);
    }

    private static void bloodImpact(Level w, Entity e) {
        if (!(w instanceof ServerLevel ws)) {
            return;
        }
        double x = e.getX();
        double y = e.getY() + (double) e.getBbHeight() * 0.5;
        double z = e.getZ();
        ws.sendParticles(new DustParticleOptions(new org.joml.Vector3f(0.9f, 0.05f, 0.05f), 1.3f), x, y, z, 1, 0.0, 0.0, 0.0, 0.0);
        ws.sendParticles(ParticleTypes.DAMAGE_INDICATOR, x, y, z, 24, 0.45, 0.35, 0.45, 0.2);
    }

    private static void applyCrashDebuffs(LivingEntity ent) {
        ent.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, DEBUFF_TICKS, 1, false, true));
        ent.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, DEBUFF_TICKS, 1, false, true));
    }

    private static boolean isHijackedSetKey(String key) {
        return key != null && !key.isEmpty() && key.toLowerCase(Locale.ROOT).startsWith("hijacked_");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (isHijackedSetKey(this.getSetKey())) {
            tooltip.add(Component.translatable("tooltip.srparasites.hijacked.fullset_bleed").withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.translatable("tooltip.srparasites.hijacked.fire_penalty", String.format(Locale.ROOT, "%.2f", SRPConfigSystems.hijackedArmorFireMult)).withStyle(ChatFormatting.RED));
            tooltip.add(Component.translatable("tooltip.srparasites.hijacked.boots_save").withStyle(ChatFormatting.BLUE));
        }
        Player client = ClientHooks.localPlayer();
        if (client == null) {
            return;
        }
        ItemStack feet = client.getItemBySlot(EquipmentSlot.FEET);
        if (!feet.isEmpty() && feet.getItem() instanceof SRPArmorSetBase bootsItem && isHijackedSetKey(bootsItem.getSetKey()) && bootsItem.getSetKey().equals(this.getSetKey())) {
            int bootCd = getBootsCD(client);
            if (bootCd > 0) {
                tooltip.add(Component.translatable("tooltip.srparasites.hijacked.boots_cd", formatTicks(bootCd)).withStyle(ChatFormatting.BLUE));
            } else {
                tooltip.add(Component.translatable("tooltip.srparasites.ready").withStyle(ChatFormatting.BLUE));
            }
        }
    }

    private static String formatTicks(int ticks) {
        if (ticks <= 0) {
            return "0:00";
        }
        int s = (ticks + 19) / 20;
        return String.format(Locale.ROOT, "%d:%02d", s / 60, s % 60);
    }

    @EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
    public static class Events {
        @SubscribeEvent
        public static void onLivingHurt(LivingIncomingDamageEvent e) {
            if (!(e.getEntity() instanceof Player p)) {
                return;
            }
            String key = getAnySetKey(p);
            if (!isHijackedSetKey(key) || !isWearingFullSet(p, key)) {
                return;
            }
            DamageSource src = e.getSource();
            if (src != null && (src.is(DamageTypeTags.IS_FIRE) || src.is(net.minecraft.world.damagesource.DamageTypes.LAVA) || src.is(net.minecraft.world.damagesource.DamageTypes.HOT_FLOOR))) {
                float mult = (float) Math.max(0.0, SRPConfigSystems.hijackedArmorFireMult);
                if (mult != 1.0f) {
                    e.setAmount(e.getAmount() * mult);
                }
            }
        }

        @SubscribeEvent
        public static void aiStep(PlayerTickEvent.Post e) {
            Player p = e.getEntity();
            CompoundTag cd = getCDTag(p);
            int boot = cd.getInt(NBT_BOOT_CD);
            int set = cd.getInt(NBT_SET_CD);
            if (boot > 0) {
                cd.putInt(NBT_BOOT_CD, boot - 1);
            }
            if (set > 0) {
                cd.putInt(NBT_SET_CD, set - 1);
            }
            boolean hasHijackedBoots = false;
            ItemStack feet = p.getItemBySlot(EquipmentSlot.FEET);
            if (!feet.isEmpty() && feet.getItem() instanceof SRPArmorSetBase bootsItem) {
                hasHijackedBoots = isHijackedSetKey(bootsItem.getSetKey());
            }
            String key = getAnySetKey(p);
            boolean hasFullHijackedSet = isHijackedSetKey(key) && isWearingFullSet(p, key);
            if (cd.contains(NBT_BOOT_CD) && cd.getInt(NBT_BOOT_CD) == 0 && !cd.getBoolean(NBT_BOOT_READY) && hasHijackedBoots) {
                cd.putBoolean(NBT_BOOT_READY, true);
                p.level().playSound(null, p.blockPosition(), SoundEvents.STONE_BUTTON_CLICK_ON, SoundSource.PLAYERS, 0.5f, 1.5f);
            }
            if (cd.contains(NBT_SET_CD) && cd.getInt(NBT_SET_CD) == 0 && !cd.getBoolean(NBT_SET_READY) && hasFullHijackedSet) {
                cd.putBoolean(NBT_SET_READY, true);
                p.level().playSound(null, p.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.7f, 1.0f);
            }
        }

        @SubscribeEvent
        public static void onLivingFall(LivingFallEvent e) {
            if (!(e.getEntity() instanceof Player p)) {
                return;
            }
            ItemStack boots = p.getItemBySlot(EquipmentSlot.FEET);
            if (boots.isEmpty() || !(boots.getItem() instanceof SRPArmorSetBase bootsItem) || !isHijackedSetKey(bootsItem.getSetKey())) {
                return;
            }
            if (e.getDistance() < BOOTS_MIN_FALL_DIST || getBootsCD(p) > 0) {
                return;
            }
            e.setCanceled(true);
            p.fallDistance = 0.0f;
            bloodImpact(p.level(), p);
            applyCrashDebuffs(p);
            setBootsCD(p, BOOTS_SAVE_COOLDOWN_TICKS);
        }
    }
}
