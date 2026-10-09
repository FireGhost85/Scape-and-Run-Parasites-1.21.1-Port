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

    /** The particle atlas is owned by the particle engine, not by the model manager's atlas set (Minecraft#getTextureAtlas only knows the block, item, ... atlases). */
    private static TextureAtlas atlas() {
        return (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_PARTICLES);
    }

    /** Sprite of textures/particle/&lt;path&gt;.png of this mod. */
    public static TextureAtlasSprite mod(String path) {
        return atlas().getSprite(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, path));
    }

    /** Sprite of textures/particle/&lt;path&gt;.png of the base game. */
    public static TextureAtlasSprite vanilla(String path) {
        return atlas().getSprite(ResourceLocation.withDefaultNamespace(path));
    }
}
