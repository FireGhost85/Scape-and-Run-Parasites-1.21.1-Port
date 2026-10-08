package com.dhanantry.scapeandrunparasites.init;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.block.BlockAlveoli;
import com.dhanantry.scapeandrunparasites.block.BlockAlveoliGrowth;
import com.dhanantry.scapeandrunparasites.block.BlockAshenGlass;
import com.dhanantry.scapeandrunparasites.block.BlockAssimilatedPumpkin;
import com.dhanantry.scapeandrunparasites.block.BlockAssimilatedReed;
import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockBiomassBlock;
import com.dhanantry.scapeandrunparasites.block.BlockBiomeCore;
import com.dhanantry.scapeandrunparasites.block.BlockBiomePurifier;
import com.dhanantry.scapeandrunparasites.block.BlockBloodyIce;
import com.dhanantry.scapeandrunparasites.block.BlockBuglin;
import com.dhanantry.scapeandrunparasites.block.BlockColonyCore;
import com.dhanantry.scapeandrunparasites.block.BlockColonyStructure;
import com.dhanantry.scapeandrunparasites.block.BlockDeadheadGrassShort;
import com.dhanantry.scapeandrunparasites.block.BlockDeadheadGrassTall;
import com.dhanantry.scapeandrunparasites.block.BlockDeadheadLeaves;
import com.dhanantry.scapeandrunparasites.block.BlockDermoidCyst;
import com.dhanantry.scapeandrunparasites.block.BlockDiseasedSponge;
import com.dhanantry.scapeandrunparasites.block.BlockDod;
import com.dhanantry.scapeandrunparasites.block.BlockEntityTrophy;
import com.dhanantry.scapeandrunparasites.block.BlockEpitomeInfestationWarpDiffuser;
import com.dhanantry.scapeandrunparasites.block.BlockEscaBulb;
import com.dhanantry.scapeandrunparasites.block.BlockEvolutionLure;
import com.dhanantry.scapeandrunparasites.block.BlockFallingInfestedStain;
import com.dhanantry.scapeandrunparasites.block.BlockFluid;
import com.dhanantry.scapeandrunparasites.block.BlockFogNullifier;
import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.block.BlockGothshroom;
import com.dhanantry.scapeandrunparasites.block.BlockHairFolliclePillar;
import com.dhanantry.scapeandrunparasites.block.BlockHarleskinnFence;
import com.dhanantry.scapeandrunparasites.block.BlockHarleskinnSlab;
import com.dhanantry.scapeandrunparasites.block.BlockHarleskinnStairs;
import com.dhanantry.scapeandrunparasites.block.BlockHirsuteHair;
import com.dhanantry.scapeandrunparasites.block.BlockInfestationPurifier;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedBush;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedColumn;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedFurnace;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedOre;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRemain;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedRubble;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedStain;
import com.dhanantry.scapeandrunparasites.block.BlockInfestedTrunk;
import com.dhanantry.scapeandrunparasites.block.BlockInfuserFurnace;
import com.dhanantry.scapeandrunparasites.block.BlockItemNoSurvivalCheck;
import com.dhanantry.scapeandrunparasites.block.BlockLeafLike;
import com.dhanantry.scapeandrunparasites.block.BlockLipomaMass;
import com.dhanantry.scapeandrunparasites.block.BlockLocs;
import com.dhanantry.scapeandrunparasites.block.BlockNodeLamp;
import com.dhanantry.scapeandrunparasites.block.BlockNodeRelay;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBarrier;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCactus;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanisterC;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteFog;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteLoot;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteMouth;
import com.dhanantry.scapeandrunparasites.block.BlockParasitePlank;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSapling;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteSpreading;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteThin;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteTrunk;
import com.dhanantry.scapeandrunparasites.block.BlockPottedSRPFlower;
import com.dhanantry.scapeandrunparasites.block.BlockRelay;
import com.dhanantry.scapeandrunparasites.block.BlockRelayController;
import com.dhanantry.scapeandrunparasites.block.BlockResidue;
import com.dhanantry.scapeandrunparasites.block.BlockResiduePlants;
import com.dhanantry.scapeandrunparasites.block.BlockSRPBookshelf;
import com.dhanantry.scapeandrunparasites.block.BlockSRPButton;
import com.dhanantry.scapeandrunparasites.block.BlockSRPFlower;
import com.dhanantry.scapeandrunparasites.block.BlockSRPLadder;
import com.dhanantry.scapeandrunparasites.block.BlockSRPPressurePlate;
import com.dhanantry.scapeandrunparasites.block.BlockSRPWorkbench;
import com.dhanantry.scapeandrunparasites.block.BlockSickAlveoli;
import com.dhanantry.scapeandrunparasites.block.BlockSnowCoveredGrass;
import com.dhanantry.scapeandrunparasites.block.BlockSnowGrass;
import com.dhanantry.scapeandrunparasites.block.BlockSolidAlveoli;
import com.dhanantry.scapeandrunparasites.block.BlockStairBase;
import com.dhanantry.scapeandrunparasites.block.BlockThornshade;
import com.dhanantry.scapeandrunparasites.block.BlockTressesHair;
import com.dhanantry.scapeandrunparasites.block.BlockVineBase;
import com.dhanantry.scapeandrunparasites.block.BlockWallBase;
import com.dhanantry.scapeandrunparasites.block.BlockWebBase;
import com.dhanantry.scapeandrunparasites.block.IVariantBlock;
import com.dhanantry.scapeandrunparasites.block.SRPDoor;
import com.dhanantry.scapeandrunparasites.block.SRPGlassPane;
import com.dhanantry.scapeandrunparasites.block.SRPMaterial;
import com.dhanantry.scapeandrunparasites.block.SRPTrapDoor;
import com.dhanantry.scapeandrunparasites.block.VariantBlockItem;
import com.dhanantry.scapeandrunparasites.block.slabs.BlockSlabRubble;
import com.dhanantry.scapeandrunparasites.block.slabs.BlockSlabStain;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.item.ItemBlockWithTooltip;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Registry of the SRP blocks. Registry names and constant names are the 1.12 ones. Every block gets a block item of the
 * same name, except those the original left without one (see {@link #regNoItem}). A block that was a 1.12 metadata block gets
 * one item per variant, named {@code <block>_<variant>} (the old {@code tile.srparasites.<block>_<variant>.name} keys), see
 * {@link #regVariants}. The two slab halves of the original (half and double block) are one block with the vanilla slab type.
 * The registration order is the order of the original, because stairs and walls copy the properties of their model block.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class SRPBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ScapeAndRunParasites.MODID);
    public static final DeferredRegister.Items BLOCK_ITEMS = DeferredRegister.createItems(ScapeAndRunParasites.MODID);

    /** The block states of the {@code optionalBlockDirt} and {@code optionalBlockRubble} config entries (set up after the config is read). */
    public static BlockState optionalDirt;
    public static BlockState optionalRub;

    // ---- registered first in the original (static initialiser)
    public static final DeferredBlock<Block> InfestedStain = reg("infestedstain", () -> new BlockInfestedStain(SRPMaterial.GROUND, 0.7f, true));
    public static final DeferredBlock<Block> InfestRemain = reg("infestremain", BlockInfestedRemain::new);
    public static final DeferredBlock<Block> InfestedTrunk = reg("infestedtrunk", () -> new BlockInfestedTrunk(SRPMaterial.WOOD, 2.1f, true));
    public static final DeferredBlock<Block> InfestedRubble = reg("infestedrubble", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 2.2f, true, 11.0f));
    public static final DeferredBlock<BlockInfestedBush> InfestedBush = regVariants("infestedbush", () -> new BlockInfestedBush(0.4f), BlockInfestedBush.EnumType.values());
    public static final DeferredBlock<Block> DeadheadGrassShort = reg("deadhead_grass_short", BlockDeadheadGrassShort::new);
    public static final DeferredBlock<Block> DeadheadGrassTall = reg("deadhead_grass_tall", BlockDeadheadGrassTall::new, BlockItemNoSurvivalCheck::new);
    public static final DeferredBlock<Block> BloodyIce = reg("bloodyice", () -> new BlockBloodyIce(SRPMaterial.ICE, 0.7f, true));
    public static final DeferredBlock<Block> gothShroom = reg("gothshroom", BlockGothshroom::new);

    // ---- registered by RegisterEvent<Block> in the original
    public static final DeferredBlock<Block> RELAY_CONTROLLER = reg("relaycontroller", BlockRelayController::new, ItemBlockWithTooltip::new);
    public static final DeferredBlock<Block> PARASITE_BARRIER = reg("parasite_barrier", BlockParasiteBarrier::new);
    public static final DeferredBlock<Block> BiomeHeart = reg("biomeheart", () -> new BlockBiomeCore(SRPMaterial.ROCK, 60.0f, true));
    public static final DeferredBlock<Block> ColonyHeart = reg("colonyheart", () -> new BlockColonyCore(SRPMaterial.ROCK, 60.0f, true, 2500.0f));
    public static final DeferredBlock<Block> ColonyOutpost = reg("colonyoutpost", () -> new BlockColonyCore(SRPMaterial.ROCK, 30.0f, true, 1200.0f));
    public static final DeferredBlock<Block> BiomePurifier = reg("biomepurifier", () -> new BlockBiomePurifier(SRPMaterial.SPONGE, 2.0f, true, 5.0f));
    public static final DeferredBlock<Block> DermoidCyst = reg("dermoid_cyst", BlockDermoidCyst::new);
    public static final DeferredBlock<Block> NODE_LAMP = reg("node_redstone_lamp", BlockNodeLamp::new);
    public static final DeferredBlock<Block> BiomassBlock = reg("biomass_block", BlockBiomassBlock::new);
    public static final DeferredBlock<Block> ResidueBlock = reg("residue_block", BlockResidue::new);
    public static final DeferredBlock<Block> NODE_RELAY = regNoItem("noderelay", BlockNodeRelay::new);
    public static final DeferredBlock<Block> RelayBase = reg("relay_base", BlockRelay::new, ItemBlockWithTooltip::new);
    public static final DeferredBlock<Block> RelayMiddle = reg("relay_middle", BlockRelay::new, ItemBlockWithTooltip::new);
    public static final DeferredBlock<Block> RelayRoof = reg("relay_roof", BlockRelay::new, ItemBlockWithTooltip::new);
    public static final DeferredBlock<Block> FogNullifier = reg("fog_nullifier", BlockFogNullifier::new);
    public static final DeferredBlock<Block> ResiduePlants = reg("residue_plants", BlockResiduePlants::new);
    public static final DeferredBlock<Block> InfuserFurnace = reg("infuser_furnace", BlockInfuserFurnace::new);
    public static final DeferredBlock<Block> relaycontroller_dummy = regNoItem("relay_controller_dummy", BlockRelay::new);
    public static final DeferredBlock<Block> SnowTallGrass = reg("snow_tall_grass", () -> new BlockSnowGrass(true), BlockSnowGrass.ItemSnowGrass::new);
    public static final DeferredBlock<Block> SnowShortGrass = reg("snow_short_grass", () -> new BlockSnowGrass(false), BlockSnowGrass.ItemSnowGrass::new);
    public static final DeferredBlock<Block> SnowCoveredGrass = regNoItem("snow_covered_grass", BlockSnowCoveredGrass::new);
    public static final DeferredBlock<Block> TrophyVoidOrb = reg("trophy_void_orb",
            () -> new BlockEntityTrophy(SRPMaterial.ROCK, "srparasites:orbvoid", true, BlockEntityTrophy.TrophyTextureMode.DEFAULT, true, 3.0f, 5.0f));
    public static final DeferredBlock<Block> TrophyBoomOrb = reg("trophy_boom_orb",
            () -> new BlockEntityTrophy(SRPMaterial.ROCK, "srparasites:orbboom", true, BlockEntityTrophy.TrophyTextureMode.DEFAULT, true, 3.0f, 5.0f));
    public static final DeferredBlock<BlockSRPFlower> ASSIMILATED_BLOSSOM = reg("assimilated_blossom", BlockSRPFlower::new);
    public static final DeferredBlock<BlockPottedSRPFlower> POTTED_ASSIMILATED_BLOSSOM = reg("potted_assimilated_blossom", BlockPottedSRPFlower::new);
    public static final DeferredBlock<BlockPottedSRPFlower> POTTED_CONSUMED_ASSIMILATED_BLOSSOM = reg("potted_consumed_assimilated_blossom", BlockPottedSRPFlower::new);
    public static final DeferredBlock<Block> INFESTED_FURNACE = reg("infested_furnace", BlockInfestedFurnace::new);
    public static final DeferredBlock<BlockPottedSRPFlower> INFESTED_POT = reg("infested_pot", BlockPottedSRPFlower::new);
    public static final DeferredBlock<BlockPottedSRPFlower> CONSUMED_POT = reg("consumed_pot", BlockPottedSRPFlower::new);
    public static final DeferredBlock<Block> Alveoli = reg("alveoli", BlockAlveoli::new);
    public static final DeferredBlock<Block> SickAlveoli = reg("sick_alveoli", BlockSickAlveoli::new);
    public static final DeferredBlock<Block> AlveoliGrowth = reg("alveoli_growth", BlockAlveoliGrowth::new);
    public static final DeferredBlock<Block> SolidAlveoliBlock = reg("solid_alveoli_block", BlockSolidAlveoli::new);
    public static final DeferredBlock<Block> InfestedCobblestone = reg("infested_cobblestone", () -> new BlockInfestedStain(SRPMaterial.ROCK, 2.0f, true));
    public static final DeferredBlock<BlockInfestationPurifier> InfestPurify = reg("infestation_purifier", BlockInfestationPurifier::new);
    public static final DeferredBlock<Block> EscaBulbWhite = reg("esca_bulb_white", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbOrange = reg("esca_bulb_orange", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbMagenta = reg("esca_bulb_magenta", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbLightBlue = reg("esca_bulb_light_blue", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbYellow = reg("esca_bulb_yellow", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbLime = reg("esca_bulb_lime", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbPink = reg("esca_bulb_pink", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbGray = reg("esca_bulb_gray", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbLightGray = reg("esca_bulb_light_gray", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbCyan = reg("esca_bulb_cyan", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbPurple = reg("esca_bulb_purple", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbBlue = reg("esca_bulb_blue", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbBrown = reg("esca_bulb_brown", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbGreen = reg("esca_bulb_green", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbRed = reg("esca_bulb_red", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulbBlack = reg("esca_bulb_black", BlockEscaBulb::new);
    public static final DeferredBlock<Block> EscaBulb = reg("esca_bulb", BlockEscaBulb::new);
    public static final DeferredBlock<BlockParasiteBush> ParasiteBush = regVariants("parasitebush", () -> new BlockParasiteBush(0.5f), BlockParasiteBush.EnumType.values());
    public static final DeferredBlock<BlockParasiteCanister> ParasiteCanister = regVariants("parasitecanister",
            () -> new BlockParasiteCanister(SRPMaterial.CACTUS, 0.7f, true), BlockParasiteCanister.EnumType.values());
    public static final DeferredBlock<BlockParasiteTrunk> ParasiteTrunk = regVariants("parasitetrunk",
            () -> new BlockParasiteTrunk(SRPMaterial.WOOD, 2.2f, true), BlockParasiteTrunk.EnumType.values());
    public static final DeferredBlock<BlockParasitePlank> ParasitePlank = regVariants("parasiteplank",
            () -> new BlockParasitePlank(SRPMaterial.WOOD, 2.2f, true), BlockParasitePlank.EnumType.values());
    public static final DeferredBlock<BlockParasiteLoot> ParasiteLoot = regVariants("parasiteloot",
            () -> new BlockParasiteLoot(SRPMaterial.GROUND, 3.5f, true), BlockParasiteLoot.EnumType.values());
    public static final DeferredBlock<BlockParasiteStain> ParasiteStain = regVariants("parasitestain",
            () -> new BlockParasiteStain(SRPMaterial.GROUND, 0.8f, false), BlockParasiteStain.EnumType.values());
    public static final DeferredBlock<BlockParasiteRubble> ParasiteRubble = regVariants("parasiterubble",
            () -> new BlockParasiteRubble(SRPMaterial.ROCK, 2.3f, false), BlockParasiteRubble.EnumType.values());
    public static final DeferredBlock<Block> ParasiteStructure = reg("parasitestructure", () -> new BlockColonyStructure(SRPMaterial.ROCK, 5.0f, true, 20.0f));
    public static final DeferredBlock<Block> ParasiteThin = reg("parasitethin", () -> new BlockParasiteThin(2.2f));
    public static final DeferredBlock<BlockParasiteSapling> ParasiteSapling = regVariants("parasitesapling", BlockParasiteSapling::new, BlockParasiteSapling.EnumType.values());
    public static final DeferredBlock<Block> ParasiteMouth = reg("parasitemouth", () -> new BlockParasiteMouth(SRPMaterial.CACTUS, 1.4f, true));
    public static final DeferredBlock<BlockParasiteRubbleDense> ParasiteRubbleDense = regVariants("parasiterubbledense",
            () -> new BlockParasiteRubbleDense(SRPMaterial.ROCK, 3.3f, true), BlockParasiteRubbleDense.EnumType.values());
    public static final DeferredBlock<Block> ParasiteRubbleFleshWall = reg("parasiterubble_flesh_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> InfestedPlanks = reg("infested_planks", () -> new BlockInfestedRubble(SRPMaterial.WOOD, 2.0f, true, 5.0f));
    public static final DeferredBlock<Block> InfestedStoneBricks = reg("infested_stone_bricks", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 1.5f, true, 10.0f));
    public static final DeferredBlock<Block> InfestedTerracotta = reg("infested_terracotta", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 1.25f, true, 4.2f));
    public static final DeferredBlock<Block> PolishedInfestedStone = reg("infested_stone_polished", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 1.5f, true, 10.0f));
    public static final DeferredBlock<Block> ResidueBricks = reg("residue_bricks", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 1.5f, true, 10.0f));
    public static final DeferredBlock<Block> InfestedColumn = reg("infested_column", BlockInfestedColumn::new);
    public static final DeferredBlock<Block> InfestedSandstone = reg("inf_ss", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 0.8f, true, 4.0f));
    public static final DeferredBlock<Block> InfestedSandstoneChiseled = reg("inf_ss_chiseled", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 0.8f, true, 4.0f));
    public static final DeferredBlock<Block> InfestedSandstoneCut = reg("inf_ss_cut", () -> new BlockInfestedRubble(SRPMaterial.ROCK, 0.8f, true, 4.0f));
    public static final DeferredBlock<Block> AshenGlass = reg("ashen_glass", BlockAshenGlass::new);
    public static final DeferredBlock<Block> ShroudedGlass = reg("shrouded_glass", BlockAshenGlass::new);
    public static final DeferredBlock<Block> HarlequinnGlass = reg("harlequinn_glass", BlockAshenGlass::new);
    public static final DeferredBlock<Block> BloodyGlass = reg("bloody_glass", BlockAshenGlass::new);
    public static final DeferredBlock<Block> InfestedGlass = reg("infested_glass", BlockAshenGlass::new);
    public static final DeferredBlock<Block> ShadeGlass = reg("shade_glass", BlockAshenGlass::new);
    public static final DeferredBlock<Block> SepiaGlass = reg("sepia_glass", BlockAshenGlass::new);
    public static final DeferredBlock<Block> MoodyGlass = reg("moody_glass", BlockAshenGlass::new);
    public static final DeferredBlock<SRPGlassPane> ASHEN_GLASS_PANE = reg("ashen_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<SRPGlassPane> INFESTED_GLASS_PANE = reg("infested_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<SRPGlassPane> BLOODY_GLASS_PANE = reg("bloody_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<SRPGlassPane> HARLEQUINN_GLASS_PANE = reg("harlequinn_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<SRPGlassPane> SHROUDED_GLASS_PANE = reg("shrouded_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<SRPGlassPane> SHADE_GLASS_PANE = reg("shade_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<SRPGlassPane> SEPIA_GLASS_PANE = reg("sepia_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<SRPGlassPane> MOODY_GLASS_PANE = reg("moody_glass_pane", SRPGlassPane::new);
    public static final DeferredBlock<Block> EpitomeInfestationWarpDiffuser = reg("epitome_infestation_warp_diffuser", BlockEpitomeInfestationWarpDiffuser::new);
    public static final DeferredBlock<Block> GothStem = reg("goth_stem", BlockInfestedColumn::new);
    public static final DeferredBlock<Block> AssimilatedPumpkin = reg("assimilated_pumpkin", () -> new BlockAssimilatedPumpkin(false), BlockAssimilatedPumpkin.ItemAssimilatedPumpkin::new);
    public static final DeferredBlock<Block> AssimilatedJackOLantern = reg("assimilated_jack_o_lantern", () -> new BlockAssimilatedPumpkin(true), BlockAssimilatedPumpkin.ItemAssimilatedPumpkin::new);
    public static final DeferredBlock<Block> AssimilatedSugarCane = reg("assimilated_reed", BlockAssimilatedReed::new);
    public static final DeferredBlock<Block> CookedFlesh = reg("cooked_flesh", () -> new BlockInfestedRubble(SRPMaterial.WOOD, 2.0f, false, 5.0f));
    public static final DeferredBlock<Block> CookedFleshPlanks = reg("cooked_flesh_planks", () -> new BlockInfestedRubble(SRPMaterial.WOOD, 2.0f, false, 5.0f));
    public static final DeferredBlock<Block> FleshPlanks = reg("flesh_planks", () -> new BlockInfestedRubble(SRPMaterial.WOOD, 2.0f, false, 5.0f));
    public static final DeferredBlock<Block> GothPlanks = reg("goth_planks", () -> new BlockInfestedRubble(SRPMaterial.WOOD, 2.0f, false, 5.0f));
    public static final DeferredBlock<Block> BrusewoodPlanks = reg("brusewood_planks", () -> new BlockInfestedRubble(SRPMaterial.WOOD, 2.0f, false, 5.0f));
    public static final DeferredBlock<Block> ConsumedPlanks = reg("consumed_planks", () -> new BlockInfestedRubble(SRPMaterial.WOOD, 2.0f, false, 5.0f));
    public static final DeferredBlock<Block> SemiorganicBlock = reg("semiorganic_block", () -> new BlockBase(SRPMaterial.IRON, 2.0f, false, 5.0f));
    public static final DeferredBlock<Block> InfestedButton = reg("infested_button", BlockSRPButton::new);
    public static final DeferredBlock<Block> CookedFleshButton = reg("cooked_flesh_button", BlockSRPButton::new);
    public static final DeferredBlock<Block> FleshButton = reg("flesh_button", BlockSRPButton::new);
    public static final DeferredBlock<Block> GothButton = reg("goth_button", BlockSRPButton::new);
    public static final DeferredBlock<Block> BrucewoodButton = reg("brucewood_button", BlockSRPButton::new);
    public static final DeferredBlock<Block> ConsumedButton = reg("consumed_button", BlockSRPButton::new);
    public static final DeferredBlock<Block> DeadheadButton = reg("deadhead_button", BlockSRPButton::new);
    public static final DeferredBlock<Block> GothLadder = reg("goth_ladder", BlockSRPLadder::new);
    public static final DeferredBlock<Block> FleshLadder = reg("flesh_ladder", BlockSRPLadder::new);
    public static final DeferredBlock<Block> BruisewoodLadder = reg("bruisewood_ladder", BlockSRPLadder::new);
    public static final DeferredBlock<Block> InfestedLadder = reg("infested_ladder", BlockSRPLadder::new);
    public static final DeferredBlock<Block> DeadheadLadder = reg("deadhead_ladder", BlockSRPLadder::new);
    public static final DeferredBlock<Block> CookedFleshLadder = reg("cooked_flesh_ladder", BlockSRPLadder::new);
    public static final DeferredBlock<Block> ConsumedLadder = reg("consumed_ladder", BlockSRPLadder::new);
    public static final DeferredBlock<Block> DeadheadBookshelf = reg("deadhead_bookshelf", BlockSRPBookshelf::new);
    public static final DeferredBlock<Block> CookedFleshBookshelf = reg("cooked_flesh_bookshelf", BlockSRPBookshelf::new);
    public static final DeferredBlock<Block> ConsumedBookshelf = reg("consumed_bookshelf", BlockSRPBookshelf::new);
    public static final DeferredBlock<Block> InfestedBookshelf = reg("infested_bookshelf", BlockSRPBookshelf::new);
    public static final DeferredBlock<Block> BruisewoodBookshelf = reg("bruisewood_bookshelf", BlockSRPBookshelf::new);
    public static final DeferredBlock<Block> FleshBookshelf = reg("flesh_bookshelf", BlockSRPBookshelf::new);
    public static final DeferredBlock<Block> GothBookshelf = reg("goth_bookshelf", BlockSRPBookshelf::new);
    public static final DeferredBlock<Block> DeadheadPressurePlate = reg("deadhead_pressure_plate", BlockSRPPressurePlate::new);
    public static final DeferredBlock<Block> InfestedPressurePlate = reg("infested_pressure_plate", BlockSRPPressurePlate::new);
    public static final DeferredBlock<Block> CookedFleshPressurePlate = reg("cooked_flesh_pressure_plate", BlockSRPPressurePlate::new);
    public static final DeferredBlock<Block> FleshPressurePlate = reg("flesh_pressure_plate", BlockSRPPressurePlate::new);
    public static final DeferredBlock<Block> GothPressurePlate = reg("goth_pressure_plate", BlockSRPPressurePlate::new);
    public static final DeferredBlock<Block> BrusewoodPressurePlate = reg("brusewood_pressure_plate", BlockSRPPressurePlate::new);
    public static final DeferredBlock<Block> ConsumedPressurePlate = reg("consumed_pressure_plate", BlockSRPPressurePlate::new);
    public static final DeferredBlock<Block> HarlequinnGrass = reg("harlequinn_grass", () -> new BlockParasiteSpreading(SRPMaterial.GROUND, 0.6f, false));
    public static final DeferredBlock<Block> HarleskinnBlock = reg("harleskinn_block", () -> new BlockParasiteSpreading(SRPMaterial.ROCK, 1.5f, false, 10.0f));
    public static final DeferredBlock<Block> PolandSkinBlock = reg("poland_skin_block", () -> new BlockParasiteSpreading(SRPMaterial.ROCK, 1.5f, false, 10.0f));
    public static final DeferredBlock<Block> HairFollicleBlock = reg("hair_follicle_block", () -> new BlockHairFolliclePillar(SRPMaterial.WOOD));
    public static final DeferredBlock<Block> LocsBlock = reg("locs_block", () -> new BlockLocs(SRPMaterial.CLOTH, 0.8f, true));
    public static final DeferredBlock<Block> InfestedLeaves = reg("infested_leaves", BlockLeafLike::new);
    public static final DeferredBlock<Block> InfestedLeavesFast = reg("infested_leaves_fast", BlockLeafLike::new);
    public static final DeferredBlock<Block> DeadheadLeaves = reg("deadhead_leaves", BlockDeadheadLeaves::new);
    public static final DeferredBlock<Block> HirsuteHair = reg("hirsute_hair", BlockHirsuteHair::new);
    public static final DeferredBlock<Block> TressesHair = reg("tresses_hair", BlockTressesHair::new);
    public static final DeferredBlock<Block> LipomaMass = reg("lipoma_mass", BlockLipomaMass::new);
    public static final DeferredBlock<Block> HarleskinnFence = reg("harleskinn_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> InfestedFence = reg("infested_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> DeadheadFence = reg("deadhead_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> GothFence = reg("goth_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> ConsumedFence = reg("consumed_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> BrusewoodFence = reg("bruisewood_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> CookedFleshFence = reg("cooked_flesh_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> FleshFence = reg("flesh_fence", BlockHarleskinnFence::new);
    public static final DeferredBlock<Block> ConsumedWorkbench = reg("consumed_workbench", () -> new BlockSRPWorkbench(2.5f, 12.5f));
    public static final DeferredBlock<Block> InfestedWorkbench = reg("infested_workbench", () -> new BlockSRPWorkbench(2.5f, 12.5f));
    public static final DeferredBlock<Block> ParasiteCactus = reg("infested_cactus", BlockParasiteCactus::new);

    // slabs: the half and double blocks of the original are one block (vanilla slab type)
    public static final DeferredBlock<Block> HarleskinnSlab = slab("harleskinn_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> InfestedCobblestoneSlab = slab("infested_cobblestone_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> InfestedStoneSlab = slab("infested_stone_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> InfestedDirtSlab = slab("infested_dirt_slab", 0.5f, 0.5f);
    public static final DeferredBlock<Block> ReinforcedHivestoneSlab = slab("reinforced_hivestone_slab", 3.0f, 9.0f);
    public static final DeferredBlock<Block> ParasiticColonyCoreSlab = slab("parasitic_colony_core_slab", 2.0f, 6.0f);
    public static final DeferredBlock<Block> SacOfFleshSlab = slab("sac_of_flesh_slab", 1.0f, 2.0f);
    public static final DeferredBlock<Block> DeadHeadPlankSlab = slab("dead_head_plank_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> WeatheredBricksSlab = slab("weathered_bricks_slab", 2.0f, 6.0f);
    public static final DeferredBlock<Block> ParasiticCompressedColonyStoneSlab = slab("parasitic_compressed_colony_stone_slab", 3.0f, 9.0f);
    public static final DeferredBlock<Block> WeatheredCobblestoneSlab = slab("weathered_cobblestone_slab", 2.0f, 6.0f);
    public static final DeferredBlock<Block> FrostWeatheredStoneSlab = slab("frost_weathered_stone_slab", 2.0f, 6.0f);
    public static final DeferredBlock<Block> InfestedStoneBrickSlab = slab("infested_stone_brick_slab", 2.0f, 6.0f);
    public static final DeferredBlock<Block> InfestedTerracottaSlab = slab("infested_terracotta_slab", 1.25f, 4.2f);
    public static final DeferredBlock<Block> PolishedInfestedStoneSlab = slab("polished_infested_stone_slab", 2.0f, 6.0f);
    public static final DeferredBlock<Block> ResidueBrickSlab = slab("residue_brick_slab", 2.0f, 6.0f);
    public static final DeferredBlock<Block> InfestedSandstoneSlab = slab("infested_sandstone_slab", 0.8f, 4.0f);
    public static final DeferredBlock<Block> GothPlankSlab = slab("goth_plank_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> BruisewoodPlankSlab = slab("bruisewood_plank_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> ConsumedPlankSlab = slab("consumed_plank_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> InfestedPlankSlab = slab("infested_plank_slab", 2.0f, 3.0f);
    public static final DeferredBlock<Block> PolandSkinSlab = slab("poland_skin_slab", 1.0f, 2.0f);
    public static final DeferredBlock<Block> LocsBlockSlab = slab("locs_block_slab", 1.5f, 3.0f);
    public static final DeferredBlock<Block> CookedFleshSlab = slab("cooked_flesh_slab", 1.5f, 3.0f);
    public static final DeferredBlock<Block> FleshSlab = slab("flesh_slab", 1.5f, 3.0f);

    // stairs of the harleskinn kind: all copy the harleskinn block
    public static final DeferredBlock<Block> HarleskinnStairs = harleskinnStairs("harleskinn_stairs");
    public static final DeferredBlock<Block> BruisewoodPlankStairs = harleskinnStairs("bruisewood_plank_stairs");
    public static final DeferredBlock<Block> InfestedSandstoneStairs = harleskinnStairs("infested_sandstone_stairs");
    public static final DeferredBlock<Block> GothPlanksStairs = harleskinnStairs("goth_planks_stairs");
    public static final DeferredBlock<Block> DeadheadPlankStairs = harleskinnStairs("deadhead_plank_stairs");
    public static final DeferredBlock<Block> ResidueStairs = harleskinnStairs("residue_stairs");
    public static final DeferredBlock<Block> InfestedPlanksStairs = harleskinnStairs("infested_planks_stairs");
    public static final DeferredBlock<Block> ConsumedPlanksStairs = harleskinnStairs("consumed_planks_stairs");
    public static final DeferredBlock<Block> InfestedStoneBricksStairs = harleskinnStairs("infested_stone_bricks_stairs");
    public static final DeferredBlock<Block> InfestedPolishedStoneBricksStairs = harleskinnStairs("infested_polished_stone_bricks_stairs");
    public static final DeferredBlock<Block> WheatheredBricksStairs = harleskinnStairs("wheathered_bricks_stairs");
    public static final DeferredBlock<Block> WheatheredCobblestoneStairs = harleskinnStairs("wheathered_cobblestone_stairs");
    public static final DeferredBlock<Block> FrostWeatheredStoneStairs = harleskinnStairs("frost_weathered_stone_stairs");
    public static final DeferredBlock<Block> FleshStairs = harleskinnStairs("flesh_stairs");
    public static final DeferredBlock<Block> CookedFleshStairs = harleskinnStairs("cooked_flesh_stairs");
    public static final DeferredBlock<Block> InfestedStoneStairs = harleskinnStairs("infested_stone_stairs");

    public static final DeferredBlock<SRPDoor> GothDoor = reg("goth_door", () -> new SRPDoor(SRPMaterial.WOOD), (b, p) -> new DoubleHighBlockItem(b, p));
    public static final DeferredBlock<SRPDoor> BrusewoodDoor = reg("brusewood_door", () -> new SRPDoor(SRPMaterial.WOOD), (b, p) -> new DoubleHighBlockItem(b, p));
    public static final DeferredBlock<SRPDoor> ConsumedDoor = reg("consumed_door", () -> new SRPDoor(SRPMaterial.WOOD), (b, p) -> new DoubleHighBlockItem(b, p));
    /** No block item: the thornshade berry plants it. */
    public static final DeferredBlock<Block> Thornshade = regNoItem("thornshade", BlockThornshade::new);
    public static final DeferredBlock<Block> diseasedSponge = reg("diseased_sponge", BlockDiseasedSponge::new);
    public static final DeferredBlock<Block> BrusewoodTrapdoor = reg("brusewood_trapdoor", () -> new SRPTrapDoor(SRPMaterial.WOOD));
    public static final DeferredBlock<Block> ConsumedTrapdoor = reg("consumed_trapdoor", () -> new SRPTrapDoor(SRPMaterial.IRON));

    // walls (copy the properties of their model block)
    public static final DeferredBlock<Block> ResidueBrickWall = reg("residue_wall", () -> new BlockWallBase(ParasitePlank.get()));
    public static final DeferredBlock<Block> InfestedPlankWall = reg("infested_plank_wall", () -> new BlockWallBase(ParasitePlank.get()));
    public static final DeferredBlock<Block> BruisewoodPlankWall = reg("bruisewood_plank_wall", () -> new BlockWallBase(ParasitePlank.get()));
    public static final DeferredBlock<Block> PolishedInfestedStoneWall = reg("polished_infested_stone_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> InfestedStoneBrickWall = reg("infested_stone_brick_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> GothPlankWall = reg("goth_plank_wall", () -> new BlockWallBase(ParasitePlank.get()));
    public static final DeferredBlock<Block> InfestedSandstoneWall = reg("infested_sandstone_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> ConsumedPlankWall = reg("consumed_plank_wall", () -> new BlockWallBase(ParasitePlank.get()));
    public static final DeferredBlock<Block> ParasitePlankDeadheadWall = reg("parasiteplank_deadhead_wall", () -> new BlockWallBase(ParasitePlank.get()));
    public static final DeferredBlock<Block> ParasiteRubbleWeathbWall = reg("parasiterubble_weathb_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> ParasiteRubbleDenseColonyWall = reg("parasiterubbledense_colony_wall", () -> new BlockWallBase(ParasiteRubbleDense.get()));
    public static final DeferredBlock<Block> ParasiteRubbleWeathfsWall = reg("parasiterubble_weathfs_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> ParasiteRubbleDenseBiomeWall = reg("parasiterubbledense_biome_wall", () -> new BlockWallBase(ParasiteRubbleDense.get()));
    public static final DeferredBlock<Block> ParasiteRubbleWeathbcWall = reg("parasiterubble_weathbc_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> ParasiteRubbleBricksWall = reg("parasiterubble_bricks_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> ParasiteCanisterBagWall = reg("parasitecanister_bag_wall", () -> new BlockWallBase(ParasiteCanister.get()));
    public static final DeferredBlock<Block> InfestedRubbleWall = reg("infestedrubble_wall", () -> new BlockWallBase(ParasiteRubble.get()));
    public static final DeferredBlock<Block> InfestedStainWall = reg("infestedstain_wall", () -> new BlockWallBase(ParasiteStain.get()));
    public static final DeferredBlock<Block> ParasiteStainFleshWall = reg("parasitestain_flesh_wall", () -> new BlockWallBase(ParasiteStain.get()));
    public static final DeferredBlock<Block> ParasiteRubbleMetalWall = reg("parasiterubble_metal_wall", () -> new BlockWallBase(ParasiteRubble.get()));

    // stairs of the parasite blocks: the registry name is the name of the original plus "stairs"
    public static final DeferredBlock<Block> ParasiteRubbleBoneStair = rubbleStairs("parasiterubble_bonestairs", BlockParasiteRubble.EnumType.BONE);
    public static final DeferredBlock<Block> ParasiteRubbleFleshStair = rubbleStairs("parasiterubble_fleshstairs", BlockParasiteRubble.EnumType.FLESH);
    public static final DeferredBlock<Block> ParasiteRubbleStoneStair = rubbleStairs("parasiterubble_stonestairs", BlockParasiteRubble.EnumType.STONE);
    public static final DeferredBlock<Block> ParasiteRubbleStoneDebrisStair = rubbleStairs("parasiterubble_stonedebrisstairs", BlockParasiteRubble.EnumType.STONEDEBRIS);
    public static final DeferredBlock<Block> ParasiteRubbleWoodStair = rubbleStairs("parasiterubble_woodstairs", BlockParasiteRubble.EnumType.WOOD);
    public static final DeferredBlock<Block> ParasiteRubbleBrickStair = rubbleStairs("parasiterubble_bricksstairs", BlockParasiteRubble.EnumType.BRICKS);
    public static final DeferredBlock<Block> ParasiteRubbleMetalStair = rubbleStairs("parasiterubble_metalstairs", BlockParasiteRubble.EnumType.METAL);
    public static final DeferredBlock<Block> ParasiteRubbleObsidianStair = rubbleStairs("parasiterubble_obsidianstairs", BlockParasiteRubble.EnumType.OBSIDIAN);
    public static final DeferredBlock<Block> ParasiteRubbleFungusStair = rubbleStairs("parasiterubble_fungusstairs", BlockParasiteRubble.EnumType.FUNGUS);
    public static final DeferredBlock<Block> ParasiteStainFleshStair = reg("parasitestain_fleshstairs",
            () -> new BlockStairBase(ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FLESH)));
    public static final DeferredBlock<Block> ParasiteStainDirtStair = reg("parasitestain_dirtstairs",
            () -> new BlockStairBase(ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.DIRT)));
    public static final DeferredBlock<Block> ParasiteStainMudStair = reg("parasitestain_mudstairs",
            () -> new BlockStairBase(ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.MUD)));
    public static final DeferredBlock<Block> ParasiteStainFeelerStair = reg("parasitestain_feelerstairs",
            () -> new BlockStairBase(ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, BlockParasiteStain.EnumType.FEELER)));
    public static final DeferredBlock<Block> ParasiteRubbleDenseWallStair = reg("parasiterubbledense_wallstairs",
            () -> new BlockStairBase(ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.WALL)));
    public static final DeferredBlock<Block> ParasiteRubbleDenseNodeStair = reg("parasiterubbledense_biomestairs",
            () -> new BlockStairBase(ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.BIOME)));
    public static final DeferredBlock<Block> ParasiteRubbleDenseColonyStair = reg("parasiterubbledense_colonystairs",
            () -> new BlockStairBase(ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, BlockParasiteRubbleDense.EnumType.COLONY)));
    public static final DeferredBlock<Block> ParasiteTrunkBallStair = reg("parasitetrunk_ballstairs",
            () -> new BlockStairBase(ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.BALL)));
    public static final DeferredBlock<Block> ParasiteTrunkTreeStair = reg("parasitetrunk_treestairs",
            () -> new BlockStairBase(ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.TREE)));
    public static final DeferredBlock<Block> ParasiteTrunkPlantStair = reg("parasitetrunk_plantstairs",
            () -> new BlockStairBase(ParasiteTrunk.get().defaultBlockState().setValue(BlockParasiteTrunk.VARIANT, BlockParasiteTrunk.EnumType.PLANT)));

    public static final DeferredBlock<BlockSlabRubble> ParasiteRubbleSlabHalf = regVariants("parasiterubbleslabhalf",
            () -> new BlockSlabRubble(SRPMaterial.ROCK, 2.3f), BlockSlabRubble.EnumType.values());
    public static final DeferredBlock<BlockSlabStain> ParasiteStainSlabHalf = regVariants("parasitestainslabhalf",
            () -> new BlockSlabStain(SRPMaterial.GROUND, 0.8f), BlockSlabStain.EnumType.values());
    public static final DeferredBlock<Block> ParasiteVine = reg("parasitetendril", () -> new BlockVineBase(0.5f, true));
    public static final DeferredBlock<Block> ParasiteFog = reg("parasitefog", BlockParasiteFog::new);
    public static final DeferredBlock<BlockFluid> DeadBlood = regNoItem("deadblood", () -> new BlockFluid(SRPFluids.DEADBLOOD_FLUID.get(), true));
    public static final DeferredBlock<BlockWebBase> SRPWeb = regVariants("srpweb", BlockWebBase::new, BlockWebBase.EnumType.values());
    public static final DeferredBlock<BlockEvolutionLure> evolutionLure = regVariants("evolutionlure",
            () -> new BlockEvolutionLure(SRPMaterial.ROCK, 1.0f, true), BlockEvolutionLure.EnumType.values());
    public static final DeferredBlock<Block> buglin = reg("tunnel", () -> new BlockBuglin(SRPMaterial.SPONGE, 0.1f, true, 0.1f));
    public static final DeferredBlock<BlockParasiteCanisterC> ParasiteCanisterActive = reg("canisteractive",
            () -> new BlockParasiteCanisterC(SRPMaterial.ROCK, 1.5f, true));
    public static final DeferredBlock<Block> dodN = reg("dispatchern", () -> new BlockDod(0.1f, true, 0.1f));
    public static final DeferredBlock<BlockInfestedOre> InfestedOre = regVariants("infestedore",
            () -> new BlockInfestedOre(SRPMaterial.GROUND, 3.5f, false), BlockInfestedOre.EnumType.values());
    public static final DeferredBlock<Block> InfestedSand = reg("infestedsand", () -> new BlockFallingInfestedStain(SRPMaterial.GROUND, 1.0f, true));
    public static final DeferredBlock<BlockGore> goreSim = regVariants("goresim", BlockGore::new, BlockGore.EnumType.values());
    public static final DeferredBlock<BlockGore> gorePri = regVariants("gorepri", BlockGore::new, BlockGore.EnumType.values());
    public static final DeferredBlock<BlockGore> goreAda = regVariants("goreada", BlockGore::new, BlockGore.EnumType.values());
    public static final DeferredBlock<BlockGore> gorePur = regVariants("gorepur", BlockGore::new, BlockGore.EnumType.values());
    public static final DeferredBlock<BlockGore> goreFer = regVariants("gorefer", BlockGore::new, BlockGore.EnumType.values());
    public static final DeferredBlock<BlockGore> goreMar = regVariants("goremar", BlockGore::new, BlockGore.EnumType.values());

    private SRPBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
    }

    /** A block and a block item of the same name. */
    private static <B extends Block> DeferredBlock<B> reg(String name, Supplier<? extends B> factory) {
        return reg(name, factory, (b, p) -> new BlockItem(b, p));
    }

    /** A block and its item made by {@code itemFactory}. */
    private static <B extends Block> DeferredBlock<B> reg(String name, Supplier<? extends B> factory, BiFunction<? super B, Item.Properties, ? extends Item> itemFactory) {
        DeferredBlock<B> holder = BLOCKS.register(name, factory);
        BLOCK_ITEMS.register(name, () -> itemFactory.apply(holder.get(), new Item.Properties()));
        return holder;
    }

    /** A block the original left without a block item. */
    private static <B extends Block> DeferredBlock<B> regNoItem(String name, Supplier<? extends B> factory) {
        return BLOCKS.register(name, factory);
    }

    /**
     * A block that was a 1.12 metadata block: one {@link VariantBlockItem} per variant named {@code <block>_<variant>}; the
     * item of the default variant is the one {@code Block.asItem()} returns.
     */
    private static <B extends Block & IVariantBlock<E>, E extends Enum<E> & StringRepresentable> DeferredBlock<B> regVariants(String name, Supplier<? extends B> factory, E[] values) {
        DeferredBlock<B> holder = BLOCKS.register(name, factory);
        for (E value : values) {
            String path = name + "_" + value.getSerializedName();
            BLOCK_ITEMS.register(path, () -> {
                B block = holder.get();
                EnumProperty<E> property = block.getVariantProperty();
                return new VariantBlockItem(block, property, value, path, block.defaultBlockState().getValue(property) == value, new Item.Properties());
            });
        }
        return holder;
    }

    private static DeferredBlock<Block> slab(String name, float hardness, float resistance) {
        return reg(name, () -> new BlockHarleskinnSlab(hardness, resistance));
    }

    private static DeferredBlock<Block> harleskinnStairs(String name) {
        return reg(name, () -> new BlockHarleskinnStairs(HarleskinnBlock.get().defaultBlockState()));
    }

    private static DeferredBlock<Block> rubbleStairs(String name, BlockParasiteRubble.EnumType variant) {
        return reg(name, () -> new BlockStairBase(ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, variant)));
    }

    /** {@code SRPBlocks.init()}: the optional blocks of the config, parsed once the registries and the config exist. */
    public static void init() {
        optionalDirt = BlockIds.parse(SRPConfigSystems.optionalBlockDirt, Blocks.GRAVEL.defaultBlockState());
        optionalRub = BlockIds.parse(SRPConfigSystems.optionalBlockRubble, Blocks.MOSSY_COBBLESTONE.defaultBlockState());
    }

    @SubscribeEvent
    static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(SRPBlocks::init);
    }
}
