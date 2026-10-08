package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

/** Client side of the parasite barrier block entity: border particles while a barrier item is held (client only). */
public final class BarrierClient {
    private static final ResourceLocation BARRIER_ITEM = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "parasite_barrier");

    private BarrierClient() {
    }

    private static boolean isBarrierItem(ItemStack st) {
        return !st.isEmpty() && BARRIER_ITEM.equals(BuiltInRegistries.ITEM.getKey(st.getItem()));
    }

    /** Every second block of the part of the field border within 48 blocks of the player. */
    public static void spawnBorderParticles(Level level, int[] bb) {
        LocalPlayer p = Minecraft.getInstance().player;
        if (p == null) {
            return;
        }
        if (!isBarrierItem(p.getMainHandItem()) && !isBarrierItem(p.getOffhandItem())) {
            return;
        }
        int minX = bb[0];
        int maxX = bb[1];
        int minZ = bb[2];
        int maxZ = bb[3];
        int range = 48;
        double y = Mth.clamp(p.getY(), 2.0, level.getHeight() - 2);
        int px = Mth.floor(p.getX());
        int pz = Mth.floor(p.getZ());
        int fromX = Mth.clamp(px - range, minX, maxX);
        int toX = Mth.clamp(px + range, minX, maxX);
        int fromZ = Mth.clamp(pz - range, minZ, maxZ);
        int toZ = Mth.clamp(pz + range, minZ, maxZ);
        BlockParticleOption marker = new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState());
        double xA = minX + 0.5;
        double xB = maxX + 0.5;
        for (int z = fromZ; z <= toZ; z += 2) {
            double zz = z + 0.5;
            level.addParticle(marker, xA, y, zz, 0.0, 0.0, 0.0);
            level.addParticle(marker, xB, y, zz, 0.0, 0.0, 0.0);
        }
        double zA = minZ + 0.5;
        double zB = maxZ + 0.5;
        for (int x = fromX; x <= toX; x += 2) {
            double xx = x + 0.5;
            level.addParticle(marker, xx, y, zA, 0.0, 0.0, 0.0);
            level.addParticle(marker, xx, y, zB, 0.0, 0.0, 0.0);
        }
    }
}
