package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.network.ClockPayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

/** Development clock: shows the universal development level on its model (item property "level", client setup). */
public class ItemClockDevelopment extends ItemBase {
    public static int level = 0;

    public ItemClockDevelopment(String name, int maxStack, int v) {
        super(name, maxStack, v);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide && entity.tickCount % 20 == 0) {
            SRPSaveData data = SRPSaveData.get(world);
            if (data != null) {
                PacketDistributor.sendToAllPlayers(new ClockPayload(data.getCooldown(world, DimKeys.of(world)), data.getEvolutionPhase(DimKeys.of(world)), data.getDeveLevel(), 2));
            }
        }
    }
}
