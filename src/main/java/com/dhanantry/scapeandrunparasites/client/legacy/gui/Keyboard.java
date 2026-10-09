package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/** LWJGL 2 Keyboard (key codes of 1.12 for the keys the GUI code uses). */
public final class Keyboard {
    public static final int KEY_ESCAPE = 1;
    public static final int KEY_RETURN = 28;
    public static final int KEY_BACK = 14;
    public static final int KEY_LSHIFT = 42;
    public static final int KEY_RSHIFT = 54;
    public static final int KEY_LCONTROL = 29;
    public static final int KEY_RCONTROL = 157;
    public static final int KEY_DELETE = 211;
    public static final int KEY_LEFT = 203;
    public static final int KEY_RIGHT = 205;

    private Keyboard() {}

    public static void enableRepeatEvents(boolean enable) {
    }

    public static boolean isKeyDown(int key) {
        long window = Minecraft.getInstance().getWindow().getWindow();
        return switch (key) {
            case KEY_LSHIFT, KEY_RSHIFT -> GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == 1 || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == 1;
            case KEY_LCONTROL, KEY_RCONTROL -> GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == 1 || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == 1;
            default -> false;
        };
    }

    /** GLFW to LWJGL 2 for the keys that matter to the text fields. */
    static int fromGlfw(int glfw) {
        return switch (glfw) {
            case GLFW.GLFW_KEY_ESCAPE -> KEY_ESCAPE;
            case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> KEY_RETURN;
            case GLFW.GLFW_KEY_BACKSPACE -> KEY_BACK;
            case GLFW.GLFW_KEY_DELETE -> KEY_DELETE;
            case GLFW.GLFW_KEY_LEFT -> KEY_LEFT;
            case GLFW.GLFW_KEY_RIGHT -> KEY_RIGHT;
            default -> 0;
        };
    }
}
