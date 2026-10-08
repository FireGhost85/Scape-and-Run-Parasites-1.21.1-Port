package com.dhanantry.scapeandrunparasites.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * 1.12 style per-axis motion / position writes ({@code entity.motionX += d}) on top of the 1.21 vector API.
 * Used by the translated entity code so the original arithmetic stays readable.
 */
public final class Mot {
    private Mot() {}

    public static void setX(Entity e, double v) {
        Vec3 m = e.getDeltaMovement();
        e.setDeltaMovement(v, m.y, m.z);
    }

    public static void setY(Entity e, double v) {
        Vec3 m = e.getDeltaMovement();
        e.setDeltaMovement(m.x, v, m.z);
    }

    public static void setZ(Entity e, double v) {
        Vec3 m = e.getDeltaMovement();
        e.setDeltaMovement(m.x, m.y, v);
    }

    public static void addX(Entity e, double v) {
        e.setDeltaMovement(e.getDeltaMovement().add(v, 0.0, 0.0));
    }

    public static void addY(Entity e, double v) {
        e.setDeltaMovement(e.getDeltaMovement().add(0.0, v, 0.0));
    }

    public static void addZ(Entity e, double v) {
        e.setDeltaMovement(e.getDeltaMovement().add(0.0, 0.0, v));
    }

    public static void mulX(Entity e, double v) {
        e.setDeltaMovement(e.getDeltaMovement().multiply(v, 1.0, 1.0));
    }

    public static void mulY(Entity e, double v) {
        e.setDeltaMovement(e.getDeltaMovement().multiply(1.0, v, 1.0));
    }

    public static void mulZ(Entity e, double v) {
        e.setDeltaMovement(e.getDeltaMovement().multiply(1.0, 1.0, v));
    }

    public static void setPosX(Entity e, double v) {
        e.setPos(v, e.getY(), e.getZ());
    }

    public static void setPosY(Entity e, double v) {
        e.setPos(e.getX(), v, e.getZ());
    }

    public static void setPosZ(Entity e, double v) {
        e.setPos(e.getX(), e.getY(), v);
    }

    /** {@code entity.motionX / motionY / motionZ} as a vector (read). */
    public static Vec3 get(Entity e) {
        return e.getDeltaMovement();
    }
}
