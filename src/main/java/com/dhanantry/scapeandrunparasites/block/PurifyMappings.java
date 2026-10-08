package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLPaths;

/**
 * Which block each SRP block turns into when the infestation purifier cleans it: the exact pairs of the mapping file
 * ({@code config/srparasites_purify_mappings.txt}, created with the defaults on first use) first, then name heuristics.
 * Block ids of the file and of the heuristics may be 1.12 ids (resolved by {@link BlockIds}).
 */
public final class PurifyMappings {
    private static boolean loaded = false;
    private static final Map<String, String> exact = new HashMap<>();

    private PurifyMappings() {
    }

    public static void ensureLoaded(Level level, String fileName) {
        if (loaded) {
            return;
        }
        loaded = true;
        Path cfgDir = FMLPaths.CONFIGDIR.get();
        Path mappingFile = cfgDir.resolve(fileName);
        try {
            Files.createDirectories(cfgDir);
            if (!Files.exists(mappingFile)) {
                writeDefault(mappingFile);
            }
            try (InputStream in = Files.newInputStream(mappingFile)) {
                readMappings(in);
            }
        } catch (IOException e) {
            ScapeAndRunParasites.LOGGER.warn("[PurifyMappings] could not read {}", mappingFile, e);
        }
    }

    public static boolean isSrp(BlockState st) {
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(st.getBlock());
        String path = rl.getPath().toLowerCase(Locale.ROOT);
        if (!ScapeAndRunParasites.MODID.equals(rl.getNamespace())) {
            return false;
        }
        if (path.equals("infestremain") || path.contains("infestation_purifier")) {
            return false;
        }
        return path.contains("inf") || path.contains("infect") || path.contains("parasite");
    }

    @Nullable
    public static BlockState mapToVanillaState(BlockState srpState) {
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(srpState.getBlock());
        String toId = exact.get(rl.toString());
        if (toId != null) {
            return def(toId);
        }
        String path = rl.getPath();
        if (containsAny(path, "glass_pane")) {
            return def("minecraft:glass_pane");
        }
        if (containsAny(path, "glass")) {
            return def("minecraft:glass");
        }
        if (containsAny(path, "stone_brick_wall", "stonebrick_wall", "brick_wall", "rubble_wall")) {
            return def("minecraft:cobblestone_wall");
        }
        if (containsAny(path, "fence")) {
            return def("minecraft:fence");
        }
        if (containsAny(path, "stone_bricks_stairs", "stonebrick_stairs")) {
            return def("minecraft:stone_brick_stairs");
        }
        if (containsAny(path, "sandstone_stairs")) {
            return def("minecraft:sandstone_stairs");
        }
        if (containsAny(path, "plank_stairs", "planks_stairs", "wood_stairs")) {
            return def("minecraft:oak_stairs");
        }
        if (containsAny(path, "ss_chiseled", "chiseled_sandstone")) {
            return def("minecraft:sandstone");
        }
        if (containsAny(path, "inf_ss", "sandstone")) {
            return def("minecraft:sandstone");
        }
        if (containsAny(path, "infestedsand", "red_sand", "sand_red", "sand")) {
            return def("minecraft:sand");
        }
        if (containsAny(path, "pot")) {
            return def("minecraft:flower_pot");
        }
        if (containsAny(path, "column", "pillar", "log_axis", "axis")) {
            return def("minecraft:log");
        }
        if (containsAny(path, "trunk", "bark", "stem", "wood")) {
            return def("minecraft:log");
        }
        if (containsAny(path, "terracotta", "hardened_clay", "stained_clay")) {
            return def("minecraft:stained_hardened_clay");
        }
        if (containsAny(path, "stone_polished", "polished")) {
            return def("minecraft:stone");
        }
        if (containsAny(path, "stone_bricks", "stonebrick")) {
            return def("minecraft:stonebrick");
        }
        if (containsAny(path, "planks", "wood_planks")) {
            return def("minecraft:planks");
        }
        if (containsAny(path, "cobblestone")) {
            return def("minecraft:cobblestone");
        }
        if (containsAny(path, "rubble", "andesite", "diorite", "granite", "stone")) {
            return def("minecraft:stone");
        }
        if (containsAny(path, "leaves")) {
            return def("minecraft:leaves");
        }
        if (containsAny(path, "stain", "dirt", "grass")) {
            return def("minecraft:dirt");
        }
        return null;
    }

    private static boolean containsAny(String s, String... keys) {
        s = s.toLowerCase(Locale.ROOT);
        for (String k : keys) {
            if (s.contains(k)) {
                return true;
            }
        }
        return false;
    }

    /** The default state of the block of a (possibly 1.12) id. */
    @Nullable
    private static BlockState def(String id) {
        Block b = BlockIds.parseBlock(id);
        return b == null ? null : b.defaultBlockState();
    }

    @Nullable
    private static ResourceLocation id(String full) {
        String t = full.trim();
        if (!t.contains(":")) {
            return null;
        }
        String[] p = t.split(":");
        if (p.length != 2) {
            return null;
        }
        return ResourceLocation.tryBuild(p[0], p[1]);
    }

    private static void readMappings(InputStream in) {
        try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                String s = line.trim();
                String[] parts;
                if (s.isEmpty() || s.startsWith("#") || (parts = s.split("\\s*-\\s*")).length != 2) {
                    continue;
                }
                ResourceLocation from = id(parts[0]);
                ResourceLocation to = id(parts[1]);
                if (from == null || to == null) {
                    continue;
                }
                if (BuiltInRegistries.BLOCK.getOptional(from).isEmpty() || BlockIds.parseBlock(parts[1].trim()) == null) {
                    continue;
                }
                exact.put(from.toString(), to.toString());
            }
        } catch (Exception ignored) {
        }
    }

    private static void writeDefault(Path f) {
        List<String> defaults = new ArrayList<>();
        defaults.add("# SRP -> Vanilla mappings (exact); heuristics handle the rest");
        defaults.add("srparasites:infestedstain - minecraft:dirt");
        defaults.add("srparasites:infested_leaves - minecraft:oak_leaves");
        defaults.add("srparasites:infestedrubble - minecraft:stone");
        defaults.add("srparasites:infested_cobblestone - minecraft:cobblestone");
        defaults.add("srparasites:infested_planks - minecraft:oak_planks");
        defaults.add("srparasites:infested_plank_stairs - minecraft:oak_stairs");
        defaults.add("srparasites:infested_stone_bricks - minecraft:stone_bricks");
        defaults.add("srparasites:infested_stone_polished - minecraft:stone");
        defaults.add("srparasites:infested_terracotta - minecraft:white_terracotta");
        defaults.add("srparasites:infested_column - minecraft:oak_log");
        defaults.add("srparasites:infested_pot - minecraft:flower_pot");
        defaults.add("srparasites:infestedsand - minecraft:sand");
        defaults.add("srparasites:inf_ss - minecraft:sandstone");
        defaults.add("srparasites:inf_ss_chiseled - minecraft:sandstone");
        defaults.add("srparasites:infested_sandstone_stairs - minecraft:sandstone_stairs");
        defaults.add("srparasites:infested_stone_bricks_stairs - minecraft:stone_brick_stairs");
        defaults.add("srparasites:infested_fence - minecraft:oak_fence");
        defaults.add("srparasites:infested_stone_brick_wall - minecraft:cobblestone_wall");
        defaults.add("srparasites:infested_glass - minecraft:glass");
        defaults.add("srparasites:infested_glass_pane - minecraft:glass_pane");
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(f, StandardCharsets.UTF_8))) {
            for (String d : defaults) {
                pw.println(d);
            }
        } catch (IOException ignored) {
        }
    }
}
