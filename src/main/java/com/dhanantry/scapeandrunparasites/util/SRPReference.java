package com.dhanantry.scapeandrunparasites.util;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.init.SRPBiomes;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasiteBase;
import java.util.Random;

public class SRPReference {
    public static final String MOD_ID = "srparasites";
    public static final String NAME = "Scape and Run Parasites";
    public static final String VERSION = "1.10.9";
    public static final String ACCEPTED_VERSIONS = "[1.12.2]";
    public static final String CLIENT_PROXY_CLASS = "com.dhanantry.scapeandrunparasites.proxy.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.dhanantry.scapeandrunparasites.proxy.CommonProxy";
    public static final int SHYCO_ID = 1;
    public static final int DORPA_ID = 2;
    public static final int RATHOL_ID = 3;
    public static final int EMANA_ID = 4;
    public static final int LODO_ID = 5;
    public static final int INFHUMAN_ID = 6;
    public static final int HULL_ID = 7;
    public static final int CARNA_ID = 8;
    public static final int ALAFHA_ID = 9;
    public static final int NOGLA_ID = 10;
    public static final int BUTHOL_ID = 11;
    public static final int MUDO_ID = 12;
    public static final int INFCOW_ID = 13;
    public static final int INFSHEEP_ID = 14;
    public static final int INFWOLF_ID = 15;
    public static final int VENKROL_ID = 16;
    public static final int BANO_ID = 17;
    public static final int VENKROLSII_ID = 18;
    public static final int VENKROLSIII_ID = 19;
    public static final int TERLA_ID = 20;
    public static final int INFWOLFHEAD_ID = 21;
    public static final int INFSHEEPHEAD_ID = 22;
    public static final int LESH_ID = 23;
    public static final int ORONCO_ID = 24;
    public static final int ANGED_ID = 25;
    public static final int INFPIG_ID = 26;
    public static final int INFVILLAGER_ID = 27;
    public static final int INFCOWHEAD_ID = 28;
    public static final int TONRO_ID = 29;
    public static final int UNVO_ID = 30;
    public static final int INFPIGHEAD_ID = 31;
    public static final int INFVILLAGERHEAD_ID = 32;
    public static final int GANRO_ID = 33;
    public static final int ANCIENTPOD_ID = 34;
    public static final int ORONCOTEN_ID = 35;
    public static final int KOL_ID = 36;
    public static final int WYMO_ID = 37;
    public static final int RANRAC_ID = 38;
    public static final int INHOOS_ID = 39;
    public static final int INFPLAYER_ID = 40;
    public static final int VENKROLSIV_ID = 41;
    public static final int VENKROLSV_ID = 42;
    public static final int INHOOM_ID = 43;
    public static final int INFHORSE_ID = 44;
    public static final int INFHORSEHEAD_ID = 45;
    public static final int INFHUMANHEAD_ID = 46;
    public static final int OMBOO_ID = 47;
    public static final int HOST_ID = 48;
    public static final int INFBEAR_ID = 49;
    public static final int ESOR_ID = 50;
    public static final int GIM_ID = 317;
    public static final int ZAA_ID = 318;
    public static final int DONE_ID = 319;
    public static final int CRUXB_ID = 320;
    public static final int BANO_FOCUSED_ID = 332;
    public static final int IKI_ADAPTED_ID = 333;
    public static final int SHYCO_ADAPTED_ID = 51;
    public static final int HULL_ADAPTED_ID = 52;
    public static final int CANRA_ADAPTED_ID = 53;
    public static final int NOGLA_ADAPTED_ID = 54;
    public static final int EMANA_ADAPTED_ID = 55;
    public static final int BANO_ADAPTED_ID = 56;
    public static final int WYMO_ADAPTED_ID = 57;
    public static final int RANRAC_ADAPTED_ID = 58;
    public static final int GIM_ADAPTED_ID = 315;
    public static final int ZAA_ADAPTED_ID = 316;
    public static final int INFENDERMAN_ID = 59;
    public static final int FLOG_ID = 60;
    public static final int JIN_ID = 61;
    public static final int CRUX_ID = 62;
    public static final int HEED_ID = 63;
    public static final int INFDRAGONE_ID = 64;
    public static final int JINJO_ID = 65;
    public static final int LUM_ID = 66;
    public static final int KIRIN_ID = 67;
    public static final int ORONCO_AWA_ID = 68;
    public static final int INFENDERMANHEAD_ID = 69;
    public static final int INFDRAGONEHEAD_ID = 70;
    public static final int INFPLAYERHEAD_ID = 71;
    public static final int NAK_ID = 72;
    public static final int DOD_ID = 73;
    public static final int DODT_ID = 74;
    public static final int HOSTII_ID = 75;
    public static final int NUUH_ID = 76;
    public static final int DODSII_ID = 77;
    public static final int DODSIII_ID = 78;
    public static final int DODSIV_ID = 79;
    public static final int MES_ID = 80;
    public static final int LUM_ADAPTED_ID = 81;
    public static final int SOO_ID = 82;
    public static final int SHYCO_FOCUSED_ID = 83;
    public static final int ORCH_ID = 84;
    public static final int ELVIA_ID = 85;
    public static final int LENCIA_ID = 86;
    public static final int PHEON_ID = 87;
    public static final int VESTA_ID = 88;
    public static final int FLAM_ID = 89;
    public static final int TENN_ID = 90;
    public static final int ATA_ID = 91;
    public static final int IKI_ID = 92;
    public static final int FERCOW_ID = 93;
    public static final int FERENDERMAN_ID = 94;
    public static final int FERHORSE_ID = 95;
    public static final int FERHUMAN_ID = 96;
    public static final int FERPIG_ID = 97;
    public static final int FERSHEEP_ID = 98;
    public static final int FERVILLAGER_ID = 99;
    public static final int FERWOLF_ID = 300;
    public static final int HIGOLEM_ID = 301;
    public static final int HIBLAZE_ID = 302;
    public static final int HISKELETON_ID = 303;
    public static final int GOTHOL_ID = 304;
    public static final int MOR_ID = 305;
    public static final int FERBEAR_ID = 306;
    public static final int INFSQUID_ID = 307;
    public static final int ROF_ID = 308;
    public static final int HEBLU_ID = 309;
    public static final int LEEM_ID = 310;
    public static final int LEEMSII_ID = 311;
    public static final int LEEMSIII_ID = 312;
    public static final int LEEMSIV_ID = 313;
    public static final int LEEMB_ID = 314;
    public static final int SPEENDERMAN_ID = 321;
    public static final int SPECOW_ID = 322;
    public static final int SPEVILLAGER_ID = 323;
    public static final int SPEHUMAN_ID = 324;
    public static final int ABOBODIES_ID = 325;
    public static final int ABOHEAD_ID = 326;
    public static final int QUAC_ID = 327;
    public static final int LEER_ID = 328;
    public static final int SPESHEEP_ID = 329;
    public static final int SPEBEAR_ID = 330;
    public static final int VIIN_ID = 334;
    public static final int WEBBALL_ID = 101;
    public static final int SPINEBALL_ID = 102;
    public static final int SALIVABALL_ID = 103;
    public static final int BALLBALL_ID = 104;
    public static final int ANCIENTBALL_ID = 105;
    public static final int HOMMING_ID = 106;
    public static final int ANTIINFESTED_ID = 107;
    public static final int BIOMASSPROJ_ID = 108;
    public static final int MISS_ID = 109;
    public static final int NADEBALL_ID = 110;
    public static final int PULLINGBALL_ID = 111;
    public static final int ELVIAP_ID = 112;
    public static final int LENCIAP_ID = 113;
    public static final int EFFP_ID = 331;
    public static final int METEOR_ID = 9500;
    public static final int DAMAGE_ID = 200;
    public static final int PART_ID = 201;
    public static final int TENDRIL_ID = 202;
    public static final int BOMB_ID = 203;
    public static final int GORE_ID = 204;
    public static final int BIOMASS_ID = 205;
    public static final int SCENT_ID = 206;
    public static final int TCLOUD_ID = 207;
    public static final int SOURCE_ID = 208;
    public static final int REMAIN_ID = 209;
    public static final int ORB_ID = 210;
    public static final int VOIDORB_ID = 9501;
    public static final int BOOMORB_ID = 9502;
    public static final int WAVE_ID = 211;
    public static final int NADE_ID = 212;
    public static final int WAVESHOCK_ID = 213;
    public static final int HEBLU_LIGHT_ID = 399;
    public static final int JUDGEMENT_CUT = 400;

    public static void setSimPlayerName(EntityParasiteBase in) {
        if (SRPConfig.parasiteNameTagChance <= in.level().random.nextDouble()) {
            return;
        }
        if (!SRPConfig.easterEggs) {
            return;
        }
        Random rand = new Random();
        String[] mans = new String[]{"nischhelm", "Crimson Gaming", "Nyx/Sharp", "Elsa", "yodxxx1", "Akirawav3", "Mega Mario 2000", "roguetictac", "Golden Breeze", "Jaktt", "Mu Yao Shuanglin", "GrandeMalum", "IanZeArtist", "Kaleido", "KirbyKawaii", "Rune Bruns", "DeltaVire", "Buddy2707", "BlueScug", "SpiderTato", "Mc Lovin", "EnderDream", "LORD GABE", "Star Of Night", "IO7NIZE", "HMShrub", "Rynhex", "Valentine", "The FacelessLord", "Quchekora", "giscaalexandru", "mymoon5782", "adapted pupsalot", "Ko-fi Supporter", "ExistingEevee", "Tarzan", "Mercy", "illusion_flame0555", "ozymandius", "Riot", "Ko-fi Supporter", "HiroNexus", "vircuit", "Grape Juice", "UwU Peanut", "NymbalaSergal", "XIAOLONGZHANG", "Ik\u00e9", "Nicedragonking", "TreeVirus", "Vertbois_", "Wikipedia", "LukeUCraft", "Ko-fi Supporter", "Tabcaps", "Al", "Starmitzy", "koxu_malinowy", "Kvintesenco", "Evan Wingerter", "InveeousPerson", "Zak", "Airsmoke", "Lava_Bucket", "plush", "Dutchevz", "I_eat_beans_bruh", "bruhsec", "Icehead", "Ahmed08", "FungalDragon", "JanaR\u00f6hr", "\ubabd", "BlueFFlame", "Hasuki Raven", "hybridbattery", "Sexual Grapefruit", "Guest21730qe2#8094", "ronald", "Azkadrony", "Wartin", "Spooky", "Nanja!", "RainingLamppost", "Zecabone", "Lifecraft", "Grape Juice", "LxnaAbxss", "The_Harbinger69", "bugengxinshiba", "Doomsday", "Dero", "odxico", "Salamand0r", "Zecabone", "tishaak", "Thismightbekevin", "sdbinkley", "cloudybogdan", "kazware", "Weissair", "Paul", "Level Gaming", "genericvarient", "Sovere1gn_947", "ZETA", "BigPumpkin", "viksy", "AnderHorlo", "Hurricane Lord", "Bliketod", "Fishman", "JoyDog76", "Mentalidk", "Honaaa", "SuoTheWither", "JSwizzle", "Fearthetater", "KaijuCreeper", "lee", "Bullfighter", "Razor Edge", "VeryKitty03", "Volkov 0203", "MoostachioCat", "DJAiroldi", "Nitsu", "Thekeeper", "Deference for darkness", "Skunk Stripes", "God", "Moxium", "Katy", "pacochill", "Goozmaguze", "\u00e6\u2014 \u00e5\u0090\u008d", "HotDogHero", "Samuru", "Chester Fernandez", "Icehead", "Naomi Watkins", "Luka", "TreeVirus", "drevorubec", "burners", "mrsqueazy sucks", "BlackH@", "WT", "Sam DeMonic", "GrandeMalum", "Riot 686", "Logan Perkinson", "Basti2004", "Solus Kosmes", "Cod1ng Gamer", "Emmarite battle", "Random Gamer", "CasualPrism", "Derooo", "Shadow Play", "L S", "Kartoffelbauer1000", "pickle tickle", "wen zed", "Ruin", "Nill", "The Hornstein Family", "Henry Clade", "Oxytoxy365", "ZLL2", "LoGosh J'R", "xyw00", "Perfect Storm", "Grungus", "Steve", "Leo Bodin", "None_Arcadia", "Ahm3d08", "peprika", "camkat1212", "Zipzop zewm", "shhaha", "Dr kortman", "Creeper Carts", "Moony", "coRReLL", "raptorfen", "XuYue", "JA-CHINA", "Klime65536", "Brecker Breckao", "Michael", "Birduim", "jumbledsnan 67", "Ragnar MK10", "Lava_Bucket_", "Vessel", "Heath", "\u00e5\u00ad\u0090\u00e7\u2030\u00a7 \u00e5\u02c6\u02dc", "Josh Leonard", "Blue Storm", "Worawut Pitakbood", "taibai_1728", "Janav Patel", "MashyPolarZX _", "something or someone", "mikolaj", "Gabriel Idrovo", "tery232552", "RealRay_OG", "DEDE 4X", "Naruu1104", "Carboncillo", "Lance Rosenbaum", "\u00d0\u00a2\u00d0\u00b8\u00d0\u00bc\u00d1\u0192\u00d1\u20ac \u00d0\u203a\u00d0\u00b0\u00d1\u201a\u00d1\u2039\u00d0\u00bf\u00d0\u00be\u00d0\u00b2", "LegendaryNeo", "functions", "radikali", "citizern", "jjzr", "Nikolas Scholz", "Alejandro", "Billions must practice", "stork", "teuer8", "OrobusPrince", "Jackson Gordon", "Snap-man", "Igu", "Maple", "sandwhitch king", "Tyler Mulrooney", "xiao_hao5r0", "the spanish inquisition", "meme list", "Sylrack", "Joshua Guerrero", "Timo Tikun", "MarcoClothingStore", "ChoJoe", "Daniel Hatfield", "Purple Snake Snak", "DLKareless", "Jaktt1337", "Pyrite", "Fierylion", "SaintedYapper", "Unknown Kmari", "Jacques John", "ShadowyIce", "Shadin Mohamed", "bmss99", "E G", "Blixer [WR]", "Vector Zlezdekof", "Kirby_Kawaii", "Ancient Overlord", "VectorAce", "Dovah", "Null", "Sylvia", "fierin plays", "Selene", "Triang_L", "Iustin Nica", "Astolfo Mutsuki", "Trayn_Shadow", "Hoshea", "Jeriel Santiago", "christian talese", "Bruno Roccasalva", "El par\u00e1sito", "Jeka Potato", "cboss999", "Liam Whittier", "LilithEECS", "Nymbala", "odxico co", "Blue", "shar0makai", "Pecora", "Dhannatry", "Bilebirda", "Sereath", "Dr. Wasabi", "Yu_Zhi_Zhong_Xing", "Boogie Zombie"};
        if (in.level().random.nextInt(3) == 0) {
            mans = new String[]{"JohnIsNotImpressed", "Cephelo", "Sansrriorar", "a cats", "LoneGuy", "Robert 25", "ZETA", "Zanity", "LazyKitch", "EnderDream", "DanieloXXelo", "ABucketOfFriedChicken", "Sweebow", "0zymandiu5", "john parasite"};
        }
        SRPEntityUtil.setCustomNameTag(in, mans[rand.nextInt(mans.length)]);
        in.setCustomNameVisible(true);
    }

    public static void setSimPlayerNameSub1(EntityParasiteBase in) {
        if (SRPConfig.parasiteNameTagChance <= in.level().random.nextDouble()) {
            return;
        }
        if (!SRPConfig.easterEggs) {
            return;
        }
        Random rand = new Random();
        String[] mans = new String[]{"KirbyKawaii", "LukeUCraft", "Starmitzy", "HiroNexus", "Nicedragonking", "koxu_malinowy", "MaysiKitti", "Jaktt", "Rynhex", "Alstraire", "SpiderTato", "XIAOLONGZHANG", "Airsmoke", "Tarzan", "Lava_Bucket", "plush", "Ik\ufffd", "The FacelessLord", "Tabcaps", "Crimson Gaming", "Elsa", "yodxxx1", "HMShrub", "Dutchevz", "I_eat_beans_bruh", "bruhsec", "Icehead", "Ahmed08", "Fifermon", "Golden Breeze", "ppie00201221", "NymbalaSergal", "BlueFFlame", "Hasuki Raven", "hybridbattery", "Guest21730qe2#8094", "DeltaVire", "Spooky", "Nanja!", "Zecabone", "Lifecraft", "Grape Juice", "bugengxinshiba", "tishaak", "Weissair", "Hurricane Lord", "Bliketod", "EnderDream"};
        if (in.level().random.nextInt(3) == 0) {
            mans = new String[]{"JohnIsNotImpressed", "Cephelo", "Sansrriorar", "a cats", "LoneGuy", "Robert 25", "ZETA", "Zanity", "LazyKitch", "DanieloXXelo", "ABucketOfFriedChicken", "Sweebow", "0zymandiu5", "john parasite"};
        }
        SRPEntityUtil.setCustomNameTag(in, mans[rand.nextInt(mans.length)]);
        in.setCustomNameVisible(true);
    }

    public static BiomeParasiteBase getBiomeFromInt(int in) {
        switch (in) {
            case 3: {
                return SRPBiomes.biomeHarlequin;
            }
        }
        return SRPBiomes.biomeShrouded;
    }
}

