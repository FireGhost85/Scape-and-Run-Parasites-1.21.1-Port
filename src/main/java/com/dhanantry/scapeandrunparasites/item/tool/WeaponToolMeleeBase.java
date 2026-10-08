package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.ReportData;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

/**
 * Base of the living / sentient melee weapons: custom attack damage, attack speed and reach, a kill counter ("srpkills", the
 * summed max health of the killed mobs) that evolves the living weapon into the sentient one, and the Prey effect of the sentient ones.
 */
public class WeaponToolMeleeBase extends SwordItem implements IHaveReach {
    private static final ResourceLocation REACH_ID = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "weapon_reach");

    private final float addReach;
    protected double attackSpeed;
    protected boolean calling;
    protected byte idTool;

    public WeaponToolMeleeBase(Tier material, String name, double attackspeed, float range, float attackD, boolean fear, int id) {
        super(material, new Item.Properties().attributes(attributes(attackD, attackspeed, range)));
        this.attackSpeed = attackspeed;
        this.addReach = range;
        this.calling = fear;
        this.idTool = (byte) id;
    }

    private static ItemAttributeModifiers attributes(float attackDamage, double attackSpeed, float reach) {
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        if (SRPConfig.weaponCancelPacket) {
            builder.add(Attributes.ENTITY_INTERACTION_RANGE, new AttributeModifier(REACH_ID, reach, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        }
        return builder.build();
    }

    @Override
    public float getReach() {
        return this.addReach;
    }

    /** Adds the max health of a killed mob to the weapon's kill counter. */
    protected static void addKills(ItemStack stack, float maxHealth) {
        ReportData.update(stack, tag -> tag.putInt("srpkills", tag.contains("srpkills") ? (int) ((float) tag.getInt("srpkills") + maxHealth) : (int) maxHealth));
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        boolean flag = super.hurtEnemy(stack, target, attacker);
        if (flag && target.getHealth() <= 0.0f) {
            addKills(stack, target.getMaxHealth());
        }
        return flag;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide) {
            return;
        }
        if (this.calling && SRPConfigSystems.useScent && level.random.nextInt(100) == 0 && entity.tickCount % 40 == 0
                && SRPSaveData.get(level).getDeveLevel() >= SRPConfigSystems.deveScentUse && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(SRPPotions.PREY_E, 1200, 0, false, false));
        }
        if (entity.tickCount % 80 == 0 && this.getNext() != null) {
            CompoundTag compound = ReportData.read(stack);
            int key = compound.contains("srpkills") ? compound.getInt("srpkills") : 0;
            if (key > SRPConfig.weapon_livingSentient_HP_needed) {
                ReportData.update(stack, tag -> tag.putInt("srpkills", 0));
                stack.shrink(1);
                ItemEntity drop = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(this.getNext(), 1));
                drop.setDefaultPickUpDelay();
                level.addFreshEntity(drop);
                if (SRPConfig.thunderEnable) {
                    SRPEntityUtil.lightning(level, entity.getX(), entity.getY(), entity.getZ(), true);
                }
            }
        }
    }

    /** The sentient version this weapon evolves into (null for the sentient weapons). */
    public Item getNext() {
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag compound = ReportData.read(stack);
        if (!compound.isEmpty()) {
            tooltip.add(Component.literal("---> " + compound.getInt("srpkills")).withStyle(ChatFormatting.BLUE));
            tooltip.add(Component.literal("  ").withStyle(ChatFormatting.BLUE));
        }
    }

    /** The three tooltip lines every melee weapon appends. */
    protected void addWeaponLines(List<Component> tooltip) {
        tooltip.add(Component.translatable("tootip.srparasites.weaponm." + this.idTool).withStyle(ChatFormatting.YELLOW));
        tooltip.add(Component.translatable("tootip.srparasites.weaponm." + this.idTool * 10).withStyle(ChatFormatting.RED));
        if (this.calling && SRPConfigSystems.useScent) {
            tooltip.add(Component.translatable("tootip.srparasites.weaponm." + this.idTool * 100).withStyle(ChatFormatting.BLACK));
        }
    }
}
