package com.dhanantry.scapeandrunparasites.client.gui.screen;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPPace;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.star.SRPStarWorldEvents;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.fml.ModList;

/** GuiSRPWorldSettings of 1.10.9: difficulty preset, meteor infection and the star type of the world that is being created. */
public class SRPWorldSettingsScreen extends Screen {
    private final Screen parent;
    private static int pendingStarType = 0;
    private static boolean pendingMushroomTrees = false;
    private static boolean pendingFracturedTerrain = false;
    private Button difficultyBtn;
    private Button meteorBtn;
    private Button starBtn;
    private Button mushroomBtn;
    private Button fracturedBtn;

    public SRPWorldSettingsScreen(Screen parent) {
        super(Component.translatable("gui.srparasites.worldsettings.title"));
        this.parent = parent;
    }

    private static final String[] DIFFICULTY = {"easy", "normal", "hard", "impossible"};

    private Component difficultyLabel() {
        int i = Math.max(0, Math.min(3, SRPPace.choiceNUMBER));
        return Component.translatable("gui.srparasites.worldsettings.difficulty").append(": ").append(Component.translatable("gui.srparasites.worldsettings.difficulty." + DIFFICULTY[i]));
    }

    private Component meteorLabel() {
        return Component.translatable("gui.srparasites.worldsettings.meteor").append(": ").append(Component.translatable(SRPConfigWorld.meteorActive ? "gui.srparasites.worldsettings.meteor.on" : "gui.srparasites.worldsettings.meteor.off"));
    }

    private Component starLabel() {
        return Component.translatable("gui.srparasites.worldsettings.star").append(": ").append(Component.translatable(pendingStarType == 1 ? "gui.srparasites.worldsettings.star.cold" : "gui.srparasites.worldsettings.star.normal"));
    }

    private static Component onOff(String key, boolean on) {
        return Component.translatable(key).append(": ").append(Component.translatable(key + (on ? ".on" : ".off")));
    }

    private void refresh() {
        this.difficultyBtn.setMessage(this.difficultyLabel());
        this.meteorBtn.setMessage(this.meteorLabel());
        this.starBtn.setMessage(this.starLabel());
        this.mushroomBtn.visible = pendingStarType == 1;
        this.fracturedBtn.visible = pendingStarType == 1;
        this.mushroomBtn.setMessage(onOff("gui.srparasites.worldsettings.mushroom_trees", pendingMushroomTrees));
        this.fracturedBtn.setMessage(onOff("gui.srparasites.worldsettings.fractured", pendingFracturedTerrain));
    }

    @Override
    protected void init() {
        pendingStarType = SRPWorldEntitySpawner.starType == 1 ? 1 : 0;
        int cx = this.width / 2;
        int left = cx - 88;
        int top = 60;
        this.difficultyBtn = this.addRenderableWidget(Button.builder(this.difficultyLabel(), b -> {
            SRPPace.choiceNUMBER = (SRPPace.choiceNUMBER + 1) % 4;
            this.refresh();
        }).bounds(left, top, 176, 20).build());
        this.meteorBtn = this.addRenderableWidget(Button.builder(this.meteorLabel(), b -> {
            SRPWorldEntitySpawner.triggerSPAWNING = SRPConfigWorld.meteorActive = !SRPConfigWorld.meteorActive;
            this.refresh();
        }).bounds(left, top + 24, 176, 20).build());
        this.starBtn = this.addRenderableWidget(Button.builder(this.starLabel(), b -> {
            pendingStarType = (pendingStarType + 1) % 2;
            if (pendingStarType != 1) {
                pendingFracturedTerrain = false;
            }
            this.refresh();
        }).bounds(left, top + 48, 176, 20).build());
        this.mushroomBtn = this.addRenderableWidget(Button.builder(Component.empty(), b -> {
            pendingMushroomTrees = !pendingMushroomTrees;
            this.refresh();
        }).bounds(left, top + 72, 176, 20).build());
        this.fracturedBtn = this.addRenderableWidget(Button.builder(Component.empty(), b -> {
            pendingFracturedTerrain = pendingStarType == 1 && !pendingFracturedTerrain;
            this.refresh();
        }).bounds(left, top + 96, 176, 20).build());
        ModList.get().getModContainerById(ScapeAndRunParasites.MODID).ifPresent(container ->
                this.addRenderableWidget(Button.builder(Component.translatable("gui.srparasites.worldsettings.config"), b ->
                        this.minecraft.setScreen(SRPConfigSectionScreen.create(container, this))).bounds(left, top + 124, 176, 20).build()));
        this.addRenderableWidget(Button.builder(Component.translatable("gui.srparasites.worldsettings.done"), b -> {
            boolean cold = pendingStarType == 1;
            SRPStarWorldEvents.markCreatingWorld(pendingStarType, cold && pendingMushroomTrees, cold && pendingFracturedTerrain);
            SRPWorldEntitySpawner.starType = pendingStarType;
            this.minecraft.setScreen(this.parent);
        }).bounds(cx - 100, this.height - 28, 200, 20).build());
        this.refresh();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        List<Component> tip = new ArrayList<>();
        if (this.difficultyBtn.isMouseOver(mouseX, mouseY)) {
            tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.difficulty." + DIFFICULTY[Math.max(0, Math.min(3, SRPPace.choiceNUMBER))]));
        } else if (this.starBtn.isMouseOver(mouseX, mouseY)) {
            String k = pendingStarType == 1 ? "cold" : "normal";
            for (int i = 1; i <= 3; ++i) {
                String key = "gui.srparasites.worldsettings.tooltip.star." + k + "." + i;
                if (net.minecraft.client.resources.language.I18n.exists(key)) {
                    tip.add(Component.translatable(key));
                }
            }
        } else if (this.mushroomBtn.visible && this.mushroomBtn.isMouseOver(mouseX, mouseY)) {
            if (pendingMushroomTrees) {
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.mushroom_trees.1"));
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.mushroom_trees.2"));
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.mushroom_trees.warning"));
            } else {
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.mushroom_trees.off"));
            }
        } else if (this.fracturedBtn.visible && this.fracturedBtn.isMouseOver(mouseX, mouseY)) {
            if (pendingFracturedTerrain) {
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.fractured.1"));
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.fractured.2"));
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.fractured.warning"));
            } else {
                tip.add(Component.translatable("gui.srparasites.worldsettings.tooltip.fractured.off"));
            }
        } else if (this.meteorBtn.isMouseOver(mouseX, mouseY)) {
            tip.add(Component.translatable(SRPConfigWorld.meteorActive ? "gui.srparasites.worldsettings.tooltip.meteor.active" : "gui.srparasites.worldsettings.tooltip.meteor.inactive"));
        }
        if (!tip.isEmpty()) {
            g.renderComponentTooltip(this.font, tip, mouseX, mouseY);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    /** SRPWorldCreateButtons: the button of the create world screen. */
    @EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
    public static final class CreateButton {
        private CreateButton() {
        }

        @SubscribeEvent
        public static void onInit(ScreenEvent.Init.Post event) {
            if (!com.dhanantry.scapeandrunparasites.config.SRPConfig.worldGIU) {
                return;
            }
            if (!(event.getScreen() instanceof net.minecraft.client.gui.screens.worldselection.CreateWorldScreen screen)) {
                return;
            }
            event.addListener(Button.builder(Component.translatable("gui.srparasites.worldsettings.open"), b -> screen.getMinecraft().setScreen(new SRPWorldSettingsScreen(screen)))
                    .bounds(screen.width - 160, 6, 150, 20).build());
        }
    }
}
