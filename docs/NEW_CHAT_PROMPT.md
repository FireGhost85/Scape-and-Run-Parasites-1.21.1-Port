I'm porting the Minecraft mod "Scape and Run: Parasites" 1.10.9 from Minecraft 1.12.2 Forge to Minecraft 1.21.1 NeoForge 21.1.256 (Java 21, Mojang mappings). The goal is a faithful port: same content, numbers, names, config keys and behaviour as the original jar.

Work from these, in this order:
1. The handoff file: `H:\Minecraft\CustomMods\Scape and Run Parasites\docs\HANDOFF.md`. It has the goal, the rules, what is done, what is not started, the flags I still need answered, and the next steps. Start there.
2. The project (where the mod is, and what you analyze and edit): `H:\Minecraft\CustomMods\Scape and Run Parasites`. Other references in it: `docs\PORTING_GUIDE.md` (conventions and the 1.12 to 1.21 cheat sheet), `docs\PORTING_PLAN.md` (milestones), `docs\JAR_INVENTORY.md` (what the jar contains), `PORTING_NOTES.md` (every deviation so far), `porting\notes\effects.md`.
3. The original jar, attached as `SRParasites-1_10_9.jar`. It is the source of truth for everything: if the jar and any document disagree, the jar wins. Decompile it with CFR 0.152 into a scratch folder OUTSIDE the project. The jar uses SRG names (`func_70097_a`, `field_70165_t`). Map them with `porting\spec\srg_names.csv`, or run `porting\tools\annotate_names.py` to get an annotated copy. For names not in the csv, decode from usage.

Rules:
- Port in this order: remaining blocks and registries, items, entities by tier, infection and merge, Beckon/Nexus, Lure/Carcass, worldgen and structures, client, commands, advancements, data, localization. Finish what is in progress before starting something new.
- Do not add new parasite types. New vanilla mobs, blocks, biomes or dimensions must plug into existing systems and be configurable. Mark them `[ADD]` in `PORTING_NOTES.md`.
- Do not invent mechanics. If the jar is ambiguous, flag it and ask me.
- Every deviation goes in `PORTING_NOTES.md` as `[CHG]`, `[FLAG]` or `[ADD]`. Beckon Stage V stays dormant. Dimension 270 stays as an unmapped legacy id.
- Edit the project files yourself. Don't tell me to edit code. Read a file before changing it, and don't overwrite work without reason. Keep changes small and clear.
- Compile with `gradlew compileJava` in the project after each area, and fix errors yourself. I run `runClient` and will send the log when something loads.
- Keep chat replies brief. State what changed and what is left. No large code blocks in chat.
- Don't commit to git unless I ask.

Start by reading `docs\HANDOFF.md`. Then give me a short plan for the first milestone (finish the blocks area: registry, meta map, fluids, block entities, verification). Ask me only the questions in the handoff's "Flags" section that block your work.
