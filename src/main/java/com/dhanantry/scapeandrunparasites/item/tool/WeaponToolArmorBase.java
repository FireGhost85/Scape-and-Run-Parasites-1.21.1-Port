package com.dhanantry.scapeandrunparasites.item.tool;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.ReportData;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Living / sentient armor: counts the hits it absorbed ("srphits" in the stack data), evolves into the sentient piece
 * and lists the resistances it adapted to ("sprresistances"/"sprresistancei").
 */
public class WeaponToolArmorBase extends ArmorItem {
    protected boolean calling;
    protected byte idTool;

    public WeaponToolArmorBase(Holder<ArmorMaterial> material, String name, int renderI, boolean fear, int id, ArmorItem.Type type) {
        super(material, type, new Item.Properties().durability(type.getDurability(fear ? SRPConfig.sentienDura : SRPConfig.livingDura)));
        this.calling = fear;
        this.idTool = (byte) id;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        if (level.isClientSide) {
            return;
        }
        if (this.calling && SRPConfigSystems.useScent && level.random.nextInt(10) == 0 && entity.tickCount % 40 == 0
                && SRPSaveData.get(level).getDeveLevel() >= SRPConfigSystems.deveScentUse && entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(SRPPotions.PREY_E, 1200, 0, false, false));
        }
        if (entity.tickCount % 80 == 0 && this.getNext() != null) {
            CompoundTag compound = ReportData.read(stack);
            int key = compound.contains("srphits") ? compound.getInt("srphits") : 0;
            if (key >= SRPConfig.weapon_livingSentient_DAMAGE_needed) {
                ReportData.update(stack, tag -> tag.putInt("srphits", 0));
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

    public boolean canCall() {
        return this.calling;
    }

    public Item getNext() {
        if (this == SRPItems.armor_helmet.get()) {
            return SRPItems.armor_helmetSentient.get();
        }
        if (this == SRPItems.armor_chest.get()) {
            return SRPItems.armor_chestSentient.get();
        }
        if (this == SRPItems.armor_pants.get()) {
            return SRPItems.armor_pantsSentient.get();
        }
        if (this == SRPItems.armor_boots.get()) {
            return SRPItems.armor_bootsSentient.get();
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        ArrayList<String> resistanceS = new ArrayList<>();
        ArrayList<Integer> resistanceI = new ArrayList<>();
        CompoundTag compound = ReportData.read(stack);
        tooltip.add(Component.literal("---> " + compound.getInt("srphits")).withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("  ").withStyle(ChatFormatting.BLUE));
        if (compound.contains("sprresistances")) {
            ListTag allResS = compound.getList("sprresistances", 10);
            ListTag allResI = compound.getList("sprresistancei", 10);
            if (allResS.size() != allResI.size()) {
                return;
            }
            for (int i = 0; i < allResS.size(); ++i) {
                resistanceS.add(i, allResS.getCompound(i).getString("resistance" + i));
                resistanceI.add(i, allResI.getCompound(i).getInt("resistance" + i));
            }
            tooltip.add(Component.literal("Current Adaptation:").withStyle(ChatFormatting.DARK_PURPLE));
            for (int i = 0; i < resistanceS.size(); ++i) {
                double reduc = Math.min(resistanceI.get(i), this.calling ? SRPConfig.sentientPointCap : SRPConfig.livingPointCap);
                double d = this.calling ? (double) SRPConfig.sentientPointReduction : (double) SRPConfig.livingPointReduction;
                DecimalFormat decimalFormat = new DecimalFormat("##.##");
                decimalFormat.setRoundingMode(RoundingMode.DOWN);
                reduc *= d;
                String formatResult = decimalFormat.format(reduc * 100.0);
                tooltip.add(Component.literal("-> " + resistanceS.get(i)).withStyle(ChatFormatting.YELLOW));
                tooltip.add(Component.literal(" reduction: " + formatResult + "%").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
}
