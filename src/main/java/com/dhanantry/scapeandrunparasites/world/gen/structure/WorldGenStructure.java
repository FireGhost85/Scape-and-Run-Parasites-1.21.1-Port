package com.dhanantry.scapeandrunparasites.world.gen.structure;

import com.dhanantry.scapeandrunparasites.world.gen.WorldGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class WorldGenStructure
extends WorldGenerator {
    public static String structureName;

    public WorldGenStructure(String name) {
        structureName = name;
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos position) {
        WorldGenStructure.generate(worldIn, position);
        return true;
    }

    public static void generate(Level world, BlockPos pos) {
        ResourceLocation location;
        MinecraftServer mcServer = world.getMinecraftServer();
        ServerLevel worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
        TemplateManager manager = worldServer.getStructureTemplateManager();
        Template template = manager.get(mcServer, location = ResourceLocation.fromNamespaceAndPath("srparasites", structureName));
        if (template != null) {
            BlockState state = world.getBlockState(pos);
            world.notifyBlockUpdate(pos, state, state, 3);
            PlacementSettings setting = new PlacementSettings().setMirror(Mirror.NONE).setRotation(Rotation.NONE);
            template.addBlocksToWorldChunk(world, pos, setting);
        }
    }
}

