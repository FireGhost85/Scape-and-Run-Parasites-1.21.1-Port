package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

/** 1.12 GuiTextField over an EditBox. */
public class GuiTextField {
    private final EditBox box;
    private boolean visible = true;
    public int xPosition;
    public int yPosition;
    public int width;
    public int height;

    public GuiTextField(int id, FontRenderer fr, int x, int y, int w, int h) {
        this.xPosition = x;
        this.yPosition = y;
        this.width = w;
        this.height = h;
        this.box = new EditBox(Minecraft.getInstance().font, x, y, w, h, Component.empty());
    }

    public void setText(String text) {
        this.box.setValue(text);
    }

    public String getText() {
        return this.box.getValue();
    }

    public void setMaxStringLength(int length) {
        this.box.setMaxLength(length);
    }

    public void setEnableBackgroundDrawing(boolean enable) {
        this.box.setBordered(enable);
    }

    public void setFocused(boolean focused) {
        this.box.setFocused(focused);
    }

    public boolean isFocused() {
        return this.box.isFocused();
    }

    public void setVisible(boolean v) {
        this.visible = v;
        this.box.visible = v;
    }

    public boolean getVisible() {
        return this.visible;
    }

    public void setTextColor(int color) {
        this.box.setTextColor(color);
    }

    public void setEnabled(boolean enabled) {
        this.box.setEditable(enabled);
    }

    public boolean textboxKeyTyped(char typedChar, int keyCode) {
        if (!this.box.isFocused()) {
            return false;
        }
        int glfw = switch (keyCode) {
            case Keyboard.KEY_BACK -> 259;
            case Keyboard.KEY_DELETE -> 261;
            case Keyboard.KEY_LEFT -> 263;
            case Keyboard.KEY_RIGHT -> 262;
            default -> 0;
        };
        if (glfw != 0) {
            return this.box.keyPressed(glfw, 0, 0);
        }
        return typedChar >= ' ' && this.box.charTyped(typedChar, 0);
    }

    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        return this.box.mouseClicked(mouseX, mouseY, button);
    }

    public void drawTextBox() {
        if (this.visible && GuiContext.g != null) {
            this.box.render(GuiContext.g, 0, 0, 0.0f);
        }
    }

    public void updateCursorCounter() {
    }

    public int getXPosition() {
        return this.box.getX();
    }

    public int getWidth() {
        return this.box.getWidth();
    }
}
