package com.dhanantry.scapeandrunparasites.client.legacy.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.GlContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/** The 1.12 GuiScreen API (initGui, drawScreen, mouse and key hooks, button list) on a 1.21 Screen. */
public class GuiScreen extends Screen {
    public Minecraft mc = Minecraft.getInstance();
    public FontRenderer fontRendererObj = new FontRenderer();
    public List<GuiButton> buttonList = new ArrayList<>();
    protected GuiButton selectedButton;

    public GuiScreen() {
        super(Component.empty());
    }

    // ---- 1.12 hooks to override
    public void initGui() {
        this.buttonList.clear();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (GuiButton button : this.buttonList) {
            button.drawButton(this.mc, mouseX, mouseY, partialTicks);
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0) {
            for (GuiButton button : this.buttonList) {
                if (button.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedButton = button;
                    button.playPressSound(this.mc);
                    this.actionPerformed(button);
                    return;
                }
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (this.selectedButton != null && state == 0) {
            this.selectedButton.mouseReleased(mouseX, mouseY);
            this.selectedButton = null;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            this.mc.setScreen(null);
        }
    }

    public void handleMouseInput() throws IOException {
    }

    public void onGuiClosed() {
    }

    public void updateScreen() {
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    public void onResize(Minecraft mcIn, int w, int h) {
    }

    // ---- bridge to the 1.21 Screen
    @Override
    protected void init() {
        this.mc = Minecraft.getInstance();
        this.initGui();
    }

    @Override
    public void tick() {
        this.updateScreen();
    }

    @Override
    public boolean isPauseScreen() {
        return this.doesGuiPauseGame();
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        GuiContext.g = g;
        com.mojang.blaze3d.vertex.PoseStack pose = g.pose();
        pose.pushPose();
        GlContext.begin(pose, g.bufferSource(), 15728880);
        try {
            this.drawScreen(mouseX, mouseY, partialTick);
        } finally {
            GuiContext.disableScissor();
            GlContext.end();
            pose.popPose();
            GuiContext.resetColor();
            g.flush();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        try {
            this.mouseClicked((int)mouseX, (int)mouseY, button);
        } catch (IOException e) {
            // 1.12 declared IOException on the hooks
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.mouseReleased((int)mouseX, (int)mouseY, button);
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.mouseClickMove((int)mouseX, (int)mouseY, button, 0L);
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        Mouse.eventDWheel = (int)Math.signum(scrollY) * 120;
        try {
            this.handleMouseInput();
        } catch (IOException e) {
            // see above
        } finally {
            Mouse.eventDWheel = 0;
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int key = Keyboard.fromGlfw(keyCode);
        if (key != 0) {
            try {
                this.keyTyped((char)0, key);
            } catch (IOException e) {
                // see above
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        try {
            this.keyTyped(codePoint, 0);
        } catch (IOException e) {
            // see above
        }
        return true;
    }

    @Override
    public void removed() {
        this.onGuiClosed();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.onResize(minecraft, width, height);
    }

    // ---- drawing helpers (Gui)
    public void drawDefaultBackground() {
        Gui.drawDefaultBackground(this.width, this.height);
    }

    public static void drawRect(int left, int top, int right, int bottom, int color) {
        Gui.drawRect(left, top, right, bottom, color);
    }

    public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        Gui.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    public void drawCenteredString(FontRenderer fr, String text, int x, int y, int color) {
        Gui.drawCenteredString(fr, text, x, y, color);
    }

    public void drawString(FontRenderer fr, String text, int x, int y, int color) {
        Gui.drawString(fr, text, x, y, color);
    }

    public void drawHoveringText(List<String> lines, int x, int y) {
        Gui.drawHoveringText(lines, x, y);
    }

    public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h) {
        Gui.drawTexturedModalRect(x, y, u, v, w, h);
    }

    public static void drawModalRectWithCustomSizedTexture(int x, int y, float u, float v, int w, int h, float texW, float texH) {
        Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, texW, texH);
    }

    public static void drawScaledCustomSizeModalRect(int x, int y, float u, float v, int uWidth, int vHeight, int w, int h, float texW, float texH) {
        Gui.drawScaledCustomSizeModalRect(x, y, u, v, uWidth, vHeight, w, h, texW, texH);
    }

    public static void renderItem(ItemStack stack, int x, int y) {
        Gui.renderItem(stack, x, y);
    }
}
