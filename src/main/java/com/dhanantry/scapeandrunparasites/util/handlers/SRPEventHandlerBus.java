package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockDiseasedSponge;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.SRPBlockLinks;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityCanHaveBodies;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPInfected;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolArmorBase;
import com.dhanantry.scapeandrunparasites.network.FogPayload;
import com.dhanantry.scapeandrunparasites.network.MovingSoundPayload;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.EIVUtil;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.dhanantry.scapeandrunparasites.util.SRPReference;
import com.dhanantry.scapeandrunparasites.util.convert.BeckonBlockInfestation;
import com.dhanantry.scapeandrunparasites.util.spawn.ParasiteSummon;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldParasiteSpawner;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityMountEvent;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.level.block.CropGrowEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class SRPEventHandlerBus {
    private static final Map<UUID, FogProperties> playerFogProperties = new HashMap<UUID, FogProperties>();
        public static byte clientCurrentEvoPhase;
    public static int clientScent;
    public static int clientVector;
    public static int musicTimer;
    private static byte lockedMu = 0;
    public static float fog;
    public static float fogRed;
    public static float fogGreen;
    public static float fogBlue;
    private static final Map<String, Long> RICARDO_DEATH_RULE_RESTORE;
    private static boolean srpSoakGuard;
    private static boolean closeG = false;
    private static int counerW = 0;
    private static int blockInfestedCountCooldown;
    private static int blockParasiteCountCooldown;
    private static int moo = 0;
    private static ArrayList<String> worldsChecked = new ArrayList<>();
    private static int meteor;
    private static int heart;

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void onWorldLoad(LevelEvent.Unload event) {
        if (!event.getLevel().isClientSide()) {
            return;
        }
        clientCurrentEvoPhase = 0;
        clientScent = 0;
        fog = 0.0f;
        musicTimer = 1000;
        SRPSaveData.falseLevel = 0;
    }

    @SubscribeEvent
    public static void cropGrow(CropGrowEvent.Pre event) {
        if (event.getResult() == CropGrowEvent.Pre.Result.DO_NOT_GROW) {
            return;
        }
        if (!SRPConfigWorld.nodesActivated && !SRPConfigSystems.useEvolution) {
            return;
        }
        SRPWorldData data = SRPWorldData.get((Level)event.getLevel());
        int nodeAge = data.nearestHeartAge(event.getPos(), false, 0);
        double[] nodeChance = new double[]{0.0, SRPConfigWorld.nodeCropStopNodeOne, SRPConfigWorld.nodeCropStopNodeTwo, SRPConfigWorld.nodeCropStopNodeThree};
        if (nodeAge >= 1 && nodeAge <= 3 && event.getLevel().getRandom().nextDouble() < nodeChance[nodeAge]) {
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
            return;
        }
        byte phase = SRPSaveData.get((Level)event.getLevel()).getEvolutionPhase(DimKeys.of((Level)event.getLevel()));
        double[] phaseChance = new double[]{0.0, SRPConfigSystems.cropGrowStunnedOne, SRPConfigSystems.cropGrowStunnedTwo, SRPConfigSystems.cropGrowStunnedThree, SRPConfigSystems.cropGrowStunnedFour, SRPConfigSystems.cropGrowStunnedFive, SRPConfigSystems.cropGrowStunnedSix, SRPConfigSystems.cropGrowStunnedSeven, SRPConfigSystems.cropGrowStunnedEight};
        if (phase >= 1 && phase < phaseChance.length && event.getLevel().getRandom().nextDouble() < phaseChance[phase]) {
            event.setResult(CropGrowEvent.Pre.Result.DO_NOT_GROW);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Entity src = event.getSource().getEntity();
        if (!(src instanceof EntityNogla)) {
            return;
        }
        EntityNogla nogla = (EntityNogla)src;
        if (!nogla.isRicardoVariant()) {
            return;
        }
        Player player = (Player)event.getEntity();
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        ServerLevel ws = (ServerLevel)player.level();
        GameRules rules = ws.getGameRules();
        boolean wasShowing = rules.getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);
        if (wasShowing) {
            rules.getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(false, server);
            RICARDO_DEATH_RULE_RESTORE.put(DimKeys.of(ws), ws.getGameTime() + 1L);
        }
        server.getPlayerList().broadcastSystemMessage(Component.translatable("death.attack.srparasites.ricardo", new Object[]{player.getDisplayName()}), false);
    }

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post e) {
        if (e.getLevel().isClientSide) {
            return;
        }
        String dim = DimKeys.of(e.getLevel());
        Long when = RICARDO_DEATH_RULE_RESTORE.get(dim);
        if (when != null && e.getLevel().getGameTime() >= when) {
            ((ServerLevel)e.getLevel()).getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES).set(true, e.getLevel().getServer());
            RICARDO_DEATH_RULE_RESTORE.remove(dim);
        }
    }

    private static Block getDeadBloodBlock() {
        return SRPBlocks.DeadBlood.get();
    }

    private static Block getDiseasedSpongeBlock() {
        return SRPBlocks.diseasedSponge.get();
    }

    private static boolean isSponge(Block b) {
        return b == Blocks.SPONGE;
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent e) {
        if (e.getLevel().isClientSide() || !(e.getLevel() instanceof Level lvl)) {
            return;
        }
        SRPEventHandlerBus.tryConvertSponge(lvl, e.getPos(), e.getPlacedBlock());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent e) {
        block20: {
            if (e.getLevel().isClientSide() || !(e.getLevel() instanceof Level world)) {
                return;
            }
            BlockPos pos = e.getPos();
            Block deadBlood = SRPEventHandlerBus.getDeadBloodBlock();
            Block changed = e.getState().getBlock();
            BlockState changedState = e.getState();
            if (!(SRPEventHandlerBus.isSponge(changed) || changed == deadBlood || SRPEventHandlerBus.isWater(changedState) || SRPEventHandlerBus.isLava(changedState))) {
                return;
            }
            if (srpSoakGuard) {
                return;
            }
            srpSoakGuard = true;
            try {
                if (changed == deadBlood) {
                    for (Direction f : Direction.values()) {
                        BlockState rubble;
                        BlockPos n = pos.relative(f);
                        BlockState st = world.getBlockState(n);
                        if (SRPEventHandlerBus.isSponge(st.getBlock())) {
                            SRPEventHandlerBus.tryConvertSponge(world, n, st);
                            continue;
                        }
                        if (SRPEventHandlerBus.isWater(st)) {
                            BlockState stain = SRPEventHandlerBus.getParasiteStainState();
                            if (stain.getBlock() == Blocks.AIR) continue;
                            world.setBlock(n, stain, 2);
                            continue;
                        }
                        if (!SRPEventHandlerBus.isLava(st) || (rubble = SRPEventHandlerBus.getParasiteRubbleState()).getBlock() == Blocks.AIR) continue;
                        world.setBlock(n, rubble, 2);
                    }
                    return;
                }
                if (SRPEventHandlerBus.isWater(changedState) || SRPEventHandlerBus.isLava(changedState)) {
                    boolean touchesDeadBlood = false;
                    for (Direction f : Direction.values()) {
                        if (world.getBlockState(pos.relative(f)).getBlock() != deadBlood) continue;
                        touchesDeadBlood = true;
                        break;
                    }
                    if (touchesDeadBlood) {
                        if (SRPEventHandlerBus.isWater(changedState)) {
                            BlockState stain = SRPEventHandlerBus.getParasiteStainState();
                            if (stain.getBlock() != Blocks.AIR) {
                                world.setBlock(pos, stain, 2);
                            }
                        } else {
                            BlockState rubble = SRPEventHandlerBus.getParasiteRubbleState();
                            if (rubble.getBlock() != Blocks.AIR) {
                                world.setBlock(pos, rubble, 2);
                            }
                        }
                    }
                    return;
                }
                if (!SRPEventHandlerBus.isSponge(changed)) break block20;
                for (Direction f : Direction.values()) {
                    if (world.getBlockState(pos.relative(f)).getBlock() != deadBlood) continue;
                    SRPEventHandlerBus.tryConvertSponge(world, pos, changedState);
                    break;
                }
            }
            finally {
                srpSoakGuard = false;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void tryConvertSponge(Level world, BlockPos spongePos, BlockState spongeState) {
        if (srpSoakGuard) {
            return;
        }
        if (!SRPEventHandlerBus.isSponge(spongeState.getBlock())) {
            return;
        }
        Block deadBlood = SRPEventHandlerBus.getDeadBloodBlock();
        Block diseased = SRPEventHandlerBus.getDiseasedSpongeBlock();
        if (deadBlood == Blocks.AIR || diseased == Blocks.AIR) {
            return;
        }
        boolean touching = false;
        for (Direction f : Direction.values()) {
            if (world.getBlockState(spongePos.relative(f)).getBlock() != deadBlood) continue;
            touching = true;
            break;
        }
        if (!touching) {
            return;
        }
        srpSoakGuard = true;
        try {
            boolean absorbed = BlockDiseasedSponge.absorbDeadBlood(world, spongePos, deadBlood);
            if (absorbed) {
                world.setBlock(spongePos, diseased.defaultBlockState(), 2);
            }
        }
        finally {
            srpSoakGuard = false;
        }
    }

    private static BlockState getParasiteStainState() {
        Block b = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("srparasites", "parasitestain"));
        if (b == null || b == Blocks.AIR) {
            return Blocks.AIR.defaultBlockState();
        }
        return BlockIds.legacyState(b, 1);
    }

    private static BlockState getParasiteRubbleState() {
        Block b = (Block)BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("srparasites", "parasiterubble"));
        if (b == null || b == Blocks.AIR) {
            return Blocks.AIR.defaultBlockState();
        }
        return BlockIds.legacyState(b, 7);
    }

    private static boolean isWater(BlockState st) {
        LegacyMaterial m = LegacyMaterial.of(st);
        return m == LegacyMaterial.water;
    }

    private static boolean isLava(BlockState st) {
        LegacyMaterial m = LegacyMaterial.of(st);
        return m == LegacyMaterial.lava;
    }

    @SubscribeEvent
    public static void entityHeal(LivingHealEvent event) {
        if (event.isCanceled()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (SRPBlockLinks.isParasiteBiome(entity.level(), entity.blockPosition())) {
            float penaltyH;
            switch (ParasiteEventWorld.canBiomeStillExistType(entity.level(), entity.blockPosition(), true)) {
                case 2: {
                    penaltyH = SRPConfigWorld.biomeTwoHealPenalty;
                    break;
                }
                case 3: {
                    penaltyH = SRPConfigWorld.biomeThreeHealPenalty;
                    break;
                }
                case 4: {
                    penaltyH = SRPConfigWorld.biomeFourHealPenalty;
                    break;
                }
                default: {
                    penaltyH = SRPConfigWorld.biomeOneHealPenalty;
                }
            }
            if (entity instanceof EntityParasiteBase) {
                return;
            }
            if (entity instanceof Player) {
                event.setAmount(event.getAmount() * penaltyH);
            } else {
                boolean flag = ParasiteEventEntity.checkName(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString(), SRPConfigWorld.biomeHealPenaltyBlackList, SRPConfigWorld.biomeHealPenaltyBlackListWhite);
                if (flag) {
                    return;
                }
                event.setAmount(event.getAmount() * penaltyH);
            }
        }
        if (SRPConfigSystems.useEvolution && SRPSaveData.get(entity.level()).getEvolutionPhase(DimKeys.of(entity.level())) >= SRPConfigSystems.evolutionNoParasiteHealing) {
            event.setAmount(event.getAmount() * SRPConfigSystems.evolutionNoParasiteHealingValue);
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingDamageEvent.Pre event) {
        LivingEntity mob;
        float damage;
        float amp;
        LivingEntity entity = event.getEntity();
        if (entity == null || entity.level().isClientSide) {
            return;
        }
        if (entity.hasEffect(SRPPotions.VIRA_E) && SRPConfigSystems.viralEnable) {
            amp = entity.getEffect(SRPPotions.VIRA_E).getAmplifier() + 1;
            damage = event.getNewDamage();
            event.setNewDamage(damage + damage * (amp * SRPConfigSystems.viralAmount));
        }
        if (entity.hasEffect(SRPPotions.OVERHEATING_E) && (event.getSource().is(DamageTypes.ON_FIRE) || event.getSource().is(DamageTypes.IN_FIRE))) {
            amp = entity.getEffect(SRPPotions.OVERHEATING_E).getAmplifier() + 1;
            damage = event.getNewDamage();
            event.setNewDamage(damage + damage * (amp * 1.0f));
        }
        if (event.getSource().getEntity() instanceof LivingEntity && (mob = (LivingEntity)event.getSource().getEntity()).hasEffect(SRPPotions.MUSCLEOUT_E)) {
            float amp2 = (float)mob.getEffect(SRPPotions.MUSCLEOUT_E).getAmplifier() + 1.0f;
            event.setNewDamage(event.getNewDamage() * (SRPConfigSystems.muscleoutDamageOut * amp2));
        }
        if (entity instanceof Player) {
            Player player = (Player)entity;
            event.setNewDamage(playerArmor(player, event.getNewDamage(), event.getSource()));
        }
    }

    private static float playerArmor(Player player, float damageV, DamageSource source) {
        float red = 0.0f;
        String damage = "";
        byte type = 0;
        boolean needCheckP = true;
        boolean paraA = false;
        for (ItemStack itemstack : player.getInventory().armor) {
            int i;
            ListTag allResI;
            ListTag allResS;
            if (!(itemstack.getItem() instanceof WeaponToolArmorBase)) continue;
            paraA = true;
            if (needCheckP && source.getDirectEntity() != null) {
                if (source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE) || player.isOnFire()) {
                    return damageV * SRPConfig.firemultyplier;
                }
                if (source.getDirectEntity() instanceof Player) {
                    damage = source.getDirectEntity().getName().getString();
                } else if (source.getDirectEntity() instanceof LivingEntity) {
                    damage = BuiltInRegistries.ENTITY_TYPE.getKey(source.getDirectEntity().getType()).toString();
                } else {
                    damage = source.getMsgId();
                    type = 2;
                }
                needCheckP = false;
            }
            ArrayList<String> resistanceS = new ArrayList<String>();
            ArrayList<Integer> resistanceI = new ArrayList<Integer>();
            CompoundTag compound = itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            boolean lag = ((WeaponToolArmorBase)itemstack.getItem()).canCall();
            if (compound == null) {
                compound = new CompoundTag();
            }
            if (compound.contains("sprresistanceb", 99)) {
                lag = compound.getBoolean("sprresistanceb");
            }
            if (compound.contains("sprresistances")) {
                allResS = compound.getList("sprresistances", 10);
                allResI = compound.getList("sprresistancei", 10);
                if (allResS.size() != allResI.size()) {
                    return damageV;
                }
                for (i = 0; i < allResS.size(); ++i) {
                    CompoundTag resT = allResS.getCompound(i);
                    String res = resT.getString("resistance" + i);
                    resistanceS.add(i, res);
                    CompoundTag resU = allResI.getCompound(i);
                    int resi = resU.getInt("resistance" + i);
                    resistanceI.add(i, resi);
                }
            }
            red += (float)hasResistance(damage, resistanceS, resistanceI, lag, player.level().random, type) * (lag ? SRPConfig.sentientPointReduction : SRPConfig.livingPointReduction);
            if (resistanceS.size() != resistanceI.size()) {
                return damageV;
            }
            allResS = new ListTag();
            allResI = new ListTag();
            for (i = 0; i < resistanceS.size(); ++i) {
                String res = resistanceS.get(i);
                CompoundTag resT = new CompoundTag();
                resT.putString("resistance" + i, res);
                allResS.add((Tag)resT);
                int resi = resistanceI.get(i);
                CompoundTag resU = new CompoundTag();
                resU.putInt("resistance" + i, resi);
                allResI.add((Tag)resU);
            }
            compound.put("sprresistances", (Tag)allResS);
            compound.put("sprresistancei", (Tag)allResI);
            compound.putBoolean("sprresistanceb", lag);
            if (compound.contains("srphits")) {
                int key = (int)((float)compound.getInt("srphits") + damageV);
                compound.putInt("srphits", key);
            } else {
                compound.putInt("srphits", (int)damageV);
            }
            itemstack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        }
        red *= damageV;
        damageV = Math.max(damageV - red, 0.0f);
        if (source.getDirectEntity() instanceof LivingEntity && paraA && SRPConfig.armorCoth) {
            SRPPotions.applyStackPotion(SRPPotions.COTH_E, (LivingEntity)source.getDirectEntity(), 400, 2);
        }
        return damageV;
    }

    private static int hasResistance(String damage, ArrayList<String> resistanceS, ArrayList<Integer> resistanceI, boolean stage, RandomSource rand, byte type) {
        double getChanceLearn;
        if (checkList(damage, type)) {
            return 0;
        }
        double d = getChanceLearn = stage ? SRPConfig.sentientChanceLe : SRPConfig.livingChanceLe;
        if (rand.nextDouble() < getChanceLearn) {
            addResistance(damage, resistanceS, resistanceI, stage);
        }
        for (int i = 0; i < resistanceS.size(); ++i) {
            if (!resistanceS.get(i).equals(damage)) continue;
            int tage = SRPConfig.livingPointCap;
            if (stage) {
                tage = SRPConfig.sentientPointCap;
            }
            return Math.min(resistanceI.get(i), tage);
        }
        return 0;
    }

    private static void addResistance(String damage, ArrayList<String> resistanceS, ArrayList<Integer> resistanceI, boolean stage) {
        boolean flag = true;
        for (int i = 0; i < resistanceS.size(); ++i) {
            if (!resistanceS.get(i).equals(damage)) continue;
            int iiii = resistanceI.get(i) + 1;
            resistanceI.set(i, iiii);
            flag = false;
            break;
        }
        if (flag) {
            int lim = SRPConfig.livingDamageCap;
            if (stage) {
                lim = SRPConfig.sentientDamageCap;
            }
            if (resistanceS.size() >= lim) {
                return;
            }
            resistanceS.add(damage);
            resistanceI.add(1);
        }
    }

    private static boolean checkList(String damage, byte type) {
        switch (type) {
            case 0: {
                if (ParasiteEventEntity.checkName(damage, SRPConfig.armorDamageTypeBlackListMob, SRPConfig.armorDamageTypeBlackListWhite)) {
                    return true;
                }
            }
            case 2: {
                if (!ParasiteEventEntity.checkName(damage, SRPConfig.armorDamageTypeBlackListElse, SRPConfig.armorDamageTypeBlackListWhite)) break;
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void playerFishing(ItemFishedEvent event) {
        if (!event.getEntity().level().isClientSide && SRPConfigSystems.useEvolution) {
            SRPWorldData data = SRPWorldData.get(event.getEntity().level());
            if (SRPSaveData.get(event.getEntity().level()).getEvolutionPhase(DimKeys.of(event.getEntity().level())) >= SRPConfigSystems.evolutionStopFishing) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void itemEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getTarget() instanceof LivingEntity && !event.getLevel().isClientSide) {
            ItemStack stack = event.getItemStack();
            String item = stack.getItem().builtInRegistryHolder().key().location().toString();
            String[] atm = new String[3];
            for (int i = 0; i < SRPConfigSystems.COTHItemPrevent.length; ++i) {
                atm = SRPConfigSystems.COTHItemPrevent[i].split(";");
                if (!atm[0].equals(item)) continue;
                int dur = Integer.parseInt(atm[2]);
                if (!event.getEntity().getAbilities().instabuild) {
                    stack.shrink(1);
                    double chance = Double.parseDouble(atm[1]);
                    if (!(event.getLevel().random.nextDouble() < chance)) continue;
                    ((LivingEntity)event.getTarget()).addEffect(new MobEffectInstance(SRPPotions.EPEL_E, dur * 20, 0));
                    PacketDistributor.sendToAllPlayers(new ParticlePayload(event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ(), event.getTarget().getBbWidth(), event.getTarget().getBbHeight(), 3));
                    continue;
                }
                ((LivingEntity)event.getTarget()).addEffect(new MobEffectInstance(SRPPotions.EPEL_E, dur * 20, 0));
                PacketDistributor.sendToAllPlayers(new ParticlePayload(event.getTarget().getX(), event.getTarget().getY(), event.getTarget().getZ(), event.getTarget().getBbWidth(), event.getTarget().getBbHeight(), 3));
            }
        }
    }

    @SubscribeEvent
    public static void itemPlayer(LivingEntityUseItemEvent.Start event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player)event.getEntity();
        if (player == null) {
            return;
        }
        if (player.hasEffect(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive) {
            if (player.getEffect(SRPPotions.FEAR_E).getAmplifier() >= 1 && player.level().random.nextDouble() < (double)SRPConfigSystems.fearItemChance && !ParasiteEventEntity.checkName(player.getItemBySlot(EquipmentSlot.MAINHAND).getItem().builtInRegistryHolder().key().location().toString(), SRPConfigSystems.fearItemBlackList, SRPConfigSystems.fearItemBlackListWhite)) {
                player.displayClientMessage(Component.translatable("message.srparasites.fearitem", new Object[0]).withStyle(ChatFormatting.RED), true);
                event.setDuration(-1);
                event.setCanceled(true);
                return;
            }
            if (event.getItem().getItem() instanceof BlockItem) {
                // empty if block
            }
        }
    }

    @SubscribeEvent
    public static void blockPlayer(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        if (player.hasEffect(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive) {
            if (player.getEffect(SRPPotions.FEAR_E).getAmplifier() >= 2 && player.level().random.nextDouble() < (double)SRPConfigSystems.fearBlockChance) {
                player.displayClientMessage(Component.translatable("message.srparasites.fearblock", new Object[0]).withStyle(ChatFormatting.RED), true);
                event.setUseBlock(TriState.FALSE);
            } else if (player.getEffect(SRPPotions.FEAR_E).getAmplifier() >= 1 && player.level().random.nextDouble() < (double)SRPConfigSystems.fearItemChance) {
                player.displayClientMessage(Component.translatable("message.srparasites.fearitem", new Object[0]).withStyle(ChatFormatting.RED), true);
                event.setUseItem(TriState.FALSE);
            }
        }
    }

    @SubscribeEvent
    public static void entityPlayer(PlayerInteractEvent.EntityInteractSpecific event) {
    }

    @SubscribeEvent
    public static void mobFear(LivingDamageEvent.Pre event) {
        boolean trueAirborne;
        boolean inFluidOrSupport;
        LivingEntity in = event.getEntity();
        if (in == null || !SRPConfigSystems.fearActive) {
            return;
        }
        if (!in.hasEffect(SRPPotions.FEAR_E)) {
            return;
        }
        int amp = in.getEffect(SRPPotions.FEAR_E).getAmplifier() + 1;
        if (event.getSource().is(DamageTypes.FALL) && SRPConfigSystems.fearFallDamage != 0.0f) {
            event.setNewDamage(event.getNewDamage() * (SRPConfigSystems.fearFallDamage * (float)amp));
        }
        if (SRPConfigSystems.fearAirDamage == 0.0f) {
            return;
        }
        if (SRPConfigSystems.fearUnfair) {
            if (!in.onGround()) {
                event.setNewDamage(event.getNewDamage() * (SRPConfigSystems.fearAirDamage * (float)amp));
            }
            return;
        }
        boolean bl = inFluidOrSupport = in.isInWater() || in.isInLava() || in.onClimbable() || in.isPassenger();
        if (inFluidOrSupport) {
            return;
        }
        boolean creativeFlying = false;
        boolean elytraFlying = false;
        if (in instanceof Player) {
            Player p = (Player)in;
            creativeFlying = p.getAbilities().flying;
            elytraFlying = p.isFallFlying();
        }
        float MIN_FALL_DISTANCE = 2.0f;
        boolean descending = in.getDeltaMovement().y < 0.0;
        boolean bl2 = trueAirborne = creativeFlying || elytraFlying || !in.onGround() && descending && in.fallDistance >= 2.0f;
        if (trueAirborne) {
            event.setNewDamage(event.getNewDamage() * (SRPConfigSystems.fearAirDamage * (float)amp));
        }
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity && !event.getEntity().level().isClientSide) {
            String mobname;
            if (event.getEntity() instanceof Player) {
                return;
            }
            try {
                mobname = BuiltInRegistries.ENTITY_TYPE.getKey(event.getEntity().getType()).toString();
            }
            catch (Exception e) {
                ScapeAndRunParasites.LOGGER.error("Problem with spawning entity");
                return;
            }
            CompoundTag tags = event.getEntity().getPersistentData();
            boolean parasite = event.getEntity() instanceof EntityParasiteBase;
            boolean flagNC = SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution;
            SRPWorldData data = null;
            if (flagNC) {
                data = SRPWorldData.get(event.getLevel());
            }
            if (SRPConfigWorld.nodesActivated && SRPConfigSystems.cothActive && data.nearestHeartAge(event.getEntity().blockPosition(), false, 0) != -1) {
                ((LivingEntity)event.getEntity()).addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            if (!tags.contains("srpcothimmunity") && SRPConfigSystems.cothActive && !parasite) {
                writeCOTHTag((LivingEntity)event.getEntity(), mobname, tags);
            }
            if (parasite) {
                setNewParasiteTask((EntityParasiteBase)event.getEntity(), mobname, flagNC, data);
            } else if (event.getEntity() instanceof PathfinderMob) {
                setNewCreatureTask((PathfinderMob)event.getEntity(), mobname);
            }
        }
    }

    private static void writeCOTHTag(LivingEntity in, String mobname, CompoundTag tags) {
        if (in instanceof ArmorStand) {
            tags.putInt("srpcothimmunity", 0);
        } else if (ParasiteEventEntity.checkName(mobname, SRPConfigSystems.COTHImmuneList, SRPConfigSystems.COTHImmuneListWhite)) {
            tags.putInt("srpcothimmunity", 0);
        } else {
            tags.putInt("srpcothimmunity", 1);
        }
        setCOTH(in, SRPSaveData.get(in.level()).getEvolutionPhase(DimKeys.of(in.level())));
    }

    private static void setNewParasiteTask(EntityParasiteBase entity, String mobname, boolean flagNC, SRPWorldData data) {
        SRPSaveData dataS = SRPSaveData.get(entity.level());
        if (!entity.spawnedByColo) {
            EntityCanHaveBodies head;
            entity.spawnedByColo = true;
            switch (dataS.getChoice()) {
                case 2: {
                    changeAttribute(entity, Attributes.MAX_HEALTH, 3.0);
                    changeAttribute(entity, Attributes.ARMOR, 3.0);
                    changeAttribute(entity, Attributes.ATTACK_DAMAGE, 3.0);
                    changeAttribute(entity, Attributes.KNOCKBACK_RESISTANCE, 3.0);
                    break;
                }
                case 3: {
                    changeAttribute(entity, Attributes.MAX_HEALTH, 10.0);
                    changeAttribute(entity, Attributes.ARMOR, 10.0);
                    changeAttribute(entity, Attributes.ATTACK_DAMAGE, 10.0);
                    changeAttribute(entity, Attributes.KNOCKBACK_RESISTANCE, 10.0);
                }
            }
            entity.applyBonuses(dataS, entity.level());
            applyDislo(entity, dataS, DimKeys.of(entity.level()));
            applyColony(entity, data.totalColonyPoints(0));
            applyNode(entity, data.totalNodePoints(0));
            if (entity instanceof EntityPMalleable) {
                EntityPMalleable uwu = (EntityPMalleable)entity;
                String damage = data.getMostCommonDamageS();
                if (damage != null) {
                    for (int times = data.getMostCommonDamageI(); times > 0; --times) {
                        uwu.addResistance(damage);
                    }
                    uwu.increaseDamageCap(1);
                    uwu.colonySpawned = true;
                }
            }
            if (entity instanceof EntityCanHaveBodies && (head = (EntityCanHaveBodies)(entity)).getCanF()) {
                int len = head.getBodyLength();
                EntityCanHaveBodies current = head;
                for (int i = 0; i < len; ++i) {
                    EntityCanHaveBodies entityWithBodies = head.getAnotherBody(entity.level());
                    entityWithBodies.setCanF(false);
                    entityWithBodies.setFollowing(current);
                    entityWithBodies.copyCopy(current);
                    entityWithBodies.onSpawn(entity.level().getCurrentDifficultyAt(entity.blockPosition()), null);
                    entity.level().addFreshEntity(entityWithBodies.getEntity());
                    entityWithBodies.setBodyNumber(i + 1);
                    if (len - 1 == i) {
                        entityWithBodies.setBodyTail(true);
                    }
                    current = entityWithBodies;
                }
            }
        }
        if (SRPConfig.parasiteGriefing.length != 0) {
            String[] task = new String[4];
            for (int i = 0; i < SRPConfig.parasiteGriefing.length; ++i) {
                if (SRPConfig.parasiteGriefing[i] == null || !(task = SRPConfig.parasiteGriefing[i].split(";"))[0].equals(mobname)) continue;
                if (entity instanceof EntityPStationary) {
                    entity.setSkillBreakBlocksValues(Float.parseFloat(task[1]), Mth.ceil((float)entity.getBbHeight()), Integer.parseInt(task[3]));
                    break;
                }
                entity.setSkillBreakBlocksValues(Float.parseFloat(task[1]), Mth.ceil((float)entity.getBbHeight()), Integer.parseInt(task[3]));
                entity.goalSelector.addGoal(9, new EntityAISkill(entity, Integer.parseInt(task[2]), 64, false, 13));
                break;
            }
        }
        if (entity instanceof EntityPInfected && SRPConfigSystems.generationUse) {
            entity.setHealth(entity.getHealth() * getSimCOTHMod(SRPSaveData.get(entity.level()), entity.level()));
        }
    }

    private static void applyDislo(EntityParasiteBase in, SRPSaveData dataTwo, String id) {
        int[] killa = dataTwo.getDisloValues(id);
        if (SRPConfigSystems.disloSummonByDeath && !in.disloNumberTwo && killa[2] > 0) {
            in.disloNumberTwo = true;
        }
        if (SRPConfigSystems.disloPotiEff && !in.disloNumberThree && killa[3] > 0) {
            in.disloNumberThree = true;
            int amp = dataTwo.getCurrentCode(id, 3);
            String here = SRPConfigSystems.disloPotiEffEffects[in.level().random.nextInt(SRPConfigSystems.disloPotiEffEffects.length)];
            Holder<MobEffect> potion = SRPEntityUtil.effect(here);
            if (potion != null) {
                in.addEffect(new MobEffectInstance(potion, dataTwo.getCurrentCodeDuration(id, 3) * 20 + 50, amp));
            }
        }
        if (SRPConfigSystems.dislostats && !in.disloNumberFour && killa[4] > 0) {
            in.disloNumberFour = true;
            int multip = dataTwo.getCurrentCode(id, 4);
            in.getAttribute(Attributes.MAX_HEALTH).setBaseValue(in.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * (double)(1 + multip));
            in.getAttribute(Attributes.ARMOR).setBaseValue(in.getAttribute(Attributes.ARMOR).getBaseValue() * (double)(1 + multip));
            in.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(in.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * (double)(1 + multip));
        }
        if (SRPConfigSystems.disloItemDura && !in.disloNumberSix && killa[6] > 0) {
            in.disloNumberSix = true;
        }
        if (SRPConfigSystems.disloHealingDeath && !in.disloNumberSeven && killa[7] > 0) {
            in.disloNumberSeven = true;
        }
        if (SRPConfigSystems.disloDamageDeath && !in.disloNumberEight && killa[8] > 0) {
            in.disloNumberEight = true;
        }
        if (SRPConfigSystems.disloFoodDeath && !in.disloNumberNine && killa[9] > 0) {
            in.disloNumberNine = true;
        }
        if (SRPConfigSystems.disloParasiteNoPotion && in.disloNumberEleven <= 0 && killa[11] > 0) {
            in.disloNumberEleven = dataTwo.getCurrentCodeDuration(id, 11) * 20 + 50;
        }
        if (SRPConfigSystems.disloGrowlNoise && in.disloNumberFifteen <= 0 && killa[15] > 0) {
            in.disloNumberFifteen = dataTwo.getCurrentCodeDuration(id, 15) * 20 + 50;
            in.getEntityData().set(EntityParasiteBase.DISLO15, true);
        }
        if (SRPConfigSystems.disloWalkNoise && in.disloNumberSixteen <= 0 && killa[16] > 0) {
            in.disloNumberSixteen = dataTwo.getCurrentCodeDuration(id, 16) * 20 + 50;
        }
        if (SRPConfigSystems.disloShieldFood && in.disloNumberSeventeen <= 0 && killa[17] > 0) {
            in.disloNumberSeventeen = dataTwo.getCurrentCodeDuration(id, 17) * 20 + 50;
        }
        if (SRPConfigSystems.disloLootXpCanc && !in.disloNumberEighteen && killa[18] > 0) {
            in.disloNumberEighteen = true;
        }
        if (SRPConfigSystems.disloKillcountInc && in.disloNumberNineteen <= 0 && killa[19] > 0) {
            in.disloNumberNineteen = dataTwo.getCurrentCodeDuration(id, 19) * 20 + 50;
            in.disloNumberNineteenValue = killa[19];
        }
        if (SRPConfigSystems.disloGiveBodies && killa[20] > 0) {
            if (in instanceof EntityInhooM) {
                ((EntityInhooM)in).disloNumberTwenty = true;
            }
            if (in instanceof EntityInhooS) {
                ((EntityInhooS)in).disloNumberTwenty = true;
            }
        }
        if (SRPConfigSystems.disloBurningDeath && !in.disloNumberTwentyone && killa[21] > 0) {
            in.disloNumberTwentyone = true;
        }
        if (SRPConfigSystems.disloSameVersionDyeing && !in.disloNumberTwentytwo && killa[22] > 0) {
            in.disloNumberTwentytwo = true;
        }
    }

    private static void applyColony(EntityParasiteBase in, int totalColonyPoints) {
        if (totalColonyPoints == 0) {
            return;
        }
        double bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraHealthPoint * SRPConfigWorld.colonyExtraHealthValue;
        changeAttribute(in, Attributes.MAX_HEALTH, bonus);
        in.setHealth((float)in.getAttribute(Attributes.MAX_HEALTH).getBaseValue());
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraArmorPoint * SRPConfigWorld.colonyExtraArmorValue;
        changeAttribute(in, Attributes.ARMOR, bonus);
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraDamagePoint * SRPConfigWorld.colonyExtraDamageValue;
        changeAttribute(in, Attributes.ATTACK_DAMAGE, bonus);
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyExtraKDResPoint * SRPConfigWorld.colonyExtraKDResValue;
        changeAttribute(in, Attributes.KNOCKBACK_RESISTANCE, bonus);
        bonus = (float)totalColonyPoints / SRPConfigWorld.colonyDamageCapPoint * SRPConfigWorld.colonyDamageCapValue;
        in.damageCap = (int)((double)in.damageCap + (double)in.damageCap * bonus);
    }

    private static void changeAttribute(EntityParasiteBase in, Holder<Attribute> stat, double bonus) {
        double base = in.getAttribute(stat).getBaseValue();
        in.getAttribute(stat).setBaseValue(base + base * bonus);
    }

    private static void applyNode(EntityParasiteBase in, int totalPoints) {
        if (totalPoints == 0) {
            return;
        }
        String[] here = new String[3];
        for (String i : SRPConfigWorld.potionEffectForNodes) {
            here = i.split(";");
            int level = Integer.parseInt(here[0]);
            if (totalPoints < level) continue;
            int amp = Integer.parseInt(here[2]);
            Holder<MobEffect> potion = SRPEntityUtil.effect(here[1]);
            if (potion == null) continue;
            in.addEffect(new MobEffectInstance(potion, 7777, amp));
        }
    }

    private static float getSimCOTHMod(SRPSaveData data, Level world) {
        switch (data.getGeneration(DimKeys.of(world))) {
            case 0: {
                return SRPConfigSystems.generationCOTH0;
            }
            case 1: {
                return SRPConfigSystems.generationCOTH1;
            }
            case 2: {
                return SRPConfigSystems.generationCOTH2;
            }
            case 3: {
                return SRPConfigSystems.generationCOTH3;
            }
            case 4: {
                return SRPConfigSystems.generationCOTH4;
            }
            case 5: {
                return SRPConfigSystems.generationCOTH5;
            }
        }
        return 1.0f;
    }

    @SubscribeEvent
    public static void onEntityMount(EntityMountEvent event) {
        if (event.getEntityBeingMounted() == null || event.getEntityMounting() == null) {
            return;
        }
        if (!event.getEntityBeingMounted().isAlive()) {
            return;
        }
        if (event.getEntityMounting() instanceof Player && event.getEntityBeingMounted() instanceof EntityHiGolem) {
            Player player = (Player)event.getEntityMounting();
            if (!player.getAbilities().invulnerable && player.getHealth() > 0.0f && event.isDismounting()) {
                event.setCanceled(true);
            }
        }
    }

    private static void setNewCreatureTask(PathfinderMob entity, String mobname) {
        if (ParasiteEventEntity.checkName(mobname, SRPConfig.entitiesWillAttack, SRPConfig.entitiesWillAttackWhite)) {
            entity.targetSelector.addGoal(5, new NearestAttackableTargetGoal(entity, EntityParasiteBase.class, true));
            return;
        }
        if (ParasiteEventEntity.checkName(mobname, SRPConfig.entitiesWillAvoid, SRPConfig.entitiesWillAvoidWhite)) {
            entity.goalSelector.addGoal(5, new AvoidEntityGoal(entity, EntityParasiteBase.class, 12.0f, 0.8, 0.8));
        }
    }

    private static void setCOTH(LivingEntity target, byte evo) {
        if (target.isBaby()) {
            return;
        }
        RandomSource rand = RandomSource.create();
        switch (evo) {
            case 1: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceOne)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 2: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceTwo)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 3: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceThree)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 4: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceFour)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 5: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceFive)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 6: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceSix)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 7: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceSeven)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
                break;
            }
            case 8: {
                if (!(rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceEight)) break;
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void serverTick(ServerTickEvent.Pre event) {
        if (BeckonBlockInfestation.blockInfestedCount > SRPConfig.BlockInfestedLimit) {
            ++blockInfestedCountCooldown;
            if (blockInfestedCountCooldown > SRPConfig.BlockInfestedLimitCD) {
                blockInfestedCountCooldown = 0;
                BeckonBlockInfestation.blockInfestedCount = 0;
            }
        }
        if (BlockParasiteSpreading.blockParasiteCount > SRPConfig.BlockParasiteLimit) {
            ++blockParasiteCountCooldown;
            if (blockParasiteCountCooldown > SRPConfig.BlockParasiteLimitCD) {
                blockParasiteCountCooldown = 0;
                BlockParasiteSpreading.blockParasiteCount = 0;
            }
        }
        ++counerW;
        if (SRPConfigWorld.originWorldCheckDebugSpeed > 0) {
            counerW += SRPConfigWorld.originWorldCheckDebugSpeed;
        }
        --ParasiteEventWorld.disloCool;
        if (counerW > SRPConfig.dayTickValue && (SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution || SRPConfigWorld.originActivated)) {
            MinecraftServer ser = event.getServer();
            SRPSaveData dat = SRPSaveData.get(ser);
            dat.addUpdateNumber(1, ser);
            worldsChecked = new ArrayList<>();
            counerW = -150;
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void worldTick(LevelTickEvent.Pre event) {
        if (!event.getLevel().isClientSide) {
            ++moo;
            if (SRPConfigSystems.useEvolution && SRPConfigSystems.phaseCustomSpawner) {
                if (event.getLevel().getServer() == null) {
                    return;
                }
                tickSpawn((ServerLevel)event.getLevel());
            }
            if (moo >= 20 * SRPConfigSystems.disloSeconds) {
                moo = 0;
                SRPSaveData.get(event.getLevel()).reduceCodesCooldown(DimKeys.of(event.getLevel()), SRPConfigSystems.disloSeconds, event.getLevel());
            }
            if (!SRPConfigWorld.meteorActive) {
                meteor = 0;
            } else {
                ++meteor;
                if (meteor > SRPConfigWorld.meteorTick) {
                    SRPWorldData data;
                    meteor = 0;
                    if (event.getLevel().random.nextDouble() < SRPConfigWorld.meteorChance && SRPConfig.spawnDays <= (int)event.getLevel().getGameTime() && (data = SRPWorldData.get(event.getLevel())).getTriggerMet() && SRPSaveData.get(event.getLevel()).getEvolutionPhase(DimKeys.of(event.getLevel())) >= 0) {
                        if (SRPConfigWorld.meteorVectorless) {
                            if (data.getorigins("x").isEmpty()) {
                                spawningMet(event.getLevel());
                            }
                        } else {
                            spawningMet(event.getLevel());
                        }
                    }
                }
            }
            if (counerW < 0 && (SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution || SRPConfigWorld.originActivated)) {
                String id = DimKeys.of(event.getLevel());
                for (String i : worldsChecked) {
                    if (!i.equals(id)) continue;
                    return;
                }
                ParasiteEventWorld.checkColonyStatus(event.getLevel());
                ParasiteEventWorld.checkNodeStatus(event.getLevel());
                ScapeAndRunParasites.LOGGER.debug("[EIV DEBUG] worldTick is calling createRandomOrigin. dim={} time={} counerW={} worldsChecked={} originActivated={} min={} max={}", DimKeys.of(event.getLevel()), event.getLevel().getGameTime(), counerW, worldsChecked, SRPConfigWorld.originActivated, SRPConfigWorld.originCreatingDistanceMin, SRPConfigWorld.originCreatingDistanceMax);
                EIVUtil.createRandomOrigin(event.getLevel(), SRPConfigWorld.originCreatingDistanceMin, SRPConfigWorld.originCreatingDistanceMax);
                worldsChecked.add(id);
            }
        }
    }

    private static void spawningMet(Level world) {
        block2: {
            Player mob;
            ArrayList mobs = new ArrayList<>();
            mobs.addAll(world.players());
            boolean flag = true;
            if (mobs.size() == 0) break block2;
            Iterator iterator = mobs.iterator();
            while (iterator.hasNext()) {
                mob = (Player)iterator.next();
                if (!world.canSeeSky(mob.blockPosition())) continue;
                ParasiteSummon.spawnMeteor(mob.blockPosition(), world.random.nextInt(SRPConfigWorld.meteorRadius), SRPConfigWorld.meteorMinRadius, world);
                flag = false;
                break;
            }
            if (flag && (iterator = mobs.iterator()).hasNext()) {
                mob = (Player)iterator.next();
                ParasiteSummon.spawnMeteor(mob.blockPosition(), world.random.nextInt(SRPConfigWorld.meteorRadius), SRPConfigWorld.meteorMinRadius, world);
            }
        }
    }

    private static void tickSpawn(ServerLevel server) {
        if (server.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && !server.isDebug()) {
            SRPWorldParasiteSpawner.findChunksForSpawning(server, true, false, server.getGameTime() % 400L == 0L);
        }
    }

    @SubscribeEvent
    public static void setLoot(LivingDropsEvent event) {
        if (event.getEntity() instanceof EntityParasiteBase) {
            if (!event.getEntity().level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                return;
            }
            if (event.getEntity().hasEffect(SRPPotions.DEBAR_E)) {
                return;
            }
            EntityParasiteBase mob = (EntityParasiteBase)event.getEntity();
            if (mob.disloNumberEighteen && SRPSaveData.get(event.getEntity().level()).getCurrentCode(DimKeys.of(event.getEntity().level()), 18) > 0) {
                return;
            }
            switch (mob.getParasiteIDRegister()) {
                case 1: {
                    loot(event, SRPConfigMobs.shycoLoot);
                    return;
                }
                case 51: {
                    loot(event, SRPConfigMobs.shycoadaptedloot);
                    return;
                }
                case 10: {
                    loot(event, SRPConfigMobs.noglaLoot);
                    return;
                }
                case 54: {
                    loot(event, SRPConfigMobs.noglaadaptedloot);
                    return;
                }
                case 4: {
                    loot(event, SRPConfigMobs.emanaLoot);
                    return;
                }
                case 55: {
                    loot(event, SRPConfigMobs.emanaadaptedloot);
                    return;
                }
                case 7: {
                    loot(event, SRPConfigMobs.hullLoot);
                    return;
                }
                case 52: {
                    loot(event, SRPConfigMobs.hulladaptedloot);
                    return;
                }
                case 8: {
                    loot(event, SRPConfigMobs.canraLoot);
                    return;
                }
                case 53: {
                    loot(event, SRPConfigMobs.canraadaptedloot);
                    return;
                }
                case 38: {
                    loot(event, SRPConfigMobs.arachnidaLoot);
                    return;
                }
                case 58: {
                    loot(event, SRPConfigMobs.arachnidaadaptedloot);
                    return;
                }
                case 37: {
                    loot(event, SRPConfigMobs.shycoLoot);
                    return;
                }
                case 57: {
                    loot(event, SRPConfigMobs.shycoLoot);
                    return;
                }
                case 66: {
                    loot(event, SRPConfigMobs.lumLoot);
                    return;
                }
                case 81: {
                    loot(event, SRPConfigMobs.lumadaptedloot);
                    return;
                }
                case 17: {
                    loot(event, SRPConfigMobs.zetmoLoot);
                    return;
                }
                case 56: {
                    loot(event, SRPConfigMobs.zetmoadaptedloot);
                    return;
                }
                case 2: {
                    loot(event, SRPConfigMobs.dorpaLoot);
                    return;
                }
                case 49: {
                    loot(event, SRPConfigMobs.infbearLoot);
                    return;
                }
                case 13: {
                    loot(event, SRPConfigMobs.infcowLoot);
                    return;
                }
                case 28: {
                    loot(event, SRPConfigMobs.infcowheadLoot);
                    return;
                }
                case 64: {
                    loot(event, SRPConfigMobs.infdragoneLoot);
                    return;
                }
                case 70: {
                    loot(event, SRPConfigMobs.infdragoneheadLoot);
                    return;
                }
                case 59: {
                    loot(event, SRPConfigMobs.infendermanLoot);
                    return;
                }
                case 69: {
                    loot(event, SRPConfigMobs.infendermanheadLoot);
                    return;
                }
                case 44: {
                    loot(event, SRPConfigMobs.infhorseLoot);
                    return;
                }
                case 45: {
                    loot(event, SRPConfigMobs.infhorseheadLoot);
                    return;
                }
                case 6: {
                    loot(event, SRPConfigMobs.infhumanLoot);
                    return;
                }
                case 46: {
                    loot(event, SRPConfigMobs.infhumanheadLoot);
                    return;
                }
                case 26: {
                    loot(event, SRPConfigMobs.infpigLoot);
                    return;
                }
                case 31: {
                    loot(event, SRPConfigMobs.infpigheadLoot);
                    return;
                }
                case 40: {
                    loot(event, SRPConfigMobs.infadventurerLoot);
                    return;
                }
                case 71: {
                    loot(event, SRPConfigMobs.infadventurerheadLoot);
                    return;
                }
                case 14: {
                    loot(event, SRPConfigMobs.infsheepLoot);
                    return;
                }
                case 22: {
                    loot(event, SRPConfigMobs.infsheepheadLoot);
                    return;
                }
                case 27: {
                    loot(event, SRPConfigMobs.infvillagerLoot);
                    return;
                }
                case 32: {
                    loot(event, SRPConfigMobs.infvillagerheadLoot);
                    return;
                }
                case 15: {
                    loot(event, SRPConfigMobs.infwolfLoot);
                    return;
                }
                case 21: {
                    loot(event, SRPConfigMobs.infwolfheadLoot);
                    return;
                }
                case 306: {
                    loot(event, SRPConfigMobs.ferbearLoot);
                    return;
                }
                case 93: {
                    loot(event, SRPConfigMobs.fercowLoot);
                    return;
                }
                case 94: {
                    loot(event, SRPConfigMobs.ferendermanLoot);
                    return;
                }
                case 95: {
                    loot(event, SRPConfigMobs.ferhorseLoot);
                    return;
                }
                case 96: {
                    loot(event, SRPConfigMobs.ferhumanLoot);
                    return;
                }
                case 97: {
                    loot(event, SRPConfigMobs.ferpigLoot);
                    return;
                }
                case 98: {
                    loot(event, SRPConfigMobs.fersheepLoot);
                    return;
                }
                case 99: {
                    loot(event, SRPConfigMobs.fervillagerLoot);
                    return;
                }
                case 300: {
                    loot(event, SRPConfigMobs.ferwolfLoot);
                    return;
                }
                case 324: {
                    loot(event, SRPConfigMobs.marhumanLoot);
                    return;
                }
                case 330: {
                    loot(event, SRPConfigMobs.marbearLoot);
                    return;
                }
                case 322: {
                    loot(event, SRPConfigMobs.marcowLoot);
                    return;
                }
                case 329: {
                    loot(event, SRPConfigMobs.marsheepLoot);
                    return;
                }
                case 321: {
                    loot(event, SRPConfigMobs.marendermanLoot);
                    return;
                }
                case 323: {
                    loot(event, SRPConfigMobs.marvillagerLoot);
                    return;
                }
                case 302: {
                    loot(event, SRPConfigMobs.hiblazeLoot);
                    return;
                }
                case 301: {
                    loot(event, SRPConfigMobs.higolemLoot);
                    return;
                }
                case 303: {
                    loot(event, SRPConfigMobs.hiskeletonLoot);
                    return;
                }
                case 9: {
                    loot(event, SRPConfigMobs.alafhaLoot);
                    return;
                }
                case 25: {
                    loot(event, SRPConfigMobs.angedLoot);
                    return;
                }
                case 50: {
                    loot(event, SRPConfigMobs.esorLoot);
                    return;
                }
                case 60: {
                    loot(event, SRPConfigMobs.flogLoot);
                    return;
                }
                case 33: {
                    loot(event, SRPConfigMobs.ganroLoot);
                    return;
                }
                case 47: {
                    loot(event, SRPConfigMobs.ombooLoot);
                    return;
                }
                case 82: {
                    loot(event, SRPConfigMobs.ombooLoot);
                    return;
                }
                case 65: {
                    loot(event, SRPConfigMobs.jinjoLoot);
                    return;
                }
                case 85: {
                    loot(event, SRPConfigMobs.elviaLoot);
                    return;
                }
                case 86: {
                    loot(event, SRPConfigMobs.lenciaLoot);
                    return;
                }
                case 88: {
                    loot(event, SRPConfigMobs.vestaLoot);
                    return;
                }
                case 87: {
                    loot(event, SRPConfigMobs.pheonLoot);
                    return;
                }
                case 11: {
                    loot(event, SRPConfigMobs.butholLoot);
                    return;
                }
                case 36: {
                    loot(event, SRPConfigMobs.kolLoot);
                    return;
                }
                case 23: {
                    loot(event, SRPConfigMobs.kolLoot);
                    return;
                }
                case 5: {
                    loot(event, SRPConfigMobs.LodoLoot);
                    return;
                }
                case 12: {
                    loot(event, SRPConfigMobs.mudoLoot);
                    return;
                }
                case 76: {
                    loot(event, SRPConfigMobs.nuuhLoot);
                    return;
                }
                case 3: {
                    loot(event, SRPConfigMobs.ratholLoot);
                    return;
                }
                case 91: {
                    loot(event, SRPConfigMobs.ataLoot);
                    return;
                }
                case 334: {
                    loot(event, SRPConfigMobs.viinLoot);
                    return;
                }
                case 74: {
                    loot(event, SRPConfigMobs.ratholLoot);
                    return;
                }
                case 72: {
                    loot(event, SRPConfigMobs.nakLoot);
                    return;
                }
                case 29: {
                    loot(event, SRPConfigMobs.tonroLoot);
                    return;
                }
                case 30: {
                    loot(event, SRPConfigMobs.unvoLoot);
                    return;
                }
                case 16: {
                    loot(event, SRPConfigMobs.venkrolLoot);
                    return;
                }
                case 18: {
                    loot(event, SRPConfigMobs.venkrolsiiLoot);
                    return;
                }
                case 19: {
                    loot(event, SRPConfigMobs.venkrolsiiiLoot);
                    return;
                }
                case 41: {
                    loot(event, SRPConfigMobs.venkrolsivLoot);
                    return;
                }
                case 73: {
                    loot(event, SRPConfigMobs.dodsiLoot);
                    return;
                }
                case 77: {
                    loot(event, SRPConfigMobs.dodsiiLoot);
                    return;
                }
                case 78: {
                    loot(event, SRPConfigMobs.dodsiiiLoot);
                    return;
                }
                case 79: {
                    loot(event, SRPConfigMobs.dodsivLoot);
                    return;
                }
                case 62: {
                    loot(event, SRPConfigMobs.cruxaLoot);
                    return;
                }
                case 63: {
                    loot(event, SRPConfigMobs.heedLoot);
                    return;
                }
                case 48: {
                    loot(event, SRPConfigMobs.hostLoot);
                    return;
                }
                case 75: {
                    loot(event, SRPConfigMobs.herdLoot);
                    return;
                }
                case 39: {
                    loot(event, SRPConfigMobs.inhooSLoot);
                    return;
                }
                case 43: {
                    loot(event, SRPConfigMobs.inhooMLoot);
                    return;
                }
                case 80: {
                    loot(event, SRPConfigMobs.thrallLoot);
                    return;
                }
                case 24: {
                    loot(event, SRPConfigMobs.oroncoLoot);
                    return;
                }
                case 20: {
                    loot(event, SRPConfigMobs.terlaLoot);
                    return;
                }
                case 309: {
                    loot(event, SRPConfigMobs.hebluLoot);
                    return;
                }
                case 67: {
                    loot(event, SRPConfigMobs.kirinLoot);
                    return;
                }
                case 34: {
                    loot(event, SRPConfigMobs.pod1Loot);
                }
            }
        } else if (event.getEntity() instanceof LivingEntity && !(event.getEntity() instanceof Player) && SRPConfigSystems.cothActive) {
            if (event.getSource().getEntity() instanceof EntityParasiteBase && SRPConfig.mobsKilledDropLoot) {
                event.setCanceled(true);
                return;
            }
            if (((LivingEntity)event.getEntity()).hasEffect(SRPPotions.COTH_E)) {
                int key;
                CompoundTag tags;
                if (SRPConfigSystems.useEvolution) {
                    int key2;
                    CompoundTag tags2;
                    if (SRPSaveData.get(event.getEntity().level()).getEvolutionPhase(DimKeys.of(event.getEntity().level())) >= SRPConfigSystems.evolutionCothStopLoot && (tags2 = event.getEntity().getPersistentData()).contains("srpcothimmunity") && (key2 = tags2.getInt("srpcothimmunity")) != 0) {
                        event.setCanceled(true);
                    }
                } else if (SRPConfigSystems.cothLootDisable && (tags = event.getEntity().getPersistentData()).contains("srpcothimmunity") && (key = tags.getInt("srpcothimmunity")) != 0) {
                    event.setCanceled(true);
                }
            }
        }
    }

    private static void loot(LivingDropsEvent event, String[] drop) {
        block8: {
            try {
                int realquantity;
                int rng;
                int chance;
                int quantity;
                if (drop.length == 0) break block8;
                String[] dropping = new String[4];
                String[] dropped = new String[drop.length];
                RandomSource rand = RandomSource.create();
                int totalFalse = 0;
                for (String s : drop) {
                    dropping = s.split(";");
                    boolean always = Boolean.parseBoolean(dropping[3]);
                    quantity = Integer.parseInt(dropping[2]);
                    chance = Integer.parseInt(dropping[1]);
                    if (always) {
                        rng = rand.nextInt(100);
                        if (rng > chance - 1) continue;
                        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(dropping[0]));
                        realquantity = rand.nextInt(quantity);
                        for (int j = 0; j <= realquantity && item != null; ++j) {
                            BlockPos pos = event.getEntity().blockPosition();
                            event.getDrops().add(new ItemEntity(event.getEntity().level(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack(item)));
                        }
                        continue;
                    }
                    dropped[totalFalse] = s;
                    ++totalFalse;
                }
                if (totalFalse != 0) {
                    int n = rand.nextInt(totalFalse);
                    String[] stringItem = dropped[n].split(";");
                    quantity = Integer.parseInt(stringItem[2]);
                    chance = Integer.parseInt(stringItem[1]);
                    rng = rand.nextInt(100);
                    if (rng <= chance - 1) {
                        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(stringItem[0]));
                        realquantity = rand.nextInt(quantity);
                        for (int j = 0; j <= realquantity && item != null; ++j) {
                            BlockPos pos = event.getEntity().blockPosition();
                            event.getDrops().add(new ItemEntity(event.getEntity().level(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack(item)));
                        }
                    }
                }
            }
            catch (Exception e) {
                ScapeAndRunParasites.LOGGER.error("Problem with loot event", e);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void playerTick(PlayerTickEvent.Pre event) {
        {
            Player thePlayer = event.getEntity();
            if (thePlayer.level().isClientSide) {
                if (closeG) {
                    if (thePlayer.containerMenu != thePlayer.inventoryMenu) {
                        thePlayer.closeContainer();
                    }
                    closeG = false;
                }
            } else {
                SRPWorldData data;
                int age;
                boolean isParaBiome = SRPBlockLinks.isParasiteBiome(thePlayer.level(), thePlayer.blockPosition());
                if (ParasiteEventWorld.canBiomeStillExist(thePlayer.level(), thePlayer.blockPosition(), false) >= 1 || isParaBiome) {
                    BiomeParasiteBase biomeChecked = isParaBiome ? SRPBlockLinks.parasiteBiomeAt(thePlayer.level(), thePlayer.blockPosition()) : SRPReference.getBiomeFromInt(ParasiteEventWorld.canBiomeStillExistType(thePlayer.level(), thePlayer.blockPosition(), false));
                    fog = Math.min(fog + 4.5E-4f, SRPConfigWorld.biomeFogDensity);
                    fogRed = biomeChecked.getRedValue();
                    fogGreen = biomeChecked.getGreenValue();
                    fogBlue = biomeChecked.getBlueValue();
                    if (fog < SRPConfigWorld.biomeFogDensity) {
                        PacketDistributor.sendToPlayer((ServerPlayer)thePlayer, new FogPayload(fog, fogRed, fogGreen, fogBlue));
                    }
                } else if (fog > 0.0f) {
                    fog = Math.max(fog - 8.0E-4f, 0.0f);
                    fogRed = 0.0f;
                    fogGreen = 0.0f;
                    fogBlue = 0.0f;
                    PacketDistributor.sendToPlayer((ServerPlayer)thePlayer, new FogPayload(fog, fogRed, fogGreen, fogBlue));
                }
                ++heart;
                if (heart < SRPConfigWorld.biomeHeartFreq) {
                    return;
                }
                heart = 0;
                if (isParaBiome && (age = (data = SRPWorldData.get(thePlayer.level())).nearestHeartAge(thePlayer.blockPosition(), true, 0)) > 0) {
                    int totalS = data.getDistanceSpreadByAge(age, true);
                    float vol = ((float)data.isInRangeOfHeart(thePlayer.blockPosition(), totalS) / (float)totalS - 1.0f) * -1.0f;
                    PacketDistributor.sendToPlayer((ServerPlayer)thePlayer, new MovingSoundPayload(-1, vol));
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            if (SRPConfigSystems.cothActive && SRPConfigMobs.infadventurerEnabled && SRPConfigMobs.infadventurerSpawnBy && player.hasEffect(SRPPotions.COTH_E)) {
                int amp = player.getEffect(SRPPotions.COTH_E).getAmplifier();
                if (amp >= 2) {
                    EntityInfPlayer out = new EntityInfPlayer(SRPEntities.SIM_ADVENTURER.get(), player.level());
                    ItemStack head = new ItemStack(player.getItemBySlot(EquipmentSlot.HEAD).getItem());
                    ItemStack legs = new ItemStack(player.getItemBySlot(EquipmentSlot.LEGS).getItem());
                    ItemStack feet = new ItemStack(player.getItemBySlot(EquipmentSlot.FEET).getItem());
                    if (head.getItem() != Items.AIR) {
                        out.setItemSlot(EquipmentSlot.HEAD, head);
                        out.setHelmetSlot(true);
                    }
                    out.setItemSlot(EquipmentSlot.LEGS, legs);
                    out.setItemSlot(EquipmentSlot.FEET, feet);
                    out.copyPosition((Entity)player);
                    out.finalizeSpawn((ServerLevel) out.level(), player.level().getCurrentDifficultyAt(out.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    player.level().addFreshEntity((Entity)out);
                    player.level().levelEvent((Player)null, 1026, out.blockPosition(), 0);
                    SRPEntityUtil.setCustomNameTag(out, player.getName().getString());
                    out.setCustomNameVisible(true);
                    out.particleStatus((byte)7);
                    out.cannotDespawn(false);
                } else if (amp == 1) {
                    EntityInhooM out = new EntityInhooM(SRPEntities.INCOMPLETEFORM_MEDIUM.get(), player.level());
                    out.copyPosition((Entity)player);
                    out.finalizeSpawn((ServerLevel) out.level(), player.level().getCurrentDifficultyAt(out.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
                    player.level().addFreshEntity((Entity)out);
                    player.level().levelEvent((Player)null, 1026, out.blockPosition(), 0);
                    SRPEntityUtil.setCustomNameTag(out, player.getName().getString());
                    out.setCustomNameVisible(true);
                    out.particleStatus((byte)7);
                    out.cannotDespawn(false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerUp(PlayerWakeUpEvent event) {
        if (!SRPConfigSystems.useEvolution || event.getEntity().level().isClientSide) {
            return;
        }
        SRPSaveData data = SRPSaveData.get(event.getEntity().level());
        Level world = event.getEntity().level();
        if (event.getEntity().level().getDayTime() % (long)SRPConfig.dayTickValue < 13000L) {
            int bonus = 1;
            if (data.getEvolutionPhase(DimKeys.of(world)) >= SRPConfigSystems.evolutionSleepDenied) {
                bonus = 5;
            }
            data.setTotalKills(DimKeys.of(world), getSleepPointP(data.getEvolutionPhase(DimKeys.of(world))) * bonus, true, world, true, true, 55);
        }
    }

    private static int getSleepPointP(byte phase) {
        switch (phase) {
            case 0: {
                return SRPConfigSystems.sleepPenaltyZero;
            }
            case 1: {
                return SRPConfigSystems.sleepPenaltyOne;
            }
            case 2: {
                return SRPConfigSystems.sleepPenaltyTwo;
            }
            case 3: {
                return SRPConfigSystems.sleepPenaltyThree;
            }
            case 4: {
                return SRPConfigSystems.sleepPenaltyFour;
            }
            case 5: {
                return SRPConfigSystems.sleepPenaltyFive;
            }
            case 6: {
                return SRPConfigSystems.sleepPenaltySix;
            }
            case 7: {
                return SRPConfigSystems.sleepPenaltySeven;
            }
            case 8: {
                return SRPConfigSystems.sleepPenaltyEight;
            }
            case 9: {
                return SRPConfigSystems.sleepPenaltyNine;
            }
            case 10: {
                return SRPConfigSystems.sleepPenaltyTen;
            }
        }
        return 0;
    }

    @SubscribeEvent
    public static void light(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof EntityParasiteBase) {
            ((EntityParasiteBase)event.getEntity()).setKillC(1000000.0);
        }
    }

    static {
        clientScent = 0;
        clientVector = 0;
        musicTimer = 1000;
        fog = 0.0f;
        fogRed = 0.0f;
        fogGreen = 0.0f;
        fogBlue = 0.0f;
        RICARDO_DEATH_RULE_RESTORE = new HashMap<String, Long>();
        srpSoakGuard = false;
    }

    public static class FogProperties {
        public float density;
        public float red;
        public float green;
        public float blue = 0.0f;

        public FogProperties(float red, float green, float blue, float density) {
            this.density = density;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public FogProperties() {
        }
    }
}

