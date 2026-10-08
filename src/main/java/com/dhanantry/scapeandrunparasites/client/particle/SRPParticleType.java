package com.dhanantry.scapeandrunparasites.client.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class SRPParticleType extends ParticleType<SRPParticleOptions> {
    private final MapCodec<SRPParticleOptions> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.optionalFieldOf("r", 0).forGetter(SRPParticleOptions::r),
            Codec.INT.optionalFieldOf("g", 0).forGetter(SRPParticleOptions::g),
            Codec.INT.optionalFieldOf("b", 0).forGetter(SRPParticleOptions::b))
            .apply(instance, (r, g, b) -> new SRPParticleOptions(this, r, g, b)));
    private final StreamCodec<RegistryFriendlyByteBuf, SRPParticleOptions> streamCodec = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SRPParticleOptions::r,
            ByteBufCodecs.VAR_INT, SRPParticleOptions::g,
            ByteBufCodecs.VAR_INT, SRPParticleOptions::b,
            (r, g, b) -> new SRPParticleOptions(this, r, g, b));

    /** The 1.12 spawner had no distance or particle-level limiter, so the limiter is overridden. */
    public SRPParticleType() {
        super(true);
    }

    @Override
    public MapCodec<SRPParticleOptions> codec() {
        return this.codec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, SRPParticleOptions> streamCodec() {
        return this.streamCodec;
    }
}
