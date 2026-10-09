package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.LayerRenderer;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLivingBase;
import com.dhanantry.scapeandrunparasites.client.legacy.Tessellator;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;

public class LayerVenkrolTornado
implements LayerRenderer<EntityVenkrolSIV> {
    private static final Map<Integer, VenkrolVortexLoopSound> ACTIVE_SOUNDS = new HashMap<Integer, VenkrolVortexLoopSound>();
    private final RenderLivingBase<EntityVenkrolSIV> renderer;
    private static final ResourceLocation TORNADO_TEX = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/particle/fog1.png");
    private static final ResourceLocation TORNADO_OUTER_TEX = ResourceLocation.fromNamespaceAndPath("srparasites", "textures/particle/fog1_outer.png");

    private boolean tornadoConditions(EntityVenkrolSIV entity) {
        if (!SRPConfigWorld.venkrolTornadoEnabled) {
            return false;
        }
        Level world = entity.level();
        if (world == null) {
            return false;
        }
        BlockPos pos = entity.blockPosition();
        if (!world.canSeeSky(pos.above())) {
            return false;
        }
        return world.isRaining() && world.isThundering();
    }

    private void updateTornadoSound(EntityVenkrolSIV entity) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }
        SoundManager handler = mc.getSoundManager();
        int id = entity.getId();
        if (!this.tornadoConditions(entity) || entity.isRemoved()) {
            VenkrolVortexLoopSound existing = ACTIVE_SOUNDS.get(id);
            if (existing != null) {
                existing.fadeOut();
            }
            return;
        }
        VenkrolVortexLoopSound sound = ACTIVE_SOUNDS.get(id);
        if (sound != null && (sound.isFinished() || !handler.isActive((SoundInstance)sound))) {
            ACTIVE_SOUNDS.remove(id);
            sound = null;
        }
        if (sound == null) {
            sound = new VenkrolVortexLoopSound(entity);
            ACTIVE_SOUNDS.put(id, sound);
            handler.play((SoundInstance)sound);
        } else {
            sound.setEntity(entity);
            if (sound.isFadingOut()) {
                sound.cancelFadeOut();
            }
        }
    }

    public LayerVenkrolTornado(RenderLivingBase<EntityVenkrolSIV> renderer) {
        this.renderer = renderer;
    }

    public void doRenderLayer(EntityVenkrolSIV entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        float right1z;
        float right1x;
        float right0z;
        float right0x;
        float alpha1;
        float alpha0;
        float r1;
        float r0;
        float y1;
        float y0;
        float t1;
        float t0;
        int yStep;
        Level world = entity.level();
        if (world == null) {
            return;
        }
        if (!SRPConfigWorld.venkrolTornadoEnabled) {
            this.updateTornadoSound(entity);
            return;
        }
        this.updateTornadoSound(entity);
        BlockPos pos = entity.blockPosition();
        if (!world.canSeeSky(pos.above())) {
            return;
        }
        if (!world.isRaining() || !world.isThundering()) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc((int)770, (int)771);
        GlStateManager.translate((float)0.0f, (float)2.0f, (float)0.0f);
        this.renderer.bindTexture(TORNADO_TEX);
        Tessellator tess = Tessellator.getInstance();
        Tessellator.BufferBuilder buf = tess.getWorldRenderer();
        float height = 100.0f;
        float bottomRadius = 5.0f;
        float topRadius = 20.0f;
        int verticalSegments = 32;
        int aroundSegments = 240;
        float time = ((float)entity.tickCount + partialTicks) * 0.1f;
        float spinSpeed = 1.5f;
        float twistAmount = (float)Math.toRadians(90.0);
        float segmentHeight = height / (float)verticalSegments;
        int entitySeed = entity.getId();
        buf.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
        for (yStep = 0; yStep < verticalSegments; ++yStep) {
            t0 = (float)yStep / (float)verticalSegments;
            t1 = (float)(yStep + 1) / (float)verticalSegments;
            y0 = -t0 * height;
            y1 = -t1 * height;
            r0 = bottomRadius + (topRadius - bottomRadius) * t0;
            r1 = bottomRadius + (topRadius - bottomRadius) * t1;
            alpha0 = (float)Math.sin(t0 * (float)Math.PI);
            alpha1 = (float)Math.sin(t1 * (float)Math.PI);
            float maxAlpha = 0.85f;
            alpha0 *= maxAlpha;
            alpha1 *= maxAlpha;
            float baseHalfWidth = 0.8f;
            float coverageFactor = 0.7f;
            float circumference0 = (float)Math.PI * 2 * r0;
            float circumference1 = (float)Math.PI * 2 * r1;
            float arcPerSlice0 = circumference0 / (float)aroundSegments;
            float arcPerSlice1 = circumference1 / (float)aroundSegments;
            float neededHalfWidth0 = arcPerSlice0 * coverageFactor * 0.5f;
            float neededHalfWidth1 = arcPerSlice1 * coverageFactor * 0.5f;
            float stripHalfWidth0 = Math.max(baseHalfWidth, neededHalfWidth0);
            float stripHalfWidth1 = Math.max(baseHalfWidth, neededHalfWidth1);
            float swayBase = 0.8f;
            float swayTop = 5.5f;
            float swayMag0 = swayBase + (swayTop - swayBase) * t0;
            float swayMag1 = swayBase + (swayTop - swayBase) * t1;
            float phase0 = time * 0.7f + t0 * 8.0f;
            float phase1 = time * 0.7f + t1 * 8.0f;
            float sway0x = (float)Math.sin(phase0) * swayMag0;
            float sway0z = (float)Math.cos((double)phase0 * 0.9) * swayMag0;
            float sway1x = (float)Math.sin(phase1) * swayMag1;
            float sway1z = (float)Math.cos((double)phase1 * 0.9) * swayMag1;
            for (int i = 0; i < aroundSegments; ++i) {
                float frac = (float)i / (float)aroundSegments;
                int panelSeed = entitySeed + yStep * 131 + i * 911;
                float phaseNoise = LayerVenkrolTornado.pseudoNoise(panelSeed, 11);
                float phaseOffset = phaseNoise * 0.2f;
                float speedNoise = LayerVenkrolTornado.pseudoNoise(panelSeed, 23);
                float localSpin = spinSpeed * (1.0f + speedNoise * 0.15f);
                float baseAngle = (float)((double)frac * Math.PI * 2.0) + time * localSpin + phaseOffset;
                float angle0 = baseAngle + t0 * twistAmount;
                float angle1 = baseAngle + t1 * twistAmount;
                float dir0x = (float)Math.cos(angle0);
                float dir0z = (float)Math.sin(angle0);
                float dir1x = (float)Math.cos(angle1);
                float dir1z = (float)Math.sin(angle1);
                float cx0 = dir0x * r0 + sway0x;
                float cz0 = dir0z * r0 + sway0z;
                float cx1 = dir1x * r1 + sway1x;
                float cz1 = dir1z * r1 + sway1z;
                int panelYSeed = entitySeed + yStep * 131 + i * 977;
                float vNoise0 = LayerVenkrolTornado.pseudoNoise(panelYSeed, 41);
                float vNoise1 = LayerVenkrolTornado.pseudoNoise(panelYSeed, 59);
                float yOffset0 = vNoise0 * segmentHeight * 0.35f;
                float yOffset1 = vNoise1 * segmentHeight * 0.35f;
                float y0Panel = y0 + yOffset0;
                float y1Panel = y1 + yOffset1;
                right0x = -dir0z;
                right0z = dir0x;
                right1x = -dir1z;
                right1z = dir1x;
                float x0L = cx0 - right0x * stripHalfWidth0;
                float z0L = cz0 - right0z * stripHalfWidth0;
                float x0R = cx0 + right0x * stripHalfWidth0;
                float z0R = cz0 + right0z * stripHalfWidth0;
                float x1L = cx1 - right1x * stripHalfWidth1;
                float z1L = cz1 - right1z * stripHalfWidth1;
                float x1R = cx1 + right1x * stripHalfWidth1;
                float z1R = cz1 + right1z * stripHalfWidth1;
                float u0 = 0.0f;
                float u1 = 1.0f;
                float v0 = 0.0f;
                float v1 = 1.0f;
                int r = 255;
                int g = 255;
                int b = 255;
                buf.pos((double)x0L, (double)y0Panel, (double)z0L).tex((double)u0, (double)v0).color(r, g, b, (int)(alpha0 * 255.0f)).endVertex();
                buf.pos((double)x0R, (double)y0Panel, (double)z0R).tex((double)u1, (double)v0).color(r, g, b, (int)(alpha0 * 255.0f)).endVertex();
                buf.pos((double)x1R, (double)y1Panel, (double)z1R).tex((double)u1, (double)v1).color(r, g, b, (int)(alpha1 * 255.0f)).endVertex();
                buf.pos((double)x1L, (double)y1Panel, (double)z1L).tex((double)u0, (double)v1).color(r, g, b, (int)(alpha1 * 255.0f)).endVertex();
            }
        }
        tess.draw();
        this.renderer.bindTexture(TORNADO_OUTER_TEX);
        buf.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
        for (yStep = 0; yStep < verticalSegments; ++yStep) {
            t0 = (float)yStep / (float)verticalSegments;
            t1 = (float)(yStep + 1) / (float)verticalSegments;
            y0 = -t0 * height;
            y1 = -t1 * height;
            r0 = bottomRadius + (topRadius - bottomRadius) * t0;
            r1 = bottomRadius + (topRadius - bottomRadius) * t1;
            alpha0 = (float)Math.sin(t0 * (float)Math.PI);
            alpha1 = (float)Math.sin(t1 * (float)Math.PI);
            float extraAlphaScale = 0.6f;
            alpha0 *= extraAlphaScale;
            alpha1 *= extraAlphaScale;
            float swayBase = 0.8f;
            float swayTop = 5.5f;
            float swayMag0 = swayBase + (swayTop - swayBase) * t0;
            float swayMag1 = swayBase + (swayTop - swayBase) * t1;
            float phase0 = time * 0.7f + t0 * 8.0f;
            float phase1 = time * 0.7f + t1 * 8.0f;
            float sway0x = (float)Math.sin(phase0) * swayMag0;
            float sway0z = (float)Math.cos((double)phase0 * 0.9) * swayMag0;
            float sway1x = (float)Math.sin(phase1) * swayMag1;
            float sway1z = (float)Math.cos((double)phase1 * 0.9) * swayMag1;
            float baseHalfWidth = 0.8f;
            float coverageFactor = 0.7f;
            float circumference0 = (float)Math.PI * 2 * (r0 + 3.0f);
            float circumference1 = (float)Math.PI * 2 * (r1 + 3.0f);
            float arcPerSlice0 = circumference0 / (float)aroundSegments;
            float arcPerSlice1 = circumference1 / (float)aroundSegments;
            float neededHalfWidth0 = arcPerSlice0 * coverageFactor * 0.5f;
            float neededHalfWidth1 = arcPerSlice1 * coverageFactor * 0.5f;
            float stripHalfWidth0 = Math.max(baseHalfWidth, neededHalfWidth0);
            float stripHalfWidth1 = Math.max(baseHalfWidth, neededHalfWidth1);
            int extraPanelsPerRing = 6;
            float extraShell = 3.0f;
            float extraSpinBase = 3.0f;
            for (int e = 0; e < extraPanelsPerRing; ++e) {
                int panelSeed = entitySeed + yStep * 131 + e * 911;
                float angleNoise = LayerVenkrolTornado.pseudoNoise(panelSeed, 11);
                float baseAngle = angleNoise * (float)Math.PI;
                float speedNoise = LayerVenkrolTornado.pseudoNoise(panelSeed, 23);
                float localSpin = extraSpinBase * (1.0f + speedNoise * 0.4f);
                float phaseNoise = LayerVenkrolTornado.pseudoNoise(panelSeed, 37);
                float phaseOffset = phaseNoise * (float)Math.PI;
                float angle0 = baseAngle + time * localSpin + phaseOffset + t0 * twistAmount;
                float angle1 = baseAngle + time * localSpin + phaseOffset + t1 * twistAmount;
                float dir0x = (float)Math.cos(angle0);
                float dir0z = (float)Math.sin(angle0);
                float dir1x = (float)Math.cos(angle1);
                float dir1z = (float)Math.sin(angle1);
                float shellNoise0 = Math.abs(LayerVenkrolTornado.pseudoNoise(panelSeed, 41));
                float shellNoise1 = Math.abs(LayerVenkrolTornado.pseudoNoise(panelSeed, 53));
                float rOuter0 = r0 + 1.0f + shellNoise0 * extraShell;
                float rOuter1 = r1 + 1.0f + shellNoise1 * extraShell;
                float cx0 = dir0x * rOuter0 + sway0x;
                float cz0 = dir0z * rOuter0 + sway0z;
                float cx1 = dir1x * rOuter1 + sway1x;
                float cz1 = dir1z * rOuter1 + sway1z;
                right0x = -dir0z;
                right0z = dir0x;
                right1x = -dir1z;
                right1z = dir1x;
                float debrisHalfWidth0 = stripHalfWidth0 * 0.7f;
                float debrisHalfWidth1 = stripHalfWidth1 * 0.7f;
                float x0L = cx0 - right0x * debrisHalfWidth0;
                float z0L = cz0 - right0z * debrisHalfWidth0;
                float x0R = cx0 + right0x * debrisHalfWidth0;
                float z0R = cz0 + right0z * debrisHalfWidth0;
                float x1L = cx1 - right1x * debrisHalfWidth1;
                float z1L = cz1 - right1z * debrisHalfWidth1;
                float x1R = cx1 + right1x * debrisHalfWidth1;
                float z1R = cz1 + right1z * debrisHalfWidth1;
                float u0 = 0.0f;
                float u1 = 1.0f;
                float v0 = 0.0f;
                float v1 = 1.0f;
                int rCol = 255;
                int gCol = 255;
                int bCol = 255;
                buf.pos((double)x0L, (double)y0, (double)z0L).tex((double)u0, (double)v0).color(rCol, gCol, bCol, (int)(alpha0 * 255.0f)).endVertex();
                buf.pos((double)x0R, (double)y0, (double)z0R).tex((double)u1, (double)v0).color(rCol, gCol, bCol, (int)(alpha0 * 255.0f)).endVertex();
                buf.pos((double)x1R, (double)y1, (double)z1R).tex((double)u1, (double)v1).color(rCol, gCol, bCol, (int)(alpha1 * 255.0f)).endVertex();
                buf.pos((double)x1L, (double)y1, (double)z1L).tex((double)u0, (double)v1).color(rCol, gCol, bCol, (int)(alpha1 * 255.0f)).endVertex();
            }
        }
        tess.draw();
        this.renderer.bindTexture(TORNADO_OUTER_TEX);
        buf.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
        float cloudCapHeight = 40.0f;
        float cloudInnerRadius = topRadius * 1.2f;
        float cloudOuterRadius = topRadius * 5.0f;
        int cloudRings = 5;
        int cloudSegments = 32;
        float cloudSpinBase = 0.7f;
        float joinY = -height;
        float capYOffset = 25.0f;
        float capBottomY = joinY - 2.0f - capYOffset;
        float capTopY = capBottomY - cloudCapHeight;
        for (int ring = 0; ring < cloudRings; ++ring) {
            float ct0 = (float)ring / (float)cloudRings;
            float ct1 = (float)(ring + 1) / (float)cloudRings;
            float cy0 = capBottomY + ct0 * (capTopY - capBottomY);
            float cy1 = capBottomY + ct1 * (capTopY - capBottomY);
            float rt0 = 1.0f - ct0;
            float rt1 = 1.0f - ct1;
            float cr0 = cloudInnerRadius + (cloudOuterRadius - cloudInnerRadius) * rt0;
            float cr1 = cloudInnerRadius + (cloudOuterRadius - cloudInnerRadius) * rt1;
            float ht0 = ct0;
            float ht1 = ct1;
            float capAlpha0 = 1.0f - ht0;
            float capAlpha1 = 1.0f - ht1;
            capAlpha0 *= capAlpha0;
            capAlpha1 *= capAlpha1;
            float maxCapAlpha = 0.75f;
            capAlpha0 *= maxCapAlpha;
            capAlpha1 *= maxCapAlpha;
            float thicknessFactor0 = 1.0f - ht0 * 0.8f;
            float thicknessFactor1 = 1.0f - ht1 * 0.8f;
            float swayBaseCap = 0.5f;
            float swayTopCap = 3.0f;
            float swayMagC0 = swayBaseCap + (swayTopCap - swayBaseCap) * ht0;
            float swayMagC1 = swayBaseCap + (swayTopCap - swayBaseCap) * ht1;
            float phaseC0 = time * 0.4f + ht0 * 5.0f;
            float phaseC1 = time * 0.4f + ht1 * 5.0f;
            float swayCx0 = (float)Math.sin(phaseC0) * swayMagC0;
            float swayCz0 = (float)Math.cos((double)phaseC0 * 0.9) * swayMagC0;
            float swayCx1 = (float)Math.sin(phaseC1) * swayMagC1;
            float swayCz1 = (float)Math.cos((double)phaseC1 * 0.9) * swayMagC1;
            float capCoverage = 0.9f;
            float capCirc0 = (float)Math.PI * 2 * cr0;
            float capCirc1 = (float)Math.PI * 2 * cr1;
            float capHalfW0 = capCirc0 / (float)cloudSegments * capCoverage * 0.5f;
            float capHalfW1 = capCirc1 / (float)cloudSegments * capCoverage * 0.5f;
            for (int i = 0; i < cloudSegments; ++i) {
                float frac = (float)i / (float)cloudSegments;
                int cloudSeed = entitySeed + ring * 733 + i * 1871;
                float spinNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 71);
                float phaseNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 83);
                float radiusNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 97);
                float localSpin = cloudSpinBase * (1.0f + spinNoise * 0.25f);
                float angleBase = (float)((double)frac * Math.PI * 2.0) + time * localSpin + phaseNoise * (float)Math.PI;
                float angle0 = angleBase + ht0 * twistAmount * 0.4f;
                float angle1 = angleBase + ht1 * twistAmount * 0.4f;
                float dir0x = (float)Math.cos(angle0);
                float dir0z = (float)Math.sin(angle0);
                float dir1x = (float)Math.cos(angle1);
                float dir1z = (float)Math.sin(angle1);
                float rc0 = cr0 + radiusNoise * 1.5f;
                float rc1 = cr1 + radiusNoise * 1.5f;
                float ccx0 = dir0x * rc0 + swayCx0;
                float ccz0 = dir0z * rc0 + swayCz0;
                float ccx1 = dir1x * rc1 + swayCx1;
                float ccz1 = dir1z * rc1 + swayCz1;
                float right0x2 = -dir0z;
                float right0z2 = dir0x;
                float right1x2 = -dir1z;
                float right1z2 = dir1x;
                float cy0Panel = cy0 * thicknessFactor0;
                float cy1Panel = cy1 * thicknessFactor1;
                float slabHalfW0 = capHalfW0 * 1.2f;
                float slabHalfW1 = capHalfW1 * 1.2f;
                float x0L = ccx0 - right0x2 * slabHalfW0;
                float z0L = ccz0 - right0z2 * slabHalfW0;
                float x0R = ccx0 + right0x2 * slabHalfW0;
                float z0R = ccz0 + right0z2 * slabHalfW0;
                float x1L = ccx1 - right1x2 * slabHalfW1;
                float z1L = ccz1 - right1z2 * slabHalfW1;
                float x1R = ccx1 + right1x2 * slabHalfW1;
                float z1R = ccz1 + right1z2 * slabHalfW1;
                float u0 = 0.0f;
                float u1 = 1.0f;
                float v0 = 0.0f;
                float v1 = 1.0f;
                int rCol = 255;
                int gCol = 255;
                int bCol = 255;
                buf.pos((double)x0L, (double)cy0Panel, (double)z0L).tex((double)u0, (double)v0).color(rCol, gCol, bCol, (int)(capAlpha0 * 255.0f)).endVertex();
                buf.pos((double)x0R, (double)cy0Panel, (double)z0R).tex((double)u1, (double)v0).color(rCol, gCol, bCol, (int)(capAlpha0 * 255.0f)).endVertex();
                buf.pos((double)x1R, (double)cy1Panel, (double)z1R).tex((double)u1, (double)v1).color(rCol, gCol, bCol, (int)(capAlpha1 * 255.0f)).endVertex();
                buf.pos((double)x1L, (double)cy1Panel, (double)z1L).tex((double)u0, (double)v1).color(rCol, gCol, bCol, (int)(capAlpha1 * 255.0f)).endVertex();
            }
        }
        tess.draw();
        this.renderer.bindTexture(TORNADO_OUTER_TEX);
        buf.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
        float innerCloudCapHeight = cloudCapHeight * 0.6f;
        float innerCloudInnerRadius = cloudInnerRadius * 0.5f;
        float innerCloudOuterRadius = cloudOuterRadius * 0.5f;
        int innerCloudRings = 4;
        int innerCloudSegments = 28;
        float innerCloudSpinBase = cloudSpinBase * 0.8f;
        float innerOffsetDown = 40.0f;
        float innerCapBottomY = joinY + innerOffsetDown;
        float innerCapTopY = innerCapBottomY - innerCloudCapHeight;
        for (int ring = 0; ring < innerCloudRings; ++ring) {
            float ct0 = (float)ring / (float)innerCloudRings;
            float ct1 = (float)(ring + 1) / (float)innerCloudRings;
            float cy0 = innerCapBottomY + ct0 * (innerCapTopY - innerCapBottomY);
            float cy1 = innerCapBottomY + ct1 * (innerCapTopY - innerCapBottomY);
            float rt0 = 1.0f - ct0;
            float rt1 = 1.0f - ct1;
            float cr0 = innerCloudInnerRadius + (innerCloudOuterRadius - innerCloudInnerRadius) * rt0;
            float cr1 = innerCloudInnerRadius + (innerCloudOuterRadius - innerCloudInnerRadius) * rt1;
            float ht0 = ct0;
            float ht1 = ct1;
            float capAlpha0 = 1.0f - ht0;
            float capAlpha1 = 1.0f - ht1;
            capAlpha0 *= capAlpha0;
            capAlpha1 *= capAlpha1;
            float maxCapAlpha = 0.55f;
            capAlpha0 *= maxCapAlpha;
            capAlpha1 *= maxCapAlpha;
            float thicknessFactor0 = 1.0f - ht0 * 0.6f;
            float thicknessFactor1 = 1.0f - ht1 * 0.6f;
            float swayBaseCap = 0.4f;
            float swayTopCap = 2.0f;
            float swayMagC0 = swayBaseCap + (swayTopCap - swayBaseCap) * ht0;
            float swayMagC1 = swayBaseCap + (swayTopCap - swayBaseCap) * ht1;
            float phaseC0 = time * 0.35f + ht0 * 4.0f;
            float phaseC1 = time * 0.35f + ht1 * 4.0f;
            float swayCx0 = (float)Math.sin(phaseC0) * swayMagC0;
            float swayCz0 = (float)Math.cos((double)phaseC0 * 0.9) * swayMagC0;
            float swayCx1 = (float)Math.sin(phaseC1) * swayMagC1;
            float swayCz1 = (float)Math.cos((double)phaseC1 * 0.9) * swayMagC1;
            float capCoverage = 0.9f;
            float capCirc0 = (float)Math.PI * 2 * cr0;
            float capCirc1 = (float)Math.PI * 2 * cr1;
            float capHalfW0 = capCirc0 / (float)innerCloudSegments * capCoverage * 0.5f;
            float capHalfW1 = capCirc1 / (float)innerCloudSegments * capCoverage * 0.5f;
            for (int i = 0; i < innerCloudSegments; ++i) {
                float frac = (float)i / (float)innerCloudSegments;
                int cloudSeed = entitySeed + 9999 + ring * 733 + i * 1871;
                float spinNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 71);
                float phaseNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 83);
                float radiusNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 97);
                float localSpin = innerCloudSpinBase * (1.0f + spinNoise * 0.25f);
                float angleBase = (float)((double)frac * Math.PI * 2.0) + time * localSpin + phaseNoise * (float)Math.PI;
                float angle0 = angleBase + ht0 * twistAmount * 0.4f;
                float angle1 = angleBase + ht1 * twistAmount * 0.4f;
                float dir0x = (float)Math.cos(angle0);
                float dir0z = (float)Math.sin(angle0);
                float dir1x = (float)Math.cos(angle1);
                float dir1z = (float)Math.sin(angle1);
                float rc0 = cr0 + radiusNoise * 1.2f;
                float rc1 = cr1 + radiusNoise * 1.2f;
                float ccx0 = dir0x * rc0 + swayCx0;
                float ccz0 = dir0z * rc0 + swayCz0;
                float ccx1 = dir1x * rc1 + swayCx1;
                float ccz1 = dir1z * rc1 + swayCz1;
                float right0x3 = -dir0z;
                float right0z3 = dir0x;
                float right1x3 = -dir1z;
                float right1z3 = dir1x;
                float cy0Panel = cy0 * thicknessFactor0;
                float cy1Panel = cy1 * thicknessFactor1;
                float slabHalfW0 = capHalfW0 * 1.1f;
                float slabHalfW1 = capHalfW1 * 1.1f;
                float x0L = ccx0 - right0x3 * slabHalfW0;
                float z0L = ccz0 - right0z3 * slabHalfW0;
                float x0R = ccx0 + right0x3 * slabHalfW0;
                float z0R = ccz0 + right0z3 * slabHalfW0;
                float x1L = ccx1 - right1x3 * slabHalfW1;
                float z1L = ccz1 - right1z3 * slabHalfW1;
                float x1R = ccx1 + right1x3 * slabHalfW1;
                float z1R = ccz1 + right1z3 * slabHalfW1;
                float u0 = 0.0f;
                float u1 = 1.0f;
                float v0 = 0.0f;
                float v1 = 1.0f;
                int rCol = 255;
                int gCol = 255;
                int bCol = 255;
                buf.pos((double)x0L, (double)cy0Panel, (double)z0L).tex((double)u0, (double)v0).color(rCol, gCol, bCol, (int)(capAlpha0 * 255.0f)).endVertex();
                buf.pos((double)x0R, (double)cy0Panel, (double)z0R).tex((double)u1, (double)v0).color(rCol, gCol, bCol, (int)(capAlpha0 * 255.0f)).endVertex();
                buf.pos((double)x1R, (double)cy1Panel, (double)z1R).tex((double)u1, (double)v1).color(rCol, gCol, bCol, (int)(capAlpha1 * 255.0f)).endVertex();
                buf.pos((double)x1L, (double)cy1Panel, (double)z1L).tex((double)u0, (double)v1).color(rCol, gCol, bCol, (int)(capAlpha1 * 255.0f)).endVertex();
            }
        }
        tess.draw();
        this.renderer.bindTexture(TORNADO_OUTER_TEX);
        Tessellator.BufferBuilder buf2 = tess.getWorldRenderer();
        buf2.begin(7, Tessellator.VertexFormat.POSITION_TEX_COLOR);
        float coreRadius = bottomRadius;
        float ringInnerRadius = coreRadius * 1.05f;
        float ringOuterRadius = coreRadius * 1.95f;
        float bottomY = joinY + height;
        float ringHeight = 4.0f;
        float verticalOffset = -4.0f;
        float ringBottomY = bottomY + 0.2f + verticalOffset;
        float ringTopY = ringBottomY + ringHeight;
        int ringRings = 2;
        int ringSegments = 64;
        float ringSpinBase = cloudSpinBase * 3.0f;
        for (int ring = 0; ring < ringRings; ++ring) {
            float ct0 = (float)ring / (float)ringRings;
            float ct1 = (float)(ring + 1) / (float)ringRings;
            float cy0 = ringBottomY + ct0 * (ringTopY - ringBottomY);
            float cy1 = ringBottomY + ct1 * (ringTopY - ringBottomY);
            float rt0 = 1.0f - ct0;
            float rt1 = 1.0f - ct1;
            float cr0 = ringInnerRadius + (ringOuterRadius - ringInnerRadius) * rt0;
            float cr1 = ringInnerRadius + (ringOuterRadius - ringInnerRadius) * rt1;
            float ht0 = ct0;
            float ht1 = ct1;
            float alpha02 = 0.8f;
            float alpha12 = 0.7f;
            float thicknessFactor0 = 1.0f - ht0 * 0.2f;
            float thicknessFactor1 = 1.0f - ht1 * 0.2f;
            float swayBase = 0.15f;
            float swayTop = 0.4f;
            float swayMag0 = swayBase + (swayTop - swayBase) * ht0;
            float swayMag1 = swayBase + (swayTop - swayBase) * ht1;
            float phase0 = time * 0.7f + ht0 * 2.0f;
            float phase1 = time * 0.7f + ht1 * 2.0f;
            float swayX0 = (float)Math.sin(phase0) * swayMag0;
            float swayZ0 = (float)Math.cos((double)phase0 * 0.9) * swayMag0;
            float swayX1 = (float)Math.sin(phase1) * swayMag1;
            float swayZ1 = (float)Math.cos((double)phase1 * 0.9) * swayMag1;
            float coverage = 0.9f;
            float circ0 = (float)Math.PI * 2 * cr0;
            float circ1 = (float)Math.PI * 2 * cr1;
            float halfW0 = circ0 / (float)ringSegments * coverage * 0.5f;
            float halfW1 = circ1 / (float)ringSegments * coverage * 0.5f;
            for (int i = 0; i < ringSegments; ++i) {
                float frac = (float)i / (float)ringSegments;
                int cloudSeed = entitySeed + 77777 + ring * 733 + i * 1871;
                float spinNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 71);
                float phaseNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 83);
                float radiusNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 97);
                float localSpin = ringSpinBase * (1.0f + spinNoise * 0.3f);
                float angleBase = (float)((double)frac * Math.PI * 2.0) + time * localSpin + phaseNoise * (float)Math.PI;
                float angle0 = angleBase + ht0 * twistAmount * 0.2f;
                float angle1 = angleBase + ht1 * twistAmount * 0.2f;
                float dir0x = (float)Math.cos(angle0);
                float dir0z = (float)Math.sin(angle0);
                float dir1x = (float)Math.cos(angle1);
                float dir1z = (float)Math.sin(angle1);
                float rc0 = cr0 + radiusNoise * 0.5f;
                float rc1 = cr1 + radiusNoise * 0.5f;
                float ccx0 = dir0x * rc0 + swayX0;
                float ccz0 = dir0z * rc0 + swayZ0;
                float ccx1 = dir1x * rc1 + swayX1;
                float ccz1 = dir1z * rc1 + swayZ1;
                float right0x4 = -dir0z;
                float right0z4 = dir0x;
                float right1x4 = -dir1z;
                float right1z4 = dir1x;
                float cy0Panel = cy0 * thicknessFactor0;
                float cy1Panel = cy1 * thicknessFactor1;
                float hNoise = LayerVenkrolTornado.pseudoNoise(cloudSeed, 113);
                float hJitterAmp = 1.5f;
                float heightOffset = (hNoise - 0.5f) * hJitterAmp;
                float cy0J = cy0Panel + heightOffset;
                float cy1J = cy1Panel + heightOffset;
                float slabHalfW0 = halfW0;
                float slabHalfW1 = halfW1;
                float x0L = ccx0 - right0x4 * slabHalfW0;
                float z0L = ccz0 - right0z4 * slabHalfW0;
                float x0R = ccx0 + right0x4 * slabHalfW0;
                float z0R = ccz0 + right0z4 * slabHalfW0;
                float x1L = ccx1 - right1x4 * slabHalfW1;
                float z1L = ccz1 - right1z4 * slabHalfW1;
                float x1R = ccx1 + right1x4 * slabHalfW1;
                float z1R = ccz1 + right1z4 * slabHalfW1;
                float u0 = 0.0f;
                float u1 = 1.0f;
                float v0 = 0.0f;
                float v1 = 1.0f;
                int rCol = 255;
                int gCol = 255;
                int bCol = 255;
                buf.pos((double)x0L, (double)cy0J, (double)z0L).tex((double)u0, (double)v0).color(rCol, gCol, bCol, (int)(alpha02 * 255.0f)).endVertex();
                buf.pos((double)x0R, (double)cy0J, (double)z0R).tex((double)u1, (double)v0).color(rCol, gCol, bCol, (int)(alpha02 * 255.0f)).endVertex();
                buf.pos((double)x1R, (double)cy1J, (double)z1R).tex((double)u1, (double)v1).color(rCol, gCol, bCol, (int)(alpha12 * 255.0f)).endVertex();
                buf.pos((double)x1L, (double)cy1J, (double)z1L).tex((double)u0, (double)v1).color(rCol, gCol, bCol, (int)(alpha12 * 255.0f)).endVertex();
            }
        }
        tess.draw();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    private static float pseudoNoise(int seed, int salt) {
        int s = seed * 73428767 ^ salt * 912931;
        s = s * 1103515245 + 12345;
        int v = s >>> 16 & Short.MAX_VALUE;
        return (float)v / 16384.0f - 1.0f;
    }

    private static class VenkrolVortexLoopSound
    extends AbstractTickableSoundInstance {
        private EntityVenkrolSIV entity;
        private static final float HEAR_DISTANCE = 100.0f;
        private boolean fadingOut = false;
        private int fadeTicks = 0;
        private static final int FADE_DURATION = 10;
        private boolean finished = false;

        boolean isFadingOut() {
            return this.fadingOut;
        }

        boolean isFinished() {
            return this.finished;
        }

        void cancelFadeOut() {
            this.fadingOut = false;
            this.fadeTicks = 0;
        }

        VenkrolVortexLoopSound(EntityVenkrolSIV entity) {
            super(SRPSounds.BECKON_VORTEX.get(), SoundSource.WEATHER, SoundInstance.createUnseededRandom());
            this.entity = entity;
            this.looping = true;
            this.delay = 0;
            this.attenuation = SoundInstance.Attenuation.LINEAR;
            this.volume = 8.25f;
            this.pitch = 1.0f;
            this.updatePos();
        }

        void setEntity(EntityVenkrolSIV e) {
            this.entity = e;
        }

        void fadeOut() {
            this.fadingOut = true;
            this.fadeTicks = 10;
        }

        private void updatePos() {
            this.x = this.entity.getX();
            this.y = this.entity.getY() + 2.0;
            this.z = this.entity.getZ();
        }

        @Override
        public void tick() {
            if (this.entity == null || this.entity.isRemoved()) {
                this.finished = true;
                this.stop();
                return;
            }
            this.updatePos();
            if (this.fadingOut) {
                --this.fadeTicks;
                this.volume *= 0.7f;
                if (this.fadeTicks <= 0 || this.volume < 0.01f) {
                    this.finished = true;
                this.stop();
                }
                return;
            }
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.player != null) {
                float dx = (float)(mc.player.getX() - this.entity.getX());
                float dy = (float)(mc.player.getY() - (this.entity.getY() + 2.0));
                float dz = (float)(mc.player.getZ() - this.entity.getZ());
                float dist = Mth.sqrt((float)(dx * dx + dy * dy + dz * dz));
                float t = 1.0f - Mth.clamp((float)(dist / 100.0f), (float)0.0f, (float)1.0f);
                this.volume = 6.25f * (0.35f + 0.65f * (t * t));
            }
        }
    }
}

