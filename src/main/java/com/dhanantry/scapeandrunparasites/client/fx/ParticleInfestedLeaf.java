package com.dhanantry.scapeandrunparasites.client.fx;

import com.dhanantry.scapeandrunparasites.client.particle.LegacyParticle;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/** Tumbling leaf that falls from infested leaves. Created directly (see BlockClientHooks), drawn as a freely oriented quad. */
public class ParticleInfestedLeaf extends LegacyParticle {
    private float yaw;
    private float pitch;
    private float rollVel;
    private float sway;
    private float swaySpeed;
    private float swayAmp;

    /** The level must be the client level. */
    public ParticleInfestedLeaf(Level w, double x, double y, double z) {
        super((ClientLevel) w, x, y, z);
        this.setSprite(ClientSRPParticles.randomInfestedLeaf(w.random));
        this.scale = 5.6f + this.random.nextFloat() * 0.6f;
        this.lifetime = 80 + this.random.nextInt(80);
        this.gravity = 0.02f;
        this.xd = (this.random.nextDouble() - 0.5) * 0.01;
        this.zd = (this.random.nextDouble() - 0.5) * 0.01;
        this.yd = -0.01 - this.random.nextDouble() * 0.01;
        this.yaw = (float) (this.random.nextDouble() * Math.PI * 2.0);
        this.pitch = (float) ((this.random.nextDouble() * 0.7 - 0.35) * Math.PI * 0.25);
        this.rollVel = (this.random.nextFloat() - 0.5f) * 0.12f;
        this.sway = this.random.nextFloat() * (float) Math.PI * 2.0f;
        this.swaySpeed = 0.15f + this.random.nextFloat() * 0.15f;
        this.swayAmp = 0.02f + this.random.nextFloat() * 0.02f;
        this.hasPhysics = true;
    }

    @Override
    public void tick() {
        this.oRoll = this.roll;
        this.roll += this.rollVel;
        this.sway += this.swaySpeed;
        double flutterX = Math.cos(this.sway) * (double) this.swayAmp;
        double flutterZ = Math.sin(this.sway * 1.3f) * ((double) this.swayAmp * 0.8);
        this.xd += flutterX * 0.2;
        this.zd += flutterZ * 0.2;
        this.yaw += (float) (Math.sin((double) this.sway * 0.7) * 0.003);
        this.pitch += (float) (Math.cos((double) this.sway * 0.9) * 0.003);
        this.yd -= 0.002 + 0.001 * Math.sin((double) this.sway * 0.5);
        if (this.yd < -0.06) {
            this.yd = -0.06;
        }
        super.tick();
        if (this.removed || this.onGround) {
            this.remove();
            return;
        }
        this.xd *= 0.96;
        this.zd *= 0.96;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTicks) {
        if (this.sprite == null) {
            return;
        }
        Vec3 cam = camera.getPosition();
        double x = Mth.lerp((double) partialTicks, this.xo, this.x) - cam.x;
        double y = Mth.lerp((double) partialTicks, this.yo, this.y) - cam.y;
        double z = Mth.lerp((double) partialTicks, this.zo, this.z) - cam.z;
        float s = 0.1f * this.scale * 0.5f;
        float roll = this.oRoll + (this.roll - this.oRoll) * partialTicks;
        float cy = (float) Math.cos(this.yaw);
        float sy = (float) Math.sin(this.yaw);
        float cp = (float) Math.cos(this.pitch);
        float sp = (float) Math.sin(this.pitch);
        float cr = (float) Math.cos(roll);
        float sr = (float) Math.sin(roll);
        float rx = cr * cy + sr * sp * sy;
        float ry = sr * cp;
        float rz = -sr * sy + cr * sp * cy;
        float ux = -sr * cy + cr * sp * sy;
        float uy = cr * cp;
        float uz = sr * sy + cr * sp * cy;
        double x1 = x - (double) (rx *= s) - (double) (ux *= s);
        double y1 = y - (double) (ry *= s) - (double) (uy *= s);
        double z1 = z - (double) (rz *= s) - (double) (uz *= s);
        double x2 = x - (double) rx + (double) ux;
        double y2 = y - (double) ry + (double) uy;
        double z2 = z - (double) rz + (double) uz;
        double x3 = x + (double) rx + (double) ux;
        double y3 = y + (double) ry + (double) uy;
        double z3 = z + (double) rz + (double) uz;
        double x4 = x + (double) rx - (double) ux;
        double y4 = y + (double) ry - (double) uy;
        double z4 = z + (double) rz - (double) uz;
        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int light = this.getLightColor(partialTicks);
        int a = (int) (this.alpha * 255.0f);
        int r = (int) (this.rCol * 255.0f);
        int g = (int) (this.gCol * 255.0f);
        int b = (int) (this.bCol * 255.0f);
        buffer.addVertex((float) x1, (float) y1, (float) z1).setUv(u1, v1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex((float) x2, (float) y2, (float) z2).setUv(u1, v0).setColor(r, g, b, a).setLight(light);
        buffer.addVertex((float) x3, (float) y3, (float) z3).setUv(u0, v0).setColor(r, g, b, a).setLight(light);
        buffer.addVertex((float) x4, (float) y4, (float) z4).setUv(u0, v1).setColor(r, g, b, a).setLight(light);
    }
}
