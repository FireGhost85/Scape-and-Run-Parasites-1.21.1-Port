package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.client.ClientHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Field guide: opens the bestiary (sneak: toggles the bestiary sounds). */
public class ItemSRPFieldGuide extends Item {
    public ItemSRPFieldGuide() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            ClientHooks.useFieldGuide(player);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
