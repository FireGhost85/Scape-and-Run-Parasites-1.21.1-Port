package com.dhanantry.scapeandrunparasites.client.gui.screen;

import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig.Entry;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.ListValueSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ValueSpec;

/**
 * The section screen of the SRP configuration with a search bar: typing lists the individual settings of this category and of every
 * category below it whose name (or category name) contains all the typed words. Edits made in the results go to the same config values
 * and sub screens as the normal view (they bubble up on close), Undo steps back through the edits made in the results.
 */
public class SRPConfigSectionScreen extends ConfigurationScreen.ConfigurationSectionScreen {
    private static final int MAX_RESULTS = 250;

    /** Shared by a whole tree of section screens: the order of the edits made while searching, for Undo. */
    static final class SearchState {
        final Deque<SRPConfigSectionScreen> order = new ArrayDeque<>();
        boolean undoing;
    }

    private record Result(SRPConfigSectionScreen screen, Entry entry) {}

    private final String sectionLabel;
    private final SearchState state;
    private String query = "";
    private boolean searching;
    private EditBox searchBox;

    public SRPConfigSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title) {
        this(parent, type, modConfig, title, new SearchState());
    }

    SRPConfigSectionScreen(Screen parent, ModConfig.Type type, ModConfig modConfig, Component title, SearchState state) {
        super(parent, type, modConfig, title, (c, k, e) -> e);
        this.sectionLabel = "";
        this.state = state;
    }

    private SRPConfigSectionScreen(Context parentContext, Screen parent, Map<String, Object> valueSpecs, String key, Set<? extends Entry> entries, Component title,
            String sectionLabel, SearchState state) {
        super(parentContext, parent, valueSpecs, key, entries, title);
        this.sectionLabel = sectionLabel;
        this.state = state;
    }

    public static ConfigurationScreen create(ModContainer container, Screen parent) {
        return new ConfigurationScreen(container, parent, (a, b, c, d) -> new SRPConfigSectionScreen(a, b, c, d));
    }

    /** The main screen with a search bar over every config file. */
    public static Screen createRoot(ModContainer container, Screen parent) {
        return new SRPConfigRootScreen(container, parent);
    }

    // ------------------------------------------------------------------ layout

    @Override
    protected void addTitle() {
        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical().spacing(4));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(this.getTitle(), this.font));
        this.searchBox = new EditBox(this.font, 240, 18, Component.translatable("srparasites.configuration.search"));
        this.searchBox.setHint(Component.translatable("srparasites.configuration.search.hint").withStyle(ChatFormatting.DARK_GRAY));
        this.searchBox.setMaxLength(64);
        this.searchBox.setValue(this.query);
        this.searchBox.setResponder(this::onSearch);
        header.addChild(this.searchBox);
        this.layout.setHeaderHeight(56);
    }

    @Override
    protected void init() {
        super.init();
        this.setInitialFocus(this.searchBox);
    }

    @Override
    protected void addOptions() {
        // the Undo / Reset buttons must exist before the footer is laid out, the results need them even if this section has no values of its own
        if (this.undoButton == null) {
            this.createUndoButton();
            this.createResetButton();
        }
        this.rebuild();
    }

    private void onSearch(String text) {
        boolean wasBlank = this.query.isBlank();
        this.query = text == null ? "" : text;
        if (wasBlank && !this.query.isBlank()) {
            this.state.order.clear();
        }
        if (this.list != null) {
            this.rebuild();
            this.list.setScrollAmount(0.0);
        }
    }

    // ------------------------------------------------------------------ sections

    @Override
    protected Element createSection(String key, UnmodifiableConfig subconfig, UnmodifiableConfig subsection) {
        if (subconfig.isEmpty()) {
            return null;
        }
        this.subScreen(key, subconfig, subsection);
        return super.createSection(key, subconfig, subsection);
    }

    private SRPConfigSectionScreen subScreen(String key, UnmodifiableConfig subconfig, UnmodifiableConfig subsection) {
        return (SRPConfigSectionScreen) this.sectionCache.computeIfAbsent(key, k -> new SRPConfigSectionScreen(this.context, this, subconfig.valueMap(), key,
                subsection.entrySet(), Component.translatable(this.getTranslationKey(key)), this.getTranslationComponent(key).getString(), this.state));
    }

    // ------------------------------------------------------------------ search

    private static boolean matches(String[] tokens, String haystack) {
        for (String t : tokens) {
            if (!haystack.contains(t)) {
                return false;
            }
        }
        return true;
    }

    /** Adds the values of this section and of all sections below it that match the words. */
    private void collect(String[] tokens, List<Result> out) {
        for (Entry entry : this.context.entries()) {
            if (out.size() >= MAX_RESULTS) {
                return;
            }
            String key = entry.getKey();
            Object raw = entry.getRawValue();
            if (raw instanceof ConfigValue<?>) {
                String name = this.getTranslationComponent(key).getString();
                if (matches(tokens, (this.sectionLabel + " " + name).toLowerCase(Locale.ROOT))) {
                    out.add(new Result(this, entry));
                }
            } else if (raw instanceof UnmodifiableConfig subsection && this.context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig && !subconfig.isEmpty()) {
                SRPConfigSectionScreen sub = this.subScreen(key, subconfig, subsection);
                // a sub section that was never opened has no font / minecraft yet, its widgets need them
                sub.minecraft = this.minecraft;
                sub.font = this.font;
                // the category itself is a result when its name matches, then everything below it is searched
                if (matches(tokens, this.getTranslationComponent(key).getString().toLowerCase(Locale.ROOT))) {
                    out.add(new Result(this, entry));
                }
                sub.collect(tokens, out);
            }
        }
    }

    /** The 1.21 NeoForge screen builds its elements inline; this is the same switch for one value. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Element elementFor(Entry entry) {
        String key = entry.getKey();
        if (!(entry.getRawValue() instanceof ConfigValue cv)) {
            return null;
        }
        ValueSpec valueSpec = this.getValueSpec(key);
        if (valueSpec == null) {
            return null;
        }
        Element element;
        if (valueSpec instanceof ListValueSpec listSpec) {
            element = this.createList(key, listSpec, cv);
        } else if (cv.getClass() == ConfigValue.class && valueSpec.getDefault() instanceof String) {
            element = this.createStringValue(key, valueSpec::test, () -> (String) cv.getRaw(), cv::set);
        } else if (cv.getClass() == ConfigValue.class && valueSpec.getDefault() instanceof Integer) {
            element = this.createIntegerValue(key, valueSpec, () -> (Integer) cv.getRaw(), cv::set);
        } else if (cv.getClass() == ConfigValue.class && valueSpec.getDefault() instanceof Long) {
            element = this.createLongValue(key, valueSpec, () -> (Long) cv.getRaw(), cv::set);
        } else if (cv.getClass() == ConfigValue.class && valueSpec.getDefault() instanceof Double) {
            element = this.createDoubleValue(key, valueSpec, () -> (Double) cv.getRaw(), cv::set);
        } else if (cv.getClass() == ConfigValue.class && valueSpec.getDefault() instanceof Enum<?>) {
            element = this.createEnumValue(key, valueSpec, (java.util.function.Supplier) cv::getRaw, (java.util.function.Consumer) cv::set);
        } else if (cv instanceof ModConfigSpec.BooleanValue value) {
            element = this.createBooleanValue(key, valueSpec, value::getRaw, value::set);
        } else if (cv instanceof ModConfigSpec.IntValue value) {
            element = this.createIntegerValue(key, valueSpec, value::getRaw, value::set);
        } else if (cv instanceof ModConfigSpec.LongValue value) {
            element = this.createLongValue(key, valueSpec, value::getRaw, value::set);
        } else if (cv instanceof ModConfigSpec.DoubleValue value) {
            element = this.createDoubleValue(key, valueSpec, value::getRaw, value::set);
        } else if (cv instanceof ModConfigSpec.EnumValue value) {
            element = this.createEnumValue(key, valueSpec, (java.util.function.Supplier) value::getRaw, (java.util.function.Consumer) value::set);
        } else {
            element = this.createOtherValue(key, cv);
        }
        return this.context.filter().filterEntry(this.context, key, element);
    }

    @Override
    protected ConfigurationScreen.ConfigurationSectionScreen rebuild() {
        if (this.query.isBlank()) {
            this.searching = false;
            this.rebuildNormal();
            return this;
        }
        if (this.list == null) {
            return this;
        }
        this.searching = true;
        fillSearchResults(this.list, this.font, this.options, this.width, this, List.of(this), this.query, null);
        return this;
    }

    /**
     * A row label that reaches to the left of its normal place, so the long setting names are not cut off: the list puts the label at the left
     * column (150 wide) and the control at the right column; the label keeps its right edge and takes the free room on its left.
     */
    static final class WideLabel extends StringWidget {
        private final int extra;

        WideLabel(int screenWidth, Component text, Font font) {
            this(text, font, Math.max(0, Math.min(200, screenWidth / 2 - 155 - 8)));
        }

        private WideLabel(Component text, Font font, int extra) {
            super(Button.DEFAULT_WIDTH + extra, Button.DEFAULT_HEIGHT, text, font);
            this.extra = extra;
            this.alignLeft();
        }

        @Override
        public void setX(int x) {
            super.setX(x - this.extra);
        }
    }

    /** The row building of NeoForge's section screen (it cannot be reused for the labels), with the wide labels. */
    private void rebuildNormal() {
        if (this.list == null) {
            return;
        }
        this.list.children().clear();
        boolean hasUndoableElements = false;
        List<Element> elements = new ArrayList<>();
        for (Entry entry : this.context.entries()) {
            String key = entry.getKey();
            Object raw = entry.getRawValue();
            if (raw instanceof ConfigValue<?>) {
                elements.add(this.elementFor(entry));
            } else if (raw instanceof UnmodifiableConfig subsection && this.context.valueSpecs().get(key) instanceof UnmodifiableConfig subconfig) {
                elements.add(this.createSection(key, subconfig, subsection));
            } else {
                elements.add(this.context.filter().filterEntry(this.context, key, this.createOtherSection(key, raw)));
            }
        }
        elements.addAll(this.createSyntheticValues());
        for (Element element : elements) {
            if (element == null) {
                continue;
            }
            if (element.name() == null) {
                this.list.addSmall(new StringWidget(Button.DEFAULT_WIDTH, Button.DEFAULT_HEIGHT, Component.empty(), this.font), element.getWidget(this.options));
            } else {
                StringWidget label = new WideLabel(this.width, element.name(), this.font);
                if (element.tooltip() != null) {
                    label.setTooltip(Tooltip.create(element.tooltip()));
                }
                this.list.addSmall(label, element.getWidget(this.options));
            }
            hasUndoableElements |= element.undoable();
        }
        if (hasUndoableElements && this.undoButton == null) {
            this.createUndoButton();
            this.createResetButton();
        }
    }

    /** Lists the settings of the given top screens (and everything below them) that match the typed words. */
    static void fillSearchResults(OptionsList list, Font font, Options options, int screenWidth, Screen host, List<SRPConfigSectionScreen> tops, String query,
            java.util.function.ToIntFunction<String[]> header) {
        list.children().clear();
        String[] tokens = query.toLowerCase(Locale.ROOT).trim().split("\\s+");
        List<Result> results = new ArrayList<>();
        for (SRPConfigSectionScreen top : tops) {
            top.collect(tokens, results);
        }
        int shown = header == null ? 0 : header.applyAsInt(tokens);
        for (Result r : results) {
            if (!(r.entry().getRawValue() instanceof ConfigValue<?>)) {
                r.screen().addCategoryRow(list, r.entry(), host, screenWidth);
                ++shown;
                continue;
            }
            Element element = r.screen().elementFor(r.entry());
            if (element == null || element.name() == null) {
                continue;
            }
            MutableComponent tip = Component.empty();
            if (!r.screen().sectionLabel.isEmpty()) {
                tip.append(Component.literal(r.screen().sectionLabel).withStyle(ChatFormatting.GRAY)).append("\n");
            }
            if (element.tooltip() != null) {
                tip.append(element.tooltip());
            }
            StringWidget label = new WideLabel(screenWidth, element.name(), font);
            label.setTooltip(Tooltip.create(tip));
            list.addSmall(label, element.getWidget(options));
            ++shown;
        }
        if (shown == 0) {
            list.addSmall(new StringWidget(Button.DEFAULT_WIDTH * 2, Button.DEFAULT_HEIGHT, Component.translatable("srparasites.configuration.search.none").withStyle(ChatFormatting.GRAY), font), null);
        }
    }

    /** A result row for a category: its name and a button that opens it; closing it returns to the screen showing the search. */
    private void addCategoryRow(OptionsList list, Entry entry, Screen host, int screenWidth) {
        String key = entry.getKey();
        UnmodifiableConfig subsection = (UnmodifiableConfig) entry.getRawValue();
        UnmodifiableConfig subconfig = (UnmodifiableConfig) this.context.valueSpecs().get(key);
        MutableComponent tip = Component.empty();
        if (!this.sectionLabel.isEmpty()) {
            tip.append(Component.literal(this.sectionLabel).withStyle(ChatFormatting.GRAY)).append("\n");
        }
        tip.append(this.getTooltipComponent(key, null));
        StringWidget label = new WideLabel(screenWidth, Component.translatable("neoforge.configuration.uitext.section", this.getTranslationComponent(key)), this.font);
        label.setTooltip(Tooltip.create(tip));
        Button button = Button.builder(Component.translatable("neoforge.configuration.uitext.section", Component.translatable("neoforge.configuration.uitext.sectiontext")),
                b -> this.minecraft.setScreen(new SRPConfigSectionScreen(this.context, host, subconfig.valueMap(), key, subsection.entrySet(),
                        Component.translatable(this.getTranslationKey(key)), this.getTranslationComponent(key).getString(), this.state)))
                .width(Button.DEFAULT_WIDTH).tooltip(Tooltip.create(tip)).build();
        list.addSmall(label, button);
    }

    /** Gives a top screen that is never opened the font it needs to build widgets. */
    void attach(Minecraft mc, Font f) {
        this.minecraft = mc;
        this.font = f;
    }

    /** Saves the edits made in search results of a top screen that is never closed itself. */
    void flush() {
        this.collectChildChanges();
        if (this.changed) {
            this.context.modSpec().save();
        }
        this.clearChanged();
    }

    private void clearChanged() {
        this.changed = false;
        for (ConfigurationScreen.ConfigurationSectionScreen child : this.sectionCache.values()) {
            if (child instanceof SRPConfigSectionScreen s) {
                s.clearChanged();
            }
        }
    }

    static boolean canUndoLast(SearchState state) {
        SRPConfigSectionScreen top = state.order.peek();
        return top != null && top.undoManager.canUndo();
    }

    static void undoLast(SearchState state) {
        SRPConfigSectionScreen last = state.order.poll();
        if (last != null) {
            state.undoing = true;
            try {
                last.undoManager.undo();
            } finally {
                state.undoing = false;
            }
        }
    }

    // ------------------------------------------------------------------ undo / reset

    @Override
    protected void onChanged(String key) {
        super.onChanged(key);
        if (!this.state.undoing) {
            this.state.order.push(this);
        }
    }

    @Override
    protected void createUndoButton() {
        this.undoButton = Button.builder(ConfigurationScreen.UNDO, button -> {
            if (this.searching) {
                undoLast(this.state);
            } else {
                this.undoManager.undo();
            }
            this.rebuild();
        }).tooltip(Tooltip.create(ConfigurationScreen.UNDO_TOOLTIP)).width(Button.SMALL_WIDTH).build();
        this.undoButton.active = false;
    }

    @Override
    protected void setUndoButtonstate(boolean state) {
        if (this.undoButton != null) {
            if (this.searching) {
                this.undoButton.active = canUndoLast(this.state);
            } else {
                this.undoButton.active = state;
            }
        }
    }

    @Override
    protected void setResetButtonstate(boolean state) {
        if (this.resetButton != null) {
            // Reset only works on the values of a screen that is open as a normal list
            this.resetButton.active = state && !this.searching;
        }
    }

    // ------------------------------------------------------------------ closing

    /** Edits made in the results went to sub screens that were never opened: let their changed / restart flags bubble up like on a normal close. */
    private void collectChildChanges() {
        for (ConfigurationScreen.ConfigurationSectionScreen child : this.sectionCache.values()) {
            if (child instanceof SRPConfigSectionScreen s) {
                s.collectChildChanges();
                if (s.changed) {
                    this.changed = true;
                    this.needsRestart = this.needsRestart.with(s.needsRestart);
                }
            }
        }
    }

    @Override
    public void onClose() {
        this.collectChildChanges();
        super.onClose();
    }
}
