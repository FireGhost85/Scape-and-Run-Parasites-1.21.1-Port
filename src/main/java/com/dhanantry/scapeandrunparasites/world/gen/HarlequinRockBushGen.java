package com.dhanantry.scapeandrunparasites.world.gen;

import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class HarlequinRockBushGen
implements IWorldGenerator {
    private static final String[] ROCKS = new String[]{"harlequin_rock_01", "harlequin_rock_02", "harlequin_rock_03", "harlequin_rock_04", "harlequin_rock_05", "harlequin_rock_06", "harlequin_rock_07"};
    private static final String[] BUSHES = new String[]{"harlequin_bush_00", "harlequin_bush_01", "harlequin_bush_02"};
    private static final int ROCKS_CHANCE_PER_CHUNK = 10;
    private static final int BUSH_CLUSTER_CHANCE = 8;
    private static final int BUSH_CLUSTER_MIN = 3;
    private static final int BUSH_CLUSTER_MAX = 6;
    private static final int BUSH_CLUSTER_RADIUS = 7;
    private static final int MAX_SLOPE = 3;

    public void generate(RandomSource rand, int chunkX, int chunkZ, Level world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        int bz;
        if (world.isClientSide) {
            return;
        }
        ServerLevel ws = (ServerLevel)world;
        if (DimKeys.of(ws) != 0) {
            return;
        }
        int bx = (chunkX << 4) + 8 + rand.nextInt(6) - 3;
        BlockPos base = BlockPos.containing(bx, ws.getHeight(new BlockPos(bx, 0, bz = (chunkZ << 4) + 8 + rand.nextInt(6) - 3)).getY(), bz);
        if (ws.getBiome(base).value() != SRPBiomes.biomeHarlequin) {
            return;
        }
        if (rand.nextInt(10) == 0) {
            this.tryPlaceTemplateSurface(ws, base, ROCKS[rand.nextInt(ROCKS.length)], rand);
        }
        if (rand.nextInt(8) == 0) {
            int count = 3 + rand.nextInt(4);
            BlockPos anchor = this.findSurface(ws, base);
            if (anchor != null) {
                for (int i = 0; i < count; ++i) {
                    BlockPos around = anchor.offset(rand.nextInt(15) - 7, 0, rand.nextInt(15) - 7);
                    this.tryPlaceTemplateSurface(ws, around, BUSHES[rand.nextInt(BUSHES.length)], rand);
                }
            }
        }
    }

    private void tryPlaceTemplateSurface(ServerLevel ws, BlockPos near, String name, RandomSource rand) {
        BlockPos surface = this.findSurface(ws, near);
        if (surface == null) {
            return;
        }
        if (!this.flatEnough(ws, surface, 7, 3)) {
            return;
        }
        int sink = name.startsWith("harlequin_rock_") ? rand.nextInt(2) : 0;
        BlockPos placeAt = surface.below(sink);
        Rotation rot = Rotation.values()[rand.nextInt(Rotation.values().length)];
        Mirror mir = rand.nextBoolean() ? Mirror.NONE : Mirror.FRONT_BACK;
        Template tpl = this.getTemplate(ws, ResourceLocation.fromNamespaceAndPath("srparasites", name));
        if (tpl == null) {
            return;
        }
        PlacementSettings settings = new PlacementSettings().setRotation(rot).setMirror(mir).setIgnoreEntities(true).setIgnoreStructureBlock(true);
        tpl.addBlocksToWorld((Level)ws, placeAt, settings);
    }

    private BlockPos findSurface(ServerLevel ws, BlockPos xz) {
        int y = ws.getHeight(xz).getY();
        BlockPos pos = BlockPos.containing(xz.getX(), y, xz.getZ());
        Block below = ws.getBlockState(pos.below()).getBlock();
        if (!this.isGoodGround(below)) {
            return null;
        }
        if (this.isBadTop(ws, pos)) {
            return null;
        }
        return pos;
    }

    private boolean isGoodGround(Block b) {
        String id;
        if (b == Blocks.SAND || b == Blocks.SANDSTONE || b == Blocks.RED_SANDSTONE || b == Blocks.GRASS_BLOCK || b == Blocks.DIRT) {
            return true;
        }
        return b.builtInRegistryHolder().key().location() != null && ((id = b.builtInRegistryHolder().key().location().toString()).equals("srparasites:harlequinn_grass") || id.equals("srparasites:harleskinn_block"));
    }

    private boolean isBadTop(ServerLevel ws, BlockPos pos) {
        BlockState state = ws.getBlockState(pos);
        Block block = state.getBlock();
        if (block == Blocks.WATER || block == Blocks.WATER || block == Blocks.LAVA || block == Blocks.LAVA) {
            return true;
        }
        return block.isLeaves(state, (BlockGetter)ws, pos);
    }

    private boolean flatEnough(ServerLevel ws, BlockPos center, int radius, int maxDelta) {
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int dx = -radius; dx <= radius; dx += radius) {
            for (int dz = -radius; dz <= radius; dz += radius) {
                int y = ws.getHeight(center.offset(dx, 0, dz)).getY();
                min = Math.min(min, y);
                max = Math.max(max, y);
            }
        }
        return max - min <= maxDelta;
    }

    private Template getTemplate(ServerLevel ws, ResourceLocation rl) {
        TemplateManager mgr = ws.getSaveHandler().getStructureTemplateManager();
        return mgr.getTemplate(ws.getMinecraftServer(), rl);
    }
}

