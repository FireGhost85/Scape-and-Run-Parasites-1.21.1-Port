package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.model.ModelMobilityArmor;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderHandEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * The mobility armor on screen: the worn model of the four pieces (getArmorModel of 1.10.9) and the arm of the first person view
 * (MobilityArmorFirstPersonHandler): with the chestpiece on and the main hand empty, the arm is drawn with the armor's arm model
 * instead of the bare arm, placed with the transform of the vanilla first person arm.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class MobilityArmorClient {
    private static ModelMobilityArmor model;

    private MobilityArmorClient() {
    }

    private static ModelMobilityArmor model() {
        if (model == null) {
            model = new ModelMobilityArmor();
        }
        return model;
    }

    @SubscribeEvent
    static void registerExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(new IClientItemExtensions() {
            @Override
            public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                return model().prepare(entity, Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false));
            }
        }, SRPItems.mobility_armor_helmet.get(), SRPItems.mobility_armor_chestpiece.get(), SRPItems.mobility_armor_leggings.get(), SRPItems.mobility_armor_boots.get());
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.options.getCameraType() != CameraType.FIRST_PERSON || player.isInvisible()) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND || !event.getItemStack().isEmpty()) {
            return;
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty() || chest.getItem() != SRPItems.mobility_armor_chestpiece.get()) {
            return;
        }
        event.setCanceled(true);
        HumanoidArm side = player.getMainArm();
        PoseStack pose = event.getPoseStack();
        pose.pushPose();
        applyArmTransform(pose, side, event.getEquipProgress(), event.getSwingProgress());
        pose.translate(0.0f, 0.03f, 0.0f);
        model().renderFirstPersonArm(pose, event.getMultiBufferSource().getBuffer(RenderType.armorCutoutNoCull(ModelMobilityArmor.TEXTURE)), event.getPackedLight(),
                OverlayTexture.NO_OVERLAY, -1, side);
        pose.popPose();
    }

    /** The transform of the first person arm of 1.10.9's handler (applyVanillaFirstPersonArmTransform). */
    private static void applyArmTransform(PoseStack pose, HumanoidArm side, float equipProgress, float swingProgress) {
        float sideSign = side == HumanoidArm.RIGHT ? 1.0f : -1.0f;
        float swingRoot = Mth.sqrt(swingProgress);
        float xSwing = -0.3f * Mth.sin(swingRoot * (float) Math.PI);
        float ySwing = 0.4f * Mth.sin(swingRoot * ((float) Math.PI * 2.0f));
        float zSwing = -0.4f * Mth.sin(swingProgress * (float) Math.PI);
        pose.translate(sideSign * (xSwing + 0.64000005f), ySwing - 0.35f + equipProgress * -0.6f, zSwing - 0.71999997f);
        pose.mulPose(Axis.YP.rotationDegrees(sideSign * 45.0f));
        float swingCurveA = Mth.sin(swingProgress * swingProgress * (float) Math.PI);
        float swingCurveB = Mth.sin(swingRoot * (float) Math.PI);
        pose.mulPose(Axis.YP.rotationDegrees(sideSign * swingCurveB * 70.0f));
        pose.mulPose(Axis.ZP.rotationDegrees(sideSign * swingCurveA * -20.0f));
        pose.translate(sideSign * -1.0f, 3.6f, 3.5f);
        pose.mulPose(Axis.ZP.rotationDegrees(sideSign * 120.0f));
        pose.mulPose(Axis.XP.rotationDegrees(200.0f));
        pose.mulPose(Axis.YP.rotationDegrees(sideSign * -135.0f));
        pose.translate(sideSign * 5.6f, 0.0f, 0.0f);
    }
}
