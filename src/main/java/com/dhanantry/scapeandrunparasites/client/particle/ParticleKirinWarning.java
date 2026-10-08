package com.dhanantry.scapeandrunparasites.client.particle;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Flat, additively blended warning marker lying on the ground (the Kirin attack telegraph). Created directly, not through a particle type. */
public class ParticleKirinWarning extends Particle {
    private static final ResourceLocation TEX = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/particle/kirin_warning.png");

    /** Additive blending of the standalone texture. Both faces are emitted, so no culling state has to be changed and restored. */
    private static final ParticleRenderType ADDITIVE = new ParticleRenderType() {
        @Nullable
        @Override
        public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleShader);
            RenderSystem.setShaderTexture(0, TEX);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
        }

        @Override
        public String toString() {
            return "srparasites:kirin_warning_additive";
        }
    };

    private final float sizeBlocks;
    private final float yaw;

    public ParticleKirinWarning(ClientLevel level, double x, double y, double z, float sizeBlocks, float yawRad, int maxAgeTicks) {
        super(level, x, y, z);
        this.sizeBlocks = sizeBlocks;
        this.yaw = yawRad;
        this.lifetime = Math.max(1, maxAgeTicks);
        this.hasPhysics = false;
        this.gravity = 0.0f;
        this.zd = 0.0;
        this.yd = 0.0;
        this.xd = 0.0;
        this.alpha = 1.0f;
        this.setColor(1.0f, 1.0f, 1.0f);
    }

    /** Adds a marker to the client level; does nothing on the server. */
    public static void spawn(Level world, double x, double y, double z, float sizeBlocks, float yawRad, int maxAgeTicks) {
        if (world instanceof ClientLevel level) {
            Minecraft.getInstance().particleEngine.add(new ParticleKirinWarning(level, x, y, z, sizeBlocks, yawRad, maxAgeTicks));
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ADDITIVE;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 cam = camera.getPosition();
        float px = (float) (this.x - cam.x);
        float py = (float) (this.y - cam.y);
        float pz = (float) (this.z - cam.z);
        float half = this.sizeBlocks * 0.5f;
        float cos = (float) Math.cos(this.yaw);
        float sin = (float) Math.sin(this.yaw);
        int a = (int) (255.0f * this.alpha);
        float[][] corners = {{-half, -half, 0.0f, 0.0f}, {-half, half, 0.0f, 1.0f}, {half, half, 1.0f, 1.0f}, {half, -half, 1.0f, 0.0f}};
        for (int face = 0; face < 2; ++face) {
            for (int i = 0; i < 4; ++i) {
                float[] c = corners[face == 0 ? i : 3 - i];
                float lx = c[0];
                float lz = c[1];
                buffer.addVertex(px + lx * cos + lz * sin, py, pz - lx * sin + lz * cos).setUv(c[2], c[3]).setColor(255, 255, 255, a).setLight(LightTexture.FULL_BRIGHT);
            }
        }
    }
}
