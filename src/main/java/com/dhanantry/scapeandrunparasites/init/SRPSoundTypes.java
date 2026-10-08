package com.dhanantry.scapeandrunparasites.init;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;

/** Block sound types of SRP 1.10.9 (volume, pitch, break, step, place, hit, fall). */
public final class SRPSoundTypes {
    public static final SoundType FLESH = new DeferredSoundType(1.5f, 1.0f, SRPSounds.FLESH_DIG::get, SRPSounds.FLESH_STEP::get, SRPSounds.FLESH_PLACE::get, SRPSounds.FLESH_HIT::get, SRPSounds.FLESH_FALL::get);
    public static final SoundType FLESH_LIGHT = new DeferredSoundType(1.5f, 1.0f, SRPSounds.FLESH_LIGHT_DIG::get, SRPSounds.FLESH_LIGHT_STEP::get, SRPSounds.FLESH_LIGHT_PLACE::get, SRPSounds.FLESH_LIGHT_HIT::get, SRPSounds.FLESH_LIGHT_FALL::get);
    public static final SoundType HEART = new DeferredSoundType(1.5f, 1.0f, SRPSounds.HEART_DIG::get, SRPSounds.FLESH_STEP::get, SRPSounds.FLESH_PLACE::get, SRPSounds.HEART_HIT::get, SRPSounds.FLESH_FALL::get);
    public static final SoundType PURIFIER = new DeferredSoundType(1.0f, 1.0f, SRPSounds.PURIFIER_DIG::get, SRPSounds.PURIFIER_STEP::get, SRPSounds.PURIFIER_PLACE::get, SRPSounds.PURIFIER_HIT::get, SRPSounds.PURIFIER_FALL::get);
    public static final SoundType LURE = new DeferredSoundType(1.0f, 1.0f, SRPSounds.LURE_DIG::get, SRPSounds.LURE_STEP::get, SRPSounds.LURE_PLACE::get, SRPSounds.LURE_HIT::get, SRPSounds.LURE_FALL::get);
    public static final SoundType BECKON = new DeferredSoundType(1.5f, 1.0f, SRPSounds.BECKON_DIG::get, SRPSounds.BECKON_STEP::get, SRPSounds.BECKON_PLACE::get, SRPSounds.BECKON_HIT::get, SRPSounds.BECKON_FALL::get);
    public static final SoundType SOIL = new DeferredSoundType(1.5f, 1.0f, SRPSounds.SOIL_DIG::get, SRPSounds.SOIL_STEP::get, SRPSounds.SOIL_PLACE::get, SRPSounds.SOIL_HIT::get, SRPSounds.SOIL_FALL::get);
    public static final SoundType VOMIT = new DeferredSoundType(2.0f, 1.0f, SRPSounds.VOMIT_DIG::get, SRPSounds.VOMIT_STEP::get, SRPSounds.VOMIT_PLACE::get, SRPSounds.VOMIT_HIT::get, SRPSounds.VOMIT_FALL::get);
    public static final SoundType TUNNEL = new DeferredSoundType(1.5f, 1.0f, SRPSounds.TUNNEL_DIG::get, SRPSounds.FLESH_STEP::get, SRPSounds.FLESH_PLACE::get, SRPSounds.FLESH_HIT::get, SRPSounds.FLESH_FALL::get);

    /**
     * Sound type {@code new SoundType(1.0f, 0.5f, BLOCKINFEST_*, SoundEvents.field_187876_fn)} that the infested sand, infested ore
     * and the stain slabs built inline. The vanilla fall sound is {@code ENTITY_SLIME_JUMP} (see notes: [FLAG]).
     */
    public static final SoundType INFEST = new DeferredSoundType(1.0f, 0.5f, SRPSounds.BLOCKINFEST_BREAK::get, SRPSounds.BLOCKINFEST_STEP::get, SRPSounds.BLOCKINFEST_PLACE::get, SRPSounds.BLOCKINFEST_HIT::get, () -> SoundEvents.SLIME_JUMP);

    private SRPSoundTypes() {
    }
}
