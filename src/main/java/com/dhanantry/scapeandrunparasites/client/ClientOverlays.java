package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPFluids;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Screen overlays of 1.10.9: the potion screens (viral, bleed, novision, vomit: ScreenOverlayRenderer), the assimilated pumpkin
 * helmet overlay, the parasite fog block overlay and the dead blood fluid (fog, tint overlay and the jump swim: DeadBlood*Handler).
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class ClientOverlays {
    private static final ResourceLocation VIRAL = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/screen_viral.png");
    private static final ResourceLocation BLEED = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/screen_bleed.png");
    private static final ResourceLocation VOMIT = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/screen_vomit.png");
    private static final ResourceLocation VISION = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/screen_novision.png");
    private static final ResourceLocation PUMPKIN = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/assimilated_pumpkin_overlay.png");
    private static final ResourceLocation FOG_BLOCK_SPRITE = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "block/parasite_fog");
    private static final ResourceLocation DEADBLOOD_SPRITE = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "block/deadblood_flowing");
    private static int vomitY = 0;

    private ClientOverlays() {
    }

    private static void fullScreen(GuiGraphics g, ResourceLocation tex, int x, int y, int w, int h) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        g.blit(tex, x, y, w, h, 0.0f, 0.0f, 1, 1, 1, 1);
        RenderSystem.disableBlend();
    }

    private static boolean eyeInDeadBlood(Player p) {
        return p != null && p.isEyeInFluidType(SRPFluids.DEADBLOOD_TYPE.get());
    }

    @SubscribeEvent
    public static void onGuiPost(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        if (p == null || mc.level == null) {
            return;
        }
        GuiGraphics g = event.getGuiGraphics();
        int w = g.guiWidth();
        int h = g.guiHeight();
        if (p.hasEffect(SRPPotions.VIRA_E)) {
            fullScreen(g, VIRAL, 0, 0, w, h);
        }
        if (p.hasEffect(SRPPotions.BLEED_E)) {
            fullScreen(g, BLEED, 0, 0, w, h);
        }
        if (p.hasEffect(SRPPotions.NOVISION_E)) {
            fullScreen(g, VISION, 0, 0, w, h);
        }
        if (p.hasEffect(SRPPotions.VOMIT_E)) {
            fullScreen(g, VOMIT, 0, vomitY++, w, h * 8);
            if (vomitY >= 0) {
                vomitY = -h * 7;
            }
        } else {
            vomitY = 0;
        }
        if (mc.options.hideGui) {
            return;
        }
        TextureAtlas atlas = mc.getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS);
        BlockPos eye = BlockPos.containing(p.getEyePosition(event.getPartialTick().getGameTimeDeltaPartialTick(false)));
        if (mc.level.getBlockState(eye).is(SRPBlocks.ParasiteFog.get())) {
            TextureAtlasSprite sprite = atlas.getSprite(FOG_BLOCK_SPRITE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            g.blit(0, 0, 0, w, h, sprite, 1.0f, 1.0f, 1.0f, 0.85f);
            RenderSystem.disableBlend();
        }
        if (eyeInDeadBlood(mc.getCameraEntity() instanceof Player cp ? cp : p)) {
            TextureAtlasSprite sprite = atlas.getSprite(DEADBLOOD_SPRITE);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            g.blit(0, 0, 0, w, h, sprite, 1.0f, 1.0f, 1.0f, 0.45f);
            RenderSystem.disableBlend();
        }
    }

    @SubscribeEvent
    public static void onCameraOverlays(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.CAMERA_OVERLAYS)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.getCameraType() != CameraType.FIRST_PERSON) {
            return;
        }
        ItemStack head = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        if (head.isEmpty()) {
            return;
        }
        Item item = head.getItem();
        if (item != SRPBlocks.AssimilatedPumpkin.get().asItem() && item != SRPBlocks.AssimilatedJackOLantern.get().asItem()) {
            return;
        }
        GuiGraphics g = event.getGuiGraphics();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        fullScreen(g, PUMPKIN, 0, 0, g.guiWidth(), g.guiHeight());
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }

    @SubscribeEvent
    public static void onFogColors(ViewportEvent.ComputeFogColor event) {
        if (event.getCamera().getEntity() instanceof Player p && eyeInDeadBlood(p)) {
            event.setRed(0.08f);
            event.setGreen(0.2f);
            event.setBlue(0.07f);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        if (event.getCamera().getEntity() instanceof Player p && eyeInDeadBlood(p)) {
            event.setNearPlaneDistance(0.0f);
            event.setFarPlaneDistance(2.5f);
            event.setCanceled(true);
        }
    }

    /** DeadBloodSwimHandler: holding jump in dead blood lifts the player. */
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player p = mc.player;
        if (p != null && eyeInDeadBlood(p) && mc.options.keyJump.isDown()) {
            var m = p.getDeltaMovement();
            p.setDeltaMovement(m.x, Math.min(m.y + 0.045, 0.12), m.z);
        }
    }
}
