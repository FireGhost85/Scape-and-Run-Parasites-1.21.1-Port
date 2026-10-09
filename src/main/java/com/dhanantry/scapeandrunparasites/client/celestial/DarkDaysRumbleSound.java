package com.dhanantry.scapeandrunparasites.client.celestial;

import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

/** The looping rumble while Dark Days is active (follows the player, no attenuation). */
public class DarkDaysRumbleSound extends AbstractTickableSoundInstance {
    public DarkDaysRumbleSound() {
        super(SRPSounds.DARK_DAYS_RUMBLE.get(), SoundSource.AMBIENT, RandomSource.create());
        this.looping = true;
        this.delay = 0;
        this.volume = 0.75f;
        this.pitch = 1.0f;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.relative = true;
    }

    @Override
    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !BlackSkyClient.isDarkDaysActive()) {
            this.stop();
            return;
        }
        this.x = mc.player.getX();
        this.y = mc.player.getY();
        this.z = mc.player.getZ();
    }
}
