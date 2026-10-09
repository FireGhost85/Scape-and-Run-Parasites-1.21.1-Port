package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class GuiVectorMapReport
extends GuiScreen {
    private static final int RANGE = 2500;
    private final ItemStack stack;
    private boolean isJumbled;
    private int cx;
    private int cz;
    private int vx;
    private int vz;
    private int radius;
    private int day;
    private int index;
    private int total;
    private int legendX;
    private int legendY;
    private int legendW;
    private int legendH;
    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;
    private int plotX;
    private int plotY;
    private int plotSide;
    private int listX;
    private int listY;
    private int listW;
    private int listH;
    private int listScroll;
    private int selected;
    private float uiScale = 1.0f;
    private int screenPanelX;
    private int screenPanelY;
    private int screenPanelW;
    private int screenPanelH;
    private static final ResourceLocation TEX_ORIGIN = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/vector_origin.png");
    private static final ResourceLocation TEX_FAR = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/vector_far.png");
    private static final ResourceLocation TEX_PINPOINT = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/vector_pinpoint.png");
    private static final ResourceLocation TEX_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/vector_gui.png");
    private static final int BG_W = 360;
    private static final int BG_H = 300;
    private final List<VectorEntry> vectors = new ArrayList<VectorEntry>();

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    public GuiVectorMapReport(ItemStack stack) {
        this.stack = stack;
    }

    public void initGui() {
        super.initGui();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        CompoundTag tag = com.dhanantry.scapeandrunparasites.item.ReportData.read(this.stack);
        if (tag == null) {
            tag = new CompoundTag();
        }
        this.cx = tag.getInt("CenterX");
        this.cz = tag.getInt("CenterZ");
        this.vx = tag.getInt("VectorX");
        this.vz = tag.getInt("VectorZ");
        this.radius = Math.max(0, tag.getInt("Radius"));
        this.day = Math.max(0, tag.getInt("Day"));
        this.index = Math.max(1, tag.getInt("Index"));
        this.total = Math.max(1, tag.getInt("Total"));
        this.vectors.clear();
        if (tag.contains("Vectors", 9)) {
            ListTag list = tag.getList("Vectors", 10);
            for (int i = 0; i < list.size(); ++i) {
                CompoundTag c = list.getCompound(i);
                int lvx = c.getInt("VectorX");
                int lvz = c.getInt("VectorZ");
                int lr = Math.max(0, c.getInt("Radius"));
                int lday = Math.max(0, c.getInt("Day"));
                this.vectors.add(new VectorEntry(lvx, lvz, lr, lday));
            }
        }
        if (this.vectors.isEmpty()) {
            this.vectors.add(new VectorEntry(this.vx, this.vz, this.radius, this.day));
        }
        this.fillHealthFromWorldData();
        this.total = Math.max(1, this.vectors.size());
        this.selected = Mth.clamp((int)(this.index - 1), (int)0, (int)(this.total - 1));
        VectorEntry sel = this.vectors.get(this.selected);
        this.vx = sel.vx;
        this.vz = sel.vz;
        this.radius = sel.r;
        this.day = sel.day;
        int maxW = this.width - 24;
        int maxH = this.height - 24;
        float s = Math.min((float)maxW / 360.0f, (float)maxH / 300.0f);
        this.uiScale = Math.min(1.0f, s);
        this.uiScale = Math.max(0.25f, this.uiScale);
        this.screenPanelW = Math.round(360.0f * this.uiScale);
        this.screenPanelH = Math.round(300.0f * this.uiScale);
        this.screenPanelX = (this.width - this.screenPanelW) / 2;
        this.screenPanelY = (this.height - this.screenPanelH) / 2;
        if (this.screenPanelX < 0) {
            this.screenPanelX = 0;
        }
        if (this.screenPanelY < 0) {
            this.screenPanelY = 0;
        }
        this.panelX = 0;
        this.panelY = 0;
        this.panelW = 360;
        this.panelH = 300;
        int pad = 14;
        int top = this.panelY + 40;
        int bottom = this.panelY + this.panelH - 44;
        int availH = bottom - top;
        this.listX = this.panelX + 10;
        this.listW = 120;
        this.listW -= Math.max(1, this.listW / 16);
        int gutter = 40;
        int plotLeft = this.listX + this.listW + gutter;
        int plotRight = this.panelX + this.panelW - pad;
        int availW = plotRight - plotLeft;
        this.plotSide = Math.min(availW, availH);
        this.plotX = plotLeft;
        this.listY = this.plotY = top + (availH - this.plotSide) / 2;
        this.listH = this.plotSide;
        float legendScale = 0.72f;
        int lineStep = 9;
        int lines = 3;
        int unscaledTextH = lineStep * lines;
        int unscaledPadTop = 2;
        int unscaledPadBottom = 3;
        int unscaledBoxH = unscaledPadTop + unscaledTextH + unscaledPadBottom;
        this.legendH = (int)Math.ceil((float)unscaledBoxH * legendScale) + 2;
        int legendGap = 6;
        this.listH = Math.max(40, this.listH - (legendGap + this.legendH));
        this.legendX = this.listX;
        this.legendW = this.listW;
        this.legendY = this.listY + this.listH + legendGap;
        this.listScroll = 0;
        this.buttonList.clear();
        GuiButtonNoShadow done = new GuiButtonNoShadow(0, this.screenPanelX + this.screenPanelW - 4 - 80, this.screenPanelY + this.screenPanelH - 24, 80, 20, this.distort(GuiContext.fmt((String)"gui.done", (Object[])new Object[0])));
        done.enabled = true;
        this.buttonList.add(done);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.setScreen(null);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.setScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    private int toDesignX(int mouseX) {
        return Math.round((float)(mouseX - this.screenPanelX) / this.uiScale);
    }

    private int toDesignY(int mouseY) {
        return Math.round((float)(mouseY - this.screenPanelY) / this.uiScale);
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dwheel = Mouse.getEventDWheel();
        if (dwheel != 0) {
            int dy;
            int mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
            int my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
            int dx = this.toDesignX(mx);
            if (this.isInList(dx, dy = this.toDesignY(my))) {
                int rowH = 32;
                int contentH = this.vectors.size() * rowH;
                int maxScroll = Math.max(0, contentH - this.listH);
                this.listScroll = dwheel < 0 ? Math.min(maxScroll, this.listScroll + rowH) : Math.max(0, this.listScroll - rowH);
            }
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int rowH;
        int relY;
        int idx;
        int dx = this.toDesignX(mouseX);
        int dy = this.toDesignY(mouseY);
        if (mouseButton == 0 && this.isInList(dx, dy) && (idx = (relY = dy - this.listY + this.listScroll) / (rowH = 32)) >= 0 && idx < this.vectors.size()) {
            this.selected = idx;
            this.index = this.selected + 1;
            VectorEntry sel = this.vectors.get(this.selected);
            this.vx = sel.vx;
            this.vz = sel.vz;
            this.radius = sel.r;
            this.day = sel.day;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean isInList(int mouseX, int mouseY) {
        return mouseX >= this.listX && mouseX < this.listX + this.listW && mouseY >= this.listY && mouseY < this.listY + this.listH;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float vecPy;
        float vecPx;
        boolean inRange;
        float rawVecPy;
        float rawVecPx;
        int dz;
        int dx;
        VectorEntry ve;
        int i;
        boolean wasJumbled = this.isJumbled;
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        if (wasJumbled != this.isJumbled && !this.buttonList.isEmpty()) {
            ((GuiButton)this.buttonList.get((int)0)).displayString = this.distort(GuiContext.fmt((String)"gui.done", (Object[])new Object[0]));
        }
        GuiVectorMapReport.drawRect((int)0, (int)0, (int)this.width, (int)this.height, (int)0x66000000);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.screenPanelX, (float)this.screenPanelY, (float)0.0f);
        GlStateManager.scale((float)this.uiScale, (float)this.uiScale, (float)1.0f);
        this.drawPanelBackgroundDesign();
        String title = this.distort(GuiContext.fmt((String)"gui.srparasites.vector_map.title", (Object[])new Object[0]));
        String headerLine = this.distort(GuiContext.fmt((String)"gui.srparasites.vector_map.day", (Object[])new Object[]{this.day}) + "  " + GuiContext.fmt((String)"gui.srparasites.vector_map.page", (Object[])new Object[]{this.index, this.total}));
        this.drawCenteredNoShadow(title, this.panelX + this.panelW / 2, this.panelY + 8, -15658735);
        this.drawCenteredNoShadow(headerLine, this.panelX + this.panelW / 2, this.panelY + 20, -12303292);
        GuiVectorMapReport.drawRect((int)this.plotX, (int)this.plotY, (int)(this.plotX + this.plotSide), (int)(this.plotY + this.plotSide), (int)-526345);
        float pxPerBlock = (float)this.plotSide / 2.0f / 2500.0f;
        int centerPx = this.plotX + this.plotSide / 2;
        int centerPy = this.plotY + this.plotSide / 2;
        this.drawGridAndTicks(pxPerBlock);
        int red = -811901;
        this.beginPlotScissor();
        for (i = 0; i < this.vectors.size(); ++i) {
            ve = this.vectors.get(i);
            dx = ve.vx - this.cx;
            dz = ve.vz - this.cz;
            rawVecPx = (float)centerPx + (float)dx * pxPerBlock;
            rawVecPy = (float)centerPy + (float)dz * pxPerBlock;
            inRange = (long)dx * (long)dx + (long)dz * (long)dz <= 6250000L;
            vecPx = rawVecPx;
            vecPy = rawVecPy;
            if (!inRange) {
                float farOuterR = 4.5f;
                float pad = farOuterR + 1.0f;
                vecPx = Mth.clamp((float)rawVecPx, (float)((float)this.plotX + pad), (float)((float)(this.plotX + this.plotSide) - pad));
                vecPy = Mth.clamp((float)rawVecPy, (float)((float)this.plotY + pad), (float)((float)(this.plotY + this.plotSide) - pad));
            }
            if (inRange) {
                if (this.isJumbled) continue;
                float rPx = (float)ve.r * pxPerBlock;
                float ringR = Mth.clamp((float)rPx, (float)4.0f, (float)((float)this.plotSide / 2.0f - 2.0f));
                this.drawRing(vecPx, vecPy, ringR, 2.0f, red, 80);
                continue;
            }
            int x = (int)vecPx;
            int y = (int)vecPy;
            GuiVectorMapReport.drawRect((int)(x - 3), (int)(y - 3), (int)(x + 3), (int)(y + 3), (int)-13312);
        }
        this.endPlotScissor();
        for (i = 0; i < this.vectors.size(); ++i) {
            ve = this.vectors.get(i);
            dx = ve.vx - this.cx;
            dz = ve.vz - this.cz;
            rawVecPx = (float)centerPx + (float)dx * pxPerBlock;
            rawVecPy = (float)centerPy + (float)dz * pxPerBlock;
            inRange = (long)dx * (long)dx + (long)dz * (long)dz <= 6250000L;
            vecPx = rawVecPx;
            vecPy = rawVecPy;
            if (!inRange) {
                float farOuterR = 4.5f;
                float pad = farOuterR + 1.0f;
                vecPx = Mth.clamp((float)rawVecPx, (float)((float)this.plotX + pad), (float)((float)(this.plotX + this.plotSide) - pad));
                vecPy = Mth.clamp((float)rawVecPy, (float)((float)this.plotY + pad), (float)((float)(this.plotY + this.plotSide) - pad));
            }
            float ringR = 0.0f;
            if (inRange && !this.isJumbled) {
                float rPx = (float)ve.r * pxPerBlock;
                ringR = Mth.clamp((float)rPx, (float)4.0f, (float)((float)this.plotSide / 2.0f - 2.0f));
            }
            String n = String.valueOf(i + 1);
            int nw = this.fontRendererObj.getStringWidth(n);
            int nx = (int)(vecPx - (float)nw / 2.0f);
            int ny = (int)(inRange ? vecPy - ringR - 10.0f : vecPy - 12.0f);
            nx = Mth.clamp((int)nx, (int)(this.plotX + 2), (int)(this.plotX + this.plotSide - 2 - nw));
            ny = Mth.clamp((int)ny, (int)(this.plotY + 2), (int)(this.plotY + this.plotSide - 10));
            this.fontRendererObj.drawString(n, nx, ny, -15658735);
        }
        this.drawVectorInfoBlock();
        this.drawLegendText();
        this.drawOriginMarker(centerPx, centerPy);
        String rangeStr = this.distort(GuiContext.fmt((String)"gui.srparasites.vector_map.range", (Object[])new Object[]{5000, 5000}));
        int rsW = this.fontRendererObj.getStringWidth(rangeStr);
        this.fontRendererObj.drawString(rangeStr, this.panelX + this.panelW / 2 - rsW / 2, this.panelY + 30, -12303292);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCenteredNoShadow(String s, int x, int y, int color) {
        int w = this.fontRendererObj.getStringWidth(s);
        this.fontRendererObj.drawString(s, x - w / 2, y, color);
    }

    private void drawScaledStringNoShadow(String s, float x, float y, float scale, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)0.0f);
        GlStateManager.scale((float)scale, (float)scale, (float)1.0f);
        this.fontRendererObj.drawString(s, 0, 0, color);
        GlStateManager.popMatrix();
    }

    private void drawVectorInfoBlock() {
        GL11.glDisable((int)3089);
        int x0 = this.listX;
        int y0 = this.panelY + 40;
        int vy = 0;
        String l1 = this.distort(GuiContext.fmt((String)"gui.srparasites.vector_map.vectors", (Object[])new Object[0]));
        float s = 0.85f;
        this.drawScaledStringNoShadow(l1, x0, y0, s, -15658735);
        GuiVectorMapReport.drawRect((int)(this.listX - 1), (int)(this.listY - 1), (int)(this.listX + this.listW + 1), (int)(this.listY + this.listH + 1), (int)0x33000000);
        GuiVectorMapReport.drawRect((int)this.listX, (int)this.listY, (int)(this.listX + this.listW), (int)(this.listY + this.listH), (int)0x44FFFFFF);
        GlStateManager.enableTexture2D();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int rowH = 32;
        int y = this.listY - this.listScroll;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.enableTexture2D();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        for (int i = 0; i < this.vectors.size(); ++i) {
            VectorEntry ve = this.vectors.get(i);
            int ry = y + i * rowH;
            if (ry + rowH < this.listY || ry > this.listY + this.listH) continue;
            int bg = i == this.selected ? 0x22000000 : 0xA000000;
            GuiVectorMapReport.drawRect((int)this.listX, (int)ry, (int)(this.listX + this.listW), (int)(ry + rowH), (int)bg);
            String aPlain = GuiContext.fmt((String)"gui.srparasites.vector_map.entry.xyz", (Object[])new Object[]{i + 1, ve.vx, vy, ve.vz});
            String bPlain = GuiContext.fmt((String)"gui.srparasites.vector_map.entry.size", (Object[])new Object[]{ve.r});
            String cPlain = GuiContext.fmt((String)"gui.srparasites.vector_map.entry.hp", (Object[])new Object[]{ve.hp >= 0 ? Integer.valueOf(ve.hp) : "?"});
            int maxW = (int)((float)(this.listW - 6) / s);
            aPlain = this.trimToWidth(aPlain, maxW);
            bPlain = this.trimToWidth(bPlain, maxW);
            cPlain = this.trimToWidth(cPlain, maxW);
            String a = this.distort(aPlain);
            String b = this.distort(bPlain);
            String c = this.distort(cPlain);
            GlStateManager.enableTexture2D();
            this.drawScaledStringNoShadow(a, this.listX + 3, ry + 3, s, -15658735);
            this.drawScaledStringNoShadow(b, this.listX + 3, ry + 13, s, -14540254);
            this.drawScaledStringNoShadow(c, this.listX + 3, ry + 23, s, -14540254);
        }
        GlStateManager.enableDepth();
    }

    private String trimToWidth(String s, int maxW) {
        if (this.fontRendererObj.getStringWidth(s) <= maxW) {
            return s;
        }
        String ell = "...";
        int ew = this.fontRendererObj.getStringWidth(ell);
        String t = s;
        while (t.length() > 0 && this.fontRendererObj.getStringWidth(t) + ew > maxW) {
            t = t.substring(0, t.length() - 1);
        }
        return t + ell;
    }

    private void drawLegendText() {
        float s = 0.72f;
        String dims = "5000 x 5000";
        String t1 = GuiContext.fmt((String)"gui.srparasites.vector_map.legend.line1", (Object[])new Object[0]);
        String t2 = GuiContext.fmt((String)"gui.srparasites.vector_map.legend.line2", (Object[])new Object[]{dims});
        String t3 = GuiContext.fmt((String)"gui.srparasites.vector_map.legend.line3", (Object[])new Object[0]);
        int wrapW = (int)((float)(this.legendW - 6) / s);
        ArrayList<String> lines = new ArrayList<String>();
        for (String line : this.fontRendererObj.listFormattedStringToWidth(t1, wrapW)) {
            lines.add(this.distort(line));
        }
        for (String line : this.fontRendererObj.listFormattedStringToWidth(t2, wrapW)) {
            lines.add(this.distort(line));
        }
        for (String line : this.fontRendererObj.listFormattedStringToWidth(t3, wrapW)) {
            lines.add(this.distort(line));
        }
        int lineStep = 9;
        int padTop = 2;
        int padBottom = 3;
        int fontH = this.fontRendererObj.FONT_HEIGHT;
        int y = this.legendY;
        int doneTop = this.panelY + this.panelH - 24;
        int contentH = padTop + lines.size() * lineStep + fontH + padBottom;
        int dynLegendH = (int)Math.ceil((float)contentH * s) + 2;
        int maxY = doneTop - dynLegendH - 2;
        if (y > maxY) {
            y = maxY;
        }
        GuiVectorMapReport.drawRect((int)(this.legendX - 1), (int)(y - 1), (int)(this.legendX + this.legendW + 1), (int)(y + dynLegendH + 1), (int)0x33000000);
        GuiVectorMapReport.drawRect((int)this.legendX, (int)y, (int)(this.legendX + this.legendW), (int)(y + dynLegendH), (int)0x44FFFFFF);
        int y0 = y + padTop;
        for (int i = 0; i < lines.size(); ++i) {
            this.drawScaledStringNoShadow((String)lines.get(i), this.legendX + 3, y0 + i * lineStep, s, -14540254);
        }
    }

    private void drawGridAndTicks(float pxPerBlock) {
        int centerPx = this.plotX + this.plotSide / 2;
        int centerPy = this.plotY + this.plotSide / 2;
        int gridColor = 0x22000000;
        int axisColor = -2013265920;
        int labelColor = -15066598;
        int step = 500;
        if (this.plotSide >= 280) {
            step = 1000;
        }
        GuiVectorMapReport.drawRect((int)centerPx, (int)this.plotY, (int)(centerPx + 1), (int)(this.plotY + this.plotSide), (int)axisColor);
        GuiVectorMapReport.drawRect((int)this.plotX, (int)centerPy, (int)(this.plotX + this.plotSide), (int)(centerPy + 1), (int)axisColor);
        int xLabelY = this.plotY + this.plotSide + 4;
        float labelScale = 0.75f;
        for (int v = -2500; v <= 2500; v += step) {
            int gx = centerPx + Math.round((float)v * pxPerBlock);
            int gy = centerPy + Math.round((float)v * pxPerBlock);
            if (v != 0) {
                GuiVectorMapReport.drawRect((int)gx, (int)this.plotY, (int)(gx + 1), (int)(this.plotY + this.plotSide), (int)gridColor);
                GuiVectorMapReport.drawRect((int)this.plotX, (int)gy, (int)(this.plotX + this.plotSide), (int)(gy + 1), (int)gridColor);
            }
            boolean alt = (v / step & 1) != 0;
            String sxw = String.valueOf(this.cx + v);
            int sx = gx - (int)((float)this.fontRendererObj.getStringWidth(sxw) * labelScale) / 2;
            int sy = xLabelY + (alt ? 9 : 0);
            this.drawScaledStringNoShadow(sxw, sx, sy, labelScale, labelColor);
            String syw = String.valueOf(this.cz + v);
            int w = (int)((float)this.fontRendererObj.getStringWidth(syw) * labelScale);
            int gapFromPlot = 4;
            int xs = this.plotX - gapFromPlot - w;
            int ys = gy - 4 + (alt ? 4 : 0);
            this.drawScaledStringNoShadow(syw, xs, ys, labelScale, labelColor);
        }
    }

    private void drawSpriteCentered(ResourceLocation tex, float cx, float cy, int w, int h) {
        GuiContext.bind(tex);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int x = Math.round(cx - (float)w / 2.0f);
        int y = Math.round(cy - (float)h / 2.0f);
        GuiVectorMapReport.drawModalRectWithCustomSizedTexture((int)x, (int)y, (float)0.0f, (float)0.0f, (int)w, (int)h, (float)w, (float)h);
        GlStateManager.disableBlend();
    }

    private void drawFilledCircle(float cx, float cy, float r, int color, int segments) {
        segments = Math.max(12, segments);
        float a = (float)(color >>> 24 & 0xFF) / 255.0f;
        float rr = (float)(color >>> 16 & 0xFF) / 255.0f;
        float gg = (float)(color >>> 8 & 0xFF) / 255.0f;
        float bb = (float)(color & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder buf = tess.getWorldRenderer();
        buf.begin(6, Tessellator.VertexFormat.POSITION_COLOR);
        buf.pos((double)cx, (double)cy, 0.0).color(rr, gg, bb, a).endVertex();
        for (int i = 0; i <= segments; ++i) {
            double ang = Math.PI * 2 * ((double)i / (double)segments);
            double x = (double)cx + Math.cos(ang) * (double)r;
            double y = (double)cy + Math.sin(ang) * (double)r;
            buf.pos(x, y, 0.0).color(rr, gg, bb, a).endVertex();
        }
        tess.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.enableDepth();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void drawRing(float cx, float cy, float rOuter, float thickness, int color, int segments) {
        segments = Math.max(16, segments);
        thickness = Math.max(1.0f, thickness);
        float rInner = Math.max(0.0f, rOuter - thickness);
        float a = (float)(color >>> 24 & 0xFF) / 255.0f;
        float rr = (float)(color >>> 16 & 0xFF) / 255.0f;
        float gg = (float)(color >>> 8 & 0xFF) / 255.0f;
        float bb = (float)(color & 0xFF) / 255.0f;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GlStateManager.disableAlpha();
        Tessellator ringTess = Tessellator.getInstance();
        Tessellator.BufferBuilder ringBuf = ringTess.getWorldRenderer();
        ringBuf.begin(5, Tessellator.VertexFormat.POSITION_COLOR);
        for (int i = 0; i <= segments; ++i) {
            double ang = Math.PI * 2 * ((double)i / (double)segments);
            float ca = (float)Math.cos(ang);
            float sa = (float)Math.sin(ang);
            ringBuf.pos((double)(cx + ca * rOuter), (double)(cy + sa * rOuter), 0.0).color(rr, gg, bb, a).endVertex();
            ringBuf.pos((double)(cx + ca * rInner), (double)(cy + sa * rInner), 0.0).color(rr, gg, bb, a).endVertex();
        }
        ringTess.draw();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    private void drawOriginMarker(int centerPx, int centerPy) {
        this.drawSpriteCentered(TEX_ORIGIN, (float)centerPx + 0.5f, (float)centerPy + 0.5f, 9, 9);
    }

    private void fillHealthFromWorldData() {
        try {
            net.minecraft.client.server.IntegratedServer srv = this.mc.getSingleplayerServer();
            if (srv == null) {
                return;
            }
            ServerLevel world = srv.getLevel(this.mc.player.level().dimension());
            if (world == null) {
                return;
            }
            SRPWorldData data = SRPWorldData.get((Level)world);
            ArrayList<Integer> xs = data.getorigins("x");
            ArrayList<Integer> zs = data.getorigins("z");
            ArrayList<Integer> hs = data.getorigins("h");
            block2: for (VectorEntry ve : this.vectors) {
                for (int i = 0; i < xs.size() && i < zs.size() && i < hs.size(); ++i) {
                    if (xs.get(i) != ve.vx || zs.get(i) != ve.vz) continue;
                    ve.hp = hs.get(i);
                    continue block2;
                }
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private void drawPanelBackgroundDesign() {
        GuiContext.bind(TEX_BG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)771);
        GuiVectorMapReport.drawScaledCustomSizeModalRect((int)this.panelX, (int)this.panelY, (float)0.0f, (float)0.0f, (int)360, (int)300, (int)360, (int)300, (float)360.0f, (float)300.0f);
        GlStateManager.disableBlend();
    }

    private void beginPlotScissor() {
        int fbW = this.mc.getMainRenderTarget().width;
        int fbH = this.mc.getMainRenderTarget().height;
        float xRatio = (float)fbW / (float)this.width;
        float yRatio = (float)fbH / (float)this.height;
        float guiX0 = (float)this.screenPanelX + (float)this.plotX * this.uiScale;
        float guiY0 = (float)this.screenPanelY + (float)this.plotY * this.uiScale;
        float guiW = (float)this.plotSide * this.uiScale;
        float guiH = (float)this.plotSide * this.uiScale;
        int scX = (int)Math.floor(guiX0 * xRatio);
        int scW = (int)Math.ceil(guiW * xRatio);
        int scYTop = (int)Math.floor(guiY0 * yRatio);
        int scH = (int)Math.ceil(guiH * yRatio);
        int scY = fbH - (scYTop + scH);
        int padX = (int)Math.ceil(1.0f * xRatio);
        int padY = (int)Math.ceil(1.0f * yRatio);
        scY -= padY;
        scW += padX * 2;
        scH += padY * 2;
        if ((scX -= padX) < 0) {
            scW += scX;
            scX = 0;
        }
        if (scY < 0) {
            scH += scY;
            scY = 0;
        }
        if (scX + scW > fbW) {
            scW = fbW - scX;
        }
        if (scY + scH > fbH) {
            scH = fbH - scY;
        }
        if (scW > 0 && scH > 0) {
            GL11.glEnable((int)3089);
            GL11.glScissor((int)scX, (int)scY, (int)scW, (int)scH);
        } else {
            GL11.glDisable((int)3089);
        }
    }

    private void endPlotScissor() {
        GL11.glDisable((int)3089);
    }

    private static class VectorEntry {
        final int vx;
        final int vz;
        final int r;
        final int day;
        int hp = -1;

        VectorEntry(int vx, int vz, int r, int day) {
            this.vx = vx;
            this.vz = vz;
            this.r = r;
            this.day = day;
        }
    }

    private static class GuiButtonNoShadow
    extends GuiButton {
        public GuiButtonNoShadow(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            GuiContext.g.blitSprite(!this.enabled ? net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_disabled") : (this.hovered ? net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button_highlighted") : net.minecraft.resources.ResourceLocation.withDefaultNamespace("widget/button")), this.x, this.y, this.width, this.height);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
            int textColor = -15066598;
            if (!this.enabled) {
                textColor = -8947849;
            }
            int sw = GuiContext.FONT.getStringWidth(this.displayString);
            GuiContext.FONT.drawString(this.displayString, this.x + this.width / 2 - sw / 2, this.y + (this.height - 8) / 2, textColor);
        }
    }
}

