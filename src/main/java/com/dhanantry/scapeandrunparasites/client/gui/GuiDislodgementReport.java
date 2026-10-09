package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;


public class GuiDislodgementReport
extends GuiScreen {
    private final ItemStack stack;
    private boolean isJumbled;
    private static final ResourceLocation TEX_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_gui.png");
    private static final ResourceLocation TEX_FG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_fg_gui.png");
    private static final ResourceLocation TEX_ROW_ONE = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_row_gui_one.png");
    private static final ResourceLocation TEX_ROW_TWO = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_row_gui_two.png");
    private static final ResourceLocation TEX_ROW_THREE = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_row_gui_three.png");
    private static final ResourceLocation TEX_ROW_UNKNOWN = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/dislo_report_row_gui_unknown.png");
    private static final int ROW_TEX_W = 332;
    private static final int ROW_TEX_H = 36;
    private long printWorldTicks;
    private static final int BG_W = 360;
    private static final int BG_H = 300;
    private static final int FG_W = 332;
    private static final int FG_H = 212;
    private int fgX;
    private int fgY;
    private int fgW;
    private int fgH;
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
    private int listX;
    private int listY;
    private int listW;
    private int listH;
    private int listScroll = 0;
    private final List<Entry> entries = new ArrayList<Entry>();

    private static ResourceLocation getRowTexForEvent(int event) {
        switch (event) {
            case 0: 
            case 6: 
            case 7: 
            case 9: 
            case 15: 
            case 16: 
            case 19: 
            case 20: {
                return TEX_ROW_ONE;
            }
            case 1: 
            case 4: 
            case 8: 
            case 10: 
            case 13: 
            case 17: 
            case 18: 
            case 22: {
                return TEX_ROW_TWO;
            }
            case 3: 
            case 11: 
            case 12: 
            case 14: 
            case 21: 
            case 25: {
                return TEX_ROW_THREE;
            }
        }
        return TEX_ROW_UNKNOWN;
    }

    private static int severityBucket(int event) {
        switch (event) {
            case 5: 
            case 23: 
            case 24: 
            case 26: 
            case 27: 
            case 28: 
            case 29: 
            case 30: {
                return 3;
            }
            case 3: 
            case 11: 
            case 12: 
            case 14: 
            case 21: 
            case 25: {
                return 2;
            }
            case 1: 
            case 4: 
            case 8: 
            case 10: 
            case 13: 
            case 17: 
            case 18: 
            case 22: {
                return 1;
            }
        }
        return 0;
    }

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    public GuiDislodgementReport(ItemStack stack) {
        this.stack = stack;
    }

    public void initGui() {
        super.initGui();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.entries.clear();
        this.parseEntriesFromNBT();
        Collections.sort(this.entries, new Comparator<Entry>(){

            @Override
            public int compare(Entry a, Entry b) {
                int sb;
                int sa = GuiDislodgementReport.severityBucket(a.event);
                if (sa != (sb = GuiDislodgementReport.severityBucket(b.event))) {
                    return Integer.compare(sb, sa);
                }
                return Integer.compare(b.timeSec, a.timeSec);
            }
        });
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
        this.listX = this.fgX + inset;
        this.listY = this.fgY + inset;
        this.listW = this.fgW - inset * 2;
        this.listH = this.fgH - inset * 2;
        this.listScroll = 0;
        this.buttonList.clear();
        this.buttonList.add(new GuiButtonNoShadow(0, this.screenPanelX + this.screenPanelW - 4 - 80, this.screenPanelY + this.screenPanelH - 24, 80, 20, this.distort(GuiContext.fmt((String)"gui.done", (Object[])new Object[0]))));
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

    private boolean isInList(int dx, int dy) {
        return dx >= this.listX && dx < this.listX + this.listW && dy >= this.listY && dy < this.listY + this.listH;
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
                int rowH = 36;
                this.clampScrollToGrid(rowH);
                int maxScroll = this.getMaxScroll(rowH);
                this.listScroll = dwheel < 0 ? Math.min(maxScroll, this.listScroll + rowH) : Math.max(0, this.listScroll - rowH);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        boolean wasJumbled = this.isJumbled;
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        if (wasJumbled != this.isJumbled && !this.buttonList.isEmpty()) {
            ((GuiButton)this.buttonList.get((int)0)).displayString = this.distort(GuiContext.fmt((String)"gui.done", (Object[])new Object[0]));
        }
        GL11.glDisable((int)3089);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.screenPanelX, (float)this.screenPanelY, (float)0.0f);
        GlStateManager.scale((float)this.uiScale, (float)this.uiScale, (float)1.0f);
        GuiContext.bind(TEX_BG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GuiDislodgementReport.drawScaledCustomSizeModalRect((int)this.panelX, (int)this.panelY, (float)0.0f, (float)0.0f, (int)360, (int)300, (int)360, (int)300, (float)360.0f, (float)300.0f);
        GlStateManager.disableBlend();
        String title = this.distort(GuiContext.fmt((String)"gui.srparasites.dislodgement.title", (Object[])new Object[0]));
        this.drawCenteredNoShadow(title, this.panelX + this.panelW / 2, this.panelY + 10, -15658735);
        GuiContext.bind(TEX_FG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GuiDislodgementReport.drawScaledCustomSizeModalRect((int)this.fgX, (int)this.fgY, (float)0.0f, (float)0.0f, (int)332, (int)212, (int)this.fgW, (int)this.fgH, (float)332.0f, (float)212.0f);
        GlStateManager.disableBlend();
        this.drawEntriesList();
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        this.initGui();
    }

    private void drawEntriesList() {
        int rowH = 36;
        this.clampScrollToGrid(rowH);
        int y0 = this.listY - this.listScroll;
        ScaledResolution sr = new ScaledResolution(this.mc);
        int scale = sr.getScaleFactor();
        int fbW = this.mc.getMainRenderTarget().width;
        int fbH = this.mc.getMainRenderTarget().height;
        float guiX0 = (float)this.screenPanelX + (float)this.listX * this.uiScale;
        float guiY0 = (float)this.screenPanelY + (float)this.listY * this.uiScale;
        float guiW = (float)this.listW * this.uiScale;
        float guiH = (float)this.listH * this.uiScale;
        int scX = Mth.floor((float)(guiX0 * (float)scale));
        int scW = Mth.ceil((float)(guiW * (float)scale));
        int scYTop = Mth.floor((float)(guiY0 * (float)scale));
        int scH = Mth.ceil((float)(guiH * (float)scale));
        int scY = fbH - (scYTop + scH);
        int pad = Math.max(1, scale);
        scY -= pad;
        scW += pad * 2;
        scH += pad * 2;
        if ((scX -= pad) < 0) {
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
        float s = 0.8f;
        for (int i = 0; i < this.entries.size(); ++i) {
            int ry = y0 + i * rowH;
            if (ry + rowH <= this.listY || ry >= this.listY + this.listH) continue;
            Entry e = this.entries.get(i);
            GuiContext.bind(GuiDislodgementReport.getRowTexForEvent(e.event));
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
            GuiDislodgementReport.drawScaledCustomSizeModalRect((int)this.listX, (int)ry, (float)0.0f, (float)0.0f, (int)332, (int)36, (int)this.listW, (int)rowH, (float)332.0f, (float)36.0f);
            GlStateManager.disableBlend();
            int rem = this.getRemainingSeconds(e.timeSec);
            String timeStr = GuiDislodgementReport.formatHMS(rem);
            boolean expired = rem <= 0;
            String pre = expired ? "\u00a7m" : "";
            String post = expired ? "\u00a7r" : "";
            String line1Plain = GuiContext.fmt((String)"gui.srparasites.dislodgement.entry.head", (Object[])new Object[]{e.dim, e.event, timeStr});
            String line2Plain = GuiDislodgementReport.formatMeaning(e.event, e.value);
            String line3Plain = GuiContext.fmt((String)"gui.srparasites.dislodgement.entry.value", (Object[])new Object[]{e.value});
            int maxW = (int)((float)(this.listW - 6) / s);
            line1Plain = this.trimToWidth(line1Plain, maxW);
            line2Plain = this.trimToWidth(line2Plain, maxW);
            line3Plain = this.trimToWidth(line3Plain, maxW);
            String line1 = this.isJumbled ? GuiDistortionHelper.jamText(line1Plain) : pre + line1Plain + post;
            String line2 = this.isJumbled ? GuiDistortionHelper.jamText(line2Plain) : pre + line2Plain + post;
            String line3 = this.isJumbled ? GuiDistortionHelper.jamText(line3Plain) : pre + line3Plain + post;
            this.drawScaledStringNoShadow(line1, this.listX + 3, ry + 3, s, expired ? -10066330 : -15658735);
            this.drawScaledStringNoShadow(line2, this.listX + 3, ry + 14, s, expired ? -8947849 : -14540254);
            this.drawScaledStringNoShadow(line3, this.listX + 3, ry + 25, s, expired ? -7829368 : -13421773);
        }
        int contentH = this.entries.size() * rowH;
        int maxScroll = Math.max(0, contentH - this.listH);
        if (maxScroll > 0) {
            int barW = 4;
            int barX0 = this.listX + this.listW - barW - 1;
            int barX1 = this.listX + this.listW - 1;
            GuiDislodgementReport.drawRect((int)barX0, (int)this.listY, (int)barX1, (int)(this.listY + this.listH), (int)0x22000000);
            int thumbH = Math.max(10, (int)((float)this.listH / (float)contentH * (float)this.listH));
            int thumbY = this.listY + (int)((float)this.listScroll / (float)maxScroll * (float)(this.listH - thumbH));
            GuiDislodgementReport.drawRect((int)barX0, (int)thumbY, (int)barX1, (int)(thumbY + thumbH), (int)0x55000000);
        }
        if (this.entries.isEmpty()) {
            String none = this.distort(GuiContext.fmt((String)"gui.srparasites.dislodgement.none", (Object[])new Object[0]));
            this.drawCenteredNoShadow(none, this.listX + this.listW / 2, this.listY + this.listH / 2 - 4, -12303292);
        }
        GL11.glDisable((int)3089);
    }

    private void parseEntriesFromNBT() {
        String code;
        CompoundTag t = com.dhanantry.scapeandrunparasites.item.ReportData.read(this.stack);
        if (t != null) {
            int day = t.getInt("PrintDay");
            int time = t.getInt("PrintTime");
            this.printWorldTicks = (long)day * 24000L + (long)time;
        } else {
            this.printWorldTicks = 0L;
        }
        String string = code = t != null && t.contains("DislodgementCode") ? t.getString("DislodgementCode") : "";
        if (code == null || code.trim().isEmpty()) {
            return;
        }
        String[] parts = code.split(";");
        if (parts.length < 4 || parts.length % 4 != 0) {
            return;
        }
        for (int i = 0; i < parts.length; i += 4) {
            String dim = parts[i].trim();
            int event = GuiDislodgementReport.safeParseInt(parts[i + 1].trim(), -1);
            String value = parts[i + 2].trim();
            int time = GuiDislodgementReport.safeParseInt(parts[i + 3].trim(), 0);
            this.entries.add(new Entry(dim, event, value, time));
        }
    }

    private static int safeParseInt(String s, int fallback) {
        try {
            return Integer.parseInt(s);
        }
        catch (Throwable t) {
            return fallback;
        }
    }

    private static String meaningKey(int event) {
        switch (event) {
            case 0: {
                return "srparasites.dislodgement.event.0";
            }
            case 1: {
                return "srparasites.dislodgement.event.1";
            }
            case 2: {
                return "srparasites.dislodgement.event.2";
            }
            case 3: {
                return "srparasites.dislodgement.event.3";
            }
            case 4: {
                return "srparasites.dislodgement.event.4";
            }
            case 6: {
                return "srparasites.dislodgement.event.6";
            }
            case 7: {
                return "srparasites.dislodgement.event.7";
            }
            case 8: {
                return "srparasites.dislodgement.event.8";
            }
            case 9: {
                return "srparasites.dislodgement.event.9";
            }
            case 10: {
                return "srparasites.dislodgement.event.10";
            }
            case 11: {
                return "srparasites.dislodgement.event.11";
            }
            case 12: {
                return "srparasites.dislodgement.event.12";
            }
            case 13: {
                return "srparasites.dislodgement.event.13";
            }
            case 14: {
                return "srparasites.dislodgement.event.14";
            }
            case 15: {
                return "srparasites.dislodgement.event.15";
            }
            case 16: {
                return "srparasites.dislodgement.event.16";
            }
            case 17: {
                return "srparasites.dislodgement.event.17";
            }
            case 18: {
                return "srparasites.dislodgement.event.18";
            }
            case 19: {
                return "srparasites.dislodgement.event.19";
            }
            case 20: {
                return "srparasites.dislodgement.event.20";
            }
            case 21: {
                return "srparasites.dislodgement.event.21";
            }
            case 22: {
                return "srparasites.dislodgement.event.22";
            }
            case 25: {
                return "srparasites.dislodgement.event.25";
            }
        }
        return "srparasites.dislodgement.event.unknown";
    }

    private static String formatMeaning(int event, String value) {
        String key = GuiDislodgementReport.meaningKey(event);
        switch (event) {
            case 0: 
            case 11: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 20: {
                return GuiContext.fmt((String)key, (Object[])new Object[0]);
            }
        }
        return GuiContext.fmt((String)key, (Object[])new Object[]{value});
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

    private static String formatHMS(int secondsIn) {
        int s = Math.max(0, secondsIn);
        int h = s / 3600;
        int m = s % 3600 / 60;
        int sec = s % 60;
        if (h > 0) {
            return String.format("%d:%02d:%02d", h, m, sec);
        }
        return String.format("%d:%02d", m, sec);
    }

    private int getRemainingSeconds(int originalSeconds) {
        if (this.mc == null || this.mc.level == null) {
            return originalSeconds;
        }
        long now = this.mc.level.getDayTime();
        long elapsedTicks = now - this.printWorldTicks;
        if (elapsedTicks < 0L) {
            elapsedTicks = 0L;
        }
        int elapsedSec = (int)(elapsedTicks / 20L);
        return originalSeconds - elapsedSec;
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

    private int getMaxScroll(int rowH) {
        int contentH = this.entries.size() * rowH;
        int rawMax = Math.max(0, contentH - this.listH);
        int snappedUp = (rawMax + rowH - 1) / rowH * rowH;
        return snappedUp;
    }

    private void clampScrollToGrid(int rowH) {
        int maxScroll = this.getMaxScroll(rowH);
        if (this.listScroll < 0) {
            this.listScroll = 0;
        }
        if (this.listScroll > maxScroll) {
            this.listScroll = maxScroll;
        }
        this.listScroll = this.listScroll / rowH * rowH;
    }

    private static class Entry {
        final String dim;
        final int event;
        final String value;
        final int timeSec;

        Entry(String dim, int event, String value, int timeSec) {
            this.dim = dim;
            this.event = event;
            this.value = value;
            this.timeSec = timeSec;
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

