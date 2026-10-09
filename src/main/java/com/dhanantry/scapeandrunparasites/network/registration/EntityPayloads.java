package com.dhanantry.scapeandrunparasites.network.registration;

import com.dhanantry.scapeandrunparasites.client.ClientQlipShake;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityBodyParts;
import com.dhanantry.scapeandrunparasites.item.CompassColony;
import com.dhanantry.scapeandrunparasites.item.CompassNode;
import com.dhanantry.scapeandrunparasites.item.CompassOrigin;
import com.dhanantry.scapeandrunparasites.item.ItemClockDevelopment;
import com.dhanantry.scapeandrunparasites.item.ItemClockEvolution;
import com.dhanantry.scapeandrunparasites.network.ClockPayload;
import com.dhanantry.scapeandrunparasites.network.CompassPayload;
import com.dhanantry.scapeandrunparasites.network.EntityBodyDeadPayload;
import com.dhanantry.scapeandrunparasites.network.QlipShakePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Payloads of the parasite entities (body parts / tendrils, Qlip camera shake). Call from SRPNetwork.register. */
public final class EntityPayloads {
    private EntityPayloads() {
    }

    public static void register(PayloadRegistrar r) {
        r.playToClient(EntityBodyDeadPayload.TYPE, EntityBodyDeadPayload.CODEC, (msg, ctx) -> EntityPayloads.handleBodyDead(msg, ctx));
        r.playToClient(QlipShakePayload.TYPE, QlipShakePayload.CODEC, (msg, ctx) -> EntityPayloads.handleQlipShake(msg, ctx));
        r.playToClient(ClockPayload.TYPE, ClockPayload.CODEC, (msg, ctx) -> EntityPayloads.handleClock(msg, ctx));
        r.playToClient(CompassPayload.TYPE, CompassPayload.CODEC, (msg, ctx) -> EntityPayloads.handleCompass(msg, ctx));
    }

    private static void handleBodyDead(EntityBodyDeadPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level == null) {
                return;
            }
            Entity target = mc.level.getEntity(msg.targetId());
            if (target instanceof EntityBodyParts parts) {
                parts.setBodyPartDead(msg.partId());
            }
        });
    }

    private static void handleQlipShake(QlipShakePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientQlipShake.INSTANCE.triggerDelayed(msg.duration(), msg.delay(), msg.dark(), msg.shake(), msg.shakeValue()));
    }

    private static void handleClock(ClockPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ItemClockEvolution.cooldown = msg.cooldown();
            ItemClockEvolution.phase = msg.phase();
            ItemClockDevelopment.level = msg.development();
        });
    }

    private static void handleCompass(CompassPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            BlockPos pos = new BlockPos(msg.x(), msg.y(), msg.z());
            switch (msg.kind()) {
                case 1 -> CompassNode.orig = pos;
                case 2 -> CompassColony.orig = pos;
                case 3 -> CompassOrigin.orig = pos;
                default -> { }
            }
        });
    }
}
