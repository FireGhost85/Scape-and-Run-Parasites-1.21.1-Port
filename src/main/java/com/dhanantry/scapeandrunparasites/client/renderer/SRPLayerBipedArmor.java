package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.LayerRenderer;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLivingBase;
import com.dhanantry.scapeandrunparasites.client.model.entity.SRPModelBiped;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;

/**
 * Draws the legs, feet and head armor of a mob on the SRPModelBiped (SRPLayerBipedArmor of 1.12, used by the infected player).
 * The chest piece is not drawn, as in the original. Armor textures come from the 1.21 armor material layers.
 */
public class SRPLayerBipedArmor<T extends LivingEntity> implements LayerRenderer<T> {
    private final RenderLivingBase<?> renderer;
    private final SRPModelBiped modelLeggings = new SRPModelBiped(0.5f);
    private final SRPModelBiped modelArmor = new SRPModelBiped(1.0f);

    public SRPLayerBipedArmor(RenderLivingBase<?> renderer) {
        this.renderer = renderer;
    }

    @Override
    public void doRenderLayer(T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        byte type = (byte)(entity instanceof EntityInfPlayer ? 1 : 0);
        this.renderArmorLayer(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, EquipmentSlot.LEGS, type);
        this.renderArmorLayer(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, EquipmentSlot.FEET, type);
        this.renderArmorLayer(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, EquipmentSlot.HEAD, type);
    }

    private void renderArmorLayer(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale, EquipmentSlot slot, byte type) {
        ItemStack stack = entity.getItemBySlot(slot);
        if (!(stack.getItem() instanceof ArmorItem armor) || armor.getEquipmentSlot() != slot) {
            return;
        }
        SRPModelBiped model = slot == EquipmentSlot.LEGS ? this.modelLeggings : this.modelArmor;
        model.setParent(type);
        model.isChild = entity.isBaby();
        model.setInvisible(false);
        switch (slot) {
            case HEAD -> {
                model.jointH.showModel = true;
                model.jointHW.showModel = true;
            }
            case LEGS -> {
                model.body.showModel = true;
                model.rightL.showModel = true;
                model.leftL.showModel = true;
            }
            case FEET -> {
                model.rightL.showModel = true;
                model.leftL.showModel = true;
            }
            default -> {
            }
        }
        boolean inner = slot == EquipmentSlot.LEGS;
        for (ArmorMaterial.Layer layer : armor.getMaterial().value().layers()) {
            this.renderer.bindTexture(layer.texture(inner));
            float r = 1.0f;
            float g = 1.0f;
            float b = 1.0f;
            if (layer.dyeable() && stack.has(DataComponents.DYED_COLOR)) {
                int color = DyedItemColor.getOrDefault(stack, -6265536);
                r = (float)(color >> 16 & 0xFF) / 255.0f;
                g = (float)(color >> 8 & 0xFF) / 255.0f;
                b = (float)(color & 0xFF) / 255.0f;
            }
            GlStateManager.color(r, g, b, 1.0f);
            model.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
