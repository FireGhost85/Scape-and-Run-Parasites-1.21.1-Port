package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPBeckon;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.SRPResidueFireManager;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import com.mojang.serialization.MapCodec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.ItemAbilities;

/** Residue left by the parasites (infested remain): spawns Venkrol reinforcements and burns when the residue wave is flammable. */
public class BlockInfestedRemain extends BushBlock {
    protected static final VoxelShape TALL_GRASS_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
    public static final IntegerProperty SOURCE = IntegerProperty.create("source", 0, 1);
    public static final BooleanProperty INFESTED_BASE = BooleanProperty.create("infested_base");
    public static final MapCodec<BlockInfestedRemain> CODEC = MapCodec.unit(BlockInfestedRemain::new);

    public BlockInfestedRemain() {
        super(SRPMaterial.VINE.props(0.0f).sound(SRPSoundTypes.VOMIT).randomTicks().friction(0.52f).noCollission().replaceable());
        this.registerDefaultState(this.stateDefinition.any().setValue(SOURCE, 0).setValue(INFESTED_BASE, Boolean.FALSE));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SOURCE, INFESTED_BASE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return TALL_GRASS_AABB;
    }

    /** 1.12 {@code canBlockStay}; the source-0 branch compared a block with a state in the original and was never true. */
    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(SOURCE) == 1) {
            BlockPos below = pos.below();
            return level.getBlockState(below).isSolidRender(level, below);
        }
        return false;
    }

    private static boolean infestedBase(BlockGetter level, BlockPos belowPos, BlockState below) {
        Block b = below.getBlock();
        ResourceLocation rl = BuiltInRegistries.BLOCK.getKey(b);
        String domain = rl.getNamespace();
        String path = rl.getPath();
        boolean idMatch = "srparasites".equals(domain) && path.startsWith("infested");
        boolean normalGeom = below.getRenderShape() == RenderShape.MODEL && below.isSolidRender(level, belowPos);
        return idMatch && normalGeom;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos below = context.getClickedPos().below();
        return this.defaultBlockState().setValue(INFESTED_BASE, infestedBase(context.getLevel(), below, context.getLevel().getBlockState(below)));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return dir == Direction.DOWN ? state.setValue(INFESTED_BASE, infestedBase(level, neighborPos, neighbor)) : state;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        Entity harvester = params.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(harvester instanceof Player player)) {
            return List.of();
        }
        if (player.getAbilities().instabuild) {
            return List.of();
        }
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        boolean mainIsShovel = !main.isEmpty() && main.canPerformAction(ItemAbilities.SHOVEL_DIG);
        boolean offIsShovel = !off.isEmpty() && off.canPerformAction(ItemAbilities.SHOVEL_DIG);
        if (mainIsShovel || offIsShovel) {
            return List.of(new ItemStack(this));
        }
        return List.of();
    }

    private void scheduleInclineNeighbors(Level level, BlockPos pos) {
        if (level.isClientSide) {
            return;
        }
        for (Direction f : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST}) {
            BlockPos pSame = pos.relative(f);
            BlockPos pUp = pSame.above();
            BlockPos pDown = pSame.below();
            if (level.getBlockState(pSame).is(this)) {
                level.scheduleTick(pSame, this, 1);
            }
            if (isFullBlock(level, pSame) && level.getBlockState(pUp).is(this)) {
                level.scheduleTick(pUp, this, 1);
            }
            if (!level.getBlockState(pDown).is(this)) {
                continue;
            }
            level.scheduleTick(pDown, this, 1);
        }
    }

    private static boolean isFullBlock(BlockGetter level, BlockPos pos) {
        return Block.isShapeFullBlock(level.getBlockState(pos).getShape(level, pos));
    }

    private static boolean fireAround(Level level, BlockPos pos) {
        return level.getBlockState(pos.north()).is(Blocks.FIRE) || level.getBlockState(pos.south()).is(Blocks.FIRE)
                || level.getBlockState(pos.east()).is(Blocks.FIRE) || level.getBlockState(pos.west()).is(Blocks.FIRE)
                || level.getBlockState(pos.above()).is(Blocks.FIRE) || level.getBlockState(pos.below()).is(Blocks.FIRE);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (SRPConfigWorld.residueFlammableWave && fireAround(level, pos)) {
            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
            SRPResidueFireManager.lightAndTrack(level, pos, 6);
            this.scheduleInclineNeighbors(level, pos);
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.7f, 1.1f + level.random.nextFloat() * 0.2f);
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 0.6, pos.getZ() + 0.5, 1, 0.0, 0.02, 0.0, 0.0);
            return;
        }
        if (level.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if (!isFullBlock(level, pos.below())) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            return;
        }
        if (rand.nextDouble() < 0.5) {
            if (SRPBlockLinks.isParasiteBiome(level, pos)) {
                BlockParasiteSpreading.spreadBiomeBlockStain(level, pos, rand);
                return;
            }
            int heart = ParasiteEventWorld.canBiomeStillExist(level, pos, true);
            if (heart > 0) {
                BlockParasiteSpreading.SpreadBiome(level, pos, heart, ParasiteEventWorld.canBiomeStillExistType(level, pos, true));
            }
        }
        if (!SRPConfig.allowMobs) {
            return;
        }
        int count = 0;
        for (Entity e : level.getAllEntities()) {
            if (!(e instanceof EntityPBeckon) || ++count <= SRPConfig.nexusVenkrolCap && !(this.getDistanceSq(pos, e) < (double) (SRPConfig.nexusVenkrolDis * SRPConfig.nexusVenkrolDis))) {
                continue;
            }
            return;
        }
        if (SRPConfigSystems.rsResidueY <= pos.getY()) {
            if (SRPConfigSystems.useEvolution) {
                int chance = this.getEvoResidue(SRPSaveData.get(level).getEvolutionPhase(DimKeys.of(level)));
                if (chance <= 0) {
                    return;
                }
                if (rand.nextInt(chance) == 0) {
                    if (SRPConfigSystems.rsSkyResidue && !level.canSeeSky(pos)) {
                        return;
                    }
                    if (ParasiteEventEntity.getRSchance(level) > 0.0) {
                        this.spawnVenkorl(pos, level);
                    }
                }
            } else {
                if (SRPConfigSystems.rsVenkrolChance <= 0) {
                    return;
                }
                if (rand.nextInt(SRPConfigSystems.rsVenkrolChance) == 0) {
                    if (SRPConfigSystems.rsSkyResidue && !level.canSeeSky(pos)) {
                        return;
                    }
                    this.spawnVenkorl(pos, level);
                }
            }
        }
    }

    private double getDistanceSq(BlockPos pos, Entity entityIn) {
        double d0 = pos.getX() - entityIn.getX();
        double d1 = pos.getY() - entityIn.getY();
        double d2 = pos.getZ() - entityIn.getZ();
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return SRPConfigWorld.residueFlammableWave;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return SRPConfigWorld.residueFlammableWave ? 200 : 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction face) {
        return SRPConfigWorld.residueFlammableWave ? 300 : 0;
    }

    private void spawnVenkorl(BlockPos pos, ServerLevel level) {
        EntityVenkrol entityOut = SRPEntities.BECKON_SI.get().create(level);
        entityOut.setPos(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
        if (!level.noCollision(entityOut, entityOut.getBoundingBox())) {
            entityOut.discard();
            return;
        }
        level.addFreshEntity(entityOut);
        if (SRPConfigSystems.rsSounds) {
            if (SRPConfigSystems.disloGrowlNoise) {
                if (SRPSaveData.get(level).getCurrentCode(DimKeys.of(level), 15) == 0) {
                    entityOut.playSound(SRPSounds.VENKROLSI.get(), 4.0f, 1.0f);
                }
            } else {
                entityOut.playSound(SRPSounds.VENKROLSI.get(), 4.0f, 1.0f);
            }
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (!state.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        if (level.isClientSide) {
            return;
        }
        if (!SRPConfigWorld.residueFlammableWave) {
            return;
        }
        if (fireAround(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        super.onRemove(state, level, pos, newState, movedByPiston);
        if (state.is(newState.getBlock())) {
            return;
        }
        BlockPos up = pos.above();
        if (level.getBlockState(up).is(Blocks.FIRE)) {
            level.removeBlock(up, false);
        }
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (level.isClientSide) {
            return;
        }
        if (!SRPConfigWorld.residueFlammableWave) {
            return;
        }
        if (fireAround(level, pos)) {
            level.scheduleTick(pos, this, 1);
        }
    }

    private int getEvoResidue(byte in) {
        int q = -5;
        switch (in) {
            case 1:
                q = SRPConfigSystems.phaseResidueOne;
                break;
            case 2:
                q = SRPConfigSystems.phaseResidueTwo;
                break;
            case 3:
                q = SRPConfigSystems.phaseResidueThree;
                break;
            case 4:
                q = SRPConfigSystems.phaseResidueFour;
                break;
            case 5:
                q = SRPConfigSystems.phaseResidueFive;
                break;
            case 6:
                q = SRPConfigSystems.phaseResidueSix;
                break;
            case 7:
                q = SRPConfigSystems.phaseResidueSeven;
                break;
            case 8:
                q = SRPConfigSystems.phaseResidueEight;
                break;
            default:
                break;
        }
        return q;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!level.isClientSide) {
            if (level.random.nextDouble() < 0.5 && entity.tickCount % 20 != 0) {
                return;
            }
            if (!(entity instanceof EntityParasiteBase) && entity instanceof LivingEntity target && !target.hasEffect(SRPPotions.COTH_E) && !target.hasEffect(SRPPotions.EPEL_E)) {
                target.addEffect(new MobEffectInstance(SRPPotions.COTH_E, 3600, 0, false, false));
            }
        }
        if (entity.onGround() && entity instanceof LivingEntity && !entity.isShiftKeyDown()) {
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x * 0.86, motion.y, motion.z * 0.86);
        }
        if (entity.onGround() && entity instanceof LivingEntity && !entity.isShiftKeyDown()) {
            Vec3 motion = entity.getDeltaMovement();
            entity.setDeltaMovement(motion.x * 0.84, motion.y, motion.z * 0.84);
        }
    }
}
