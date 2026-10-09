I am porting the Minecraft mod "Scape and Run: Parasites" 1.10.9 (Minecraft 1.12.2 Forge) to Minecraft 1.21.1 NeoForge 21.1.256 (Java 21, Mojang mappings). The goal is a faithful port: same content, numbers, names, config keys, textures, models and behaviour as the original jar.

References, use all three:
1. `H:\Minecraft\CustomMods\Scape and Run Parasites\docs\PROGRESS_AND_REMAINING.md`: everything done so far, everything left, rules, flags, and how to decompile the jar. Read it first, then `docs\HANDOFF.md`, `docs\PORTING_GUIDE.md`, `docs\PORTING_PLAN.md`, `docs\JAR_INVENTORY.md`, `PORTING_NOTES.md` and `porting\notes\*.md`.
2. The project folder to analyze and edit: `H:\Minecraft\CustomMods\Scape and Run Parasites` (ModDevGradle project; sources under `src\main\java\com\dhanantry\scapeandrunparasites`). Check what already exists before writing anything.
3. The original jar, the source of truth: `H:\Minecraft\Mods 1.12.2\SRParasites-1.10.9.jar`. If the jar and any document disagree, the jar wins. Decompile it with CFR 0.152 into a scratch folder outside the project (the md file explains how; ask me before downloading CFR), annotate SRG names with `porting\tools\annotate_names.py` and `porting\spec\srg_names.csv`, and decode missing names from usage. Extract the jar's `assets` and `data` into another scratch folder.

Priorities, in this order:
1. **The parasites are the main priority.** Check which parasites are already imported and import every one that is not, with all of its mechanics. That means all 248 files of the jar's `entity` package: every tier, variant, head, special, Beckon, Dispatcher, Rooter, Dod, Lodo/buglin, Scent, Venkrol and hijacked mob, starting with `EntityParasiteBase`. Port attributes times config multipliers, AI goals, climbing and flying, attacks, projectiles, sounds, drops, COTH conversion, Merge, Hijack, Assimilate, evolution points, dislodgments, adaptation, spawn rules and caps, per-dimension phases, data sync and NBT. Then `SRPEntities`, spawn eggs, and the helpers that finished block and potion code already needs (`ParasiteEventWorld`, the rest of `ParasiteEventEntity`, `SRPAttributes`, `SRPWorldData`, `BeckonBlockInfestation`, and so on).
2. Items, tools, armour and the creative tab (`SRPItems` and the 60 item classes).
3. **Textures and models:** port everything from the jar that is not ported yet: textures, 1385 models, 298 blockstates, 667 item models, entity textures, particles, `sounds.json`, lang files. Convert the 1.12 entity `ModelBase` code to 1.21 models and renderers. Use scripts that read the extracted jar directly.
4. Loot tables, tags, recipes, advancements, then worldgen, biomes, structures, spawner, client (HUD, Bestiary, screens), commands, and the remaining systems listed in the md file.

Rules:
- Keep the `srparasites` namespace, registry names, config keys and defaults. Do not add new parasite types. New vanilla content only as configurable `[ADD]` additions.
- Do not invent mechanics. If the jar is ambiguous, flag it and ask me.
- Every deviation goes in `PORTING_NOTES.md` as `[CHG]`, `[FLAG]` or `[ADD]`. Beckon Stage V stays dormant. Dimension 270 stays an unmapped legacy id.
- Edit the project files yourself and read a file before changing it. Do not stub missing dependencies silently: port them, or write a documented placeholder with a `[CHG]` note.
- Compile with `gradlew compileJava` after each area and fix errors yourself. I run `runClient` and send the log when something loads.
- Keep chat replies brief: what changed and what is left. No large code blocks in chat. Do not commit unless I ask. Ask before any download.

Start by reading the md file, then give me a short plan for the parasite port (tier order, shared base classes, registry) and ask only the questions that block your work.

---
## Status update (latest session, 2026-10-08)

**Working / done:** mod compiles and runs (client + dedicated server); 159 entity types register, tick, render (missing-renderer crash fixed, `RenderNothing` safety net), die and spawn without exceptions; blockstates, item models, lang JSON (31 languages), spawn eggs (config `Vanilla Mob Eggs`), creative tab, effect names and icons, `sounds.json` loads; transparent block models have cutout/translucent render types; secondary handlers ported (thornshade, snow grass, bonemeal, rage sound, bottles, fishing, sign, sound eater, escape feature, debug items); commands `/srpevolution /srpvectors /srpnodes /srpcolonies /srparasites /srpdislodgment /srpgeneration /srpudevelopment /srp_summon_nidus /srphelp`; phase music (client); all network sends go through `network/SRPSend`.
Tools: `porting/tools/gen_blockstates.py`, `gen_lang.py`, `gen_renderers.py`, `datagen/SRPRegistryDump` (`gradlew runData`). Dev: `-PquickPlay=host:port`, server runs in `run-server/`, config in `run/config/srparasites/*.toml`.

**Important gameplay note (not a bug):** the special abilities, sprinting and water leaps of the parasites are gated by the *Generation* of the dimension (0-5, `/srpgeneration`), not by the evolution phase. With the default config specials (`generationSpecialM*`) start at generation 4, sprinting at 2, water leap at 3, faster attacks at 3-5. A fresh world is generation 0, so parasites look slow and without their special attacks even at phase 6. Test with `/srpgeneration setgeneration 4` (or 5). The generation grows with time (`generationTime1..`).

**Fixed this session:** Beckon stages I/III/IV were bright green (glow textures had RGB in transparent pixels; cleaned), enderman parasites ignored all damage, parasite food theft took any item, deprecated `EventBusSubscriber.Bus` arguments removed.

**Open bugs:** Yelloweye (Emana) acid box (`EntityNade`, damage = father attack damage 3.5, applied by `selfExplode`) reportedly does no damage: not diagnosed; enderman parasites reportedly not despawning/hard hitting: not diagnosed; structure NBT walls use the old wall format; `damage` entity not summonable; a few textures missing in the original jar.

**Not done (deferred):** JEI integration, loot tables, infected-player armor layer (SRPLayerBipedArmor/SRPModelBiped), vanilla music suppression and parasite biome music, bestiary and field guide (+ `/srpguide`, `/srpguideclear`, `/srpbestiarystats`), parasite biomes (worldgen) and the Harlequin commands, celestial events and `/srp_celestial` (+ shrimp drops), Star biomes, report screens, fog, mouse reach, three block-entity renderers (relay controller, trophy, distorted sign), held-pearl glow, screen overlays, advancements, shaders, `/srpguidistortion`.
