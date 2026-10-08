# effects engineer notes

Scope: mob effects (`potion/`, `init/SRPPotions`), particles (`client/particle/*`, `init/SRPParticles`), fx (`client/fx/*`) and the effect/particle/fx payloads.
Package root: `com.dhanantry.scapeandrunparasites` (shortened to `.` below).

Compile (`compile_agent.sh effects '/potion/|/particle/|/fx/|SRPPotions|SRPParticles|ParticlePayload'`): 40 errors, all unresolved classes/methods owned by other engineers (list under "External dependencies"). Nothing else is reported for my files. `grep TODO|FIXME|UnsupportedOperationException` over my files: no hits.

## Wiring for the lead

1. Mod constructor: `SRPPotions.register(modBus)` and `SRPParticles.register(modBus)`.
2. In `SRPNetwork.register(RegisterPayloadHandlersEvent)`: `EffectsPayloads.register(registrar)` (`.network.registration.EffectsPayloads`). Registers 4 play-to-client payloads: `ParticlePayload`, `PureParticlesPayload`, `VengeanceFxPayload`, `ExtremeSnowPayload`.
3. Everything else registers itself: `Recipe` (`RegisterBrewingRecipesEvent`, game bus), `DistortedEnlightenmentEvents` (game bus), `SRPParticleProviders` (client, mod bus), `ClientExtremeSnow` and `EffectsClientEvents` (client, game bus).
4. Replacing the old senders (done by the owners of those files):
   - `SRPMain.network.sendToAll(new SRPPacketParticle(x,y,z,w,h,type))` -> `PacketDistributor.sendToAllPlayers(new ParticlePayload(x,y,z,w,h,type))` (an `int` type overload exists) or `ParticlePayload.at(entity, type)`.
   - `MsgSpawnPureParticles` -> `new PureParticlesPayload(x,y,z,count,kind)` with `PacketDistributor.sendToPlayersNear(level, null, x,y,z, 64, payload)`.
   - `PacketVengeanceFX(type, Vec3d, a, count)` -> `new VengeanceFxPayload(int type, Vec3 pos, float a, int count)` (constants `SPARKS`, `IMPACT_DUST`, `HEAVY_BLEED`, `LIGHTNING_EXPL` kept).
   - `ExtremeSnowNetwork.broadcast(ServerLevel, boolean, float, boolean, float, float)` keeps its name (used by `CommandExtremeSnow`); `ExtremeSnowNetwork.init()` is gone.
5. Lang keys the lang owner needs (1.12 -> 1.21): `mob_effect.srparasites:<n>` -> `effect.srparasites.<n>`. Potion item names are `item.minecraft.potion.effect.srparasites:<n>`, `item.minecraft.splash_potion.effect.srparasites:<n>`, `item.minecraft.lingering_potion.effect.srparasites:<n>`, `item.minecraft.tipped_arrow.effect.srparasites:<n>`, where `<n>` is the Potion name (the 1.12 PotionType name), e.g. `srparasites:antimall` for the RES_P potion registered as `res`.

## Files ported (original -> new)

potion (16 + 1):
- SRPEffectBase -> `potion/SRPEffectBase` (MobEffect; ctor `(name, bad, color)`)
- PotionBleed, PotionCOTH, PotionContamination, PotionCorrosion, PotionDistortedEnlightenment, EffectDodSmokeTrail, PotionFoster, PotionNeedler, PotionOverheat, PotionPrey, PotionSpotted, PotionTheSign, PotionThornshadeThorns -> same names in `potion/`
- DistortedEnlightenmentEvents -> same (LivingIncomingDamageEvent, MobEffectEvent.Remove/Expired)
- Recipe -> `potion/Recipe` (RegisterBrewingRecipesEvent, five IBrewingRecipe classes; the two water/awkward recipes share a base class)
- init/SRPPotions -> `init/SRPPotions`: 37 `*_E` effect holders, 33 `*_P` potion holders (names and registry names unchanged, including `RES_P` = `res`, `CORRO_P` = `corro`, `VIRA_P` = `vira`), `applyStackPotion(Holder<MobEffect>, LivingEntity, int, int)`, `applySense`, helper `effect(holder, duration, amp)`.

client/particle (18 original files -> 24 files):
- ParticleBiomass, ParticleBlood, ParticleCoolerFog, ParticleDot, ParticleEen, ParticleFlash, ParticleFog, ParticleMultipleGore, ParticleRGBSmoke, ParticleRHappy, ParticleRage, ParticleSpore, ParticleWind: same names, each with a nested `Provider(SpriteSet)`.
- ParticlePure, ParticleKirinWarning: same names, created directly (no particle type).
- ParticleSpawner, SRPEnumParticle, SRPParticleRegistry: same names.
- New: `SRPParticleOptions`, `SRPParticleType` (the ParticleType/options of all 13 enum particles), `SRPParticleProviders` (registration), `LegacyParticle` (shared base), `ParticleSprites` (atlas lookup), `ClientParticleHooks` (package private).
- `init/SRPParticles`: 13 types (fog, spore, gcloud, gsplash, rhappy, biomass, een, flash, dot, wind, coolerfog, rage, blood).

client/fx (5): ClientExtremeSnow, ClientSRPParticles, ParticleBlizzard, ParticleInfestedLeaf, ParticleVengeance -> same names.
client: `SRPClientParticles` (kept, only `spawnKirinWarning`), new `EffectsClientHandlers`, `EffectsClientEvents`.
network: SRPPacketParticle -> `ParticlePayload`; MsgSpawnPureParticles -> `PureParticlesPayload`; PacketVengeanceFX -> `VengeanceFxPayload`; ExtremeSnowNetwork.S2CExtremeSnow -> `ExtremeSnowPayload` + `ExtremeSnowNetwork.broadcast`; new `network/registration/EffectsPayloads`.

Resources: `assets/srparasites/particles/*.json` (13), `textures/particle/**` (92 files, all of the original folder except `upd_particles(unused)`), `textures/mob_effect/<name>.png` (37, copied from `textures/gui/potion_<name>.png`; `potion_cysticercosis.png` and `potion_pitted.png` have no effect and were not copied).

## [CHG] deviations

Effects
- [CHG] Icons: the custom `renderInventoryEffect`/`renderHUDEffect` draws are replaced by the standard sprite `textures/mob_effect/<name>.png`. `PotionTheSign`/`PotionThornshadeThorns` are kept as classes although the 1.10.9 registry uses plain `SRPEffectBase` for those two.
- [CHG] `new PotionEffect(e, d, a, false, false)` (no particles, icon visible in 1.12) -> `SRPPotions.effect(holder, d, a)` = `new MobEffectInstance(holder, d, a, false, false, true)`.
- [CHG] `getCurativeItems()` returning an empty list -> `fillEffectCures` adds only `PROTECTED_BY_TOTEM`.
- [CHG] `LivingHurtEvent` (fires before armor in 1.12) -> `LivingIncomingDamageEvent` with `getAmount/setAmount`.
- [CHG] `removeAttributesModifiersFromEntity` override of Distorted Enlightenment (clears the glowing flag) -> `MobEffectEvent.Remove` and `Expired` handlers in `DistortedEnlightenmentEvents`.
- [CHG] Rage/vomit/senses attribute modifiers use fixed ids `srparasites:effect.rage_speed|rage_damage|vomit|senses` instead of `UUID.randomUUID()`; the rage amounts read `SRPConfigSystems.rageSpeed/rageDamage` when the modifier is applied (times amplifier+1, operation multiply total, like 1.12).
- [CHG] Corrosion: `getEquipmentAndArmor()` -> all equipment slots except BODY; `damageItem(n, entity)` -> `hurtAndBreak(n, entity, slot)`.
- [CHG] Needler: `EntityList.getKey` -> entity type registry key; `createExplosion(..., 0, false)` -> `explode(..., 0, ExplosionInteraction.NONE)`.
- [CHG] COTH, Prey, Spotted: `getEntityData()` -> `getPersistentData()`; entity counts over `loadedEntityList` -> `ServerLevel.getEntities(EntityTypeTest.forClass(X), always)`; dimension ids -> `DimKeys.of(level)`.
- [CHG] Recipe: the null checks for the potion types and the vanilla water/awkward types are dropped (holders always exist); the item null checks keep their early return and now log through the mod logger.
- [CHG] Potion brewing names: `new Potion("srparasites:<name>", ...)` keeps the 1.12 PotionType names so the old lang key style survives.

Particles
- [CHG] The 13 `SRPEnumParticle` values are registered `ParticleType`s with one options record (`SRPParticleOptions`: type + r,g,b) and sprite-set providers (`SRPParticleProviders`). `SRPEnumParticle.getParticleType()` and `options(r,g,b)` were added; `ParticleSpawner.spawnParticle` now returns void and still spawns only when the particle setting is "All". Types use `overrideLimiter = true`, so there is no 32 block cut-off (1.12 had none either).
- [CHG] Hard-coded `getAtlasSprite(...)` lookups became particle descriptions (`particles/<name>.json`) and `setFrame(SpriteSet, index, count)`; frame indices are the original texture order. `ParticleCoolerFog` and `ParticleWind` keep the original double age increment (age advances by 2 per tick) and thresholds.
- [CHG] `ParticleFog` and `ParticleRGBSmoke` used frames 7..0 of the vanilla particle sheet via `setParticleTextureIndex`; their descriptions list `minecraft:generic_0..7`. The index of the last frame can be -1 in 1.12 (one unrendered tick); `setFrame` clamps it.
- [CHG] `getBrightnessForRender` -> `getLightColor` (same bit layout); the shared age based glow is `LegacyParticle.glowingLightColor`.
- [CHG] 1.12 rendered the particle angle as `a + (a - prev) * pt` and these particles never set `prev`; `LegacyParticle.setExtrapolatedRoll` reproduces that with `oRoll/roll` (Flash, Rage).
- [CHG] `ParticleRGBSmoke`: `getClosestPlayer(..., false)` (not creative, not spectator) -> `getNearestPlayer(..., EntitySelector.NO_CREATIVE_OR_SPECTATOR)`.
- [CHG] `ParticlePure`, `ParticleInfestedLeaf`, `ParticleBlizzard`, `ParticleKirinWarning` are created directly and added to the `ParticleEngine`, as in 1.10.9, so there are no particle types for them (an earlier idea of `PURE`/`INFESTED_LEAF` types was dropped). Sprites come from the particle atlas by name (`ParticleSprites`); the `TextureStitchEvent` handlers are not needed (the atlas includes `textures/particle/**`).
- [CHG] `ParticleKirinWarning` uses its own `ParticleRenderType` (additive blend, standalone texture `textures/particle/kirin_warning.png`, full bright) and emits both faces instead of disabling culling. Like the original it does not interpolate its position.
- [CHG] `ParticleBlizzard` extended the vanilla snow shovel particle which no longer exists; the relevant logic (generic frames, size ramp `snowDigSize * clamp(32 * age / maxAge)`, 0.99 damping, -0.03 gravity step) is part of the class. The constructor's `particleScale` assignment was dead in 1.12 (overwritten at the first render) and was not ported.
- [CHG] `ClientSRPParticles` keeps `canSpawn/onSpawn/fx`; the per-tick reset moved to `EffectsClientEvents` (client tick, end). `ParticleInfestedLeaf(Level, x, y, z)` takes the common `Level` type (needed by `BlockClientHooks`) and casts to `ClientLevel`.
- [CHG] `ParticleVengeance`: SPELL_MOB -> ENTITY_EFFECT with colour, CRIT_MAGIC -> ENCHANTED_HIT, BLOCK_DUST id 1 -> BLOCK with stone, DRIP_LAVA -> DRIPPING_LAVA, EXPLOSION_HUGE -> EXPLOSION_EMITTER, SMOKE_LARGE -> LARGE_SMOKE.
- [CHG] 1.21 `Particle.move` stops a particle for good once it lands (`stoppedByCollision`); 1.12 let it slide on the ground. Applies to the particles that call `move` (Blood, Fog, Spore, Biomass, MultipleGore, RGBSmoke, Blizzard).

Fx / network
- [CHG] `ClientExtremeSnow`: the exponential GL fog (density sqrt(ln 50)/10, 2 percent visibility at 10 blocks) does not exist; terrain fog (not water/lava/blind) is the linear fog 0..10 with sphere shape through `ViewportEvent.RenderFog` (cancelled to apply). `FogColors` -> `ComputeFogColor`. `canSnowAt(pos, false)` -> `Biome.shouldSnow`. `getPrecipitationHeight` -> `getHeightmapPos(MOTION_BLOCKING, ...)`. `renderGlobal.loadRenderers()` -> `levelRenderer.allChanged()`; `entityRenderer.updateRenderer()` dropped (it only ran an extra renderer tick).
- [CHG] Payload record components are named `particleType` (ParticlePayload) and `fxType` (VengeanceFxPayload): a component named `type` clashes with `CustomPacketPayload.type()`.
- [CHG] `ParticlePayload` handler keeps the switch (1 gore, 2 smoke, 3 happy, 4 green cloud, 5 none, 10 gore burst, 11 large gore burst, 12 rage) and the exact velocity maths.

## [FLAG] ambiguities

- [FLAG] `PotionCOTH` popping sound: the original used `SoundEvents.field_187929_hc`. No mapping for that field is available offline (the MCP csv has no sound fields), so `SoundEvents.ZOMBIE_VILLAGER_CONVERTED` is a guess. Please confirm the vanilla sound.
- [FLAG] Colour channels: `ParticleFog` (FOG, spawned by the stain/rubble/sand blocks with `g` = 200 or 250) and `ParticleFlash` (colour = age * r) store the raw option values as float colours (not divided by 255), exactly like 1.10.9. The vertex colour then overflows its byte exactly as the 1.12 buffer did (the 1.21.1 `BufferBuilder` also packs unmasked), so the visual result should match the original, but it was not looked at in game.
- [FLAG] `BlocksPayloads` (blocks engineer) defines its own `ParticleBurst`/`PureBurst` payloads for `SRPPacketParticle`/`MsgSpawnPureParticles`, which duplicates `ParticlePayload`/`PureParticlesPayload`. Its `ParticleBurst` record also has a component named `type`, which fails to compile (see the CHG about `type()`). The lead should pick one set; mine are already wired by `EffectsPayloads`.
- [FLAG] `ParticleMultipleGore` texture kinds above 4 pick sprite 0 (1.12 left the particle without a sprite); no caller passes them.
- [FLAG] `LegacyParticle.setFrame` clamps the sprite index; it is the only clamp added and only prevents an out-of-range sprite.

## External dependencies (names as called, 1.21 signatures)

- `.entity.ai.misc.EntityParasiteBase`, `EntityPMalleable.increaseAllResistances()`, `EntityPInfected`, `EntityPPrimitive`, `EntityPAdapted`, `EntityPPure` (PotionSpotted, DistortedEnlightenmentEvents, PotionFoster)
- `.entity.monster.derived.EntityHeblu`
- `.entity.EntityParasiticScent`: ctor `(ServerLevel, int, LivingEntity)`, `getTargetToKill()`, `getCanFollow()`, `setScentLife(int)`, `increaseDanger(x, boolean)`, `setScentReaction(x, boolean)`, `setCanFollow(boolean)`
- `.util.ParasiteEventEntity`: `convertEntity(LivingEntity, CompoundTag, boolean, String[])`, `spawnInsider(LivingEntity, Level, CompoundTag)`, `checkName(String, String[], boolean)`, `spawnUnitFromRof(ServerLevel, LivingEntity, BlockPos, String[], int, int)`, `getScentBonus(byte)`, `getScentReactionBonus(byte)`
- `.util.ParasiteEventWorld.setOriginInHealth(Level, BlockPos, int, boolean)`
- `.item.ItemInfestedBonemeal.boneMealEffect(Level, BlockPos, Direction)`
- `.world.SRPWorldData.get(ServerLevel).nearestInfectionValue(BlockPos, boolean)`
- `.world.SRPSaveData` (exists): `get(Level)`, `getCurrentCode(String dim, int)`, `getEvolutionPhase(String dim)`, `setTotalKills(String dim, ..., Level, boolean, int)`; `.util.DimKeys.of(Level)`
- Config: `SRPConfigSystems`, `SRPConfigWorld`, `SRPConfig.stackablePotionsLimit/worldMobCap`.
- Users of my API outside my files (already compile against it): `BlockClientHooks` (`ParticleSpawner`, `SRPEnumParticle`, `ClientSRPParticles.canSpawn/onSpawn/fx`, `ParticleInfestedLeaf(Level,..)`, `SRPParticleRegistry.spawnPureBurst(Level,..)`), `EntityKirin` (`client.SRPClientParticles.spawnKirinWarning(Level, ...)`), `CommandExtremeSnow` (`ExtremeSnowNetwork.broadcast`).

## Open items

- Server to client sync of the extreme snow state for players who join later is not part of this port (it lived in `world/ExtremeSnow*`); only the payload and the client state exist.
- Brewing: `Recipe` registers the five recipes; the potion-to-splash/lingering conversions that vanilla already provides for every potion are additionally registered for Fear exactly like the original.
- No in game testing was possible; sprite lists, frame orders and render types were checked against the originals and the vanilla `particles/poof.json` and `atlases/particles.json` only.
