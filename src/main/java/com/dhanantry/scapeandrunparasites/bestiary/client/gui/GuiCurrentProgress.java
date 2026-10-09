package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.CurrentProgressClientCache;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.network.ReconfigureProgressUnlockPayload;
import com.dhanantry.scapeandrunparasites.network.RequestProgressSnapshotPayload;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class GuiCurrentProgress
extends GuiScreen {
    private static final ResourceLocation TEX_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/current_progress_gui.png");
    private static final int BG_W = 480;
    private static final int BG_H = 300;
    private static final int BTN_BACK = 0;
    private static final int BTN_REFRESH = 1;
    private static final int BTN_RECONFIGURE = 2;
    private static final int BTN_PRIOR_PHASES = 3;
    private static final int BTN_PRIOR_UD = 4;
    private final Player player;
    private final GuiScreen parent;
    private boolean isJumbled;
    private float uiScale = 1.0f;
    private int screenPanelX;
    private int screenPanelY;
    private int screenPanelW;
    private int screenPanelH;
    private int listX;
    private int listY;
    private int listW;
    private int listH;
    private int phaseSlotX;
    private int phaseSlotY;
    private int udSlotX;
    private int udSlotY;
    private int phaseCardX;
    private int phaseCardY;
    private int phaseCardW;
    private int phaseCardH;
    private int udCardX;
    private int udCardY;
    private int udCardW;
    private int udCardH;
    private int warningX;
    private int warningY;
    private int warningW;
    private boolean phaseClockInserted = false;
    private boolean udClockInserted = false;
    private boolean showPriorPhases = false;
    private boolean showPriorUD = false;
    private int scroll = 0;
    private final List<Line> lines = new ArrayList<Line>();
    private final List<HoverTerm> hoverTerms = new ArrayList<HoverTerm>();
    private final List<HoverWord> hoverWords = new ArrayList<HoverWord>();

    public GuiCurrentProgress(Player player, GuiScreen parent) {
        this.player = player;
        this.parent = parent;
    }

    private String tr(String key, Object ... args) {
        return GuiContext.fmt((String)key, (Object[])args);
    }

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    private boolean needsUnlockUi() {
        return !CurrentProgressClientCache.phaseUnlocked || !CurrentProgressClientCache.udUnlocked;
    }

    public void initGui() {
        super.initGui();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        CurrentProgressClientCache.hasData = false;
        PacketDistributor.sendToServer(new RequestProgressSnapshotPayload());
        int maxW = this.width - 24;
        int maxH = this.height - 24;
        float s = Math.min((float)maxW / 480.0f, (float)maxH / 300.0f);
        this.uiScale = Math.min(1.0f, s);
        this.uiScale = Math.max(0.25f, this.uiScale);
        this.screenPanelW = Math.round(480.0f * this.uiScale);
        this.screenPanelH = Math.round(300.0f * this.uiScale);
        this.screenPanelX = (this.width - this.screenPanelW) / 2;
        this.screenPanelY = (this.height - this.screenPanelH) / 2;
        this.applyLayout();
        this.scroll = 0;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.sx(10), this.sy(276), this.sw(88), this.sh(20), this.distort(this.tr("bestiary.nav.back", new Object[0]))));
        this.buttonList.add(new GuiButton(2, this.sx(180), this.sy(276), this.sw(120), this.sh(20), this.distort(this.tr("bestiary.progress.reconfigure", new Object[0]))));
        this.buttonList.add(new GuiButton(1, this.sx(382), this.sy(276), this.sw(88), this.sh(20), this.distort(this.tr("bestiary.progress.refresh", new Object[0]))));
        this.buttonList.add(new GuiButton(3, this.sx(22), this.sy(this.needsUnlockUi() ? 74 : 32), this.sw(210), this.sh(18), this.distort(this.tr(this.showPriorPhases ? "bestiary.progress.prior_phases.hide" : "bestiary.progress.prior_phases.show", new Object[0]))));
        this.buttonList.add(new GuiButton(4, this.sx(248), this.sy(this.needsUnlockUi() ? 74 : 32), this.sw(210), this.sh(18), this.distort(this.tr(this.showPriorUD ? "bestiary.progress.prior_ud.hide" : "bestiary.progress.prior_ud.show", new Object[0]))));
        this.rebuildHoverTerms();
        this.rebuildLines();
        this.updateButtonStates();
    }

    private void applyLayout() {
        if (this.needsUnlockUi()) {
            this.phaseCardX = 22;
            this.phaseCardY = 28;
            this.phaseCardW = 210;
            this.phaseCardH = 22;
            this.udCardW = 210;
            this.udCardH = 22;
            this.udCardX = 458 - this.udCardW;
            this.udCardY = 28;
            this.phaseSlotX = this.phaseCardX + 4;
            this.phaseSlotY = this.phaseCardY + 2;
            this.udSlotX = this.udCardX + 4;
            this.udSlotY = this.udCardY + 2;
            this.warningX = 22;
            this.warningY = 52;
            this.warningW = 436;
            this.listX = 22;
            this.listY = 98;
            this.listW = 436;
            this.listH = 160;
        } else {
            this.phaseCardX = 0;
            this.phaseCardY = 0;
            this.phaseCardW = 0;
            this.phaseCardH = 0;
            this.udCardX = 0;
            this.udCardY = 0;
            this.udCardW = 0;
            this.udCardH = 0;
            this.phaseSlotX = 0;
            this.phaseSlotY = 0;
            this.udSlotX = 0;
            this.udSlotY = 0;
            this.warningX = 0;
            this.warningY = 0;
            this.warningW = 0;
            this.listX = 22;
            this.listY = 56;
            this.listW = 436;
            this.listH = 202;
        }
    }

    private int sx(int designX) {
        return this.screenPanelX + Math.round((float)designX * this.uiScale);
    }

    private int sy(int designY) {
        return this.screenPanelY + Math.round((float)designY * this.uiScale);
    }

    private int sw(int designW) {
        return Math.max(1, Math.round((float)designW * this.uiScale));
    }

    private int sh(int designH) {
        return Math.max(1, Math.round((float)designH * this.uiScale));
    }

    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        this.initGui();
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            this.mc.setScreen(this.parent);
            return;
        }
        if (button.id == 1) {
            CurrentProgressClientCache.hasData = false;
            PacketDistributor.sendToServer(new RequestProgressSnapshotPayload());
            this.scroll = 0;
            this.rebuildLines();
            this.updateButtonStates();
            return;
        }
        if (button.id == 3) {
            this.showPriorPhases = !this.showPriorPhases;
            button.displayString = this.distort(this.tr(this.showPriorPhases ? "bestiary.progress.prior_phases.hide" : "bestiary.progress.prior_phases.show", new Object[0]));
            this.scroll = 0;
            this.rebuildLines();
            return;
        }
        if (button.id == 4) {
            this.showPriorUD = !this.showPriorUD;
            button.displayString = this.distort(this.tr(this.showPriorUD ? "bestiary.progress.prior_ud.hide" : "bestiary.progress.prior_ud.show", new Object[0]));
            this.scroll = 0;
            this.rebuildLines();
            return;
        }
        if (button.id == 2 && (this.phaseClockInserted || this.udClockInserted)) {
            PacketDistributor.sendToServer(new ReconfigureProgressUnlockPayload(this.phaseClockInserted, this.udClockInserted));
            this.phaseClockInserted = false;
            this.udClockInserted = false;
            this.updateButtonStates();
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int dx = this.toDesignX(mouseX);
        int dy = this.toDesignY(mouseY);
        if (mouseButton == 0 && this.needsUnlockUi()) {
            if (this.isOverSlot(dx, dy, this.phaseSlotX, this.phaseSlotY) && !CurrentProgressClientCache.phaseUnlocked) {
                if (this.phaseClockInserted || this.playerHasItem("srparasites:evclock")) {
                    this.phaseClockInserted = !this.phaseClockInserted;
                    this.updateButtonStates();
                }
                return;
            }
            if (this.isOverSlot(dx, dy, this.udSlotX, this.udSlotY) && !CurrentProgressClientCache.udUnlocked) {
                if (this.udClockInserted || this.playerHasItem("srparasites:levelclock")) {
                    this.udClockInserted = !this.udClockInserted;
                    this.updateButtonStates();
                }
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private boolean playerHasItem(String itemId) {
        Item wanted = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (wanted == null || this.player == null) {
            return false;
        }
        for (int i = 0; i < this.player.getInventory().items.size(); ++i) {
            ItemStack stack = (ItemStack)this.player.getInventory().items.get(i);
            if (stack.isEmpty() || stack.getItem() != wanted) continue;
            return true;
        }
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == 1) {
            this.mc.setScreen(this.parent);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    public void handleMouseInput() throws IOException {
        int dy;
        super.handleMouseInput();
        int dwheel = Mouse.getEventDWheel();
        if (dwheel == 0) {
            return;
        }
        int mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
        int my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
        int dx = this.toDesignX(mx);
        if (this.isInList(dx, dy = this.toDesignY(my))) {
            int maxScroll = this.getMaxScroll();
            this.scroll = dwheel < 0 ? Math.min(maxScroll, this.scroll + 14) : Math.max(0, this.scroll - 14);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        boolean oldJumbled = this.isJumbled;
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        if (oldJumbled != this.isJumbled) {
            for (GuiButton b : this.buttonList) {
                if (b.id == 0) {
                    b.displayString = this.distort(this.tr("bestiary.nav.back", new Object[0]));
                }
                if (b.id == 1) {
                    b.displayString = this.distort(this.tr("bestiary.progress.refresh", new Object[0]));
                }
                if (b.id == 2) {
                    b.displayString = this.distort(this.tr("bestiary.progress.reconfigure", new Object[0]));
                }
                if (b.id == 3) {
                    b.displayString = this.distort(this.tr(this.showPriorPhases ? "bestiary.progress.prior_phases.hide" : "bestiary.progress.prior_phases.show", new Object[0]));
                }
                if (b.id != 4) continue;
                b.displayString = this.distort(this.tr(this.showPriorUD ? "bestiary.progress.prior_ud.hide" : "bestiary.progress.prior_ud.show", new Object[0]));
            }
            this.rebuildHoverTerms();
        }
        this.applyLayout();
        this.rebuildLines();
        this.updateButtonStates();
        GL11.glDisable((int)3089);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.screenPanelX, (float)this.screenPanelY, (float)0.0f);
        GlStateManager.scale((float)this.uiScale, (float)this.uiScale, (float)1.0f);
        GuiContext.bind(TEX_BG);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
        GuiCurrentProgress.drawScaledCustomSizeModalRect((int)0, (int)0, (float)0.0f, (float)0.0f, (int)480, (int)300, (int)480, (int)300, (float)480.0f, (float)300.0f);
        GlStateManager.disableBlend();
        this.drawCenteredNoShadow(this.distort(this.tr("bestiary.progress.title", new Object[0])), 240, 9, -15658735);
        this.drawCenteredNoShadow("----------------", 240, 17, -2006568141);
        if (this.needsUnlockUi()) {
            this.drawUnlockArea();
        }
        this.drawListPanel();
        this.drawProgressList();
        GlStateManager.popMatrix();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawHover(mouseX, mouseY);
    }

    private void updateButtonStates() {
        boolean allUnlocked = CurrentProgressClientCache.phaseUnlocked && CurrentProgressClientCache.udUnlocked;
        boolean anythingInserted = this.phaseClockInserted || this.udClockInserted;
        for (GuiButton b : this.buttonList) {
            if (b.id == 2) {
                b.visible = !allUnlocked;
                boolean bl = b.enabled = !allUnlocked && anythingInserted;
            }
            if (b.id == 3) {
                b.displayString = this.distort(this.tr(this.showPriorPhases ? "bestiary.progress.prior_phases.hide" : "bestiary.progress.prior_phases.show", new Object[0]));
                b.x = this.sx(22);
                b.y = this.sy(this.needsUnlockUi() ? 74 : 32);
                b.width = this.sw(210);
                b.height = this.sh(18);
            }
            if (b.id != 4) continue;
            b.displayString = this.distort(this.tr(this.showPriorUD ? "bestiary.progress.prior_ud.hide" : "bestiary.progress.prior_ud.show", new Object[0]));
            b.x = this.sx(248);
            b.y = this.sy(this.needsUnlockUi() ? 74 : 32);
            b.width = this.sw(210);
            b.height = this.sh(18);
        }
    }

    private void drawUnlockArea() {
        this.drawUnlockCard(this.phaseCardX, this.phaseCardY, this.phaseCardW, this.phaseCardH, this.phaseSlotX, this.phaseSlotY, this.phaseClockInserted, "srparasites:evclock", CurrentProgressClientCache.phaseUnlocked, "bestiary.progress.slot.phase", "srparasites:evclock");
        this.drawUnlockCard(this.udCardX, this.udCardY, this.udCardW, this.udCardH, this.udSlotX, this.udSlotY, this.udClockInserted, "srparasites:levelclock", CurrentProgressClientCache.udUnlocked, "bestiary.progress.slot.ud", "srparasites:levelclock");
        if (!CurrentProgressClientCache.phaseUnlocked || !CurrentProgressClientCache.udUnlocked) {
            GuiCurrentProgress.drawRect((int)(this.warningX - 2), (int)(this.warningY - 2), (int)(this.warningX + this.warningW + 2), (int)(this.warningY + 18), (int)0x22000000);
            GuiCurrentProgress.drawRect((int)(this.warningX - 1), (int)(this.warningY - 1), (int)(this.warningX + this.warningW + 1), (int)(this.warningY + 17), (int)0x44FFFFFF);
            GuiCurrentProgress.drawRect((int)this.warningX, (int)this.warningY, (int)(this.warningX + this.warningW), (int)(this.warningY + 16), (int)0x20FFFFFF);
            this.resetTextRenderState();
            String warning = this.distort(this.tr("bestiary.progress.unlock_warning", new Object[0]));
            this.drawSplitNoShadow(warning, this.warningX + 4, this.warningY + 4, this.warningW - 8, -12312030);
        }
    }

    private void drawUnlockCard(int cardX, int cardY, int cardW, int cardH, int slotX, int slotY, boolean inserted, String itemId, boolean alreadyUnlocked, String labelKey, String requiredItemId) {
        String label;
        GuiCurrentProgress.drawRect((int)(cardX - 1), (int)(cardY - 1), (int)(cardX + cardW + 1), (int)(cardY + cardH + 1), (int)0x77000000);
        GuiCurrentProgress.drawRect((int)cardX, (int)cardY, (int)(cardX + cardW), (int)(cardY + cardH), (int)(alreadyUnlocked ? 0x22557755 : 0x16FFFFFF));
        boolean hasItem = this.playerHasItem(requiredItemId);
        int slotFill = alreadyUnlocked ? 0x66557755 : (inserted ? 0x66775555 : (hasItem ? 0x66DDDDDD : 0x66444444));
        GuiCurrentProgress.drawRect((int)(slotX - 1), (int)(slotY - 1), (int)(slotX + 20), (int)(slotY + 20), (int)-1442840576);
        GuiCurrentProgress.drawRect((int)slotX, (int)slotY, (int)(slotX + 19), (int)(slotY + 19), (int)slotFill);
        Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item != null && (inserted || alreadyUnlocked)) {
            RenderHelper.enableGUIStandardItemLighting();
            GuiScreen.renderItem(new ItemStack(item), slotX + 2, slotY + 2);
            RenderHelper.disableStandardItemLighting();
            this.resetTextRenderState();
        }
        String string = label = alreadyUnlocked ? this.distort(this.tr("bestiary.progress.slot.unlocked", new Object[0])) : this.distort(this.tr(labelKey, new Object[0]));
        int textColor = alreadyUnlocked ? -13413069 : (hasItem ? -14540254 : -11184811);
        this.drawSplitNoShadow(label, slotX + 26, cardY + 6, cardW - 32, textColor);
    }

    private void drawListPanel() {
        GuiCurrentProgress.drawRect((int)(this.listX - 2), (int)(this.listY - 2), (int)(this.listX + this.listW + 2), (int)(this.listY + this.listH + 2), (int)0x55000000);
        GuiCurrentProgress.drawRect((int)(this.listX - 1), (int)(this.listY - 1), (int)(this.listX + this.listW + 1), (int)(this.listY + this.listH + 1), (int)0x66FFFFFF);
        GuiCurrentProgress.drawRect((int)this.listX, (int)this.listY, (int)(this.listX + this.listW), (int)(this.listY + this.listH), (int)0x10FFFFFF);
    }

    private void rebuildLines() {
        this.lines.clear();
        if (!CurrentProgressClientCache.hasData) {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.loading", new Object[0])), 0x222222, true));
            return;
        }
        if (CurrentProgressClientCache.phaseUnlocked) {
            int phase = CurrentProgressClientCache.phase;
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.phase_header", phase)), 0x660000, true));
            this.addPhaseLines(phase);
            if (this.showPriorPhases) {
                this.lines.add(new Line("", 0x222222, false));
                this.lines.add(new Line(this.distort(this.tr("bestiary.progress.prior_phases.header", new Object[0])), 0x660000, true));
                this.addPriorPhaseLines(phase);
            }
        } else {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.phase_locked_header", new Object[0])), 0x660000, true));
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.phase_locked_body", new Object[0])), 0x222222, false));
        }
        this.lines.add(new Line("", 0x222222, false));
        if (CurrentProgressClientCache.udUnlocked) {
            int udl = CurrentProgressClientCache.udl;
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.ud_header", udl)), 0x660000, true));
            this.addUDLines(udl);
            if (this.showPriorUD) {
                this.lines.add(new Line("", 0x222222, false));
                this.lines.add(new Line(this.distort(this.tr("bestiary.progress.prior_ud.header", new Object[0])), 0x660000, true));
                this.addPriorUDLines(udl);
            }
        } else {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.ud_locked_header", new Object[0])), 0x660000, true));
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.ud_locked_body", new Object[0])), 0x222222, false));
        }
    }

    private void addPhaseLines(int phase) {
        switch (phase) {
            case 0: {
                this.bullet("bestiary.progress.phase.0.0", new Object[0]);
                this.bullet("bestiary.progress.phase.0.1", new Object[0]);
                break;
            }
            case 1: {
                this.bullet("bestiary.progress.phase.1.0", new Object[0]);
                this.bullet("bestiary.progress.phase.1.1", new Object[0]);
                break;
            }
            case 2: {
                this.bullet("bestiary.progress.phase.2.0", new Object[0]);
                this.bullet("bestiary.progress.phase.2.1", new Object[0]);
                break;
            }
            case 3: {
                this.bullet("bestiary.progress.phase.3.0", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseReinforcementChance));
                this.bullet("bestiary.progress.phase.3.1", GuiCurrentProgress.pct(CurrentProgressClientCache.beckonStageIGrowPenalty), GuiCurrentProgress.pct(CurrentProgressClientCache.beckonStageIIGrowPenalty), GuiCurrentProgress.pct(CurrentProgressClientCache.beckonStageIIIGrowPenalty));
                this.bullet("bestiary.progress.phase.3.2", GuiCurrentProgress.raw(CurrentProgressClientCache.phasePassiveGain));
                this.bullet("bestiary.progress.phase.3.3", new Object[0]);
                this.addExtraPhaseLines(3, 4);
                break;
            }
            case 4: {
                this.bullet("bestiary.progress.phase.4.0", GuiCurrentProgress.pct(CurrentProgressClientCache.beckonStageIIGrowPenalty), GuiCurrentProgress.pct(CurrentProgressClientCache.beckonStageIIIGrowPenalty));
                this.bullet("bestiary.progress.phase.4.1", GuiCurrentProgress.raw(CurrentProgressClientCache.phasePassiveGain));
                this.bullet("bestiary.progress.phase.4.2", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseReinforcementChance));
                this.bullet("bestiary.progress.phase.4.3", new Object[0]);
                this.bullet("bestiary.progress.phase.4.4", new Object[0]);
                this.addExtraPhaseLines(4, 5);
                break;
            }
            case 5: {
                this.bullet("bestiary.progress.phase.5.0", GuiCurrentProgress.raw(CurrentProgressClientCache.phasePassiveGain));
                this.bullet("bestiary.progress.phase.5.1", CurrentProgressClientCache.phaseSleepPenalty);
                this.bullet("bestiary.progress.phase.5.2", GuiCurrentProgress.pct(CurrentProgressClientCache.beckonStageIIIGrowPenalty));
                this.bullet("bestiary.progress.phase.5.3", new Object[0]);
                this.bullet("bestiary.progress.phase.5.4", new Object[0]);
                this.bullet("bestiary.progress.phase.5.5", new Object[0]);
                this.bullet("bestiary.progress.phase.5.6", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseReinforcementChance));
                this.addExtraPhaseLines(5, 7);
                break;
            }
            case 6: {
                this.bullet("bestiary.progress.phase.6.0", new Object[0]);
                this.bullet("bestiary.progress.phase.6.1", GuiCurrentProgress.raw(CurrentProgressClientCache.phasePassiveGain));
                this.bullet("bestiary.progress.phase.6.2", new Object[0]);
                this.bullet("bestiary.progress.phase.6.3", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseCothSpawnChance));
                this.bullet("bestiary.progress.phase.6.4", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseCropStuntChance));
                this.bullet("bestiary.progress.phase.6.5", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseReinforcementChance));
                this.addExtraPhaseLines(6, 6);
                break;
            }
            case 7: {
                this.bullet("bestiary.progress.phase.7.0", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseCothSpawnChance));
                this.bullet("bestiary.progress.phase.7.1", GuiCurrentProgress.raw(CurrentProgressClientCache.phasePassiveGain));
                this.bullet("bestiary.progress.phase.7.2", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseCropStuntChance));
                this.bullet("bestiary.progress.phase.7.3", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseReinforcementChance));
                this.addExtraPhaseLines(7, 4);
                break;
            }
            case 8: {
                this.bullet("bestiary.progress.phase.8.0", GuiCurrentProgress.raw(CurrentProgressClientCache.phasePassiveGain));
                this.bullet("bestiary.progress.phase.8.1", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseCothSpawnChance));
                this.bullet("bestiary.progress.phase.8.2", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseCropStuntChance));
                this.bullet("bestiary.progress.phase.8.3", GuiCurrentProgress.pct(CurrentProgressClientCache.phaseReinforcementChance));
                this.addExtraPhaseLines(8, 4);
                break;
            }
            case 9: {
                this.addExtraPhaseLines(9, 0);
                break;
            }
            case 10: {
                this.addExtraPhaseLines(10, 0);
                break;
            }
            default: {
                this.bullet("bestiary.progress.phase.unknown.0", new Object[0]);
            }
        }
    }

    private void addPriorPhaseLines(int currentPhase) {
        int maxKnown = Math.min(currentPhase - 1, 8);
        if (maxKnown < 0) {
            this.bullet("bestiary.progress.prior.none", new Object[0]);
            return;
        }
        for (int p = 0; p <= maxKnown; ++p) {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.prior.phase_marker", p)), 0x554444, true));
            this.addPhaseLines(p);
        }
    }

    private void addPriorUDLines(int currentUD) {
        boolean any = false;
        if (currentUD >= CurrentProgressClientCache.udDislodgmentLevel) {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.prior.ud_marker", CurrentProgressClientCache.udDislodgmentLevel)), 0x554444, true));
            this.bullet("bestiary.progress.ud.dislodgment", CurrentProgressClientCache.udDislodgmentLevel);
            this.bullet("bestiary.progress.ud.merge", CurrentProgressClientCache.udMergeLevel);
            any = true;
        }
        if (currentUD >= CurrentProgressClientCache.udCollectiveConsciousnessLevel) {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.prior.ud_marker", CurrentProgressClientCache.udCollectiveConsciousnessLevel)), 0x554444, true));
            this.bullet("bestiary.progress.ud.collective_consciousness", CurrentProgressClientCache.udCollectiveConsciousnessLevel);
            this.bullet("bestiary.progress.ud.scent", CurrentProgressClientCache.udScentLevel);
            this.bullet("bestiary.progress.ud.vectorless", CurrentProgressClientCache.udVectorlessLevel);
            any = true;
        }
        if (currentUD >= CurrentProgressClientCache.udNestsLevel) {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.prior.ud_marker", CurrentProgressClientCache.udNestsLevel)), 0x554444, true));
            this.bullet("bestiary.progress.ud.nests", CurrentProgressClientCache.udNestsLevel);
            this.bullet("bestiary.progress.ud.variants", CurrentProgressClientCache.udVariantsLevel);
            any = true;
        }
        if (currentUD >= CurrentProgressClientCache.udColoniesLevel) {
            this.lines.add(new Line(this.distort(this.tr("bestiary.progress.prior.ud_marker", CurrentProgressClientCache.udColoniesLevel)), 0x554444, true));
            this.bullet("bestiary.progress.ud.colonies", CurrentProgressClientCache.udColoniesLevel);
            this.bullet("bestiary.progress.ud.hives", CurrentProgressClientCache.udHivesLevel);
            this.bullet("bestiary.progress.ud.nodes", CurrentProgressClientCache.udNodesLevel);
            any = true;
        }
        if (!any) {
            this.bullet("bestiary.progress.prior.none", new Object[0]);
        }
    }

    private void addUDLines(int udl) {
        boolean any = false;
        if (udl >= CurrentProgressClientCache.udDislodgmentLevel) {
            this.bullet("bestiary.progress.ud.dislodgment", CurrentProgressClientCache.udDislodgmentLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udMergeLevel) {
            this.bullet("bestiary.progress.ud.merge", CurrentProgressClientCache.udMergeLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udCollectiveConsciousnessLevel) {
            this.bullet("bestiary.progress.ud.collective_consciousness", CurrentProgressClientCache.udCollectiveConsciousnessLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udScentLevel) {
            this.bullet("bestiary.progress.ud.scent", CurrentProgressClientCache.udScentLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udVectorlessLevel) {
            this.bullet("bestiary.progress.ud.vectorless", CurrentProgressClientCache.udVectorlessLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udNestsLevel) {
            this.bullet("bestiary.progress.ud.nests", CurrentProgressClientCache.udNestsLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udVariantsLevel) {
            this.bullet("bestiary.progress.ud.variants", CurrentProgressClientCache.udVariantsLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udColoniesLevel) {
            this.bullet("bestiary.progress.ud.colonies", CurrentProgressClientCache.udColoniesLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udHivesLevel) {
            this.bullet("bestiary.progress.ud.hives", CurrentProgressClientCache.udHivesLevel);
            any = true;
        }
        if (udl >= CurrentProgressClientCache.udNodesLevel) {
            this.bullet("bestiary.progress.ud.nodes", CurrentProgressClientCache.udNodesLevel);
            any = true;
        }
        if (CurrentProgressClientCache.udMobChance > 0.0) {
            this.bullet("bestiary.progress.ud.mob_chance", GuiCurrentProgress.pct(CurrentProgressClientCache.udMobChance));
            any = true;
        }
        if (!any) {
            this.bullet("bestiary.progress.ud.none", new Object[0]);
        }
    }

    private void bullet(String key, Object ... args) {
        this.lines.add(new Line(this.distort(this.tr("bestiary.progress.bullet", this.tr(key, args))), 0x222222, false));
    }

    private void rebuildHoverTerms() {
        this.hoverTerms.clear();
        this.addHoverTerm("bestiary.progress.term.coth", "bestiary.progress.term.coth.tooltip");
        this.addHoverTerm("bestiary.progress.term.reinforcement", "bestiary.progress.term.reinforcement.tooltip");
        this.addHoverTerm("bestiary.progress.term.beckons", "bestiary.progress.term.beckons.tooltip");
        this.addHoverTerm("bestiary.progress.term.passive_points", "bestiary.progress.term.passive_points.tooltip");
        this.addHoverTerm("bestiary.progress.term.crop_growth", "bestiary.progress.term.crop_growth.tooltip");
        this.addHoverTerm("bestiary.progress.term.feral", "bestiary.progress.term.feral.tooltip");
        this.addHoverTerm("bestiary.progress.term.assimilated", "bestiary.progress.term.assimilated.tooltip");
        this.addHoverTerm("bestiary.progress.term.buglins", "bestiary.progress.term.buglins.tooltip");
        this.addHoverTerm("bestiary.progress.term.rupters", "bestiary.progress.term.rupters.tooltip");
        this.addHoverTerm("bestiary.progress.term.fishing", "bestiary.progress.term.fishing.tooltip");
        this.addHoverTerm("bestiary.progress.term.dislodgment", "bestiary.progress.term.dislodgment.tooltip");
        this.addHoverTerm("bestiary.progress.term.merge", "bestiary.progress.term.merge.tooltip");
        this.addHoverTerm("bestiary.progress.term.collective_consciousness", "bestiary.progress.term.collective_consciousness.tooltip");
        this.addHoverTerm("bestiary.progress.term.scent", "bestiary.progress.term.scent.tooltip");
        this.addHoverTerm("bestiary.progress.term.vectorless", "bestiary.progress.term.vectorless.tooltip");
        this.addHoverTerm("bestiary.progress.term.nests", "bestiary.progress.term.nests.tooltip");
        this.addHoverTerm("bestiary.progress.term.variants", "bestiary.progress.term.variants.tooltip");
        this.addHoverTerm("bestiary.progress.term.colonies", "bestiary.progress.term.colonies.tooltip");
        this.addHoverTerm("bestiary.progress.term.hives", "bestiary.progress.term.hives.tooltip");
        this.addHoverTerm("bestiary.progress.term.nodes", "bestiary.progress.term.nodes.tooltip");
        this.addHoverTerm("bestiary.progress.term.ubiquitous_mob_chance", "bestiary.progress.term.ubiquitous_mob_chance.tooltip");
    }

    private void addHoverTerm(String termKey, String tooltipKey) {
        String term = this.tr(termKey, new Object[0]);
        String tooltip = this.tr(tooltipKey, new Object[0]);
        if (term.equals(termKey) && "bestiary.progress.term.ubiquitous_mob_chance".equals(termKey)) {
            term = "Ubiquitous mob list swap chance";
        }
        if (tooltip.equals(tooltipKey) && "bestiary.progress.term.ubiquitous_mob_chance.tooltip".equals(tooltipKey)) {
            tooltip = "Chance for the system to use the Ubiquitous Development mob list instead of the normal evolution-phase mob list.";
        }
        if (term == null || term.trim().isEmpty()) {
            return;
        }
        if (term.equals(termKey)) {
            return;
        }
        this.hoverTerms.add(new HoverTerm(term, tooltip));
    }

    private void drawProgressList() {
        this.hoverWords.clear();
        ScaledResolution sr = new ScaledResolution(this.mc);
        int scale = sr.getScaleFactor();
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
        GL11.glEnable((int)3089);
        GL11.glScissor((int)(scX -= pad), (int)(scY -= pad), (int)(scW += pad * 2), (int)(scH += pad * 2));
        int y = this.listY + 6 - this.scroll;
        for (Line line : this.lines) {
            y = this.drawWrappedLine(line, this.listX + 8, y, this.listW - 16);
        }
        GL11.glDisable((int)3089);
        this.drawScrollbar();
    }

    private int drawWrappedLine(Line line, int x, int y, int width) {
        if (line.text == null || line.text.isEmpty()) {
            return y + 8;
        }
        List<? extends String> wrapped = this.fontRendererObj.listFormattedStringToWidth(line.text, width);
        for (String part : wrapped) {
            if (y > this.listY - 12 && y < this.listY + this.listH + 12) {
                if (line.header) {
                    GuiCurrentProgress.drawRect((int)(x - 4), (int)(y - 2), (int)(x + width + 2), (int)(y + 10), (int)0x14FFFFFF);
                }
                this.resetTextRenderState();
                this.fontRendererObj.drawString(part, (float)x, (float)y, line.color, false);
                this.drawHoverTerms(part, x, y);
            }
            y += 12;
        }
        return y + 1;
    }

    private void drawHoverTerms(String text, int x, int y) {
        for (HoverTerm term : this.hoverTerms) {
            this.markHoverTerm(text, term, x, y);
        }
    }

    private void markHoverTerm(String text, HoverTerm term, int x, int y) {
        int searchFrom = 0;
        String haystack = text;
        String needle = term.term;
        while (searchFrom < text.length()) {
            int index = haystack.indexOf(needle, searchFrom);
            if (index < 0) {
                index = text.toLowerCase(Locale.ROOT).indexOf(term.term.toLowerCase(Locale.ROOT), searchFrom);
            }
            if (index < 0) {
                return;
            }
            String before = text.substring(0, index);
            String matched = text.substring(index, Math.min(text.length(), index + term.term.length()));
            int tx = x + this.fontRendererObj.getStringWidth(before);
            int tw = this.fontRendererObj.getStringWidth(matched);
            GuiCurrentProgress.drawRect((int)tx, (int)(y + 9), (int)(tx + tw), (int)(y + 10), (int)-1436155904);
            this.hoverWords.add(new HoverWord(tx, y, tw, 10, term.tooltip));
            searchFrom = index + Math.max(1, term.term.length());
        }
    }

    private void addExtraPhaseLines(int phase, int startIndex) {
        String key;
        for (int i = startIndex; i < 64 && GuiContext.hasKey((String)(key = "bestiary.progress.phase." + phase + "." + i)); ++i) {
            this.bullet(key, new Object[0]);
        }
    }

    private void drawHover(int mouseX, int mouseY) {
        int dx = this.toDesignX(mouseX);
        int dy = this.toDesignY(mouseY);
        for (HoverWord word : this.hoverWords) {
            if (dx < word.x || dx >= word.x + word.w || dy < word.y || dy >= word.y + word.h) continue;
            List tip = this.fontRendererObj.listFormattedStringToWidth(word.tooltip, 220);
            this.drawHoveringText(tip, mouseX, mouseY);
            return;
        }
    }

    private void drawScrollbar() {
        int maxScroll = this.getMaxScroll();
        if (maxScroll <= 0) {
            return;
        }
        int barX = this.listX + this.listW - 5;
        int barY = this.listY + 4;
        int barH = this.listH - 8;
        GuiCurrentProgress.drawRect((int)barX, (int)barY, (int)(barX + 2), (int)(barY + barH), (int)0x66000000);
        int thumbH = Math.max(14, (int)((float)(barH * this.listH) / (float)Math.max(this.listH, this.getContentHeight())));
        int thumbY = barY + (int)((float)(barH - thumbH) * ((float)this.scroll / (float)maxScroll));
        GuiCurrentProgress.drawRect((int)(barX - 1), (int)thumbY, (int)(barX + 3), (int)(thumbY + thumbH), (int)-1439493871);
    }

    private int getContentHeight() {
        int y = 0;
        for (Line line : this.lines) {
            if (line.text == null || line.text.isEmpty()) {
                y += 8;
                continue;
            }
            List<? extends String> wrapped = this.fontRendererObj.listFormattedStringToWidth(line.text, this.listW - 16);
            y += wrapped.size() * 12 + 1;
        }
        return y + 12;
    }

    private int getMaxScroll() {
        return Math.max(0, this.getContentHeight() - this.listH + 12);
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

    private boolean isOverSlot(int dx, int dy, int sx, int sy) {
        return dx >= sx && dx < sx + 20 && dy >= sy && dy < sy + 20;
    }

    private void resetTextRenderState() {
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate((int)770, (int)771, (int)1, (int)0);
    }

    private void drawCenteredNoShadow(String text, int x, int y, int color) {
        this.resetTextRenderState();
        this.fontRendererObj.drawString(text, (float)(x - this.fontRendererObj.getStringWidth(text) / 2), (float)y, color, false);
    }

    private void drawSplitNoShadow(String text, int x, int y, int width, int color) {
        if (text == null || text.isEmpty()) {
            return;
        }
        List<? extends String> wrapped = this.fontRendererObj.listFormattedStringToWidth(text, width);
        this.resetTextRenderState();
        for (String line : wrapped) {
            this.fontRendererObj.drawString(line, (float)x, (float)y, color, false);
            y += 10;
        }
    }

    private static String pct(double value) {
        return String.format(Locale.ROOT, "%.1f%%", value * 100.0);
    }

    private static String raw(double value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private static class HoverWord {
        final int x;
        final int y;
        final int w;
        final int h;
        final String tooltip;

        HoverWord(int x, int y, int w, int h, String tooltip) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.tooltip = tooltip;
        }
    }

    private static class HoverTerm {
        final String term;
        final String tooltip;

        HoverTerm(String term, String tooltip) {
            this.term = term;
            this.tooltip = tooltip;
        }
    }

    private static class Line {
        final String text;
        final int color;
        final boolean header;

        Line(String text, int color, boolean header) {
            this.text = text;
            this.color = color;
            this.header = header;
        }
    }
}

