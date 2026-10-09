package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.client.legacy.ModelBase;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderLiving;
import com.dhanantry.scapeandrunparasites.client.legacy.RenderManager;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.Frustum;

public abstract class RenderSRP<T extends EntityParasiteBase>
extends RenderLiving<T> {
    private int ticklag;
    private boolean tickflag;

    public RenderSRP(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn) {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
    }

    @Override
    public boolean shouldRender(T livingEntity, Frustum camera, double camX, double camY, double camZ) {
        ++this.ticklag;
        if (this.ticklag > 20) {
            this.ticklag = 0;
            this.tickflag = Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(SRPPotions.BRAINING_E);
        }
        if (this.tickflag) {
            return false;
        }
        return super.shouldRender(livingEntity, camera, camX, camY, camZ);
    }
}
