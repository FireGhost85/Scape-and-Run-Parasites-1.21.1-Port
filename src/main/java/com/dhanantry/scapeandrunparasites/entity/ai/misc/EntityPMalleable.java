package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityOrbScary;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAIBlockLight;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPCosmical;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCruxB;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.Mot;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public abstract class EntityPMalleable
extends EntityParasiteBase {
    protected static final EntityDataAccessor<Byte> HIT = SynchedEntityData.defineId(EntityPMalleable.class, EntityDataSerializers.BYTE);
    private ArrayList<String> resistanceS = new ArrayList();
    private ArrayList<Integer> resistanceI = new ArrayList();
    protected int newDamageCooldown = 0;
    protected float pointReduction = 0.1f;
    protected int pointCap = 10;
    protected int DamageTypeCap = 5;
    protected double chanceLearn = 0.5;
    protected double chanceLearnFire = 0.0;
    private int onFireA;
    private int onProj;
    protected float adaptationCap = 1.0f;
    protected float regen = 0.0f;
    protected int regenEff = 1;
    protected int regenUse = 1;
    private int stun;
    public boolean colonySpawned;
    protected int fuseOrb;
    protected int orbStartTimer;
    protected int orbItemCool;
    protected int orbVersionCooldown;
    protected float damageAmountReduced;
    protected boolean geneAdaptation = true;
    protected boolean geneBlocksearch = true;
    protected boolean geneResidue = true;
    protected boolean geneOrbbox = true;
    protected int limitOrb;
    protected int borderOrb;
    protected boolean skillOrb;

    public EntityPMalleable(EntityType<? extends EntityPMalleable> type, Level worldIn) {
        super(type, worldIn);
        this.foodRott = 0.0;
        this.foodRootNumber = 1;
        if (SRPConfig.canTargetBlock) {
            this.goalSelector.addGoal(7, new EntityAIBlockLight(this, 20, 5));
        }
        this.setScentHPMultiplier(2.5f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HIT, (byte) (0));
    }

    @Override
    public boolean getGeneMod(int id) {
        switch (id) {
            case 6: {
                return this.geneAdaptation;
            }
            case 7: {
                return this.geneBlocksearch;
            }
            case 8: {
                return this.geneResidue;
            }
            case 9: {
                return this.geneOrbbox;
            }
        }
        return super.getGeneMod(id);
    }

    @Override
    public void applyGene(boolean[] kool, float[] goon) {
        if (!SRPConfigSystems.generationUse) {
            return;
        }
        this.geneMindam = kool[0];
        this.geneDamcap = kool[1];
        this.geneLookwall = kool[2];
        this.geneSprinting = kool[3];
        this.geneWaterleap = kool[4];
        this.geneSpecialmove = kool[5];
        this.geneAdaptation = kool[6];
        this.geneBlocksearch = kool[7];
        this.geneResidue = kool[8];
        this.geneOrbbox = kool[9];
        this.genePoisonHealing = goon[0];
        this.geneMobHealing = goon[1];
        this.geneAttackSpeed = goon[2];
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.srpTicks == 10) {
                if (this.hasEffect(SRPPotions.RES_E)) {
                    this.removeAllResistance(1);
                }
                if (this.orbVersionCooldown > 0) {
                    --this.orbVersionCooldown;
                }
                if (!this.isRemoved() && this.getHealth() > 0.0f && !this.isOnFire() && this.regen > 0.0f && this.killcount > 1.0 && this.getHealth() < this.getMaxHealth()) {
                    this.setHealth(this.getHealth() + this.regen);
                    --this.regenUse;
                    if (this.regenUse <= 0) {
                        this.killcount -= 1.0;
                        this.regenUse = this.regenEff;
                    }
                }
            }
            if (this.onFireA > 0) {
                --this.onFireA;
            }
            if (this.onProj > 0) {
                --this.onProj;
            }
            if (this.newDamageCooldown > 0) {
                --this.newDamageCooldown;
            }
            if (this.stun >= 0 && --this.stun < 0 && this.getParasiteStatus() == 25) {
                this.setParasiteStatus(0);
            }
        }
    }

    @Override
    public boolean hurt(@Nonnull DamageSource source, float amount) {
        ResourceLocation key;
        ItemStack stack;
        if (this.level().isClientSide) {
            return false;
        }
        this.entityData.set(HIT, (byte) (0));
        if (SRPConfigSystems.useOneMind && source.getEntity() instanceof LivingEntity && ParasiteEventEntity.alertOthers(this, (LivingEntity)source.getEntity(), this.level(), 7)) {
            SRPPotions.applySense((LivingEntity)this, 400, this.distanceToSqr(source.getEntity()), SRPConfigSystems.oneMinRangeCap);
        }
        if (source.is(DamageTypes.MAGIC) && this.hasEffect(MobEffects.POISON) && amount == 1.0f) {
            this.heal(this.genePoisonHealing);
            return false;
        }
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.IN_WALL)) {
            return super.hurt(source, amount);
        }
        if ((source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.ON_FIRE)) && this.getRandom().nextDouble() < this.chanceLearnFire) {
            this.onFireA = SRPConfig.fireTickWindow;
        }
        Entity immediate = source.getDirectEntity();
        Entity trueSrc = source.getEntity();
        float times = immediate instanceof LivingEntity ? (immediate instanceof Player ? (!(stack = ((Player)immediate).getItemBySlot(EquipmentSlot.MAINHAND)).isEmpty() && stack.getItem().builtInRegistryHolder().key().location() != null ? (float)this.hasResistance(stack.getItem().builtInRegistryHolder().key().location().toString(), (byte)1) : (float)this.hasResistance(source.getMsgId(), (byte)2)) : ((key = BuiltInRegistries.ENTITY_TYPE.getKey(immediate.getType())) == null ? (float)this.hasResistance(source.getMsgId(), (byte)2) : (float)this.hasResistance(key.toString(), (byte)0))) : (trueSrc instanceof LivingEntity ? (trueSrc instanceof Player ? (!(stack = ((Player)trueSrc).getItemBySlot(EquipmentSlot.MAINHAND)).isEmpty() && stack.getItem().builtInRegistryHolder().key().location() != null ? (float)this.hasResistance(stack.getItem().builtInRegistryHolder().key().location().toString(), (byte)1) : (float)this.hasResistance(source.getMsgId(), (byte)2)) : ((key = BuiltInRegistries.ENTITY_TYPE.getKey(trueSrc.getType())) == null ? (float)this.hasResistance(source.getMsgId(), (byte)2) : (float)this.hasResistance(key.toString(), (byte)0))) : (float)this.hasResistance(source.getMsgId(), (byte)2));
        float bonus = times * this.pointReduction * amount;
        float amountTwo = amount;
        amount = Math.max(amount - bonus * this.adaptationCap, 0.0f);
        this.damageAmountReduced = amountTwo - amount;
        if (bonus != 0.0f && amount != 0.0f) {
            this.particleStatus((byte)9);
            this.particleStatus((byte)9);
        }
        if (amount == 0.0f) {
            this.particleStatus((byte)9);
            this.particleStatus((byte)9);
        }
        boolean flag = super.hurt(source, amount);
        SoundEvent sound = SRPSounds.MOBSILENCE.get();
        if (flag) {
            switch (this.getHitStatus()) {
                case 1: {
                    sound = SRPSounds.ADAPTATION_P.get();
                    break;
                }
                case 2: {
                    sound = SRPSounds.ADAPTATION_F.get();
                    break;
                }
                case 3: {
                    sound = SRPSounds.CYST_EATING.get();
                }
            }
        }
        if (sound.equals(SRPSounds.MOBSILENCE.get())) {
            this.playSound(sound, this.getSoundVolume(), (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2f + 1.0f);
        }
        return flag;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || this.level().isClientSide) {
            return super.mobInteract(player, hand);
        }
        Item wea = player.getItemBySlot(EquipmentSlot.MAINHAND).getItem();
        if (wea == SRPItems.itembase.get()) {
            StringBuilder out = new StringBuilder("Current adaptation for " + Objects.requireNonNull(BuiltInRegistries.ENTITY_TYPE.getKey(this.getType())) + " (Damage type, points): ");
            for (int i = 0; i < this.resistanceS.size(); ++i) {
                out.append("[").append(this.resistanceS.get(i)).append(", ").append(this.resistanceI.get(i)).append("] ");
            }
            player.sendSystemMessage(Component.literal(out.toString()));
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean doHurtTarget(@Nonnull Entity entityIn) {
        if (this.borderOrb > 0) {
            return false;
        }
        return super.doHurtTarget(entityIn);
    }

    protected int hasResistance(String damage, byte type) {
        if (!this.geneAdaptation) {
            return 0;
        }
        if (this.hasEffect(SRPPotions.RES_E) || this.dead) {
            return 0;
        }
        if (this.checkList(damage, type)) {
            return 0;
        }
        if (this.DamageTypeCap <= 0) {
            return 0;
        }
        if (this.getRandom().nextDouble() < this.getChanceLearn()) {
            this.addResistance(damage);
        }
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            if (!this.resistanceS.get(i).equals(damage)) continue;
            int dama = this.resistanceI.get(i);
            this.entityData.set(HIT, (byte) ((dama <= this.pointCap ? (byte)1 : 2)));
            return Math.min(dama, this.pointCap);
        }
        return 0;
    }

    private double getChanceLearn() {
        double bonus = 0.0;
        if (SRPConfig.adaptationDimStrong.length > 0) {
            for (int i = 0; i < SRPConfig.adaptationDimStrong.length; ++i) {
                String[] here = SRPConfigSystems.evolutionDimStart[i].split(";");
                String dim = DimKeys.normalize(here[0]);
                double b = Double.parseDouble(here[1]);
                if (!dim.equals(DimKeys.of(this.level()))) continue;
                bonus = b;
                break;
            }
        }
        return this.chanceLearn + this.chanceLearn * bonus;
    }

    public void addResistance(String damage) {
        if (this.onFireA > 0) {
            return;
        }
        if (this.newDamageCooldown > 0) {
            return;
        }
        boolean flag = true;
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            if (!this.resistanceS.get(i).equals(damage)) continue;
            int iiii = this.resistanceI.get(i) + 1;
            this.resistanceI.set(i, iiii);
            flag = false;
            if (iiii > this.pointCap) break;
            this.particleStatus((byte)5);
            this.particleStatus((byte)5);
            break;
        }
        if (flag) {
            if (this.resistanceS.size() >= this.DamageTypeCap) {
                return;
            }
            this.resistanceS.add(damage);
            this.resistanceI.add(1);
            this.particleStatus((byte)5);
            this.particleStatus((byte)5);
        }
        this.newDamageCooldown = SRPConfig.adaptationNewDamageCooldon;
    }

    public void removeAllResistance(int value) {
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            int iiii = this.resistanceI.get(i) - value;
            if (iiii <= 0) {
                this.removeResistance(i);
                --i;
                continue;
            }
            this.resistanceI.set(i, iiii);
        }
    }

    public ArrayList<String> getResistanceS() {
        return this.resistanceS;
    }

    public ArrayList<Integer> getResistanceI() {
        return this.resistanceI;
    }

    public void copyResistancesFrom(EntityPMalleable in) {
        this.resistanceS = in.getResistanceS();
        this.resistanceI = in.getResistanceI();
    }

    public void increaseAllResistances() {
        this.resistanceI.replaceAll(integer -> integer + 1);
    }

    public void cutResistances(int in) {
        if (this.DamageTypeCap <= 0) {
            return;
        }
        this.stun += 40;
        this.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 100, false, false));
        this.setParasiteStatus(25);
        this.playSound(SRPSounds.TENDRIL.get(), 3.0f, 1.0f);
        if (SRPConfigSystems.rageEnable) {
            this.addEffect(new MobEffectInstance(SRPPotions.RAGE_E, 200, 1, false, false));
        }
        SRPPotions.applyStackPotion(SRPPotions.BLEED_E, (LivingEntity)this, 100, 0);
        if (in % 2 != 0) {
            ++in;
        }
        this.DamageTypeCap -= in;
        if (this.resistanceI.size() != this.resistanceS.size()) {
            this.resistanceI = new ArrayList();
            this.resistanceS = new ArrayList();
            this.DamageTypeCap = 0;
            return;
        }
        int size = this.resistanceS.size() - in;
        if (size <= 0) {
            this.resistanceI = new ArrayList();
            this.resistanceS = new ArrayList();
        } else {
            while (in <= 0) {
                if (this.resistanceI.isEmpty()) {
                    this.DamageTypeCap = 0;
                    return;
                }
                this.removeResistance(0);
                --in;
            }
        }
    }

    public void removeResistance(int index) {
        this.resistanceI.remove(index);
        this.resistanceS.remove(index);
    }

    public void increaseDamageCap(int in) {
        this.DamageTypeCap += in;
    }

    private boolean checkList(String damage, byte type) {
        switch (type) {
            case 0: {
                if (ParasiteEventEntity.checkName(damage, SRPConfig.damageTypeBlackListMob, SRPConfig.damageTypeBlackListWhite)) {
                    return true;
                }
            }
            case 1: {
                if (ParasiteEventEntity.checkName(damage, SRPConfig.damageTypeBlackListItem, SRPConfig.damageTypeBlackListWhite)) {
                    return true;
                }
            }
            case 2: {
                if (!ParasiteEventEntity.checkName(damage, SRPConfig.damageTypeBlackListElse, SRPConfig.damageTypeBlackListWhite)) break;
                return true;
            }
        }
        return false;
    }

    private void outOfRange(DamageSource source) {
        if (this.getTarget() == null) {
            Vec3 vec3d;
            if (source.getEntity() != null) {
                Entity sss = source.getEntity();
                if (sss instanceof Player) {
                    Player pl = (Player)sss;
                    if (pl.getAbilities().invulnerable || pl.isSpectator()) {
                        return;
                    }
                }
                if (this.hasLineOfSight(sss) && SRPPotions.applySense((LivingEntity)this, 600, this.distanceToSqr(sss), 50)) {
                    this.getNavigation().moveTo(sss.getX(), sss.getY(), sss.getZ(), 1.3);
                }
            } else if (this.getNavigation().getPath() == null && (vec3d = DefaultRandomPos.getPosAway((PathfinderMob)this, (int)16, (int)7, (Vec3)new Vec3(this.getX(), this.getY(), this.getZ()))) != null && !this.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1.3) && (vec3d = DefaultRandomPos.getPosAway((PathfinderMob)this, (int)16, (int)7, (Vec3)new Vec3(this.getX(), this.getY(), this.getZ()))) != null && !this.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1.3) && this.tickCount % 20 == 0) {
                this.getNavigation().stop();
                double dd0 = vec3d.x - this.getX();
                double dd1 = vec3d.z - this.getZ();
                float f = (float)Math.sqrt((double)(dd0 * dd0 + dd1 * dd1));
                Mot.addX(this, dd0 / (double)f * 1.0 * (double)0.8f + this.getDeltaMovement().x * (double)0.2f);
                Mot.addZ(this, dd1 / (double)f * 1.0 * (double)0.8f + this.getDeltaMovement().z * (double)0.2f);
                Mot.setY(this, 0.15);
            }
        }
    }

    public byte getHitStatus() {
        return (Byte)this.entityData.get(HIT);
    }

    public void setOrbVersionCooldown(int in) {
        this.orbVersionCooldown = in;
    }

    public String getMostCommonDamage() {
        if (this.hasEffect(SRPPotions.RES_E)) {
            return null;
        }
        String atm = null;
        int times = 0;
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            if (this.resistanceI.get(i) <= times) continue;
            times = this.resistanceI.get(i);
            atm = this.resistanceS.get(i);
        }
        return atm;
    }

    public void removeCommonDamage(String damage, int times) {
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            if (!this.resistanceS.get(i).equals(damage)) continue;
            int neww = this.resistanceI.get(i) - times;
            if (neww <= 0) {
                this.removeResistance(i);
                --i;
                continue;
            }
            this.resistanceI.set(i, neww);
        }
    }

    public boolean scaryOrbEffect(LivingEntity in, int totalMobs) {
        if (in instanceof Player) {
            Player player = (Player)in;
            if (player.getAbilities().instabuild) {
                return false;
            }
            player.causeFoodExhaustion(this.foodSteal * (float)SRPConfig.orbFoodMult);
            if (this.orbItemCool > 0) {
                ItemCooldowns track = player.getCooldowns();
                for (int i = 0; i < player.getInventory().items.size(); ++i) {
                    if (track.getCooldownPercent(((ItemStack)player.getInventory().items.get(i)).getItem(), 1.0f) != 0.0f) continue;
                    track.addCooldown(((ItemStack)player.getInventory().items.get(i)).getItem(), this.orbItemCool);
                }
            }
            this.attackEntityAsMobFood((Entity)player, false, this.foodRootNumber, this.foodRott);
        } else {
            if (in == this) {
                return false;
            }
            if (in instanceof EntityPCosmical && ((EntityPCosmical)in).getCloneC()) {
                return false;
            }
        }
        in.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 1200, 3, false, false));
        if (!(in instanceof EntityParasiteBase)) {
            this.attackEntityAsMobMinimum(in, this.getMiniDamage() * 0.5f);
        }
        SRPPotions.applyStackPotion(MobEffects.POISON, in, 100, 0);
        return true;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (!this.level().isClientSide) {
            ParasiteEventEntity.checkColony(this.level(), cause, this);
            if (this.level().random.nextInt(3) == 0 && this.isOnFire() && this.canModRender == 1) {
                this.madeRng = 0;
                this.level().broadcastEntityEvent((Entity)this, (byte)40);
            }
        }
    }

    @Override
    protected void selfExplode() {
        super.selfExplode();
        if (!this.level().isClientSide && this.isOnFire()) {
            EntityCruxB lol = new EntityCruxB(SRPEntities.CRUX_INCOMPLETE.get(), this.level());
            lol.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)lol);
        }
    }

    @Override
    protected void onDeathUpdateOG() {
        ++this.deathTime;
        if (this.deathTime == 20) {
            if (!this.level().isClientSide && (this.lastHurtByPlayerTime > 0 && this.shouldDropLoot() && this.level().getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT))) {
                int j;
                int i = this.getBaseExperienceReward();
                for (i = EventHooks.getExperienceDrop((LivingEntity)this, (Player)this.lastHurtByPlayer, (int)i); i > 0; i -= j) {
                    j = ExperienceOrb.getExperienceValue((int)i);
                    this.level().addFreshEntity((Entity)new ExperienceOrb(this.level(), this.getX(), this.getY(), this.getZ(), j));
                }
            }
            this.discard();
            if (this.level().isClientSide) {
                for (int k = 0; k < 20; ++k) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("paracolony", this.colonySpawned);
        if (this.resistanceS.size() != this.resistanceI.size()) {
            return;
        }
        ListTag allResS = new ListTag();
        ListTag allResI = new ListTag();
        for (int i = 0; i < this.resistanceS.size(); ++i) {
            String res = this.resistanceS.get(i);
            CompoundTag resT = new CompoundTag();
            resT.putString("resistance" + i, res);
            allResS.add((Tag)resT);
            int resi = this.resistanceI.get(i);
            CompoundTag resU = new CompoundTag();
            resU.putInt("resistance" + i, resi);
            allResI.add((Tag)resU);
        }
        compound.putInt("damagetypecap", this.DamageTypeCap);
        compound.put("sprresistances", (Tag)allResS);
        compound.put("sprresistancei", (Tag)allResI);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("paracolony", 99)) {
            this.colonySpawned = compound.getBoolean("paracolony");
        }
        if (compound.contains("damagetypecap", 99)) {
            this.DamageTypeCap = compound.getInt("damagetypecap");
        }
        if (compound.contains("sprresistances")) {
            ListTag allResS = compound.getList("sprresistances", 10);
            ListTag allResI = compound.getList("sprresistancei", 10);
            if (allResS.size() != allResI.size()) {
                return;
            }
            for (int i = 0; i < allResS.size(); ++i) {
                CompoundTag resT = allResS.getCompound(i);
                String res = resT.getString("resistance" + i);
                this.resistanceS.add(i, res);
                CompoundTag resU = allResI.getCompound(i);
                int resi = resU.getInt("resistance" + i);
                this.resistanceI.add(i, resi);
            }
        }
    }

    @Override
    public boolean getFinished(byte attID) {
        return attID == 21 ? this.skillOrb : super.getFinished(attID);
    }

    @Override
    public void setFinished(byte attID, boolean in) {
        if (attID == 21) {
            this.skillOrb = in;
            return;
        }
        super.setFinished(attID, in);
    }

    @Override
    public void doSpecialSkill(byte id) {
        if (id == 21) {
            this.scaryOrb();
            return;
        }
        super.doSpecialSkill(id);
    }

    private void scaryOrb() {
        if (!this.geneOrbbox) {
            this.skillOrb = true;
            return;
        }
        if (this.borderOrb == -1 || this.orbVersionCooldown > 0 && this.borderOrb == 0) {
            this.skillOrb = true;
            return;
        }
        if (this.borderOrb == 0) {
            AABB axisalignedbb = new AABB(this.getX(), this.getY(), this.getZ(), this.getX() + 1.0, this.getY() + 1.0, this.getZ() + 1.0).inflate((double)this.fuseOrb / 2.0 + 1.0);
            List<? extends EntityPMalleable> moblist = this.level().getEntitiesOfClass(EntityPMalleable.class, axisalignedbb);
            for (EntityPMalleable mob : moblist) {
                if (mob == this || mob.getParasiteType() > this.getParasiteType()) continue;
                mob.setOrbVersionCooldown(4);
            }
        }
        this.setParasiteStatus(9);
        this.getNavigation().stop();
        if (this.tickCount % 20 != 0) {
            return;
        }
        ++this.borderOrb;
        if (this.borderOrb == 1) {
            EntityOrbScary ttt = new EntityOrbScary(SRPEntities.ORBSCARY.get(), this.level(), this, this.fuseOrb, this.orbStartTimer);
            ttt.copyPosition((Entity)this);
            this.level().addFreshEntity((Entity)ttt);
            this.playSound(SRPSounds.ORB_S.get(), 1.0f, 1.0f);
        }
        if (this.borderOrb > 3) {
            this.skillOrb = true;
            this.setParasiteStatus(0);
            this.borderOrb = 0;
            this.limitOrb = 0;
        }
    }
}

