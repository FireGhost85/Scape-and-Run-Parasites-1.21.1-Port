package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

public class FishingHooksSRP {
    public static void register() {
        MinecraftForge.EVENT_BUS.register(new FishingHooksSRP());
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent e) {
        FishingHook hook = e.getHookEntity();
        if (hook == null) {
            return;
        }
        Level w = hook.level();
        if (w.isClientSide) {
            return;
        }
        BlockPos pos = BlockPos.containing(hook.getX(), hook.getY(), hook.getZ());
        Biome biome = w.getBiome(pos).value();
        boolean inHarlequin = false;
        ResourceLocation bn = (ResourceLocation)Biome.REGISTRY.getNameForObject(biome);
        if (bn != null) {
            String path = bn.getPath();
            inHarlequin = path != null && path.toLowerCase(Locale.ROOT).contains("harlequin");
        }
        boolean inDeadblood = false;
        ResourceLocation blockName = w.getBlockState(pos).getBlock().builtInRegistryHolder().key().location();
        if (blockName != null) {
            String bp = blockName.getPath();
            boolean bl = inDeadblood = bp != null && bp.toLowerCase(Locale.ROOT).contains("deadblood");
        }
        if (inHarlequin || inDeadblood) {
            e.getDrops().clear();
            e.getDrops().add(new ItemStack(SRPItems.fishlin.get()));
        }
    }
}

