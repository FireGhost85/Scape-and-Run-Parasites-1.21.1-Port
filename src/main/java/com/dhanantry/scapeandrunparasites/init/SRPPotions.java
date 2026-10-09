package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.potion.EffectDodSmokeTrail;
import com.dhanantry.scapeandrunparasites.potion.PotionBleed;
import com.dhanantry.scapeandrunparasites.potion.PotionCOTH;
import com.dhanantry.scapeandrunparasites.potion.PotionContamination;
import com.dhanantry.scapeandrunparasites.potion.PotionCorrosion;
import com.dhanantry.scapeandrunparasites.potion.PotionDistortedEnlightenment;
import com.dhanantry.scapeandrunparasites.potion.PotionFoster;
import com.dhanantry.scapeandrunparasites.potion.PotionNeedler;
import com.dhanantry.scapeandrunparasites.potion.PotionOverheat;
import com.dhanantry.scapeandrunparasites.potion.PotionPrey;
import com.dhanantry.scapeandrunparasites.potion.PotionSpotted;
import com.dhanantry.scapeandrunparasites.potion.SRPEffectBase;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Mob effects (<code>_E</code>) and the brewing potions (<code>_P</code>) of SRP 1.10.9. Constant names and registry names are unchanged.
 * Call {@link #register(IEventBus)} from the mod constructor.
 */
public final class SRPPotions {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, ScapeAndRunParasites.MODID);
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, ScapeAndRunParasites.MODID);

    private static final ResourceLocation VOMIT_FOLLOW_ID = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "effect.vomit");
    private static final ResourceLocation RAGE_SPEED_ID = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "effect.rage_speed");
    private static final ResourceLocation RAGE_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "effect.rage_damage");
    private static final ResourceLocation SENS_FOLLOW_ID = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "effect.senses");

    private SRPPotions() {
    }

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
        POTIONS.register(bus);
    }

    private static DeferredHolder<MobEffect, MobEffect> effect(String name, java.util.function.Supplier<MobEffect> factory) {
        return EFFECTS.register(name, factory);
    }

    private static DeferredHolder<Potion, Potion> potion(String registryName, String potionName, DeferredHolder<MobEffect, MobEffect> effect, int duration) {
        return POTIONS.register(registryName, () -> new Potion(ScapeAndRunParasites.MODID + "." + potionName, new MobEffectInstance(effect, duration)));
    }

    public static final DeferredHolder<MobEffect, MobEffect> COTH_E = effect("coth", () -> new PotionCOTH("coth", false, 5046283));
    public static final DeferredHolder<MobEffect, MobEffect> DOD_SMOKE_TRAIL_E = effect("dod_smoke_trail", () -> new EffectDodSmokeTrail());
    public static final DeferredHolder<MobEffect, MobEffect> THORNSHADE_THORNS_E = effect("thornshade_thorns", () -> new SRPEffectBase("thornshade_thorns", false, 4333438));
    public static final DeferredHolder<MobEffect, MobEffect> FEAR_E = effect("fear", () -> new SRPEffectBase("fear", false, 0x111114));
    public static final DeferredHolder<MobEffect, MobEffect> RES_E = effect("antimall", () -> new SRPEffectBase("antimall", true, 8938092));
    public static final DeferredHolder<MobEffect, MobEffect> DISTORTED_ENLIGHTENMENT_E = effect("distorted_enlightenment", () -> new PotionDistortedEnlightenment("distorted_enlightenment", true, 8970751));
    public static final DeferredHolder<MobEffect, MobEffect> BLEED_E = effect("bleed", () -> new PotionBleed("bleed", true, 6162438));
    public static final DeferredHolder<MobEffect, MobEffect> CORRO_E = effect("corrosive", () -> new PotionCorrosion("corrosive", true, 8020058));
    public static final DeferredHolder<MobEffect, MobEffect> VIRA_E = effect("viral", () -> new SRPEffectBase("viral", true, 1270580));
    public static final DeferredHolder<MobEffect, MobEffect> VOMIT_E = effect("vomit", () -> new SRPEffectBase("vomit", false, 7498817).addAttributeModifier(Attributes.FOLLOW_RANGE, VOMIT_FOLLOW_ID, 0.9, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> RAGE_E = effect("rage", () -> new SRPEffectBase("rage", false, 16270147)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, RAGE_SPEED_ID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, amplifier -> SRPConfigSystems.rageSpeed * (double) (amplifier + 1))
            .addAttributeModifier(Attributes.ATTACK_DAMAGE, RAGE_DAMAGE_ID, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, amplifier -> SRPConfigSystems.rageDamage * (double) (amplifier + 1)));
    public static final DeferredHolder<MobEffect, MobEffect> EPEL_E = effect("repel", () -> new SRPEffectBase("repel", false, 4434992));
    public static final DeferredHolder<MobEffect, MobEffect> SENS_E = effect("senses", () -> new SRPEffectBase("senses", false, 9346775).addAttributeModifier(Attributes.FOLLOW_RANGE, SENS_FOLLOW_ID, 0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    public static final DeferredHolder<MobEffect, MobEffect> PREY_E = effect("prey", () -> new PotionPrey("prey", true, 4800055));
    public static final DeferredHolder<MobEffect, MobEffect> DEBAR_E = effect("debar", () -> new SRPEffectBase("debar", false, 10359627));
    public static final DeferredHolder<MobEffect, MobEffect> DLER_E = effect("needler", () -> new PotionNeedler("needler", false, 13086595));
    public static final DeferredHolder<MobEffect, MobEffect> FOSTER_E = effect("foster", () -> new PotionFoster("foster", false, 5804908));
    public static final DeferredHolder<MobEffect, MobEffect> LINK_E = effect("link", () -> new SRPEffectBase("link", false, 16741781));
    public static final DeferredHolder<MobEffect, MobEffect> PIVOT_E = effect("pivot", () -> new SRPEffectBase("pivot", false, 16757187));
    public static final DeferredHolder<MobEffect, MobEffect> JUGG_E = effect("jugg", () -> new SRPEffectBase("jugg", false, 12433541));
    public static final DeferredHolder<MobEffect, MobEffect> PARATE_E = effect("parate", () -> new SRPEffectBase("parate", false, 11753270));
    public static final DeferredHolder<MobEffect, MobEffect> KILLPRI_E = effect("primitive", () -> new SRPEffectBase("primitive", false, 9391173));
    public static final DeferredHolder<MobEffect, MobEffect> KILLADA_E = effect("adapted", () -> new SRPEffectBase("adapted", false, 8345678));
    public static final DeferredHolder<MobEffect, MobEffect> KILLPUR_E = effect("pure", () -> new SRPEffectBase("pure", false, 894258));
    public static final DeferredHolder<MobEffect, MobEffect> KILLCRU_E = effect("crude", () -> new SRPEffectBase("crude", false, 894258));
    public static final DeferredHolder<MobEffect, MobEffect> KILLFER_E = effect("feral", () -> new SRPEffectBase("feral", false, 0x993030));
    public static final DeferredHolder<MobEffect, MobEffect> KILLNEX_E = effect("nexus", () -> new SRPEffectBase("nexus", false, 0x487848));
    public static final DeferredHolder<MobEffect, MobEffect> SPOT_E = effect("spotted", () -> new PotionSpotted("spotted", false, 8149607));
    public static final DeferredHolder<MobEffect, MobEffect> BRAINING_E = effect("braining", () -> new SRPEffectBase("braining", false, 7958149));
    public static final DeferredHolder<MobEffect, MobEffect> NOVISION_E = effect("novision", () -> new SRPEffectBase("novision", false, 1582649));
    public static final DeferredHolder<MobEffect, MobEffect> INDEAF_E = effect("indeaf", () -> new SRPEffectBase("indeaf", false, 0xFFDD00));
    public static final DeferredHolder<MobEffect, MobEffect> OVERHEATING_E = effect("overheating", () -> new PotionOverheat("overheating", true, 16746246));
    public static final DeferredHolder<MobEffect, MobEffect> CONTA_E = effect("conta", () -> new PotionContamination("conta", true, 10350848));
    public static final DeferredHolder<MobEffect, MobEffect> MUSCLEOUT_E = effect("muscleout", () -> new SRPEffectBase("muscleout", true, 15499138));
    public static final DeferredHolder<MobEffect, MobEffect> EFFECTPOS_E = effect("effectpos", () -> new SRPEffectBase("effectpos", true, 12095688));
    public static final DeferredHolder<MobEffect, MobEffect> EFFECTNEG_E = effect("effectneg", () -> new SRPEffectBase("effectneg", true, 7318708));
    public static final DeferredHolder<MobEffect, MobEffect> THE_SIGN_E = effect("the_sign", () -> new SRPEffectBase("the_sign", false, 8970751));

    public static final DeferredHolder<Potion, Potion> COTH_P = potion("coth", "coth", COTH_E, 2400);
    public static final DeferredHolder<Potion, Potion> FEAR_P = potion("fear", "fear", FEAR_E, 2400);
    public static final DeferredHolder<Potion, Potion> RES_P = potion("res", "antimall", RES_E, 2400);
    public static final DeferredHolder<Potion, Potion> CORRO_P = potion("corro", "corrosive", CORRO_E, 2400);
    public static final DeferredHolder<Potion, Potion> VIRA_P = potion("vira", "viral", VIRA_E, 2400);
    public static final DeferredHolder<Potion, Potion> VOMIT_P = potion("vomit", "vomit", VOMIT_E, 2400);
    public static final DeferredHolder<Potion, Potion> DISTORTED_ENLIGHTENMENT_P = potion("distorted_enlightenment", "distorted_enlightenment", DISTORTED_ENLIGHTENMENT_E, 900);
    public static final DeferredHolder<Potion, Potion> RAGE_P = potion("rage", "rage", RAGE_E, 2400);
    public static final DeferredHolder<Potion, Potion> EPEL_P = potion("repel", "repel", EPEL_E, 2400);
    public static final DeferredHolder<Potion, Potion> SENS_P = potion("senses", "senses", SENS_E, 2400);
    public static final DeferredHolder<Potion, Potion> DEBAR_P = potion("debar", "debar", DEBAR_E, 2400);
    public static final DeferredHolder<Potion, Potion> FOSTER_P = potion("foster", "foster", FOSTER_E, 2400);
    public static final DeferredHolder<Potion, Potion> LINK_P = potion("link", "link", LINK_E, 2400);
    public static final DeferredHolder<Potion, Potion> PIVOT_P = potion("pivot", "pivot", PIVOT_E, 2400);
    public static final DeferredHolder<Potion, Potion> JUGG_P = potion("jugg", "jugg", JUGG_E, 2400);
    public static final DeferredHolder<Potion, Potion> PARATE_P = potion("parate", "parate", PARATE_E, 2400);
    public static final DeferredHolder<Potion, Potion> KILLPRI_P = potion("primitive", "primitive", KILLPRI_E, 2400);
    public static final DeferredHolder<Potion, Potion> KILLADA_P = potion("adapted", "adapted", KILLADA_E, 2400);
    public static final DeferredHolder<Potion, Potion> KILLPUR_P = potion("pure", "pure", KILLPUR_E, 2400);
    public static final DeferredHolder<Potion, Potion> KILLCRU_P = potion("crude", "crude", KILLCRU_E, 2400);
    public static final DeferredHolder<Potion, Potion> KILLFER_P = potion("feral", "feral", KILLFER_E, 2400);
    public static final DeferredHolder<Potion, Potion> KILLNEX_P = potion("nexus", "nexus", KILLNEX_E, 2400);
    public static final DeferredHolder<Potion, Potion> SPOT_P = potion("spotted", "spotted", SPOT_E, 2400);
    public static final DeferredHolder<Potion, Potion> BRAINING_P = potion("braining", "braining", BRAINING_E, 2400);
    public static final DeferredHolder<Potion, Potion> NOVISION_P = potion("novision", "novision", NOVISION_E, 2400);
    public static final DeferredHolder<Potion, Potion> THE_SIGN_P = potion("the_sign", "the_sign", THE_SIGN_E, 2400);
    public static final DeferredHolder<Potion, Potion> INDEAF_P = potion("indeaf", "indeaf", INDEAF_E, 2400);
    public static final DeferredHolder<Potion, Potion> OVERHEATING_P = potion("overheating", "overheating", OVERHEATING_E, 2400);
    public static final DeferredHolder<Potion, Potion> CONTA_P = potion("conta", "conta", CONTA_E, 2400);
    public static final DeferredHolder<Potion, Potion> MUSCLEOUT_P = potion("muscleout", "muscleout", MUSCLEOUT_E, 2400);
    public static final DeferredHolder<Potion, Potion> EFFECTPOS_P = potion("effectpos", "effectpos", EFFECTPOS_E, 2400);
    public static final DeferredHolder<Potion, Potion> EFFECTNEG_P = potion("effectneg", "effectneg", EFFECTNEG_E, 2400);
    public static final DeferredHolder<Potion, Potion> THORNSHADE_THORNS_P = potion("thornshade_thorns", "thornshade_thorns", THORNSHADE_THORNS_E, 60);

    /** Effect instance as created by <code>new PotionEffect(effect, duration, amp, false, false)</code> in 1.12: no particles, but the HUD icon stays visible. */
    public static MobEffectInstance effect(Holder<MobEffect> effect, int duration, int amplifier) {
        return new MobEffectInstance(effect, duration, amplifier, false, false, true);
    }

    public static void applyStackPotion(Holder<MobEffect> effect, LivingEntity in, int duration, int amp) {
        if (in.level().isClientSide) {
            return;
        }
        if (amp + 1 >= 256) {
            return;
        }
        if (amp - 1 <= -256) {
            return;
        }
        MobEffectInstance flag = in.getEffect(effect);
        if (flag != null) {
            String name = effect.unwrapKey().map(key -> key.location().toString()).orElse("");
            int lockAmp = 0;
            int newDur = flag.getDuration();
            newDur = newDur + 40 <= duration ? duration : (newDur += 10);
            String[] here = new String[2];
            for (int i = 0; i < SRPConfig.stackablePotionsLimit.length; ++i) {
                here = SRPConfig.stackablePotionsLimit[i].split(";");
                if (!here[0].equals(name)) continue;
                lockAmp = Integer.parseInt(here[1]);
                if (amp <= lockAmp && flag.getAmplifier() + 1 <= lockAmp) break;
                in.addEffect(effect(effect, newDur, lockAmp));
                return;
            }
            if (flag.getAmplifier() < amp) {
                in.addEffect(effect(effect, newDur, amp));
                return;
            }
            int newAmp = flag.getAmplifier();
            newAmp = newAmp < 0 ? --newAmp : ++newAmp;
            in.addEffect(effect(effect, newDur, newAmp));
        } else {
            in.addEffect(effect(effect, duration, amp));
        }
    }

    public static boolean applySense(LivingEntity in, int duration, double rangeToCover, int withLimit) {
        return !in.level().isClientSide;
    }
}
