package com.dhanantry.scapeandrunparasites.bestiary.client.gui;

import com.dhanantry.scapeandrunparasites.client.legacy.gui.*;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;

import com.dhanantry.scapeandrunparasites.bestiary.BestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.BlocksPage;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiBestiary;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.StatusEffectsPage;
import com.dhanantry.scapeandrunparasites.bestiary.client.gui.SystemsPage;
import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.OpenGlHelper;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class ParasitesPage
extends GuiScreen {
    private static final ResourceLocation TEX_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/background.png");
    private static final ResourceLocation TEX_LABEL_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/label_background.png");
    private static final ResourceLocation TEX_MODEL_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/model_background.png");
    private static final ResourceLocation TEX_STATS_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/stats_background.png");
    private static final ResourceLocation TEX_NAME_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/name_background.png");
    private static final ResourceLocation TEX_LORE_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/lore_background.png");
    private static final ResourceLocation TEX_DROP_BG = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/drop_background.png");
    private static final ResourceLocation TEX_WARNING_SHEET = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/gui/bestiary/warning_sheet.png");
    private static final int LIST_HEADER_H = 18;
    private boolean isJumbled;
    private final Player player;
    private BestiaryPage page = BestiaryPage.HOME;
    private ParasiteTier selectedTier = null;
    private BestiaryEntry selectedMob = null;
    private boolean suppressPoseFieldUpdates = false;
    private final List<ParasiteTier> visibleTiers = new ArrayList<ParasiteTier>();
    private final List<BestiaryEntry> visibleMobs = new ArrayList<BestiaryEntry>();
    private final Map<String, LivingEntity> entityCache = new HashMap<String, LivingEntity>();
    private float spinDeg = 0.0f;
    private int scrollTiers = 0;
    private int scrollMobs = 0;
    private boolean listScrollDrag = false;
    private boolean listScrollIsTiers = false;
    private int listScrollDragStartMouseY = 0;
    private int listScrollDragStartScrollPx = 0;
    private int listScrollTrackX = 0;
    private int listScrollTrackY = 0;
    private int listScrollTrackW = 0;
    private int listScrollTrackH = 0;
    private int listScrollThumbY = 0;
    private int listScrollThumbH = 0;
    private static final int BTN_RUN = 1003;
    private boolean modelRun = false;
    private boolean renderRunThisCall = false;
    private boolean syncRequested = false;
    private static final boolean DEBUG_SYNC = false;
    private static final int THUMB = 28;
    private static final int BTN_H = 20;
    private static final int V_PAD = 8;
    private static final int ROW_H = 36;
    private static final int LIST_X = 30;
    private static final int LIST_W = 140;
    private static final int LIST_TOP = 50;
    private static final int UI_ICON_MARGIN_PX = 3;
    private static final float UI_ICON_SHRINK = 0.92f;
    private static final int UI_DARK_GRAY = 0x2E2E2E;
    private static final int BTN_HOME = 1000;
    private static final int BTN_TIERS = 1001;
    private static final int BTN_MOBLIST = 1002;
    private static final int BTN_LORE = 1012;
    private boolean lorePopupOpen = false;
    private final GuiScreen parent;
    private float tierListAnim = 1.0f;
    private float tierListAnimTarget = 1.0f;
    private long tierListAnimLastMs = 0L;
    private BestiaryPage pendingPage3 = null;
    private ParasiteTier pendingTier3 = null;
    private BestiaryEntry pendingMob3 = null;
    private int loreScrollPx = 0;
    private boolean loreScrollDrag = false;
    private int loreScrollDragStartMouseY = 0;
    private int loreScrollDragStartScrollPx = 0;
    private int loreTextX = 0;
    private int loreTextY = 0;
    private int loreTextW = 0;
    private int loreTextH = 0;
    private int loreContentH = 0;
    private int loreScrollTrackX = 0;
    private int loreScrollTrackY = 0;
    private int loreScrollTrackW = 0;
    private int loreScrollTrackH = 0;
    private int loreScrollThumbY = 0;
    private int loreScrollThumbH = 0;
    private boolean modelDragActive = false;
    private float manualYawDeg = 0.0f;
    private int dragStartMouseX = 0;
    private float dragStartYawDeg = 0.0f;
    private boolean autoRotateModel = true;
    private float manualPitchDeg = 0.0f;
    private int dragStartMouseY = 0;
    private float dragStartPitchDeg = 0.0f;
    private static final int BTN_ROTATE = 1004;
    private int modelRectX;
    private int modelRectY;
    private int modelRectW;
    private int modelRectH;
    private float modelZoom = 1.0f;
    private static final float MODEL_ZOOM_MIN = 0.35f;
    private static final float MODEL_ZOOM_MAX = 2.75f;
    private static final int BTN_GREENSCREEN = 1005;
    private static final int BGSCREEN_OFF = 0;
    private static final int BGSCREEN_GREEN = 1;
    private static final int BGSCREEN_BLUE = 2;
    private int modelBgScreen = 0;
    private static final int BTN_APPLY_POSE = 1010;
    private static final int BTN_RESET_POSE = 1011;
    private float loreAnim = 0.0f;
    private float loreAnimTarget = 0.0f;
    private boolean lorePopupClosingRefresh = false;
    private long loreAnimLastMs = 0L;
    private int lorePopupX = 0;
    private int lorePopupY = 0;
    private int lorePopupW = 0;
    private int lorePopupH = 0;
    private int loreBackX = 0;
    private int loreBackY = 0;
    private int loreBackW = 80;
    private int loreBackH = 20;
    private GuiTextField tfYaw;
    private GuiTextField tfPitch;
    private GuiTextField tfZoom;
    private GuiTextField tfPanX;
    private GuiTextField tfPanY;
    private float mobDetailAnim = 1.0f;
    private float mobDetailAnimTarget = 1.0f;
    private long mobDetailAnimLastMs = 0L;
    private float mobListAnim = 1.0f;
    private float mobListAnimTarget = 1.0f;
    private long mobListAnimLastMs = 0L;
    private boolean tierListEnterFromRight = false;
    private BestiaryPage pendingPage2 = null;
    private ParasiteTier pendingTier2 = null;
    private BestiaryEntry pendingMob2 = null;
    private int dropsScrollPx = 0;
    private boolean dropsScrollDrag = false;
    private int dropsScrollDragStartMouseY = 0;
    private int dropsScrollDragStartScrollPx = 0;
    private int dropsViewX = 0;
    private int dropsViewY = 0;
    private int dropsViewW = 0;
    private int dropsViewH = 0;
    private int dropsContentH = 0;
    private int dropsScrollTrackX = 0;
    private int dropsScrollTrackY = 0;
    private int dropsScrollTrackW = 0;
    private int dropsScrollTrackH = 0;
    private int dropsScrollThumbY = 0;
    private int dropsScrollThumbH = 0;
    private ItemStack hoveredDropStack = ItemStack.EMPTY;
    private int hoveredDropMouseX = 0;
    private int hoveredDropMouseY = 0;
    private BestiaryPage pendingPage = null;
    private ParasiteTier pendingTier = null;
    private BestiaryEntry pendingMob = null;
    private int lastW = -1;
    private int lastH = -1;
    private boolean poseControlsInit = false;
    private static final boolean DEBUG_DROPS = false;
    private static final Map<String, String> CFG_CATEGORY_TO_FAMILY = new HashMap<String, String>();
    private boolean dropsLoaded = false;
    private long dropsLastModified = -1L;
    private final Map<String, List<DropEntry>> dropsByCategory = new HashMap<String, List<DropEntry>>();
    private final Set<String> knownCategories = new HashSet<String>();
    private boolean modelPanActive = false;
    private int panStartMouseX = 0;
    private int panStartMouseY = 0;
    private int modelPanX = 0;
    private int modelPanY = 0;
    private int panStartX = 0;
    private int panStartY = 0;
    private static final Map<ParasiteTier, ItemStack> TIER_ICONS = new HashMap<ParasiteTier, ItemStack>();
    private static final ItemStack DEFAULT_TIER_ICON;

    private int LIST_VIEW_TOP() {
        return 68;
    }

    private int LIST_BOTTOM() {
        return this.height - 40;
    }

    private void startMobListExit(BestiaryPage nextPage, ParasiteTier nextTier, BestiaryEntry nextMob) {
        this.pendingPage2 = nextPage;
        this.pendingTier2 = nextTier;
        this.pendingMob2 = nextMob;
        this.mobListAnimTarget = 0.0f;
        this.mobListAnimLastMs = 0L;
        if (nextPage == BestiaryPage.MOB_DETAIL) {
            this.mobDetailAnim = 0.0f;
            this.mobDetailAnimTarget = 1.0f;
            this.mobDetailAnimLastMs = 0L;
        }
    }

    private void startTierListExit(BestiaryPage nextPage, ParasiteTier nextTier, BestiaryEntry nextMob) {
        this.pendingPage3 = nextPage;
        this.pendingTier3 = nextTier;
        this.pendingMob3 = nextMob;
        this.tierListAnimTarget = 0.0f;
        this.tierListAnimLastMs = 0L;
    }

    public ParasitesPage(Player player, GuiScreen parent) {
        this.player = player;
        this.parent = parent;
    }

    public void openParasitesRoot() {
        this.page = BestiaryPage.PARASITES;
        this.selectedTier = null;
        this.selectedMob = null;
        this.visibleTiers.clear();
        this.visibleMobs.clear();
        this.tierListEnterFromRight = false;
        this.tierListAnim = 0.0f;
        this.tierListAnimTarget = 1.0f;
        this.tierListAnimLastMs = 0L;
    }

    private static float clamp01(float v) {
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    private static float smoothstep(float a) {
        a = ParasitesPage.clamp01(a);
        return a * a * (3.0f - 2.0f * a);
    }

    private void startMobDetailExit(BestiaryPage nextPage, ParasiteTier nextTier, BestiaryEntry nextMob) {
        this.pendingPage = nextPage;
        this.pendingTier = nextTier;
        this.pendingMob = nextMob;
        this.mobDetailAnimTarget = 0.0f;
        this.mobDetailAnimLastMs = 0L;
        this.loreAnimTarget = 0.0f;
        this.lorePopupClosingRefresh = false;
        if (nextPage == BestiaryPage.MOB_LIST) {
            this.mobListAnim = 0.0f;
            this.mobListAnimTarget = 1.0f;
            this.mobListAnimLastMs = 0L;
        }
    }

    private File getMobsCfgFile() {
        return new File(this.mc.gameDirectory, "config/srparasites/SRParasitesMobs.cfg");
    }

    private void drawAnimatedStripVertical(ResourceLocation sheet, int frames, int x, int y, int w, int h, int frameW, int frameH, int frameDurationMs) {
        long nowMs = System.nanoTime() / 1000000L;
        long index = nowMs / (long)Math.max(1, frameDurationMs) % (long)Math.max(1, frames);
        int u = 0;
        int v = (int)index * frameH;
        int sheetW = frameW;
        int sheetH = frames * frameH;
        GuiContext.bind(sheet);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Gui.drawScaledCustomSizeModalRect((int)x, (int)y, (float)((float)u), (float)v, (int)frameW, (int)frameH, (int)w, (int)h, (float)sheetW, (float)sheetH);
    }

    private void applyTierScrollLayout() {
        int contentH = this.visibleTiers.size() * 36;
        int maxScroll = Math.max(0, contentH - (this.LIST_BOTTOM() - this.LIST_VIEW_TOP()));
        this.scrollTiers = Math.max(0, Math.min(this.scrollTiers, maxScroll));
        for (GuiButton b : this.buttonList) {
            boolean inView;
            if (!(b instanceof ListButton) || b.id < 100 || b.id >= 200) continue;
            ListButton lb = (ListButton)b;
            int rowY = lb.rowTopY - this.scrollTiers;
            b.y = rowY + 4;
            b.visible = inView = rowY + 28 > this.LIST_VIEW_TOP() && rowY < this.LIST_BOTTOM();
        }
    }

    private void applyMobScrollLayout() {
        int contentH = this.visibleMobs.size() * 36;
        int maxScroll = Math.max(0, contentH - (this.LIST_BOTTOM() - this.LIST_VIEW_TOP()));
        this.scrollMobs = Math.max(0, Math.min(this.scrollMobs, maxScroll));
        for (GuiButton b : this.buttonList) {
            boolean inView;
            if (!(b instanceof ListButton) || b.id < 200 || b.id >= 300) continue;
            ListButton lb = (ListButton)b;
            int rowY = lb.rowTopY - this.scrollMobs;
            b.y = rowY + 4;
            b.visible = inView = rowY + 28 > this.LIST_VIEW_TOP() && rowY < this.LIST_BOTTOM();
        }
    }

    private void drawPanel(ResourceLocation tex, int x, int y, int w, int h) {
        GuiContext.bind(tex);
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        Gui.drawScaledCustomSizeModalRect((int)x, (int)y, (float)0.0f, (float)0.0f, (int)256, (int)256, (int)w, (int)h, (float)256.0f, (float)256.0f);
    }

    public void handleMouseInput() throws IOException {
        int my;
        int mx;
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();
        if (dWheel == 0) {
            return;
        }
        if (this.lorePopupOpen) {
            int wheelDir = (int)Math.signum(dWheel);
            int step = 14;
            this.loreScrollPx += (wheelDir < 0 ? step : -step) * 3;
            this.clampLoreScroll();
            return;
        }
        int wheelDir = (int)Math.signum(dWheel);
        if (!this.lorePopupOpen && this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null && dWheel != 0) {
            boolean overDrops;
            mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
            my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
            boolean bl = overDrops = mx >= this.dropsViewX && mx < this.dropsViewX + this.dropsViewW && my >= this.dropsViewY && my < this.dropsViewY + this.dropsViewH;
            if (overDrops && this.dropsContentH > this.dropsViewH) {
                int step = 14;
                this.dropsScrollPx += (wheelDir < 0 ? step : -step) * 3;
                this.clampDropsScroll();
                return;
            }
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null) {
            boolean overModel;
            mx = Mouse.getEventX() * this.width / this.mc.getWindow().getWidth();
            my = this.height - Mouse.getEventY() * this.height / this.mc.getWindow().getHeight() - 1;
            boolean bl = overModel = mx >= this.modelRectX && mx < this.modelRectX + this.modelRectW && my >= this.modelRectY && my < this.modelRectY + this.modelRectH;
            if (this.isBgScreenActive() || overModel) {
                float step = 0.1f;
                this.modelZoom = wheelDir > 0 ? (this.modelZoom += step) : (this.modelZoom -= step);
                this.modelZoom = Math.max(0.35f, Math.min(2.75f, this.modelZoom));
                this.syncPoseFieldsFromState();
                return;
            }
        }
        int delta = (int)Math.signum(dWheel) * -18;
        switch (this.page) {
            case PARASITES: {
                this.scrollTiers += delta * 2;
                this.applyTierScrollLayout();
                break;
            }
            case MOB_LIST: {
                this.scrollMobs += delta * 2;
                this.applyMobScrollLayout();
                break;
            }
        }
    }

    private static String loreKeyFromMobId(String mobId) {
        ResourceLocation rl = mobId.indexOf(58) >= 0 ? ResourceLocation.parse(mobId) : ResourceLocation.fromNamespaceAndPath("srparasites", mobId);
        return "lore." + rl.getNamespace() + "." + rl.getPath();
    }

    private static String tierLangKey(ParasiteTier t) {
        String key = t.name().toLowerCase(Locale.ROOT);
        if ("infected".equals(key)) {
            key = "assimilated";
        }
        return "bestiary.tier." + key;
    }

    private String distort(String s) {
        return GuiDistortionHelper.jamTextIfNeeded(s, this.isJumbled);
    }

    public void initGui() {
        super.initGui();
        this.isJumbled = GuiDistortionHelper.isDistortionActive(this.mc);
        Keyboard.enableRepeatEvents((boolean)true);
        this.buttonList.clear();
        if (!this.syncRequested) {
            this.syncRequested = true;
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(new com.dhanantry.scapeandrunparasites.network.BestiaryRequestPayload());
        }
        int navY = 10;
        int navHomeX = 10;
        int navHomeW = 60;
        int navTiersX = 74;
        int navTiersW = 60;
        int navMobListX = 138;
        int navMobListW = 80;
        switch (this.page) {
            case HOME: {
                int cx = this.width / 2;
                this.buttonList.add(new GuiButton(10, cx - 60, 70, 120, 20, this.distort(GuiContext.fmt((String)"bestiary.tab.parasites", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(11, cx - 60, 95, 120, 20, this.distort(GuiContext.fmt((String)"bestiary.tab.blocks", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(12, cx - 60, 120, 120, 20, this.distort(GuiContext.fmt((String)"bestiary.tab.celestial", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(14, cx - 60, 170, 120, 20, this.distort(GuiContext.fmt((String)"bestiary.tab.systems", (Object[])new Object[0]))));
                this.buttonList.add(new GuiButton(13, cx - 60, 145, 120, 20, this.distort(GuiContext.fmt((String)"bestiary.tab.effects", (Object[])new Object[0]))));
                break;
            }
            case PARASITES: {
                this.visibleMobs.clear();
                this.visibleTiers.clear();
                IBestiaryProgress prog = BestiaryCapability.get(this.player);
                if (prog != null) {
                    for (ParasiteTier t : GuiBestiary.getDisplayOrderLocalized()) {
                        if (!prog.isTierSeen(t)) continue;
                        this.visibleTiers.add(t);
                    }
                }
                int rowTop = this.LIST_VIEW_TOP();
                for (int i = 0; i < this.visibleTiers.size(); ++i) {
                    ParasiteTier t = this.visibleTiers.get(i);
                    String tierLabel = this.distort(GuiContext.fmt((String)ParasitesPage.tierLangKey(t), (Object[])new Object[0]));
                    ItemStack icon = TIER_ICONS.get(t);
                    if (icon == null || icon.isEmpty()) {
                        icon = DEFAULT_TIER_ICON;
                    }
                    this.buttonList.add(new TierButton(100 + i, 30, rowTop, 140, 20, tierLabel, t, icon));
                    rowTop += 36;
                }
                this.scrollTiers = 0;
                this.applyTierScrollLayout();
                this.buttonList.add(new AnimatedButton(1000, 10, 10, 60, 20, this.distort(GuiContext.fmt((String)"bestiary.nav.home", (Object[])new Object[0])), 0));
                break;
            }
            case MOB_LIST: {
                this.visibleMobs.clear();
                IBestiaryProgress prog = BestiaryCapability.get(this.player);
                if (prog != null && this.selectedTier != null) {
                    for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
                        if (e.tier != this.selectedTier || !ParasitesPage.isKnown(prog, e)) continue;
                        this.visibleMobs.add(e);
                    }
                }
                int rowTop = this.LIST_VIEW_TOP();
                for (int i = 0; i < this.visibleMobs.size(); ++i) {
                    BestiaryEntry e = this.visibleMobs.get(i);
                    String name = this.distort(GuiContext.fmt((String)e.nameKey, (Object[])new Object[0]));
                    this.buttonList.add(new MobListEntryButton(200 + i, 30, rowTop, 140, 20, name));
                    rowTop += 36;
                }
                this.applyMobScrollLayout();
                this.buttonList.add(new AnimatedButton(1000, 10, 10, 60, 20, this.distort(GuiContext.fmt((String)"bestiary.nav.home", (Object[])new Object[0])), 0));
                this.buttonList.add(new AnimatedButton(1001, 74, 10, 60, 20, this.distort(GuiContext.fmt((String)"bestiary.nav.tiers", (Object[])new Object[0])), 0));
                break;
            }
            case MOB_DETAIL: {
                if (this.isBgScreenActive()) {
                    int bottomY = this.height - 24;
                    int gap = 6;
                    int gsW = 140;
                    int rotW = 130;
                    int runW = 90;
                    int x = this.width - 10;
                    this.buttonList.add(new GuiButton(1005, x -= gsW, bottomY, gsW, 20, this.distort(GuiContext.fmt((String)this.getBgScreenLabelKey(), (Object[])new Object[0]))));
                    x -= gap;
                    this.buttonList.add(new GuiButton(1004, x -= rotW, bottomY, rotW, 20, this.autoRotateModel ? this.distort(GuiContext.fmt((String)"bestiary.controls.rotation.on", (Object[])new Object[0])) : this.distort(GuiContext.fmt((String)"bestiary.controls.rotation.off", (Object[])new Object[0]))));
                    x -= gap;
                    this.buttonList.add(new GuiButton(1003, x -= runW, bottomY, runW, 20, this.modelRun ? this.distort(GuiContext.fmt((String)"bestiary.controls.run.on", (Object[])new Object[0])) : this.distort(GuiContext.fmt((String)"bestiary.controls.run.off", (Object[])new Object[0]))));
                    break;
                }
                this.buttonList.add(new AnimatedButton(1000, 10, 10, 60, 20, this.distort(GuiContext.fmt((String)"bestiary.nav.home", (Object[])new Object[0])), 0));
                this.buttonList.add(new AnimatedButton(1001, 74, 10, 60, 20, this.distort(GuiContext.fmt((String)"bestiary.nav.tiers", (Object[])new Object[0])), 0));
                this.buttonList.add(new AnimatedButton(1002, 138, 10, 80, 20, this.distort(GuiContext.fmt((String)"bestiary.nav.mob_list", (Object[])new Object[0])), 0));
                int runX = 222;
                int runW = 90;
                this.buttonList.add(new AnimatedButton(1003, runX, 10, runW, 20, this.modelRun ? this.distort(GuiContext.fmt((String)"bestiary.controls.run.on", (Object[])new Object[0])) : this.distort(GuiContext.fmt((String)"bestiary.controls.run.off", (Object[])new Object[0])), 0));
                int rotX = runX + runW + 4;
                int rotW = 130;
                this.buttonList.add(new AnimatedButton(1004, rotX, 10, rotW, 20, this.autoRotateModel ? this.distort(GuiContext.fmt((String)"bestiary.controls.rotation.on", (Object[])new Object[0])) : this.distort(GuiContext.fmt((String)"bestiary.controls.rotation.off", (Object[])new Object[0])), 0));
                int gsW = 140;
                int gsBtnX = this.width - 10 - gsW;
                int gsBtnY = this.height - 18 - 22;
                this.buttonList.add(new AnimatedButton(1005, gsBtnX, gsBtnY, gsW, 20, this.distort(GuiContext.fmt((String)this.getBgScreenLabelKey(), (Object[])new Object[0])), 1));
                int loreW = 110;
                int loreX = gsBtnX - 6 - loreW;
                int loreY = gsBtnY;
                IBestiaryProgress prog = BestiaryCapability.get(this.player);
                int kills = 0;
                if (prog != null && this.selectedMob != null) {
                    kills = prog.getKills(this.selectedMob.mobId);
                }
                int loreMin = 10;
                if (this.selectedMob != null && this.selectedMob.minLoreKill > 0) {
                    loreMin = this.selectedMob.minLoreKill;
                }
                String loreLabel = kills >= loreMin ? this.distort(GuiContext.hasKey((String)"bestiary.lore") ? GuiContext.fmt((String)"bestiary.lore", (Object[])new Object[0]) : GuiContext.fmt((String)"bestiary.lore_fallback", (Object[])new Object[0])) : this.distort(GuiContext.fmt((String)"bestiary.lore_unlocks_at_n", (Object[])new Object[]{loreMin}));
                this.buttonList.add(new AnimatedButton(1012, loreX, loreY, loreW, 20, loreLabel, 1));
                break;
            }
        }
    }

    private static boolean isKnown(IBestiaryProgress prog, BestiaryEntry e) {
        if (prog == null || e == null) {
            return false;
        }
        if (prog.getKills(e.mobId) > 0) {
            return true;
        }
        return prog.isMobSeen(e.mobId);
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        if (this.lorePopupOpen && button.id != 1012) {
            this.loreAnimTarget = 0.0f;
            this.lorePopupClosingRefresh = false;
        }
        if (button.id == 1000) {
            if (this.page == BestiaryPage.MOB_DETAIL) {
                this.startMobDetailExit(BestiaryPage.HOME, null, null);
                return;
            }
            if (this.page == BestiaryPage.MOB_LIST) {
                this.startMobListExit(BestiaryPage.HOME, null, null);
                return;
            }
            this.lorePopupOpen = false;
            this.resetModelPan();
            if (this.parent != null) {
                this.mc.setScreen(this.parent);
                return;
            }
            this.page = BestiaryPage.HOME;
            this.selectedTier = null;
            this.selectedMob = null;
            this.visibleMobs.clear();
            this.visibleTiers.clear();
            this.initGui();
            return;
        }
        if (button.id == 1001) {
            if (this.page == BestiaryPage.MOB_DETAIL) {
                this.startMobDetailExit(BestiaryPage.PARASITES, null, null);
                return;
            }
            if (this.page == BestiaryPage.MOB_LIST) {
                this.startMobListExit(BestiaryPage.PARASITES, null, null);
                return;
            }
            this.resetModelPan();
            this.lorePopupOpen = false;
            this.tierListEnterFromRight = false;
            this.tierListAnim = 0.0f;
            this.tierListAnimTarget = 1.0f;
            this.tierListAnimLastMs = 0L;
            this.page = BestiaryPage.PARASITES;
            this.selectedMob = null;
            this.visibleMobs.clear();
            this.selectedTier = null;
            this.initGui();
            return;
        }
        if (button.id == 1010) {
            this.applyPoseFromFields();
            this.syncPoseFieldsFromState();
            return;
        }
        if (button.id == 1011) {
            this.resetPoseFields();
            this.syncPoseFieldsFromState();
            return;
        }
        if (button.id == 1003) {
            this.modelRun = !this.modelRun;
            this.initGui();
            return;
        }
        if (button.id == 1004) {
            boolean bl = this.autoRotateModel = !this.autoRotateModel;
            if (this.autoRotateModel) {
                this.modelDragActive = false;
            }
            this.initGui();
            return;
        }
        if (button.id == 1005) {
            ++this.modelBgScreen;
            if (this.modelBgScreen > 2) {
                this.modelBgScreen = 0;
            }
            if (this.isBgScreenActive()) {
                this.autoRotateModel = false;
                this.modelDragActive = false;
                this.modelPanActive = false;
                this.ensurePoseFields();
                this.syncPoseFieldsFromState();
            } else {
                this.modelPanActive = false;
            }
            this.initGui();
            return;
        }
        if (button.id == 1012) {
            if (this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null) {
                IBestiaryProgress prog = BestiaryCapability.get(this.player);
                int kills = prog != null ? prog.getKills(this.selectedMob.mobId) : 0;
                int loreMin = 10;
                if (this.selectedMob.minLoreKill > 0) {
                    loreMin = this.selectedMob.minLoreKill;
                }
                if (kills >= loreMin) {
                    if (!this.lorePopupOpen) {
                        this.lorePopupOpen = true;
                        this.loreAnim = 0.0f;
                        this.loreAnimTarget = 1.0f;
                        this.loreAnimLastMs = 0L;
                        this.lorePopupClosingRefresh = false;
                        this.initGui();
                    } else {
                        this.loreAnimTarget = this.loreAnimTarget > 0.5f ? 0.0f : 1.0f;
                        this.lorePopupClosingRefresh = false;
                    }
                }
            }
            return;
        }
        if (button.id == 1002) {
            if (this.page == BestiaryPage.MOB_DETAIL) {
                this.startMobDetailExit(BestiaryPage.MOB_LIST, this.selectedTier, null);
                return;
            }
            this.lorePopupOpen = false;
            this.resetModelPan();
            this.resetModelView();
            this.modelRun = false;
            this.modelZoom = 1.0f;
            this.page = BestiaryPage.MOB_LIST;
            this.selectedMob = null;
            this.initGui();
            return;
        }
        switch (this.page) {
            case HOME: {
                if (button.id == 10) {
                    this.page = BestiaryPage.PARASITES;
                    this.selectedTier = null;
                    this.selectedMob = null;
                    this.tierListEnterFromRight = false;
                    this.tierListAnim = 0.0f;
                    this.tierListAnimTarget = 1.0f;
                    this.tierListAnimLastMs = 0L;
                    this.initGui();
                    break;
                }
                if (button.id == 11) {
                    this.mc.setScreen((GuiScreen)new BlocksPage(this.player, this));
                    break;
                }
                if (button.id == 12) {
                    GuiContext.click();
                    break;
                }
                if (button.id == 13) {
                    this.mc.setScreen((GuiScreen)new StatusEffectsPage(this.player, this));
                    break;
                }
                if (button.id != 14) break;
                this.mc.setScreen((GuiScreen)new SystemsPage(this.player, this));
                break;
            }
            case PARASITES: {
                int idx = button.id - 100;
                if (idx < 0 || idx >= this.visibleTiers.size()) break;
                this.lorePopupOpen = false;
                this.selectedTier = this.visibleTiers.get(idx);
                this.startTierListExit(BestiaryPage.MOB_LIST, this.selectedTier, null);
                return;
            }
            case MOB_LIST: {
                int idx = button.id - 200;
                if (idx < 0 || idx >= this.visibleMobs.size()) break;
                this.lorePopupOpen = false;
                BestiaryEntry next = this.visibleMobs.get(idx);
                this.resetModelView();
                this.modelRun = false;
                this.modelZoom = 1.0f;
                this.startMobListExit(BestiaryPage.MOB_DETAIL, this.selectedTier, next);
                return;
            }
        }
    }

    private void renderEntityPreviewDetail(String mobId, int cx, int cy, int boxW, int boxH, float yawDeg, float pitchDeg) {
        boolean prev = this.renderRunThisCall;
        this.renderRunThisCall = this.modelRun;
        this.renderEntityPreview(mobId, cx, cy, boxW, boxH, yawDeg, pitchDeg);
        this.renderRunThisCall = prev;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.hoveredDropStack = ItemStack.EMPTY;
        this.hoveredDropMouseX = mouseX;
        this.hoveredDropMouseY = mouseY;
        float t = ((float)this.player.tickCount + partialTicks) / 10.0f;
        long nowMs = GuiContext.systemTime();
        if (this.loreAnimLastMs == 0L) {
            this.loreAnimLastMs = nowMs;
        }
        float dt = (float)(nowMs - this.loreAnimLastMs) / 1000.0f;
        this.loreAnimLastMs = nowMs;
        float speed = 12.0f;
        float k = 1.0f - (float)Math.exp(-speed * Math.max(0.0f, Math.min(dt, 0.1f)));
        this.loreAnim += (this.loreAnimTarget - this.loreAnim) * k;
        long nowMs2 = GuiContext.systemTime();
        if (this.mobDetailAnimLastMs == 0L) {
            this.mobDetailAnimLastMs = nowMs2;
        }
        float dt2 = (float)(nowMs2 - this.mobDetailAnimLastMs) / 1000.0f;
        this.mobDetailAnimLastMs = nowMs2;
        float speed2 = 12.0f;
        float k2 = 1.0f - (float)Math.exp(-speed2 * Math.max(0.0f, Math.min(dt2, 0.1f)));
        this.mobDetailAnim += (this.mobDetailAnimTarget - this.mobDetailAnim) * k2;
        if (this.pendingPage != null && this.mobDetailAnimTarget == 0.0f && this.mobDetailAnim <= 0.001f) {
            BestiaryPage next = this.pendingPage;
            this.page = this.pendingPage;
            if (this.page == BestiaryPage.PARASITES) {
                this.tierListEnterFromRight = false;
                this.tierListAnim = 0.0f;
                this.tierListAnimTarget = 1.0f;
                this.tierListAnimLastMs = 0L;
            }
            this.selectedTier = this.pendingTier;
            this.selectedMob = this.pendingMob;
            this.pendingPage = null;
            this.pendingTier = null;
            this.pendingMob = null;
            if (next == BestiaryPage.MOB_LIST) {
                this.mobListAnim = 0.0f;
                this.mobListAnimTarget = 1.0f;
                this.mobListAnimLastMs = GuiContext.systemTime();
            } else if (next == BestiaryPage.MOB_DETAIL) {
                this.mobDetailAnim = 0.0f;
                this.mobDetailAnimTarget = 1.0f;
                this.mobDetailAnimLastMs = GuiContext.systemTime();
            } else {
                this.mobDetailAnim = 1.0f;
                this.mobDetailAnimTarget = 1.0f;
                this.mobDetailAnimLastMs = GuiContext.systemTime();
            }
            this.initGui();
        }
        long nowMs3 = GuiContext.systemTime();
        if (this.mobListAnimLastMs == 0L) {
            this.mobListAnimLastMs = nowMs3;
        }
        float dt3 = (float)(nowMs3 - this.mobListAnimLastMs) / 1000.0f;
        this.mobListAnimLastMs = nowMs3;
        float speed3 = 12.0f;
        float k3 = 1.0f - (float)Math.exp(-speed3 * Math.max(0.0f, Math.min(dt3, 0.1f)));
        this.mobListAnim += (this.mobListAnimTarget - this.mobListAnim) * k3;
        if (this.pendingPage2 != null && this.mobListAnimTarget == 0.0f && this.mobListAnim <= 0.001f) {
            this.page = this.pendingPage2;
            if (this.page == BestiaryPage.HOME) {
                this.selectedTier = null;
                this.selectedMob = null;
                this.visibleMobs.clear();
                this.visibleTiers.clear();
            }
            if (this.page == BestiaryPage.PARASITES) {
                this.tierListEnterFromRight = false;
                this.tierListAnim = 0.0f;
                this.tierListAnimTarget = 1.0f;
                this.tierListAnimLastMs = 0L;
            }
            this.selectedTier = this.pendingTier2;
            this.selectedMob = this.pendingMob2;
            this.pendingPage2 = null;
            this.pendingTier2 = null;
            this.pendingMob2 = null;
            this.mobListAnim = 1.0f;
            this.mobListAnimTarget = 1.0f;
            this.mobListAnimLastMs = 0L;
            if (this.page == BestiaryPage.MOB_DETAIL) {
                this.mobDetailAnim = 0.0f;
                this.mobDetailAnimTarget = 1.0f;
                this.mobDetailAnimLastMs = 0L;
            }
            this.initGui();
        }
        if (this.lorePopupOpen && this.loreAnimTarget == 0.0f && this.loreAnim <= 0.001f) {
            this.loreAnim = 0.0f;
            this.lorePopupOpen = false;
            if (!this.lorePopupClosingRefresh) {
                this.lorePopupClosingRefresh = true;
                this.initGui();
            }
        } else if (this.loreAnimTarget > 0.0f) {
            this.lorePopupClosingRefresh = false;
        }
        String title = this.distort(GuiContext.fmt((String)"item.srparasites.srp_field_guide.name", (Object[])new Object[0]));
        long nowMs4 = GuiContext.systemTime();
        if (this.tierListAnimLastMs == 0L) {
            this.tierListAnimLastMs = nowMs4;
        }
        float dt4 = (float)(nowMs4 - this.tierListAnimLastMs) / 1000.0f;
        this.tierListAnimLastMs = nowMs4;
        float speed4 = 12.0f;
        float k4 = 1.0f - (float)Math.exp(-speed4 * Math.max(0.0f, Math.min(dt4, 0.1f)));
        this.tierListAnim += (this.tierListAnimTarget - this.tierListAnim) * k4;
        if (this.pendingPage3 != null && this.tierListAnimTarget == 0.0f && this.tierListAnim <= 0.001f) {
            BestiaryPage next = this.pendingPage3;
            this.page = this.pendingPage3;
            this.selectedTier = this.pendingTier3;
            this.selectedMob = this.pendingMob3;
            this.pendingPage3 = null;
            this.pendingTier3 = null;
            this.pendingMob3 = null;
            this.tierListAnim = 1.0f;
            this.tierListAnimTarget = 1.0f;
            this.tierListAnimLastMs = 0L;
            if (next == BestiaryPage.MOB_LIST) {
                this.mobListAnim = 0.0f;
                this.mobListAnimTarget = 1.0f;
                this.mobListAnimLastMs = 0L;
            } else if (next == BestiaryPage.MOB_DETAIL) {
                this.mobDetailAnim = 0.0f;
                this.mobDetailAnimTarget = 1.0f;
                this.mobDetailAnimLastMs = 0L;
            }
            this.initGui();
        }
        int titleW = this.fontRendererObj.getStringWidth(title);
        int titleX = this.width - titleW - 10;
        int titleY = 12;
        boolean hoverTitle = mouseX >= titleX && mouseX <= titleX + titleW && mouseY >= titleY && mouseY <= titleY + this.fontRendererObj.FONT_HEIGHT;
        float wiggleX = 0.0f;
        float wiggleY = 0.0f;
        if (hoverTitle) {
            float phase = t;
            wiggleX = (float)Math.sin(phase * 2.0f) * 0.4f;
            wiggleY = (float)Math.sin(phase * 3.0f) * 0.6f;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)wiggleX, (float)wiggleY, (float)0.0f);
        this.drawString(this.fontRendererObj, title, titleX, titleY, 0xFFFFFF);
        GlStateManager.popMatrix();
        if (this.page != BestiaryPage.MOB_DETAIL || this.autoRotateModel) {
            this.spinDeg += partialTicks * 1.5f;
        }
        switch (this.page) {
            case HOME: {
                this.drawCenteredString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.select_category", (Object[])new Object[0])), this.width / 2, 35, 0xAAAAAA);
                break;
            }
            case PARASITES: {
                float a = ParasitesPage.smoothstep(this.tierListAnim);
                int xOff = this.getTierPageXOff();
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.translate((float)xOff, (float)0.0f, (float)0.0f);
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)a);
                this.drawTierPanelBackground(18);
                this.drawString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.parasite_tiers", (Object[])new Object[0])), 20, 40, 0xFFFFFF);
                this.drawListScrollbar(true);
                if (this.selectedTier != null && (this.pendingPage3 != BestiaryPage.MOB_LIST || this.tierListAnimTarget != 0.0f)) {
                    this.drawTierPreview(this.selectedTier, 180, 70);
                }
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
                break;
            }
            case MOB_LIST: {
                if (this.selectedTier == null) break;
                float a = ParasitesPage.smoothstep(this.mobListAnim);
                int leftOffX = -230;
                int rightOffX = this.width + 60;
                int leftXOff = (int)((float)leftOffX * (1.0f - a));
                int rightXOff = (int)((float)rightOffX * (1.0f - a));
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)leftXOff, (float)0.0f, (float)0.0f);
                this.drawTierPanelBackground(18);
                String tierLabel = this.distort(GuiContext.fmt((String)("bestiary.tier." + this.selectedTier.name().toLowerCase(Locale.ROOT)), (Object[])new Object[0]));
                this.drawString(this.fontRendererObj, this.distort(GuiContext.fmt((String)"bestiary.tier_label", (Object[])new Object[]{tierLabel})), 20, 40, 0xFFFFFF);
                this.drawListScrollbar(false);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)rightXOff, (float)0.0f, (float)0.0f);
                this.drawMobListWithRenders(this.visibleMobs, 190);
                GlStateManager.popMatrix();
                break;
            }
            case MOB_DETAIL: {
                if (this.selectedMob == null) break;
                float a = ParasitesPage.smoothstep(this.mobDetailAnim);
                int offX = (int)((1.0f - a) * (float)(this.width + 60));
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.translate((float)offX, (float)0.0f, (float)0.0f);
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)a);
                this.drawMobDetail(this.selectedMob, 20, 40, mouseX - offX, mouseY, partialTicks);
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null && this.lorePopupOpen && !this.isBgScreenActive() && this.loreAnim > 0.0f) {
            IBestiaryProgress prog = BestiaryCapability.get(this.player);
            int kills = prog != null ? prog.getKills(this.selectedMob.mobId) : 0;
            int loreMin = 10;
            if (this.selectedMob.minLoreKill > 0) {
                loreMin = this.selectedMob.minLoreKill;
            }
            if (kills >= loreMin) {
                int boxW = Math.min(300, this.width - 40);
                int boxH = Math.min(160, this.height - 60);
                int boxX = (this.width - boxW) / 2;
                int boxY = (this.height - boxH) / 2;
                float a = Math.max(0.0f, Math.min(1.0f, this.loreAnim));
                float eased = a * a * (3.0f - 2.0f * a);
                int offY = this.height + 20;
                int animY = (int)((float)offY + (float)(boxY - offY) * eased);
                this.lorePopupX = boxX;
                this.lorePopupY = animY;
                this.lorePopupW = boxW;
                this.lorePopupH = boxH;
                int dimA = (int)(136.0f * eased);
                ParasitesPage.drawRect((int)0, (int)0, (int)this.width, (int)this.height, (int)(dimA << 24));
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)eased);
                this.drawPanel(TEX_LORE_BG, boxX, animY, boxW, boxH);
                String lore = this.distort(GuiContext.fmt((String)ParasitesPage.loreKeyFromMobId(this.selectedMob.mobId), (Object[])new Object[0]));
                int tx = boxX + 10;
                int ty = animY + 10;
                int tw = boxW - 20;
                int th = boxH - 40;
                int sbW = 6;
                int sbPad = 2;
                int textW = tw - (sbW + sbPad);
                this.loreTextX = tx;
                this.loreTextY = ty;
                this.loreTextW = textW;
                this.loreTextH = th;
                int textA = (int)(255.0f * eased);
                int loreColor = textA << 24 | 0x2E2E2E;
                List loreLines = this.fontRendererObj.listFormattedStringToWidth(lore, this.loreTextW);
                int lineH = this.fontRendererObj.FONT_HEIGHT;
                int lineStep = lineH + 1;
                this.loreContentH = Math.max(0, loreLines.size() * lineStep);
                this.clampLoreScroll();
                this.enableScissor(this.loreTextX, this.loreTextY, this.loreTextW, this.loreTextH);
                int drawY = this.loreTextY - this.loreScrollPx;
                for (int i = 0; i < loreLines.size(); ++i) {
                    int yLineTop = drawY;
                    int yLineBot = yLineTop + lineH;
                    if (yLineBot >= this.loreTextY && yLineTop <= this.loreTextY + this.loreTextH) {
                        this.fontRendererObj.drawString((String)loreLines.get(i), this.loreTextX, yLineTop, loreColor);
                    }
                    drawY += lineStep;
                }
                this.disableScissor();
                this.loreScrollTrackX = tx + textW + sbPad;
                this.loreScrollTrackY = ty;
                this.loreScrollTrackW = sbW;
                this.loreScrollTrackH = th;
                if (this.loreContentH > this.loreTextH) {
                    int trackBgA = (int)(85.0f * eased);
                    int thumbA = (int)(170.0f * eased);
                    int trackBg = trackBgA << 24 | 0;
                    int thumbBg = thumbA << 24 | 0;
                    ParasitesPage.drawRect((int)this.loreScrollTrackX, (int)this.loreScrollTrackY, (int)(this.loreScrollTrackX + this.loreScrollTrackW), (int)(this.loreScrollTrackY + this.loreScrollTrackH), (int)trackBg);
                    int maxScroll = this.loreContentH - this.loreTextH;
                    int thumbH = (int)((float)this.loreTextH * (float)this.loreTextH / (float)this.loreContentH);
                    thumbH = Math.max(10, Math.min(this.loreTextH, thumbH));
                    int thumbY = this.loreTextY;
                    if (maxScroll > 0) {
                        float frac = (float)this.loreScrollPx / (float)maxScroll;
                        thumbY = this.loreTextY + (int)((float)(this.loreTextH - thumbH) * frac);
                    }
                    this.loreScrollThumbY = thumbY;
                    this.loreScrollThumbH = thumbH;
                    ParasitesPage.drawRect((int)this.loreScrollTrackX, (int)thumbY, (int)(this.loreScrollTrackX + this.loreScrollTrackW), (int)(thumbY + thumbH), (int)thumbBg);
                } else {
                    this.loreScrollThumbY = this.loreTextY;
                    this.loreScrollThumbH = this.loreTextH;
                }
                this.loreBackW = 80;
                this.loreBackH = 20;
                this.loreBackX = boxX + boxW - this.loreBackW - 10;
                this.loreBackY = animY + boxH - this.loreBackH - 10;
                int btnBgA = (int)(170.0f * eased);
                int btnBg = btnBgA << 24 | 0;
                ParasitesPage.drawRect((int)this.loreBackX, (int)this.loreBackY, (int)(this.loreBackX + this.loreBackW), (int)(this.loreBackY + this.loreBackH), (int)btnBg);
                String back = this.distort(GuiContext.fmt((String)"gui.back", (Object[])new Object[0]));
                int backW = this.fontRendererObj.getStringWidth(back);
                int backX = this.loreBackX + (this.loreBackW - backW) / 2;
                int backY = this.loreBackY + (this.loreBackH - this.fontRendererObj.FONT_HEIGHT) / 2;
                int backColor = textA << 24 | 0xFFFFFF;
                this.fontRendererObj.drawString(back, backX, backY, backColor);
                GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            } else {
                this.loreAnimTarget = 0.0f;
                this.lorePopupClosingRefresh = false;
            }
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.isBgScreenActive() && this.selectedMob != null) {
            this.ensurePoseFields();
            int panelX = 10;
            int panelY = this.height - 96;
            ParasitesPage.drawRect((int)(panelX - 4), (int)(panelY - 6), (int)(panelX + 170), (int)(panelY + 52), (int)-1442840576);
            String yawL = this.distort(GuiContext.fmt((String)"bestiary.pose.yaw", (Object[])new Object[0]));
            String pitchL = this.distort(GuiContext.fmt((String)"bestiary.pose.pitch", (Object[])new Object[0]));
            String zoomL = this.distort(GuiContext.fmt((String)"bestiary.pose.zoom", (Object[])new Object[0]));
            String panXL = this.distort(GuiContext.fmt((String)"bestiary.pose.panx", (Object[])new Object[0]));
            String panYL = this.distort(GuiContext.fmt((String)"bestiary.pose.pany", (Object[])new Object[0]));
            this.fontRendererObj.drawString(yawL, panelX, panelY + 2, 0xFFFFFF);
            this.fontRendererObj.drawString(pitchL, panelX, panelY + 16, 0xFFFFFF);
            this.fontRendererObj.drawString(zoomL, panelX, panelY + 30, 0xFFFFFF);
            this.fontRendererObj.drawString(panXL, panelX + 74, panelY + 2, 0xFFFFFF);
            this.fontRendererObj.drawString(panYL, panelX + 74, panelY + 16, 0xFFFFFF);
            this.tfYaw.drawTextBox();
            this.tfPitch.drawTextBox();
            this.tfZoom.drawTextBox();
            this.tfPanX.drawTextBox();
            this.tfPanY.drawTextBox();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawRunTooltipIfHovered(mouseX, mouseY);
        this.drawDropItemTooltipIfHovered(mouseX, mouseY);
    }

    private void drawDropItemTooltipIfHovered(int mouseX, int mouseY) {
        if (this.page != BestiaryPage.MOB_DETAIL) {
            return;
        }
        if (this.selectedMob == null) {
            return;
        }
        if (this.hoveredDropStack == null || this.hoveredDropStack.isEmpty()) {
            return;
        }
        if (this.mc.player == null) {
            return;
        }
        List<String> tip = new java.util.ArrayList<>();
        for (net.minecraft.network.chat.Component line : this.hoveredDropStack.getTooltipLines(net.minecraft.world.item.Item.TooltipContext.of(this.mc.level), this.mc.player, net.minecraft.world.item.TooltipFlag.NORMAL)) {
            tip.add(line.getString());
        }
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        this.drawHoveringText(tip, mouseX, mouseY);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
    }

    private void drawRunTooltipIfHovered(int mouseX, int mouseY) {
        GuiButton runBtn = null;
        for (GuiButton b : this.buttonList) {
            if (b == null || !b.visible || b.id != 1003) continue;
            runBtn = b;
            break;
        }
        if (runBtn == null) {
            return;
        }
        if (mouseX >= runBtn.x && mouseX < runBtn.x + runBtn.width && mouseY >= runBtn.y && mouseY < runBtn.y + runBtn.height) {
            List<String> tip = Collections.singletonList(this.distort(GuiContext.fmt((String)"bestiary.tooltip.run_inaccurate", (Object[])new Object[0])));
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.drawHoveringText(tip, mouseX, mouseY);
            GlStateManager.enableDepth();
            GlStateManager.enableLighting();
        }
    }

    private static int clampInt(int v, int lo, int hi) {
        if (v < lo) {
            return lo;
        }
        if (v > hi) {
            return hi;
        }
        return v;
    }

    private void clampDropsScroll() {
        int maxScroll = Math.max(0, this.dropsContentH - this.dropsViewH);
        this.dropsScrollPx = ParasitesPage.clampInt(this.dropsScrollPx, 0, maxScroll);
    }

    private void clampLoreScroll() {
        int maxScroll = Math.max(0, this.loreContentH - this.loreTextH);
        this.loreScrollPx = ParasitesPage.clampInt(this.loreScrollPx, 0, maxScroll);
    }

    private void enableScissor(int x, int y, int w, int h) {
        ScaledResolution sr = new ScaledResolution(this.mc);
        int scale = sr.getScaleFactor();
        int sx = x * scale;
        int sy = this.mc.getWindow().getHeight() - (y + h) * scale;
        int sw = w * scale;
        int sh = h * scale;
        GL11.glEnable((int)3089);
        GL11.glScissor((int)sx, (int)sy, (int)sw, (int)sh);
    }

    private void disableScissor() {
        GL11.glDisable((int)3089);
    }

    private int getTierPageXOff() {
        int off;
        float a = ParasitesPage.smoothstep(this.tierListAnim);
        if (this.tierListEnterFromRight) {
            int rightOffX = this.width + 60;
            off = (int)((float)rightOffX * (1.0f - a));
        } else {
            int leftOffX = -230;
            off = (int)((float)leftOffX * (1.0f - a));
        }
        return off;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (this.page == BestiaryPage.MOB_DETAIL && this.isBgScreenActive() && this.selectedMob != null) {
            boolean used;
            this.ensurePoseFields();
            boolean bl = used = this.tfYaw.textboxKeyTyped(typedChar, keyCode) || this.tfPitch.textboxKeyTyped(typedChar, keyCode) || this.tfZoom.textboxKeyTyped(typedChar, keyCode) || this.tfPanX.textboxKeyTyped(typedChar, keyCode) || this.tfPanY.textboxKeyTyped(typedChar, keyCode);
            if (used) {
                this.applyPoseFromFields();
                return;
            }
            if (keyCode == 28 || keyCode == 156) {
                this.applyPoseFromFields();
                this.syncPoseFieldsFromState();
                return;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    private void drawTierPanelBackground(int extraBottom) {
        int panelX = 18;
        int panelY = this.LIST_VIEW_TOP() - 14;
        int panelW = 164;
        int btnOffset = 4;
        int panelBottom = this.LIST_BOTTOM() + btnOffset + 10 + extraBottom;
        int panelH = panelBottom - panelY;
        ParasitesPage.drawRect((int)(panelX - 1), (int)(panelY - 1), (int)(panelX + panelW + 1), (int)(panelY + panelH + 1), (int)-1442840576);
        ParasitesPage.drawRect((int)panelX, (int)panelY, (int)(panelX + panelW), (int)(panelY + panelH), (int)-2013265920);
    }

    private boolean isMouseOverPoseFields(int mouseX, int mouseY) {
        if (!this.poseControlsInit) {
            return false;
        }
        return this.tfYaw != null && this.tfYaw.getVisible() && this.tfYaw.isFocused() || this.isOver(this.tfYaw, mouseX, mouseY) || this.tfPitch != null && this.tfPitch.getVisible() && this.tfPitch.isFocused() || this.isOver(this.tfPitch, mouseX, mouseY) || this.tfZoom != null && this.tfZoom.getVisible() && this.tfZoom.isFocused() || this.isOver(this.tfZoom, mouseX, mouseY) || this.tfPanX != null && this.tfPanX.getVisible() && this.tfPanX.isFocused() || this.isOver(this.tfPanX, mouseX, mouseY) || this.tfPanY != null && this.tfPanY.getVisible() && this.tfPanY.isFocused() || this.isOver(this.tfPanY, mouseX, mouseY);
    }

    private boolean isOver(GuiTextField tf, int mx, int my) {
        if (tf == null) {
            return false;
        }
        return mx >= tf.xPosition && mx < tf.xPosition + tf.width && my >= tf.yPosition && my < tf.yPosition + tf.height;
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents((boolean)false);
    }

    private void drawTierPreview(ParasiteTier tier, int x, int y) {
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        if (prog == null) {
            return;
        }
        int shown = 0;
        for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
            if (e.tier != tier || !ParasitesPage.isKnown(prog, e)) continue;
            String name = this.distort(GuiContext.fmt((String)e.nameKey, (Object[])new Object[0]));
            this.fontRendererObj.drawString(name, x + 30, y + 4, 0xDDDDDD);
            float scale = SRPBestiaryRegistry.getRenderScale(e.mobId);
            this.renderEntityPreview(e.mobId, x + 14, y + 14, 28, 28, this.spinDeg, 3, 0.92f);
            y += 32;
            if (++shown < 5) continue;
            break;
        }
        if (shown == 0) {
            this.fontRendererObj.drawString(this.distort(GuiContext.fmt((String)"bestiary.parasites.unlock_tier_hint", (Object[])new Object[0])), x, y, 0x777777);
        }
    }

    private void syncPoseFieldsFromState() {
        if (!this.poseControlsInit) {
            return;
        }
        if (this.suppressPoseFieldUpdates) {
            return;
        }
        if (this.page != BestiaryPage.MOB_DETAIL || !this.isBgScreenActive() || this.selectedMob == null) {
            return;
        }
        this.suppressPoseFieldUpdates = true;
        try {
            this.tfYaw.setText(String.format(Locale.US, "%.1f", Float.valueOf(this.manualYawDeg)));
            this.tfPitch.setText(String.format(Locale.US, "%.1f", Float.valueOf(this.manualPitchDeg)));
            this.tfZoom.setText(String.format(Locale.US, "%.2f", Float.valueOf(this.modelZoom)));
            this.tfPanX.setText(Integer.toString(this.modelPanX));
            this.tfPanY.setText(Integer.toString(this.modelPanY));
        }
        finally {
            this.suppressPoseFieldUpdates = false;
        }
    }

    private void resetModelView() {
        this.modelDragActive = false;
        this.autoRotateModel = true;
        this.manualYawDeg = 0.0f;
        this.manualPitchDeg = 0.0f;
        this.dragStartMouseX = 0;
        this.dragStartMouseY = 0;
        this.dragStartYawDeg = 0.0f;
        this.dragStartPitchDeg = 0.0f;
        this.modelZoom = 1.0f;
        this.modelRun = false;
    }

    private void drawMobListWithRenders(List<BestiaryEntry> mobs, int xRight) {
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        if (prog == null) {
            return;
        }
        int panelPadX = 8;
        int panelPadY = 4;
        int iconBoxW = 28;
        int iconBoxH = 28;
        int textGap = 8;
        int panelX = xRight - 8;
        int panelW = this.width - 8 - panelX;
        int rowY = this.LIST_VIEW_TOP() - this.scrollMobs;
        for (BestiaryEntry e : mobs) {
            int rowTop = rowY;
            int rowBot = rowTop + 28;
            if (rowBot > this.LIST_VIEW_TOP() && rowTop < this.LIST_BOTTOM()) {
                int centerY = rowTop + 14;
                int pY = rowTop - 4;
                int pH = 36;
                ParasitesPage.drawRect((int)(panelX - 1), (int)(pY - 1), (int)(panelX + panelW + 1), (int)(pY + pH + 1), (int)-1442840576);
                ParasitesPage.drawRect((int)panelX, (int)pY, (int)(panelX + panelW), (int)(pY + pH), (int)-2013265920);
                int iconCenterX = xRight + 14;
                int iconCenterY = centerY;
                float rs = SRPBestiaryRegistry.getRenderScale(e.mobId);
                float thumbShrink = 0.8f / Math.max(1.0f, rs);
                int thumbMargin = 10;
                this.renderEntityPreview(e.mobId, iconCenterX, iconCenterY, 28, 28, this.spinDeg, thumbMargin, thumbShrink);
                int kills = prog.getKills(e.mobId);
                String name = this.distort(GuiContext.fmt((String)e.nameKey, (Object[])new Object[0]));
                String killsLabel = this.distort(GuiContext.fmt((String)"bestiary.kills_label", (Object[])new Object[0]));
                String raw = name + "  (\u00a77" + killsLabel + ": " + kills + "\u00a7r)";
                int textX = xRight + 28 + 8;
                int avail = panelX + panelW - textX - 6;
                String shown = this.fontRendererObj.trimStringToWidth(raw, Math.max(32, avail));
                int textY = centerY - this.fontRendererObj.FONT_HEIGHT / 2;
                this.fontRendererObj.drawString(shown, textX, textY, 0xDDDDDD);
            }
            rowY += 36;
        }
        if (mobs.isEmpty()) {
            this.fontRendererObj.drawString(this.distort(GuiContext.fmt((String)"bestiary.no_mobs_unlocked_in_tier", (Object[])new Object[0])), xRight, this.LIST_VIEW_TOP(), 0x777777);
        }
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        boolean overThumb;
        int viewH;
        boolean tiers;
        int count;
        int contentH;
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if ((this.page == BestiaryPage.PARASITES || this.page == BestiaryPage.MOB_LIST) && mouseButton == 0 && (contentH = (count = (tiers = this.page == BestiaryPage.PARASITES) ? this.visibleTiers.size() : this.visibleMobs.size()) * 36) > (viewH = this.LIST_BOTTOM() - this.LIST_VIEW_TOP())) {
            boolean overThumb2;
            this.drawListScrollbar(tiers);
            boolean bl = overThumb2 = mouseX >= this.listScrollTrackX && mouseX < this.listScrollTrackX + this.listScrollTrackW && mouseY >= this.listScrollThumbY && mouseY < this.listScrollThumbY + this.listScrollThumbH;
            if (overThumb2) {
                this.listScrollDrag = true;
                this.listScrollIsTiers = tiers;
                this.listScrollDragStartMouseY = mouseY;
                this.listScrollDragStartScrollPx = tiers ? this.scrollTiers : this.scrollMobs;
                return;
            }
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null && this.lorePopupOpen && !this.isBgScreenActive() && this.loreAnim > 0.0f && mouseButton == 0 && mouseX >= this.loreBackX && mouseX < this.loreBackX + this.loreBackW && mouseY >= this.loreBackY && mouseY < this.loreBackY + this.loreBackH) {
            this.loreAnimTarget = 0.0f;
            this.lorePopupClosingRefresh = false;
            return;
        }
        if (this.lorePopupOpen) {
            if (mouseButton == 0 && this.loreContentH > this.loreTextH) {
                boolean bl = overThumb = mouseX >= this.loreScrollTrackX && mouseX < this.loreScrollTrackX + this.loreScrollTrackW && mouseY >= this.loreScrollThumbY && mouseY < this.loreScrollThumbY + this.loreScrollThumbH;
                if (overThumb) {
                    this.loreScrollDrag = true;
                    this.loreScrollDragStartMouseY = mouseY;
                    this.loreScrollDragStartScrollPx = this.loreScrollPx;
                    return;
                }
            }
            return;
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.isBgScreenActive() && this.selectedMob != null) {
            this.ensurePoseFields();
            this.tfYaw.mouseClicked(mouseX, mouseY, mouseButton);
            this.tfPitch.mouseClicked(mouseX, mouseY, mouseButton);
            this.tfZoom.mouseClicked(mouseX, mouseY, mouseButton);
            this.tfPanX.mouseClicked(mouseX, mouseY, mouseButton);
            this.tfPanY.mouseClicked(mouseX, mouseY, mouseButton);
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null && mouseButton == 0) {
            boolean overModel;
            boolean bl = overModel = mouseX >= this.modelRectX && mouseX < this.modelRectX + this.modelRectW && mouseY >= this.modelRectY && mouseY < this.modelRectY + this.modelRectH;
            if ((this.isBgScreenActive() || overModel) && !this.isMouseOverAnyButton(mouseX, mouseY) && !this.isMouseOverPoseFields(mouseX, mouseY)) {
                this.autoRotateModel = false;
                this.modelDragActive = true;
                this.dragStartMouseX = mouseX;
                this.dragStartMouseY = mouseY;
                this.dragStartYawDeg = this.manualYawDeg;
                this.dragStartPitchDeg = this.manualPitchDeg;
                this.initGui();
            }
        }
        if (!this.lorePopupOpen && this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null && mouseButton == 0 && this.dropsContentH > this.dropsViewH) {
            boolean bl = overThumb = mouseX >= this.dropsScrollTrackX && mouseX < this.dropsScrollTrackX + this.dropsScrollTrackW && mouseY >= this.dropsScrollThumbY && mouseY < this.dropsScrollThumbY + this.dropsScrollThumbH;
            if (overThumb) {
                this.dropsScrollDrag = true;
                this.dropsScrollDragStartMouseY = mouseY;
                this.dropsScrollDragStartScrollPx = this.dropsScrollPx;
                return;
            }
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null && mouseButton == 1 && this.isBgScreenActive()) {
            this.modelPanActive = true;
            this.panStartMouseX = mouseX;
            this.panStartMouseY = mouseY;
            this.panStartX = this.modelPanX;
            this.panStartY = this.modelPanY;
        }
    }

    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        this.loreScrollDrag = false;
        this.dropsScrollDrag = false;
        if (state == 0) {
            this.modelDragActive = false;
        }
        if (state == 1) {
            this.modelPanActive = false;
        }
    }

    private static ItemStack stackFromId(String id) {
        if (id == null || id.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Item it = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse(id = id.trim()));
        if (it == net.minecraft.world.item.Items.AIR) {
            it = null;
        }
        if (it == null) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(it);
    }

    private boolean isBgScreenActive() {
        return this.modelBgScreen != 0;
    }

    private int getBgScreenColor() {
        if (this.modelBgScreen == 2) {
            return -16776961;
        }
        return -16711936;
    }

    private String getBgScreenLabelKey() {
        switch (this.modelBgScreen) {
            case 1: {
                return "bestiary.controls.greenscreen.green";
            }
            case 2: {
                return "bestiary.controls.greenscreen.blue";
            }
        }
        return "bestiary.controls.greenscreen.off";
    }

    private void renderItemIconSway(ItemStack stack, int x, int y, float t, int index) {
        if (stack == null || stack.isEmpty()) {
            return;
        }
        float phase = t * 0.9f + (float)index * 0.35f;
        float ang = (float)Math.sin(phase) * 6.0f;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)(x + 8), (float)(y + 8), (float)0.0f);
        GlStateManager.rotate((float)ang, (float)0.0f, (float)0.0f, (float)1.0f);
        GlStateManager.translate((float)(-(x + 8)), (float)(-(y + 8)), (float)0.0f);
        RenderHelper.enableGUIStandardItemLighting();
        GuiScreen.renderItem(stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    private void loadDropCacheIfNeeded() {
        File f = this.getMobsCfgFile();
        if (f == null || !f.exists() || !f.isFile()) {
            this.dropsLoaded = true;
            this.dropsByCategory.clear();
            this.knownCategories.clear();
            this.dropsLastModified = -1L;
            return;
        }
        long lm = f.lastModified();
        if (this.dropsLoaded && lm == this.dropsLastModified) {
            return;
        }
        this.dropsLoaded = true;
        this.dropsLastModified = lm;
        this.dropsByCategory.clear();
        this.knownCategories.clear();
        String currentCategory = null;
        boolean readingLoot = false;
        ArrayList<DropEntry> currentLoot = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(f), StandardCharsets.UTF_8));){
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts;
                String t = line.trim();
                if (t.isEmpty() || t.startsWith("#")) continue;
                if (!readingLoot && t.startsWith("\"") && t.endsWith("{")) {
                    int q2 = t.indexOf(34, 1);
                    if (q2 <= 1) continue;
                    currentCategory = t.substring(1, q2);
                    this.knownCategories.add(currentCategory);
                    continue;
                }
                if (!readingLoot && t.startsWith("}")) {
                    currentCategory = null;
                    continue;
                }
                if (!readingLoot && currentCategory != null && t.startsWith("S:\"") && t.contains("Loot Table\" <")) {
                    readingLoot = true;
                    currentLoot = new ArrayList<DropEntry>();
                    continue;
                }
                if (!readingLoot) continue;
                if (t.startsWith(">")) {
                    readingLoot = false;
                    if (currentCategory != null && currentLoot != null) {
                        this.dropsByCategory.put(currentCategory, currentLoot);
                    }
                    currentLoot = null;
                    continue;
                }
                if (t.startsWith("#") || (parts = t.split(";")).length < 4) continue;
                String itemId = parts[0].trim();
                int chance = ParasitesPage.parseIntSafe(parts[1], 0);
                int amount = ParasitesPage.parseIntSafe(parts[2], 1);
                boolean looting = "true".equalsIgnoreCase(parts[3].trim());
                if (itemId.isEmpty()) continue;
                currentLoot.add(new DropEntry(itemId, chance, amount, looting));
            }
        }
        catch (Throwable ex) {
            System.out.println("[SRP][BESTIARY][DROPS] Failed reading cfg: " + ex);
            this.dropsByCategory.clear();
            this.knownCategories.clear();
        }
    }

    private List<String> buildCategoryCandidates(String mobId) {
        String[] prefixes;
        ArrayList<String> out = new ArrayList<String>();
        if (mobId == null || mobId.isEmpty()) {
            return out;
        }
        ResourceLocation rl = mobId.indexOf(58) >= 0 ? ResourceLocation.parse(mobId) : ResourceLocation.fromNamespaceAndPath("srparasites", mobId);
        String domain = rl.getNamespace();
        String path = rl.getPath();
        out.add(domain + ":" + path);
        for (String p : prefixes = new String[]{"pri_", "ada_", "sim_", "fer_"}) {
            if (!path.startsWith(p) || path.length() <= p.length()) continue;
            out.add(domain + ":" + path.substring(p.length()));
        }
        int us = path.indexOf(95);
        if (us > 0 && us + 1 < path.length()) {
            out.add(domain + ":" + path.substring(us + 1));
        }
        if (path.startsWith("carrier_") && path.length() > "carrier_".length()) {
            out.add(domain + ":" + path.substring("carrier_".length()));
        }
        LinkedHashSet<String> uniq = new LinkedHashSet<String>(out);
        return new ArrayList<String>(uniq);
    }

    private String resolveCfgCategoryForMob(String mobId) {
        if (mobId == null) {
            return null;
        }
        String override = CFG_CATEGORY_TO_FAMILY.get(mobId);
        if (override != null && !override.isEmpty()) {
            return override;
        }
        if (this.knownCategories.contains(mobId)) {
            return mobId;
        }
        for (String c : this.buildCategoryCandidates(mobId)) {
            if (!this.knownCategories.contains(c)) continue;
            return c;
        }
        return null;
    }

    private List<DropEntry> getDropsForMob(String mobId) {
        this.loadDropCacheIfNeeded();
        String cat = this.resolveCfgCategoryForMob(mobId);
        if (cat == null) {
            return Collections.emptyList();
        }
        List<DropEntry> drops = this.dropsByCategory.get(cat);
        return drops != null ? drops : Collections.emptyList();
    }

    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        float pixelsPerScroll;
        int trackTravel;
        int maxScroll;
        int dy;
        int dx;
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.listScrollDrag && clickedMouseButton == 0) {
            int count = this.listScrollIsTiers ? this.visibleTiers.size() : this.visibleMobs.size();
            int contentH = count * 36;
            int viewH = this.LIST_BOTTOM() - this.LIST_VIEW_TOP();
            int maxScroll2 = Math.max(1, contentH - viewH);
            int trackTravel2 = Math.max(1, viewH - this.listScrollThumbH);
            int dy2 = mouseY - this.listScrollDragStartMouseY;
            int newScroll = this.listScrollDragStartScrollPx + (int)((float)dy2 * ((float)maxScroll2 / (float)trackTravel2));
            if (this.listScrollIsTiers) {
                this.scrollTiers = newScroll;
                this.applyTierScrollLayout();
            } else {
                this.scrollMobs = newScroll;
                this.applyMobScrollLayout();
            }
            return;
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.modelDragActive && clickedMouseButton == 0) {
            dx = mouseX - this.dragStartMouseX;
            dy = mouseY - this.dragStartMouseY;
            float yawSens = 0.6f;
            float pitchSens = 0.6f;
            this.manualYawDeg = this.dragStartYawDeg - (float)dx * yawSens;
            this.manualPitchDeg = this.dragStartPitchDeg + (float)dy * pitchSens;
            this.manualPitchDeg = Math.max(-60.0f, Math.min(60.0f, this.manualPitchDeg));
            this.syncPoseFieldsFromState();
        }
        if (!this.lorePopupOpen && this.page == BestiaryPage.MOB_DETAIL && this.selectedMob != null && this.dropsScrollDrag && this.dropsContentH > this.dropsViewH) {
            maxScroll = this.dropsContentH - this.dropsViewH;
            trackTravel = Math.max(1, this.dropsViewH - this.dropsScrollThumbH);
            int dy3 = mouseY - this.dropsScrollDragStartMouseY;
            pixelsPerScroll = (float)maxScroll / (float)trackTravel;
            this.dropsScrollPx = this.dropsScrollDragStartScrollPx + (int)((float)dy3 * pixelsPerScroll);
            this.clampDropsScroll();
            return;
        }
        if (this.lorePopupOpen && this.loreScrollDrag && this.loreContentH > this.loreTextH) {
            maxScroll = this.loreContentH - this.loreTextH;
            trackTravel = Math.max(1, this.loreTextH - this.loreScrollThumbH);
            int dy4 = mouseY - this.loreScrollDragStartMouseY;
            pixelsPerScroll = (float)maxScroll / (float)trackTravel;
            this.loreScrollPx = this.loreScrollDragStartScrollPx + (int)((float)dy4 * pixelsPerScroll);
            this.clampLoreScroll();
            return;
        }
        if (this.page == BestiaryPage.MOB_DETAIL && this.modelPanActive && clickedMouseButton == 1 && this.isBgScreenActive()) {
            dx = mouseX - this.panStartMouseX;
            dy = mouseY - this.panStartMouseY;
            this.modelPanX = this.panStartX + dx;
            this.modelPanY = this.panStartY + dy;
            this.syncPoseFieldsFromState();
        }
    }

    private void ensurePoseFields() {
        if (this.poseControlsInit && this.lastW == this.width && this.lastH == this.height) {
            return;
        }
        String yawTxt = this.tfYaw != null ? this.tfYaw.getText() : String.format(Locale.US, "%.1f", Float.valueOf(this.manualYawDeg));
        String pitchTxt = this.tfPitch != null ? this.tfPitch.getText() : String.format(Locale.US, "%.1f", Float.valueOf(this.manualPitchDeg));
        String zoomTxt = this.tfZoom != null ? this.tfZoom.getText() : String.format(Locale.US, "%.2f", Float.valueOf(this.modelZoom));
        String panXTxt = this.tfPanX != null ? this.tfPanX.getText() : Integer.toString(this.modelPanX);
        String panYTxt = this.tfPanY != null ? this.tfPanY.getText() : Integer.toString(this.modelPanY);
        this.lastW = this.width;
        this.lastH = this.height;
        int panelX = 10;
        int panelY = this.height - 96;
        int w = 40;
        int h = 12;
        int gapY = 14;
        int col1X = panelX + 34;
        int col2X = panelX + 108;
        this.tfYaw = new GuiTextField(3001, this.fontRendererObj, col1X, panelY + 0, w, h);
        this.tfPitch = new GuiTextField(3002, this.fontRendererObj, col1X, panelY + gapY, w, h);
        this.tfZoom = new GuiTextField(3003, this.fontRendererObj, col1X, panelY + gapY * 2, w, h);
        this.tfPanX = new GuiTextField(3004, this.fontRendererObj, col2X, panelY + 0, w, h);
        this.tfPanY = new GuiTextField(3005, this.fontRendererObj, col2X, panelY + gapY, w, h);
        this.tfYaw.setText(yawTxt);
        this.tfPitch.setText(pitchTxt);
        this.tfZoom.setText(zoomTxt);
        this.tfPanX.setText(panXTxt);
        this.tfPanY.setText(panYTxt);
        this.tfYaw.setEnableBackgroundDrawing(true);
        this.tfPitch.setEnableBackgroundDrawing(true);
        this.tfZoom.setEnableBackgroundDrawing(true);
        this.tfPanX.setEnableBackgroundDrawing(true);
        this.tfPanY.setEnableBackgroundDrawing(true);
        this.tfYaw.setMaxStringLength(10);
        this.tfPitch.setMaxStringLength(10);
        this.tfZoom.setMaxStringLength(10);
        this.tfPanX.setMaxStringLength(6);
        this.tfPanY.setMaxStringLength(6);
        this.poseControlsInit = true;
    }

    private void resetPoseFields() {
        this.manualYawDeg = 0.0f;
        this.manualPitchDeg = 0.0f;
        this.modelZoom = 1.0f;
        this.modelPanX = 0;
        this.modelPanY = 0;
        this.autoRotateModel = false;
        if (this.poseControlsInit) {
            this.tfYaw.setText("0");
            this.tfPitch.setText("0");
            this.tfZoom.setText("1.00");
            this.tfPanX.setText("0");
            this.tfPanY.setText("0");
        }
    }

    private static float parseFloatSafe(String s, float def) {
        try {
            return Float.parseFloat(s.trim());
        }
        catch (Throwable t) {
            return def;
        }
    }

    private static int parseIntSafe(String s, int def) {
        try {
            return Integer.parseInt(s.trim());
        }
        catch (Throwable t) {
            return def;
        }
    }

    private void applyPoseFromFields() {
        if (!this.poseControlsInit) {
            return;
        }
        float yaw = ParasitesPage.parseFloatSafe(this.tfYaw.getText(), this.manualYawDeg);
        float pitch = ParasitesPage.parseFloatSafe(this.tfPitch.getText(), this.manualPitchDeg);
        float zoom = ParasitesPage.parseFloatSafe(this.tfZoom.getText(), this.modelZoom);
        int panX = ParasitesPage.parseIntSafe(this.tfPanX.getText(), this.modelPanX);
        int panY = ParasitesPage.parseIntSafe(this.tfPanY.getText(), this.modelPanY);
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch));
        zoom = Math.max(0.35f, Math.min(2.75f, zoom));
        this.manualYawDeg = yaw;
        this.manualPitchDeg = pitch;
        this.modelZoom = zoom;
        this.modelPanX = panX;
        this.modelPanY = panY;
        this.autoRotateModel = false;
        this.modelDragActive = false;
    }

    private void drawListScrollbar(boolean tiers) {
        int count = tiers ? this.visibleTiers.size() : this.visibleMobs.size();
        int contentH = count * 36;
        int viewTop = this.LIST_VIEW_TOP();
        int viewH = this.LIST_BOTTOM() - viewTop;
        if (contentH <= viewH) {
            return;
        }
        int scroll = tiers ? this.scrollTiers : this.scrollMobs;
        int maxScroll = Math.max(1, contentH - viewH);
        this.listScrollTrackW = 5;
        this.listScrollTrackX = 178;
        this.listScrollTrackY = viewTop;
        this.listScrollTrackH = viewH;
        this.listScrollThumbH = Math.max(12, (int)((float)viewH / (float)contentH * (float)viewH));
        int travel = Math.max(1, viewH - this.listScrollThumbH);
        this.listScrollThumbY = this.listScrollTrackY + (int)((float)scroll / (float)maxScroll * (float)travel);
        ParasitesPage.drawRect((int)this.listScrollTrackX, (int)this.listScrollTrackY, (int)(this.listScrollTrackX + this.listScrollTrackW), (int)(this.listScrollTrackY + this.listScrollTrackH), (int)0x66000000);
        ParasitesPage.drawRect((int)this.listScrollTrackX, (int)this.listScrollThumbY, (int)(this.listScrollTrackX + this.listScrollTrackW), (int)(this.listScrollThumbY + this.listScrollThumbH), (int)-1437248171);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawMobDetail(BestiaryEntry e, int x, int y, int mouseX, int mouseY, float partialTicks) {
        float yaw;
        String statsLine;
        this.hoveredDropStack = ItemStack.EMPTY;
        IBestiaryProgress prog = BestiaryCapability.get(this.player);
        if (prog == null) {
            this.fontRendererObj.drawString(this.distort(GuiContext.fmt((String)"bestiary.capability_missing_client", (Object[])new Object[0])), x, y, 0xFF5555);
            return;
        }
        int kills = prog.getKills(e.mobId);
        String name = this.distort(GuiContext.fmt((String)e.nameKey, (Object[])new Object[0]));
        float t = ((float)this.player.tickCount + partialTicks) / 10.0f;
        int pad = 6;
        int sectionGap = 6;
        int lineH = this.fontRendererObj.FONT_HEIGHT;
        int pageLeft = x;
        int pageRight = this.width - 8;
        int pageW = Math.max(140, pageRight - pageLeft);
        int loreMinH = lineH * 3 + 8;
        int descW = (int)((float)pageW * 0.55f);
        int modelW = pageW - descW - 6;
        int curY = y;
        int bgX = pageLeft - 6;
        int bgY = curY - 6;
        int bgW = pageW + 12;
        int bgH = 200;
        this.drawPanel(TEX_BG, bgX, bgY, bgW, bgH);
        String label = this.distort(GuiContext.fmt((String)"lore.srparasites.compendium", (Object[])new Object[0]));
        int labelAvailW = Math.max(80, this.width - x - 8);
        curY = this.drawTextPanelLeftWiggle(TEX_LABEL_BG, label, x, curY, labelAvailW, 0x2E2E2E, mouseX, mouseY, t) + 2;
        String killsLabel = this.distort(GuiContext.fmt((String)"bestiary.kills_label", (Object[])new Object[0]));
        String nameText = String.format("%s  (%s: %d)", name, killsLabel, kills);
        int nameAvailW = Math.max(80, this.width - x - 8);
        curY = this.drawTextPanelLeftWiggle(TEX_NAME_BG, nameText, x, curY, nameAvailW, 0x2E2E2E, mouseX, mouseY, t) + 6;
        int descLeft = pageLeft;
        int modelLeft = pageLeft + descW + 6;
        int contentTop = curY;
        String descKey = "bestiary." + e.mobId.replace(':', '.') + ".desc";
        String desc = GuiContext.hasKey((String)descKey) ? this.distort(GuiContext.fmt((String)descKey, (Object[])new Object[0])) : "";
        List<? extends String> lines = this.fontRendererObj.listFormattedStringToWidth(desc, descW);
        int textY = contentTop;
        if (lines.isEmpty()) {
            textY = contentTop + lineH;
        } else {
            for (String line : lines) {
                this.fontRendererObj.drawString(line, descLeft, textY, 0xCCCCCC);
                textY += lineH + 2;
            }
        }
        int statsTop = textY + 6;
        int statMin = 3;
        if (this.selectedMob != null && this.selectedMob.minStatKill > 0) {
            statMin = this.selectedMob.minStatKill;
        }
        boolean hasStats = kills >= statMin;
        double hp = 0.0;
        double dmg = 0.0;
        if (hasStats) {
            hp = e.baseHp > 0 ? (double)e.baseHp : this.readEntityStat(e.mobId, Attributes.MAX_HEALTH);
            dmg = e.baseDamage > 0.0f ? (double)e.baseDamage : this.readEntityStat(e.mobId, Attributes.ATTACK_DAMAGE);
            statsLine = this.distort(GuiContext.fmt((String)"bestiary.stats", (Object[])new Object[]{String.valueOf((int)Math.round(hp)), String.valueOf((float)dmg)}));
        } else {
            statsLine = this.distort(GuiContext.fmt((String)"bestiary.more_info_at_n", (Object[])new Object[]{statMin}));
        }
        int statsPadX = 12;
        int statsPadY = 6;
        int statsTextW = this.fontRendererObj.getStringWidth(statsLine);
        int statsW = Math.min(descW, statsTextW + 24);
        int statsPanelH = this.fontRendererObj.FONT_HEIGHT + 6;
        boolean hoverStats = mouseX >= descLeft && mouseX <= descLeft + statsW && mouseY >= statsTop && mouseY <= statsTop + statsPanelH;
        float statsWiggleX = 0.0f;
        float statsWiggleY = 0.0f;
        if (hoverStats) {
            float phase = t;
            statsWiggleX = (float)Math.sin(phase * 2.0f) * 0.4f;
            statsWiggleY = (float)Math.sin(phase * 3.0f) * 0.6f;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)statsWiggleX, (float)statsWiggleY, (float)0.0f);
        this.drawPanel(TEX_STATS_BG, descLeft, statsTop, statsW, statsPanelH);
        int statsTextX = descLeft + (statsW - statsTextW) / 2;
        int statsTextY = statsTop + (statsPanelH - this.fontRendererObj.FONT_HEIGHT) / 2;
        this.fontRendererObj.drawString(statsLine, statsTextX, statsTextY, 0x2E2E2E);
        GlStateManager.popMatrix();
        int leftBottom = statsTop + statsPanelH;
        if (!this.isBgScreenActive()) {
            int dropsMin = statMin;
            int dropTop = leftBottom + 6;
            int dropBoxX = descLeft;
            int dropBoxW = descW;
            int dropPadX = 6;
            int dropPadY = 6;
            String dropTitle = this.distort(GuiContext.hasKey((String)"bestiary.drops") ? GuiContext.fmt((String)"bestiary.drops", (Object[])new Object[0]) : GuiContext.fmt((String)"bestiary.drops_fallback", (Object[])new Object[0]));
            int dropLineH = this.fontRendererObj.FONT_HEIGHT;
            int lineStep = Math.max(dropLineH + 4, 18);
            int bottomLimitY = this.height - 18 - 22;
            int maxDropsBottom = bottomLimitY - 6;
            int headerH = dropPadY + lineStep;
            int availableForText = maxDropsBottom - dropTop - (headerH + dropPadY);
            int visibleLines = Math.max(2, availableForText / lineStep);
            int textAreaH = visibleLines * lineStep;
            int dropBoxH = dropPadY + lineStep + textAreaH + dropPadY;
            int iconSize = 16;
            int iconGap = 4;
            int textLeftInset = 20;
            boolean hoverDrops = mouseX >= dropBoxX && mouseX <= dropBoxX + dropBoxW && mouseY >= dropTop && mouseY <= dropTop + dropBoxH;
            float dropWiggleX = 0.0f;
            float dropWiggleY = 0.0f;
            if (hoverDrops) {
                float phase = t;
                dropWiggleX = (float)Math.sin(phase * 2.0f) * 0.4f;
                dropWiggleY = (float)Math.sin(phase * 3.0f) * 0.6f;
            }
            int wX = (int)dropWiggleX;
            int wY = (int)dropWiggleY;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)dropWiggleX, (float)dropWiggleY, (float)0.0f);
            this.drawPanel(TEX_DROP_BG, dropBoxX, dropTop, dropBoxW, dropBoxH);
            int titleX = dropBoxX + dropPadX;
            int titleY = dropTop + dropPadY;
            this.fontRendererObj.drawString(this.distort(dropTitle + ":"), titleX, titleY, 0x2E2E2E);
            int textX = dropBoxX + dropPadX;
            int dropTextY = titleY + lineStep;
            int textW = dropBoxW - dropPadX * 2;
            int textH = textAreaH;
            this.dropsViewX = textX + wX;
            this.dropsViewY = dropTextY + wY;
            this.dropsViewW = textW;
            this.dropsViewH = textH;
            List<DropEntry> drops = Collections.emptyList();
            if (kills < dropsMin) {
                String msg = this.distort(GuiContext.hasKey((String)"bestiary.loot_unlocks_at_n") ? GuiContext.fmt((String)"bestiary.loot_unlocks_at_n", (Object[])new Object[]{String.valueOf(dropsMin)}) : GuiContext.fmt((String)"bestiary.loot_unlocks_at_n_fallback", (Object[])new Object[]{String.valueOf(dropsMin)}));
                this.enableScissor(this.dropsViewX, this.dropsViewY, this.dropsViewW, this.dropsViewH);
                this.fontRendererObj.drawString(msg, textX, dropTextY, 0x555555);
                this.disableScissor();
                if (this.dropsContentH > this.dropsViewH) {
                    this.dropsScrollTrackW = 5;
                    this.dropsScrollTrackX = this.dropsViewX + this.dropsViewW - this.dropsScrollTrackW;
                    this.dropsScrollTrackY = this.dropsViewY;
                    this.dropsScrollTrackH = this.dropsViewH;
                    this.dropsScrollThumbH = Math.max(12, (int)((float)this.dropsViewH / (float)this.dropsContentH * (float)this.dropsViewH));
                    int maxScroll = Math.max(1, this.dropsContentH - this.dropsViewH);
                    int travel = Math.max(1, this.dropsViewH - this.dropsScrollThumbH);
                    this.dropsScrollThumbY = this.dropsScrollTrackY + (int)((float)this.dropsScrollPx / (float)maxScroll * (float)travel);
                    ParasitesPage.drawRect((int)this.dropsScrollTrackX, (int)this.dropsScrollTrackY, (int)(this.dropsScrollTrackX + this.dropsScrollTrackW), (int)(this.dropsScrollTrackY + this.dropsScrollTrackH), (int)0x66000000);
                    ParasitesPage.drawRect((int)this.dropsScrollTrackX, (int)this.dropsScrollThumbY, (int)(this.dropsScrollTrackX + this.dropsScrollTrackW), (int)(this.dropsScrollThumbY + this.dropsScrollThumbH), (int)-1437248171);
                }
                this.dropsContentH = lineStep;
                this.dropsScrollPx = 0;
            } else {
                drops = this.getDropsForMob(e.mobId);
                int contentLines = Math.max(1, drops.size());
                this.dropsContentH = contentLines * lineStep;
                this.clampDropsScroll();
                this.enableScissor(this.dropsViewX, this.dropsViewY, this.dropsViewW, this.dropsViewH);
                int drawY = dropTextY - this.dropsScrollPx;
                if (drops.isEmpty()) {
                    String none = this.distort(GuiContext.hasKey((String)"bestiary.drops.none") ? GuiContext.fmt((String)"bestiary.drops.none", (Object[])new Object[0]) : GuiContext.fmt((String)"bestiary.drops.none_fallback", (Object[])new Object[0]));
                    this.fontRendererObj.drawString(none, textX, drawY, 0x555555);
                } else {
                    for (int i = 0; i < drops.size(); ++i) {
                        DropEntry d0 = (DropEntry)drops.get(i);
                        int yLineTop = drawY;
                        int yLineBot = yLineTop + dropLineH;
                        if (yLineBot >= dropTextY && yLineTop <= dropTextY + this.dropsViewH) {
                            ItemStack st = ParasitesPage.stackFromId(d0.itemId);
                            if (!st.isEmpty()) {
                                int iconX = textX;
                                int iconY = yLineTop + (lineStep - 16) / 2;
                                this.renderItemIconSway(st, iconX, iconY, t, i);
                                int realIconX = iconX + wX;
                                int realIconY = iconY + wY;
                                if (mouseX >= realIconX && mouseX < realIconX + 16 && mouseY >= realIconY && mouseY < realIconY + 16 && realIconY + 16 >= this.dropsViewY && realIconY <= this.dropsViewY + this.dropsViewH) {
                                    this.hoveredDropStack = st.copy();
                                    this.hoveredDropMouseX = mouseX;
                                    this.hoveredDropMouseY = mouseY;
                                }
                            }
                            String displayName = !st.isEmpty() ? ParasitesPage.forceBlack(st.getHoverName().getString()) : d0.itemId;
                            String lootingTag = d0.looting ? " " + GuiContext.fmt((String)"bestiary.loot_looting", (Object[])new Object[0]) : "";
                            String rowPlain = displayName + " x" + d0.amount + " (" + d0.chance + "%)" + lootingTag;
                            int avail = Math.max(40, this.dropsViewW - 20);
                            String rowDraw = this.isJumbled ? GuiDistortionHelper.jamText(rowPlain) : this.fontRendererObj.trimStringToWidth(rowPlain, avail);
                            int textYRow = yLineTop + (lineStep - dropLineH) / 2;
                            this.fontRendererObj.drawString(rowDraw, textX + 20, textYRow, 0x2E2E2E);
                        }
                        drawY += lineStep;
                    }
                }
                this.disableScissor();
            }
            if (this.dropsContentH > this.dropsViewH) {
                this.dropsScrollTrackW = 5;
                this.dropsScrollTrackX = this.dropsViewX + this.dropsViewW - this.dropsScrollTrackW;
                this.dropsScrollTrackY = this.dropsViewY;
                this.dropsScrollTrackH = this.dropsViewH;
                this.dropsScrollThumbH = Math.max(12, (int)((float)this.dropsViewH / (float)this.dropsContentH * (float)this.dropsViewH));
                int maxScroll = Math.max(1, this.dropsContentH - this.dropsViewH);
                int travel = Math.max(1, this.dropsViewH - this.dropsScrollThumbH);
                this.dropsScrollThumbY = this.dropsScrollTrackY + (int)((float)this.dropsScrollPx / (float)maxScroll * (float)travel);
                ParasitesPage.drawRect((int)this.dropsScrollTrackX, (int)this.dropsScrollTrackY, (int)(this.dropsScrollTrackX + this.dropsScrollTrackW), (int)(this.dropsScrollTrackY + this.dropsScrollTrackH), (int)0x66000000);
                ParasitesPage.drawRect((int)this.dropsScrollTrackX, (int)this.dropsScrollThumbY, (int)(this.dropsScrollTrackX + this.dropsScrollTrackW), (int)(this.dropsScrollThumbY + this.dropsScrollThumbH), (int)-1437248171);
            }
            GlStateManager.popMatrix();
            leftBottom = dropTop + dropBoxH;
        }
        int modelH = Math.max(90, leftBottom - contentTop);
        this.modelRectX = modelLeft;
        this.modelRectY = contentTop;
        this.modelRectW = modelW;
        this.modelRectH = modelH;
        int cx = modelLeft + modelW / 2 + (this.isBgScreenActive() ? this.modelPanX : 0);
        int cy = contentTop + modelH / 2 + (this.isBgScreenActive() ? this.modelPanY : 0);
        int innerMargin = 12;
        int baseW = Math.max(40, modelW - 24);
        int baseH = Math.max(40, modelH - 24);
        float extraShrink = 0.88f;
        int zoomW = Math.max(10, (int)((float)baseW * 0.88f * this.modelZoom));
        int zoomH = Math.max(10, (int)((float)baseH * 0.88f * this.modelZoom));
        if (this.isBgScreenActive()) {
            ParasitesPage.drawRect((int)0, (int)0, (int)this.width, (int)this.height, (int)this.getBgScreenColor());
        } else {
            this.drawPanel(TEX_MODEL_BG, modelLeft, contentTop, modelW, modelH);
        }
        float pitch = this.manualPitchDeg;
        float f = yaw = this.autoRotateModel ? this.spinDeg : this.manualYawDeg;
        if (this.isBgScreenActive() && this.poseControlsInit && !this.tfYaw.isFocused()) {
            this.suppressPoseFieldUpdates = true;
            try {
                this.tfYaw.setText(String.format(Locale.US, "%.1f", Float.valueOf(yaw)));
            }
            finally {
                this.suppressPoseFieldUpdates = false;
            }
        }
        this.renderEntityPreviewDetail(e.mobId, cx, cy, zoomW, zoomH, yaw, pitch);
        if (!this.isBgScreenActive()) {
            float a2 = this.page == BestiaryPage.MOB_DETAIL ? ParasitesPage.smoothstep(this.mobDetailAnim) : 1.0f;
            int warnH = 18;
            int warnW = this.width;
            int warnX = 0;
            int warnY = this.height - warnH;
            int warnOffDown = this.height + 60;
            int warnAnimY = warnY + (int)((float)warnOffDown * (1.0f - a2));
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)a2);
            this.drawAnimatedStripVertical(TEX_WARNING_SHEET, 80, warnX, warnAnimY, warnW, warnH, 400, 16, 20);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private boolean isMouseOverAnyButton(int mouseX, int mouseY) {
        for (GuiButton b : this.buttonList) {
            if (b == null || !b.visible || mouseX < b.x || mouseX >= b.x + b.width || mouseY < b.y || mouseY >= b.y + b.height) continue;
            return true;
        }
        return false;
    }

    private static String forceBlack(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        if (s.length() >= 2 && s.charAt(0) == '\u00a7') {
            return "\u00a70" + s.substring(2);
        }
        return "\u00a70" + s;
    }

    private double readEntityStat(String mobId, net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr) {
        Entity ent = SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(mobId), (Level)this.player.level());
        if (!(ent instanceof LivingEntity)) {
            return -1.0;
        }
        LivingEntity living = (LivingEntity)ent;
        AttributeInstance inst = living.getAttribute(attr);
        return inst != null ? inst.getBaseValue() : -1.0;
    }

    private void resetModelPan() {
        this.modelPanActive = false;
        this.modelPanX = 0;
        this.modelPanY = 0;
        this.panStartMouseY = 0;
        this.panStartMouseX = 0;
        this.panStartY = 0;
        this.panStartX = 0;
    }

    private int drawTextPanelLeftWiggle(ResourceLocation tex, String text, int leftX, int topY, int maxWidth, int textColor, int mouseX, int mouseY, float t) {
        int padX = 12;
        int padY = 8;
        int textW = this.fontRendererObj.getStringWidth(text);
        int panelW = Math.min(Math.max(80, maxWidth), textW + 24);
        int panelH = this.fontRendererObj.FONT_HEIGHT + 8;
        boolean hovered = mouseX >= leftX && mouseX <= leftX + panelW && mouseY >= topY && mouseY <= topY + panelH;
        float wiggleX = 0.0f;
        float wiggleY = 0.0f;
        if (hovered) {
            float phase = t;
            wiggleX = (float)Math.sin(phase * 2.0f) * 0.4f;
            wiggleY = (float)Math.sin(phase * 3.0f) * 0.6f;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)wiggleX, (float)wiggleY, (float)0.0f);
        this.drawPanel(tex, leftX, topY, panelW, panelH);
        this.fontRendererObj.drawString(text, leftX + 12, topY + (panelH - this.fontRendererObj.FONT_HEIGHT) / 2, textColor);
        GlStateManager.popMatrix();
        return topY + panelH;
    }

    private float computeAutoScale(LivingEntity ent, int boxW, int boxH) {
        float padW = Math.max(0, boxW - 4);
        float padH = Math.max(0, boxH - 4);
        float w = Math.max(0.6f, ent.getBbWidth());
        float h = Math.max(0.6f, ent.getBbHeight());
        float scaleW = padW / w;
        float scaleH = padH / h;
        float scale = Math.min(scaleW, scaleH);
        scale = Math.max(8.0f, Math.min(scale, 120.0f));
        return scale;
    }

    private void renderEntityPreview(String mobId, int cx, int cy, int boxW, int boxH, float yawDeg, float pitchDeg) {
        this.renderEntityPreview(mobId, cx, cy, boxW, boxH, yawDeg, pitchDeg, 0, 1.0f);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderEntityPreview(String mobId, int cx, int cy, int boxW, int boxH, float yawDeg, float pitchDeg, int marginPx, float extraShrink) {
        LivingEntity ent = this.entityCache.computeIfAbsent(mobId, id -> {
            Entity created = SRPEntityUtil.create((ResourceLocation)ResourceLocation.parse(id), (Level)this.player.level());
            return created instanceof LivingEntity ? (LivingEntity)created : null;
        });
        if (ent == null) {
            return;
        }
        float oYawOff = ent.yBodyRot;
        float oYawHead = ent.yHeadRot;
        float oYaw = ent.getYRot();
        float oPitch = ent.getXRot();
        float ppYawOff = ent.yBodyRotO;
        float ppYawHead = ent.yHeadRotO;
        float ppYaw = ent.yRotO;
        float ppPitch = ent.xRotO;
        int oTicks = ent.tickCount;
        ent.setPos(0.0, 0.0, 0.0);
        ent.xo = ent.getX();
        ent.yo = ent.getY();
        ent.zo = ent.getZ();
        ent.xOld = ent.getX();
        ent.yOld = ent.getY();
        ent.zOld = ent.getZ();
        ent.tickCount = this.player.tickCount;
        if (this.renderRunThisCall) {
            float age = (float)this.player.tickCount + GuiContext.partial();
            ent.walkAnimation.update(1.0f, 1.0f);
            ent.setSprinting(true);
        } else {
            ent.setSprinting(false);
        }
        ent.setXRot(0.0f);
        ent.xRotO = 0.0f;
        ent.yBodyRot = yawDeg;
        ent.yBodyRotO = yawDeg;
        ent.yHeadRot = yawDeg;
        ent.yHeadRotO = yawDeg;
        ent.setYRot(yawDeg);
        ent.yRotO = yawDeg;
        int innerW = Math.max(0, boxW - (marginPx << 1));
        int innerH = Math.max(0, boxH - (marginPx << 1));
        float scale = this.computeAutoScale(ent, innerW, innerH);
        scale *= SRPBestiaryRegistry.getRenderScale(mobId);
        if (extraShrink > 0.0f) {
            scale *= extraShrink;
        }
        try {
            GuiContext.renderEntity(ent, cx, cy, scale, pitchDeg);
        }
        finally {
            ent.yBodyRot = oYawOff;
            ent.yBodyRotO = ppYawOff;
            ent.yHeadRot = oYawHead;
            ent.yHeadRotO = ppYawHead;
            ent.setYRot(oYaw);
            ent.yRotO = ppYaw;
            ent.setXRot(oPitch);
            ent.xRotO = ppPitch;
            ent.tickCount = oTicks;
        }
    }

    private void renderEntityPreview(String mobId, int cx, int cy, int boxW, int boxH, float yawDeg, int marginPx, float extraShrink) {
        this.renderEntityPreview(mobId, cx, cy, boxW, boxH, yawDeg, 0.0f, marginPx, extraShrink);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    static {
        Function<String, ItemStack> byId = id -> {
            Item it = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
            if (it == net.minecraft.world.item.Items.AIR) {
                it = null;
            }
            return it != null ? new ItemStack(it) : ItemStack.EMPTY;
        };
        TIER_ICONS.put(ParasiteTier.INBORN, byId.apply("srparasites:lurecomponent1"));
        TIER_ICONS.put(ParasiteTier.ASSIMILATED, byId.apply("srparasites:lurecomponent3"));
        TIER_ICONS.put(ParasiteTier.HIJACKED, byId.apply("srparasites:hijacked_drop"));
        TIER_ICONS.put(ParasiteTier.FERAL, byId.apply("srparasites:lurecomponent2"));
        TIER_ICONS.put(ParasiteTier.CRUDE, byId.apply("srparasites:itemmobspawner_host"));
        TIER_ICONS.put(ParasiteTier.PRIMITIVE, byId.apply("srparasites:ada_reeker_drop"));
        TIER_ICONS.put(ParasiteTier.ADAPTED, byId.apply("srparasites:ada_longarms_drop"));
        TIER_ICONS.put(ParasiteTier.NEXUS, byId.apply("srparasites:beckon_drop"));
        TIER_ICONS.put(ParasiteTier.DETERRENT, byId.apply("srparasites:dispatcher_drop"));
        TIER_ICONS.put(ParasiteTier.PURE, byId.apply("srparasites:living_core"));
        TIER_ICONS.put(ParasiteTier.PREEMINENT, byId.apply("srparasites:itemthrow"));
        TIER_ICONS.put(ParasiteTier.ANCIENT, byId.apply("srparasites:itemmobspawner_oronco"));
        TIER_ICONS.put(ParasiteTier.ABOMINATION, byId.apply("srparasites:ada_vermin_drop"));
        TIER_ICONS.put(ParasiteTier.ASSIMARA, byId.apply("srparasites:ada_viscera_drop"));
        TIER_ICONS.put(ParasiteTier.DERIVED, byId.apply("srparasites:alveoligrowth"));
        TIER_ICONS.put(ParasiteTier.WALKING_HEAD, byId.apply("srparasites:assimilated_flesh"));
        Item it = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(ResourceLocation.parse("srparasites:itemtab"));
        if (it == net.minecraft.world.item.Items.AIR) {
            it = null;
        }
        DEFAULT_TIER_ICON = it != null ? new ItemStack(it) : ItemStack.EMPTY;
    }

    private class AnimatedButton
    extends GuiButton {
        final int baseX;
        final int baseY;
        final int group;

        AnimatedButton(int id, int x, int y, int w, int h, String txt, int group) {
            super(id, x, y, w, h, txt);
            this.baseX = x;
            this.baseY = y;
            this.group = group;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            float a = ParasitesPage.this.page == BestiaryPage.MOB_DETAIL ? ParasitesPage.smoothstep(ParasitesPage.this.mobDetailAnim) : (ParasitesPage.this.page == BestiaryPage.MOB_LIST ? ParasitesPage.smoothstep(ParasitesPage.this.mobListAnim) : (ParasitesPage.this.page == BestiaryPage.PARASITES ? ParasitesPage.smoothstep(ParasitesPage.this.tierListAnim) : 1.0f));
            int x = this.baseX;
            int y = this.baseY;
            if (ParasitesPage.this.page == BestiaryPage.MOB_DETAIL || ParasitesPage.this.page == BestiaryPage.MOB_LIST || ParasitesPage.this.page == BestiaryPage.PARASITES) {
                if (this.group == 0) {
                    int offUp = -(this.baseY + this.height + 8);
                    y = this.baseY + (int)((float)offUp * (1.0f - a));
                } else if (this.group == 1) {
                    int offDown = mc.getWindow().getHeight() + 60;
                    y = this.baseY + (int)((float)offDown * (1.0f - a));
                }
            }
            int ox = this.x;
            int oy = this.y;
            this.x = x;
            this.y = y;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)a);
            super.drawButton(mc, mouseX, mouseY, partialTicks);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            this.x = ox;
            this.y = oy;
        }
    }

    private static class TierButton
    extends ListButton {
        final ParasiteTier tier;
        final ItemStack icon;

        TierButton(int id, int x, int rowTopY, int w, int h, String text, ParasiteTier tier, ItemStack icon) {
            super(id, x, rowTopY, w, h, text);
            this.tier = tier;
            this.icon = icon == null ? ItemStack.EMPTY : icon;
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            ParasitesPage g;
            if (!this.visible) {
                return;
            }
            net.minecraft.client.gui.screens.Screen s = mc.screen;
            float a = 1.0f;
            int xOff = 0;
            if (s instanceof ParasitesPage && (g = (ParasitesPage)s).page == BestiaryPage.PARASITES) {
                a = ParasitesPage.smoothstep(g.tierListAnim);
                xOff = g.getTierPageXOff();
            }
            int ox = this.x;
            int oy = this.y;
            this.x = ox + xOff;
            boolean hover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int oxx = this.x;
            if (hover) {
                this.x = oxx + 2;
            }
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)a);
            super.drawButton(mc, mouseX - xOff, mouseY, partialTicks);
            if (!this.icon.isEmpty()) {
                int iconX = this.x - 5;
                int iconY = this.y - 5;
                RenderHelper.enableGUIStandardItemLighting();
                GuiScreen.renderItem(this.icon, iconX, iconY);
                RenderHelper.disableStandardItemLighting();
            }
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            this.x = ox;
            this.y = oy;
        }
    }

    private static class MobListEntryButton
    extends ListButton {
        MobListEntryButton(int id, int x, int rowTopY, int w, int h, String txt) {
            super(id, x, rowTopY, w, h, txt);
        }

        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) {
                return;
            }
            net.minecraft.client.gui.screens.Screen s = mc.screen;
            if (!(s instanceof ParasitesPage)) {
                super.drawButton(mc, mouseX, mouseY, partialTicks);
                return;
            }
            ParasitesPage g = (ParasitesPage)s;
            float a = ParasitesPage.smoothstep(g.mobListAnim);
            int leftOffX = -230;
            int xOff = (int)((float)leftOffX * (1.0f - a));
            int ox = this.x;
            int oy = this.y;
            this.x = ox + xOff;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)a);
            super.drawButton(mc, mouseX - xOff, mouseY, partialTicks);
            GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
            this.x = ox;
            this.y = oy;
        }
    }

    private static class ListButton
    extends GuiButton {
        final int rowTopY;

        ListButton(int id, int x, int rowTopY, int w, int h, String txt) {
            super(id, x, rowTopY, w, h, txt);
            this.rowTopY = rowTopY;
        }
    }

    private static class DropEntry {
        final String itemId;
        final int chance;
        final int amount;
        final boolean looting;

        DropEntry(String itemId, int chance, int amount, boolean looting) {
            this.itemId = itemId;
            this.chance = chance;
            this.amount = amount;
            this.looting = looting;
        }
    }

    private static enum BestiaryPage {
        HOME,
        PARASITES,
        MOB_LIST,
        MOB_DETAIL;

    }
}

