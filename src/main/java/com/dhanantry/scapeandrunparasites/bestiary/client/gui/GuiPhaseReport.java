package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GuiPhaseReport
extends GuiScreen {
    private final ItemStack stack;
    private static final ResourceLocation TEX_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_gui.png");
    private static final ResourceLocation TEX_FG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_fg_gui.png");
    private static final int BG_W = 360;
    private static final int BG_H = 300;
    private static final int FG_W = 332;
    private static final int FG_H = 212;
    private static final int FG_BORDER_THICKNESS = 1;
    private boolean isJumbled;
    private float uiScale = 1.0f;
    private int screenPanelX;
    private int screenPanelY;
    private int screenPanelW;
    private int screenPanelH;
    private int panelX;
    private int panelY;
    private int panelW;
    private int panelH;
    private int fgX;
    private int fgY;
    private int fgW;
    private int fgH;
    private int textX;
    private int textY;
    private int textW;
    private int textH;
    private int chartX;
    private int chartY;
    private int chartW;
    private int chartH;
    private int bottomLineY;
    private int statusY;
    private int textScroll = 0;
    private final List<String> lines = new ArrayList<String>();
    private int phase;
    private int totalPoints;
    private int nextPoints;
    private String progress;
    private int cooldown;
    private boolean canGain;
    private boolean canLoss;
    private String dimension = "";
    private int mobcap;
    private int generation;
    private int genTicks;
    private int parasiteCount;
    private int cothCount;
    private int totalMobCount;

    public GuiPhaseReport(ItemStack stack) {
        this.stack = stack;
    }

    public void initGui() {
        super.initGui();
        this.readNBT();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.buildLines();
        int maxW = this.width - 24;
        int maxH = this.height - 24;
        float s = Math.min((float)maxW / 360.0f, (float)maxH / 300.0f);
        this.uiScale = Math.min(1.0f, s);
        this.uiScale = Math.max(0.25f, this.uiScale);
        this.screenPanelW = Math.round(360.0f * this.uiScale);
        this.screenPanelH = Math.round(300.0f * this.uiScale);
        this.screenPanelX = (this.width - this.screenPanelW) / 2;
        this.screenPanelY = (this.height - this.screenPanelH) / 2;
        this.panelX = 0;
        this.panelY = 0;
        this.panelW = 360;
        this.panelH = 300;
        int pad = 14;
        int top = this.panelY + 44;
        int bottom = this.panelY + this.panelH - 44;
        this.fgX = this.panelX + pad;
        this.fgY = top;
        this.fgW = this.panelW - pad * 2;
        this.fgH = bottom - top;
        int inset = 3;
        int contentX = this.fgX + inset;
        int contentY = this.fgY + inset;
        int contentW = this.fgW - inset * 2;
        int contentH = this.fgH - inset * 2;
        int gap = 10;
        this.textX = contentX + 8;
        this.textY = contentY + 8;
        this.textH = contentH - 42;
        this.textW = Math.min(185, contentW - 110);
        this.chartW = contentW - this.textW - gap - 8;
        this.chartX = this.textX + this.textW + gap;
        this.chartY = contentY + 8;
        this.chartH = contentH - 42;
        this.bottomLineY = contentY + contentH - 24;
        this.statusY = this.bottomLineY + 8;
        this.textScroll = 0;
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonNoShadow(0, this.screenPanelX + this.screenPanelW - 4 - 80, this.screenPanelY + this.screenPanelH - 24, 80, 20, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"gui.done", (Object[])new Object[0]), this.isJumbled)));
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

    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        this.initGui();
    }

    private int toDesignX(int mouseX) {
        return Math.round((float)(mouseX - this.screenPanelX) / this.uiScale);
    }

    private int toDesignY(int mouseY) {
        return Math.round((float)(mouseY - this.screenPanelY) / this.uiScale);
    }

    private boolean isInText(int dx, int dy) {
        return dx >= this.textX && dx < this.textX + this.textW && dy >= this.textY && dy < this.textY + this.textH;
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dwheel = Mouse.getEventDWheel();
        if (dwheel != 0) {
            int dy;
            int mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
            int my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
            int dx = this.toDesignX(mx);
            if (this.isInText(dx, dy = this.toDesignY(my))) {
                int maxScroll = this.getMaxScroll();
                this.textScroll = dwheel < 0 ? Math.min(maxScroll, this.textScroll + 10) : Math.max(0, this.textScroll - 10);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glDisable((int)3089);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.screenPanelX, (float)this.screenPanelY, (float)0.0f);
        GlStateManager.scale((float)this.uiScale, (float)this.uiScale, (float)1.0f);
        GuiContext.bind(TEX_BG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GuiPhaseReport.drawScaledCustomSizeModalRect((int)this.panelX, (int)this.panelY, (float)0.0f, (float)0.0f, (int)360, (int)300, (int)360, (int)300, (float)360.0f, (float)300.0f);
        GlStateManager.disableBlend();
        String title = GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"gui.srparasites.phase_report.title", (Object[])new Object[0]), this.isJumbled);
        this.drawCenteredNoShadow(title, this.panelX + this.panelW / 2, this.panelY + 10, -10066330);
        GuiContext.bind(TEX_FG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GuiPhaseReport.drawScaledCustomSizeModalRect((int)this.fgX, (int)this.fgY, (float)0.0f, (float)0.0f, (int)332, (int)212, (int)this.fgW, (int)this.fgH, (float)332.0f, (float)212.0f);
        GlStateManager.disableBlend();
        this.drawTextPanel();
        this.drawPieSection();
        this.drawThinHorizontalLine(this.textX, this.bottomLineY, this.textX + this.textW - 6, -10066330);
        String gainLabel = GuiContext.fmt((String)"gui.srparasites.phase_report.point_gain", (Object[])new Object[0]);
        String gainValue = this.canGain ? GuiContext.fmt((String)"gui.srparasites.phase_report.enabled", (Object[])new Object[0]) : GuiContext.fmt((String)"gui.srparasites.phase_report.disabled", (Object[])new Object[0]);
        String lossLabel = GuiContext.fmt((String)"gui.srparasites.phase_report.point_loss", (Object[])new Object[0]);
        String lossValue = this.canLoss ? GuiContext.fmt((String)"gui.srparasites.phase_report.enabled", (Object[])new Object[0]) : GuiContext.fmt((String)"gui.srparasites.phase_report.disabled", (Object[])new Object[0]);
        gainLabel = GuiDistortionHelper.jamTextIfNeeded(gainLabel, this.isJumbled);
        gainValue = GuiDistortionHelper.jamTextIfNeeded(gainValue, this.isJumbled);
        lossLabel = GuiDistortionHelper.jamTextIfNeeded(lossLabel, this.isJumbled);
        lossValue = GuiDistortionHelper.jamTextIfNeeded(lossValue, this.isJumbled);
        this.drawStatusLine(gainLabel, gainValue, this.textX, this.statusY, this.canGain ? -11141291 : -43691);
        this.drawStatusLine(lossLabel, lossValue, this.textX + 110, this.statusY, this.canLoss ? -11141291 : -43691);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawTextPanel() {
        this.enableDesignScissor(this.textX, this.textY, this.textW, this.textH);
        int x = this.textX;
        int y = this.textY - this.textScroll;
        int wrapWidth = this.textW - 8;
        for (String line : this.lines) {
            List<? extends String> wrapped = this.fontRendererObj.listFormattedStringToWidth(line, wrapWidth);
            for (String wrappedLine : wrapped) {
                if (y + 10 >= this.textY && y < this.textY + this.textH) {
                    this.fontRendererObj.drawString(wrappedLine, (float)x, (float)y, -15658735, false);
                }
                y += 10;
            }
        }
        GL11.glDisable((int)3089);
    }

    private void drawPieSection() {
        CompoundTag tag = com.dhanantry.scapeandrunparasites.item.ReportData.read(this.stack);
        if (tag == null || !tag.contains("PhaseValue")) {
            return;
        }
        int pure = Math.max(0, this.totalMobCount - this.parasiteCount - this.cothCount);
        int infected = Math.max(0, this.cothCount);
        int parasites = Math.max(0, this.parasiteCount);
        pure = GuiDistortionHelper.getDisplayValue(pure, this.isJumbled, 11);
        parasites = GuiDistortionHelper.getDisplayValue(parasites, this.isJumbled, 22);
        int total = pure + (infected = GuiDistortionHelper.getDisplayValue(infected, this.isJumbled, 33)) + parasites;
        if (total <= 0) {
            total = 1;
        }
        int cx = this.chartX + this.chartW / 2;
        int cy = this.chartY + 52;
        int radius = Math.min(this.chartW / 2 - 10, 38);
        String chartTitle = GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"gui.srparasites.phase_report.mob_chart", (Object[])new Object[0]), this.isJumbled);
        this.drawCenteredNoShadow(chartTitle, cx, this.chartY, -15658735);
        this.drawPieSlice(cx, cy, radius, 0.0f, 360.0f * ((float)pure / (float)total), -4210753);
        this.drawPieSlice(cx, cy, radius, 360.0f * ((float)pure / (float)total), 360.0f * ((float)(pure + parasites) / (float)total), -43691);
        this.drawPieSlice(cx, cy, radius, 360.0f * ((float)(pure + parasites) / (float)total), 360.0f, -43521);
        this.drawCircleOutline(cx, cy, radius, -11184811);
        int legendY = cy + radius + 12;
        String labelPure = GuiContext.fmt((String)"gui.srparasites.phase_report.uninfected_hosts", (Object[])new Object[0]);
        String labelParasites = GuiContext.fmt((String)"gui.srparasites.phase_report.parasite_entities", (Object[])new Object[0]);
        String labelInfected = GuiContext.fmt((String)"gui.srparasites.phase_report.infected_hosts", (Object[])new Object[0]);
        int displayPure = pure;
        int displayParasites = parasites;
        int displayInfected = infected;
        labelPure = GuiDistortionHelper.jamTextIfNeeded(labelPure, this.isJumbled);
        labelParasites = GuiDistortionHelper.jamTextIfNeeded(labelParasites, this.isJumbled);
        labelInfected = GuiDistortionHelper.jamTextIfNeeded(labelInfected, this.isJumbled);
        this.drawLegendWidget(this.chartX + 4, legendY, this.chartW - 8, 14, -10066330, labelPure, displayPure);
        this.drawLegendWidget(this.chartX + 4, legendY + 16, this.chartW - 8, 14, -43691, labelParasites, displayParasites);
        this.drawLegendWidget(this.chartX + 4, legendY + 32, this.chartW - 8, 14, -43521, labelInfected, displayInfected);
    }

    private void drawLegendWidget(int x, int y, int w, int h, int color, String label, int value) {
        GuiPhaseReport.drawRect((int)x, (int)y, (int)(x + w), (int)(y + h), (int)0x22000000);
        GuiPhaseReport.drawRect((int)x, (int)y, (int)(x + 6), (int)(y + h), (int)color);
        this.fontRendererObj.drawString(label, (float)(x + 10), (float)(y + 3), -15658735, false);
        String val = GuiDistortionHelper.jamTextIfNeeded(String.valueOf(value), this.isJumbled);
        this.fontRendererObj.drawString(val, (float)(x + w - this.fontRendererObj.getStringWidth(val) - 4), (float)(y + 3), color, false);
    }

    private void drawStatusLine(String label, String value, int x, int y, int valueColor) {
        this.fontRendererObj.drawString(label + ": ", (float)x, (float)y, -10066330, false);
        int off = this.fontRendererObj.getStringWidth(label + ": ");
        this.fontRendererObj.drawString(value, (float)(x + off), (float)y, valueColor, false);
    }

    private void drawPieSlice(int cx, int cy, int radius, float startDeg, float endDeg, int color) {
        if (endDeg <= startDeg) {
            return;
        }
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder buf = tess.getWorldRenderer();
        buf.begin(6, Tessellator.VertexFormat.POSITION_COLOR);
        buf.pos((double)cx, (double)cy, 0.0).color(r, g, b, a).endVertex();
        int steps = Math.max(12, (int)Math.ceil(Math.abs(endDeg - startDeg) / 6.0f));
        for (int i = 0; i <= steps; ++i) {
            float t = (float)i / (float)steps;
            float ang = startDeg + (endDeg - startDeg) * t;
            double rad = Math.toRadians((double)ang - 90.0);
            double px = (double)cx + Math.cos(rad) * (double)radius;
            double py = (double)cy + Math.sin(rad) * (double)radius;
            buf.pos(px, py, 0.0).color(r, g, b, a).endVertex();
        }
        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void drawCircleOutline(int cx, int cy, int radius, int color) {
        int a = color >> 24 & 0xFF;
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder buf = tess.getWorldRenderer();
        buf.begin(2, Tessellator.VertexFormat.POSITION_COLOR);
        int steps = 40;
        for (int i = 0; i < steps; ++i) {
            double ang = Math.toRadians((double)i / (double)steps * 360.0 - 90.0);
            double px = (double)cx + Math.cos(ang) * (double)radius;
            double py = (double)cy + Math.sin(ang) * (double)radius;
            buf.pos(px, py, 0.0).color(r, g, b, a).endVertex();
        }
        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }

    private void readNBT() {
        CompoundTag tag = com.dhanantry.scapeandrunparasites.item.ReportData.read(this.stack);
        if (tag == null || !tag.contains("PhaseValue")) {
            this.lines.clear();
            this.lines.add(GuiContext.fmt((String)"gui.srparasites.phase_report.no_data", (Object[])new Object[0]));
            this.lines.add("");
            this.lines.add(GuiContext.fmt((String)"gui.srparasites.phase_report.no_data_hint", (Object[])new Object[0]));
            return;
        }
        this.dimension = tag.getString("PhaseDimension");
        this.phase = tag.getInt("PhaseValue");
        this.totalPoints = tag.getInt("PhaseTotalPoints");
        this.nextPoints = tag.getInt("PhasePointsNext");
        this.progress = tag.getString("PhaseProgress");
        this.cooldown = tag.getInt("PhaseCooldown");
        this.canGain = tag.getBoolean("PhaseCanGain");
        this.canLoss = tag.getBoolean("PhaseCanLoss");
        this.mobcap = tag.getInt("PhaseMobcap");
        this.generation = tag.getInt("PhaseGeneration");
        this.genTicks = tag.getInt("PhaseGenTicks");
        this.parasiteCount = tag.getInt("PhaseParasiteCount");
        this.cothCount = tag.getInt("PhaseCothCount");
        this.totalMobCount = tag.getInt("PhaseTotalMobs");
    }

    private void buildLines() {
        if (!this.lines.isEmpty()) {
            return;
        }
        this.lines.clear();
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.dimension", (Object[])new Object[]{this.dimension})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.phase", (Object[])new Object[]{this.phase})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.total_points", (Object[])new Object[]{this.totalPoints})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.points_next", (Object[])new Object[]{this.nextPoints})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.progress", (Object[])new Object[]{this.progress})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.cooldown", (Object[])new Object[]{this.formatCooldown(this.cooldown)})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.mobcap", (Object[])new Object[]{this.mobcap})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.generation", (Object[])new Object[]{this.generation})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.gen_ticks", (Object[])new Object[]{this.genTicks})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.parasite_count", (Object[])new Object[]{this.parasiteCount})));
        this.lines.add(this.formatDisplayLine(GuiContext.fmt((String)"command.srpevolution.getphase.coth_count", (Object[])new Object[]{this.cothCount})));
    }

    private String formatDisplayLine(String s) {
        s = GuiDistortionHelper.toneDownTextColors(s);
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    private String toneDownTextColors(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\u00a7e", "\u00a78").replace("\u00a76", "\u00a78").replace("\u00a77", "\u00a78");
    }

    private String formatCooldown(int totalSeconds) {
        if (totalSeconds <= 0) {
            return "0s";
        }
        int hours = totalSeconds / 3600;
        int minutes = totalSeconds % 3600 / 60;
        int seconds = totalSeconds % 60;
        if (hours > 0) {
            if (seconds > 0) {
                return hours + "h " + minutes + "m " + seconds + "s";
            }
            return hours + "h " + minutes + "m";
        }
        if (minutes > 0) {
            if (seconds > 0) {
                return minutes + "m " + seconds + "s";
            }
            return minutes + "m";
        }
        return seconds + "s";
    }

    private int getContentHeight() {
        int wrapWidth = this.textW - 8;
        int h = 0;
        for (String line : this.lines) {
            List wrapped = this.fontRendererObj.listFormattedStringToWidth(line, wrapWidth);
            h += Math.max(1, wrapped.size()) * 10;
        }
        return h;
    }

    private int getMaxScroll() {
        return Math.max(0, this.getContentHeight() - this.textH);
    }

    private void drawCenteredNoShadow(String s, int x, int y, int color) {
        int w = this.fontRendererObj.getStringWidth(s);
        this.fontRendererObj.drawString(s, x - w / 2, y, color);
    }

    private void drawThinHorizontalLine(int x1, int y, int x2, int color) {
        GuiPhaseReport.drawRect((int)x1, (int)y, (int)x2, (int)(y + 1), (int)color);
    }

    private void enableDesignScissor(int x, int y, int w, int h) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        int factor = sr.getScaleFactor();
        int sx = Math.round(((float)this.screenPanelX + (float)x * this.uiScale) * (float)factor);
        int sy = Math.round(((float)this.screenPanelY + (float)y * this.uiScale) * (float)factor);
        int sw = Math.round((float)w * this.uiScale * (float)factor);
        int sh = Math.round((float)h * this.uiScale * (float)factor);
        int fbH = this.mc.getWindow().getHeight();
        GL11.glEnable((int)3089);
        GL11.glScissor((int)sx, (int)(fbH - (sy + sh)), (int)sw, (int)sh);
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
            int textColor = -15066598;
            if (!this.enabled) {
                textColor = -8947849;
            }
            int sw = GuiContext.FONT.getStringWidth(this.displayString);
            GuiContext.FONT.drawString(this.displayString, this.x + this.width / 2 - sw / 2, this.y + (this.height - 8) / 2, textColor);
        }
    }
}

