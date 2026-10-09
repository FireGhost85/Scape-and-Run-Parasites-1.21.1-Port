package com.dhanantry.scapeandrunparasites.entity.ai;

import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EndGatewayBlock;
import net.minecraft.world.level.block.EndPortalBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;

public class EntityAIBlockLight
extends Goal {
    /** 1.12 ticked running tasks every tick; 1.21 only every second tick unless this is set. */
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    private final EntityParasiteBase parent;
    private int ticks = 0;
    private int range;
    private int lightTrigger;
    private BlockPos target;
    private Level world;
    private int progressB;
    private int idle;
    private int neededTime;
    private Block block;
    private int disss;
    private int prevProgressB = -1;
    private ArrayList<Long> cant;

    public EntityAIBlockLight(EntityParasiteBase in, int range, int lightLook) {
        this.parent = in;
        this.world = this.parent.level();
        this.range = range;
        this.lightTrigger = lightLook;
        this.cant = new ArrayList();
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        ++this.ticks;
        if (!this.parent.getGeneMod(7)) {
            return false;
        }
        if (this.ticks < 40) {
            return false;
        }
        this.ticks = 0;
        if (!this.parent.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) || this.parent.getTarget() != null) {
            return false;
        }
        BlockPos ll = this.findSource();
        if (ll != null) {
            this.target = ll;
            return true;
        }
        return false;
    }

    public boolean canContinueToUse() {
        return this.target != null && this.world.getBlockState(this.target).getBlock() == this.block && this.parent.getTarget() == null;
    }

    public void start() {
        this.progressB = 0;
        this.idle = 0;
        this.block = this.world.getBlockState(this.target).getBlock();
        float multiplier = 10.0f;
        BlockState state = this.world.getBlockState(this.target);
        this.neededTime = (int)(state.getDestroySpeed(this.world, this.target) * Math.max(0.0f, multiplier));
    }

    public void stop() {
        this.parent.getNavigation().stop();
        this.prevProgressB = -1;
        this.target = null;
    }

    public void tick() {
        if (this.target == null) {
            return;
        }
        double r = 5.0;
        double distance = this.realdistanceSq(this.target, this.parent.getX(), this.parent.getY(), this.parent.getZ());
        if (distance > r) {
            this.parent.getNavigation().moveTo((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), 1.1);
        }
        if (this.parent.isPassenger()) {
            this.stop();
            return;
        }
        ++this.idle;
        this.idle = (int)Math.round(distance) == this.disss ? ++this.idle : 0;
        if (this.idle == 120) {
            this.parent.skillBreakBlocks();
        }
        if (this.idle >= 240) {
            boolean canAdd = true;
            for (int p = 0; p < this.cant.size(); ++p) {
                if (this.cant.get(p).longValue() != this.target.asLong()) continue;
                canAdd = false;
            }
            if (canAdd) {
                this.cant.add(this.target.asLong());
            }
            this.stop();
            this.ticks += 30;
            return;
        }
        this.disss = (int)Math.round(distance);
        if (distance <= r && this.target != null) {
            this.parent.getNavigation().moveTo((double)this.target.getX(), (double)this.target.getY(), (double)this.target.getZ(), 0.0);
            ++this.progressB;
            this.idle = 0;
            int i = (int)((float)this.progressB / (float)this.neededTime);
            if (i != this.prevProgressB) {
                this.world.destroyBlockProgress(this.parent.getId(), this.target, i);
                this.prevProgressB = i;
            }
            if (this.progressB >= this.neededTime) {
                this.world.destroyBlock(this.target, true);
                this.progressB = 0;
                this.ticks += 30;
            }
        }
    }

    private BlockPos findSource() {
        if (this.world.getBrightness(LightLayer.BLOCK, this.parent.blockPosition()) < this.lightTrigger && this.world.random.nextInt(3) != 0) {
            return null;
        }
        double distanceM = 40000.0;
        BlockPos light = null;
        if (this.parent.isPassenger()) {
            return light;
        }
        ArrayList<BlockPos> sources = new ArrayList<BlockPos>();
        for (int i = -this.range; i <= this.range; ++i) {
            int j = -4;
            while ((float)j <= this.parent.getBbHeight()) {
                for (int k = -this.range; k <= this.range; ++k) {
                    BlockPos atm = BlockPos.containing(this.parent.getX() + (double)i, this.parent.getY() + (double)j, this.parent.getZ() + (double)k);
                    BlockState state = this.world.getBlockState(atm);
                    Block block = state.getBlock();
                    if (block instanceof LiquidBlock || block instanceof NetherPortalBlock || block instanceof EndGatewayBlock || block instanceof EndPortalFrameBlock || block instanceof BlockBase || state.getBlock() instanceof EndPortalBlock || ParasiteEventEntity.checkName(block.builtInRegistryHolder().key().location().toString(), SRPConfig.parasiteGriefingBlackList, SRPConfig.parasiteGriefingWhite) || state.getLightEmission() < this.lightTrigger && !SRPEntityUtil.isCircuits(state)) continue;
                    if (this.cant.size() == 0) {
                        sources.add(atm);
                        continue;
                    }
                    boolean canAdd = true;
                    for (int p = 0; p < this.cant.size(); ++p) {
                        if (this.cant.get(p).longValue() != atm.asLong()) continue;
                        canAdd = false;
                        break;
                    }
                    if (!canAdd) continue;
                    sources.add(atm);
                }
                ++j;
            }
        }
        for (BlockPos pos : sources) {
            double distance = pos.distToLowCornerSqr(this.parent.getX(), this.parent.getY(), this.parent.getZ());
            if (!(distance < distanceM)) continue;
            light = pos;
            distanceM = distance;
        }
        return light;
    }

    private double realdistanceSq(BlockPos pos, double toX, double toY, double toZ) {
        double d0 = (double)pos.getX() + 0.5 - toX;
        double d1 = (double)pos.getY() + 0.5 - toY;
        double d2 = (double)pos.getZ() + 0.5 - toZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
}

