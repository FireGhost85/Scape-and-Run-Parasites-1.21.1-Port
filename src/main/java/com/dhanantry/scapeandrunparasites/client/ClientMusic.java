package com.dhanantry.scapeandrunparasites.client;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPMusic;
import com.dhanantry.scapeandrunparasites.network.RequestEvoPhasePayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * The phase music of 1.12 (soundThree of SRPEventHandlerBus): once the evolution phase is above 0 (and the player is inside an
 * infestation vector when vectors are active) the music of the phase starts after a random pause. The removal of vanilla
 * streaming music (soundTwo) and the parasite biome music are not ported yet (parasite biomes are deferred).
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class ClientMusic {
    private static int ticks;

    private ClientMusic() {}

    private static Music phaseMusic() {
        return switch (SRPClientState.clientCurrentEvoPhase) {
            case 1 -> SRPMusic.EVPHASE_1_MUSIC;
            case 2 -> SRPMusic.EVPHASE_2_MUSIC;
            case 3 -> SRPMusic.EVPHASE_3_MUSIC;
            case 4 -> SRPMusic.EVPHASE_4_MUSIC;
            case 5 -> SRPMusic.EVPHASE_5_MUSIC;
            case 6 -> SRPMusic.EVPHASE_6_MUSIC;
            case 7 -> SRPMusic.EVPHASE_7_MUSIC;
            case 8 -> SRPMusic.EVPHASE_8_MUSIC;
            case 9 -> SRPMusic.EVPHASE_9_MUSIC;
            case 10 -> SRPMusic.EVPHASE_10_MUSIC;
            default -> null;
        };
    }

    @SubscribeEvent
    static void onTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (!SRPConfig.musicTrue || mc.player == null || mc.level == null || mc.isPaused()) {
            return;
        }
        if (++ticks % 20 == 0) {
            PacketDistributor.sendToServer(new RequestEvoPhasePayload());
        }
        int prev = SRPClientState.clientVector;
        if (SRPClientState.clientVector > -10) {
            --SRPClientState.clientVector;
        }
        if (SRPConfigWorld.originActivated && SRPClientState.clientVector <= 0) {
            if (prev > 0) {
                mc.getSoundManager().stop(null, SoundSource.MUSIC);
            }
            return;
        }
        if (SRPClientState.musicTimer >= 0) {
            --SRPClientState.musicTimer;
        }
        if (SRPClientState.clientScent >= 0) {
            --SRPClientState.clientScent;
        }
        if (SRPClientState.clientScent == -1) {
            mc.getSoundManager().stop(null, SoundSource.MUSIC);
            SRPClientState.clientScent = -10;
        }
        if (SRPClientState.musicTimer <= 0 && SRPClientState.clientCurrentEvoPhase > 0) {
            SRPClientState.resetSoundTicker(0);
            Music music = phaseMusic();
            if (music != null && SRPClientState.clientScent <= 0) {
                mc.getSoundManager().stop(null, SoundSource.MUSIC);
                mc.getSoundManager().play(SimpleSoundInstance.forMusic(music.getEvent().value()));
            }
        } else {
            SRPClientState.resetSoundTicker(0);
        }
    }
}
