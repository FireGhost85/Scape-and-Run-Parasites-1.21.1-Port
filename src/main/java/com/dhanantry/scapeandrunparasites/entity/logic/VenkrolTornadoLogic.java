package com.dhanantry.scapeandrunparasites.entity.logic;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class VenkrolTornadoLogic {
    public static void tickTornadoEffects(LivingEntity venkrol) {
        if (!SRPConfigWorld.venkrolTornadoEnabled) {
            return;
        }
        if (venkrol == null) {
            return;
        }
        Level world = venkrol.level();
        if (world == null || world.isClientSide) {
            return;
        }
        if (!world.isRaining() || !world.isThundering()) {
            return;
        }
        double x = venkrol.getX();
        double maxRadius = 120.0;
        double y = venkrol.getY();
        double z = venkrol.getZ();
        double height = 50.0;
        AABB box = new AABB(x - maxRadius, y, z - maxRadius, x + maxRadius, y + height, z + maxRadius);
        List<? extends LivingEntity> targets = world.getEntitiesOfClass(LivingEntity.class, box);
        if (targets.isEmpty()) {
            return;
        }
        for (LivingEntity e : targets) {
            if (e == venkrol || !e.isAlive() || VenkrolTornadoLogic.isSRPParasite(e) || e.getY() < venkrol.getY()) continue;
            VenkrolTornadoLogic.applyTornadoForces(venkrol, e, maxRadius);
        }
    }

    private static boolean isSRPParasite(LivingEntity e) {
        ResourceLocation rl = BuiltInRegistries.ENTITY_TYPE.getKey(e.getType());
        if (rl == null) {
            return false;
        }
        String modid = rl.getNamespace();
        return "srparasites".equals(modid);
    }

    private static void applyTornadoForces(LivingEntity venkrol, LivingEntity target, double maxRadius) {
        double heightAboveVenkrol;
        boolean inFlingZone;
        double horizDist;
        Level world;
        if (target instanceof Player) {
            Player player = (Player)target;
            ItemStack boots = (ItemStack)player.getInventory().armor.get(0);
            if (!boots.isEmpty() && boots.getItem() == SRPItems.VENKROL_BOOTS.get()) {
                return;
            }
            if (player.isSpectator()) {
                return;
            }
            if (player.getAbilities().instabuild && player.getAbilities().flying) {
                return;
            }
        }
        if ((world = venkrol.level()) == null) {
            return;
        }
        if (target.isPassenger() || target.isRemoved()) {
            return;
        }
        double dx = venkrol.getX() - target.getX();
        double dz = venkrol.getZ() - target.getZ();
        double distSq = dx * dx + dz * dz;
        if (distSq < 1.0E-4) {
            distSq = 1.0E-4;
        }
        if ((horizDist = Math.sqrt(distSq)) > maxRadius) {
            return;
        }
        double normX = dx / horizDist;
        double normZ = dz / horizDist;
        double pullTierFactor = horizDist >= 50.0 ? 0.05 : (horizDist >= 25.0 ? 0.1 : (horizDist >= 15.0 ? 0.2 : (horizDist >= 10.0 ? 0.35 : (horizDist >= 5.0 ? 0.55 : 1.0))));
        double basePullStrength = 0.08;
        double baseSwirlStrength = 0.07;
        double pullStrength = basePullStrength * pullTierFactor;
        double swirlStrength = baseSwirlStrength * pullTierFactor;
        double swirlX = -normZ;
        double swirlZ = normX;
        double innerLiftRadius = 15.0;
        double liftAccel = 0.0;
        if (horizDist <= innerLiftRadius) {
            double liftFactor = 1.0 - horizDist / innerLiftRadius;
            if (liftFactor < 0.0) {
                liftFactor = 0.0;
            }
            if (liftFactor > 1.0) {
                liftFactor = 1.0;
            }
            double baseLiftAccel = 0.25;
            liftAccel = baseLiftAccel * liftFactor;
        }
        boolean bl = inFlingZone = (heightAboveVenkrol = target.getY() - venkrol.getY()) > 16.0 && horizDist < 12.0;
        if (heightAboveVenkrol > 18.0 && horizDist > 18.0) {
            return;
        }
        double radialDirX = normX;
        double radialDirZ = normZ;
        if (inFlingZone) {
            radialDirX = -normX;
            radialDirZ = -normZ;
            double flingFactor = 1.0 - Math.min(horizDist / 12.0, 1.0);
            double baseFlingMult = 1.4;
            double maxFlingMult = 6.0;
            double flingMult = baseFlingMult + (maxFlingMult - baseFlingMult) * flingFactor;
            pullStrength *= flingMult;
            double baseSwirlMult = 1.0;
            double maxSwirlMult = 2.3;
            double swirlMult = baseSwirlMult + (maxSwirlMult - baseSwirlMult) * flingFactor;
            swirlStrength *= swirlMult;
            double outwardBurst = 1.1 * flingFactor;
            Mot.addX(target, radialDirX * outwardBurst);
            Mot.addZ(target, radialDirZ * outwardBurst);
            double downwardBoost = -0.16 * flingFactor;
            Mot.addY(target, downwardBoost);
            if (target.getDeltaMovement().y < -1.6) {
                Mot.setY(target, -1.6);
            }
        } else if (liftAccel > 0.0 && heightAboveVenkrol < 25.0) {
            Mot.addY(target, liftAccel);
            if (target.getDeltaMovement().y > 1.2) {
                Mot.setY(target, 1.2);
            }
            target.fallDistance = 0.0f;
        }
        double awayX = -normX;
        double awayZ = -normZ;
        double dotAway = target.getDeltaMovement().x * awayX + target.getDeltaMovement().z * awayZ;
        double forceScale = dotAway > 0.0 ? 1.0 : 0.25;
        Mot.addX(target, radialDirX * (pullStrength *= forceScale) + swirlX * swirlStrength);
        Mot.addZ(target, radialDirZ * pullStrength + swirlZ * swirlStrength);
        target.hurtMarked = true;
    }
}

