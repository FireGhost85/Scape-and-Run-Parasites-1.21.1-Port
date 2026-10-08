# SRP port guide (conventions for everyone who writes code in this project)

Project: port of **Scape and Run: Parasites 1.10.9** (modid `srparasites`, 1.12.2 Forge) to **Minecraft 1.21.1, NeoForge 21.1.256, Java 21, Mojang mappings**.
The jar is the source of truth for content, numbers, names, behaviour. Do not invent mechanics, do not add parasite types. Every deviation goes in your notes file (see section 8).

## 1. Where things are
| What | Path |
|---|---|
| Decompiled original (CFR, SRG names `func_/field_`) | `/home/claude/work/src/com/dhanantry/scapeandrunparasites/**` |
| Same, annotated with MCP 1.12 names as comments (`func_70097_a/*attackEntityFrom*/`) — **read this one** | `/home/claude/work/src_named/...` |
| Extracted jar (assets, data, mcmod.info, mixins json) | `/home/claude/work/jar/` |
| MCP name list (partial, ~80% of ids) | `/home/claude/work/mcp/srg_names.csv` (`srg,name`) |
| Port tree (write here) | `/mnt/user-data/outputs/project/src/main/java/com/dhanantry/scapeandrunparasites/**` and `.../src/main/resources/**` |
| Compile check | `/home/claude/work/compile_agent.sh <your-name> [regex-filter]` compiles the whole port tree against NeoForge/MC and prints errors (filter with e.g. `'/entity/|/potion/'`). Empty output = clean. |
| API lookup | `javap -cp "$(cat /home/claude/work/cp.txt)" -p net.minecraft.world.entity.Mob` (never guess an API: check it) |
| Config reference | `/home/claude/work/spec/config_spec.json`, generated classes in `.../config/` |

No Gradle, no game. You cannot run Minecraft. Only the compiler verifies you, so be careful with semantics (event ordering, sided code, data parameters). **Do not commit to the user's PC; the lead does that.** Work only on files in your assignment (other agents are editing other files at the same time). If something you need from another area is missing, code against the name/signature described in section 4 and list it in your notes under "External dependencies"; errors in files you do not own can be ignored.

## 2. Hard rules
1. Package mirror: original `com.dhanantry.scapeandrunparasites.X.Y` becomes the same package and class name unless this guide says otherwise.
2. Keep every number, name, string, registry name, config key, default, spawn weight, colour, sound name. Keep original field/method names where they carry meaning (config fields, `disloNumberX` -> see 4.4).
3. No placeholder logic that silently does nothing. If something cannot be ported yet because a dependency is outside your assignment, call the dependency by its section-4 name. If something is impossible in 1.21, write the closest equivalent and log it in notes with `[CHG]`.
4. Use Mojang mappings (`net.minecraft.world.entity.Mob`, `aiStep`, `hurt`, `addAdditionalSaveData`...).
5. Never use deprecated 1.12-isms: no `SharedMonsterAttributes`, `DataParameter`, `EnumHand`, `Potion`, `IBlockState`, `World` etc.
6. Comments: only short javadoc on non-obvious ported behaviour (cite the original class when the logic moved). No narrating comments.
7. User preferences: variables must not be artificially limited. Config ranges are already generated; do not clamp values the original did not clamp.

## 3. Registries (DeferredRegister) - holders keep the ORIGINAL constant names
| Registry class (package `init`) | Type | Example |
|---|---|---|
| `SRPSounds` (done) | `DeferredHolder<SoundEvent,SoundEvent>` | `SRPSounds.BHEART.get()` |
| `SRPPotions` | `DeferredHolder<MobEffect,MobEffect>` constants `COTH_E`, `RAGE_E`...; brewing potions `COTH_P`... as `DeferredHolder<Potion,Potion>` | `entity.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 2400, 1, false, false))` |
| `SRPBlocks` | `DeferredBlock<Block>` | `SRPBlocks.PARASITEBLOOD` |
| `SRPItems` | `DeferredItem<Item>` | |
| `SRPEntities` | `DeferredHolder<EntityType<?>,EntityType<EntityX>>` | |
| `SRPTileEntities`/block entities, `SRPFluids`, `SRPBiomes`, creative tab, menu types | | |
Constant names = original field names (`SRPBlocks.PARASITEBLOOD`). Registry paths = original registry names (lower case, `srparasites:` namespace).
Mob-effect holders are `Holder<MobEffect>` compatible (`DeferredHolder` is a `Holder`).

## 4. Shared APIs that already exist (use them exactly)
### 4.1 Config (`config.SRPConfig`, `SRPConfigMobs`, `SRPConfigSystems`, `SRPConfigWorld`)
Static fields with the original names and types (`SRPConfigSystems.useEvolution`, `SRPConfigMobs.quacHealthMultiplier`...). Values are baked on config load, so read them at use time, not in static initialisers. Dimension-id lists are `String[]` (legacy numbers `0/-1/1` or resource locations); match them with `DimKeys.matches(list, DimKeys.of(level))`.
### 4.2 Dimensions and phase data
`phase.DimKeys.of(Level)` -> `"minecraft:overworld"`; `DimKeys.normalize(String)`; `DimKeys.level(server, key)`.
`world.SRPSaveData.get(Level)` (server only, null on client) -> methods with original names and `String dim` instead of `int dim`: `getEvolutionPhase(dim)`, `getTotalKills(dim)`, `setTotalKills(dim, points, plus, level, canChangePhase[, isFromSleep], srcId)`, `getCurrentCode(dim, pos)`, `setCurrentCode(...)`, `getGeneration(dim)`, `getGeneModi(dim)`, `getGeneModi2(dim)`, `getDeveLevel()`, `getCooldown(level, dim)`, `checkParasiteID(id)`, `getNumberIDDataSpawn(id)`, `addNumberIDDataSpawn(id)`, `getEIVHealth/Area(dim)`. Read the class for the rest. Original call `SRPSaveData.get(world, 12).getEvolutionPhase(world.provider.getDimension())` becomes `SRPSaveData.get(level).getEvolutionPhase(DimKeys.of(level))`.
`phase.PhaseConfig` has the per-phase lookups (`neededPoints`, `delayTicks`, `vectorPointCap`, `warning`, `originBonusSize/Health`, `disloCooldown`, `disloPhaseCooldown`, `disloMessage`).
### 4.3 Network and client
Payload records + `network.SRPNetwork` registration (add new payloads there: `registrar.playToClient/playToServer(TYPE, CODEC, handler)`; client handlers live in `client.ClientPayloadHandlers`-style classes so servers never load client classes). `ParasiteEventEntity.alertAllPlayerDim(level, message, soundCode)` exists. `client.SRPClientState` holds client phase/music state.
### 4.4 Dislodgments
`dislodgment.IDislodgmentTarget.setDislodgment(int position, int value)` replaces the public `disloNumberX` fields of `EntityParasiteBase`. Boolean positions get 1/0, timed positions (11,15,16,17,19) get ticks/0. `EntityParasiteBase` implements it and keeps the fields as private/protected ints with getters named after the original (`isDisloNumberTwo()` etc.). See `dislodgment/Dislodgments.java`.
### 4.5 Entities (contract for everyone outside the entity-base work)
- `entity.ai.misc.EntityParasiteBase extends net.minecraft.world.entity.monster.Monster implements IDislodgmentTarget`, same helper names as the original where meaningful. Subclass chain as original (`EntityPInfected`, `EntityPFeral`, ...).
- Entity classes keep their original names/packages and expose `public static AttributeSupplier.Builder createAttributes()`; sizes go in the `EntityType.Builder` in `SRPEntities` (generated).
- Capability interfaces `EntityCanClimb`, `EntityCanFly`... keep names.
### 4.6 Misc
`util.ParasiteEventEntity` / `ParasiteEventWorld` keep original static helper names; `SRPPotions.applyStackPotion(effectHolder, living, duration, amp)`.

## 5. Translation cheat sheet (1.12.2 MCP name -> 1.21.1 Mojmap)
Entities: `onLivingUpdate`->`aiStep`, `onUpdate`->`tick`, `attackEntityFrom`->`hurt`, `attackEntityAsMob`->`doHurtTarget`, `applyEntityAttributes`->static `createAttributes`, `entityInit`->`defineSynchedData(SynchedEntityData.Builder)`, `writeEntityToNBT/readEntityFromNBT`->`addAdditionalSaveData/readAdditionalSaveData`, `initEntityAI`->`registerGoals`, `getCanSpawnHere`->spawn placement + `checkSpawnRules`, `canDespawn`->`removeWhenFarAway`, `setDead`->`discard`/`kill`, `onDeath`->`die`, `dropLoot`/`dropFewItems`-> loot table JSON, `getLootTable`->`getLootTable` (data), `isEntityInvulnerable`->`isInvulnerableTo`, `collideWithEntity/applyEntityCollision`->`doPush/push`, `moveEntity/move(MoverType)`->`move(MoverType)`, `world.isRemote`->`level().isClientSide`, `this.world`->`this.level()`, `posX/posY/posZ`->`getX()/getY()/getZ()`, `motionX/Y/Z`->`getDeltaMovement()`/`setDeltaMovement`, `rotationYaw/Pitch`->`getYRot()/getXRot()`, `ticksExisted`->`tickCount`, `isDead`->`isRemoved()`/`!isAlive()`, `getEntityBoundingBox`->`getBoundingBox`, `EntityLivingBase`->`LivingEntity`, `EntityCreature`->`PathfinderMob`, `EntityMob`->`Monster`, `EnumCreatureAttribute`->use `MobType` is gone: use entity type tags/`getType().is(tag)`.
AI: `EntityAIBase`->`Goal` (`setMutexBits`->`setFlags(EnumSet<Goal.Flag>)`: bit1 MOVE, 2 LOOK, 4 JUMP), `shouldExecute/shouldContinueExecuting/startExecuting/updateTask/resetTask`->`canUse/canContinueToUse/start/tick/stop`, `tasks/targetTasks`->`goalSelector/targetSelector`, `EntityAINearestAttackableTarget`->`NearestAttackableTargetGoal` (predicate based), `PathNavigate`->`PathNavigation`, `EntityMoveHelper`->`MoveControl`, `EntityLookHelper`->`LookControl`, `EntityJumpHelper`->`JumpControl`.
Attributes: `SharedMonsterAttributes.MAX_HEALTH`->`Attributes.MAX_HEALTH`, `ATTACK_DAMAGE`, `MOVEMENT_SPEED`, `FOLLOW_RANGE`, `KNOCKBACK_RESISTANCE`, `ARMOR`; `ARMOR_TOUGHNESS`; flying speed `Attributes.FLYING_SPEED`. Modifiers: `AttributeModifier(ResourceLocation id, amount, Operation)` with operations `ADD_VALUE/ADD_MULTIPLIED_BASE/ADD_MULTIPLIED_TOTAL` (old 0/1/2) and `instance.addTransientModifier`, `removeModifier(ResourceLocation)`.
Effects: `Potion`->`MobEffect`, `PotionEffect`->`MobEffectInstance(Holder<MobEffect>, dur, amp, ambient, visible)`, `isPotionActive`->`hasEffect`, `getActivePotionEffect`->`getEffect`, `addPotionEffect`->`addEffect`, `removePotionEffect`->`removeEffect`, `performEffect`->`applyEffectTick(LivingEntity,int)`, `isReady`->`shouldApplyEffectTickThisTick(int duration,int amp)`, `MobEffects.X`->`MobEffects.X` holders.
Damage: `DamageSource.MAGIC/WITHER/OUT_OF_WORLD/GENERIC`->`level.damageSources().magic()/wither()/fellOutOfWorld()/generic()`; custom sources -> data-driven damage types under `data/srparasites/damage_type/*.json` plus `ResourceKey<DamageType>` constants in `init.SRPDamageTypes`.
World: `BlockPos` immutable (`offset`, `above()`), `getBlockState(pos).getBlock()`, `IBlockState`->`BlockState`, `setBlockState(pos,state,3)`->`level.setBlock(pos,state,3)`, `world.playSound(null,pos,sound,cat,vol,pitch)`->`level.playSound(null,pos,sound.get(),cat,vol,pitch)`, `spawnParticle`->`ServerLevel.sendParticles`, `getEntitiesWithinAABB`->`level.getEntitiesOfClass`, `world.loadedEntityList`->`ServerLevel.getAllEntities()`, `world.playerEntities`->`level.players()`, `world.provider.getDimension()`->`DimKeys.of(level)`, `world.getWorldTime()` -> `getDayTime()`, `getTotalWorldTime()`->`getGameTime()`, `rand`->`level.random` (`RandomSource`: `nextInt(bound)`, `nextFloat`, `nextDouble`, `nextBoolean`).
NBT: `NBTTagCompound`->`CompoundTag` (`hasKey`->`contains`, `getInteger`->`getInt`, `setInteger`->`putInt`...). Entity persistent data -> `entity.getPersistentData()`; `getEntityData()` same.
Items: no metadata, no damage-as-variant. `ItemStack` is never null (`isEmpty()`); durability via `hurtAndBreak`, data components for custom data (`DataComponents.CUSTOM_DATA`).
Text: `TextComponentString`->`Component.literal`, `TextComponentTranslation`->`Component.translatable`.
Events: `@SubscribeEvent` on `NeoForge.EVENT_BUS` via `@EventBusSubscriber(modid = "srparasites")` (game bus) or `bus = Bus.MOD` (registry/setup events). `LivingHurtEvent`->`LivingIncomingDamageEvent`/`LivingDamageEvent.Pre/Post`, `LivingDeathEvent` same name, `LivingUpdateEvent`->`EntityTickEvent.Pre/Post`, `EntityJoinWorldEvent`->`EntityJoinLevelEvent`, `PlayerSleepInBedEvent` etc. exist: check with `javap`.
Meta/blocks: flatten `id:meta` to explicit blocks / blockstate properties (see `porting/spec/meta_map.json` if it exists, otherwise define clearly in your notes).

## 6. Resource locations and assets
Assets: `src/main/resources/assets/srparasites/{blockstates,models/{block,item},textures/{block,item,entity,gui,...},lang,sounds,sounds.json,particles}`. 1.13+ rename: textures `blocks/`->`block/`, `items/`->`item/`, lang `.lang`->`.json` (lower-case locale file names, e.g. `en_us.json`). Data: `src/main/resources/data/srparasites/{loot_table,recipe,advancement,tags/{block,item,entity_type},damage_type,worldgen,structure}` (1.21 uses singular folder names: `loot_table`, `recipe`, `advancement`, `structure`). Tags replace hardcoded lists where the lists are identical.

## 7. Verification you must do before reporting done
1. `compile_agent.sh <name> '<your path regex>'` prints nothing for your files.
2. Re-read each ported class against the original once, checking: every branch, every number, every config flag, every event/sync call survived. If you intentionally changed something, it is in notes.
3. Grep your files for `TODO`, `FIXME`, `UnsupportedOperationException`: each must be justified in notes.

## 8. Reporting
Write `/mnt/user-data/outputs/project/porting/notes/<your-name>.md`: list of files ported (original -> new), `[CHG]` deviations (what/why), `[FLAG]` ambiguities needing the user's decision, external dependencies (class + method names you rely on from other areas), open items. Keep it factual and short. Your final message to the lead: counts, compile status, flags (max 15 lines).
