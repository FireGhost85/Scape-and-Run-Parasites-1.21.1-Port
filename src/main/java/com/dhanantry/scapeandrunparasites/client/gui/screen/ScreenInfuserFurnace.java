package com.dhanantry.scapeandrunparasites.client.gui.screen;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.container.ContainerInfuserFurnace;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** GuiInfuserFurnace of 1.10.9. */
public class ScreenInfuserFurnace extends AbstractContainerScreen<ContainerInfuserFurnace> {
    private static final ResourceLocation TEX_BG = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/infuser_furnace.png");

    public ScreenInfuserFurnace(ContainerInfuserFurnace menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 96 + 2;
        this.titleLabelY = 6;
    }

    private static int cookScaled(int cook, int pixels) {
        return cook <= 0 ? 0 : cook * pixels / 200;
    }

    private int burnLeftScaled(int pixels) {
        int cur = this.menu.getCurrentBurnTime();
        if (cur <= 0) {
            cur = 200;
        }
        int burn = this.menu.getBurnTime();
        return burn <= 0 ? 0 : burn * pixels / cur;
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        g.blit(TEX_BG, x, y, 0, 0, 176, 166);
        int smelt = Math.min(cookScaled(this.menu.getCookSmelt(), 24), 24);
        if (smelt > 0) {
            g.blit(TEX_BG, x + 80, y + 35, 176, 14, smelt + 1, 16);
        }
        if (this.menu.getBurnTime() > 0) {
            int k = this.burnLeftScaled(13);
            g.blit(TEX_BG, x + 56, y + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }
        int infuse = cookScaled(this.menu.getCookInfuse(), 24);
        if (infuse > 0) {
            g.blit(TEX_BG, x + 80, y + 35, 176, 14, infuse + 1, 16);
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);
    }
}
