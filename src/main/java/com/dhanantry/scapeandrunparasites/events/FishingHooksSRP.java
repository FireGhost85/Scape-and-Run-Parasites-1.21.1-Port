package com.dhanantry.scapeandrunparasites.events;

import com.dhanantry.scapeandrunparasites.init.SRPItems;
import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class FishingHooksSRP {
    @SubscribeEvent
    public static void onItemFished(ItemFishedEvent e) {
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
        ResourceLocation bn = w.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME).getKey(biome);
        if (bn != null) {
            String path = bn.getPath();
            inHarlequin = path != null && path.toLowerCase(Locale.ROOT).contains("harlequin");
        }
        boolean inDeadblood = false;
        ResourceLocation blockName = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(w.getBlockState(pos).getBlock());
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

