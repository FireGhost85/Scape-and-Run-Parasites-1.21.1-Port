package com.dhanantry.scapeandrunparasites.item;

import net.minecraft.world.item.Item;

/** Item used only as the icon of an advancement (not in the creative tab). */
public class ItemAdvancementIcon extends Item {
    public ItemAdvancementIcon(String name) {
        super(new Item.Properties().stacksTo(1));
    }
}
