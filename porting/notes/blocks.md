# Blocks (M3 part): registry, fluid, block entities, menus, meta table

Status 2026-10-08. All block code compiles; the only errors left in the tree come from classes of other areas (list in section 8).
The original is the annotated CFR output of `SRParasites-1.10.9.jar`; every number below was taken from it.

## 1. Files

New in this pass (original in brackets):

| Port | Original |
|---|---|
| `init/SRPBlocks.java` (registry + block items) | `init/SRPBlocks.java` |
| `init/SRPFluids.java`, `block/BlockFluid.java`, `client/SRPFluidClientExtensions.java` | `init/SRPFluids`, `fluid/DeadBloodFluid`, `block/BlockFluid` |
| `init/SRPBlockEntities.java` (12 types + item handler capabilities) | `GameRegistry.registerTileEntity` calls |
| `init/SRPMenus.java`, `container/ContainerInfuserFurnace`, `container/ContainerParasiteLoot` | `container/*`, `gui/SRPGuiHandler` ids 7 and parasite loot |
| `init/SRPDamageTypes.java` + `data/srparasites/damage_type/*.json` + `data/minecraft/tags/damage_type/bypasses_*.json` | the four `new DamageSource(name)` |
| `util/BlockIds.java`, `porting/tools/gen_meta_map.py`, `porting/spec/meta_map.json`, `src/main/resources/srparasites_meta_map.json` | (new, replaces `id:meta` parsing) |
| `porting/tools/gen_blocks_spec.py`, `porting/spec/blocks_registry.json` | (new, input of the data step) |
| `block/BlockParasiteCactus`, `BlockParasiteCanister`, `BlockParasiteCanisterC`, `entity/tile/TileEntityCanister` | same names |
| `block/BlockParasiteMouth`, `BlockResidue`, `BlockResiduePlants`, `BlockSnowGrass`, `BlockSnowCoveredGrass`, `BlockGothshroom`, `BlockParasiteThin`, `BlockParasiteFog`, `BlockThornshade`, `BlockNodeLamp` | same names |
| `block/slabs/BlockSlabBase`, `BlockSlabRubble`, `BlockSlabStain` | `block/slabs/*` (half and double merged) |
| `block/BlockFogNullifier` + `TileEntityFogNullifier`, `BlockParasiteBarrier` + `TileEntityParasiteBarrier`, `client/BarrierClient` | same names |
| `block/BlockInfestationPurifier` + `TileEntityInfestationPurifier` + `PurifyMappings` | same names |
| `block/BlockInfestedFurnace` + `tileentity/TileEntityInfestedFurnace`, `block/BlockInfuserFurnace` + `tileentity/TileEntityInfuserFurnace`, `recipes/InfuserFurnaceRecipe(s)`, `recipes/SRPInfuserFurnaceRecipeInit` | same names |
| `block/BlockRelay`, `BlockRelayController`, `BlockNodeRelay`, `tileentity/TileEntityNodeRelay`, `TileEntityRelayController`, `client/SRPBlockClientExtensions` | same names |
| `item/ItemBlockWithTooltip`, `util/LangHelper` | same names |

Not ported on purpose: `BlockColonyOutpost` (never instantiated: the outpost is a `BlockColonyCore`), `BlockGlassBase` (never used).
Fixed in existing files: `BlockBiomassBlock` (removed particle `AMBIENT_ENTITY_EFFECT` -> `ENTITY_EFFECT` colour), `BlockTressesHair` and `BlockWebBase` (codec override had weaker access), `BlockParasiteSapling` (`EventHooks.fireBlockGrowFeature(...).isCanceled()`), `BlockAssimilatedReed` (valid bases also coarse dirt, podzol, red sand, which were `dirt:1/2` and `sand:1` in 1.12).

The 80 earlier block classes were checked against the original (constructors, numbers, sounds, references, enum order, drop methods): no behavioural error found beyond the fixes above. Classes read side by side: Alveoli, AlveoliGrowth, AshenGlass, AssimilatedPumpkin, AssimilatedReed, BiomassBlock, BiomeCore, BiomePurifier, BloodyIce, Buglin, BushBase, ColonyCore, ColonyStructure, DeadheadGrassShort/Tall, DeadheadLeaves, DiseasedSponge, EpitomeInfestationWarpDiffuser, Gore, InfestedRemain, InfestedStain, ParasiteSpreading, LeafLike. The remaining classes were checked with a token comparison (numbers, strings, registry/config/sound references) and the enum order of every variant block. Findings are in section 7.

## 2. Registry rules

* Registry names and constant names are the 1.12 ones (`SRPBlocks.InfestedStain`, `SRPBlocks.PARASITE_BARRIER`, ...). Fields are `DeferredBlock<Block>`, or the concrete class where code needs it (variant blocks, panes, doors, pots, `InfestPurify`, `ParasiteCanisterActive`, `DeadBlood`).
* Every block has a `BlockItem` with the block's name except: `deadblood`, `noderelay`, `relay_controller_dummy`, `snow_covered_grass`, `thornshade` (the original had no item for these; the thornshade berry plants the bush).
* Special items: `relay_base/middle/roof/relaycontroller` use `ItemBlockWithTooltip` (lang key `tooltip.<block name>`); `snow_tall_grass/short_grass` use `BlockSnowGrass.ItemSnowGrass` (place sound); `assimilated_pumpkin/jack_o_lantern` use `ItemAssimilatedPumpkin` (wearable); `deadhead_grass_tall` uses `BlockItemNoSurvivalCheck`; doors use `DoubleHighBlockItem` (the original used `ItemDoor`).
* Registration order is the original order, because stairs and walls copy the properties of their model block at registration time.
* 1.12 metadata blocks became a `variant` property. Their items are `<block>_<variant>` (`parasiterubble_bone`, `goresim_small`, `evolutionlure_one`, `parasitebush_tendril`, ...), exactly the old `tile.srparasites.<block>_<variant>.name` keys. The item of the default variant is what `Block.asItem()` returns. Variant blocks: parasiterubble (15), parasitestain (7), parasitetrunk (5), parasiteplank (2), parasiterubbledense (3), parasiteloot (3), parasitebush (5), infestedbush (6), infestedore (8), parasitesapling (6), parasitecanister (4), srpweb (3), evolutionlure (10), goresim/gorepri/goreada/gorepur/gorefer/goremar (3 each), parasiterubbleslabhalf (8), parasitestainslabhalf (7).
* Slabs: half and double block are one block with the vanilla slab type; only the half names exist (`harleskinn_slab`, ..., `parasiterubbleslabhalf`, `parasitestainslabhalf`). The `*_double` names and `parasiterubbleslabdouble/parasitestainslabdouble` are gone.
* Stairs of the parasite blocks keep `<name>stairs` (`parasiterubble_bonestairs`). `infestedrubblestairs`, `infestedstainstairs`, `infestedtrunkstairs` exist only as blockstate files in the jar; they were never registered and are not.
* `SRPBlocks.optionalDirt/optionalRub` are filled in `FMLCommonSetupEvent` from the config strings (fallback gravel / mossy cobblestone).
* Ore dictionary `plankParasiteWood` -> item tag `srparasites:plank_parasite_wood` (data step): `parasiteplank_deadhead`, `parasiteplank_deadheads`, `infested_planks`, `cooked_flesh_planks`, `flesh_planks`, `goth_planks`, `brusewood_planks`, `consumed_planks`.

## 3. Block entities and menus

Registry names are the 1.12 tile entity ids: `node_relay`, `trophy_te`, `dermoid_cyst`, `infested_furnace`, `tileentitycanister`, `tileentitydod`, `parasite_loot`, `relaycontroller`, `infestation_purifier_te`, `parasite_barrier_te`, `fog_nullifier_te`, `infuser_furnace`. Item handler capability (unsided `InvWrapper`, as `TileEntityLockable` gave in 1.12): dermoid cyst, canister, parasite loot, infuser furnace; relay controller exposes its module slot.
Menu types: `infuser_furnace`, `parasite_loot` (screens belong to the client milestone). `container.infuser_furnace` is the container name.

## 4. Meta table

`porting/tools/gen_meta_map.py` writes `meta_map.json` (copy in `src/main/resources/srparasites_meta_map.json`): `renames` (1.12 id to 1.21 id, meta ignored) and `metas` (every meta of 58 vanilla 1.12 blocks to a state string). `BlockIds.parse/tryParse/parseBlock/matches` accept `ns:name:meta` and `ns:name[prop=value]`. All 399 `id:meta` strings of the config defaults resolve. SRP blocks are resolved from their class: variant ordinal (slabs: ordinal = meta & 7, bit 8 = top), infestation stage, slab half, stairs orientation (0 east, 1 west, 2 south, 3 north, bit 4 = upside down), pillar axis. A meta the 1.12 block did not have gives `null` (logged by `parse(String, fallback)`).

## 5. Harvest tools and tags (for the data step)

`porting/spec/blocks_registry.json` lists every block with class, registrar, variants, item flag and the 1.12 `setHarvestLevel` (tool, level) of its class chain: 83 blocks have one (pickaxe 1: 52, shovel 0: 13, axe 1: 11, axe 0: 5, pickaxe 0: 1, pickaxe 3: 1). Tags to generate: `mineable/pickaxe|shovel|axe` and `needs_stone_tool` (level 1), `needs_iron_tool` (2), `needs_diamond_tool` (3). The classes already use `requiresCorrectToolForDrops` for rock and iron like `Material.rock/iron`. Blocks without a harvest tool follow the old tool effectiveness: rock and iron blocks pickaxe, wood/plants/vine axe, nothing else.
Block tags that must exist: `minecraft:wooden_fences` and `minecraft:fences` contain every SRP fence (`*_fence`) and `parasitethin` (1.12 fences connected to any wooden fence); `srparasites:parasites` entity type tag (spawning on SRP blocks); `minecraft:leaves` for `infested_leaves*` and `deadhead_leaves` (decay logic uses `BlockTags.LEAVES`).

## 6. Drops (loot tables, data step)

Default: the block drops itself; a variant block drops its variant item; slabs drop 1 (2 when double) of the slab; doors drop the door item from the lower half; potted flowers and `infested_furnace` drop themselves. Exceptions, from `getItemDropped/quantityDropped/getDrops/canSilkHarvest/isShearable` of the original:

| Block | Drop |
|---|---|
| `deadblood`, `noderelay`, `parasitefog`, `tunnel`, `srpweb`, `gore*`, `thornshade`, `relay_controller_dummy` | nothing (`tunnel` also has no silk touch) |
| `ashen/shrouded/harlequinn/bloody/infested/shade/sepia/moody_glass` and their 8 panes | silk touch only |
| `bloodyice` | silk touch only |
| `deadhead_grass_short`, `deadhead_grass_tall` | silk touch or shears: the block |
| `parasitebush_*`, `infestedbush_*` | shears: the variant item, otherwise nothing |
| `infested_leaves`, `infested_leaves_fast` | shears: the block; otherwise `false_apple` with chance `1 / max(1, 40 - 8*fortune)`; no silk touch |
| `deadhead_leaves` | 1/20 `parasitesapling_deadhead` (fortune ignored); silk touch: the block |
| `snow_covered_grass` | dirt; silk touch: `grass_block` |
| `snow_tall_grass`, `snow_short_grass` | 1/8 chance of a grass seed (vanilla grass drops, fortune); shears or silk touch: the block |
| `residue_plants` | 1-2 `infestremain` (+ uniform bonus with fortune); silk touch: the block |
| `gothshroom` | 1 item, 2 when `group=true` |
| `parasitecanister_sac` | 5% `itemlurecomponent6` (item area); the other three variants drop their variant item |
| `infestremain` | itself only when mined with a shovel in either hand and not in creative (Java `getDrops`) |
| `fog_nullifier` | itself with `UsesRemaining` when uses are left, otherwise nothing (Java `getDrops`) |
| `alveoli`, `sick_alveoli`, `diseased_sponge`, ... | the block (shears give the block as well) |

## 7. Deviations (all also in PORTING_NOTES.md)

See `PORTING_NOTES.md` entries tagged "blocks M3". Short list: slab merge and the double slab drop duplication, variant items, dead blood fluid type, snow grass collision, thornshade snowfall clause, fog uses `air()`, infested furnace uses the vanilla furnace entity, infuser menu quirks, relay scanner deferred, nullifier particles, barrier permission, purifier mapping file, canister creative-only user counting, unidentified SRG sound fields.

## 8. External dependencies of the block code (what is still missing in the tree)

`ParasiteEventWorld` (`setDisloWorldPhase`, `canBiomeStillExist(Type)`, `removeHeartInWorld`, `removeColonyInWorld`, `checkNodeStatus`, `checkColonyStatus`, `getHeartAgePostion`), `ParasiteEventEntity` (`getRandomFeral/Primitive/Adapted/Pure`, `spawnUnitFromRof`, `getRSchance`, `spawnInsider`, `convertEntity`), `SRPAttributes`, `SRPDebugRules`, `SRPResidueFireManager`, `SRPWorldData`, `BiomeParasiteBase`, `util/convert/BeckonBlockInfestation`, `world/gen/feature/WorldGen*` (NodeCore, ColonyB1-4, ColonyBS1-3, Tree, TreeThin, TallFlower, DeadheadTreeStructure), `SRPItems` (`ALVEOLAR_FLUID`, `FOG_BOTTLE`, `DEADBLOOD_FLUID`, `semiorganicingot`, `itemThornshadeBerry`, `falseapple`, `itemlurecomponent6`), `SRPEntities` (`BUGLIN`, `SCENT`, `BECKON_SI`, `DISPATCHER_SI`), the entity classes `EntityParasiteBase`, `EntityLodo`, `EntityDod/SII/SIII/SIV`, `EntityPBeckon`, `EntityVenkrol`, `EntityParasiticScent`, `EntityPStationaryArchitect`, `EntityPDispatcher`, `item/ItemModule`, `ItemInfestedBonemeal`. `TileEntityRelayController` needs `ItemModule` for its slot filter.

## 9. Open items

* Relay scanner (M6): `TileEntityRelayController.performScan`, the report papers, `ScanRegistry`, the scanner menu and screen (`ScannerContainer`, `ScannerGui`) and the opening of the menu from `BlockRelayController` / `BlockNodeRelay` (both return success without opening anything for now).
* Screens for the infuser furnace and the parasite loot menu (client milestone); JEI integration of the infuser recipes.
* Outline-less blocks (`residue_plants`, `snow_*_grass`, `gothshroom`, `alveoli_growth`, `buglin`, `parasitefog`) draw their outline now; 1.12 returned an empty selection box. A `RenderHighlightEvent.Block` filter can restore it.
* `SRPBlocks` item names for creative tab order, blockstates, models, loot tables and tags are not generated yet (asset and data step).
