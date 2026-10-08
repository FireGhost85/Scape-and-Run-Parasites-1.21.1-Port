package com.dhanantry.scapeandrunparasites.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Beholder pearl: held in a hand it "ticks" faster the closer an infected / feral / assimara enderman is (up to 100 blocks).
 * The needle value is the item property {@code srparasites:pearl_state}, registered in the client setup with {@link #pearlState}.
 */
public class ItemBeholderPearl extends Item {
    private static final ResourceLocation ID_SIM = ResourceLocation.fromNamespaceAndPath("srparasites", "sim_enderman");
    private static final ResourceLocation ID_SIM_HEAD = ResourceLocation.fromNamespaceAndPath("srparasites", "sim_endermanhead");
    private static final ResourceLocation ID_FERAL = ResourceLocation.fromNamespaceAndPath("srparasites", "fer_enderman");
    private static final ResourceLocation ID_ASSIMARA = ResourceLocation.fromNamespaceAndPath("srparasites", "mar_enderman");
    public static final ResourceLocation KEY_PEARL_STATE = ResourceLocation.fromNamespaceAndPath("srparasites", "pearl_state");
    private static final double SCAN_RADIUS = 100.0;
    private static final int UPDATE_INTERVAL = 6;
    private static final Map<Integer, Cache> CACHE = new HashMap<>();

    public ItemBeholderPearl() {
        super(new Item.Properties().stacksTo(64));
    }

    /** The "pearl_state" item property function (client). */
    public static float pearlState(ItemStack stack, @Nullable Level worldIn, @Nullable LivingEntity entityIn) {
        if (worldIn == null || entityIn == null) {
            return 0.0f;
        }
        boolean holding = !entityIn.getMainHandItem().isEmpty() && entityIn.getMainHandItem().getItem() instanceof ItemBeholderPearl
                || !entityIn.getOffhandItem().isEmpty() && entityIn.getOffhandItem().getItem() instanceof ItemBeholderPearl;
        if (!holding) {
            CACHE.remove(entityIn.getId());
            return 0.0f;
        }
        long now = worldIn.getGameTime();
        Cache c = CACHE.computeIfAbsent(entityIn.getId(), k -> new Cache());
        c.lastAccessTick = now;
        if (now < c.nextScanTick) {
            return c.lastVal;
        }
        c.nextScanTick = now + UPDATE_INTERVAL;
        if ((now & 0xFFL) == 0L) {
            long cutoff = now - 600L;
            CACHE.entrySet().removeIf(e -> e.getValue().lastAccessTick < cutoff);
        }
        double r = SCAN_RADIUS;
        AABB box = new AABB(entityIn.getX() - r, entityIn.getY() - r, entityIn.getZ() - r, entityIn.getX() + r, entityIn.getY() + r, entityIn.getZ() + r);
        List<Entity> matches = worldIn.getEntitiesOfClass(Entity.class, box, e -> {
            if (e == null || e.isRemoved() || e == entityIn) {
                return false;
            }
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType());
            return id.equals(ID_ASSIMARA) || id.equals(ID_FERAL) || id.equals(ID_SIM) || id.equals(ID_SIM_HEAD);
        });
        int tier = 0;
        double nearestSq = Double.POSITIVE_INFINITY;
        for (Entity e2 : matches) {
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(e2.getType());
            int candidate = id.equals(ID_ASSIMARA) ? 3 : (id.equals(ID_FERAL) ? 2 : (id.equals(ID_SIM) || id.equals(ID_SIM_HEAD) ? 1 : 0));
            if (candidate == 0) continue;
            double d2 = e2.distanceToSqr(entityIn);
            if (candidate <= tier && (candidate != tier || !(d2 < nearestSq))) continue;
            tier = candidate;
            nearestSq = d2;
            if (tier == 3) break;
        }
        float out;
        if (tier == 0) {
            out = 0.0f;
        } else {
            double dist = Math.sqrt(nearestSq);
            float proximity = (float) Math.max(0.0, Math.min(1.0, (SCAN_RADIUS - dist) / SCAN_RADIUS));
            long t = now + (long) entityIn.tickCount;
            float freq = 1.5f + 1.0f * (float) tier + 4.0f * proximity;
            float omega = (float) ((double) freq * 2.0 * Math.PI / 20.0);
            float osc = (float) Math.sin((float) t * omega);
            float amp = 0.12f + 0.15f * (float) tier + 0.25f * proximity;
            float base = tier;
            float val = base + 0.1f + amp * osc;
            out = Math.max(base + 0.05f, Math.min(base + 0.45f, val));
        }
        c.lastVal = out;
        return out;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.srparasites.pearl.desc").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tooltip.srparasites.pearl.assimilated").withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.translatable("tooltip.srparasites.pearl.feral").withStyle(ChatFormatting.RED));
        tooltip.add(Component.translatable("tooltip.srparasites.pearl.assimara").withStyle(ChatFormatting.BLUE));
    }

    private static final class Cache {
        long nextScanTick;
        long lastAccessTick;
        float lastVal;
    }
}
