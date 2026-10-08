package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.block.BlockThornshade;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Infested bone meal: converts the blocks within 3 blocks of the clicked one into their infested counterparts (the table
 * below, recognised by block class, tag or name) and grows infested plants / hair / lipoma on the results. Thornshades and
 * parasite saplings are grown like with bone meal.
 */
public class ItemInfestedBonemeal extends Item {
    private static final ResourceLocation RL_INFESTEDBUSH = rl("infestedbush");
    private static final ResourceLocation RL_PARASITEBUSH = rl("parasitebush");
    private static final ResourceLocation RL_TRESSES = rl("tresses_hair");
    private static final ResourceLocation RL_HIRSUTE = rl("hirsute_hair");
    private static final ResourceLocation RL_LIPOMA = rl("lipoma_mass");
    private static final ResourceLocation RL_INFESTED_STAIN = rl("infestedstain");
    private static final ResourceLocation RL_INFESTED_SAND = rl("infestedsand");
    private static final ResourceLocation RL_INFESTED_TRUNK = rl("infestedtrunk");
    private static final ResourceLocation RL_INFESTED_PLANKS = rl("infested_planks");
    private static final ResourceLocation RL_INFESTED_LEAVES = rl("infested_leaves");
    private static final ResourceLocation RL_INFESTED_STONEBRICK = rl("infested_stone_bricks");
    private static final ResourceLocation RL_INFESTED_WALL = rl("infestedrubble_wall");
    private static final ResourceLocation RL_INFESTED_FENCE = rl("infested_fence");
    private static final ResourceLocation RL_INFESTED_GLASS = rl("infested_glass");
    private static final ResourceLocation RL_INFESTED_GLASS_PANE = rl("infested_glass_pane");
    private static final ResourceLocation RL_INFESTED_RUBBLE = rl("infestedrubble");
    private static final ResourceLocation RL_INFESTED_COBBLE = rl("infested_cobblestone");
    private static final ResourceLocation RL_INFESTED_PLANKS_STAIRS = rl("infested_planks_stairs");
    private static final ResourceLocation RL_INFESTED_STONE_STAIRS = rl("infested_stone_stairs");
    private static final ResourceLocation RL_INFESTED_SANDSTONE_STAIRS = rl("infested_sandstone_stairs");
    private static final ResourceLocation RL_INFESTED_REMAIN = rl("infestedremain");
    private static final ResourceLocation RL_INF_SS = rl("inf_ss");
    private static final ResourceLocation RL_INF_SS_CHISELED = rl("inf_ss_chiseled");
    private static final ResourceLocation RL_PARASITE_RUBBLE = rl("parasiterubble");
    private static final ResourceLocation RL_INFESTED_ORE = rl("infestedore");
    private static final int RADIUS = 3;
    private static final int SAFE_SET_FLAGS = 18;

    public ItemInfestedBonemeal(String name) {
        super(new Item.Properties());
    }

    private static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath("srparasites", path);
    }

    public static boolean boneMealEffect(Level world, BlockPos pos, Direction facing) {
        boolean any = false;
        RandomSource r = world.random;
        int r2 = RADIUS * RADIUS;
        for (int dx = -RADIUS; dx <= RADIUS; ++dx) {
            for (int dy = -RADIUS; dy <= RADIUS; ++dy) {
                for (int dz = -RADIUS; dz <= RADIUS; ++dz) {
                    BlockPos p = pos.offset(dx, dy, dz);
                    if (dx * dx + dy * dy + dz * dz > r2 || world.getBlockEntity(p) != null || isLiquid(world, p)) continue;
                    BlockState s = world.getBlockState(p);
                    Block b = s.getBlock();
                    if (hasPendingTick(world, p, b) || b == Blocks.AIR) continue;
                    ResourceLocation key = BuiltInRegistries.BLOCK.getKey(b);
                    String path = key.getPath();
                    boolean converted = false;
                    Integer oreMeta = oreMetaFor(b, path);
                    if (oreMeta != null) {
                        converted = setBlockMeta(world, p, RL_INFESTED_ORE, oreMeta);
                    }
                    if (!converted && (isFlower(s, b, path) || isVine(b, path) || isCrop(b, path))) {
                        converted = setAir(world, p);
                    }
                    if (!converted) {
                        if (isGrassPath(b, path)) {
                            converted = setBlock(world, p, RL_INFESTED_SAND);
                        } else if (b == Blocks.COBBLESTONE || path.contains("cobblestone")) {
                            converted = setBlock(world, p, RL_INFESTED_COBBLE);
                        } else if (b == Blocks.FARMLAND || path.contains("farmland")) {
                            converted = setBlock(world, p, RL_INFESTED_SAND);
                        } else if (isWool(s, path)) {
                            converted = setBlockMeta(world, p, RL_PARASITE_RUBBLE, 8);
                        } else if (b == Blocks.SANDSTONE || path.contains("sandstone")) {
                            converted = isChiseledSandstone(path) ? setBlock(world, p, RL_INF_SS_CHISELED) : setBlock(world, p, RL_INF_SS);
                        } else if (isSandstoneStairs(b, path)) {
                            converted = setStairs(world, p, s, RL_INFESTED_SANDSTONE_STAIRS);
                        } else if (isWoodStairs(b, s)) {
                            converted = setStairs(world, p, s, RL_INFESTED_PLANKS_STAIRS);
                        } else if (isStoneStairs(b, s, path)) {
                            converted = setStairs(world, p, s, RL_INFESTED_STONE_STAIRS);
                        } else if (b == Blocks.STONE || path.equals("stone")) {
                            converted = setBlock(world, p, RL_INFESTED_RUBBLE);
                        } else if (b == Blocks.DIRT || path.contains("dirt")) {
                            converted = setBlock(world, p, RL_INFESTED_STAIN);
                        } else if (b == Blocks.GRASS_BLOCK) {
                            converted = setBlock(world, p, RL_INFESTED_STAIN);
                        } else if (b == Blocks.SAND) {
                            converted = setBlock(world, p, RL_INFESTED_SAND);
                        } else if (isAnyLog(s, path)) {
                            converted = setBlock(world, p, RL_INFESTED_TRUNK);
                        } else if (isAnyPlanks(s, path)) {
                            converted = setBlock(world, p, RL_INFESTED_PLANKS);
                        } else if (isAnyLeaves(s, path)) {
                            converted = setBlock(world, p, RL_INFESTED_LEAVES);
                        } else if (isAnyStoneBrick(path)) {
                            converted = setBlock(world, p, RL_INFESTED_STONEBRICK);
                        } else if (isAnyWall(b, path)) {
                            converted = setBlock(world, p, RL_INFESTED_WALL);
                        } else if (isAnyFence(b, path)) {
                            converted = setBlock(world, p, RL_INFESTED_FENCE);
                        } else if (isAnyGlassPane(b, path)) {
                            converted = setBlock(world, p, RL_INFESTED_GLASS_PANE);
                        } else if (isAnyGlassBlock(s, b, path)) {
                            converted = setBlock(world, p, RL_INFESTED_GLASS);
                        } else if (b instanceof CarpetBlock || path.contains("carpet")) {
                            converted = setBlock(world, p, RL_INFESTED_REMAIN);
                        } else if (b == Blocks.CLAY || path.contains("clay")) {
                            converted = setBlock(world, p, RL_INFESTED_SAND);
                        }
                    }
                    if (converted) {
                        smoke(world, p);
                        any = true;
                    }
                    s = world.getBlockState(p);
                    b = s.getBlock();
                    key = BuiltInRegistries.BLOCK.getKey(b);
                    String domain = key.getNamespace();
                    path = key.getPath();
                    if ("srparasites".equals(domain)) {
                        BlockPos up;
                        if (path.startsWith("infested") && !path.equals("infestedsand")) {
                            up = p.above();
                            BlockState grow = pickInfestedPlant(r);
                            if (grow != null && canPlaceClean(world, up, grow) && world.setBlock(up, grow, SAFE_SET_FLAGS)) {
                                smoke(world, up);
                                any = true;
                            }
                        }
                        if (path.startsWith("harleskinn") || path.startsWith("poland_skin_block")) {
                            if (facing == Direction.DOWN) {
                                BlockPos under = p.below();
                                BlockState lip2 = stateOf(RL_LIPOMA, 0);
                                if (lip2 != null && canPlaceClean(world, under, lip2) && world.setBlock(under, lip2, SAFE_SET_FLAGS)) {
                                    smoke(world, under);
                                    any = true;
                                }
                            } else {
                                up = p.above();
                                BlockState hair = r.nextBoolean() ? stateOf(RL_TRESSES, 0) : stateOf(RL_HIRSUTE, 0);
                                if (hair != null && canPlaceClean(world, up, hair) && world.setBlock(up, hair, SAFE_SET_FLAGS)) {
                                    smoke(world, up);
                                    any = true;
                                }
                            }
                        }
                    }
                    if (any) continue;
                    BlockState above = world.getBlockState(p.above());
                    ResourceLocation ak = BuiltInRegistries.BLOCK.getKey(above.getBlock());
                    String ad = ak.getNamespace();
                    String ap = ak.getPath();
                    BlockState lip;
                    if (!"srparasites".equals(ad) || !ap.startsWith("harleskinn") && !ap.startsWith("poland_skin_block") || (lip = stateOf(RL_LIPOMA, 0)) == null
                            || !canPlaceClean(world, p, lip) || !world.setBlock(p, lip, SAFE_SET_FLAGS)) continue;
                    smoke(world, p);
                    any = true;
                }
            }
        }
        return any;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockState clickedState = world.getBlockState(pos);
        Block clickedBlock = clickedState.getBlock();
        if (clickedBlock instanceof BlockThornshade || clickedBlock == SRPBlocks.ParasiteSapling.get()) {
            // growing works like bone meal (the event, the growth check and the particles are the vanilla ones)
            if (BoneMealItem.applyBonemeal(stack, world, pos, player)) {
                spawnSaplingUseParticles(world, pos, world.random);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        boolean any = boneMealEffect(world, pos, context.getClickedFace());
        if (any && (player == null || !player.getAbilities().instabuild)) {
            stack.shrink(1);
        }
        return any ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    private static void spawnSaplingUseParticles(Level world, BlockPos pos, RandomSource rand) {
        if (world.isClientSide) {
            return;
        }
        ServerLevel ws = (ServerLevel) world;
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.6;
        double z = pos.getZ() + 0.5;
        for (int i = 0; i < 4; ++i) {
            double ox = (rand.nextDouble() - 0.5) * 0.4;
            double oz = (rand.nextDouble() - 0.5) * 0.4;
            ws.sendParticles(ParticleTypes.LARGE_SMOKE, x + ox, y, z + oz, 1, 0.0, 0.02, 0.0, 0.0);
        }
        for (int i = 0; i < 6; ++i) {
            double ox = (rand.nextDouble() - 0.5) * 0.6;
            double oz = (rand.nextDouble() - 0.5) * 0.6;
            ws.sendParticles(ParticleTypes.HAPPY_VILLAGER, x + ox, y, z + oz, 1, 0.0, 0.02, 0.0, 0.0);
        }
    }

    private static boolean isAnyLog(BlockState s, String path) {
        return s.is(BlockTags.LOGS) || path.contains("log");
    }

    private static boolean isAnyPlanks(BlockState s, String path) {
        return s.is(BlockTags.PLANKS) || path.contains("plank");
    }

    private static boolean isAnyLeaves(BlockState s, String path) {
        return s.is(BlockTags.LEAVES) || path.contains("leaves") || path.contains("leaf");
    }

    private static boolean isAnyStoneBrick(String path) {
        return path.contains("stonebrick") || path.contains("stone_brick");
    }

    private static boolean isAnyWall(Block b, String path) {
        return b instanceof WallBlock || path.endsWith("_wall") || path.contains("wall");
    }

    private static boolean isAnyFence(Block b, String path) {
        return b instanceof FenceBlock || path.endsWith("_fence") || path.contains("fence");
    }

    private static boolean isAnyGlassBlock(BlockState s, Block b, String path) {
        return s.is(BlockTags.IMPERMEABLE) && !(b instanceof IronBarsBlock) || path.contains("glass") && !path.contains("pane");
    }

    private static boolean isAnyGlassPane(Block b, String path) {
        return b instanceof IronBarsBlock || path.contains("pane");
    }

    private static boolean isSandstoneStairs(Block b, String path) {
        return b instanceof StairBlock && path.contains("sandstone") && path.contains("stairs");
    }

    private static boolean isWoodStairs(Block b, BlockState s) {
        return b instanceof StairBlock && s.is(BlockTags.WOODEN_STAIRS);
    }

    private static boolean isStoneStairs(Block b, BlockState s, String path) {
        if (!(b instanceof StairBlock)) {
            return false;
        }
        if (isSandstoneStairs(b, path)) {
            return false;
        }
        return !s.is(BlockTags.WOODEN_STAIRS);
    }

    private static boolean isChiseledSandstone(String path) {
        return path.contains("chiseled") && path.contains("sandstone");
    }

    private static boolean isFlower(BlockState s, Block b, String path) {
        return s.is(BlockTags.FLOWERS) || path.contains("flower");
    }

    private static boolean isVine(Block b, String path) {
        return b instanceof VineBlock || path.contains("vine");
    }

    private static boolean isCrop(Block b, String path) {
        return b instanceof CropBlock || b instanceof StemBlock || b instanceof SugarCaneBlock || path.contains("wart");
    }

    private static boolean isGrassPath(Block b, String path) {
        return b == Blocks.DIRT_PATH || path.contains("grass_path") || path.endsWith("_path");
    }

    private static boolean isWool(BlockState s, String path) {
        return s.is(BlockTags.WOOL) || path.contains("wool");
    }

    @Nullable
    private static Integer oreMetaFor(Block b, String path) {
        if (b == Blocks.COAL_ORE) {
            return 0;
        }
        if (b == Blocks.DIAMOND_ORE) {
            return 1;
        }
        if (b == Blocks.EMERALD_ORE) {
            return 2;
        }
        if (b == Blocks.GOLD_ORE) {
            return 3;
        }
        if (b == Blocks.IRON_ORE) {
            return 4;
        }
        if (b == Blocks.LAPIS_ORE) {
            return 5;
        }
        if (b == Blocks.REDSTONE_ORE) {
            return 6;
        }
        if (path != null && path.contains("ore")) {
            return 7;
        }
        return null;
    }

    private static boolean setAir(Level w, BlockPos p) {
        return w.setBlock(p, Blocks.AIR.defaultBlockState(), SAFE_SET_FLAGS);
    }

    private static boolean setBlock(Level w, BlockPos p, ResourceLocation rl) {
        BlockState st = stateOf(rl, 0);
        return st != null && w.setBlock(p, st, SAFE_SET_FLAGS);
    }

    private static boolean setBlockMeta(Level w, BlockPos p, ResourceLocation rl, int meta) {
        BlockState st = stateOf(rl, meta);
        return st != null && w.setBlock(p, st, SAFE_SET_FLAGS);
    }

    private static boolean hasPendingTick(Level world, BlockPos p, Block b) {
        return world instanceof ServerLevel server && server.getBlockTicks().hasScheduledTick(p, b);
    }

    private static boolean setStairs(Level w, BlockPos p, BlockState srcStairsState, ResourceLocation targetRL) {
        BlockState tgt = stateOf(targetRL, 0);
        if (tgt == null) {
            return false;
        }
        tgt = copyPropByName(srcStairsState, tgt, "facing");
        tgt = copyPropByName(srcStairsState, tgt, "half");
        tgt = copyPropByName(srcStairsState, tgt, "shape");
        return w.setBlock(p, tgt, SAFE_SET_FLAGS);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BlockState copyPropByName(BlockState from, BlockState to, String propName) {
        Property src = findProp(from, propName);
        Property tgt = findProp(to, propName);
        if (src == null || tgt == null) {
            return to;
        }
        Comparable val = from.getValue(src);
        String name = src.getName(val);
        for (Object cand : tgt.getPossibleValues()) {
            if (!tgt.getName((Comparable) cand).equals(name)) continue;
            to = to.setValue(tgt, (Comparable) cand);
            break;
        }
        return to;
    }

    @Nullable
    private static Property<?> findProp(BlockState st, String name) {
        for (Property<?> p : st.getProperties()) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    private static boolean canPlaceClean(Level w, BlockPos p, BlockState stateToPlace) {
        if (!canReplace(w, p) || isLiquid(w, p)) {
            return false;
        }
        return stateToPlace.canSurvive(w, p);
    }

    private static boolean canReplace(Level w, BlockPos p) {
        BlockState s = w.getBlockState(p);
        return w.isEmptyBlock(p) || s.canBeReplaced();
    }

    private static boolean isLiquid(Level w, BlockPos p) {
        return !w.getFluidState(p).isEmpty();
    }

    private static void smoke(Level w, BlockPos p) {
        if (w instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.LARGE_SMOKE, p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, 6, 0.3, 0.3, 0.3, 0.01);
        }
    }

    /** The state of the block with that registry name and 1.12 meta (null if unknown). */
    @Nullable
    private static BlockState stateOf(ResourceLocation rl, int meta) {
        Block b = BuiltInRegistries.BLOCK.getOptional(rl).orElse(null);
        if (b == null) {
            return null;
        }
        return meta == 0 ? b.defaultBlockState() : BlockIds.legacyState(b, meta);
    }

    @Nullable
    private static BlockState pickInfestedPlant(RandomSource r) {
        switch (r.nextInt(5)) {
            case 0:
                return stateOf(RL_INFESTEDBUSH, 0);
            case 1:
                return stateOf(RL_INFESTEDBUSH, 1);
            case 2:
                return stateOf(RL_INFESTEDBUSH, 2);
            case 3:
                return stateOf(RL_INFESTEDBUSH, 5);
            default:
                return stateOf(RL_PARASITEBUSH, 4);
        }
    }
}
