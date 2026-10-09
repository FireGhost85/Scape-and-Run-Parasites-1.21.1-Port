package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import net.minecraft.client.Minecraft;

/** LWJGL 2 Mouse. The wheel delta of the event in progress is set by GuiScreen before handleMouseInput. */
public final class Mouse {
    static int eventDWheel;

    private Mouse() {}

    public static int getEventDWheel() {
        return eventDWheel;
    }

    private static int guiX() {
        Minecraft mc = Minecraft.getInstance();
        return (int)(mc.mouseHandler.xpos() * mc.getWindow().getGuiScaledWidth() / mc.getWindow().getScreenWidth());
    }

    private static int guiY() {
        Minecraft mc = Minecraft.getInstance();
        return (int)(mc.mouseHandler.ypos() * mc.getWindow().getGuiScaledHeight() / mc.getWindow().getScreenHeight());
    }

    /** 1.12 coordinates of the event: x scaled, y counted from the bottom. */
    public static int getEventX() {
        return (int)(Minecraft.getInstance().mouseHandler.xpos() * Minecraft.getInstance().getWindow().getWidth() / Minecraft.getInstance().getWindow().getScreenWidth());
    }

    public static int getEventY() {
        Minecraft mc = Minecraft.getInstance();
        return mc.getWindow().getHeight() - 1 - (int)(mc.mouseHandler.ypos() * mc.getWindow().getHeight() / mc.getWindow().getScreenHeight());
    }

    public static int getX() {
        return getEventX();
    }

    public static int getY() {
        return getEventY();
    }

    public static boolean isButtonDown(int button) {
        return Minecraft.getInstance().mouseHandler.isLeftPressed() && button == 0 || Minecraft.getInstance().mouseHandler.isRightPressed() && button == 1;
    }
}
