package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

/** 1.12 FontRenderer over the 1.21 Font and the GuiGraphics of the frame. */
public class FontRenderer {
    public int FONT_HEIGHT = 9;

    private Font font() {
        return Minecraft.getInstance().font;
    }

    private static int opaque(int color) {
        return (color & 0xFC000000) == 0 ? color | 0xFF000000 : color;
    }

    public int drawString(String text, int x, int y, int color) {
        return this.drawString(text, (float)x, (float)y, color, false);
    }

    public int drawString(String text, float x, float y, int color, boolean shadow) {
        if (text == null || GuiContext.g == null) {
            return 0;
        }
        return GuiContext.g.drawString(this.font(), text, (int)x, (int)y, opaque(color), shadow);
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        return this.drawString(text, x, y, color, true);
    }

    public void drawSplitString(String text, int x, int y, int wrapWidth, int color) {
        int yy = y;
        for (String line : this.listFormattedStringToWidth(text, wrapWidth)) {
            this.drawString(line, x, yy, color);
            yy += this.FONT_HEIGHT;
        }
    }

    public int getStringWidth(String text) {
        return text == null ? 0 : this.font().width(text);
    }

    public int getCharWidth(char c) {
        return this.font().width(String.valueOf(c));
    }

    public List<String> listFormattedStringToWidth(String text, int width) {
        List<String> out = new ArrayList<>();
        if (text == null) {
            return out;
        }
        for (String paragraph : text.split("\\n", -1)) {
            List<FormattedText> lines = this.font().getSplitter().splitLines(paragraph, Math.max(1, width), Style.EMPTY);
            if (lines.isEmpty()) {
                out.add("");
            }
            for (FormattedText line : lines) {
                out.add(line.getString());
            }
        }
        return out;
    }

    public String trimStringToWidth(String text, int width) {
        return this.font().plainSubstrByWidth(text, width);
    }

    public String trimStringToWidth(String text, int width, boolean reverse) {
        return this.font().plainSubstrByWidth(text, width, reverse);
    }

    public int splitStringWidth(String text, int width) {
        return this.listFormattedStringToWidth(text, width).size() * this.FONT_HEIGHT;
    }

    public static String getFormatFromString(String text) {
        return "";
    }
}
