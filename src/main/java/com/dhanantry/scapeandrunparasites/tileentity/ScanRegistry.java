package com.dhanantry.scapeandrunparasites.tileentity;

import com.dhanantry.scapeandrunparasites.item.ItemModule;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

/** The scan profiles of the relay modules: which tiers (entity ids) each module counts (TileEntityRelayController.ScanRegistry of 1.10.9). */
public final class ScanRegistry {
        private static boolean INIT = false;
        private static final Map<String, Tier> TIERS = new LinkedHashMap<String, Tier>();
        private static final Map<ItemModule.Kind, ModuleProfile> MODULES = new EnumMap<ItemModule.Kind, ModuleProfile>(ItemModule.Kind.class);

        static void ensureInit() {
            if (INIT) {
                return;
            }
            INIT = true;
            Tier INBORN = ScanRegistry.tier("INBORN");
            Tier ASSIMILATED = ScanRegistry.tier("ASSIMILATED");
            Tier HIJACKED = ScanRegistry.tier("HIJACKED");
            Tier FERAL = ScanRegistry.tier("FERAL");
            Tier CRUDE = ScanRegistry.tier("CRUDE");
            Tier PRIMITIVE = ScanRegistry.tier("PRIMITIVE");
            Tier ADAPTED = ScanRegistry.tier("ADAPTED");
            Tier NEXUS = ScanRegistry.tier("NEXUS");
            Tier DETERRENT = ScanRegistry.tier("DETERRENT");
            Tier PURE = ScanRegistry.tier("PURE");
            Tier PREEMINENT = ScanRegistry.tier("PREEMINENT");
            Tier ANCIENT = ScanRegistry.tier("ANCIENT");
            Tier ASSIMARA = ScanRegistry.tier("ASSIMARA");
            Tier DERIVED = ScanRegistry.tier("DERIVED");
            String MOD = "srparasites";
            Function<String, ResourceLocation> rl = path -> ResourceLocation.fromNamespaceAndPath("srparasites", path);
            ASSIMILATED.addIds(rl.apply("sim_bigspider"), rl.apply("sim_squid"), rl.apply("sim_human"), rl.apply("sim_cow"), rl.apply("sim_sheep"), rl.apply("sim_wolf"), rl.apply("sim_pig"), rl.apply("sim_villager"), rl.apply("sim_adventurer"), rl.apply("sim_horse"), rl.apply("sim_bear"), rl.apply("sim_enderman"), rl.apply("sim_dragone"), rl.apply("sim_sheephead"), rl.apply("sim_wolfhead"), rl.apply("sim_cowhead"), rl.apply("sim_pighead"), rl.apply("sim_villagerhead"), rl.apply("sim_horsehead"), rl.apply("sim_humanhead"), rl.apply("sim_endermanhead"), rl.apply("sim_dragonehead"), rl.apply("sim_adventurerhead"));
            ASSIMARA.addIds(rl.apply("mar_enderman"), rl.apply("mar_cow"), rl.apply("mar_villager"), rl.apply("mar_human"), rl.apply("mar_sheep"), rl.apply("mar_bear"));
            DERIVED.addIds(rl.apply("draconite"), rl.apply("kirin"));
            FERAL.addIds(rl.apply("fer_bear"), rl.apply("fer_cow"), rl.apply("fer_enderman"), rl.apply("fer_horse"), rl.apply("fer_human"), rl.apply("fer_pig"), rl.apply("fer_sheep"), rl.apply("fer_villager"), rl.apply("fer_wolf"));
            HIJACKED.addIds(rl.apply("hi_blaze"), rl.apply("hi_golem"), rl.apply("hi_skeleton"));
            INBORN.addIds(rl.apply("carrier_heavy"), rl.apply("carrier_light"), rl.apply("buglin"), rl.apply("carrier_flying"), rl.apply("rupter"), rl.apply("movingflesh"), rl.apply("worker"), rl.apply("mangler"), rl.apply("gnat"), rl.apply("lice"));
            DETERRENT.addIds(rl.apply("kyphosis"), rl.apply("sentry"), rl.apply("seizer"), rl.apply("worm"));
            NEXUS.addIds(rl.apply("beckon_si"), rl.apply("beckon_sii"), rl.apply("beckon_siii"), rl.apply("beckon_siv"), rl.apply("dispatcherten"), rl.apply("dispatcher_si"), rl.apply("dispatcher_sii"), rl.apply("dispatcher_siii"), rl.apply("dispatcher_siv"), rl.apply("rooterball"), rl.apply("rooter_si"), rl.apply("rooter_sii"), rl.apply("rooter_siii"), rl.apply("rooter_siv"));
            CRUDE.addIds(rl.apply("incompleteform_small"), rl.apply("incompleteform_medium"), rl.apply("host"), rl.apply("hostii"), rl.apply("heed"), rl.apply("crux"), rl.apply("crux_incomplete"), rl.apply("thrall"), rl.apply("dredge"), rl.apply("airscrew"), rl.apply("carrier_worm"));
            PRIMITIVE.addIds(rl.apply("pri_longarms"), rl.apply("pri_manducater"), rl.apply("pri_reeker"), rl.apply("pri_yelloweye"), rl.apply("pri_summoner"), rl.apply("pri_bolster"), rl.apply("pri_tozoon"), rl.apply("pri_arachnida"), rl.apply("pri_devourer"), rl.apply("pri_vermin"), rl.apply("pri_viscera"), rl.apply("pri_burrower"));
            ADAPTED.addIds(rl.apply("ada_longarms"), rl.apply("ada_manducater"), rl.apply("ada_reeker"), rl.apply("ada_yelloweye"), rl.apply("ada_summoner"), rl.apply("ada_bolster"), rl.apply("ada_tozoon"), rl.apply("ada_arachnida"), rl.apply("ada_devourer"), rl.apply("ada_vermin"), rl.apply("ada_viscera"), rl.apply("ada_burrower"));
            PURE.addIds(rl.apply("overseer"), rl.apply("vigilante"), rl.apply("warden"), rl.apply("bomber_light"), rl.apply("marauder"), rl.apply("monarch"), rl.apply("grunt"));
            PREEMINENT.addIds(rl.apply("bomber_heavy"), rl.apply("wraith"), rl.apply("bogle"), rl.apply("haunter"), rl.apply("carrier_colony"), rl.apply("succor"), rl.apply("seeker"), rl.apply("architect"));
            ANCIENT.addIds(rl.apply("anc_dreadnaut"), rl.apply("anc_overlord"), rl.apply("anc_pod"), rl.apply("anc_dreadnaut_ten"));
            ScanRegistry.map(ItemModule.Kind.INBORN, ScanRegistry.profile("Inborn").add(INBORN));
            ScanRegistry.map(ItemModule.Kind.ASSIMILATED, ScanRegistry.profile("Assimilated").add(ASSIMILATED));
            ScanRegistry.map(ItemModule.Kind.HIJACKED, ScanRegistry.profile("Hijacked").add(HIJACKED));
            ScanRegistry.map(ItemModule.Kind.FERAL, ScanRegistry.profile("Feral").add(FERAL));
            ScanRegistry.map(ItemModule.Kind.CRUDE, ScanRegistry.profile("Crude").add(CRUDE));
            ScanRegistry.map(ItemModule.Kind.PRIMITIVE, ScanRegistry.profile("Primitive").add(PRIMITIVE));
            ScanRegistry.map(ItemModule.Kind.ADAPTED, ScanRegistry.profile("Adapted").add(ADAPTED));
            ScanRegistry.map(ItemModule.Kind.NEXUS, ScanRegistry.profile("Nexus").add(NEXUS));
            ScanRegistry.map(ItemModule.Kind.DETERRENT, ScanRegistry.profile("Deterrent").add(DETERRENT));
            ScanRegistry.map(ItemModule.Kind.PURE, ScanRegistry.profile("Pure").add(PURE));
            ScanRegistry.map(ItemModule.Kind.PREEMINENT, ScanRegistry.profile("Preeminent").add(PREEMINENT));
            ScanRegistry.map(ItemModule.Kind.ANCIENT, ScanRegistry.profile("Ancient").add(ANCIENT));
            ScanRegistry.map(ItemModule.Kind.ASSIMARA, ScanRegistry.profile("Assimara").add(ASSIMARA));
            ScanRegistry.map(ItemModule.Kind.DERIVED, ScanRegistry.profile("Derived").add(DERIVED));
            ScanRegistry.map(ItemModule.Kind.DESMOID, ScanRegistry.profile("Desmoid").add(INBORN, ASSIMARA, ASSIMILATED, HIJACKED));
            ScanRegistry.map(ItemModule.Kind.ESCHAR, ScanRegistry.profile("Eschar").add(FERAL, CRUDE, PRIMITIVE));
            ScanRegistry.map(ItemModule.Kind.RESISTANCE, ScanRegistry.profile("Resistance").add(ADAPTED, NEXUS, DETERRENT));
            ScanRegistry.map(ItemModule.Kind.IDEAL, ScanRegistry.profile("Ideal").add(PURE, PREEMINENT, DERIVED, ANCIENT));
            ScanRegistry.map(ItemModule.Kind.ORIGIN, ScanRegistry.profile("Origin").add(INBORN, ASSIMARA, ASSIMILATED, HIJACKED, FERAL, CRUDE, PRIMITIVE, ADAPTED, NEXUS, DETERRENT, PURE, PREEMINENT, DERIVED, ANCIENT));
        }

        private static Tier tier(String id) {
            Tier t = new Tier(id);
            TIERS.put(id, t);
            return t;
        }

        private static ModuleProfile profile(String name) {
            return new ModuleProfile(name);
        }

        private static void map(ItemModule.Kind kind, ModuleProfile mp) {
            MODULES.put(kind, mp);
        }

        @Nullable
        public static ModuleProfile getProfileFor(ItemModule.Kind kind) {
            ScanRegistry.ensureInit();
            return MODULES.get((Object)kind);
        }

        public static Collection<Tier> getAllTiers() {
            ScanRegistry.ensureInit();
            return Collections.unmodifiableCollection(TIERS.values());
        }

        public static final class ModuleProfile {
            public final String name;
            public final LinkedHashSet<Tier> tiers = new LinkedHashSet();

            ModuleProfile(String name) {
                this.name = name;
            }

            ModuleProfile add(Tier ... ts) {
                Collections.addAll(this.tiers, ts);
                return this;
            }
        }

        public static final class Tier {
            public final String id;
            private final Set<ResourceLocation> entityIds = new LinkedHashSet<ResourceLocation>();
            final String langKey;
            final Set<ResourceLocation> ids = new LinkedHashSet<ResourceLocation>();

            Tier(String id) {
                this.id = id;
                this.langKey = "tier.srparasites." + id.toLowerCase(Locale.ROOT);
            }

            public String langKey() {
                return this.langKey;
            }

            public String getDisplayName() {
                return net.minecraft.network.chat.Component.translatable(this.langKey).getString();
            }

            void addIds(ResourceLocation ... rs) {
                Collections.addAll(this.ids, rs);
                Collections.addAll(this.entityIds, rs);
            }

            boolean matches(net.minecraft.world.entity.LivingEntity e) {
                ResourceLocation key = net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(e.getType());
                return key != null && this.ids.contains(key);
            }

            public Set<ResourceLocation> getEntityIds() {
                return Collections.unmodifiableSet(this.entityIds);
            }

            public String getIdLower() {
                return this.id == null ? "" : this.id.toLowerCase(Locale.ROOT);
            }
        }
    }
