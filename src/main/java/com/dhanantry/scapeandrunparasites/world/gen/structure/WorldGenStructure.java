package com.dhanantry.scapeandrunparasites.world.gen.structure;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** Places one of the structure templates ({@code data/srparasites/structure/<name>.nbt}, converted from the 1.12 files). */
public class WorldGenStructure
extends WorldGenerator {
    public static String structureName;

    public WorldGenStructure(String name) {
        structureName = name;
    }

    @Override
    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        WorldGenStructure.generate(worldIn, position);
        return true;
    }

    public static void generate(Level world, BlockPos pos) {
        if (!(world instanceof ServerLevel server)) {
            return;
        }
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, structureName);
        StructureTemplate template = server.getServer().getStructureManager().get(location).orElse(null);
        if (template != null) {
            StructurePlaceSettings setting = new StructurePlaceSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE);
            template.placeInWorld(server, pos, pos, setting, server.random, 2);
        } else {
            ScapeAndRunParasites.LOGGER.warn("Missing structure template {}", location);
        }
    }
}
