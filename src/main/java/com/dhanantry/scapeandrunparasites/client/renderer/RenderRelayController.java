package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/** RenderRelayController of 1.10.9: the large relay tower (OBJ) drawn by the controller block entity, with the lit texture while the scan cooldown runs. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public class RenderRelayController implements BlockEntityRenderer<TileEntityRelayController> {
    private static final ModelResourceLocation OFF = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "block/arraytower_off"));
    private static final ModelResourceLocation ON = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "block/arraytower_on"));

    public RenderRelayController(BlockEntityRendererProvider.Context context) {
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(OFF);
        event.register(ON);
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntityRelayController te) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(TileEntityRelayController te) {
        var p = te.getBlockPos();
        return new AABB(p.getX() - 3, p.getY(), p.getZ() - 3, p.getX() + 4, p.getY() + 10, p.getZ() + 4);
    }

    @Override
    public boolean shouldRender(TileEntityRelayController te, net.minecraft.world.phys.Vec3 cameraPos) {
        return true;
    }

    @Override
    public void render(TileEntityRelayController te, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (te == null || te.getLevel() == null) {
            return;
        }
        boolean cooldownActive = te.getCooldownRemainingTicks() > 0;
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(cooldownActive ? ON : OFF);
        int level = Math.max(te.getLevel().getBrightness(net.minecraft.world.level.LightLayer.BLOCK, te.getBlockPos()), te.getLevel().getBrightness(net.minecraft.world.level.LightLayer.SKY, te.getBlockPos()));
        float brightness = 0.35f + (float)level / 15.0f * 0.65f;
        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(RenderType.cutout()), te.getBlockState(), model, brightness, brightness, brightness, 15728880, packedOverlay);
        poseStack.popPose();
    }
}
