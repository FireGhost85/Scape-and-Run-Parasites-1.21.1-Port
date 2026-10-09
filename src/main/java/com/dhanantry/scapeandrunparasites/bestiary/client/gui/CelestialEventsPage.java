package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectDefinition;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialObjectRegistry;
import com.dhanantry.scapeandrunparasites.client.celestial.CelestialPhaseClient;
import com.dhanantry.scapeandrunparasites.client.gui.StarfieldBackground;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class CelestialEventsPage
extends GuiScreen {
    private final Player player;
    private final GuiScreen parent;
    private StarfieldBackground starfield;
    private boolean isJumbled;
    private final List<CelestialObjectDefinition> discovered = new ArrayList<CelestialObjectDefinition>();
    private int scroll = 0;
    private int selectedIndex = -1;
    private boolean draggingScrollbar = false;
    private int scrollbarDragOffset = 0;
    private int detailScrollPx = 0;
    private boolean draggingDetailScrollbar = false;
    private int detailDragGrabOffset = 0;
    private static final int TOP = 28;
    private static final int LIST_X = 16;
    private static final int LIST_Y = 38;
    private static final int LIST_W = 150;
    private static final int LIST_H = 180;
    private static final int ROW_H = 18;
    private static final int DETAIL_X = 182;
    private static final int DETAIL_Y = 38;
    private static final int DETAIL_W = 220;
    private static final int DETAIL_H = 180;
    private static final int SCROLLBAR_W = 6;
    private static final int SCROLLBAR_GAP = 6;
    private static final int SCROLLBAR_X = 4;
    private static final int SCROLLBAR_Y = 38;
    private static final int SCROLLBAR_H = 180;
    private static final int MIN_THUMB_H = 18;
    private static final int DETAIL_PAD = 12;
    private static final int DETAIL_SCROLLBAR_W = 8;
    private static final int DETAIL_SCROLLBAR_GAP = 4;
    private static final int DETAIL_LINE_H = 10;
    private static final int DETAIL_MIN_THUMB_H = 12;
    private static final int DETAIL_HEADER_H = 62;
    private static final int DETAIL_TEXT_LEFT_INSET = 4;
    private static final int DETAIL_TEXT_RIGHT_INSET = 2;
    private static final int DETAIL_TEXT_TOP_INSET = 2;
    private static final int DETAIL_TEXT_BOTTOM_INSET = 2;
    private static final int DETAIL_SPRITE_SIZE = 72;
    private static final int DETAIL_SPRITE_BOTTOM_PAD = 8;
    private static final int DETAIL_SPRITE_AREA_H = 88;
    private static final int ACTIVE_ORB_SIZE = 10;
    private static final int ACTIVE_ORB_MARGIN_LEFT = 4;
    private static final int COUNTER_Y = 226;
    private static final int COUNTER_W = 150;
    private static final ResourceLocation JUMBLED_ICON = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/question_mark_small.png");
    private static final ResourceLocation DARK_DAYS_REDACTED_ICON = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/abhorrence.png");
    private static final ResourceLocation EXP_ORB_TEX = ResourceLocation.parse("textures/entity/experience_orb.png");

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    private boolean isDarkDaysActive() {
        if (this.mc == null || this.mc.level == null) {
            return false;
        }
        if (!this.mc.level.dimensionType().natural()) {
            return false;
        }
        String dim = DimKeys.of(this.mc.level);
        return CelestialPhaseClient.getActiveIds(dim).contains("dark_days") || CelestialPhaseClient.getForcedIds(dim).contains("dark_days");
    }

    private boolean shouldDarkDaysRedact(CelestialObjectDefinition def) {
        if (def == null) {
            return false;
        }
        if ("dark_days".equals(def.id)) {
            return false;
        }
        return this.isDarkDaysActive();
    }

    private String darkDaysName() {
        return ChatFormatting.WHITE + "[ ? ? ? ]";
    }

    private String darkDaysDesc() {
        return ChatFormatting.WHITE + "[ " + GuiContext.fmt((String)"bestiary.celestial.out_of_range", (Object[])new Object[0]) + " ]";
    }

    private String darkDaysBlocks() {
        return ChatFormatting.WHITE + "\u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2588 \u2588\u2588\u2588\u2588 \u2588\u2588\u2588\u2588\u2588\u2588\u2588";
    }

    private String displayNameFor(CelestialObjectDefinition def) {
        if (this.shouldDarkDaysRedact(def)) {
            return this.darkDaysName();
        }
        String nameKey = "bestiary.celestial." + def.id + ".name";
        return this.distort(GuiContext.hasKey((String)nameKey) ? GuiContext.fmt((String)nameKey, (Object[])new Object[0]) : def.id);
    }

    private String displayDetailLineFor(CelestialObjectDefinition def, String normalText) {
        if (this.shouldDarkDaysRedact(def)) {
            return this.darkDaysBlocks();
        }
        return this.distort(normalText);
    }

    private int getTotalCelestialCount() {
        return CelestialObjectRegistry.getObjectCount();
    }

    public CelestialEventsPage(Player player, GuiScreen parent) {
        this.player = player;
        this.parent = parent;
    }

    public void initGui() {
        super.initGui();
        if (this.starfield == null) {
            this.starfield = new StarfieldBackground(this.mc);
        }
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, 10, 10, 60, 20, this.distort("< " + GuiContext.fmt((String)"bestiary.celestial.home", (Object[])new Object[0]))));
        // the list buttons are lost whenever the screen is initialised again (resize, coming back from another page)
        this.syncDiscovered();
        this.rebuildListButtons();
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.dhanantry.scapeandrunparasites.network.BestiaryRequestPayload());
    }

    private void syncDiscovered() {
        this.discovered.clear();
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        if (prog == null) {
            return;
        }
        for (CelestialObjectDefinition def : CelestialObjectRegistry.getObjects()) {
            if ("dark_days".equals(def.id) && !SRPConfigWorld.darkDaysEnabled || !prog.hasSeenCelestial(def.id)) continue;
            this.discovered.add(def);
        }
        Collections.sort(this.discovered, new Comparator<CelestialObjectDefinition>(){

            @Override
            public int compare(CelestialObjectDefinition a, CelestialObjectDefinition b) {
                return a.id.compareToIgnoreCase(b.id);
            }
        });
        if (this.selectedIndex >= this.discovered.size()) {
            int n = this.selectedIndex = this.discovered.isEmpty() ? -1 : 0;
        }
        if (this.selectedIndex < 0 && !this.discovered.isEmpty()) {
            this.selectedIndex = 0;
        }
        this.detailScrollPx = 0;
        this.draggingDetailScrollbar = false;
    }

    private int getMaxScroll() {
        int contentH = this.discovered.size() * 18;
        int max = contentH - 180;
        return Math.max(0, max);
    }

    private int visibleRows() {
        return 10;
    }

    private int getScrollbarThumbHeight() {
        int contentH = Math.max(1, this.discovered.size() * 18);
        if (contentH <= 180) {
            return 180;
        }
        int h = (int)(180.0f / (float)contentH * 180.0f);
        return Math.max(18, Math.min(180, h));
    }

    private int getScrollbarThumbY() {
        int max = this.getMaxScroll();
        if (max <= 0) {
            return 38;
        }
        int thumbH = this.getScrollbarThumbHeight();
        int trackRange = 180 - thumbH;
        if (trackRange <= 0) {
            return 38;
        }
        float t = (float)this.scroll / (float)max;
        return 38 + Math.round(t * (float)trackRange);
    }

    private boolean isMouseOverScrollbar(int mouseX, int mouseY) {
        return mouseX >= 4 && mouseX < 10 && mouseY >= 38 && mouseY < 218;
    }

    private void setScrollFromThumbTop(int thumbTop) {
        int trackRange;
        int max = this.getMaxScroll();
        if (max <= 0) {
            this.scroll = 0;
            return;
        }
        int thumbH = this.getScrollbarThumbHeight();
        int trackMin = 38;
        int trackMax = 218 - thumbH;
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
    }

    private void rebuildListButtons() {
        int idx;
        while (this.buttonList.size() > 1) {
            this.buttonList.remove(this.buttonList.size() - 1);
        }
        int firstRow = this.scroll / 18;
        int rows = this.visibleRows();
        for (int i = 0; i < rows && (idx = firstRow + i) >= 0 && idx < this.discovered.size(); ++i) {
            CelestialObjectDefinition def = this.discovered.get(idx);
            String label = this.displayNameFor(def);
            int y = 38 + i * 18;
            GuiButton b = new GuiButton(100 + idx, 16, y, 150, 18, label);
            this.buttonList.add(b);
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        int idx;
        if (button.id == 1) {
            GuiContext.click();
            this.mc.setScreen(this.parent);
            return;
        }
        if (button.id >= 100 && (idx = button.id - 100) >= 0 && idx < this.discovered.size()) {
            this.selectedIndex = idx;
            this.detailScrollPx = 0;
            this.draggingDetailScrollbar = false;
            GuiContext.click();
        }
    }

    public void handleMouseInput() throws IOException {
        int dir;
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel == 0) {
            return;
        }
        int mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
        int my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
        boolean overList = this.isMouseOver(16, 38, 166, 218, mx, my);
        boolean overDetailText = this.isMouseOver(this.getDetailTextScissorX(), this.getDetailTextScissorY(), this.getDetailTextScissorX() + this.getDetailTextScissorW(), this.getDetailTextScissorY() + this.getDetailTextScissorH(), mx, my);
        boolean overDetailScrollbar = this.isMouseOver(this.getDetailScrollbarX(), this.getDetailScrollbarY(), this.getDetailScrollbarX() + 8, this.getDetailScrollbarY() + this.getDetailScrollbarH(), mx, my);
        int n = dir = dWheel > 0 ? -1 : 1;
        if (overDetailText || overDetailScrollbar) {
            this.scrollDetail(dir);
        } else {
            this.scroll = Math.max(0, Math.min(this.getMaxScroll(), this.scroll + dir * 18));
            this.rebuildListButtons();
        }
    }

    private boolean isMouseOver(int x1, int y1, int x2, int y2, int mx, int my) {
        return mx >= x1 && mx < x2 && my >= y1 && my < y2;
    }

    private int getDetailTextX() {
        return 198;
    }

    private int getDetailTextY() {
        return 102;
    }

    private int getDetailTextW() {
        return 178;
    }

    private int getDetailTextH() {
        return 26;
    }

    private int getDetailTextScissorX() {
        return 194;
    }

    private int getDetailTextScissorY() {
        return 100;
    }

    private int getDetailTextScissorW() {
        return 184;
    }

    private int getDetailTextScissorH() {
        return 30;
    }

    private int getDetailScrollbarX() {
        return 382;
    }

    private int getDetailScrollbarY() {
        return this.getDetailTextScissorY();
    }

    private int getDetailScrollbarH() {
        return this.getDetailTextScissorH();
    }

    private List<String> getDetailWrappedLines() {
        ArrayList<String> lines = new ArrayList<String>();
        if (this.selectedIndex < 0 || this.selectedIndex >= this.discovered.size()) {
            return lines;
        }
        CelestialObjectDefinition def = this.discovered.get(this.selectedIndex);
        if (this.shouldDarkDaysRedact(def)) {
            String desc = ChatFormatting.ITALIC + this.darkDaysDesc();
            lines.addAll(this.fontRendererObj.listFormattedStringToWidth(desc, this.getDetailTextW()));
            lines.add(ChatFormatting.WHITE + " ");
            lines.addAll(this.fontRendererObj.listFormattedStringToWidth(this.darkDaysBlocks(), this.getDetailTextW()));
            lines.addAll(this.fontRendererObj.listFormattedStringToWidth(this.darkDaysBlocks(), this.getDetailTextW()));
            lines.addAll(this.fontRendererObj.listFormattedStringToWidth(this.darkDaysBlocks(), this.getDetailTextW()));
            return lines;
        }
        String descKey = "bestiary.celestial." + def.id + ".desc";
        if (GuiContext.hasKey((String)descKey)) {
            String desc = this.distort(GuiContext.fmt((String)descKey, (Object[])new Object[0]));
            desc = ChatFormatting.ITALIC + desc;
            lines.addAll(this.fontRendererObj.listFormattedStringToWidth(desc, this.getDetailTextW()));
        } else {
            lines.add(ChatFormatting.ITALIC + this.distort(GuiContext.fmt((String)"bestiary.celestial.missing_desc", (Object[])new Object[0])));
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

    private void scrollDetail(int dir) {
        int maxScroll = this.getDetailMaxScrollPx();
        this.detailScrollPx = Math.max(0, Math.min(maxScroll, this.detailScrollPx + dir * 10));
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

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int idx;
        int thumbTop;
        this.drawDefaultBackground();
        if (this.starfield != null) {
            this.starfield.render(mouseX, mouseY, partialTicks, this.isJumbled, this.isDarkDaysActive());
        } else {
            this.drawDefaultBackground();
        }
        if (this.draggingScrollbar) {
            thumbTop = mouseY - this.scrollbarDragOffset;
            this.setScrollFromThumbTop(thumbTop);
            this.rebuildListButtons();
        }
        if (this.draggingDetailScrollbar) {
            thumbTop = mouseY - this.detailDragGrabOffset;
            this.setDetailScrollFromThumbTop(thumbTop);
        }
        String title = this.distort(GuiContext.fmt((String)"bestiary.celestial.title", (Object[])new Object[0]));
        this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 12, 0xFFFFFF);
        CelestialEventsPage.drawRect((int)14, (int)36, (int)168, (int)220, (int)-1442840576);
        CelestialEventsPage.drawRect((int)16, (int)38, (int)166, (int)218, (int)0x66000000);
        CelestialEventsPage.drawRect((int)3, (int)37, (int)11, (int)219, (int)-1442840576);
        CelestialEventsPage.drawRect((int)4, (int)38, (int)10, (int)218, (int)0x44000000);
        if (this.getMaxScroll() > 0) {
            int thumbY = this.getScrollbarThumbY();
            int thumbH = this.getScrollbarThumbHeight();
            CelestialEventsPage.drawRect((int)4, (int)thumbY, (int)10, (int)(thumbY + thumbH), (int)-1716868438);
            CelestialEventsPage.drawRect((int)5, (int)(thumbY + 1), (int)9, (int)(thumbY + thumbH - 1), (int)-857874979);
        }
        if (this.discovered.isEmpty()) {
            String s = this.distort(GuiContext.fmt((String)"bestiary.celestial.none", (Object[])new Object[0]));
            this.drawString(this.fontRendererObj, s, 22, 44, 0xFFFFFF);
        }
        CelestialEventsPage.drawRect((int)180, (int)36, (int)404, (int)220, (int)-1442840576);
        CelestialEventsPage.drawRect((int)182, (int)38, (int)402, (int)218, (int)0x66000000);
        if (this.selectedIndex >= 0 && this.selectedIndex < this.discovered.size()) {
            CelestialObjectDefinition def = this.discovered.get(this.selectedIndex);
            int x = 194;
            int y = 50;
            String name = this.displayNameFor(def);
            this.drawString(this.fontRendererObj, this.shouldDarkDaysRedact(def) ? name : ChatFormatting.AQUA + name, x, y, 0xFFFFFF);
            this.drawString(this.fontRendererObj, this.displayDetailLineFor(def, GuiContext.fmt((String)"bestiary.celestial.id", (Object[])new Object[0]) + ": " + def.id), x, y += 14, 0xFFFFFF);
            this.drawString(this.fontRendererObj, this.displayDetailLineFor(def, GuiContext.fmt((String)"bestiary.celestial.phase", (Object[])new Object[0]) + ": " + def.minPhase + " - " + def.maxPhase), x, y += 12, 0xFFFFFF);
            y += 12;
            float pct = def.chancePerNight * 100.0f;
            String pctStr = String.format(Locale.US, "%.2f", Float.valueOf(pct));
            float chanceT = def.chancePerNight;
            if (chanceT < 0.0f) {
                chanceT = 0.0f;
            }
            if (chanceT > 1.0f) {
                chanceT = 1.0f;
            }
            int red = (int)(255.0f * (1.0f - chanceT));
            int green = (int)(255.0f * chanceT);
            int blue = 70;
            int chanceColor = red << 16 | green << 8 | blue;
            this.drawString(this.fontRendererObj, this.displayDetailLineFor(def, GuiContext.fmt((String)"bestiary.celestial.chance", (Object[])new Object[0]) + ": " + pctStr + "%"), x, y, this.shouldDarkDaysRedact(def) ? 0xFFFFFF : chanceColor);
            CelestialEventsPage.drawRect((int)(this.getDetailTextScissorX() - 2), (int)(this.getDetailTextScissorY() - 2), (int)(this.getDetailTextScissorX() + this.getDetailTextScissorW() + 2), (int)(this.getDetailTextScissorY() + this.getDetailTextScissorH() + 2), (int)0x33000000);
            int spriteAreaTop = 130;
            CelestialEventsPage.drawRect((int)192, (int)spriteAreaTop, (int)392, (int)212, (int)0x22000000);
            List<String> lines = this.getDetailWrappedLines();
            this.enableScissor(this.getDetailTextScissorX(), this.getDetailTextScissorY(), this.getDetailTextScissorW(), this.getDetailTextScissorH());
            int yDraw = this.getDetailTextY() - this.detailScrollPx;
            for (String line : lines) {
                if (yDraw > this.getDetailTextScissorY() + this.getDetailTextScissorH()) break;
                if (yDraw + 10 >= this.getDetailTextScissorY()) {
                    this.drawString(this.fontRendererObj, line, this.getDetailTextX(), yDraw, 0xB8B8B8);
                }
                yDraw += 10;
            }
            this.disableScissor();
            int trackX = this.getDetailScrollbarX();
            int trackY = this.getDetailScrollbarY();
            int trackH = this.getDetailScrollbarH();
            CelestialEventsPage.drawRect((int)trackX, (int)trackY, (int)(trackX + 8), (int)(trackY + trackH), (int)0x22000000);
            int detailThumbY = this.getDetailScrollbarThumbY();
            int detailThumbH = this.getDetailScrollbarThumbH();
            int thumbColor = this.hasScrollableDetail() ? (this.draggingDetailScrollbar ? -861230422 : -863467384) : 0x66444444;
            CelestialEventsPage.drawRect((int)trackX, (int)detailThumbY, (int)(trackX + 8), (int)(detailThumbY + detailThumbH), (int)thumbColor);
            int cx = 292;
            int cy = 174;
            this.drawCelestialSprite(def, cx, cy, 72);
        } else {
            String hint = this.distort(GuiContext.fmt((String)"bestiary.celestial.select_hint", (Object[])new Object[0]));
            this.drawString(this.fontRendererObj, hint, 190, 46, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        String counter = this.distort(GuiContext.fmt((String)"bestiary.celestial.counter", (Object[])new Object[]{this.discovered.size(), this.getTotalCelestialCount()}));
        this.drawCenteredString(this.fontRendererObj, counter, 91, 226, 0xCCCCCC);
        int hoveredOrbIndex = -1;
        int firstRow = this.scroll / 18;
        for (int i = 0; i < this.visibleRows() && (idx = firstRow + i) >= 0 && idx < this.discovered.size(); ++i) {
            CelestialObjectDefinition def = this.discovered.get(idx);
            if (!this.isCelestialEventActiveTonight(def)) continue;
            int rowY = 38 + i * 18;
            int orbX = 20;
            int orbY = rowY + 4;
            this.drawActiveOrb(orbX, orbY, 10);
            if (!this.isMouseOverActiveOrb(mouseX, mouseY, orbX, orbY, 10)) continue;
            hoveredOrbIndex = idx;
        }
        if (hoveredOrbIndex >= 0 && hoveredOrbIndex < this.discovered.size()) {
            ArrayList<String> tooltip = new ArrayList<String>();
            CelestialObjectDefinition hovered = this.discovered.get(hoveredOrbIndex);
            tooltip.add(this.shouldDarkDaysRedact(hovered) ? this.darkDaysBlocks() : this.distort(GuiContext.fmt((String)"bestiary.celestial.ongoing", (Object[])new Object[0])));
            this.drawHoveringText(tooltip, mouseX, mouseY);
        }
    }

    public void refreshFromCapability() {
        this.syncDiscovered();
        this.rebuildListButtons();
    }

    private void drawCelestialSprite(CelestialObjectDefinition def, int centerX, int centerY, int sizePx) {
        ResourceLocation tex;
        if (def == null) {
            return;
        }
        if (this.shouldDarkDaysRedact(def)) {
            tex = DARK_DAYS_REDACTED_ICON;
        } else {
            ResourceLocation resourceLocation = tex = this.isJumbled ? JUMBLED_ICON : def.texture;
        }
        if (tex == null) {
            return;
        }
        int frames = 1;
        int frame = 0;
        if (!this.isJumbled && !this.shouldDarkDaysRedact(def)) {
            frames = Math.max(1, def.frameCount);
            int fTime = Math.max(1, def.frameTimeTicks);
            if (def.animated && frames > 1) {
                int t = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.tickCount : 0;
                frame = t / fTime % frames;
            }
        }
        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = (float)frame / (float)frames;
        float v1 = (float)(frame + 1) / (float)frames;
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.translate((float)centerX, (float)centerY, (float)0.0f);
        GlStateManager.translate((float)((float)(-sizePx) / 2.0f), (float)((float)(-sizePx) / 2.0f), (float)0.0f);
        GuiContext.bind(tex);
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder bb = tess.getWorldRenderer();
        bb.begin(7, Tessellator.VertexFormat.POSITION_TEX);
        bb.pos(0.0, (double)sizePx, 0.0).tex((double)u0, (double)v1).endVertex();
        bb.pos((double)sizePx, (double)sizePx, 0.0).tex((double)u1, (double)v1).endVertex();
        bb.pos((double)sizePx, 0.0, 0.0).tex((double)u1, (double)v0).endVertex();
        bb.pos(0.0, 0.0, 0.0).tex((double)u0, (double)v0).endVertex();
        tess.draw();
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private boolean isCelestialEventActiveTonight(CelestialObjectDefinition def) {
        if (def == null || this.mc == null || this.mc.level == null) {
            return false;
        }
        if (!this.mc.level.dimensionType().natural()) {
            return false;
        }
        String dim = DimKeys.of(this.mc.level);
        int phase = CelestialPhaseClient.getPhase(dim);
        boolean isForced = CelestialPhaseClient.isForcedTonight(dim, def.id);
        if (isForced) {
            return true;
        }
        if (!def.isPhaseAllowed(phase)) {
            return false;
        }
        return CelestialPhaseClient.isActiveTonight(dim, def.id);
    }

    private void drawActiveOrb(int x, int y, int size) {
        GuiContext.bind(EXP_ORB_TEX);
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        float u0 = 0.0f;
        float u1 = 0.25f;
        float v0 = 0.0f;
        float v1 = 0.25f;
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder bb = tess.getWorldRenderer();
        bb.begin(7, Tessellator.VertexFormat.POSITION_TEX);
        bb.pos((double)x, (double)(y + size), 0.0).tex((double)u0, (double)v1).endVertex();
        bb.pos((double)(x + size), (double)(y + size), 0.0).tex((double)u1, (double)v1).endVertex();
        bb.pos((double)(x + size), (double)y, 0.0).tex((double)u1, (double)v0).endVertex();
        bb.pos((double)x, (double)y, 0.0).tex((double)u0, (double)v0).endVertex();
        tess.draw();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private boolean isMouseOverActiveOrb(int mouseX, int mouseY, int orbX, int orbY, int orbSize) {
        return mouseX >= orbX && mouseX < orbX + orbSize && mouseY >= orbY && mouseY < orbY + orbSize;
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
                this.rebuildListButtons();
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
            this.rebuildListButtons();
        }
        if (this.draggingDetailScrollbar && this.hasScrollableDetail()) {
            thumbTop = mouseY - this.detailDragGrabOffset;
            this.setDetailScrollFromThumbTop(thumbTop);
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}

