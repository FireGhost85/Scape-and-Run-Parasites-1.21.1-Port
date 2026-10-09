package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.blocks.BlockBestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.SRPBlockCompendiumRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BlocksPage
extends GuiScreen {
    private final Player player;
    private final GuiScreen parent;
    private boolean isJumbled;
    private final List<BlockBestiaryEntry> discoveredBlocks = new ArrayList<BlockBestiaryEntry>();
    private int scroll = 0;
    private static final ResourceLocation TEX_BACKGROUND = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/blocks_background.png");
    private static final ResourceLocation TEX_SLOT = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/slot.png");
    private static final int GRID_TOP = 40;
    private static final int GRID_BOTTOM = 210;
    private static final int CELL_SIZE = 20;
    private static final int CELL_PADDING = 4;
    private static final int CELL_STRIDE = 24;
    private static final int GAP_COLS = 4;
    private static final int GRID_Y_OFFSET = 8;
    private static final int PAGE_INNER_SHIFT = 10;

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    public BlocksPage(Player player, GuiScreen parent) {
        this.player = player;
        this.parent = parent;
    }

    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.buttonList.add(new GuiButton(1, 10, 10, 60, 20, this.distort("< " + GuiContext.fmt((String)"bestiary.blocks.home", (Object[])new Object[0]))));
        this.syncDiscoveredBlocks();
    }

    private void syncDiscoveredBlocks() {
        this.discoveredBlocks.clear();
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        if (prog == null) {
            return;
        }
        for (BlockBestiaryEntry entry : SRPBlockCompendiumRegistry.all()) {
            if (!prog.hasSeenBlock(entry.id)) continue;
            this.discoveredBlocks.add(entry);
        }
        Collections.sort(this.discoveredBlocks, new Comparator<BlockBestiaryEntry>(){

            @Override
            public int compare(BlockBestiaryEntry a, BlockBestiaryEntry b) {
                String an = GuiContext.fmt((String)a.nameKey, (Object[])new Object[0]);
                String bn = GuiContext.fmt((String)b.nameKey, (Object[])new Object[0]);
                return an.compareToIgnoreCase(bn);
            }
        });
        this.scroll = 0;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 1) {
            if (this.parent != null) {
                this.mc.setScreen(this.parent);
            } else {
                this.mc.setScreen(null);
            }
            return;
        }
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel == 0) {
            return;
        }
        int direction = (int)Math.signum(dWheel);
        int maxScroll = this.getMaxScroll();
        this.scroll -= direction * 12;
        if (this.scroll < 0) {
            this.scroll = 0;
        }
        if (this.scroll > maxScroll) {
            this.scroll = maxScroll;
        }
    }

    private int getColumns() {
        int gapWidth = 96;
        int availableWidth = this.width - 40 - gapWidth;
        int cols = availableWidth / 24;
        if (cols < 2) {
            cols = 2;
        }
        return cols;
    }

    private int getMaxScroll() {
        if (this.discoveredBlocks.isEmpty()) {
            return 0;
        }
        int cols = this.getColumns();
        int rows = (this.discoveredBlocks.size() + cols - 1) / cols;
        int contentHeight = rows * 24;
        int viewHeight = 170;
        return Math.max(0, contentHeight - viewHeight);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int marginSides;
        this.drawDefaultBackground();
        GuiContext.bind(TEX_BACKGROUND);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int marginTop = 10;
        int marginBottom = 10;
        int bgX = marginSides = 20;
        int bgY = marginTop;
        int bgWidth = this.width - marginSides * 2;
        int bgHeight = this.height - marginTop - marginBottom;
        float texW = 256.0f;
        float texH = 256.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)bgX, (float)bgY, (float)0.0f);
        GlStateManager.scale((float)((float)bgWidth / texW), (float)((float)bgHeight / texH), (float)1.0f);
        this.drawTexturedModalRect(0, 0, 0, 0, (int)texW, (int)texH);
        GlStateManager.popMatrix();
        int titleY = 22;
        int subtitleY = 32;
        this.drawCenteredString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.blocks.title", (Object[])new Object[0])), this.width / 2, titleY, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.blocks.subtitle", (Object[])new Object[0])), this.width / 2, subtitleY, 0xAAAAAA);
        this.drawBlockGrid(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawBlockGrid(int mouseX, int mouseY, float partialTicks) {
        if (this.discoveredBlocks.isEmpty()) {
            this.drawCenteredString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.blocks.empty", (Object[])new Object[0])), this.width / 2, 125, 0x666666);
            return;
        }
        int slotsPerRow = this.getColumns();
        int leftCols = slotsPerRow / 2;
        int rightCols = slotsPerRow - leftCols;
        int gapWidthPx = 96;
        int totalRowWidth = slotsPerRow * 24 + gapWidthPx;
        int startX = (this.width - totalRowWidth) / 2;
        int yTop = 48 - this.scroll;
        BlockBestiaryEntry hovered = null;
        float t = ((float)this.player.tickCount + partialTicks) / 10.0f;
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        for (int i = 0; i < this.discoveredBlocks.size(); ++i) {
            BlockBestiaryEntry entry = this.discoveredBlocks.get(i);
            int row = i / slotsPerRow;
            int indexInRow = i % slotsPerRow;
            boolean isRightPage = indexInRow >= leftCols;
            int colLocal = isRightPage ? indexInRow - leftCols : indexInRow;
            int leftStartX = startX + 10;
            int rightStartX = startX + leftCols * 24 + gapWidthPx - 10;
            int cellX = isRightPage ? rightStartX + colLocal * 24 : leftStartX + colLocal * 24;
            int cellY = yTop + row * 24;
            if (cellY + 20 < 40 || cellY > 210) continue;
            boolean isHovered = mouseX >= cellX && mouseX <= cellX + 20 && mouseY >= cellY && mouseY <= cellY + 20;
            GuiContext.bind(TEX_SLOT);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            BlocksPage.drawModalRectWithCustomSizedTexture((int)cellX, (int)cellY, (float)0.0f, (float)0.0f, (int)20, (int)20, (float)20.0f, (float)20.0f);
            int iconSize = 16;
            int cellOffset = 2;
            int iconX = cellX + 2;
            int iconY = cellY + 2;
            float wiggleX = 0.0f;
            float wiggleY = 0.0f;
            if (isHovered) {
                hovered = entry;
                float phase = t;
                wiggleX = (float)Math.sin(phase * 2.0f) * 0.4f;
                wiggleY = (float)Math.sin(phase * 3.0f) * 0.6f;
            }
            ItemStack stack = entry.icon;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)wiggleX, (float)wiggleY, (float)0.0f);
            GuiScreen.renderItem(stack, iconX, iconY);
            GlStateManager.popMatrix();
            if (!isHovered) continue;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            BlocksPage.drawRect((int)(cellX - 1), (int)(cellY - 1), (int)(cellX + 20 + 1), (int)cellY, (int)-2130706433);
            BlocksPage.drawRect((int)(cellX - 1), (int)cellY, (int)cellX, (int)(cellY + 20 + 1), (int)-2130706433);
            BlocksPage.drawRect((int)(cellX + 20), (int)cellY, (int)(cellX + 20 + 1), (int)(cellY + 20 + 1), (int)-2130706433);
            BlocksPage.drawRect((int)(cellX - 1), (int)(cellY + 20), (int)(cellX + 20 + 1), (int)(cellY + 20 + 1), (int)-2130706433);
            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
        }
        RenderHelper.disableStandardItemLighting();
        if (hovered != null) {
            ArrayList<String> tooltip = new ArrayList<String>();
            tooltip.add(this.distort(GuiContext.fmt((String)hovered.nameKey, (Object[])new Object[0])));
            String lore = this.distort(GuiContext.fmt((String)hovered.loreKey, (Object[])new Object[0]));
            if (!hovered.loreKey.equals(lore)) {
                tooltip.add(ChatFormatting.GRAY + lore);
            }
            this.drawHoveringText(tooltip, mouseX, mouseY);
            GlStateManager.disableLighting();
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}

