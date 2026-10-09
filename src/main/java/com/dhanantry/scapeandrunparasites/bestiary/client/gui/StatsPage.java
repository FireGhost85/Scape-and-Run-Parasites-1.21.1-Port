package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityRelayController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class StatsPage
extends GuiScreen {
    private final Player player;
    private final GuiScreen parent;
    private boolean isJumbled;
    private static final ResourceLocation TEX_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_gui.png");
    private static final ResourceLocation TEX_FG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_fg_gui.png");
    private static final int BG_W = 360;
    private static final int BG_H = 300;
    private static final int FG_W = 332;
    private static final int FG_H = 212;
    private static final int FG_BORDER_THICKNESS = 1;
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
    private int leftX;
    private int leftY;
    private int leftW;
    private int leftH;
    private int rightX;
    private int rightY;
    private int rightW;
    private int rightH;
    private TierStat hoveredBarStat = null;
    private int rightScroll = 0;
    private final List<TierStat> stats = new ArrayList<TierStat>();

    public StatsPage(Player player, GuiScreen parent) {
        this.player = player;
        this.parent = parent;
    }

    public void initGui() {
        super.initGui();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.rebuildStats();
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
        int inset = 4;
        int contentX = this.fgX + inset;
        int contentY = this.fgY + inset;
        int contentW = this.fgW - inset * 2;
        int contentH = this.fgH - inset * 2;
        int gap = 10;
        this.leftX = contentX + 7;
        this.leftY = contentY + 7;
        this.leftW = 138;
        this.leftH = contentH - 14;
        this.rightX = this.leftX + this.leftW + gap;
        this.rightY = contentY + 7;
        this.rightW = contentX + contentW - this.rightX - 7;
        this.rightH = contentH - 14;
        this.rightScroll = StatsPage.clamp(this.rightScroll, 0, this.getRightMaxScroll());
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.screenPanelX + 8, this.screenPanelY + this.screenPanelH - 24, 80, 20, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"gui.back", (Object[])new Object[0]), this.isJumbled)));
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.setScreen(this.parent);
        }
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.setScreen(null);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    private String formatDamage(float amount) {
        return String.format(Locale.ROOT, "%.1f", Float.valueOf(amount));
    }

    private int getCombatSectionHeight() {
        return 64;
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

    private boolean isInRightPanel(int dx, int dy) {
        return dx >= this.rightX && dx < this.rightX + this.rightW && dy >= this.rightY && dy < this.rightY + this.rightH;
    }

    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dwheel = Mouse.getEventDWheel();
        if (dwheel == 0) {
            return;
        }
        int mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
        int my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
        int dx = this.toDesignX(mx);
        int dy = this.toDesignY(my);
        int step = 12;
        if (this.isInRightPanel(dx, dy)) {
            this.rightScroll = dwheel < 0 ? Math.min(this.getRightMaxScroll(), this.rightScroll + step) : Math.max(0, this.rightScroll - step);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean wasJumbled = this.isJumbled;
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        if (wasJumbled != this.isJumbled && !this.buttonList.isEmpty()) {
            GuiButton back = (GuiButton)this.buttonList.get(0);
            back.displayString = GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"gui.back", (Object[])new Object[0]), this.isJumbled);
        }
        this.hoveredBarStat = null;
        GL11.glDisable((int)3089);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.screenPanelX, (float)this.screenPanelY, (float)0.0f);
        GlStateManager.scale((float)this.uiScale, (float)this.uiScale, (float)1.0f);
        GuiContext.bind(TEX_BG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        StatsPage.drawScaledCustomSizeModalRect((int)this.panelX, (int)this.panelY, (float)0.0f, (float)0.0f, (int)360, (int)300, (int)360, (int)300, (float)360.0f, (float)300.0f);
        GlStateManager.disableBlend();
        this.drawCenteredNoShadow(GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.tab.stats", (Object[])new Object[0]), this.isJumbled), this.panelX + this.panelW / 2, this.panelY + 10, -10066330);
        GuiContext.bind(TEX_FG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        StatsPage.drawScaledCustomSizeModalRect((int)this.fgX, (int)this.fgY, (float)0.0f, (float)0.0f, (int)332, (int)212, (int)this.fgW, (int)this.fgH, (float)332.0f, (float)212.0f);
        GlStateManager.disableBlend();
        this.drawLeftBarGraph(mouseX, mouseY);
        this.drawRightStatsPanel();
        this.drawScrollHints();
        GL11.glDisable((int)3089);
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hoveredBarStat != null) {
            ArrayList<String> tooltip = new ArrayList<String>();
            tooltip.add(GuiDistortionHelper.jamTextIfNeeded(this.hoveredBarStat.label, this.isJumbled));
            int shownKills = GuiDistortionHelper.getDisplayValue(this.hoveredBarStat.kills, this.isJumbled, 7000 + this.stats.indexOf(this.hoveredBarStat));
            tooltip.add(GuiDistortionHelper.jamTextIfNeeded(String.valueOf(shownKills), this.isJumbled));
            this.drawHoveringText(tooltip, mouseX, mouseY);
        }
    }

    private void drawLeftBarGraph(int mouseX, int mouseY) {
        this.enableDesignScissor(this.leftX, this.leftY, this.leftW, this.leftH);
        int titleY = this.leftY;
        this.fontRendererObj.drawString(GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.chart_title", (Object[])new Object[0]), this.isJumbled), (float)this.leftX, (float)titleY, -14540254, false);
        StatsPage.drawRect((int)this.leftX, (int)(titleY + 10), (int)(this.leftX + Math.min(this.leftW - 6, 120)), (int)(titleY + 11), (int)-2007082412);
        int chartX = this.leftX + 4;
        int chartY = this.leftY + 16;
        int chartW = this.leftW - 10;
        int chartH = this.leftH - 20;
        StatsPage.drawRect((int)chartX, (int)chartY, (int)(chartX + chartW), (int)(chartY + chartH), (int)0x10000000);
        int axisColor = 1717459540;
        StatsPage.drawRect((int)chartX, (int)(chartY + chartH - 1), (int)(chartX + chartW), (int)(chartY + chartH), (int)axisColor);
        StatsPage.drawRect((int)chartX, (int)chartY, (int)(chartX + 1), (int)(chartY + chartH), (int)axisColor);
        int max = 1;
        if (!this.isJumbled) {
            for (TierStat stat : this.stats) {
                if (stat.kills <= max) continue;
                max = stat.kills;
            }
        }
        int count = Math.max(1, this.stats.size());
        int slotW = Math.max(6, chartW / count);
        int barW = Math.max(3, Math.min(7, slotW - 2));
        int hoverDx = this.toDesignX(mouseX);
        int hoverDy = this.toDesignY(mouseY);
        TierStat hovered = null;
        for (int i = 0; i < this.stats.size(); ++i) {
            int barH;
            TierStat stat = this.stats.get(i);
            int centerX = chartX + i * slotW + slotW / 2;
            int barX = centerX - barW / 2;
            int usableH = chartH - 4;
            int shownKills = GuiDistortionHelper.getDisplayValue(stat.kills, this.isJumbled, 100 + i);
            if (this.isJumbled) {
                float fakeRatio = GuiDistortionHelper.getDisplayRatio(stat.kills, max, true, 1000 + i);
                barH = Math.round(fakeRatio * (float)usableH);
                barH = Math.max(1, Math.min(usableH, barH));
            } else {
                barH = (int)Math.round((double)shownKills / (double)max * (double)usableH);
                if (shownKills > 0) {
                    barH = Math.max(1, barH);
                }
            }
            int barY = chartY + chartH - 2 - barH;
            StatsPage.drawRect((int)barX, (int)(chartY + chartH - 2 - usableH), (int)(barX + barW), (int)(chartY + chartH - 2), (int)0x8000000);
            StatsPage.drawRect((int)barX, (int)barY, (int)(barX + barW), (int)(chartY + chartH - 2), (int)stat.color);
            if (hoverDx < barX || hoverDx >= barX + barW || hoverDy < chartY || hoverDy >= chartY + chartH) continue;
            hovered = stat;
            StatsPage.drawRect((int)(barX - 1), (int)(barY - 1), (int)(barX + barW + 1), (int)(chartY + chartH - 1), (int)0x40FFFFFF);
        }
        GL11.glDisable((int)3089);
        this.hoveredBarStat = hovered;
    }

    private void drawRightStatsPanel() {
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        int x = this.rightX;
        int rowW = this.rightW - 6;
        int combatSectionH = this.getCombatSectionHeight();
        int killsHeaderY = this.rightY;
        this.drawSectionTitle(GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.total_kills", (Object[])new Object[0]), this.isJumbled), x, killsHeaderY, rowW);
        int listY = this.rightY + 14;
        int listH = this.rightH - 14 - combatSectionH - 6;
        this.enableDesignScissor(x, listY, this.rightW, listH);
        int y = listY - this.rightScroll;
        int grandTotal = 0;
        for (TierStat stat : this.stats) {
            grandTotal += stat.kills;
        }
        this.drawStatRow(x, y, rowW, -7699333, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.all_parasites", (Object[])new Object[0]), this.isJumbled), GuiDistortionHelper.getDisplayValue(grandTotal, this.isJumbled, 5000));
        y += 18;
        for (int i = 0; i < this.stats.size(); ++i) {
            TierStat stat;
            stat = this.stats.get(i);
            this.drawStatRow(x, y, rowW, stat.color, GuiDistortionHelper.jamTextIfNeeded(stat.label, this.isJumbled), GuiDistortionHelper.getDisplayValue(stat.kills, this.isJumbled, 5100 + i));
            y += 16;
        }
        GL11.glDisable((int)3089);
        int combatHeaderY = this.rightY + this.rightH - combatSectionH;
        this.drawSectionTitle(GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.combat_totals", (Object[])new Object[0]), this.isJumbled), x, combatHeaderY, rowW);
        int cy = combatHeaderY + 14;
        if (prog != null) {
            String dmgTo = this.formatDamage(prog.getDamageToParasites());
            String dmgFrom = this.formatDamage(prog.getDamageFromParasites());
            String deaths = String.valueOf(prog.getDeathsByParasites());
            if (this.isJumbled) {
                dmgTo = String.valueOf(GuiDistortionHelper.getJammedValue(6001));
                dmgFrom = String.valueOf(GuiDistortionHelper.getJammedValue(6002));
                deaths = String.valueOf(GuiDistortionHelper.getJammedValue(6003));
            }
            this.drawStatRow(x, cy, rowW, -7699333, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.damage_to_parasites", (Object[])new Object[0]), this.isJumbled), GuiDistortionHelper.jamTextIfNeeded(dmgTo, this.isJumbled));
            this.drawStatRow(x, cy += 18, rowW, -7699333, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.damage_from_parasites", (Object[])new Object[0]), this.isJumbled), GuiDistortionHelper.jamTextIfNeeded(dmgFrom, this.isJumbled));
            this.drawStatRow(x, cy += 16, rowW, -7699333, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.deaths_by_parasites", (Object[])new Object[0]), this.isJumbled), GuiDistortionHelper.jamTextIfNeeded(deaths, this.isJumbled));
        } else {
            String dmgTo = this.isJumbled ? String.valueOf(GuiDistortionHelper.getJammedValue(6011)) : "0.0";
            String dmgFrom = this.isJumbled ? String.valueOf(GuiDistortionHelper.getJammedValue(6012)) : "0.0";
            String deaths = this.isJumbled ? String.valueOf(GuiDistortionHelper.getJammedValue(6013)) : "0";
            this.drawStatRow(x, cy, rowW, -7699333, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.damage_to_parasites", (Object[])new Object[0]), this.isJumbled), GuiDistortionHelper.jamTextIfNeeded(dmgTo, this.isJumbled));
            this.drawStatRow(x, cy += 18, rowW, -7699333, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.damage_from_parasites", (Object[])new Object[0]), this.isJumbled), GuiDistortionHelper.jamTextIfNeeded(dmgFrom, this.isJumbled));
            this.drawStatRow(x, cy += 16, rowW, -7699333, GuiDistortionHelper.jamTextIfNeeded(GuiContext.fmt((String)"bestiary.stats.deaths_by_parasites", (Object[])new Object[0]), this.isJumbled), GuiDistortionHelper.jamTextIfNeeded(deaths, this.isJumbled));
        }
    }

    private void drawScrollHints() {
        int listY = this.rightY + 14;
        int listH = this.rightH - 14 - this.getCombatSectionHeight() - 6;
        int rightBarX = this.rightX + this.rightW - 2;
        if (this.getRightMaxScroll() > 0) {
            StatsPage.drawRect((int)rightBarX, (int)listY, (int)(rightBarX + 1), (int)(listY + listH), (int)0x33222222);
            int thumbH = Math.max(10, listH * listH / Math.max(listH, this.getRightContentHeight()));
            int thumbY = listY + this.rightScroll * (listH - thumbH) / Math.max(1, this.getRightMaxScroll());
            StatsPage.drawRect((int)rightBarX, (int)thumbY, (int)(rightBarX + 2), (int)(thumbY + thumbH), (int)0x66666666);
        }
    }

    private void drawSectionTitle(String s, int x, int y, int w) {
        this.fontRendererObj.drawString(s, (float)x, (float)y, -14540254, false);
        StatsPage.drawRect((int)x, (int)(y + 10), (int)(x + Math.min(w - 6, 120)), (int)(y + 11), (int)-2007082412);
    }

    private void drawStatRow(int x, int y, int w, int color, String label, int value) {
        this.drawStatRow(x, y, w, color, label, String.valueOf(value));
    }

    private void drawStatRow(int x, int y, int w, int color, String label, String valueText) {
        StatsPage.drawRect((int)x, (int)y, (int)(x + w), (int)(y + 13), (int)0x18000000);
        StatsPage.drawRect((int)x, (int)y, (int)(x + 4), (int)(y + 13), (int)color);
        int textColor = -14540254;
        String val = valueText == null ? "" : valueText;
        String safeLabel = label == null ? "" : label;
        int valueWidth = this.fontRendererObj.getStringWidth(val);
        int labelMaxWidth = Math.max(20, w - 14 - valueWidth - 8);
        String plainLabel = ChatFormatting.stripFormatting((String)safeLabel);
        String shownLabel = safeLabel;
        if (this.fontRendererObj.getStringWidth(plainLabel) > labelMaxWidth) {
            String trimmedPlain = this.fontRendererObj.trimStringToWidth(plainLabel, labelMaxWidth);
            if (!trimmedPlain.equals(plainLabel) && trimmedPlain.length() > 2) {
                trimmedPlain = trimmedPlain.substring(0, Math.max(0, trimmedPlain.length() - 2)) + ".";
            }
            shownLabel = this.isJumbled ? GuiDistortionHelper.jamText(trimmedPlain) : trimmedPlain;
        }
        this.fontRendererObj.drawString(shownLabel, (float)(x + 8), (float)(y + 3), textColor, false);
        this.fontRendererObj.drawString(val, (float)(x + w - valueWidth - 3), (float)(y + 3), color, false);
    }

    private void rebuildStats() {
        this.stats.clear();
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        if (prog == null) {
            return;
        }
        for (com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier tier : com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier.values()) {
            int total = 0;
            for (com.dhanantry.scapeandrunparasites.bestiary.BestiaryEntry en : com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry.all()) {
                if (en.tier == tier) {
                    total += Math.max(0, prog.getKills(en.mobId));
                }
            }
            String idLower = tier.name().toLowerCase(java.util.Locale.ROOT);
            String tierKey = "bestiary.tier." + idLower;
            String label = GuiContext.hasKey((String)tierKey) ? GuiContext.fmt((String)tierKey, (Object[])new Object[0]) : tier.name();
            int color = this.getTierColor(idLower);
            this.stats.add(new TierStat(label, color, total));
        }
    }

    private int getTierColor(String tierId) {
        switch (tierId) {
            case "inborn": {
                return -8742026;
            }
            case "assimilated": {
                return -9594985;
            }
            case "assimara": {
                return -11109252;
            }
            case "hijacked": {
                return -7709090;
            }
            case "feral": {
                return -6655374;
            }
            case "crude": {
                return -9672605;
            }
            case "primitive": {
                return -7436675;
            }
            case "adapted": {
                return -5401757;
            }
            case "nexus": {
                return -6586730;
            }
            case "deterrent": {
                return -8558982;
            }
            case "pure": {
                return -9601123;
            }
            case "preeminent": {
                return -10519969;
            }
            case "ancient": {
                return -9084294;
            }
            case "derived": {
                return -10588274;
            }
        }
        return -7699333;
    }

    private int getRightContentHeight() {
        int height = 18;
        return height += this.stats.size() * 16;
    }

    private int getRightMaxScroll() {
        int listVisibleHeight = this.rightH - 14 - this.getCombatSectionHeight() - 6;
        return Math.max(0, this.getRightContentHeight() - listVisibleHeight);
    }

    private void enableDesignScissor(int x, int y, int w, int h) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        int scaleFactor = sr.getScaleFactor();
        int sx = Math.round(((float)this.screenPanelX + (float)x * this.uiScale) * (float)scaleFactor);
        int sy = Math.round(((float)this.screenPanelY + (float)y * this.uiScale) * (float)scaleFactor);
        int sw = Math.round((float)w * this.uiScale * (float)scaleFactor);
        int sh = Math.round((float)h * this.uiScale * (float)scaleFactor);
        if (sw <= 0 || sh <= 0) {
            return;
        }
        GL11.glEnable((int)3089);
        GL11.glScissor((int)sx, (int)(this.mc.getWindow().getHeight() - (sy + sh)), (int)sw, (int)sh);
    }

    private void drawCenteredNoShadow(String text, int x, int y, int color) {
        this.fontRendererObj.drawString(text, (float)(x - this.fontRendererObj.getStringWidth(text) / 2), (float)y, color, false);
    }

    private static int clamp(int val, int min, int max) {
        if (val < min) {
            return min;
        }
        return Math.min(val, max);
    }

    public static final class CurrentProgressClientCache {
        public static boolean hasData = false;
        public static int dimension = 0;
        public static int phase = 0;
        public static int totalPoints = 0;
        public static int pointsNeededNext = 0;
        public static double progressPercent = 0.0;
        public static int cooldown = 0;
        public static boolean canGain = true;
        public static boolean canLose = true;
        public static String dislodgementCodes = "";
        public static int mobCap = 0;
        public static int generation = 0;
        public static int generationTicks = 0;
        public static int udl = 0;
        public static int parasiteCount = 0;
        public static int cothCount = 0;
        public static int eivCount = 0;

        private CurrentProgressClientCache() {
        }

        public static void set(int dimensionIn, int phaseIn, int totalPointsIn, int pointsNeededNextIn, double progressPercentIn, int cooldownIn, boolean canGainIn, boolean canLoseIn, String dislodgementCodesIn, int mobCapIn, int generationIn, int generationTicksIn, int udlIn, int parasiteCountIn, int cothCountIn, int eivCountIn) {
            dimension = dimensionIn;
            phase = phaseIn;
            totalPoints = totalPointsIn;
            pointsNeededNext = pointsNeededNextIn;
            progressPercent = progressPercentIn;
            cooldown = cooldownIn;
            canGain = canGainIn;
            canLose = canLoseIn;
            dislodgementCodes = dislodgementCodesIn == null ? "" : dislodgementCodesIn;
            mobCap = mobCapIn;
            generation = generationIn;
            generationTicks = generationTicksIn;
            udl = udlIn;
            parasiteCount = parasiteCountIn;
            cothCount = cothCountIn;
            eivCount = eivCountIn;
            hasData = true;
        }
    }

    private static class TierStat {
        final String label;
        final int color;
        final int kills;

        TierStat(String label, int color, int kills) {
            this.label = label;
            this.color = color;
            this.kills = kills;
        }
    }
}

