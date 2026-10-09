package com.dhanantry.scapeandrunparasites.potion;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.ItemInfestedBonemeal;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class PotionCOTH extends SRPEffectBase {
    public PotionCOTH(String name, boolean isBadEffectIn, int liquidColorIn) {
        super(name, isBadEffectIn, liquidColorIn);
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        boolean tickFlag;
        if (entity.level().isClientSide) {
            return true;
        }
        boolean flagPr = entity instanceof Player;
        if (!SRPConfigSystems.cothPlayer && flagPr) {
            return true;
        }
        CompoundTag tags = entity.getPersistentData();
        if (tags.contains("srpcothimmunity") && tags.getInt("srpcothimmunity") == 0) {
            entity.removeEffect(SRPPotions.COTH_E);
            return true;
        }
        boolean flag = !entity.level().isClientSide;
        boolean particle = false;
        int dur = entity.getEffect(SRPPotions.COTH_E).getDuration();
        if (SRPConfigSystems.disloCOTHIgnoreAmp && amplifier <= 1 && entity.tickCount % 20 == 0 && SRPSaveData.get(entity.level()).getCurrentCode(DimKeys.of(entity.level()), 0) > 0) {
            entity.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 6666, 10));
            if (tags.contains("srpcothimmunity") && tags.getInt("srpcothimmunity") != 0) {
                ParasiteEventEntity.convertEntity(entity, tags, false, SRPConfigSystems.COTHVictimParasite);
            }
        }
        if (SRPConfigSystems.COTHPopping && amplifier >= 2) {
            if (entity instanceof Player player && !(player.getHealth() > 0.0f)) {
                BlockPos pos = BlockPos.containing(player.getX(), player.getY() - 1.0, player.getZ());
                if (player.level().isEmptyBlock(pos)) {
                    return true;
                }
                ItemInfestedBonemeal.boneMealEffect(player.level(), pos, Direction.DOWN);
                player.level().playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CONVERTED, SoundSource.BLOCKS, 1.0f, 1.0f);
                player.removeEffect(SRPPotions.COTH_E);
            }
            return true;
        }
        tickFlag = entity.tickCount % 20 == 0;
        if (amplifier == 0) {
            if (flag) {
                this.effectCOTHextendDuration(dur, 200, entity, amplifier, flagPr, true, true);
            }
        } else if (flag) {
            if (tickFlag) {
                this.InfectNearby(entity, SRPConfigSystems.cothAura);
                this.effectCOTHextendDuration(dur, 200, entity, amplifier, flagPr, true, true);
            }
            particle = this.effectCOTHTransform(entity, dur, tickFlag, flagPr, amplifier == 1);
        }
        if (particle) {
            com.dhanantry.scapeandrunparasites.network.SRPSend.sendToAllPlayers(ParticlePayload.at(entity, 1));
        }
        return true;
    }

    private void effectCOTHextendDuration(int duration, int durationCheck, LivingEntity entity, int amplifier, boolean flagPr, boolean origin, boolean evoPoints) {
        if (duration < durationCheck) {
            int cothDur = 3600;
            amplifier = Math.min(amplifier + 1, 2);
            CompoundTag tags = entity.getPersistentData();
            if (flagPr) {
                entity.addEffect(SRPPotions.effect(SRPPotions.COTH_E, cothDur, amplifier));
            } else if (tags.contains("srpcothimmunity")) {
                if (tags.getInt("srpcothimmunity") != 0) {
                    entity.addEffect(SRPPotions.effect(SRPPotions.COTH_E, cothDur, amplifier));
                    if (SRPConfigWorld.originActivated && origin) {
                        ParasiteEventWorld.setOriginInHealth(entity.level(), entity.blockPosition(), (int) ((double) entity.getMaxHealth() * SRPConfigWorld.originCOTHMultiplier), true);
                    }
                    if (SRPConfigSystems.useEvolution) {
                        SRPSaveData data = SRPSaveData.get(entity.level());
                        String dim = DimKeys.of(entity.level());
                        if (evoPoints) {
                            data.setTotalKills(dim, SRPConfigSystems.valueCOTH, true, entity.level(), true, 52);
                        }
                        if (data.getEvolutionPhase(dim) >= SRPConfigSystems.evolutionAssimilatedDehiding) {
                            int key = tags.getInt("srpcothimmunity");
                            tags.putInt("srpcothimmunity", ++key);
                        }
                    }
                } else {
                    entity.removeEffect(SRPPotions.COTH_E);
                }
            } else {
                entity.removeEffect(SRPPotions.COTH_E);
            }
        }
    }

    private boolean effectCOTHTransform(LivingEntity entity, int duration, boolean tickFlag, boolean flagPr, boolean insider) {
        if (flagPr) {
            return false;
        }
        boolean flagReturn = false;
        CompoundTag tags = entity.getPersistentData();
        if (tags.contains("srpcothimmunity")) {
            int key = tags.getInt("srpcothimmunity");
            if (entity.getMaxHealth() * SRPConfigSystems.cothUnhide > entity.getHealth() && entity.getHealth() > 0.0f || key > 1) {
                flagReturn = true;
                if (insider && tickFlag) {
                    ParasiteEventEntity.spawnInsider(entity, entity.level(), tags);
                } else if (tickFlag) {
                    ParasiteEventEntity.convertEntity(entity, tags, false, SRPConfigSystems.COTHVictimParasite);
                }
            }
        } else {
            entity.removeEffect(SRPPotions.COTH_E);
        }
        return flagReturn;
    }
}
