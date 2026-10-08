# SRP 1.10.9 → 1.21.1 NeoForge: porting plan

## 0. Working method
- Jar is the source of truth. Decompiled with CFR 0.152 (`1,094` files). The mod ships **SRG names** (`func_xxx`/`field_xxx`, 2,055 distinct, no MCP mapping available offline); I decode them from usage and the 1.12.2 API.
- No shell on the PC, no Maven access in my workspace. I compile with `javac` against the NeoForge 21.1.256 + Minecraft jars from `build/moddev/artifacts` + your Gradle cache (`./compile.sh`), then copy files into the project. **`runClient` cannot be run by me**; run it in IntelliJ and give me `run/logs/latest.log` (or the crash report), I fix from that.
- Generated code (config, registries, assets) comes from scripts in `porting/tools/` so it can be regenerated.

## 1. Content found in the jar that the brief did not list (jar wins, all will be ported, later milestones)
Celestial night events (`world/celestial`, `/srp forcecelestial`, sky flash), "Star" world system (cold/warm/dynamic star biomes, blizzard / extreme snow + shaders + 3 client mixins, cold villages `dh_village_*`, fractured terrain, cold-star trees), meteors (impact/crash worldgen, `meteor` entity), Harlequin biome + convert/scatter/here commands, Thornshade plant set, Field Guide, Compass Origin/Node/Colony, Fog Bottle, Greek Fire, Book of Vengeance, Beholder Pearl, The Sign charm, Shrimp/Fishlin, 36 mob effects / 33 potion types, 22 commands (not 4), 43 advancement JSONs (not ~50), only 1 loot-table file (drops are Java + config).

## 2. Package layout (`com.dhanantry.scapeandrunparasites`)
Same sub-package names as the original so decompiled logic maps 1:1:
`config` (4 generated classes with the **original static field names**, baked from `ModConfigSpec` on load/reload), `phase` (EvoPhase, EvoPhases, PhaseManager, `EvolutionSavedData` per dimension), `registry` (DeferredRegisters: blocks, items, entities, block entities, effects, sounds, biomes, fluids, creative tabs, menus, recipe types, damage types, attachments), `entity` (+ `ai`, `monster/<tier>`, `projectile`), `block`, `item`, `effect`, `network` (payloads), `client` (renderers, models, HUD, GUI, shaders), `world` (spawner, worldgen, structures, celestial, star), `util`, `compat`, `command`. Mixins in `.mixin` (existing `srparasites.mixins.json`).

## 3. Port order (milestones)
| # | Milestone | Verify |
|---|---|---|
| M0 | Docs, project scaffolding (strip template example), compile harness | javac clean |
| M1 | Config → `ModConfigSpec` (all 2,396 entries, names/categories/defaults/comments kept) | javac + TOML generated |
| M2 | Phase/EP core: `EvoPhase(s)`, per-dimension SavedData, gain/loss rules, sleep EP, lock list, sync payload, `/srp evolution` | unit-style checks vs jar tables |
| M3 | Registries: sounds, mob effects (36), blocks (298 blockstates), items, block entities, creative tab; asset flattening + lang `.json` ×33 | javac, then your `runClient` |
| M4 | Entities by tier (Inborn → … → Nexus), attributes from config, AI goals, COTH, Merge, Hijack | per tier |
| M5 | Reinforcement (Beckon/Dispatcher/Rooter), block infestation + meta→state table | |
| M6 | Lures/Carcass, Relay multiblock, purifiers, tools/armor | |
| M7 | Spawner (custom, mob cap), worldgen, biomes, structures (`.nbt` → 1.21 format), star/celestial | |
| M8 | Client: model/renderer conversion (generated from Tabula-style `ModelBase` code), HUD, Bestiary, clocks, GUI distortion, shaders, music | |
| M9 | Commands, advancements (43), recipes, loot, tags, data | |
| M10 | Scent, Dislodgments, Ubiquitous Development, EIV, Generations, Adaptation | |
| M11 | New-content integration (vanilla 1.13–1.21 mobs/blocks/biomes), listed as additions | |

## 4. Cannot be ported 1:1 – proposed solutions
| Item | Solution |
|---|---|
| Numeric dimension IDs in config (`0`,`-1`,`1`,`270`) | Keep the same strings; resolve `0/-1/1` → overworld/nether/end; also accept `namespace:path` ids. `270` has no vanilla meaning → **question Q2** |
| `id:meta` block strings (`minecraft:stone:1`, revert block `minecraft:gravel:0`) | Meta table → flattened blocks (`stone:1` → `granite`…). Both formats accepted in config |
| Meta variants (Lure ×10, infested blocks, slabs, etc.) | Separate blocks / blockstate properties; revert logic keyed by state |
| Forge `Configuration` (.cfg) | TOML via `ModConfigSpec`; `float` → `double` entries, same keys/categories; `byte[]` lists → int lists |
| Potions/`PotionType` | `MobEffect` + brewing recipes via `RegisterBrewingRecipesEvent` |
| `ModelBase`/`ModelRenderer` models + `setRotationAngles` | Script converts geometry to `LayerDefinition`; animation code ported mechanically (`rotateAngleX`→`xRot`) |
| Entity ids/`EntityEntry` ids | Namespaced registry names kept; numeric IDs only used by the config "parasite ID" lists (kept as-is, mapped by a table) |
| `SRPSaveData` (WorldSavedData) | `SavedData` per dimension + `Codec`; attachments for per-entity adaptation |
| Mixins (3, client) | Rewritten against Mojmap targets (`FogRenderer`, `LevelRenderer`, `PostChain`) |
| Sky/`WorldProvider` hooks | `DimensionSpecialEffects` / `RegisterDimensionSpecialEffectsEvent` |
| Legacy worldgen (`IWorldGenerator`, GenLayer star biomes) | Biome modifiers + features + structure/template pool JSON; star layers → biome source tags/`BiomeModifier` |
| Mod compat (`wyrmsofnyrus`, `srrevenants`) | Kept as string ids in defaults (harmless if mod absent) |

## 5. Open questions (jar is ambiguous / needs your decision)
- **Q1 Beckon Stage V (DECIDED: port dormant):** `EntityVenkrolSV`, `ModelVenkrolSV`, `RenderVenkrolSV`, lang and `VENKROLSV_ID=42` exist, but it is **not registered** in `SRPEntities` (only Stage I–IV), so it is non-functional leftover code.
- **Q2 Dimension 270 (DECIDED: keep as unmapped legacy id):** default phase list has `270;-1;300`. Which dimension was that?
- **Carcass layout (resolved from `BlockEvolutionLure`):** the clicked Lure (centre) plus four Lures at diagonal offsets (±3, ±3); on use the four outer ones are removed, centre is consumed. Same-variant requirement to be confirmed while porting `checkBlocks`.
