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

    /**
     * Cutout without back face culling: the gratings of the tower are single planes that 1.12 drew from both sides (it did not cull the
     * back faces of this model); the vanilla cutout type would only draw the front.
     */
    private static final RenderType CUTOUT_NO_CULL = RenderType.create("srp_relay_cutout_no_cull", com.mojang.blaze3d.vertex.DefaultVertexFormat.BLOCK,
            com.mojang.blaze3d.vertex.VertexFormat.Mode.QUADS, 4194304, true, false,
            RenderType.CompositeState.builder()
                    .setLightmapState(net.minecraft.client.renderer.RenderStateShard.LIGHTMAP)
                    .setShaderState(net.minecraft.client.renderer.RenderStateShard.RENDERTYPE_CUTOUT_SHADER)
                    .setTextureState(net.minecraft.client.renderer.RenderStateShard.BLOCK_SHEET)
                    .setCullState(net.minecraft.client.renderer.RenderStateShard.NO_CULL)
                    .createCompositeState(true));

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
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(poseStack.last(), buffer.getBuffer(CUTOUT_NO_CULL), te.getBlockState(), model, brightness, brightness, brightness, 15728880, packedOverlay);
        poseStack.popPose();
    }
}
