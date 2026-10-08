package com.dhanantry.scapeandrunparasites.item;

import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/** Module (bestiary chapter) item. The tooltip is the translation of {@code tooltip.<registry name>} split at "\n". */
public class ItemModule extends Item {
    private final Kind kind;

    public ItemModule(String name, Kind kind) {
        super(new Item.Properties().stacksTo(1));
        this.kind = kind;
    }

    public Kind getKind() {
        return this.kind;
    }

    public Kind getKind(ItemStack ignored) {
        return this.kind;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        String key = "tooltip." + BuiltInRegistries.ITEM.getKey(this).getPath();
        Language language = Language.getInstance();
        if (language.has(key)) {
            String translated = language.getOrDefault(key);
            if (!translated.isEmpty()) {
                if (translated.contains("\\n")) {
                    for (String line : translated.split("\\\\n")) {
                        tooltip.add(Component.literal(line));
                    }
                } else {
                    tooltip.add(Component.literal(translated));
                }
            }
        }
        tooltip.add(Component.translatable("tooltip.module.profile", this.kind.name()));
    }

    public enum Kind {
        INBORN, ASSIMILATED, HIJACKED, FERAL, CRUDE, PRIMITIVE, ADAPTED, NEXUS, DETERRENT, PURE, PREEMINENT, ANCIENT, DESMOID, ESCHAR,
        RESISTANCE, IDEAL, ORIGIN, ASSIMARA, DERIVED, VECTORS, PHASE, DISLODGEMENT
    }
}
