package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GearBackground;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.bestiary.systems.SRPSystemsRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.systems.SystemEntry;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class SystemsPage
extends GuiScreen {
    private static final int LIST_X = 14;
    private static final int LIST_Y = 32;
    private static final int LIST_W = 130;
    private static final int LIST_H = 180;
    private static final int DETAIL_X = 156;
    private static final int DETAIL_Y = 32;
    private static final int DETAIL_W = 220;
    private static final int DETAIL_H = 180;
    private static final int ROW_H = 14;
    private static final int BTN_H = 14;
    private static final int DETAIL_PAD = 8;
    private static final int DETAIL_SCROLLBAR_W = 8;
    private static final int DETAIL_SCROLLBAR_GAP = 4;
    private static final int DETAIL_LINE_H = 10;
    private static final int DETAIL_MIN_THUMB_H = 12;
    private static final int LIST_SCROLLBAR_W = 6;
    private static final int LIST_SCROLLBAR_GAP = 6;
    private static final int LIST_SCROLLBAR_X = 2;
    private static final int LIST_SCROLLBAR_Y = 32;
    private static final int LIST_SCROLLBAR_H = 180;
    private static final int LIST_MIN_THUMB_H = 18;
    private final Player player;
    private final GuiScreen parent;
    private final List<SystemEntry> entries = new ArrayList<SystemEntry>();
    private GearBackground gearBackground;
    private int selectedIndex = -1;
    private boolean isJumbled;
    private int listScrollPx = 0;
    private int detailScrollPx = 0;
    private boolean draggingListScrollbar = false;
    private int listDragGrabOffset = 0;
    private boolean draggingDetailScrollbar = false;
    private int detailDragGrabOffset = 0;
    private static final ResourceLocation JUMBLED_ICON = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/question_mark_small.png");

    public SystemsPage(Player player, GuiScreen parent) {
        this.player = player;
        this.parent = parent;
    }

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    public void initGui() {
        super.initGui();
        if (this.gearBackground == null) {
            this.gearBackground = new GearBackground(this.mc);
        }
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, 10, 10, 60, 20, this.distort("< " + GuiContext.fmt((String)"bestiary.systems.home", (Object[])new Object[0]))));
        this.rebuildEntries();
        this.rebuildListButtons();
    }

    private void rebuildEntries() {
        this.entries.clear();
        this.entries.addAll(SRPSystemsRegistry.all());
        if (this.selectedIndex >= this.entries.size()) {
            int n = this.selectedIndex = this.entries.isEmpty() ? -1 : 0;
        }
        if (this.selectedIndex < 0 && !this.entries.isEmpty()) {
            this.selectedIndex = 0;
        }
        this.detailScrollPx = 0;
        this.draggingDetailScrollbar = false;
    }

    private int visibleRows() {
        return 12;
    }

    private void rebuildListButtons() {
        int idx;
        this.buttonList.removeIf(b -> b.id >= 200 && b.id < 2000);
        int firstRow = this.listScrollPx / 14;
        int maxRows = this.visibleRows();
        for (int i = 0; i < maxRows && (idx = firstRow + i) < this.entries.size(); ++i) {
            int y = 32 + i * 14;
            this.buttonList.add(new GuiButton(200 + idx, 16, y, 126, 14, ""));
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        int idx;
        if (button.id == 1) {
            this.mc.setScreen(this.parent);
            return;
        }
        if (button.id >= 200 && button.id < 2000 && (idx = button.id - 200) >= 0 && idx < this.entries.size()) {
            this.selectedIndex = idx;
            this.detailScrollPx = 0;
            this.draggingDetailScrollbar = false;
        }
    }

    private boolean isMouseOver(int x1, int y1, int x2, int y2, int mx, int my) {
        return mx >= x1 && mx < x2 && my >= y1 && my < y2;
    }

    public void handleMouseInput() throws IOException {
        int dir;
        super.handleMouseInput();
        int dwheel = Mouse.getEventDWheel();
        if (dwheel == 0) {
            return;
        }
        int mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
        int my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
        boolean overList = this.isMouseOver(14, 32, 144, 212, mx, my);
        boolean overDetailText = this.isMouseOver(this.getDetailTextX(), this.getDetailTextY(), this.getDetailTextX() + this.getDetailTextW(), this.getDetailTextY() + this.getDetailTextH(), mx, my);
        boolean overDetailScrollbar = this.isMouseOver(this.getDetailScrollbarX(), this.getDetailScrollbarY(), this.getDetailScrollbarX() + 8, this.getDetailScrollbarY() + this.getDetailScrollbarH(), mx, my);
        int n = dir = dwheel > 0 ? -1 : 1;
        if (overDetailText || overDetailScrollbar) {
            this.scrollDetail(dir);
        } else if (overList) {
            this.scrollList(dir);
        } else {
            this.scrollList(dir);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 0 && this.hasScrollableList() && this.isMouseOverListScrollbar(mouseX, mouseY)) {
            int thumbY = this.getListScrollbarThumbY();
            int thumbH = this.getListScrollbarThumbH();
            if (mouseY >= thumbY && mouseY < thumbY + thumbH) {
                this.draggingListScrollbar = true;
                this.listDragGrabOffset = mouseY - thumbY;
                return;
            }
            int newThumbTop = mouseY - thumbH / 2;
            this.setListScrollFromThumbTop(newThumbTop);
            this.draggingListScrollbar = true;
            this.listDragGrabOffset = thumbH / 2;
            return;
        }
        if (mouseButton == 0 && this.hasScrollableDetail() && this.isMouseOver(this.getDetailScrollbarX(), this.getDetailScrollbarY(), this.getDetailScrollbarX() + 8, this.getDetailScrollbarY() + this.getDetailScrollbarH(), mouseX, mouseY)) {
            int thumbY = this.getDetailScrollbarThumbY();
            int thumbH = this.getDetailScrollbarThumbH();
            if (mouseY >= thumbY && mouseY < thumbY + thumbH) {
                this.draggingDetailScrollbar = true;
                this.detailDragGrabOffset = mouseY - thumbY;
                return;
            }
            int newThumbTop = mouseY - thumbH / 2;
            this.setDetailScrollFromThumbTop(newThumbTop);
            this.draggingDetailScrollbar = true;
            this.detailDragGrabOffset = thumbH / 2;
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.draggingListScrollbar = false;
        this.draggingDetailScrollbar = false;
        super.mouseReleased(mouseX, mouseY, state);
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        int newThumbTop;
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.draggingDetailScrollbar && this.hasScrollableDetail()) {
            newThumbTop = mouseY - this.detailDragGrabOffset;
            this.setDetailScrollFromThumbTop(newThumbTop);
        }
        if (this.draggingListScrollbar && this.hasScrollableList()) {
            newThumbTop = mouseY - this.listDragGrabOffset;
            this.setListScrollFromThumbTop(newThumbTop);
        }
    }

    private void scrollList(int dir) {
        int maxScroll = Math.max(0, this.entries.size() * 14 - 180);
        this.listScrollPx = Math.max(0, Math.min(maxScroll, this.listScrollPx + dir * 14));
        this.rebuildListButtons();
    }

    private void scrollDetail(int dir) {
        int maxScroll = this.getDetailMaxScrollPx();
        this.detailScrollPx = Math.max(0, Math.min(maxScroll, this.detailScrollPx + dir * 10));
    }

    private int getDetailTextX() {
        return 164;
    }

    private int getDetailTextY() {
        return 68;
    }

    private int getDetailTextW() {
        return 192;
    }

    private int getDetailTextH() {
        return 136;
    }

    private int getDetailScrollbarX() {
        return 360;
    }

    private int getDetailScrollbarY() {
        return this.getDetailTextY();
    }

    private int getDetailScrollbarH() {
        return this.getDetailTextH();
    }

    private List<String> getDetailWrappedLines() {
        ArrayList<String> lines = new ArrayList<String>();
        if (this.selectedIndex < 0 || this.selectedIndex >= this.entries.size()) {
            return lines;
        }
        SystemEntry e = this.entries.get(this.selectedIndex);
        if (GuiContext.hasKey((String)e.descKey())) {
            String desc = this.distort(GuiContext.fmt((String)e.descKey(), (Object[])new Object[0]));
            lines.addAll(this.fontRendererObj.listFormattedStringToWidth(desc, this.getDetailTextW()));
        } else {
            lines.add(this.distort(GuiContext.fmt((String)"bestiary.systems.missing_desc", (Object[])new Object[0])));
        }
        return lines;
    }

    private int getDetailContentHeightPx() {
        return this.getDetailWrappedLines().size() * 10;
    }

    private int getDetailViewportHeightPx() {
        return this.getDetailTextH();
    }

    private int getDetailMaxScrollPx() {
        return Math.max(0, this.getDetailContentHeightPx() - this.getDetailViewportHeightPx());
    }

    private boolean hasScrollableDetail() {
        return this.getDetailMaxScrollPx() > 0;
    }

    private int getDetailScrollbarThumbH() {
        int contentH = this.getDetailContentHeightPx();
        int viewH = this.getDetailViewportHeightPx();
        int trackH = this.getDetailScrollbarH();
        if (contentH <= 0 || contentH <= viewH) {
            return trackH;
        }
        int thumbH = (int)((float)viewH / (float)contentH * (float)trackH);
        return Mth.clamp((int)thumbH, (int)12, (int)trackH);
    }

    private int getDetailScrollbarThumbY() {
        int trackY = this.getDetailScrollbarY();
        int trackH = this.getDetailScrollbarH();
        int thumbH = this.getDetailScrollbarThumbH();
        int maxScroll = this.getDetailMaxScrollPx();
        if (maxScroll <= 0) {
            return trackY;
        }
        int movable = trackH - thumbH;
        int thumbOffset = (int)((float)this.detailScrollPx / (float)maxScroll * (float)movable);
        return trackY + thumbOffset;
    }

    private void setDetailScrollFromThumbTop(int thumbTop) {
        int trackY = this.getDetailScrollbarY();
        int trackH = this.getDetailScrollbarH();
        int thumbH = this.getDetailScrollbarThumbH();
        int movable = trackH - thumbH;
        int maxScroll = this.getDetailMaxScrollPx();
        if (movable <= 0 || maxScroll <= 0) {
            this.detailScrollPx = 0;
            return;
        }
        int clampedThumbTop = Mth.clamp((int)thumbTop, (int)trackY, (int)(trackY + movable));
        float pct = (float)(clampedThumbTop - trackY) / (float)movable;
        this.detailScrollPx = Mth.clamp((int)((int)(pct * (float)maxScroll)), (int)0, (int)maxScroll);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int idx;
        int firstRow;
        int rel;
        if (this.gearBackground != null) {
            this.gearBackground.render(partialTicks, this.isJumbled);
        } else {
            this.drawDefaultBackground();
        }
        String title = this.distort(GuiContext.fmt((String)"bestiary.systems.title", (Object[])new Object[0]));
        this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 12, 0xFFFFFF);
        SystemsPage.drawRect((int)12, (int)30, (int)146, (int)214, (int)-1442840576);
        SystemsPage.drawRect((int)14, (int)32, (int)144, (int)212, (int)0x66000000);
        SystemsPage.drawRect((int)1, (int)31, (int)9, (int)213, (int)-1442840576);
        SystemsPage.drawRect((int)2, (int)32, (int)8, (int)212, (int)0x44000000);
        if (this.hasScrollableList()) {
            int thumbY = this.getListScrollbarThumbY();
            int thumbH = this.getListScrollbarThumbH();
            SystemsPage.drawRect((int)2, (int)thumbY, (int)8, (int)(thumbY + thumbH), (int)(this.draggingListScrollbar ? -861230422 : -1716868438));
            SystemsPage.drawRect((int)3, (int)(thumbY + 1), (int)7, (int)(thumbY + thumbH - 1), (int)-857874979);
        }
        SystemsPage.drawRect((int)154, (int)30, (int)378, (int)214, (int)-1442840576);
        SystemsPage.drawRect((int)156, (int)32, (int)376, (int)212, (int)0x66000000);
        if (this.entries.isEmpty()) {
            this.drawString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.systems.none", (Object[])new Object[0])), 20, 38, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }
        if (this.selectedIndex >= 0 && this.selectedIndex < this.entries.size() && (rel = this.selectedIndex - (firstRow = this.listScrollPx / 14)) >= 0 && rel < this.visibleRows()) {
            int y0 = 32 + rel * 14;
            SystemsPage.drawRect((int)14, (int)y0, (int)144, (int)(y0 + 14), (int)0x33FFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.enableScissor(16, 34, 126, 176);
        firstRow = this.listScrollPx / 14;
        int maxRows = this.visibleRows();
        for (int i = 0; i < maxRows && (idx = firstRow + i) < this.entries.size(); ++i) {
            SystemEntry e = this.entries.get(idx);
            String label = this.distort(GuiContext.hasKey((String)e.nameKey()) ? GuiContext.fmt((String)e.nameKey(), (Object[])new Object[0]) : e.id);
            int centerX = 79;
            int yText = 32 + i * 14 + 3;
            int maxTextWidth = 118;
            this.drawScaledStringCentered(label, centerX, yText, maxTextWidth, 0xFFFFFF);
        }
        this.disableScissor();
        if (this.selectedIndex >= 0 && this.selectedIndex < this.entries.size()) {
            SystemEntry e = this.entries.get(this.selectedIndex);
            int x = 164;
            int y = 40;
            String name = this.distort(GuiContext.hasKey((String)e.nameKey()) ? GuiContext.fmt((String)e.nameKey(), (Object[])new Object[0]) : e.id);
            this.drawString(this.fontRendererObj, name, x, y, 0xFFFFFF);
            this.drawString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.systems.id", (Object[])new Object[0]) + ": " + e.id), x, y += 14, 0xFFFFFF);
            SystemsPage.drawRect((int)(this.getDetailTextX() - 2), (int)(this.getDetailTextY() - 2), (int)(this.getDetailTextX() + this.getDetailTextW() + 2), (int)(this.getDetailTextY() + this.getDetailTextH() + 2), (int)0x33000000);
            List<String> lines = this.getDetailWrappedLines();
            this.enableScissor(this.getDetailTextX(), this.getDetailTextY(), this.getDetailTextW(), this.getDetailTextH());
            int yDraw = this.getDetailTextY() - this.detailScrollPx;
            for (String line : lines) {
                if (yDraw > this.getDetailTextY() + this.getDetailTextH()) break;
                if (yDraw + 10 >= this.getDetailTextY()) {
                    this.drawString(this.fontRendererObj, line, this.getDetailTextX(), yDraw, 0xFFFFFF);
                }
                yDraw += 10;
            }
            this.disableScissor();
            int trackX = this.getDetailScrollbarX();
            int trackY = this.getDetailScrollbarY();
            int trackH = this.getDetailScrollbarH();
            SystemsPage.drawRect((int)trackX, (int)trackY, (int)(trackX + 8), (int)(trackY + trackH), (int)0x22000000);
            int thumbY = this.getDetailScrollbarThumbY();
            int thumbH = this.getDetailScrollbarThumbH();
            int thumbColor = this.hasScrollableDetail() ? (this.draggingDetailScrollbar ? -861230422 : -863467384) : 0x66444444;
            SystemsPage.drawRect((int)trackX, (int)thumbY, (int)(trackX + 8), (int)(thumbY + thumbH), (int)thumbColor);
            this.drawSystemIconTopRight(e, 370, 38, 24);
        }
    }

    private void drawSystemIconTopRight(SystemEntry e, int rightX, int topY, int sizePx) {
        ResourceLocation tex;
        if (e == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        ResourceLocation resourceLocation = tex = this.isJumbled ? JUMBLED_ICON : e.icon;
        if (tex == null) {
            return;
        }
        float timeSec = (float)GuiContext.systemTime() / 1000.0f;
        float maxTiltDeg = 6.0f;
        float speedHz = 0.2f;
        float angle = Mth.sin((float)(timeSec * ((float)Math.PI * 2) * speedHz)) * maxTiltDeg;
        int x = rightX - sizePx;
        int y = topY;
        GuiContext.bind(tex);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)((float)x + (float)sizePx / 2.0f), (float)((float)y + (float)sizePx / 2.0f), (float)0.0f);
        GlStateManager.rotate((float)angle, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((float)((float)(-sizePx) / 2.0f), (float)((float)(-sizePx) / 2.0f), (float)0.0f);
        SystemsPage.drawModalRectWithCustomSizedTexture((int)0, (int)0, (float)0.0f, (float)0.0f, (int)sizePx, (int)sizePx, (float)sizePx, (float)sizePx);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    private void drawScaledStringCentered(String text, int centerX, int y, int maxWidth, int color) {
        int textWidth = this.fontRendererObj.getStringWidth(text);
        float scale = 1.0f;
        if (textWidth > maxWidth) {
            scale = (float)maxWidth / (float)textWidth;
            scale = Math.max(scale, 0.65f);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)centerX, (float)y, (float)0.0f);
        GlStateManager.scale((float)scale, (float)scale, (float)1.0f);
        this.fontRendererObj.drawString(text, (float)(-this.fontRendererObj.getStringWidth(text) / 2), 0.0f, color, false);
        GlStateManager.popMatrix();
    }

    private void enableScissor(int x, int y, int w, int h) {
        Minecraft mc = Minecraft.getInstance();
        ScaledResolution sr = new ScaledResolution(mc);
        int factor = sr.getScaleFactor();
        int sx = x * factor;
        int sy = (this.height - (y + h)) * factor;
        int sw = w * factor;
        int sh = h * factor;
        GL11.glEnable((int)3089);
        GL11.glScissor((int)sx, (int)sy, (int)sw, (int)sh);
    }

    private void disableScissor() {
        GL11.glDisable((int)3089);
    }

    private int getListMaxScrollPx() {
        return Math.max(0, this.entries.size() * 14 - 180);
    }

    private boolean hasScrollableList() {
        return this.getListMaxScrollPx() > 0;
    }

    private int getListScrollbarThumbH() {
        int contentH = Math.max(1, this.entries.size() * 14);
        int viewH = 180;
        int trackH = 180;
        if (contentH <= viewH) {
            return trackH;
        }
        int thumbH = (int)((float)viewH / (float)contentH * (float)trackH);
        return Mth.clamp((int)thumbH, (int)18, (int)trackH);
    }

    private int getListScrollbarThumbY() {
        int maxScroll = this.getListMaxScrollPx();
        if (maxScroll <= 0) {
            return 32;
        }
        int thumbH = this.getListScrollbarThumbH();
        int movable = 180 - thumbH;
        if (movable <= 0) {
            return 32;
        }
        int thumbOffset = (int)((float)this.listScrollPx / (float)maxScroll * (float)movable);
        return 32 + thumbOffset;
    }

    private boolean isMouseOverListScrollbar(int mouseX, int mouseY) {
        return mouseX >= 2 && mouseX < 8 && mouseY >= 32 && mouseY < 212;
    }

    private void setListScrollFromThumbTop(int thumbTop) {
        int maxScroll = this.getListMaxScrollPx();
        int thumbH = this.getListScrollbarThumbH();
        int movable = 180 - thumbH;
        if (maxScroll <= 0 || movable <= 0) {
            this.listScrollPx = 0;
            return;
        }
        int clampedThumbTop = Mth.clamp((int)thumbTop, (int)32, (int)(32 + movable));
        float pct = (float)(clampedThumbTop - 32) / (float)movable;
        this.listScrollPx = Mth.clamp((int)((int)(pct * (float)maxScroll)), (int)0, (int)maxScroll);
        this.rebuildListButtons();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}

