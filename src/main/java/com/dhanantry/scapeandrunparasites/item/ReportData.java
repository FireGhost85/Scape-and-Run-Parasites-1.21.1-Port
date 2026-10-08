package com.dhanantry.scapeandrunparasites.item;

import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/** The 1.12 {@code stack.getTagCompound()} of the printed report items (NBT lives in the custom-data component now). */
public final class ReportData {
    private ReportData() {}

    /** A copy of the stack's tag (empty when there is none). */
    public static CompoundTag read(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data == null ? new CompoundTag() : data.copyTag();
    }

    public static boolean has(ItemStack stack, String key) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(key);
    }

    /** Mutates the stack's tag. */
    public static void update(ItemStack stack, Consumer<CompoundTag> editor) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, editor);
    }
}
