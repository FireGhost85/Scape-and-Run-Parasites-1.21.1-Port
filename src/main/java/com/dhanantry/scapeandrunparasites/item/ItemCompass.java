package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.network.CompassPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Base of the node / colony / origin compasses. Once a second the server sends the position of the nearest target (or a
 * random position when there is none) to every player; the "angle" item property (client setup) points the needle to it.
 */
public abstract class ItemCompass extends Item {
    protected byte type;

    public ItemCompass(String n, int t) {
        super(new Item.Properties().stacksTo(64));
        this.type = (byte) t;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && entity.tickCount % 20 == 0) {
            BlockPos pos = this.getOrigin(entity, level);
            if (pos != null) {
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new CompassPayload(pos.getX(), pos.getY(), pos.getZ(), this.type));
            } else {
                com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(new CompassPayload(level.random.nextInt(), level.random.nextInt(), level.random.nextInt(), this.type));
            }
        }
    }

    public abstract BlockPos getOrigin(Entity entity, Level level);

    public abstract BlockPos getOri();
}
