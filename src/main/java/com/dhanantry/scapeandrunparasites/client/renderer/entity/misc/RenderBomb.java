package com.dhanantry.scapeandrunparasites.client.renderer.entity.misc;

import com.dhanantry.scapeandrunparasites.client.legacy.GlStateManager;
import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.Render;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBombHost;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBombJinjo;
import com.dhanantry.scapeandrunparasites.client.model.entity.misc.ModelBombOmboo;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RenderBomb
extends Render<EntityBomb> {
    protected ModelBase modelO = new ModelBombOmboo();
    protected ModelBase modelH = new ModelBombHost();
    protected ModelBase modelJ = new ModelBombJinjo();
    public static final ResourceLocation TEXTUREO = ResourceLocation.parse("srparasites:textures/entity/monster/bombo.png");
    public static final ResourceLocation TEXTUREH = ResourceLocation.parse("srparasites:textures/entity/monster/bombh.png");
    public static final ResourceLocation TEXTUREJ = ResourceLocation.parse("srparasites:textures/entity/monster/bombj.png");

    public RenderBomb(RenderManager p_i47208_1_) {
        super(p_i47208_1_);
        this.shadowSize = 0.5f;
    }

    protected ResourceLocation getEntityTexture(EntityBomb entity) {
        switch (entity.getSkin()) {
            case 0: {
                return TEXTUREO;
            }
            case 1: {
                return TEXTUREH;
            }
            case 2: {
                return TEXTUREJ;
            }
            case 3: {
                return TEXTUREJ;
            }
        }
        return TEXTUREO;
    }

    public void doRender(EntityBomb entity, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        switch (entity.getSkin()) {
            case 0: {
                GlStateManager.translate((float)((float)x), (float)((float)y + 1.5f), (float)((float)z));
                this.bindTexture(TEXTUREO);
                break;
            }
            case 1: {
                GlStateManager.translate((float)((float)x), (float)((float)y + 1.5f), (float)((float)z));
                this.bindTexture(TEXTUREH);
                break;
            }
            case 2: {
                GlStateManager.translate((float)((float)x), (float)((float)y + 1.5f), (float)((float)z));
                this.bindTexture(TEXTUREJ);
                break;
            }
            case 3: {
                GlStateManager.translate((float)((float)x), (float)((float)y + 1.5f), (float)((float)z));
                this.bindTexture(TEXTUREJ);
            }
        }
        GlStateManager.rotate((float)180.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        if (this.renderOutlines) {
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode((int)this.getTeamColor(entity));
        }
        switch (entity.getSkin()) {
            case 0: {
                this.modelO.render((Entity)entity, 0.0f, 0.0f, (float)entity.tickCount, entity.getYRot(), entity.getXRot(), 0.0625f);
                break;
            }
            case 1: {
                this.modelH.render((Entity)entity, 0.0f, 0.0f, (float)entity.tickCount, entity.getYRot(), entity.getXRot(), 0.0625f);
                break;
            }
            case 2: {
                this.modelJ.render((Entity)entity, 0.0f, 0.0f, (float)entity.tickCount, entity.getYRot(), entity.getXRot(), 0.0625f);
                break;
            }
            case 3: {
                this.modelJ.render((Entity)entity, 0.0f, 0.0f, (float)entity.tickCount, entity.getYRot(), entity.getXRot(), 0.0625f);
            }
        }
        if (this.renderOutlines) {
            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
        }
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
        float f;
        for (f = yawOffset - prevYawOffset; f < -180.0f; f += 360.0f) {
        }
        while (f >= 180.0f) {
            f -= 360.0f;
        }
        return prevYawOffset + partialTicks * f;
    }
}

