package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Iterator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

/** Beholder pearl drops: infected / feral / assimara endermen drop pearls; the pearls of a player killed by one are destroyed. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class PearlEvents {
    private static final ResourceLocation SIM = ResourceLocation.fromNamespaceAndPath("srparasites", "sim_enderman");
    private static final ResourceLocation FERAL = ResourceLocation.fromNamespaceAndPath("srparasites", "fer_enderman");
    private static final ResourceLocation ASSIMARA = ResourceLocation.fromNamespaceAndPath("srparasites", "mar_enderman");
    private static final ResourceLocation SIM_HEAD = ResourceLocation.fromNamespaceAndPath("srparasites", "sim_endermanhead");

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent e) {
        if (e.getEntity().level().isClientSide) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getEntity().getType());
        float chance = 0.0f;
        if (id.equals(SIM)) {
            chance = 0.1f;
        } else if (id.equals(FERAL)) {
            chance = 0.3f;
        } else if (id.equals(ASSIMARA)) {
            chance = 0.4f;
        }
        if (chance <= 0.0f) {
            return;
        }
        if (e.getEntity().level().random.nextFloat() < chance) {
            ItemStack drop = new ItemStack(SRPItems.pearl.get());
            e.getDrops().add(new ItemEntity(e.getEntity().level(), e.getEntity().getX(), e.getEntity().getY(), e.getEntity().getZ(), drop));
        }
    }

    @SubscribeEvent
    public static void onPlayerDrops(LivingDropsEvent e) {
        if (!(e.getEntity() instanceof Player player) || !SRPConfigMobs.pearlDestroyedOnBeholderKill) {
            return;
        }
        if (player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            return;
        }
        DamageSource src = e.getSource();
        Entity killer = src.getEntity();
        if (!(killer instanceof LivingEntity)) {
            return;
        }
        ResourceLocation killerId = BuiltInRegistries.ENTITY_TYPE.getKey(killer.getType());
        boolean isBeholder = killerId.equals(SIM) || killerId.equals(SIM_HEAD) || killerId.equals(FERAL) || killerId.equals(ASSIMARA);
        if (!isBeholder) {
            return;
        }
        Iterator<ItemEntity> it = e.getDrops().iterator();
        while (it.hasNext()) {
            ItemStack s = it.next().getItem();
            if (s.getItem() == SRPItems.pearl.get()) {
                it.remove();
            }
        }
    }
}
