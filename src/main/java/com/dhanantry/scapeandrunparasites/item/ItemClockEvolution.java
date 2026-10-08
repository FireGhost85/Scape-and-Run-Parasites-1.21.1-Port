package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.network.ClockPayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Evolution clock: shows the current phase on its model (item property "phase", registered in the client setup) and the
 * remaining cooldown in the tooltip. The values are static and refreshed by the server once per second while held.
 */
public class ItemClockEvolution extends ItemBase {
    public static int cooldown = 0;
    public static int phase = 0;

    public ItemClockEvolution(String name, int maxStack, int v) {
        super(name, maxStack, v);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity.tickCount % 20 == 0) {
            SRPSaveData data = SRPSaveData.get(level);
            if (data != null) {
                cooldown = data.getCooldown(level, DimKeys.of(level));
                phase = data.getEvolutionPhase(DimKeys.of(level));
                PacketDistributor.sendToAllPlayers(new ClockPayload(cooldown, phase, data.getDeveLevel(), 2));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("-> " + cooldown).withStyle(ChatFormatting.AQUA));
    }
}
