package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

/** 1.12 GuiButton (drawn with the 1.21 button sprites). */
public class GuiButton extends Gui {
    private static final ResourceLocation ENABLED = ResourceLocation.withDefaultNamespace("widget/button");
    private static final ResourceLocation DISABLED = ResourceLocation.withDefaultNamespace("widget/button_disabled");
    private static final ResourceLocation HIGHLIGHTED = ResourceLocation.withDefaultNamespace("widget/button_highlighted");
    public int width = 200;
    public int height = 20;
    public int x;
    public int y;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled = true;
    public boolean visible = true;
    protected boolean hovered;

    public GuiButton(int buttonId, int x, int y, String text) {
        this(buttonId, x, y, 200, 20, text);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String text) {
        this.id = buttonId;
        this.x = x;
        this.y = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = text;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            return;
        }
        GuiGraphics g = GuiContext.g;
        this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        ResourceLocation sprite = !this.enabled ? DISABLED : (this.hovered ? HIGHLIGHTED : ENABLED);
        g.blitSprite(sprite, this.x, this.y, this.width, this.height);
        int color = !this.enabled ? 10526880 : (this.hovered ? 16777120 : 14737632);
        g.drawCenteredString(mc.font, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, color | 0xFF000000);
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void playPressSound(Minecraft mc) {
        mc.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }
}
