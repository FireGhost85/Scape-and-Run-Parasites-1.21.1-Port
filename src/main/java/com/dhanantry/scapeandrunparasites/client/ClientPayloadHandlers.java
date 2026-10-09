package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.EvoPhaseCancelPayload;
import com.dhanantry.scapeandrunparasites.network.MovingSoundPayload;
import com.dhanantry.scapeandrunparasites.network.UpdateEvoPhasePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Client handlers for the SRP payloads (ClientProxy.playMovingSound and the packet handlers of 1.10.9). */
public final class ClientPayloadHandlers {
    private ClientPayloadHandlers() {}

    public static void movingSound(MovingSoundPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> playMovingSound(msg.evPhase(), msg.volume()));
    }

    public static void escapeOffer(com.dhanantry.scapeandrunparasites.network.EscapeOfferPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            EscapeClientState.OFFER = msg.offer();
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof net.minecraft.client.gui.screens.DeathScreen) {
                mc.screen.resize(mc, mc.screen.width, mc.screen.height);
            }
        });
    }

    public static void bestiarySync(com.dhanantry.scapeandrunparasites.network.BestiarySyncPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> com.dhanantry.scapeandrunparasites.client.BestiaryClient.applySync(msg.progress()));
    }

    public static void celestialState(com.dhanantry.scapeandrunparasites.network.CelestialNightStatePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> com.dhanantry.scapeandrunparasites.client.celestial.CelestialPhaseClient.setServerNightState(msg.dim(), msg.phase(), msg.nightIndex(), msg.activeSet(), msg.forcedSet()));
    }

    public static void progressSnapshot(com.dhanantry.scapeandrunparasites.network.SyncProgressSnapshotPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> com.dhanantry.scapeandrunparasites.bestiary.client.gui.CurrentProgressClientCache.read(msg.tag()));
    }

    public static void fog(com.dhanantry.scapeandrunparasites.network.FogPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            SRPClientState.fog = msg.fog();
            SRPClientState.fogRed = msg.red();
            SRPClientState.fogGreen = msg.green();
            SRPClientState.fogBlue = msg.blue();
        });
    }

    public static void evoPhaseCancel(EvoPhaseCancelPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            stopMusic();
            SRPClientState.resetSoundTicker(200);
            SRPClientState.clientCurrentEvoPhase = (byte) msg.phase();
        });
    }

    public static void updateEvoPhase(UpdateEvoPhasePayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            int prev = SRPClientState.clientVector;
            if (msg.vector()) {
                if (prev <= 0) stopMusic();
                SRPClientState.clientVector = 200;
            }
            SRPClientState.clientCurrentEvoPhase = (byte) msg.phase();
        });
    }

    static void stopMusic() {
        Minecraft.getInstance().getSoundManager().stop(null, SoundSource.MUSIC);
    }

    private static void play(SoundEvent sound, float pitch, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, pitch, volume));
    }

    public static void playMovingSound(int sound, float v) {
        RandomSource rand = RandomSource.create();
        if (sound == -1) {
            play(SRPSounds.BHEART.get(), 1.0f, v * (float) SRPConfigWorld.biomeHeartVol);
        } else if (sound >= 1 && sound <= 10) {
            SoundEvent[] phase = {SRPSounds.EVPHASE_1.get(), SRPSounds.EVPHASE_2.get(), SRPSounds.EVPHASE_3.get(), SRPSounds.EVPHASE_4.get(), SRPSounds.EVPHASE_5.get(),
                    SRPSounds.EVPHASE_6.get(), SRPSounds.EVPHASE_7.get(), SRPSounds.EVPHASE_8.get(), SRPSounds.EVPHASE_9.get(), SRPSounds.EVPHASE_10.get()};
            play(phase[sound - 1], 1.0f, 0.75f);
        } else if (sound == 100) {
            play(SRPSounds.NODE_1.get(), 1.0f, 0.75f);
        } else if (sound == 101) {
            play(SRPSounds.COLONY_1.get(), 1.0f, 0.75f);
        } else if (sound == 102) {
            if (SRPClientState.clientScent <= 0) {
                stopMusic();
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forMusic(SRPSounds.SCENT_M.get()));
            }
            SRPClientState.musicTimer = 5000;
            SRPClientState.clientScent = 5000;
        } else if (sound == 103) {
            SRPClientState.musicTimer = 1000;
            SRPClientState.clientScent = 140;
        } else if (sound == 104) {
            SRPClientState.musicTimer = 1000;
            stopMusic();
        } else if (sound >= 200 && sound <= 229) {
            play(disloSound(sound - 200), 1.0f, 0.45f);
        } else if (sound == 400) {
            play(SRPSounds.ORIGINNEW.get(), (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f, 0.75f);
        } else if (sound == 401) {
            play(SRPSounds.ORIGINOUTBREAK.get(), (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f, 0.75f);
        } else if (sound == 402) {
            play(SRPSounds.ORIGINDELETED.get(), (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f, 0.75f);
        }
    }

    private static SoundEvent disloSound(int n) {
        return switch (n) {
            case 0 -> SRPSounds.DISLO_0.get();
            case 1 -> SRPSounds.DISLO_1.get();
            case 2 -> SRPSounds.DISLO_2.get();
            case 3 -> SRPSounds.DISLO_3.get();
            case 4 -> SRPSounds.DISLO_4.get();
            case 5 -> SRPSounds.DISLO_5.get();
            case 6 -> SRPSounds.DISLO_6.get();
            case 7 -> SRPSounds.DISLO_7.get();
            case 8 -> SRPSounds.DISLO_8.get();
            case 9 -> SRPSounds.DISLO_9.get();
            case 10 -> SRPSounds.DISLO_10.get();
            case 11 -> SRPSounds.DISLO_11.get();
            case 12 -> SRPSounds.DISLO_12.get();
            case 13 -> SRPSounds.DISLO_13.get();
            case 14 -> SRPSounds.DISLO_14.get();
            case 15 -> SRPSounds.DISLO_15.get();
            case 16 -> SRPSounds.DISLO_16.get();
            case 17 -> SRPSounds.DISLO_17.get();
            case 18 -> SRPSounds.DISLO_18.get();
            case 19 -> SRPSounds.DISLO_19.get();
            case 20 -> SRPSounds.DISLO_20.get();
            case 21 -> SRPSounds.DISLO_21.get();
            case 22 -> SRPSounds.DISLO_22.get();
            case 23 -> SRPSounds.DISLO_23.get();
            case 24 -> SRPSounds.DISLO_24.get();
            case 25 -> SRPSounds.DISLO_25.get();
            case 26 -> SRPSounds.DISLO_26.get();
            case 27 -> SRPSounds.DISLO_27.get();
            case 28 -> SRPSounds.DISLO_28.get();
            case 29 -> SRPSounds.DISLO_29.get();
            default -> SRPSounds.DISLO_0.get();
        };
    }
}
