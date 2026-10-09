package com.dhanantry.scapeandrunparasites.command;

import com.dhanantry.scapeandrunparasites.block.SRPBlockLinks;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.convert.HarlequinBlockConverter;
import com.dhanantry.scapeandrunparasites.world.biome.ParasiteBiomeGeneration;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

/** {@code /harlequin_convert}, {@code /harlequin_here}, {@code /harlequin_scatter} (CommandHarlequin* of 1.10.9). */
public final class HarlequinCommands {
    private HarlequinCommands() {
    }

    private static int intArg(String[] args, int i, int def, int min, int max) {
        if (args.length <= i) {
            return def;
        }
        try {
            return Math.max(min, Math.min(max, Integer.parseInt(args[i])));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    private static int surfaceY(ServerLevel w, int x, int z) {
        return w.getHeight(Heightmap.Types.WORLD_SURFACE, x, z) - 1;
    }

    private static int groundY(ServerLevel w, int x, int z) {
        return w.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) - 1;
    }

    private static long keyXZ(int x, int z) {
        return (long)x << 32 ^ (long)z & 0xFFFFFFFFL;
    }

    // ------------------------------------------------------------------ /harlequin_convert
    public static class Convert extends ArgCommand {
        public Convert() {
            super("harlequin_convert");
        }

        @Override
        protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
            int radius = intArg(args, 0, 16, 1, 256);
            int sampleChance = intArg(args, 1, 1, 1, 128);
            int depth = intArg(args, 2, 32, 1, 256);
            int blotchChance = intArg(args, 3, 18, 1, 256);
            int blotchMinDiam = intArg(args, 4, 3, 1, 64);
            int blotchMaxDiam = intArg(args, 5, 5, 1, 64);
            BlockPos center = BlockPos.containing(src.getPosition());
            RandomSource rand = world.random;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            HarlequinBlockConverter.Config cfg = new HarlequinBlockConverter.Config(SRPBlocks.HarleskinnBlock.get(), SRPBlocks.HarlequinnGrass.get(), SRPBlocks.Alveoli.get(), SRPBlocks.AlveoliGrowth.get(), SRPBlocks.LipomaMass.get(), SRPBlocks.TressesHair.get(), SRPBlocks.HirsuteHair.get());
            int changed = 0;
            Set<Long> blotchXZ = new HashSet<>();
            int blotchesSeeded = 0;
            int blotchCellsMarked = 0;
            int blotchCellsConverted = 0;
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    int x = center.getX() + dx;
                    int z = center.getZ() + dz;
                    boolean seed = blotchChance <= 1 || rand.nextInt(Math.max(2, blotchChance)) == 0;
                    if (!seed) {
                        continue;
                    }
                    int minD = Math.min(blotchMinDiam, blotchMaxDiam);
                    int maxD = Math.max(blotchMinDiam, blotchMaxDiam);
                    int diameter = minD + rand.nextInt(maxD - minD + 1);
                    int r = diameter / 2;
                    boolean anyMarked = false;
                    for (int ox = -r; ox <= r; ++ox) {
                        for (int oz = -r; oz <= r; ++oz) {
                            if (ox * ox + oz * oz > r * r) {
                                continue;
                            }
                            int sx = x + ox;
                            int sz = z + oz;
                            int ySurf = groundY(world, sx, sz);
                            if (ySurf < world.getMinBuildHeight()) {
                                continue;
                            }
                            BlockPos top = new BlockPos(sx, ySurf, sz);
                            BlockState sTop = world.getBlockState(top);
                            Block topB = sTop.getBlock();
                            boolean wouldBecomeHarlequinn = world.canSeeSky(top.above()) && (sTop.is(BlockTags.SAND) || topB == Blocks.SANDSTONE || topB == Blocks.RED_SANDSTONE || topB == Blocks.STONE);
                            if (topB != Blocks.GRASS_BLOCK && !wouldBecomeHarlequinn || !blotchXZ.add(keyXZ(sx, sz))) {
                                continue;
                            }
                            ++blotchCellsMarked;
                            anyMarked = true;
                        }
                    }
                    if (anyMarked) {
                        ++blotchesSeeded;
                    }
                }
            }
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    int x = center.getX() + dx;
                    int z = center.getZ() + dz;
                    int yTop = surfaceY(world, x, z);
                    if (yTop < world.getMinBuildHeight()) {
                        continue;
                    }
                    for (int y = yTop; y >= Math.max(5, yTop - depth); --y) {
                        pos.set(x, y, z);
                        BlockState state = world.getBlockState(pos);
                        Block b = state.getBlock();
                        boolean blotchHere = blotchXZ.contains(keyXZ(x, z));
                        if (!blotchHere && !(b instanceof LeavesBlock) && rand.nextInt(Math.max(1, sampleChance)) != 0) {
                            continue;
                        }
                        LegacyMaterial mat = LegacyMaterial.of(state);
                        if (mat == LegacyMaterial.air || mat == LegacyMaterial.water || mat == LegacyMaterial.lava) {
                            continue;
                        }
                        Block replacedWith = HarlequinBlockConverter.convert(world, pos, state, blotchHere, rand, cfg);
                        if (replacedWith == null) {
                            continue;
                        }
                        BlockPos below = pos.below();
                        if (replacedWith != cfg.ALVEOLI && rand.nextInt(100) < 30 && world.isEmptyBlock(below)) {
                            world.setBlock(below, cfg.LIPOMA.defaultBlockState(), 2);
                        }
                        ++changed;
                        if (blotchHere && b == Blocks.GRASS_BLOCK && replacedWith == cfg.HARLESKINN) {
                            ++blotchCellsConverted;
                        }
                    }
                }
            }
            msg(src, "Harlequin convert: changed ~" + changed + " | blotches: " + blotchesSeeded + " | cells marked: " + blotchCellsMarked + " | cells converted: " + blotchCellsConverted + " | radius " + radius + " | 1-in-" + sampleChance + " | depth " + depth + " | blotchChance 1-in-" + Math.max(1, blotchChance) + " | blotchDiam " + Math.min(blotchMinDiam, blotchMaxDiam) + ".." + Math.max(blotchMinDiam, blotchMaxDiam));
        }
    }

    // ------------------------------------------------------------------ /harlequin_here
    public static class Here extends ArgCommand {
        public Here() {
            super("harlequin_here");
        }

        @Override
        protected void execute(CommandSourceStack src, ServerLevel world, String[] args) {
            if (!(src.getEntity() instanceof ServerPlayer p)) {
                msg(src, "This command needs a player.");
                return;
            }
            int r = args.length >= 1 ? Math.max(0, intArg(args, 0, 1, 0, 32)) : 1;
            int baseX = p.blockPosition().getX() >> 4;
            int baseZ = p.blockPosition().getZ() >> 4;
            List<ChunkAccess> chunks = new ArrayList<>();
            for (int dx = -r; dx <= r; ++dx) {
                for (int dz = -r; dz <= r; ++dz) {
                    ChunkAccess c = world.getChunk(baseX + dx, baseZ + dz);
                    if (ParasiteBiomeGeneration.applyBiome(world, c, SRPBlockLinks.BIOME_HARLEQUIN)) {
                        chunks.add(c);
                    }
                }
            }
            world.getChunkSource().chunkMap.resendBiomesForChunks(chunks);
            msg(src, "Applied Harlequin to " + chunks.size() + " chunk(s) in a " + (2 * r + 1) + "x" + (2 * r + 1) + " square.");
            msg(src, "Server biome at your position now: " + world.getBiome(p.blockPosition()).unwrapKey().map(k -> k.location().toString()).orElse("unknown"));
        }
    }

    // ------------------------------------------------------------------ /harlequin_scatter
    public static class Scatter extends ArgCommand {
        private static final String[] ROCKS = {"harlequin_rock_01", "harlequin_rock_02", "harlequin_rock_03", "harlequin_rock_04", "harlequin_rock_05", "harlequin_rock_06", "harlequin_rock_07"};
        private static final String[] BUSHES = {"harlequin_bush_00", "harlequin_bush_01", "harlequin_bush_02"};
        private static final String[] TREES = {"harlequin_tree_01", "harlequin_tree_02", "harlequin_tree_03", "harlequin_tree_04", "harlequin_tree_05", "harlequin_tree_06", "harlequin_tree_07"};
        private static final String[] RUINS = {"harlequin_ruin_01", "harlequin_ruin_02", "harlequin_ruin_03"};

        public Scatter() {
            super("harlequin_scatter");
        }

        @Override
        protected List<String> words() {
            return List.of("rock", "bush", "tree", "ruin", "both", "all");
        }

        private static boolean isGoodGround(ServerLevel w, BlockPos pos) {
            BlockState s = w.getBlockState(pos);
            Block b = s.getBlock();
            if (s.is(BlockTags.SAND) || b == Blocks.SANDSTONE || b == Blocks.RED_SANDSTONE || b == Blocks.GRASS_BLOCK || b == Blocks.DIRT) {
                return true;
            }
            return b == SRPBlocks.HarlequinnGrass.get() || b == SRPBlocks.HarleskinnBlock.get();
        }

        private static boolean isBadTop(ServerLevel w, BlockPos pos) {
            BlockState s = w.getBlockState(pos);
            return s.is(Blocks.WATER) || s.is(Blocks.LAVA) || s.getBlock() instanceof LeavesBlock || !w.getFluidState(pos).isEmpty();
        }

        private static boolean flatEnough(ServerLevel w, BlockPos center, int radius, int maxDelta) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            for (int dx = -radius; dx <= radius; dx += radius) {
                for (int dz = -radius; dz <= radius; dz += radius) {
                    int y = w.getHeight(Heightmap.Types.WORLD_SURFACE, center.getX() + dx, center.getZ() + dz);
                    min = Math.min(min, y);
                    max = Math.max(max, y);
                }
            }
            return max - min <= maxDelta;
        }

        private static BlockPos surfaceAt(ServerLevel w, int x, int z) {
            return new BlockPos(x, w.getHeight(Heightmap.Types.WORLD_SURFACE, x, z), z);
        }

        private static boolean harlequin(ServerLevel w, BlockPos p) {
            return w.getBiome(p).is(SRPBlockLinks.BIOME_HARLEQUIN);
        }

        private static boolean okSpot(ServerLevel w, BlockPos s) {
            return harlequin(w, s) && isGoodGround(w, s.below()) && !isBadTop(w, s);
        }

        private boolean placeTemplate(ServerLevel w, BlockPos at, String name, RandomSource rand) {
            StructureTemplate tpl = w.getServer().getStructureManager().get(ResourceLocation.fromNamespaceAndPath("srparasites", name)).orElse(null);
            if (tpl == null) {
                return false;
            }
            Rotation rot = Rotation.values()[rand.nextInt(Rotation.values().length)];
            Mirror mir = rand.nextBoolean() ? Mirror.NONE : Mirror.FRONT_BACK;
            StructurePlaceSettings settings = new StructurePlaceSettings().setRotation(rot).setMirror(mir).setIgnoreEntities(true).setKnownShape(true);
            return tpl.placeInWorld(w, at, at, settings, rand, 2);
        }

        private static String pick(String[] arr, RandomSource r) {
            return arr[r.nextInt(arr.length)];
        }

        private int bushes(ServerLevel w, BlockPos surf, int spread, int count, RandomSource rand) {
            int placed = 0;
            for (int k = 0; k < count; ++k) {
                BlockPos s2 = surfaceAt(w, surf.getX() + rand.nextInt(spread * 2 + 1) - spread, surf.getZ() + rand.nextInt(spread * 2 + 1) - spread);
                if (okSpot(w, s2) && flatEnough(w, s2, 3, 3) && this.placeTemplate(w, s2, pick(BUSHES, rand), rand)) {
                    ++placed;
                }
            }
            return placed;
        }

        @Override
        protected void execute(CommandSourceStack src, ServerLevel w, String[] args) {
            if (args.length < 1) {
                msg(src, "/harlequin_scatter <rock|bush|tree|ruin|both|all> [tries=32] [radius=32]");
                return;
            }
            String type = args[0].toLowerCase(Locale.ROOT);
            if (!List.of("rock", "bush", "tree", "ruin", "both", "all").contains(type)) {
                msg(src, "/harlequin_scatter <rock|bush|tree|ruin|both|all> [tries=32] [radius=32]");
                return;
            }
            int tries = intArg(args, 1, 32, 1, 4096);
            int radius = intArg(args, 2, 32, 8, 512);
            if (!(src.getEntity() instanceof ServerPlayer p)) {
                msg(src, "This command needs a player.");
                return;
            }
            RandomSource rand = w.random;
            int placed = 0;
            for (int i = 0; i < tries; ++i) {
                int dx = rand.nextInt(radius * 2 + 1) - radius;
                int dz = rand.nextInt(radius * 2 + 1) - radius;
                BlockPos surf = surfaceAt(w, (int)p.getX() + dx, (int)p.getZ() + dz);
                if (!okSpot(w, surf)) {
                    continue;
                }
                switch (type) {
                    case "rock" -> {
                        if (flatEnough(w, surf, 5, 3) && this.placeTemplate(w, surf.below(rand.nextInt(2)), pick(ROCKS, rand), rand)) {
                            ++placed;
                        }
                    }
                    case "bush" -> placed += this.bushes(w, surf, 3, 1 + rand.nextInt(3), rand);
                    case "tree" -> {
                        if (flatEnough(w, surf, 6, 3) && this.placeTemplate(w, surf, pick(TREES, rand), rand)) {
                            ++placed;
                        }
                    }
                    case "ruin" -> {
                        if (flatEnough(w, surf, 10, 4) && this.placeTemplate(w, surf.below(rand.nextBoolean() ? 1 : 0), pick(RUINS, rand), rand)) {
                            ++placed;
                        }
                    }
                    case "both" -> {
                        if (!flatEnough(w, surf, 5, 3)) {
                            continue;
                        }
                        if (this.placeTemplate(w, surf.below(rand.nextInt(2)), pick(ROCKS, rand), rand)) {
                            ++placed;
                        }
                        placed += this.bushes(w, surf, 3, 1 + rand.nextInt(2), rand);
                    }
                    default -> {
                        if (flatEnough(w, surf, 5, 3) && this.placeTemplate(w, surf.below(rand.nextInt(2)), pick(ROCKS, rand), rand)) {
                            ++placed;
                        }
                        BlockPos tpos = surfaceAt(w, surf.getX() + rand.nextInt(9) - 4, surf.getZ() + rand.nextInt(9) - 4);
                        if (okSpot(w, tpos) && flatEnough(w, tpos, 6, 3) && this.placeTemplate(w, tpos, pick(TREES, rand), rand)) {
                            ++placed;
                        }
                        BlockPos rpos = surfaceAt(w, surf.getX() + rand.nextInt(17) - 8, surf.getZ() + rand.nextInt(17) - 8);
                        if (okSpot(w, rpos) && flatEnough(w, rpos, 10, 4) && this.placeTemplate(w, rpos.below(rand.nextBoolean() ? 1 : 0), pick(RUINS, rand), rand)) {
                            ++placed;
                        }
                        placed += this.bushes(w, surf, 4, 2 + rand.nextInt(3), rand);
                    }
                }
            }
            msg(src, "Harlequin scatter: placed ~" + placed + " (type=" + type + ", tries=" + tries + ", radius=" + radius + ")");
        }
    }
}
