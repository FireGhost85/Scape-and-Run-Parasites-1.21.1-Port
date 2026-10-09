package com.dhanantry.scapeandrunparasites.client.gui;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.BestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.ParasiteTier;
import com.dhanantry.scapeandrunparasites.bestiary.SRPBestiaryRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.BlockBestiaryEntry;
import com.dhanantry.scapeandrunparasites.bestiary.blocks.SRPBlockCompendiumRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.bestiary.effects.SRPStatusEffectRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.systems.SRPSystemsRegistry;
import com.dhanantry.scapeandrunparasites.bestiary.systems.SystemEntry;
import com.dhanantry.scapeandrunparasites.network.BestiaryRequestPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * The field guide / bestiary (GuiBestiary and its pages in 1.12): categories parasites (by tier, then mob with model, lore and stats),
 * blocks, status effects, systems and kill statistics. Same content and unlock rules (lore and stats appear after the kill
 * counts of the entry); the layout is a simplified 1.21 one. The celestial page and the current progress page come with the
 * celestial events (deferred).
 */
public class BestiaryScreen extends Screen {
    private enum Page { HOME, TIERS, MOBS, DETAIL, BLOCKS, EFFECTS, SYSTEMS, STATS }

    private static final int PANEL = 0xE0101418;
    private static final int PANEL_EDGE = 0xFF5A6B52;
    private static final int ROW = 0xA0202830;
    private static final int ROW_HOVER = 0xC0384650;
    private static final int TEXT = 0xFFE0E6DA;
    private static final int DIM = 0xFF9AA595;
    private static final int GOLD = 0xFFE8C26A;

    private Page page = Page.HOME;
    private ParasiteTier tier;
    private BestiaryEntry mob;
    private int scroll;
    private int contentHeight;
    private float spin;
    private final Map<String, LivingEntity> entities = new HashMap<>();
    private final List<Runnable> clickable = new ArrayList<>();
    private final List<int[]> clickAreas = new ArrayList<>();

    public BestiaryScreen() {
        super(Component.translatable("bestiary.parasite_tiers"));
    }

    private IBestiaryProgress progress() {
        return this.minecraft != null && this.minecraft.player != null ? BestiaryCapability.get(this.minecraft.player) : null;
    }

    @Override
    protected void init() {
        PacketDistributor.sendToServer(new BestiaryRequestPayload());
        this.clearWidgets();
        int cx = this.width / 2;
        if (this.page == Page.HOME) {
            String[] keys = {"bestiary.tab.parasites", "bestiary.tab.blocks", "bestiary.tab.effects", "bestiary.tab.systems", "bestiary.tab.stats"};
            Page[] pages = {Page.TIERS, Page.BLOCKS, Page.EFFECTS, Page.SYSTEMS, Page.STATS};
            for (int i = 0; i < keys.length; ++i) {
                Page target = pages[i];
                this.addRenderableWidget(Button.builder(Component.translatable(keys[i]), b -> this.go(target)).bounds(cx - 75, 78 + 24 * i, 150, 20).build());
            }
            Button celestial = Button.builder(Component.translatable("bestiary.tab.celestial"), b -> { }).bounds(cx - 75, 78 + 24 * 5, 150, 20).build();
            celestial.active = false;
            this.addRenderableWidget(celestial);
            Button current = Button.builder(Component.translatable("bestiary.tab.current_progress"), b -> { }).bounds(cx - 75, 78 + 24 * 6, 150, 20).build();
            current.active = false;
            this.addRenderableWidget(current);
        } else {
            this.addRenderableWidget(Button.builder(Component.translatable("bestiary.nav.back"), b -> this.back()).bounds(10, this.height - 28, 70, 20).build());
            this.addRenderableWidget(Button.builder(Component.translatable("bestiary.nav.home"), b -> this.go(Page.HOME)).bounds(84, this.height - 28, 70, 20).build());
        }
    }

    private void go(Page target) {
        this.page = target;
        this.scroll = 0;
        this.rebuildWidgets();
    }

    private void back() {
        switch (this.page) {
            case DETAIL -> this.go(Page.MOBS);
            case MOBS -> this.go(Page.TIERS);
            default -> this.go(Page.HOME);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int max = Math.max(0, this.contentHeight - (this.height - 90));
        this.scroll = Mth.clamp(this.scroll - (int)(scrollY * 18), 0, max);
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (int i = 0; i < this.clickAreas.size(); ++i) {
            int[] a = this.clickAreas.get(i);
            if (mouseX >= a[0] && mouseX < a[2] && mouseY >= a[1] && mouseY < a[3]) {
                this.clickable.get(i).run();
                return true;
            }
        }
        return false;
    }

    private static boolean known(IBestiaryProgress prog, BestiaryEntry e) {
        return prog.getKills(e.mobId) > 0 || prog.isMobSeen(e.mobId);
    }

    private static String tierKey(ParasiteTier t) {
        return "bestiary.tier." + t.name().toLowerCase(Locale.ROOT);
    }

    private static String loreKey(String mobId) {
        ResourceLocation rl = ResourceLocation.parse(mobId);
        return "lore." + rl.getNamespace() + "." + rl.getPath();
    }

    private void area(int x0, int y0, int x1, int y1, Runnable action) {
        this.clickAreas.add(new int[]{x0, y0, x1, y1});
        this.clickable.add(action);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g, mouseX, mouseY, partialTick);
        this.clickAreas.clear();
        this.clickable.clear();
        this.spin += partialTick * 2.0f;
        int left = 20;
        int right = this.width - 20;
        g.fill(left - 6, 20, right + 6, this.height - 36, PANEL);
        g.renderOutline(left - 6, 20, right - left + 12, this.height - 56, PANEL_EDGE);
        IBestiaryProgress prog = this.progress();
        g.drawCenteredString(this.font, this.title, this.width / 2, 8, GOLD);
        if (prog == null) {
            g.drawCenteredString(this.font, Component.translatable("bestiary.cap_missing_client"), this.width / 2, 60, TEXT);
            super.render(g, mouseX, mouseY, partialTick);
            return;
        }
        g.enableScissor(left, 24, right, this.height - 40);
        int y = 28 - this.scroll;
        int start = y;
        switch (this.page) {
            case HOME -> g.drawCenteredString(this.font, Component.translatable("bestiary.select_category"), this.width / 2, 50, TEXT);
            case TIERS -> y = this.renderTiers(g, prog, left, right, y, mouseX, mouseY);
            case MOBS -> y = this.renderMobs(g, prog, left, right, y, mouseX, mouseY);
            case DETAIL -> y = this.renderDetail(g, prog, left, right, y, mouseX, mouseY);
            case BLOCKS -> y = this.renderBlocks(g, prog, left, right, y);
            case EFFECTS -> y = this.renderEffects(g, prog, left, right, y);
            case SYSTEMS -> y = this.renderSystems(g, left, right, y);
            case STATS -> y = this.renderStats(g, prog, left, right, y);
        }
        g.disableScissor();
        this.contentHeight = y - start + 8;
        super.render(g, mouseX, mouseY, partialTick);
    }

    private int row(GuiGraphics g, int left, int right, int y, int h, int mouseX, int mouseY, Runnable click) {
        boolean hover = mouseX >= left && mouseX < right && mouseY >= y && mouseY < y + h && mouseY > 24 && mouseY < this.height - 40;
        g.fill(left, y, right, y + h, hover ? ROW_HOVER : ROW);
        if (click != null) {
            this.area(left, Math.max(y, 24), right, Math.min(y + h, this.height - 40), click);
        }
        return y + h + 4;
    }

    private int renderTiers(GuiGraphics g, IBestiaryProgress prog, int left, int right, int y, int mouseX, int mouseY) {
        g.drawString(this.font, Component.translatable("bestiary.parasite_tiers"), left, y, GOLD);
        y += 14;
        List<ParasiteTier> tiers = new ArrayList<>(List.of(ParasiteTier.values()));
        tiers.sort(Comparator.comparing(t -> Component.translatable(tierKey(t)).getString().toLowerCase(Locale.ROOT)));
        for (ParasiteTier t : tiers) {
            int known = 0;
            int total = 0;
            for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
                if (e.tier == t) {
                    ++total;
                    if (known(prog, e)) {
                        ++known;
                    }
                }
            }
            if (total == 0) {
                continue;
            }
            final ParasiteTier chosen = t;
            int top = y;
            y = this.row(g, left, right, y, 24, mouseX, mouseY, () -> {
                this.tier = chosen;
                this.go(Page.MOBS);
            });
            g.drawString(this.font, Component.translatable(tierKey(t)), left + 8, top + 8, TEXT);
            g.drawString(this.font, known + " / " + total, right - 50, top + 8, known > 0 ? GOLD : DIM);
        }
        return y;
    }

    private int renderMobs(GuiGraphics g, IBestiaryProgress prog, int left, int right, int y, int mouseX, int mouseY) {
        g.drawString(this.font, Component.translatable(tierKey(this.tier)), left, y, GOLD);
        y += 14;
        boolean any = false;
        for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
            if (e.tier != this.tier || !known(prog, e)) {
                continue;
            }
            any = true;
            final BestiaryEntry chosen = e;
            int top = y;
            y = this.row(g, left, right, y, 24, mouseX, mouseY, () -> {
                this.mob = chosen;
                this.go(Page.DETAIL);
            });
            g.drawString(this.font, Component.translatable(e.nameKey), left + 8, top + 8, TEXT);
            g.drawString(this.font, prog.getKills(e.mobId) + " " + Component.translatable("bestiary.kills").getString(), right - 70, top + 8, DIM);
        }
        if (!any) {
            g.drawString(this.font, Component.translatable("bestiary.no_mobs_unlocked_in_tier"), left, y, DIM);
            g.drawString(this.font, Component.translatable("bestiary.unlock_by_kill"), left, y + 12, DIM);
            y += 28;
        }
        return y;
    }

    @SuppressWarnings("unchecked")
    private LivingEntity entityFor(String mobId) {
        LivingEntity cached = this.entities.get(mobId);
        if (cached != null || this.minecraft == null || this.minecraft.level == null) {
            return cached;
        }
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(mobId));
        if (type == null) {
            return null;
        }
        try {
            if (type.create(this.minecraft.level) instanceof LivingEntity living) {
                this.entities.put(mobId, living);
                return living;
            }
        } catch (RuntimeException e) {
            ScapeAndRunParasites.LOGGER.debug("bestiary preview of {} failed", mobId, e);
        }
        return null;
    }

    private int renderDetail(GuiGraphics g, IBestiaryProgress prog, int left, int right, int y, int mouseX, int mouseY) {
        BestiaryEntry e = this.mob;
        int kills = prog.getKills(e.mobId);
        int modelW = Math.min(150, (right - left) / 3);
        int textLeft = left + modelW + 12;
        int textW = right - textLeft - 4;
        g.fill(left, y, left + modelW, y + 170, ROW);
        LivingEntity entity = this.entityFor(e.mobId);
        if (entity != null) {
            float size = Math.max(entity.getBbHeight(), entity.getBbWidth());
            float scale = Mth.clamp(110.0f / Math.max(size, 0.2f) * SRPBestiaryRegistry.getRenderScale(e.mobId), 6.0f, 110.0f);
            Quaternionf pose = new Quaternionf().rotationXYZ(0.43633232f, (float)Math.toRadians(this.spin) + (float)Math.PI, (float)Math.PI);
            InventoryScreen.renderEntityInInventory(g, left + modelW / 2.0f, y + 150.0f, scale, new Vector3f(0.0f, entity.getBbHeight() / 2.0f, 0.0f), pose, null, entity);
        }
        g.drawString(this.font, Component.translatable(e.nameKey), textLeft, y, GOLD);
        g.drawString(this.font, Component.translatable("bestiary.tier_label", Component.translatable(tierKey(e.tier))), textLeft, y + 12, DIM);
        g.drawString(this.font, Component.translatable("bestiary.kills_label").getString() + ": " + kills, textLeft, y + 24, DIM);
        int ty = y + 42;
        if (kills >= e.minLoreKill) {
            for (FormattedCharSequence line : this.font.split(Component.translatable(loreKey(e.mobId)), textW)) {
                g.drawString(this.font, line, textLeft, ty, TEXT);
                ty += 10;
            }
        } else {
            g.drawString(this.font, Component.translatable("bestiary.lore_unlocks_at_n", e.minLoreKill), textLeft, ty, DIM);
            ty += 12;
        }
        ty += 8;
        if (kills >= e.minStatKill) {
            String hp = "?";
            String dmg = "?";
            if (e.baseHp > 0) {
                hp = String.valueOf(e.baseHp);
            } else if (entity != null) {
                hp = String.valueOf(Math.round(entity.getAttributeValue(Attributes.MAX_HEALTH)));
            }
            if (e.baseDamage > 0.0f) {
                dmg = String.valueOf(e.baseDamage);
            } else if (entity != null && entity.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
                dmg = String.valueOf(Math.round(entity.getAttributeValue(Attributes.ATTACK_DAMAGE)));
            }
            g.drawString(this.font, Component.translatable("bestiary.stats", hp, dmg), textLeft, ty, TEXT);
            ty += 12;
        } else {
            g.drawString(this.font, Component.translatable("bestiary.more_info_at_n", e.minStatKill), textLeft, ty, DIM);
            ty += 12;
        }
        return Math.max(y + 176, ty);
    }

    private int renderBlocks(GuiGraphics g, IBestiaryProgress prog, int left, int right, int y) {
        g.drawString(this.font, Component.translatable("bestiary.blocks.title"), left, y, GOLD);
        y += 14;
        int count = 0;
        for (BlockBestiaryEntry b : SRPBlockCompendiumRegistry.all()) {
            if (!prog.hasSeenBlock(b.id)) {
                continue;
            }
            ++count;
            List<FormattedCharSequence> lines = this.font.split(Component.translatable(b.loreKey), right - left - 40);
            int h = Math.max(24, 16 + lines.size() * 10);
            g.fill(left, y, right, y + h, ROW);
            g.renderItem(b.icon, left + 4, y + 4);
            g.drawString(this.font, Component.translatable(b.nameKey), left + 28, y + 4, TEXT);
            int ly = y + 15;
            for (FormattedCharSequence line : lines) {
                g.drawString(this.font, line, left + 28, ly, DIM);
                ly += 10;
            }
            y += h + 4;
        }
        if (count == 0) {
            g.drawString(this.font, Component.translatable("bestiary.blocks.empty"), left, y, DIM);
            y += 12;
        }
        return y;
    }

    private int renderEffects(GuiGraphics g, IBestiaryProgress prog, int left, int right, int y) {
        g.drawString(this.font, Component.translatable("bestiary.effects.title"), left, y, GOLD);
        y += 14;
        int count = 0;
        for (SRPStatusEffectRegistry.Entry e : SRPStatusEffectRegistry.all()) {
            if (!prog.hasSeenEffect(e.id)) {
                continue;
            }
            ++count;
            ResourceLocation icon = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/mob_effect/" + e.id + ".png");
            List<FormattedCharSequence> lines = this.font.split(Component.translatable("bestiary.effect." + e.id + ".desc"), right - left - 40);
            int h = Math.max(26, 16 + lines.size() * 10);
            g.fill(left, y, right, y + h, ROW);
            RenderSystem.enableBlend();
            g.blit(icon, left + 4, y + 4, 0, 0, 18, 18, 18, 18);
            g.drawString(this.font, Component.translatable("effect.srparasites." + e.id), left + 28, y + 4, TEXT);
            int ly = y + 15;
            for (FormattedCharSequence line : lines) {
                g.drawString(this.font, line, left + 28, ly, DIM);
                ly += 10;
            }
            y += h + 4;
        }
        if (count == 0) {
            g.drawString(this.font, Component.translatable("bestiary.effects.none"), left, y, DIM);
            y += 12;
        }
        return y;
    }

    private int renderSystems(GuiGraphics g, int left, int right, int y) {
        g.drawString(this.font, Component.translatable("bestiary.systems.title"), left, y, GOLD);
        y += 14;
        for (SystemEntry s : SRPSystemsRegistry.all()) {
            List<FormattedCharSequence> lines = this.font.split(Component.translatable(s.descKey()), right - left - 40);
            int h = Math.max(26, 16 + lines.size() * 10);
            g.fill(left, y, right, y + h, ROW);
            g.drawString(this.font, Component.translatable(s.nameKey()), left + 28, y + 4, TEXT);
            int ly = y + 15;
            for (FormattedCharSequence line : lines) {
                g.drawString(this.font, line, left + 28, ly, DIM);
                ly += 10;
            }
            y += h + 4;
        }
        return y;
    }

    private int renderStats(GuiGraphics g, IBestiaryProgress prog, int left, int right, int y) {
        g.drawString(this.font, Component.translatable("bestiary.stats.chart_title"), left, y, GOLD);
        y += 14;
        Map<ParasiteTier, Integer> byTier = new HashMap<>();
        int total = 0;
        for (BestiaryEntry e : SRPBestiaryRegistry.all()) {
            int k = prog.getKills(e.mobId);
            total += k;
            byTier.merge(e.tier, k, Integer::sum);
        }
        int max = 1;
        for (int v : byTier.values()) {
            max = Math.max(max, v);
        }
        for (ParasiteTier t : ParasiteTier.values()) {
            int v = byTier.getOrDefault(t, 0);
            g.drawString(this.font, Component.translatable(tierKey(t)), left, y + 1, TEXT);
            int barX = left + 110;
            int barW = (int)((right - barX - 50) * (v / (float)max));
            g.fill(barX, y, barX + Math.max(barW, v > 0 ? 1 : 0), y + 9, 0xFF7A9A5A);
            g.drawString(this.font, String.valueOf(v), barX + barW + 4, y + 1, DIM);
            y += 12;
        }
        y += 8;
        g.drawString(this.font, Component.translatable("bestiary.stats.summary", total), left, y, TEXT);
        y += 16;
        g.drawString(this.font, Component.translatable("bestiary.stats.combat_totals"), left, y, GOLD);
        y += 12;
        g.drawString(this.font, Component.translatable("bestiary.stats.damage_to_parasites").getString() + ": " + Math.round(prog.getDamageToParasites()), left, y, TEXT);
        y += 11;
        g.drawString(this.font, Component.translatable("bestiary.stats.damage_from_parasites").getString() + ": " + Math.round(prog.getDamageFromParasites()), left, y, TEXT);
        y += 11;
        g.drawString(this.font, Component.translatable("bestiary.stats.deaths_by_parasites").getString() + ": " + prog.getDeathsByParasites(), left, y, TEXT);
        return y + 12;
    }

    /** Opens the field guide screen of the player. */
    public static void open() {
        Minecraft.getInstance().setScreen(new BestiaryScreen());
    }
}
