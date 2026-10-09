package com.dhanantry.scapeandrunparasites.client.shader;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

/**
 * One screen post effect of the mod (shaders/post/*.json), loaded into the game renderer (the 1.12 EntityRenderer.loadShader /
 * stopUseShader of the shader managers). The game renderer has a single post effect slot; this class only unloads what it loaded.
 */
public final class SRPPostEffect {
    private final ResourceLocation post;
    private PostChain loaded;
    private long nextRetryMs = 0L;
    private long startNs = 0L;

    public SRPPostEffect(String name) {
        this.post = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "shaders/post/" + name + ".json");
    }

    public boolean isApplied() {
        Minecraft mc = Minecraft.getInstance();
        return this.loaded != null && mc.gameRenderer.currentEffect() == this.loaded;
    }

    /** Loads the effect if it is wanted and not applied, unloads it when it is not wanted any more. */
    public void update(boolean wanted) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            this.loaded = null;
            return;
        }
        if (!wanted) {
            if (this.loaded != null && mc.gameRenderer.currentEffect() == this.loaded) {
                mc.gameRenderer.shutdownEffect();
            }
            this.loaded = null;
            return;
        }
        if (this.isApplied()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < this.nextRetryMs) {
            return;
        }
        try {
            this.startNs = System.nanoTime();
            mc.gameRenderer.loadEffect(this.post);
            this.loaded = mc.gameRenderer.currentEffect();
        }
        catch (Throwable t) {
            ScapeAndRunParasites.LOGGER.error("[SRP] could not load post effect {}", this.post, t);
            this.nextRetryMs = System.currentTimeMillis() + 2000L;
            this.loaded = null;
        }
    }

    public float timeSeconds() {
        return (float)((double)(System.nanoTime() - this.startNs) / 1.0E9);
    }

    public void setUniform(String name, float value) {
        if (this.isApplied()) {
            this.loaded.setUniform(name, value);
        }
    }
}
