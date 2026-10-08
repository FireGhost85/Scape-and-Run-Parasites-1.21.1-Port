"""Generates porting/spec/meta_map.json and src/main/resources/srparasites_meta_map.json: the table that turns 1.12 `id:meta`
block strings (used by the SRP config defaults) into 1.21 block state strings.

usage: python gen_meta_map.py [project_root]

Output format:
  "renames": {legacy id (meta ignored): 1.21 id}          used when a string has no meta or the id has no `metas` entry
  "metas":   {legacy id: {meta: "ns:id[prop=value,...]"}} one entry per meta value of the 1.12 block
SRP blocks are not listed: BlockIds resolves their meta from the block class (variant ordinal, infestation stage,
slab half, stairs orientation, pillar axis).
"""
import json
import os
import sys

root = sys.argv[1] if len(sys.argv) > 1 else os.path.join(os.path.dirname(__file__), "..", "..")

COLORS = ["white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue",
          "brown", "green", "red", "black"]
WOODS = ["oak", "spruce", "birch", "jungle", "acacia", "dark_oak"]
FACING_STAIRS = ["east", "west", "south", "north"]

renames = {}
metas = {}


def mc(name):
    return "minecraft:" + name


def put(legacy, meta, state):
    metas.setdefault(mc(legacy), {})[str(meta)] = state


def rename(old, new):
    renames[mc(old)] = mc(new)


# simple 1:1 renames of the 1.13 flattening that keep one state (meta ignored)
for old, new in {
    "deadbush": "dead_bush", "hardened_clay": "terracotta", "brick_block": "bricks", "nether_brick": "nether_bricks",
    "fence": "oak_fence", "fence_gate": "oak_fence_gate", "stone_stairs": "cobblestone_stairs", "grass": "grass_block",
    "tallgrass": "short_grass", "web": "cobweb", "snow": "snow_block", "snow_layer": "snow", "mossy_cobblestone": "mossy_cobblestone",
    "stonebrick": "stone_bricks", "log": "oak_log", "log2": "acacia_log", "leaves": "oak_leaves", "leaves2": "acacia_leaves",
    "planks": "oak_planks", "sapling": "oak_sapling", "wooden_slab": "oak_slab", "stone_slab": "smooth_stone_slab",
    "stone_slab2": "red_sandstone_slab", "double_stone_slab": "smooth_stone_slab", "double_wooden_slab": "oak_slab",
    "red_flower": "poppy", "yellow_flower": "dandelion", "stained_glass": "white_stained_glass",
    "stained_glass_pane": "white_stained_glass_pane", "stained_hardened_clay": "white_terracotta", "wool": "white_wool",
    "carpet": "white_carpet", "concrete": "white_concrete", "concrete_powder": "white_concrete_powder", "quartz_block": "quartz_block",
    "sandstone": "sandstone", "red_sandstone": "red_sandstone", "sand": "sand", "dirt": "dirt", "stone": "stone",
    "prismarine": "prismarine", "sponge": "sponge", "log_oak": "oak_log", "waterlily": "lily_pad", "reeds": "sugar_cane",
    "mob_spawner": "spawner", "lit_pumpkin": "jack_o_lantern", "pumpkin": "carved_pumpkin", "melon_block": "melon",
    "noteblock": "note_block", "golden_rail": "powered_rail", "sticky_piston": "sticky_piston", "end_bricks": "end_stone_bricks",
    "magma": "magma_block", "red_nether_brick": "red_nether_bricks", "quartz_ore": "nether_quartz_ore", "slime": "slime_block",
    "double_plant": "tall_grass", "cobblestone_wall": "cobblestone_wall", "trapdoor": "oak_trapdoor", "wooden_door": "oak_door",
    "wooden_button": "oak_button", "wooden_pressure_plate": "oak_pressure_plate", "standing_sign": "oak_sign", "ladder": "ladder",
    "bookshelf": "bookshelf", "crafting_table": "crafting_table", "lit_furnace": "furnace", "hay_block": "hay_block",
    "packed_ice": "packed_ice", "ice": "ice", "frosted_ice": "frosted_ice", "snow_block": "snow_block", "clay": "clay",
    "soul_sand": "soul_sand", "glowstone": "glowstone", "redstone_lamp": "redstone_lamp", "end_portal_frame": "end_portal_frame",
    "chorus_flower": "chorus_flower", "chorus_plant": "chorus_plant", "purpur_block": "purpur_block", "purpur_pillar": "purpur_pillar",
    "bone_block": "bone_block", "nether_wart_block": "nether_wart_block", "glass": "glass", "glass_pane": "glass_pane",
}.items():
    rename(old, new)

# stone
for m, s in enumerate(["stone", "granite", "polished_granite", "diorite", "polished_diorite", "andesite", "polished_andesite"]):
    put("stone", m, mc(s))
# dirt
put("dirt", 0, mc("dirt"))
put("dirt", 1, mc("coarse_dirt"))
put("dirt", 2, mc("podzol") + "[snowy=false]")
put("grass", 0, mc("grass_block") + "[snowy=false]")
# tallgrass: 0 dead bush shrub, 1 grass, 2 fern
put("tallgrass", 0, mc("dead_bush"))
put("tallgrass", 1, mc("short_grass"))
put("tallgrass", 2, mc("fern"))
put("deadbush", 0, mc("dead_bush"))
put("sand", 0, mc("sand"))
put("sand", 1, mc("red_sand"))
for legacy, base in (("sandstone", "sandstone"), ("red_sandstone", "red_sandstone")):
    put(legacy, 0, mc(base))
    put(legacy, 1, mc("chiseled_" + base))
    put(legacy, 2, mc("cut_" + base))
for m, w in enumerate(WOODS):
    put("planks", m, mc(w + "_planks"))
    put("sapling", m, mc(w + "_sapling") + "[stage=0]")
    put("sapling", m + 8, mc(w + "_sapling") + "[stage=1]")
# logs: bits 0-1 type, bits 2-3 axis (0 y, 4 x, 8 z, 12 bark = wood block)
AXIS = {0: "y", 1: "x", 2: "z"}
for legacy, types in (("log", ["oak", "spruce", "birch", "jungle"]), ("log2", ["acacia", "dark_oak"])):
    for meta in range(16):
        t = meta & 3
        a = (meta >> 2) & 3
        if t >= len(types):
            continue
        if a == 3:
            put(legacy, meta, mc(types[t] + "_wood") + "[axis=y]")
        else:
            put(legacy, meta, mc(types[t] + "_log") + "[axis=%s]" % AXIS[a])
# leaves: bits 0-1 type, bit 4 = no_decay (persistent), bit 8 = check_decay
for legacy, types in (("leaves", ["oak", "spruce", "birch", "jungle"]), ("leaves2", ["acacia", "dark_oak"])):
    for meta in range(16):
        t = meta & 3
        if t >= len(types):
            continue
        persistent = "true" if meta & 4 else "false"
        put(legacy, meta, mc(types[t] + "_leaves") + "[persistent=%s]" % persistent)
for m, s in enumerate(["stone_bricks", "mossy_stone_bricks", "cracked_stone_bricks", "chiseled_stone_bricks"]):
    put("stonebrick", m, mc(s))
put("prismarine", 0, mc("prismarine"))
put("prismarine", 1, mc("prismarine_bricks"))
put("prismarine", 2, mc("dark_prismarine"))
for m, s in enumerate(["quartz_block", "chiseled_quartz_block"]):
    put("quartz_block", m, mc(s))
put("quartz_block", 2, mc("quartz_pillar") + "[axis=y]")
put("quartz_block", 3, mc("quartz_pillar") + "[axis=x]")
put("quartz_block", 4, mc("quartz_pillar") + "[axis=z]")
put("sponge", 0, mc("sponge"))
put("sponge", 1, mc("wet_sponge"))
for m, s in enumerate(["poppy", "blue_orchid", "allium", "azure_bluet", "red_tulip", "orange_tulip", "white_tulip", "pink_tulip",
                       "oxeye_daisy"]):
    put("red_flower", m, mc(s))
put("yellow_flower", 0, mc("dandelion"))
put("cobblestone_wall", 0, mc("cobblestone_wall"))
put("cobblestone_wall", 1, mc("mossy_cobblestone_wall"))
for legacy, suffix in (("stained_glass", "stained_glass"), ("stained_glass_pane", "stained_glass_pane"),
                       ("stained_hardened_clay", "terracotta"), ("wool", "wool"), ("carpet", "carpet"), ("concrete", "concrete"),
                       ("concrete_powder", "concrete_powder")):
    for m, c in enumerate(COLORS):
        put(legacy, m, mc(c + "_" + suffix))
# 1.12 colour order: 0 white, 1 orange, 2 magenta, 3 light_blue, 4 yellow, 5 lime, 6 pink, 7 gray, 8 silver, 9 cyan, 10 purple,
# 11 blue, 12 brown, 13 green, 14 red, 15 black (COLORS above uses the same order, silver = light_gray)
put("glass", 0, mc("glass"))
put("glass_pane", 0, mc("glass_pane"))
put("hardened_clay", 0, mc("terracotta"))

# vine: 1 south, 2 west, 4 north, 8 east; no side set -> up=true
for meta in range(16):
    props = {"south": bool(meta & 1), "west": bool(meta & 2), "north": bool(meta & 4), "east": bool(meta & 8)}
    props["up"] = not any(props.values())
    put("vine", meta, mc("vine") + "[" + ",".join("%s=%s" % (k, str(v).lower()) for k, v in sorted(props.items())) + "]")

# slabs: bit 3 = upper half
SLAB_VARIANTS = {
    "stone_slab": ["smooth_stone_slab", "sandstone_slab", "oak_slab", "cobblestone_slab", "brick_slab", "stone_brick_slab",
                   "nether_brick_slab", "quartz_slab"],
    "stone_slab2": ["red_sandstone_slab"],
    "purpur_slab": ["purpur_slab"],
    "wooden_slab": [w + "_slab" for w in WOODS],
}
for legacy, variants in SLAB_VARIANTS.items():
    for meta in range(16):
        v = meta & 7
        if v >= len(variants):
            continue
        put(legacy, meta, mc(variants[v]) + "[type=%s]" % ("top" if meta & 8 else "bottom"))

# stairs: bits 0-1 facing (0 east, 1 west, 2 south, 3 north), bit 2 = upside down
STAIRS = {
    "oak_stairs": "oak_stairs", "stone_stairs": "cobblestone_stairs", "brick_stairs": "brick_stairs",
    "stone_brick_stairs": "stone_brick_stairs", "nether_brick_stairs": "nether_brick_stairs", "sandstone_stairs": "sandstone_stairs",
    "spruce_stairs": "spruce_stairs", "birch_stairs": "birch_stairs", "jungle_stairs": "jungle_stairs", "quartz_stairs": "quartz_stairs",
    "acacia_stairs": "acacia_stairs", "dark_oak_stairs": "dark_oak_stairs", "red_sandstone_stairs": "red_sandstone_stairs",
    "purpur_stairs": "purpur_stairs", "prismarine_stairs": "prismarine_stairs", "dark_prismarine_stairs": "dark_prismarine_stairs",
}
for legacy, new in STAIRS.items():
    rename(legacy, new)
    for meta in range(8):
        put(legacy, meta, mc(new) + "[facing=%s,half=%s]" % (FACING_STAIRS[meta & 3], "top" if meta & 4 else "bottom"))

# fence gates: bits 0-1 facing (0 south, 1 west, 2 north, 3 east), bit 2 open, bit 3 powered
GATES = {"fence_gate": "oak_fence_gate", "spruce_fence_gate": "spruce_fence_gate", "birch_fence_gate": "birch_fence_gate",
         "jungle_fence_gate": "jungle_fence_gate", "dark_oak_fence_gate": "dark_oak_fence_gate", "acacia_fence_gate": "acacia_fence_gate"}
GATE_FACING = ["south", "west", "north", "east"]
for legacy, new in GATES.items():
    rename(legacy, new)
    for meta in range(16):
        put(legacy, meta, mc(new) + "[facing=%s,open=%s,powered=%s]" % (GATE_FACING[meta & 3], str(bool(meta & 4)).lower(),
                                                                          str(bool(meta & 8)).lower()))
for legacy, new in (("spruce_fence", "spruce_fence"), ("birch_fence", "birch_fence"), ("jungle_fence", "jungle_fence"),
                    ("dark_oak_fence", "dark_oak_fence"), ("acacia_fence", "acacia_fence"), ("nether_brick_fence", "nether_brick_fence")):
    rename(legacy, new)

for ident in ("air", "cobblestone", "gravel", "clay", "netherrack", "end_stone", "mycelium", "obsidian", "brown_mushroom", "red_mushroom",
              "bedrock", "coal_ore", "iron_ore", "gold_ore", "diamond_ore", "lapis_ore", "redstone_ore", "emerald_ore", "coal_block",
              "iron_block", "gold_block", "diamond_block", "emerald_block", "lapis_block", "redstone_block", "torch", "water", "lava",
              "cactus", "pumpkin", "furnace", "chest", "glowstone"):
    renames.setdefault(mc(ident), mc(ident))
renames[mc("pumpkin")] = mc("pumpkin")
renames[mc("lit_pumpkin")] = mc("jack_o_lantern")

out = {"renames": renames, "metas": metas}
text = json.dumps(out, indent=1, sort_keys=True)
spec = os.path.join(root, "porting", "spec", "meta_map.json")
res = os.path.join(root, "src", "main", "resources", "srparasites_meta_map.json")
for p in (spec, res):
    os.makedirs(os.path.dirname(p), exist_ok=True)
    with open(p, "w", encoding="utf8") as f:
        f.write(text)
print("renames", len(renames), "metas", sum(len(v) for v in metas.values()), "blocks", len(metas))
