package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

/** The 1.12 Gui base class: the drawing helpers (static and instance). */
public class Gui {
    public float zLevel;

    public static void drawDefaultBackground(int width, int height) {
        GuiContext.g.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);
    }

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        GuiContext.g.fill(Math.min(left, right), Math.min(top, bottom), Math.max(left, right), Math.max(top, bottom), color);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        GuiContext.g.fillGradient(left, top, right, bottom, startColor, endColor);
    }

    public static void drawCenteredString(FontRenderer fr, String text, int x, int y, int color) {
        fr.drawStringWithShadow(text, (float)(x - fr.getStringWidth(text) / 2), (float)y, color);
    }

    public static void drawString(FontRenderer fr, String text, int x, int y, int color) {
        fr.drawStringWithShadow(text, (float)x, (float)y, color);
    }

    public static void drawHoveringText(List<String> lines, int x, int y) {
        if (lines.isEmpty()) {
            return;
        }
        GuiContext.g.renderComponentTooltip(Minecraft.getInstance().font, lines.stream().map(Component::literal).collect(Collectors.toList()), x, y);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int w, int h) {
        if (GuiContext.texture == null) {
            return;
        }
        GuiContext.applyColor();
        GuiContext.g.blit(GuiContext.texture, x, y, u, v, w, h, 256, 256);
        GuiContext.resetColor();
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int w, int h, float texW, float texH) {
        if (GuiContext.texture == null) {
            return;
        }
        GuiContext.applyColor();
        GuiContext.g.blit(GuiContext.texture, x, y, w, h, u, v, w, h, (int)texW, (int)texH);
        GuiContext.resetColor();
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int w, int h, float texW, float texH) {
        if (GuiContext.texture == null) {
            return;
        }
        GuiContext.applyColor();
        GuiContext.g.blit(GuiContext.texture, x, y, w, h, u, v, uWidth, vHeight, (int)texW, (int)texH);
        GuiContext.resetColor();
    }

    public static void renderItem(ItemStack stack, int x, int y) {
        GuiContext.g.renderItem(stack, x, y);
        GuiContext.g.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
    }
}
