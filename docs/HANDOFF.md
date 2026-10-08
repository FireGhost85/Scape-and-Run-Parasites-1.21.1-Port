# SRP port: handoff (as of 2026-10-08, after the blocks pass)

Project folder (source of truth): `H:\Minecraft\CustomMods\Scape and Run Parasites`
Original mod: `SRParasites-1_10_9.jar` (Scape and Run: Parasites 1.10.9, modid `srparasites`, Forge 1.12.2)

## 1. Goal and rules

Port the mod faithfully to **Minecraft 1.21.1, NeoForge 21.1.256, Java 21, Mojang mappings** (ModDevGradle project, IntelliJ).

- The jar is the source of truth for content, numbers, names, defaults and behaviour. Prompt/wiki are secondary.
- Keep the `srparasites` namespace, registry names, config keys and defaults.
- Do NOT add new parasite types. New vanilla content (mobs, blocks, biomes, dimensions) plugs into existing systems (COTH conversion tables, existing infested blocks, per-dimension phase system) as configurable, documented additions, tagged `[ADD]` in PORTING_NOTES.md.
- Do not invent mechanics. If the jar is ambiguous, flag it and ask.
- Every deviation goes in `PORTING_NOTES.md`: `[CHG]` necessary change, `[FLAG]` needs a decision, `[ADD]` addition.
- Port in dependency order: config and phase core, registries, entities by tier, infection and merge, Beckon/Nexus, Lure/Carcass, worldgen and structures, client, commands, advancements, data, localization.
- Compile regularly and fix errors yourself. The user runs `runClient` and sends logs.
- User preferences: edit the project files directly (don't tell the user to edit code). Don't overwrite things without reading them first. Keep chat replies brief and contain all important information. No large code blocks in chat. The Unity-specific preferences (6000.3, URP, slider rules) do not apply to this Java project.
- Scope: port everything in the jar, including the jar-only systems (celestial events, Star biomes/blizzards/cold villages, meteors, Harlequin, Field Guide, 22 commands). Core first.
- Decided: Beckon Stage V is ported **dormant** (class, model, lang present; not registered, not spawned). Dimension id `270` is kept as an unmapped legacy id in config.

## 2. Where things are

| What | Where |
|---|---|
| Port source | `src\main\java\com\dhanantry\scapeandrunparasites\...` |
| Resources | `src\main\resources\assets\srparasites\...`, `src\main\resources\data\srparasites\...` (not yet populated for most areas) |
| Guide for engineers (conventions, 1.12 to 1.21 cheat sheet, shared APIs) | `docs\PORTING_GUIDE.md` |
| Plan, milestones, open questions | `docs\PORTING_PLAN.md` |
| Jar content inventory | `docs\JAR_INVENTORY.md` |
| Deviation log | `PORTING_NOTES.md` |
| Per-engineer notes | `porting\notes\*.md` (`effects.md`, `blocks.md`) |
| Config spec and generator | `porting\spec\config_spec.json`, `porting\tools\gen_config.py` |
| MCP 1.12 names for SRG ids (partial) | `porting\spec\srg_names.csv` (`srg,name`) |
| Other generators | `porting\tools\gen_sounds.py`, `gen_phaseconfig.py`, `annotate_names.py`, `cfgparse*.py`, `inventory.py` |

The original jar must be decompiled into a scratch folder OUTSIDE the project (CFR 0.152, `--jarfilter` on the jar; decompiling a directory failed earlier). The jar is SRG-named (`func_70097_a`, `field_70165_t`). Use `porting\spec\srg_names.csv` to map them to MCP 1.12 names. `porting\tools\annotate_names.py` writes an annotated copy with the names in comments. Names missing from the csv must be decoded from usage.

The sandbox tools from earlier (`compile.sh`, `compile_agent.sh`, the `cp.txt` classpath, `src_named`) do NOT exist on your PC. Compile with Gradle on the PC (`gradlew compileJava`, or `runClient`).

## 3. Done

| Area | Status | Notes |
|---|---|---|
| Scaffolding (M0) | done | Template block, item and tab removed. `ScapeAndRunParasites` main class. `Config.java` registers the 4 COMMON specs. |
| Config (M1) | done | 2,396 entries in 4 TOML files under `config\srparasites\`. Original field names, defaults, comments. The 7 dimension-id lists are `String[]` (legacy numbers `0/-1/1` or resource locations). |
| Sounds | done | `init\SRPSounds.java`: 433 SoundEvents, DeferredRegister, generated. Registered from the main constructor. The `sounds.json` file is not copied yet. |
| Phase and Evolution Points core (M2) | done | `world\SRPSaveData.java` (one instance on overworld storage, one entry per dimension keyed by resource location), `phase\PhaseConfig.java`, `phase\DimKeys.java`, `dislodgment\Dislodgments.java` and `IDislodgmentTarget`, payloads (`MovingSoundPayload`, `EvoPhaseCancelPayload`, `UpdateEvoPhasePayload`), `network\SRPNetwork.java`, `client\SRPClientState.java`, `client\ClientPayloadHandlers.java`, `world\SRPPace.java`. Logic tested in the sandbox: promotion, demotion, dislodgment cooldown, NBT round-trip. |
| Effects, potions, particles (part of M3) | done in code | 37 MobEffects (`potion\`, `init\SRPPotions.java`), 33 brewing potions (`Recipe.java` via `RegisterBrewingRecipesEvent`), 13 particle types (`init\SRPParticles.java`, JSON under `assets\srparasites\particles\`), client fx, `network\registration\EffectsPayloads.java`. Details in `porting\notes\effects.md`. Not compiling until entity classes exist. |
| Blocks, fluid, block entities, menus (M3) | done, compiles | 266 registry entries (`init\SRPBlocks.java`), dead blood fluid (`init\SRPFluids.java`), 12 block entity types (`init\SRPBlockEntities.java`), menus (`init\SRPMenus.java`), `util\BlockIds.java` + `porting\spec\meta_map.json`, `porting\spec\blocks_registry.json`, damage types. All 80 earlier classes checked against the jar. Details: `porting\notes\blocks.md`. Relay scanner is deferred to M6. |

## 4. Not started

- Item registry and item classes (M3). The block items are already registered by `SRPBlocks` (`SRPBlocks.BLOCK_ITEMS`); `SRPItems` holds the rest.
- Entity registry and entity classes (M4), by tier: attributes from `SRPAttributes` times the config multipliers, AI goals, COTH, Merge, Hijack. The entity hierarchy has not been ported. `EntityParasiteBase` is missing; many classes depend on it.
- Reinforcement (Beckons, Dispatcher, Rooter), Lures and Carcass, Relay multiblock, purifiers, tools and armour (M5/M6).
- Spawner, worldgen, biomes, structures (`.nbt` to the 1.21 format), Star and celestial systems (M7).
- Client: model and renderer conversion (`ModelBase` to `LayerDefinition`), HUD, Bestiary, clocks, GUI distortion, shaders and mixins, music (M8). Music uses `SRPClientState`.
- Commands (22), advancements (43), recipes, loot tables, tags (M9).
- Scent, Dislodgments (partly ported: effects and start/mid/end logic), Ubiquitous Development, EIV, Generations, Adaptation (M10).
- New-content integration as `[ADD]` items (M11).
- Lang: 33 `.lang` files to `.json`. Asset flattening: textures `blocks\` to `block\`, `items\` to `item\`, models, blockstates, `sounds.json`. These can be done by a script that reads the extracted jar directly, so nothing needs to be transferred through chat.

## 5. Current state of the tree

- `gradlew compileJava` fails only because of classes of other areas (226 error lines). Blocks have no errors of their own. Missing: `EntityParasiteBase` and the entity classes, `ParasiteEventWorld`, `SRPAttributes`, `SRPItems`, `SRPEntities`, `SRPWorldData`, biome and worldgen classes, `BeckonBlockInfestation`, `ItemModule`; the list per class is in `porting\notes\blocks.md` section 8. Do not stub them silently.
- To see every error (javac stops at 100) and to type-check beyond the first failing class, compile with an init script: `gradlew compileJava -I <script>` where the script adds `-Xmaxerrs 10000 -XDshould-stop.ifError=FLOW` to `JavaCompile`.
- Registrations are wired in the main constructor (`SRPSounds`, `SRPPotions`, `SRPParticles`, `SRPFluids`, `SRPBlocks`, `SRPBlockEntities`, `SRPMenus`) and in `SRPNetwork` (`EffectsPayloads`, `BlocksPayloads`).
- Not generated yet: blockstates and models for 266 blocks, item models for the variant items, loot tables, tags, lang, recipes (data/asset step).

## 6. Flags and changes (also in PORTING_NOTES.md)

Decisions needed from the user:
- `[FLAG]` One global `SRPSaveData` (overworld storage, entries per dimension). The original kept one instance per dimension with stale copies. Confirm this, or ask for per-dimension behaviour back.
- `[FLAG]` The COTH "popping" sound: the original `SoundEvents` field `field_187929_hc` has not been identified. The effects engineer used `ZOMBIE_VILLAGER_CONVERTED` as a guess. Confirm the vanilla sound.
- `[FLAG]` Fog particles use raw colour floats as in 1.12. Check in game.
- `[FLAG]` Difficulty cap: the jar uses integer division (Peaceful and Easy give cap 0, Normal and Hard give cap 10). The brief said multipliers 0.5, 1, 1.5. Ported as the jar does.
- `[FLAG]` The world-creation "pace" choice (0 to 3: x0.5, x1, x3, x10; cooldown ignored and cap bypassed for x10) exists in the jar but not in the brief. Ported.
- `[FLAG]` `initdislo020Config` is never called in the jar. Its 6 entries keep their defaults.
- `[FLAG]` `ancient`, `preeminent` and `derivedOneMindDeathV` have an integer default of 0 with minimum 1. Clamped to 1, as Forge `getInt` does.
- `[FLAG]` Several `SoundEvents` SRG fields have no name in `srg_names.csv` (the COTH one above, `field_187577_bU` gothshroom, `field_187715_dR` and `field_187635_cQ` fog nullifier). The port guesses from context. The MCP 1.12.2 name list (mcp_stable 39) would settle all of them at once; downloading it needs the user's OK.
- `[FLAG]` Parasite fog uses `Properties.air()`; a chunk section with only air and fog is not rendered by 1.21. Check in game if fog is invisible high above the ground.

Changes already made:
- `[CHG]` Original NBT bug: locked parasites wrote `dimensionId` and read `dimId`. One key is used now.
- `[CHG]` Dislodgment 2 end effect ran once per loaded entity in the original. It runs once now.
- `[CHG]` Sound `QUAC_HURT` was assigned twice and `QUAC_DIG` was left null. Both are now registered (`quac.hurt`, `quac.dig`).
- `[CHG]` Dimension lists accept legacy numbers or resource locations. Unmapped numbers (such as 270) are kept as keys.
- `[CHG]` Mob-effect lang keys become `effect.srparasites.<name>` (from `mob_effect.srparasites:<name>`).
- `[CHG]` `LivingHurtEvent` maps to `LivingIncomingDamageEvent`, because 1.12 fired before armour.
- `[CHG]` Extreme-snow exponential fog becomes linear fog from 0 to 10 blocks.
- `[CHG]` `ParticleBlizzard` reimplements the old vanilla snow-shovel logic, since that particle no longer exists.
- `[CHG]` Each per-dimension `DeferredRegister` class exposes `register(IEventBus)`. The lead wires them into the main class.

## 7. Next steps, in order

1. Items: `SRPItems` and the item classes (M3), then the creative tab. Block code already expects `SRPItems.ALVEOLAR_FLUID`, `FOG_BOTTLE`, `DEADBLOOD_FLUID`, `semiorganicingot`, `itemThornshadeBerry`, `falseapple`, `itemlurecomponent6`, `ItemModule`, `ItemInfestedBonemeal`.
2. Entities by tier, starting with `EntityParasiteBase` (M4), then `SRPEntities` (`BUGLIN`, `SCENT`, `BECKON_SI`, `DISPATCHER_SI`), `ParasiteEventWorld`, `ParasiteEventEntity` rest, `SRPAttributes`.
3. Asset and data conversion by script from the extracted jar: textures, blockstates and models (blocks and the variant items `<block>_<variant>`), lang x33, `sounds.json`, loot tables (drop list in `porting\notes\blocks.md` section 6), tags (section 5), recipes, advancements, structures.
4. Compile with `gradlew compileJava` after each area. Run `runClient` and send the log when something loads. Then M5 and later per `PORTING_PLAN.md` (Relay scanner with M6).
