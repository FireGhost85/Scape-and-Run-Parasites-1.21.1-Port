package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.bestiary.effects.SRPStatusEffectRegistry;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class StatusEffectsPage
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
    private static final int SCROLLBAR_W = 6;
    private static final int SCROLLBAR_GAP = 6;
    private static final int SCROLLBAR_X = 2;
    private static final int SCROLLBAR_Y = 32;
    private static final int SCROLLBAR_H = 180;
    private static final int MIN_THUMB_H = 18;
    private static final int DETAIL_PAD = 8;
    private static final int DETAIL_SCROLLBAR_W = 8;
    private static final int DETAIL_SCROLLBAR_GAP = 4;
    private static final int DETAIL_LINE_H = 10;
    private static final int DETAIL_MIN_THUMB_H = 12;
    private static final ResourceLocation POTION_TEX = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/gui/container/inventory.png");
    private final Player player;
    private final GuiScreen parent;
    private final List<SRPStatusEffectRegistry.Entry> discovered = new ArrayList<SRPStatusEffectRegistry.Entry>();
    private int selectedIndex = -1;
    private int scroll = 0;
    private int detailScrollPx = 0;
    private boolean draggingScrollbar = false;
    private int scrollbarDragOffset = 0;
    private boolean draggingDetailScrollbar = false;
    private int detailDragGrabOffset = 0;
    private boolean isJumbled;

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    public StatusEffectsPage(Player player, GuiScreen parent) {
        this.player = player;
        this.parent = parent;
    }

    public void initGui() {
        super.initGui();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, 10, 10, 60, 20, this.distort("< " + GuiContext.fmt((String)"bestiary.effects.home", (Object[])new Object[0]))));
        this.rebuildDiscovered();
        this.rebuildListButtons();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    private void rebuildDiscovered() {
        this.discovered.clear();
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        if (prog == null) {
            return;
        }
        for (SRPStatusEffectRegistry.Entry e : SRPStatusEffectRegistry.all()) {
            if (!prog.hasSeenEffect(e.id)) continue;
            this.discovered.add(e);
        }
        if (this.selectedIndex >= this.discovered.size()) {
            int n = this.selectedIndex = this.discovered.isEmpty() ? -1 : 0;
        }
        if (this.selectedIndex < 0 && !this.discovered.isEmpty()) {
            this.selectedIndex = 0;
        }
        this.detailScrollPx = 0;
        this.draggingDetailScrollbar = false;
    }

    private String forceWhite(String s) {
        return s == null ? "" : ChatFormatting.stripFormatting((String)s);
    }

    private void rebuildListButtons() {
        int idx;
        this.buttonList.removeIf(b -> b.id >= 200 && b.id < 2000);
        int firstRow = this.scroll / 14;
        int maxRows = this.visibleRows();
        for (int i = 0; i < maxRows && (idx = firstRow + i) < this.discovered.size(); ++i) {
            SRPStatusEffectRegistry.Entry e = this.discovered.get(idx);
            String label = this.distort(this.resolveEffectNameList(e));
            int y = 32 + i * 14;
            this.buttonList.add(new GuiButton(200 + idx, 16, y, 126, 14, label));
        }
    }

    private String resolveEffectNameList(SRPStatusEffectRegistry.Entry e) {
        String name = this.resolveEffectName(e);
        return ChatFormatting.stripFormatting((String)name);
    }

    private int visibleRows() {
        return 12;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        int idx;
        if (button.id == 1) {
            this.mc.setScreen(this.parent);
            return;
        }
        if (button.id >= 200 && button.id < 2000 && (idx = button.id - 200) >= 0 && idx < this.discovered.size()) {
            this.selectedIndex = idx;
            this.detailScrollPx = 0;
            this.draggingDetailScrollbar = false;
        }
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
            int maxScroll = this.getMaxScroll();
            this.scroll = Math.max(0, Math.min(maxScroll, this.scroll + dir * 14));
            this.rebuildListButtons();
        } else {
            int maxScroll = this.getMaxScroll();
            this.scroll = Math.max(0, Math.min(maxScroll, this.scroll + dir * 14));
            this.rebuildListButtons();
        }
    }

    private int getMaxScroll() {
        int contentH = this.discovered.size() * 14;
        return Math.max(0, contentH - 180);
    }

    private int getScrollbarThumbHeight() {
        int contentH = Math.max(1, this.discovered.size() * 14);
        if (contentH <= 180) {
            return 180;
        }
        int h = (int)(180.0f / (float)contentH * 180.0f);
        return Math.max(18, Math.min(180, h));
    }

    private int getScrollbarThumbY() {
        int max = this.getMaxScroll();
        if (max <= 0) {
            return 32;
        }
        int thumbH = this.getScrollbarThumbHeight();
        int trackRange = 180 - thumbH;
        if (trackRange <= 0) {
            return 32;
        }
        float t = (float)this.scroll / (float)max;
        return 32 + Math.round(t * (float)trackRange);
    }

    private boolean isMouseOverScrollbar(int mouseX, int mouseY) {
        return mouseX >= 2 && mouseX < 8 && mouseY >= 32 && mouseY < 212;
    }

    private void setScrollFromThumbTop(int thumbTop) {
        int trackRange;
        int max = this.getMaxScroll();
        if (max <= 0) {
            this.scroll = 0;
            return;
        }
        int thumbH = this.getScrollbarThumbHeight();
        int trackMin = 32;
        int trackMax = 212 - thumbH;
        if (thumbTop < trackMin) {
            thumbTop = trackMin;
        }
        if (thumbTop > trackMax) {
            thumbTop = trackMax;
        }
        if ((trackRange = trackMax - trackMin) <= 0) {
            this.scroll = 0;
            return;
        }
        float t = (float)(thumbTop - trackMin) / (float)trackRange;
        this.scroll = Math.round(t * (float)max);
        if (this.scroll < 0) {
            this.scroll = 0;
        }
        if (this.scroll > max) {
            this.scroll = max;
        }
        this.rebuildListButtons();
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
        if (this.selectedIndex < 0 || this.selectedIndex >= this.discovered.size()) {
            return lines;
        }
        SRPStatusEffectRegistry.Entry e = this.discovered.get(this.selectedIndex);
        String descKey = "bestiary.effect." + e.id + ".desc";
        if (GuiContext.hasKey((String)descKey)) {
            String desc = this.distort(GuiContext.fmt((String)descKey, (Object[])new Object[0]));
            lines.addAll(this.fontRendererObj.listFormattedStringToWidth(desc, this.getDetailTextW()));
        } else {
            lines.add(this.distort(GuiContext.fmt((String)"bestiary.effects.missing_desc", (Object[])new Object[0])));
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

    private boolean isMouseOver(int x1, int y1, int x2, int y2, int mx, int my) {
        return mx >= x1 && mx < x2 && my >= y1 && my < y2;
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

    private void scrollDetail(int dir) {
        int maxScroll = this.getDetailMaxScrollPx();
        this.detailScrollPx = Math.max(0, Math.min(maxScroll, this.detailScrollPx + dir * 10));
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int firstRow;
        int rel;
        int thumbTop;
        this.drawDefaultBackground();
        if (this.draggingScrollbar) {
            thumbTop = mouseY - this.scrollbarDragOffset;
            this.setScrollFromThumbTop(thumbTop);
        }
        if (this.draggingDetailScrollbar) {
            thumbTop = mouseY - this.detailDragGrabOffset;
            this.setDetailScrollFromThumbTop(thumbTop);
        }
        String title = this.distort(GuiContext.fmt((String)"bestiary.effects.title", (Object[])new Object[0]));
        this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 12, 0xFFFFFF);
        StatusEffectsPage.drawRect((int)12, (int)30, (int)146, (int)214, (int)-1442840576);
        StatusEffectsPage.drawRect((int)14, (int)32, (int)144, (int)212, (int)0x66000000);
        StatusEffectsPage.drawRect((int)1, (int)31, (int)9, (int)213, (int)-1442840576);
        StatusEffectsPage.drawRect((int)2, (int)32, (int)8, (int)212, (int)0x44000000);
        if (this.getMaxScroll() > 0) {
            int thumbY = this.getScrollbarThumbY();
            int thumbH = this.getScrollbarThumbHeight();
            StatusEffectsPage.drawRect((int)2, (int)thumbY, (int)8, (int)(thumbY + thumbH), (int)-1716868438);
            StatusEffectsPage.drawRect((int)3, (int)(thumbY + 1), (int)7, (int)(thumbY + thumbH - 1), (int)-857874979);
        }
        StatusEffectsPage.drawRect((int)154, (int)30, (int)378, (int)214, (int)-1442840576);
        StatusEffectsPage.drawRect((int)156, (int)32, (int)376, (int)212, (int)0x66000000);
        if (this.discovered.isEmpty()) {
            String s = this.distort(GuiContext.fmt((String)"bestiary.effects.none", (Object[])new Object[0]));
            this.drawString(this.fontRendererObj, s, 20, 38, 0xFFFFFF);
            String hint = this.distort(GuiContext.fmt((String)"bestiary.effects.select_hint", (Object[])new Object[0]));
            this.drawString(this.fontRendererObj, hint, 164, 40, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }
        if (this.selectedIndex >= 0 && this.selectedIndex < this.discovered.size() && (rel = this.selectedIndex - (firstRow = this.scroll / 14)) >= 0 && rel < this.visibleRows()) {
            int y0 = 32 + rel * 14;
            StatusEffectsPage.drawRect((int)14, (int)y0, (int)144, (int)(y0 + 14), (int)0x33FFFFFF);
        }
        if (this.selectedIndex >= 0 && this.selectedIndex < this.discovered.size()) {
            SRPStatusEffectRegistry.Entry e = this.discovered.get(this.selectedIndex);
            int x = 164;
            int y = 40;
            String name = this.distort(this.resolveEffectName(e));
            this.drawString(this.fontRendererObj, name, x, y, 0xFFFFFF);
            this.drawString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.effects.id", (Object[])new Object[0]) + ": " + e.id), x, y += 14, 0xFFFFFF);
            StatusEffectsPage.drawRect((int)(this.getDetailTextX() - 2), (int)(this.getDetailTextY() - 2), (int)(this.getDetailTextX() + this.getDetailTextW() + 2), (int)(this.getDetailTextY() + this.getDetailTextH() + 2), (int)0x33000000);
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
            StatusEffectsPage.drawRect((int)trackX, (int)trackY, (int)(trackX + 8), (int)(trackY + trackH), (int)0x22000000);
            int thumbY = this.getDetailScrollbarThumbY();
            int thumbH = this.getDetailScrollbarThumbH();
            int thumbColor = this.hasScrollableDetail() ? (this.draggingDetailScrollbar ? -861230422 : -863467384) : 0x66444444;
            StatusEffectsPage.drawRect((int)trackX, (int)thumbY, (int)(trackX + 8), (int)(thumbY + thumbH), (int)thumbColor);
            this.drawPotionIconTopRight(e, 370, 38, 24);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawPotionIconTopRight(SRPStatusEffectRegistry.Entry e, int rightX, int topY, int sizePx) {
        if (e == null || e.potion == null || e.id == null) {
            return;
        }
        ResourceLocation tex = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/potion_" + e.id + ".png");
        Minecraft mc = Minecraft.getInstance();
        GuiContext.bind(tex);
        float timeSec = (float)GuiContext.systemTime() / 1000.0f;
        float maxTiltDeg = 6.0f;
        float speedHz = 0.2f;
        float angle = Mth.sin((float)(timeSec * ((float)Math.PI * 2) * speedHz)) * maxTiltDeg;
        int x = rightX - sizePx;
        int y = topY;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((GlStateManager.SourceFactor)GlStateManager.SourceFactor.SRC_ALPHA, (GlStateManager.DestFactor)GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, (GlStateManager.SourceFactor)GlStateManager.SourceFactor.ONE, (GlStateManager.DestFactor)GlStateManager.DestFactor.ZERO);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.translate((float)((float)x + (float)sizePx / 2.0f), (float)((float)y + (float)sizePx / 2.0f), (float)0.0f);
        GlStateManager.rotate((float)angle, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((float)((float)(-sizePx) / 2.0f), (float)((float)(-sizePx) / 2.0f), (float)0.0f);
        StatusEffectsPage.drawModalRectWithCustomSizedTexture((int)0, (int)0, (float)0.0f, (float)0.0f, (int)sizePx, (int)sizePx, (float)sizePx, (float)sizePx);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }

    private String resolveEffectName(SRPStatusEffectRegistry.Entry e) {
        String bestiaryKey = "bestiary.effect." + e.id + ".name";
        if (GuiContext.hasKey((String)bestiaryKey)) {
            return GuiContext.fmt((String)bestiaryKey, (Object[])new Object[0]);
        }
        if (e.potion != null && net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(e.potion) != null) {
            String mobEffectKey = "mob_effect." + net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.getKey(e.potion).toString();
            if (GuiContext.hasKey((String)mobEffectKey)) {
                return GuiContext.fmt((String)mobEffectKey, (Object[])new Object[0]);
            }
            String maybeKey = e.potion.getDescriptionId();
            if (maybeKey != null && GuiContext.hasKey((String)maybeKey)) {
                return GuiContext.fmt((String)maybeKey, (Object[])new Object[0]);
            }
        }
        return e.id;
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0 && this.getMaxScroll() > 0 && this.isMouseOverScrollbar(mouseX, mouseY)) {
            int thumbY = this.getScrollbarThumbY();
            int thumbH = this.getScrollbarThumbHeight();
            if (mouseY >= thumbY && mouseY < thumbY + thumbH) {
                this.draggingScrollbar = true;
                this.scrollbarDragOffset = mouseY - thumbY;
            } else {
                int newThumbTop = mouseY - thumbH / 2;
                this.setScrollFromThumbTop(newThumbTop);
                this.draggingScrollbar = true;
                this.scrollbarDragOffset = thumbH / 2;
            }
            return;
        }
        if (mouseButton == 0 && this.hasScrollableDetail() && this.isMouseOver(this.getDetailScrollbarX(), this.getDetailScrollbarY(), this.getDetailScrollbarX() + 8, this.getDetailScrollbarY() + this.getDetailScrollbarH(), mouseX, mouseY)) {
            int thumbY = this.getDetailScrollbarThumbY();
            int thumbH = this.getDetailScrollbarThumbH();
            if (mouseY >= thumbY && mouseY < thumbY + thumbH) {
                this.draggingDetailScrollbar = true;
                this.detailDragGrabOffset = mouseY - thumbY;
            } else {
                int newThumbTop = mouseY - thumbH / 2;
                this.setDetailScrollFromThumbTop(newThumbTop);
                this.draggingDetailScrollbar = true;
                this.detailDragGrabOffset = thumbH / 2;
            }
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            this.draggingScrollbar = false;
            this.draggingDetailScrollbar = false;
        }
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        int thumbTop;
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.draggingScrollbar && this.getMaxScroll() > 0) {
            thumbTop = mouseY - this.scrollbarDragOffset;
            this.setScrollFromThumbTop(thumbTop);
        }
        if (this.draggingDetailScrollbar && this.hasScrollableDetail()) {
            thumbTop = mouseY - this.detailDragGrabOffset;
            this.setDetailScrollFromThumbTop(thumbTop);
        }
    }
}

