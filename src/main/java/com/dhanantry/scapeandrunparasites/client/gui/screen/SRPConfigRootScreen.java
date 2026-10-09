package com.dhanantry.scapeandrunparasites.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * The main config screen of the mod: one button per config file like NeoForge's own selection screen, plus a search bar that lists the
 * matching settings of every category of every file.
 */
public class SRPConfigRootScreen extends OptionsSubScreen {
    private final ModContainer mod;
    private final SRPConfigSectionScreen.SearchState state = new SRPConfigSectionScreen.SearchState();
    private final List<SRPConfigSectionScreen> tops = new ArrayList<>();
    private String query = "";
    private EditBox searchBox;
    private Button undoButton;
    private final Button doneButton = Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).width(Button.SMALL_WIDTH).build();

    public SRPConfigRootScreen(ModContainer mod, Screen parent) {
        super(parent, Minecraft.getInstance().options, Component.translatable("srparasites.configuration.title", mod.getModInfo().getDisplayName()));
        this.mod = mod;
    }

    private String fileKey(ModConfig cfg) {
        return this.mod.getModId() + ".configuration.section." + cfg.getFileName().replaceAll("[^a-zA-Z0-9]+", ".").replaceFirst("^\\.", "").replaceFirst("\\.$", "").toLowerCase(Locale.ENGLISH);
    }

    private List<ModConfig> configs() {
        List<ModConfig> result = new ArrayList<>();
        for (ModConfig.Type type : ModConfig.Type.values()) {
            for (ModConfig cfg : ModConfigs.getConfigSet(type)) {
                if (cfg.getModId().equals(this.mod.getModId())) {
                    result.add(cfg);
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------ layout

    @Override
    protected void addTitle() {
        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical().spacing(4));
        header.defaultCellSetting().alignHorizontallyCenter();
        header.addChild(new StringWidget(this.getTitle(), this.font));
        this.searchBox = new EditBox(this.font, 240, 18, Component.translatable("srparasites.configuration.search"));
        this.searchBox.setHint(Component.translatable("srparasites.configuration.search.hint.all").withStyle(ChatFormatting.DARK_GRAY));
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
    protected void addFooter() {
        this.undoButton = Button.builder(ConfigurationScreen.UNDO, b -> {
            SRPConfigSectionScreen.undoLast(this.state);
            this.fillList();
        }).tooltip(Tooltip.create(ConfigurationScreen.UNDO_TOOLTIP)).width(Button.SMALL_WIDTH).build();
        this.undoButton.active = false;
        LinearLayout footer = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
        footer.addChild(this.undoButton);
        footer.addChild(this.doneButton);
    }

    @Override
    protected void addOptions() {
        if (this.tops.isEmpty()) {
            for (ModConfig cfg : this.configs()) {
                if (cfg.getSpec() instanceof ModConfigSpec spec && spec.isLoaded()) {
                    SRPConfigSectionScreen top = new SRPConfigSectionScreen(this, cfg.getType(), cfg,
                            Component.translatable(this.fileKey(cfg) + ".title", this.mod.getModInfo().getDisplayName()), this.state);
                    this.tops.add(top);
                }
            }
        }
        for (SRPConfigSectionScreen top : this.tops) {
            top.attach(this.minecraft, this.font);
        }
        this.fillList();
    }

    private void onSearch(String text) {
        boolean wasBlank = this.query.isBlank();
        this.query = text == null ? "" : text;
        if (wasBlank && !this.query.isBlank()) {
            this.state.order.clear();
        }
        if (this.list != null) {
            this.fillList();
            this.list.setScrollAmount(0.0);
        }
    }

    private void fillList() {
        if (this.list == null) {
            return;
        }
        if (!this.query.isBlank()) {
            SRPConfigSectionScreen.fillSearchResults(this.list, this.font, this.options, this.width, this, this.tops, this.query, tokens -> {
                // the config files themselves are results too
                int added = 0;
                for (ModConfig cfg : this.configs()) {
                    String name = Component.translatable(this.fileKey(cfg), this.mod.getModInfo().getDisplayName()).getString().toLowerCase(Locale.ROOT);
                    boolean all = true;
                    for (String t : tokens) {
                        all &= name.contains(t);
                    }
                    if (all && cfg.getSpec() instanceof ModConfigSpec spec && spec.isLoaded()) {
                        this.list.addSmall(new StringWidget(Button.BIG_WIDTH / 2, Button.DEFAULT_HEIGHT, Component.translatable("neoforge.configuration.uitext.section",
                                Component.translatable(this.fileKey(cfg), this.mod.getModInfo().getDisplayName())), this.font).alignLeft(),
                                Button.builder(Component.translatable("neoforge.configuration.uitext.section", Component.translatable("neoforge.configuration.uitext.sectiontext")),
                                        b -> this.open(cfg)).width(Button.DEFAULT_WIDTH).build());
                        ++added;
                    }
                }
                return added;
            });
            return;
        }
        this.list.children().clear();
        this.list.addSmall(new StringWidget(Button.BIG_WIDTH, Button.DEFAULT_HEIGHT, Component.translatable("neoforge.configuration.uitext.common").withStyle(ChatFormatting.UNDERLINE), this.font).alignLeft(), null);
        for (ModConfig cfg : this.configs()) {
            boolean loaded = cfg.getSpec() instanceof ModConfigSpec spec && spec.isLoaded();
            Button button = Button.builder(Component.translatable("neoforge.configuration.uitext.section",
                    Component.translatable(this.fileKey(cfg), this.mod.getModInfo().getDisplayName())), b -> this.open(cfg)).width(Button.BIG_WIDTH).build();
            button.active = loaded;
            button.setTooltip(Tooltip.create(Component.translatable("neoforge.configuration.uitext.filenametooltip", cfg.getFileName()).withStyle(ChatFormatting.GRAY)));
            this.list.addSmall(button, null);
        }
    }

    private void open(ModConfig cfg) {
        // edits made in search results are saved before the file is opened in its own screen
        for (SRPConfigSectionScreen top : this.tops) {
            top.flush();
        }
        this.minecraft.setScreen(new SRPConfigSectionScreen(this, cfg.getType(), cfg,
                Component.translatable(this.fileKey(cfg) + ".title", this.mod.getModInfo().getDisplayName())));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.undoButton != null) {
            this.undoButton.active = !this.query.isBlank() && SRPConfigSectionScreen.canUndoLast(this.state);
        }
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        for (SRPConfigSectionScreen top : this.tops) {
            top.flush();
        }
        super.onClose();
    }
}
