package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

/**
 * The GUI distortion text hooks of 1.10.9 (DistortionGuiSwapHandler, DistortedGuiNewChat, DistortedGuiSubtitleOverlay,
 * DistortedItemHighlightOverlayHandler, DerivedDistortionTextHandler): chat lines, subtitles, item tooltips and the held item name
 * are jumbled while a distortion mob is near. The chat and the subtitle overlay of the in-game GUI are swapped for subclasses.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class DistortionHooks {
    private static boolean swapTried = false;

    private DistortionHooks() {
    }

    private static String plain(FormattedCharSequence seq) {
        StringBuilder sb = new StringBuilder();
        seq.accept((i, style, cp) -> {
            sb.appendCodePoint(cp);
            return true;
        });
        return sb.toString();
    }

    /** The chat component that draws jumbled lines while the distortion is active. */
    private static final class DistortedChatComponent extends ChatComponent {
        private final Minecraft mc;

        DistortedChatComponent(Minecraft mc) {
            super(mc);
            this.mc = mc;
        }

        @Override
        public void render(GuiGraphics g, int tickCount, int mouseX, int mouseY, boolean focused) {
            if (!GuiDistortionHelper.shouldDistortChat(this.mc)) {
                super.render(g, tickCount, mouseX, mouseY, focused);
                return;
            }
            List<GuiMessage.Line> original = new ArrayList<>(this.trimmedMessages);
            try {
                List<GuiMessage.Line> jammed = new ArrayList<>(original.size());
                for (GuiMessage.Line l : original) {
                    String s = plain(l.content());
                    jammed.add(new GuiMessage.Line(l.addedTime(), Component.literal(GuiDistortionHelper.jamText(s)).getVisualOrderText(), l.tag(), l.endOfEntry()));
                }
                this.trimmedMessages.clear();
                this.trimmedMessages.addAll(jammed);
                super.render(g, tickCount, mouseX, mouseY, focused);
            } finally {
                this.trimmedMessages.clear();
                this.trimmedMessages.addAll(original);
            }
        }
    }

    /** Subtitles of the sounds, jumbled while the distortion is active. */
    private static final class DistortedSubtitleOverlay extends SubtitleOverlay {
        private final Minecraft mc;

        DistortedSubtitleOverlay(Minecraft mc) {
            super(mc);
            this.mc = mc;
        }

        @Override
        public void onPlaySound(SoundInstance sound, WeighedSoundEvents accessor, float range) {
            Component sub = accessor.getSubtitle();
            if (sub != null && GuiDistortionHelper.shouldDistortSubtitles(this.mc)) {
                Component jammed = Component.literal(GuiDistortionHelper.jamText(sub.getString()));
                WeighedSoundEvents wrapped = new WeighedSoundEvents(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "distorted"), null) {
                    @Override
                    public Component getSubtitle() {
                        return jammed;
                    }
                };
                super.onPlaySound(sound, wrapped, range);
            } else {
                super.onPlaySound(sound, accessor, range);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (swapTried || mc.gui == null) {
            return;
        }
        swapTried = true;
        try {
            Gui gui = mc.gui;
            DistortedChatComponent chat = new DistortedChatComponent(mc);
            chat.restoreState(gui.chat.storeState());
            gui.chat = chat;
        } catch (Throwable t) {
            GuiDistortionHelper.chatHookAvailable = false;
            ScapeAndRunParasites.LOGGER.warn("Failed to swap the chat component for the GUI distortion", t);
        }
        try {
            Gui gui = mc.gui;
            mc.getSoundManager().removeListener(gui.subtitleOverlay);
            gui.subtitleOverlay = new DistortedSubtitleOverlay(mc);
        } catch (Throwable t) {
            GuiDistortionHelper.subtitleHookAvailable = false;
            ScapeAndRunParasites.LOGGER.warn("Failed to swap the subtitle overlay for the GUI distortion", t);
        }
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (!GuiDistortionHelper.shouldDistortItemTooltips(Minecraft.getInstance())) {
            return;
        }
        List<Component> lines = event.getToolTip();
        for (int i = 0; i < lines.size(); ++i) {
            String s = lines.get(i).getString();
            if (s == null || s.isEmpty()) {
                continue;
            }
            lines.set(i, Component.literal(GuiDistortionHelper.jamText(s)));
        }
    }

    /** The name of the held item above the hotbar. */
    @SubscribeEvent
    public static void onSelectedItemName(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.SELECTED_ITEM_NAME)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !GuiDistortionHelper.shouldDistortItemHighlight(mc)) {
            return;
        }
        Gui gui = mc.gui;
        ItemStack stack = gui.lastToolHighlight;
        if (stack == null || stack.isEmpty() || gui.toolHighlightTimer <= 0) {
            return;
        }
        event.setCanceled(true);
        GuiGraphics g = event.getGuiGraphics();
        MutableComponent name = Component.literal(GuiDistortionHelper.jamText(stack.getHoverName().getString()));
        int w = mc.font.width(name);
        int x = (g.guiWidth() - w) / 2;
        int y = g.guiHeight() - 59;
        if (mc.gameMode != null && !mc.gameMode.canHurtPlayer()) {
            y += 14;
        }
        int alpha = (int)((float)gui.toolHighlightTimer * 256.0f / 10.0f);
        if (alpha > 255) {
            alpha = 255;
        }
        if (alpha > 0) {
            g.fill(x - 2, y - 2, x + w + 2, y + mc.font.lineHeight + 2, mc.options.getBackgroundColor(0));
            g.drawString(mc.font, name, x, y, 16777215 + (alpha << 24));
        }
    }

    private static boolean effectsPushed = false;

    /**
     * The potion icons of the HUD. The 1.10.9 jar has the setting (GUI Distortion Affects Potion HUD) and the check in the helper but
     * nothing that uses it, so the effect is this port's: the icons shake and jump around their corner while the distortion is active.
     */
    @SubscribeEvent
    public static void onEffectsPre(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.EFFECTS)) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || !GuiDistortionHelper.shouldDistortPotionHud(mc)) {
            return;
        }
        GuiGraphics g = event.getGuiGraphics();
        float t = (float) mc.level.getGameTime() + event.getPartialTick().getGameTimeDeltaPartialTick(false);
        // a smooth wobble plus a jump every few ticks
        long step = mc.level.getGameTime() / 4L;
        java.util.Random jump = new java.util.Random(step * 7919L);
        float dx = Mth.sin(t * 0.9f) * 2.0f + (jump.nextFloat() - 0.5f) * 8.0f;
        float dy = Mth.cos(t * 1.3f) * 2.0f + (jump.nextFloat() - 0.5f) * 6.0f;
        float angle = Mth.sin(t * 0.55f) * 4.0f + (jump.nextFloat() - 0.5f) * 10.0f;
        g.pose().pushPose();
        g.pose().translate((float) g.guiWidth(), 0.0f, 0.0f);
        g.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(angle));
        g.pose().translate(dx - (float) g.guiWidth(), dy, 0.0f);
        effectsPushed = true;
    }

    @SubscribeEvent
    public static void onEffectsPost(RenderGuiLayerEvent.Post event) {
        if (effectsPushed && event.getName().equals(VanillaGuiLayers.EFFECTS)) {
            event.getGuiGraphics().pose().popPose();
            effectsPushed = false;
        }
    }
}
