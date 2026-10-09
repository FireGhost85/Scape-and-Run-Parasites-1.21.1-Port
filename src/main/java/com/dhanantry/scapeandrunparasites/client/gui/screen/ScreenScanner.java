package com.dhanantry.scapeandrunparasites.client.gui.screen;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.container.ScannerContainer;
import com.dhanantry.scapeandrunparasites.network.RequestScanPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

/** ScannerGui of 1.10.9: the module slot, the Scan button and the sliding cooldown bar. */
public class ScreenScanner extends AbstractContainerScreen<ScannerContainer> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/scanner_gui.png");
    private Button scanBtn;
    private float cdSlide = 0.0f;

    public ScreenScanner(ScannerContainer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelY = -100;
        this.inventoryLabelY = this.imageHeight - 96 + 2;
    }

    @Override
    protected void init() {
        super.init();
        this.scanBtn = Button.builder(Component.translatable("chat.srparasites.relay.scan_text"), b -> {
            PacketDistributor.sendToServer(new RequestScanPayload(this.menu.getPos()));
        }).bounds(this.leftPos + 120, this.topPos + 34, 48, 20).build();
        this.addRenderableWidget(this.scanBtn);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        boolean hasModule = this.menu.getSlot(0).hasItem();
        boolean visible = this.menu.getCooldownRemaining() > 0 && this.menu.getCooldownTotal() > 0;
        this.scanBtn.active = hasModule && this.menu.isFormed() && !visible;
        float target = visible ? 1.0f : 0.0f;
        this.cdSlide += (target - this.cdSlide) * 0.25f;
        if (!visible && this.cdSlide < 0.01f) {
            this.cdSlide = 0.0f;
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        g.blit(BG, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    private void drawCooldownBar(GuiGraphics g) {
        int total = this.menu.getCooldownTotal();
        if (this.cdSlide <= 0.0f || total <= 0) {
            return;
        }
        int barH = 20;
        int x = this.leftPos;
        int w = this.imageWidth;
        int yHidden = this.topPos + this.imageHeight + barH;
        int yShown = this.topPos + this.imageHeight;
        int y = (int)((float)yHidden + (float)(yShown - yHidden) * this.cdSlide);
        g.fill(x, y, x + w, y + barH, 0xAA000000);
        int remaining = this.menu.getCooldownRemaining();
        float progress = 1.0f - (float)remaining / (float)total;
        progress = Math.max(0.0f, Math.min(1.0f, progress));
        int pad = 2;
        int filled = (int)((float)(w - pad * 2) * progress);
        g.fill(x + pad, y + pad, x + pad + filled, y + barH - pad, 0xFF55FF55);
        int secs = (int)Math.ceil((double)remaining / 20.0);
        g.drawString(this.font, Component.translatable("gui.srparasites.scanner.cooldown", secs), x + 6, y + 6, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        this.drawCooldownBar(g);
        this.renderTooltip(g, mouseX, mouseY);
    }
}
