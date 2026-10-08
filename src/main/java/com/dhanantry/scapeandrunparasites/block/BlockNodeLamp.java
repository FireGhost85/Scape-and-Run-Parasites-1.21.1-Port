package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.util.LangHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import java.util.List;

/**
 * Node redstone lamp: powers on while a Dod (any of the four tiers) is within 250 blocks and shows the distance tier in
 * {@code range_level} (0 to 5). Right click prints the strength and the distance. Gives no light and no redstone signal,
 * only a comparator output of three times the range level (as in 1.12, where no light level was set).
 */
public class BlockNodeLamp extends BlockBase {
    public static final BooleanProperty POWERED = BooleanProperty.create("powered");
    public static final IntegerProperty RANGE_LEVEL = IntegerProperty.create("range_level", 0, 5);

    public BlockNodeLamp() {
        super(prop(SRPMaterial.CIRCUITS.props(0.3f).sound(SoundType.GLASS), true));
        this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.FALSE).setValue(RANGE_LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, RANGE_LEVEL);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(RANGE_LEVEL) * 3;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        this.updateLampState(level, pos, state);
        level.scheduleTick(pos, this, 100);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide) {
            this.updateLampState(level, pos, state);
            level.scheduleTick(pos, this, 20);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            this.updateLampState(level, pos, state);
        }
    }

    private static boolean isDod(Entity e) {
        return e.isAlive() && (e instanceof EntityDod || e instanceof EntityDodSII || e instanceof EntityDodSIII || e instanceof EntityDodSIV);
    }

    private static double nearestDodDistanceSq(Level level, BlockPos pos) {
        double nearestDistSq = Double.MAX_VALUE;
        Vec3 corner = Vec3.atLowerCornerOf(pos);
        for (Entity e : level.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(250.0), BlockNodeLamp::isDod)) {
            double distSq = e.distanceToSqr(corner);
            if (distSq < nearestDistSq) {
                nearestDistSq = distSq;
            }
        }
        return nearestDistSq;
    }

    private static int distanceTier(double nearestDistSq) {
        if (nearestDistSq <= 2500.0) {
            return 5;
        } else if (nearestDistSq <= 5625.0) {
            return 4;
        } else if (nearestDistSq <= 10000.0) {
            return 3;
        } else if (nearestDistSq <= 22500.0) {
            return 2;
        } else if (nearestDistSq <= 40000.0) {
            return 1;
        }
        return 0;
    }

    private boolean updateLampState(Level level, BlockPos pos, BlockState state) {
        int newLevel = distanceTier(nearestDodDistanceSq(level, pos));
        boolean powered = newLevel > 0;
        boolean changed = powered != state.getValue(POWERED) || newLevel != state.getValue(RANGE_LEVEL);
        if (changed) {
            level.setBlock(pos, state.setValue(POWERED, powered).setValue(RANGE_LEVEL, newLevel), 3);
            level.getLightEngine().checkBlock(pos);
            level.updateNeighbourForOutputSignal(pos, this);
        }
        return changed;
    }

    private static String distanceColored(int displayTier) {
        switch (displayTier) {
            case 1:
                return "\u00a7c1";
            case 2:
                return "\u00a762";
            case 3:
                return "\u00a7e3";
            case 4:
                return "\u00a7a4";
            case 5:
                return "\u00a725";
            default:
                return "\u00a770";
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        boolean powered = state.getValue(POWERED);
        int maxStrength = 0;
        double nearestDistSq = Double.MAX_VALUE;
        Vec3 corner = Vec3.atLowerCornerOf(pos);
        for (Entity e : level.getEntitiesOfClass(Entity.class, new AABB(pos).inflate(250.0), Entity::isAlive)) {
            int s = 0;
            if (e instanceof EntityDod) {
                s = 1;
            } else if (e instanceof EntityDodSII) {
                s = 2;
            } else if (e instanceof EntityDodSIII) {
                s = 3;
            } else if (e instanceof EntityDodSIV) {
                s = 4;
            }
            if (s <= 0) {
                continue;
            }
            if (s > maxStrength) {
                maxStrength = s;
            }
            double d = e.distanceToSqr(corner);
            if (d < nearestDistSq) {
                nearestDistSq = d;
            }
        }
        int distanceTierRaw = nearestDistSq != Double.MAX_VALUE ? distanceTier(nearestDistSq) : 0;
        if (!powered) {
            maxStrength = 0;
            distanceTierRaw = 0;
        }
        int displayDistanceTier = distanceTierRaw == 0 ? 0 : 6 - distanceTierRaw;
        String strengthLabel = maxStrength == 0 ? "0" : LangHelper.toRoman(maxStrength);
        String distanceLabel = distanceColored(displayDistanceTier);
        player.sendSystemMessage(Component.translatable("message.srparasites.node_lamp.strength", strengthLabel));
        player.sendSystemMessage(Component.translatable("message.srparasites.node_lamp.distance", distanceLabel));
        return InteractionResult.SUCCESS;
    }
}
