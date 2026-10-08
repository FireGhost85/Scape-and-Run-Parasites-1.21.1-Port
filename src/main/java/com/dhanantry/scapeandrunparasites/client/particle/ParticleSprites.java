package com.dhanantry.scapeandrunparasites.client.particle;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;

/** Sprites of the particle atlas for the particles that are created directly instead of through a particle description (client only). */
public final class ParticleSprites {
    private ParticleSprites() {
    }

    /** Sprite of textures/particle/&lt;path&gt;.png of this mod. */
    public static TextureAtlasSprite mod(String path) {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_PARTICLES).apply(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, path));
    }

    /** Sprite of textures/particle/&lt;path&gt;.png of the base game. */
    public static TextureAtlasSprite vanilla(String path) {
        return Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_PARTICLES).apply(ResourceLocation.withDefaultNamespace(path));
    }
}
