package com.dhanantry.scapeandrunparasites.client.weather;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

/** The one-shot sound of the blizzard switching direction, played at the player. */
public class SoundBlizzardReverse extends AbstractTickableSoundInstance {
    private final Player player;

    public SoundBlizzardReverse(SoundEvent sound, Player player) {
        super(sound, SoundSource.WEATHER, RandomSource.create());
        this.player = player;
        this.looping = false;
        this.delay = 0;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.attenuation = SoundInstance.Attenuation.NONE;
        this.updatePosition();
    }

    @Override
    public void tick() {
        if (this.player == null || this.player.isRemoved() || this.player.level() == null) {
            this.stop();
            return;
        }
        this.updatePosition();
    }

    private void updatePosition() {
        this.x = this.player.getX();
        this.y = this.player.getEyeY();
        this.z = this.player.getZ();
    }
}
