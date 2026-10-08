package com.dhanantry.scapeandrunparasites.block;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/** Support tests and raw-meat check shared by the infested bush and the parasite bush. */
final class BushHelper {
    private static final TagKey<Item> RAW_MEAT = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/raw_meat"));
    private static final TagKey<Item> RAW_FISH = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/raw_fish"));

    private BushHelper() {
    }

    /** An SRP block (not bloody ice or ashen glass) whose given face is solid. */
    static boolean isSrpSupport(BlockGetter level, BlockPos supportPos, Direction face) {
        BlockState sup = level.getBlockState(supportPos);
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(sup.getBlock());
        if (id == null) {
            return false;
        }
        String domain = id.getNamespace();
        String path = id.getPath();
        if (!"srparasites".equals(domain)) {
            return false;
        }
        if ("bloodyice".equals(path) || "ashen_glass".equals(path)) {
            return false;
        }
        return sup.isFaceSturdy(level, supportPos, face);
    }

    /** Walks down (at most 64 blocks) while the block is of the given kind and returns the first other position. */
    static BlockPos columnBase(BlockGetter level, BlockPos start, Predicate<Block> isBush) {
        BlockPos base = start;
        for (int guard = 0; guard < 64 && isBush.test(level.getBlockState(base).getBlock()); ++guard) {
            base = base.below();
        }
        return base;
    }

    /** The serialised name of the {@code variant} property of the state, or null. */
    static String variantName(BlockState state) {
        for (Property<?> p : state.getProperties()) {
            if (!"variant".equals(p.getName())) {
                continue;
            }
            Comparable<?> val = state.getValue(p);
            if (val instanceof StringRepresentable s) {
                return s.getSerializedName();
            }
            if (val == null) {
                break;
            }
            return String.valueOf(val);
        }
        return null;
    }

    /** 1.12 {@code isSpecialGround}: parasite stain or stone parasite rubble. */
    static boolean isSpecialGround(BlockGetter level, BlockPos groundPos) {
        BlockState below = level.getBlockState(groundPos);
        Block b = below.getBlock();
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
        if (id == null || !"srparasites".equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        if ("parasitestain".equals(path)) {
            return true;
        }
        if ("parasiterubble".equals(path)) {
            return below.getValue(BlockParasiteRubble.VARIANT) == BlockParasiteRubble.EnumType.STONE;
        }
        return false;
    }

    static boolean isRawMeat(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Item it = stack.getItem();
        if (it == Items.BEEF || it == Items.PORKCHOP || it == Items.CHICKEN || it == Items.MUTTON || it == Items.RABBIT
                || it == Items.COD || it == Items.SALMON || it == Items.TROPICAL_FISH || it == Items.PUFFERFISH) {
            return true;
        }
        return stack.is(RAW_MEAT) || stack.is(RAW_FISH);
    }
}
