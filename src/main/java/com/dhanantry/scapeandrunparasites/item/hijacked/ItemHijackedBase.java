package com.dhanantry.scapeandrunparasites.item.hijacked;

import java.util.List;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

/** Shared tooltip of the hijacked tools: {@code item.srparasites.<name>.desc} when the language file has it. */
public final class ItemHijackedBase {
    private ItemHijackedBase() {}

    public static void describe(String name, List<Component> tooltip) {
        String key = "item.srparasites." + name + ".desc";
        if (Language.getInstance().has(key)) {
            tooltip.add(Component.translatable(key));
        }
    }
}
