package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.monster.abomination.EntityAboBodies;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityGimAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityIkiAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityLumAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityWymoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityZaaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxA;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxB;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityDone;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHeed;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLeer;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityHeblu;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeem;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityLeemSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerBear;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerCow;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerPig;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.feral.EntityFerWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiBlaze;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiGolem;
import com.dhanantry.scapeandrunparasites.entity.monster.hijacked.EntityHiSkeleton;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityAta;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityButhol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityGothol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityViin;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSquid;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfCowHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfDragonEHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfEndermanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHorseHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfHumanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfPigHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfPlayerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfSheepHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfVillagerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.head.EntityInfWolfHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.special.EntitySpeVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityGim;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityIki;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityLum;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityWymo;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityZaa;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOrch;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityElvia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityLencia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.item.ItemBase;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The mod's own "spawn eggs" (itemmobspawner_NAME): right click on a block (or on liquid) spawns the mob named NAME, honouring the
 * per-mob enable switches of the config. Disabled mobs spawn nothing (1.10.9 handed back an unspawned zombie).
 */
public class ItemMobSpawner extends ItemBase {
    private final String name;

    public ItemMobSpawner(String name) {
        super(new Item.Properties().stacksTo(64), 0);
        this.name = name;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level worldIn = context.getLevel();
        if (worldIn.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Player player = context.getPlayer();
        ItemStack itemstack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Direction facing = context.getClickedFace();
        if (player == null || !player.mayUseItemAt(pos.relative(facing), facing, itemstack)) {
            return InteractionResult.FAIL;
        }
        BlockPos blockpos = pos.relative(facing);
        double d0 = this.getYOffset(worldIn, blockpos);
        Entity entity = this.spawnEntity(worldIn, (double) blockpos.getX() + 0.5, (double) blockpos.getY() + d0, (double) blockpos.getZ() + 0.5, player);
        if (entity != null) {
            if (entity instanceof LivingEntity && itemstack.has(DataComponents.CUSTOM_NAME)) {
                entity.setCustomName(itemstack.getHoverName());
            }
            applyItemEntityDataToEntity(worldIn, player, itemstack, entity);
            if (!player.getAbilities().instabuild) {
                itemstack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }

    protected double getYOffset(Level level, BlockPos pos) {
        AABB aabb = new AABB(pos).expandTowards(0.0, -1.0, 0.0);
        double d0 = aabb.minY;
        boolean any = false;
        for (VoxelShape shape : level.getBlockCollisions(null, aabb)) {
            any = true;
            d0 = Math.max(shape.max(Direction.Axis.Y), d0);
        }
        return any ? d0 - (double) pos.getY() : 0.0;
    }

    /** Applies the stack's "EntityTag" (custom data) to the new entity, as the vanilla egg does. */
    public static void applyItemEntityDataToEntity(Level entityWorld, @Nullable Player player, ItemStack stack, @Nullable Entity targetEntity) {
        MinecraftServer server = entityWorld.getServer();
        if (server == null || targetEntity == null) {
            return;
        }
        CompoundTag tag = ReportData.read(stack);
        if (!tag.contains("EntityTag", 10)) {
            return;
        }
        if (!entityWorld.isClientSide && targetEntity.onlyOpCanSetNbt() && (player == null || !server.getPlayerList().isOp(player.getGameProfile()))) {
            return;
        }
        CompoundTag entityTag = targetEntity.saveWithoutId(new CompoundTag());
        java.util.UUID uuid = targetEntity.getUUID();
        entityTag.merge(tag.getCompound("EntityTag"));
        targetEntity.setUUID(uuid);
        targetEntity.load(entityTag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (worldIn.isClientSide) {
            return InteractionResultHolder.pass(itemstack);
        }
        BlockHitResult hit = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.SOURCE_ONLY);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos blockpos = hit.getBlockPos();
            if (!(worldIn.getBlockState(blockpos).getBlock() instanceof LiquidBlock)) {
                return InteractionResultHolder.pass(itemstack);
            }
            if (worldIn.mayInteract(playerIn, blockpos) && playerIn.mayUseItemAt(blockpos, hit.getDirection(), itemstack)) {
                Entity entity = this.spawnEntity(worldIn, (double) blockpos.getX() + 0.5, (double) blockpos.getY() + 0.5, (double) blockpos.getZ() + 0.5, playerIn);
                if (entity == null) {
                    return InteractionResultHolder.pass(itemstack);
                }
                if (entity instanceof LivingEntity && itemstack.has(DataComponents.CUSTOM_NAME)) {
                    entity.setCustomName(itemstack.getHoverName());
                }
                applyItemEntityDataToEntity(worldIn, playerIn, itemstack, entity);
                if (!playerIn.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }
                playerIn.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.success(itemstack);
            }
            return InteractionResultHolder.fail(itemstack);
        }
        return InteractionResultHolder.pass(itemstack);
    }

    private Entity spawnEntity(Level worldIn, double x, double y, double z, Player playerIn) {
        Entity entity = new Zombie(EntityType.ZOMBIE, worldIn);
        if (!SRPConfig.allowMobs) {
            return entity;
        }
        if (this.name.equals("pod")) {
            entity = new EntityDropPod(SRPEntities.ANC_POD.get(), worldIn);
            y += 25.0;
        } else {
            if (this.name.equals("mes") && SRPConfigMobs.cruxaEnabled) {
                return this.spawnPlayer2(worldIn, x, y, z, playerIn);
            }
            if (this.name.equals("cruxa") && SRPConfigMobs.cruxaEnabled) {
                entity = new EntityCruxA(SRPEntities.CRUX.get(), worldIn);
            } else if (this.name.equals("cruxb") && SRPConfigMobs.cruxaEnabled) {
                entity = new EntityCruxB(SRPEntities.CRUX_INCOMPLETE.get(), worldIn);
            } else if (this.name.equals("heed") && SRPConfigMobs.heedEnabled) {
                entity = new EntityHeed(SRPEntities.HEED.get(), worldIn);
            } else if (this.name.equals("done") && SRPConfigMobs.heedEnabled) {
                entity = new EntityDone(SRPEntities.DREDGE.get(), worldIn);
            } else if (this.name.equals("jinjo") && SRPConfigMobs.jinjoEnabled) {
                entity = new EntityJinjo(SRPEntities.BOMBER_HEAVY.get(), worldIn);
            } else if (this.name.equals("elvia") && SRPConfigMobs.jinjoEnabled) {
                entity = new EntityElvia(SRPEntities.WRAITH.get(), worldIn);
            } else if (this.name.equals("pheon") && SRPConfigMobs.jinjoEnabled) {
                entity = new EntityPheon(SRPEntities.HAUNTER.get(), worldIn);
            } else if (this.name.equals("lencia") && SRPConfigMobs.jinjoEnabled) {
                entity = new EntityLencia(SRPEntities.BOGLE.get(), worldIn);
            } else if (this.name.equals("vesta") && SRPConfigMobs.jinjoEnabled) {
                entity = new EntityVesta(SRPEntities.CARRIER_COLONY.get(), worldIn);
            } else if (this.name.equals("rathol") && SRPConfigMobs.ratholEnabled) {
                entity = new EntityRathol(SRPEntities.CARRIER_HEAVY.get(), worldIn);
            } else if (this.name.equals("gothol") && SRPConfigMobs.ratholEnabled) {
                entity = new EntityGothol(SRPEntities.CARRIER_LIGHT.get(), worldIn);
            } else if (this.name.equals("lodo") && SRPConfigMobs.lodoEnabled) {
                entity = new EntityLodo(SRPEntities.BUGLIN.get(), worldIn);
            } else if (this.name.equals("mudo") && SRPConfigMobs.mudoEnabled) {
                entity = new EntityMudo(SRPEntities.RUPTER.get(), worldIn);
            } else if (this.name.equals("nuuh") && SRPConfigMobs.mudoEnabled) {
                entity = new EntityNuuh(SRPEntities.MANGLER.get(), worldIn);
            } else if (this.name.equals("ata")) {
                entity = new EntityAta(SRPEntities.GNAT.get(), worldIn);
            } else if (this.name.equals("viin")) {
                entity = new EntityViin(SRPEntities.LICE.get(), worldIn);
            } else if (this.name.equals("venkrol") && SRPConfigSystems.rsEnabled) {
                entity = new EntityVenkrol(SRPEntities.BECKON_SI.get(), worldIn);
            } else if (this.name.equals("venkrolsii") && SRPConfigSystems.rsEnabled) {
                entity = new EntityVenkrolSII(SRPEntities.BECKON_SII.get(), worldIn);
            } else if (this.name.equals("venkrolsiii") && SRPConfigSystems.rsEnabled) {
                entity = new EntityVenkrolSIII(SRPEntities.BECKON_SIII.get(), worldIn);
            } else if (this.name.equals("venkrolsiv") && SRPConfigSystems.rsEnabled) {
                entity = new EntityVenkrolSIV(SRPEntities.BECKON_SIV.get(), worldIn);
            } else if (this.name.equals("nak") && SRPConfigSystems.rsEnabled) {
                entity = new EntityNak(SRPEntities.SEIZER.get(), worldIn);
            } else if (this.name.equals("leem") && SRPConfigSystems.rsEnabled) {
                entity = new EntityLeem(SRPEntities.ROOTER_SI.get(), worldIn);
            } else if (this.name.equals("leemsii") && SRPConfigSystems.rsEnabled) {
                entity = new EntityLeemSII(SRPEntities.ROOTER_SII.get(), worldIn);
            } else if (this.name.equals("leemsiii") && SRPConfigSystems.rsEnabled) {
                entity = new EntityLeemSIII(SRPEntities.ROOTER_SIII.get(), worldIn);
            } else if (this.name.equals("leemsiv") && SRPConfigSystems.rsEnabled) {
                entity = new EntityLeemSIV(SRPEntities.ROOTER_SIV.get(), worldIn);
            } else if (this.name.equals("dod") && SRPConfigSystems.rsEnabled) {
                entity = new EntityDod(SRPEntities.DISPATCHER_SI.get(), worldIn);
            } else if (this.name.equals("dodsii") && SRPConfigSystems.rsEnabled) {
                entity = new EntityDodSII(SRPEntities.DISPATCHER_SII.get(), worldIn);
            } else if (this.name.equals("dodsiii") && SRPConfigSystems.rsEnabled) {
                entity = new EntityDodSIII(SRPEntities.DISPATCHER_SIII.get(), worldIn);
            } else if (this.name.equals("dodsiv") && SRPConfigSystems.rsEnabled) {
                entity = new EntityDodSIV(SRPEntities.DISPATCHER_SIV.get(), worldIn);
            } else if (this.name.equals("dorpa") && SRPConfigMobs.dorpaEnabled) {
                entity = new EntityDorpa(SRPEntities.SIM_BIGSPIDER.get(), worldIn);
            } else if (this.name.equals("infhuman") && SRPConfigMobs.infhumanEnabled) {
                entity = new EntityInfHuman(SRPEntities.SIM_HUMAN.get(), worldIn);
            } else if (this.name.equals("infhumanhead") && SRPConfigMobs.infhumanEnabled) {
                entity = new EntityInfHumanHead(SRPEntities.SIM_HUMANHEAD.get(), worldIn);
            } else if (this.name.equals("infcow") && SRPConfigMobs.infcowEnabled) {
                entity = new EntityInfCow(SRPEntities.SIM_COW.get(), worldIn);
            } else if (this.name.equals("infcowhead") && SRPConfigMobs.infcowEnabled) {
                entity = new EntityInfCowHead(SRPEntities.SIM_COWHEAD.get(), worldIn);
            } else if (this.name.equals("infsheep") && SRPConfigMobs.infsheepEnabled) {
                entity = new EntityInfSheep(SRPEntities.SIM_SHEEP.get(), worldIn);
            } else if (this.name.equals("infsquid") && SRPConfigMobs.infsquidEnabled) {
                entity = new EntityInfSquid(SRPEntities.SIM_SQUID.get(), worldIn);
            } else if (this.name.equals("infsheephead") && SRPConfigMobs.infsheepEnabled) {
                entity = new EntityInfSheepHead(SRPEntities.SIM_SHEEPHEAD.get(), worldIn);
            } else if (this.name.equals("infwolf") && SRPConfigMobs.infwolfEnabled) {
                entity = new EntityInfWolf(SRPEntities.SIM_WOLF.get(), worldIn);
            } else if (this.name.equals("infwolfhead") && SRPConfigMobs.infwolfEnabled) {
                entity = new EntityInfWolfHead(SRPEntities.SIM_WOLFHEAD.get(), worldIn);
            } else if (this.name.equals("infpig") && SRPConfigMobs.infpigEnabled) {
                entity = new EntityInfPig(SRPEntities.SIM_PIG.get(), worldIn);
            } else if (this.name.equals("infpighead") && SRPConfigMobs.infpigEnabled) {
                entity = new EntityInfPigHead(SRPEntities.SIM_PIGHEAD.get(), worldIn);
            } else if (this.name.equals("infvillager") && SRPConfigMobs.infvillagerEnabled) {
                entity = new EntityInfVillager(SRPEntities.SIM_VILLAGER.get(), worldIn);
            } else if (this.name.equals("infvillagerhead") && SRPConfigMobs.infvillagerEnabled) {
                entity = new EntityInfVillagerHead(SRPEntities.SIM_VILLAGERHEAD.get(), worldIn);
            } else {
                if (this.name.equals("infplayer") && SRPConfigMobs.infadventurerEnabled) {
                    return this.spawnPlayer(worldIn, x, y, z, playerIn);
                }
                if (this.name.equals("infplayerhead") && SRPConfigMobs.infadventurerEnabled) {
                    return this.spawnPlayerHead(worldIn, x, y, z, playerIn);
                }
                if (this.name.equals("inhoos")) {
                    entity = new EntityInhooS(SRPEntities.INCOMPLETEFORM_SMALL.get(), worldIn);
                } else if (this.name.equals("inhoom")) {
                    entity = new EntityInhooM(SRPEntities.INCOMPLETEFORM_MEDIUM.get(), worldIn);
                } else if (this.name.equals("infhorse") && SRPConfigMobs.infhorseEnabled) {
                    entity = new EntityInfHorse(SRPEntities.SIM_HORSE.get(), worldIn);
                } else if (this.name.equals("infhorsehead") && SRPConfigMobs.infhorseEnabled) {
                    entity = new EntityInfHorseHead(SRPEntities.SIM_HORSEHEAD.get(), worldIn);
                } else if (this.name.equals("infenderman") && SRPConfigMobs.infendermanEnabled) {
                    entity = new EntityInfEnderman(SRPEntities.SIM_ENDERMAN.get(), worldIn);
                } else if (this.name.equals("infendermanhead") && SRPConfigSystems.rsEnabled) {
                    entity = new EntityInfEndermanHead(SRPEntities.SIM_ENDERMANHEAD.get(), worldIn);
                } else if (this.name.equals("infbear") && SRPConfigMobs.infbearEnabled) {
                    entity = new EntityInfBear(SRPEntities.SIM_BEAR.get(), worldIn);
                } else if (this.name.equals("infdragone")) {
                    entity = new EntityInfDragonE(SRPEntities.SIM_DRAGONE.get(), worldIn);
                } else if (this.name.equals("infdragonehead") && SRPConfigSystems.rsEnabled) {
                    entity = new EntityInfDragonEHead(SRPEntities.SIM_DRAGONEHEAD.get(), worldIn);
                } else if (this.name.equals("host") && SRPConfigMobs.hostEnabled) {
                    entity = new EntityHost(SRPEntities.HOST.get(), worldIn);
                } else if (this.name.equals("hostii") && SRPConfigMobs.hostEnabled) {
                    entity = new EntityHostII(SRPEntities.HOSTII.get(), worldIn);
                } else if (this.name.equals("hull") && SRPConfigMobs.hullEnabled) {
                    entity = new EntityHull(SRPEntities.PRI_MANDUCATER.get(), worldIn);
                } else if (this.name.equals("hulladapted") && SRPConfigMobs.hullEnabled) {
                    entity = new EntityHullAdapted(SRPEntities.ADA_MANDUCATER.get(), worldIn);
                } else if (this.name.equals("canra") && SRPConfigMobs.canraEnabled) {
                    entity = new EntityCanra(SRPEntities.PRI_SUMMONER.get(), worldIn);
                } else if (this.name.equals("canraadapted") && SRPConfigMobs.canraEnabled) {
                    entity = new EntityCanraAdapted(SRPEntities.ADA_SUMMONER.get(), worldIn);
                } else if (this.name.equals("nogla") && SRPConfigMobs.noglaEnabled) {
                    entity = new EntityNogla(SRPEntities.PRI_REEKER.get(), worldIn);
                } else if (this.name.equals("noglaadapted") && SRPConfigMobs.noglaEnabled) {
                    entity = new EntityNoglaAdapted(SRPEntities.ADA_REEKER.get(), worldIn);
                } else if (this.name.equals("gim") && SRPConfigMobs.gimEnabled) {
                    entity = new EntityGim(SRPEntities.PRI_VISCERA.get(), worldIn);
                } else if (this.name.equals("gimadapted") && SRPConfigMobs.gimEnabled) {
                    entity = new EntityGimAdapted(SRPEntities.ADA_VISCERA.get(), worldIn);
                } else if (this.name.equals("zaa") && SRPConfigMobs.zaaEnabled) {
                    entity = new EntityZaa(SRPEntities.PRI_BURROWER.get(), worldIn);
                } else if (this.name.equals("zaaadapted") && SRPConfigMobs.zaaEnabled) {
                    entity = new EntityZaaAdapted(SRPEntities.ADA_BURROWER.get(), worldIn);
                } else if (this.name.equals("bano") && SRPConfigMobs.zetmoEnabled) {
                    entity = new EntityBano(SRPEntities.PRI_BOLSTER.get(), worldIn);
                } else if (this.name.equals("banoadapted") && SRPConfigMobs.zetmoEnabled) {
                    entity = new EntityBanoAdapted(SRPEntities.ADA_BOLSTER.get(), worldIn);
                } else if (this.name.equals("ranrac") && SRPConfigMobs.arachnidaEnabled) {
                    entity = new EntityRanrac(SRPEntities.PRI_ARACHNIDA.get(), worldIn);
                } else if (this.name.equals("ranracadapted") && SRPConfigMobs.arachnidaEnabled) {
                    entity = new EntityRanracAdapted(SRPEntities.ADA_ARACHNIDA.get(), worldIn);
                } else if (this.name.equals("lum") && SRPConfigMobs.lumEnabled) {
                    entity = new EntityLum(SRPEntities.PRI_DEVOURER.get(), worldIn);
                } else if (this.name.equals("lumadapted") && SRPConfigMobs.lumEnabled) {
                    entity = new EntityLumAdapted(SRPEntities.ADA_DEVOURER.get(), worldIn);
                } else if (this.name.equals("shyco") && SRPConfigMobs.shycoEnabled) {
                    entity = new EntityShyco(SRPEntities.PRI_LONGARMS.get(), worldIn);
                } else if (this.name.equals("shycoadapted") && SRPConfigMobs.shycoEnabled) {
                    entity = new EntityShycoAdapted(SRPEntities.ADA_LONGARMS.get(), worldIn);
                } else if (this.name.equals("emana") && SRPConfigMobs.emanaEnabled) {
                    entity = new EntityEmana(SRPEntities.PRI_YELLOWEYE.get(), worldIn);
                } else if (this.name.equals("emanaadapted") && SRPConfigMobs.emanaEnabled) {
                    entity = new EntityEmanaAdapted(SRPEntities.ADA_YELLOWEYE.get(), worldIn);
                } else if (this.name.equals("iki")) {
                    entity = new EntityIki(SRPEntities.PRI_VERMIN.get(), worldIn);
                } else if (this.name.equals("ikiadapted")) {
                    entity = new EntityIkiAdapted(SRPEntities.ADA_VERMIN.get(), worldIn);
                } else if (this.name.equals("wymo")) {
                    entity = new EntityWymo(SRPEntities.PRI_TOZOON.get(), worldIn);
                } else if (this.name.equals("wymoadapted")) {
                    entity = new EntityWymoAdapted(SRPEntities.ADA_TOZOON.get(), worldIn);
                } else if (this.name.equals("buthol") && SRPConfigMobs.butholEnabled) {
                    entity = new EntityButhol(SRPEntities.CARRIER_FLYING.get(), worldIn);
                } else if (this.name.equals("alafha") && SRPConfigMobs.alafhaEnabled) {
                    entity = new EntityAlafha(SRPEntities.OVERSEER.get(), worldIn);
                } else if (this.name.equals("oronco") && SRPConfigMobs.oroncoEnabled) {
                    entity = new EntityOronco(SRPEntities.ANC_DREADNAUT.get(), worldIn);
                } else if (this.name.equals("terla") && SRPConfigMobs.terlaEnabled) {
                    entity = new EntityTerla(SRPEntities.ANC_OVERLORD.get(), worldIn);
                } else if (this.name.equals("anged") && SRPConfigMobs.angedEnabled) {
                    entity = new EntityAnged(SRPEntities.VIGILANTE.get(), worldIn);
                } else if (this.name.equals("lesh")) {
                    entity = new EntityLesh(SRPEntities.MOVINGFLESH.get(), worldIn);
                } else if (this.name.equals("leer")) {
                    entity = new EntityLeer(SRPEntities.AIRSCREW.get(), worldIn);
                } else if (this.name.equals("tonro") && SRPConfigMobs.tonroEnabled) {
                    entity = new EntityTonro(SRPEntities.KYPHOSIS.get(), worldIn);
                } else if (this.name.equals("unvo") && SRPConfigMobs.unvoEnabled) {
                    entity = new EntityUnvo(SRPEntities.SENTRY.get(), worldIn);
                } else if (this.name.equals("ganro") && SRPConfigMobs.ganroEnabled) {
                    entity = new EntityGanro(SRPEntities.WARDEN.get(), worldIn);
                } else if (this.name.equals("omboo") && SRPConfigMobs.ombooEnabled) {
                    entity = new EntityOmboo(SRPEntities.BOMBER_LIGHT.get(), worldIn);
                } else if (this.name.equals("esor") && SRPConfigMobs.esorEnabled) {
                    entity = new EntityEsor(SRPEntities.MARAUDER.get(), worldIn);
                } else if (this.name.equals("orch") && SRPConfigMobs.esorEnabled) {
                    entity = new EntityOrch(SRPEntities.MONARCH.get(), worldIn);
                } else if (this.name.equals("flog") && SRPConfigMobs.flogEnabled) {
                    entity = new EntityFlog(SRPEntities.GRUNT.get(), worldIn);
                } else if (this.name.equals("ferbear") && SRPConfigMobs.ferbearEnabled) {
                    entity = new EntityFerBear(SRPEntities.FER_BEAR.get(), worldIn);
                } else if (this.name.equals("fercow") && SRPConfigMobs.fercowEnabled) {
                    entity = new EntityFerCow(SRPEntities.FER_COW.get(), worldIn);
                } else if (this.name.equals("ferenderman") && SRPConfigMobs.ferendermanEnabled) {
                    entity = new EntityFerEnderman(SRPEntities.FER_ENDERMAN.get(), worldIn);
                } else if (this.name.equals("ferhuman") && SRPConfigMobs.ferhumanEnabled) {
                    entity = new EntityFerHuman(SRPEntities.FER_HUMAN.get(), worldIn);
                } else if (this.name.equals("ferhorse") && SRPConfigMobs.ferhorseEnabled) {
                    entity = new EntityFerHorse(SRPEntities.FER_HORSE.get(), worldIn);
                } else if (this.name.equals("fersheep") && SRPConfigMobs.fersheepEnabled) {
                    entity = new EntityFerSheep(SRPEntities.FER_SHEEP.get(), worldIn);
                } else if (this.name.equals("ferpig") && SRPConfigMobs.ferpigEnabled) {
                    entity = new EntityFerPig(SRPEntities.FER_PIG.get(), worldIn);
                } else if (this.name.equals("ferwolf")) {
                    entity = new EntityFerWolf(SRPEntities.FER_WOLF.get(), worldIn);
                } else if (this.name.equals("fervillager") && SRPConfigMobs.fervillagerEnabled) {
                    entity = new EntityFerVillager(SRPEntities.FER_VILLAGER.get(), worldIn);
                } else if (this.name.equals("marcow")) {
                    entity = new EntitySpeCow(SRPEntities.MAR_COW.get(), worldIn);
                } else if (this.name.equals("marenderman")) {
                    entity = new EntitySpeEnderman(SRPEntities.MAR_ENDERMAN.get(), worldIn);
                } else if (this.name.equals("marvillager")) {
                    entity = new EntitySpeVillager(SRPEntities.MAR_VILLAGER.get(), worldIn);
                } else if (this.name.equals("marhuman")) {
                    entity = new EntitySpeHuman(SRPEntities.MAR_HUMAN.get(), worldIn);
                } else if (this.name.equals("marsheep")) {
                    entity = new EntitySpeSheep(SRPEntities.MAR_SHEEP.get(), worldIn);
                } else if (this.name.equals("marbear")) {
                    entity = new EntitySpeBear(SRPEntities.MAR_BEAR.get(), worldIn);
                } else if (this.name.equals("abobodies")) {
                    entity = new EntityAboBodies(SRPEntities.ABO_BODIES.get(), worldIn);
                } else if (this.name.equals("higolem") && SRPConfigMobs.higolemEnabled) {
                    entity = new EntityHiGolem(SRPEntities.HI_GOLEM.get(), worldIn);
                } else if (this.name.equals("hiblaze")) {
                    entity = new EntityHiBlaze(SRPEntities.HI_BLAZE.get(), worldIn);
                } else if (this.name.equals("hiskeleton")) {
                    entity = new EntityHiSkeleton(SRPEntities.HI_SKELETON.get(), worldIn);
                } else if (this.name.equals("kirin")) {
                    entity = new EntityKirin(SRPEntities.KIRIN.get(), worldIn);
                } else if (this.name.equals("heblu") && SRPConfigMobs.hebluEnabled) {
                    entity = new EntityHeblu(SRPEntities.DRACONITE.get(), worldIn);
                }
            }
        }
        entity.moveTo(x, y, z, Mth.wrapDegrees(worldIn.random.nextFloat() * 360.0f), 0.0f);
        if (entity instanceof LivingEntity living) {
            living.yHeadRot = living.getYRot();
            living.yBodyRot = living.getYRot();
        }
        if (entity instanceof Mob mob) {
            mob.finalizeSpawn((ServerLevel) worldIn, worldIn.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, (SpawnGroupData) null);
        }
        worldIn.addFreshEntity(entity);
        return entity;
    }

    private Entity spawnPlayer(Level worldIn, double x, double y, double z, Player playerIn) {
        EntityInfPlayer entity = new EntityInfPlayer(SRPEntities.SIM_ADVENTURER.get(), worldIn);
        ItemStack head = new ItemStack(playerIn.getItemBySlot(EquipmentSlot.HEAD).getItem());
        ItemStack legs = new ItemStack(playerIn.getItemBySlot(EquipmentSlot.LEGS).getItem());
        ItemStack feet = new ItemStack(playerIn.getItemBySlot(EquipmentSlot.FEET).getItem());
        if (head.getItem() != Items.AIR) {
            entity.setItemSlot(EquipmentSlot.HEAD, head);
            entity.setHelmetSlot(true);
        }
        entity.setItemSlot(EquipmentSlot.LEGS, legs);
        entity.setItemSlot(EquipmentSlot.FEET, feet);
        entity.moveTo(x, y, z, Mth.wrapDegrees(worldIn.random.nextFloat() * 360.0f), 0.0f);
        entity.yHeadRot = entity.getYRot();
        entity.yBodyRot = entity.getYRot();
        entity.finalizeSpawn((ServerLevel) worldIn, worldIn.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        SRPEntityUtil.setCustomNameTag(entity, playerIn.getName().getString());
        entity.setCustomNameVisible(true);
        worldIn.addFreshEntity((Entity)entity);
        return entity;
    }

    private Entity spawnPlayer2(Level worldIn, double x, double y, double z, Player playerIn) {
        EntityMes entity = new EntityMes(SRPEntities.THRALL.get(), worldIn);
        entity.moveTo(x, y, z, Mth.wrapDegrees(worldIn.random.nextFloat() * 360.0f), 0.0f);
        entity.yHeadRot = entity.getYRot();
        entity.yBodyRot = entity.getYRot();
        entity.finalizeSpawn((ServerLevel) worldIn, worldIn.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        SRPEntityUtil.setCustomNameTag(entity, playerIn.getName().getString());
        entity.setCustomNameVisible(true);
        worldIn.addFreshEntity((Entity)entity);
        return entity;
    }

    private Entity spawnPlayerHead(Level worldIn, double x, double y, double z, Player playerIn) {
        EntityInfPlayerHead entity = new EntityInfPlayerHead(SRPEntities.SIM_ADVENTURERHEAD.get(), worldIn);
        entity.moveTo(x, y, z, Mth.wrapDegrees(worldIn.random.nextFloat() * 360.0f), 0.0f);
        entity.yHeadRot = entity.getYRot();
        entity.yBodyRot = entity.getYRot();
        entity.finalizeSpawn((ServerLevel) worldIn, worldIn.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.MOB_SUMMONED, null);
        SRPEntityUtil.setCustomNameTag(entity, playerIn.getName().getString());
        entity.setCustomNameVisible(true);
        worldIn.addFreshEntity((Entity)entity);
        return entity;
    }
}
