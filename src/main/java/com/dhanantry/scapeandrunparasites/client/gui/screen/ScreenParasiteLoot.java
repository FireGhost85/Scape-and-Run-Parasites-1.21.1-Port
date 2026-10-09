package com.dhanantry.scapeandrunparasites.client.gui.screen;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.container.ContainerParasiteLoot;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/** GuiParasiteLoot of 1.10.9: the fullness bar and the rising blood bubbles behind the chest (they speed up when the chest is used). */
public class ScreenParasiteLoot extends AbstractContainerScreen<ContainerParasiteLoot> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/parasite_loot.png");
    private static final ResourceLocation BUBBLE_TEX = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/gui/blood_bubble.png");
    private static final int MAX_BUBBLES = 28;
    private final List<Bubble> bubbles = new ArrayList<>();
    private final Random fxRand = new Random();
    private int guiTick = 0;
    private int boostStartTick = -1;
    private int boostActiveUntil = -1;
    private int boostCooldownUntil = 0;
    private int prevFullnessScaled = -1;
    private int fullnessScaled = 0;

    public ScreenParasiteLoot(ContainerParasiteLoot menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        this.renderTooltip(g, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        for (Bubble b : this.bubbles) {
            g.pose().pushPose();
            g.pose().translate(b.x + b.size / 2.0f, b.y + b.size / 2.0f, 0.0f);
            g.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees(b.rot));
            g.pose().translate(-b.size / 2.0f, -b.size / 2.0f, 0.0f);
            g.setColor(1.0f, 1.0f, 1.0f, b.alpha);
            g.blit(BUBBLE_TEX, 0, 0, (int)b.size, (int)b.size, 0.0f, 0.0f, 1, 1, 1, 1);
            g.pose().popPose();
        }
        g.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        g.blit(BG, x, y, 0, 0, this.imageWidth, this.imageHeight);
        int barMax = 160;
        int barH = 6;
        float f = (float)this.fullnessScaled / 1000.0f;
        int barFilled = Math.round((float)barMax * f);
        int barX = x + 8;
        int barY = y + 16;
        g.fill(barX, barY, barX + barMax, barY + barH, 0xFF201A20);
        g.fill(barX, barY, barX + barFilled, barY + barH, colorRedYellowGreen(f));
    }

    private void triggerBubbleBoost() {
        if (this.guiTick >= this.boostCooldownUntil) {
            this.boostStartTick = this.guiTick;
            this.boostActiveUntil = this.guiTick + 10;
            this.boostCooldownUntil = this.guiTick + 10;
        }
    }

    private float currentBoostMultiplier() {
        if (this.guiTick >= this.boostActiveUntil || this.boostStartTick < 0) {
            return 1.0f;
        }
        float t = (float)(this.guiTick - this.boostStartTick) / 10.0f;
        float pulse = (float)Math.sin(Math.PI * (double)t);
        return 1.0f + 1.25f * pulse;
    }

    private void spawnBubble() {
        Bubble b = new Bubble();
        b.size = 8.0f + this.fxRand.nextFloat() * 16.0f;
        b.x = this.fxRand.nextFloat() * ((float)this.width - b.size);
        b.y = (float)this.height + b.size;
        b.rot = this.fxRand.nextFloat() * 360.0f;
        b.rotSpeed = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.6f;
        b.speed = 0.25f + this.fxRand.nextFloat() * 0.65f;
        b.alpha = 0.35f + this.fxRand.nextFloat() * 0.35f;
        b.vx = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.15f;
        b.splitTick = this.fxRand.nextFloat() < 0.35f ? this.guiTick + (100 + this.fxRand.nextInt(101)) : -1;
        this.bubbles.add(b);
    }

    private List<Bubble> makeChildBubbles(Bubble parent) {
        int room = Math.max(0, MAX_BUBBLES - this.bubbles.size());
        if (room <= 0 || parent.size < 12.8f) {
            return Collections.emptyList();
        }
        int count = Math.min(2, room);
        ArrayList<Bubble> out = new ArrayList<>(count);
        for (int i = 0; i < count; ++i) {
            Bubble c = new Bubble();
            float scale = 0.5f + this.fxRand.nextFloat() * 0.2f;
            c.size = Math.max(8.0f, parent.size * scale);
            float offset = i == 0 ? -c.size * 0.4f : c.size * 0.4f;
            c.x = Math.max(0.0f, Math.min((float)this.width - c.size, parent.x + offset));
            c.y = parent.y + this.fxRand.nextFloat() * 2.0f;
            c.rot = this.fxRand.nextFloat() * 360.0f;
            c.rotSpeed = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.6f;
            c.speed = Math.min(1.125f, parent.speed * (1.05f + this.fxRand.nextFloat() * 0.15f));
            c.alpha = Math.min(1.0f, parent.alpha * (0.9f + this.fxRand.nextFloat() * 0.2f));
            c.vx = (this.fxRand.nextFloat() * 2.0f - 1.0f) * 0.2f;
            c.splitTick = c.size > 14.4f && this.fxRand.nextFloat() < 0.15f ? this.guiTick + (80 + this.fxRand.nextInt(81)) : -1;
            out.add(c);
        }
        return out;
    }

    private static int colorRedYellowGreen(float f) {
        int g;
        int r;
        if (f < 0.0f) {
            f = 0.0f;
        } else if (f > 1.0f) {
            f = 1.0f;
        }
        if (f < 0.5f) {
            r = 255;
            g = (int)(f / 0.5f * 255.0f);
        } else {
            r = (int)((1.0f - f) / 0.5f * 255.0f);
            g = 255;
        }
        return 0xFF000000 | r << 16 | g << 8;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        ++this.guiTick;
        int newFullnessScaled = this.menu.getFullnessField();
        if (this.prevFullnessScaled == -1) {
            this.prevFullnessScaled = newFullnessScaled;
        } else if (newFullnessScaled != this.prevFullnessScaled) {
            this.triggerBubbleBoost();
            this.prevFullnessScaled = newFullnessScaled;
        }
        this.fullnessScaled = newFullnessScaled;
        float boost = this.currentBoostMultiplier();
        List<Bubble> toAdd = null;
        Iterator<Bubble> it = this.bubbles.iterator();
        while (it.hasNext()) {
            Bubble b = it.next();
            b.y -= b.speed * boost;
            b.x += b.vx;
            b.rot += b.rotSpeed * (0.5f + 0.5f * boost);
            if (b.x < -b.size) {
                b.x = -b.size;
            }
            if (b.x > (float)this.width) {
                b.x = this.width;
            }
            if (b.splitTick > 0 && this.guiTick >= b.splitTick) {
                List<Bubble> children = this.makeChildBubbles(b);
                if (!children.isEmpty()) {
                    if (toAdd == null) {
                        toAdd = new ArrayList<>(children.size());
                    }
                    toAdd.addAll(children);
                }
                it.remove();
                continue;
            }
            if (b.y + b.size < 0.0f) {
                it.remove();
            }
        }
        int allowed;
        if (toAdd != null && !toAdd.isEmpty() && (allowed = Math.max(0, MAX_BUBBLES - this.bubbles.size())) > 0) {
            if (toAdd.size() > allowed) {
                toAdd = toAdd.subList(0, allowed);
            }
            this.bubbles.addAll(toAdd);
        }
        if (this.bubbles.size() < MAX_BUBBLES) {
            float f = (float)this.fullnessScaled / 1000.0f;
            float spawnChance = 0.18f + (1.0f - f) * 0.22f;
            if (this.fxRand.nextFloat() < spawnChance) {
                this.spawnBubble();
            }
        }
    }

    private static final class Bubble {
        float x;
        float y;
        float size;
        float rot;
        float rotSpeed;
        float speed;
        float alpha;
        float vx;
        int splitTick;
    }
}
