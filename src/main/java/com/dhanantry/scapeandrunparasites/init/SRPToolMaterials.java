package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import java.util.function.IntSupplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * Tool tiers of the mod (EnumHelper.addToolMaterial of 1.12): "material_livings" (harvest level 0, uses from the config,
 * speed 1.0, damage 1.0, enchantability 1) and the hijacked iron tools (level 2, 1561 uses, speed 7.0, damage 2.5, enchantability 14).
 * None of them has a repair item.
 */
public final class SRPToolMaterials implements Tier {
    public static final SRPToolMaterials LIVING = new SRPToolMaterials(() -> SRPConfig.weapon_living_durability, 1.0f, 1.0f, 1, BlockTags.INCORRECT_FOR_WOODEN_TOOL);
    public static final SRPToolMaterials HIJACKED_IRON = new SRPToolMaterials(() -> 1561, 7.0f, 2.5f, 14, BlockTags.INCORRECT_FOR_IRON_TOOL);

    private final IntSupplier uses;
    private final float speed;
    private final float damage;
    private final int enchantability;
    private final TagKey<Block> incorrect;

    private SRPToolMaterials(IntSupplier uses, float speed, float damage, int enchantability, TagKey<Block> incorrect) {
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantability = enchantability;
        this.incorrect = incorrect;
    }

    @Override
    public int getUses() {
        return this.uses.getAsInt();
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrect;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
}
