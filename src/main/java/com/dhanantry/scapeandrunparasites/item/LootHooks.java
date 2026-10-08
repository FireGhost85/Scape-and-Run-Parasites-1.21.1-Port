package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

/** Extra mob drops registered in code (the registration list is empty in 1.10.9, the hook is kept for completeness). */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class LootHooks {
    private static final List<DropSpec> SPECS = new ArrayList<>();

    private LootHooks() {}

    public static void register(Item item, Class<? extends LivingEntity> mobClass, float chance, int min, int max) {
        SPECS.add(new DropSpec(item, mobClass, null, Mode.CHANCE, clamp(chance), Math.max(1, min), Math.max(min, max)));
    }

    public static void registerById(Item item, ResourceLocation mobId, float chance, int min, int max) {
        SPECS.add(new DropSpec(item, null, mobId, Mode.CHANCE, clamp(chance), Math.max(1, min), Math.max(min, max)));
    }

    public static void registerSkeletonRateById(Item item, ResourceLocation mobId) {
        SPECS.add(new DropSpec(item, null, mobId, Mode.SKELETON_RATE, 1.0f, 0, 0));
    }

    private static float clamp(float v) {
        return v < 0.0f ? 0.0f : (v > 1.0f ? 1.0f : v);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent e) {
        LivingEntity mob = e.getEntity();
        Level w = mob.level();
        if (w.isClientSide || SPECS.isEmpty()) {
            return;
        }
        RandomSource r = w.random;
        int looting = e.getSource().getEntity() instanceof LivingEntity killer ? EnchantmentHelper.getEnchantmentLevel(w.registryAccess().holderOrThrow(net.minecraft.world.item.enchantment.Enchantments.LOOTING), killer) : 0;
        ResourceLocation thisId = BuiltInRegistries.ENTITY_TYPE.getKey(mob.getType());
        for (DropSpec spec : SPECS) {
            boolean match = spec.mobClass != null && spec.mobClass.isInstance(mob) || spec.mobId != null && spec.mobId.equals(thisId);
            if (!match) continue;
            int count = 0;
            if (spec.mode == Mode.SKELETON_RATE) {
                count = r.nextInt(3) + r.nextInt(1 + Math.max(0, looting));
            } else {
                float chance = spec.chance + (float) looting * 0.02f;
                if (r.nextFloat() <= clamp(chance)) {
                    count = spec.min + r.nextInt(spec.max - spec.min + 1);
                }
            }
            if (count <= 0) continue;
            e.getDrops().add(new ItemEntity(w, mob.getX(), mob.getY(), mob.getZ(), new ItemStack(spec.item, count)));
        }
    }

    private record DropSpec(Item item, Class<? extends LivingEntity> mobClass, ResourceLocation mobId, Mode mode, float chance, int min, int max) {}

    private enum Mode {
        CHANCE, SKELETON_RATE
    }
}
