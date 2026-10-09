package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityDod;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPDamageTypes;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/** Dod nexus block: pushes and damages players that touch it; its tile entity spawns the Dod. */
public class BlockDod extends BlockBase implements EntityBlock {
    private static final VoxelShape DOD_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
    private static final String NBT_DOD_LAST_HIT = "srp_dod_last_hit";

    public BlockDod(float hardness, boolean tickRandom, float resistance) {
        super(prop(SRPMaterial.SPONGE.props(hardness, resistance).sound(SRPSoundTypes.FLESH), tickRandom));
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityDod(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == SRPBlockEntities.DOD.get() ? (lvl, pos, st, be) -> TileEntityDod.tick(lvl, pos, st, (TileEntityDod) be) : null;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return DOD_AABB;
    }

    private void pushAndHit(Level level, BlockPos hitPos, Player player) {
        if (player.isRemoved()) {
            return;
        }
        float yawRad = (float) Math.toRadians(player.getYRot());
        double lookX = -Mth.sin(yawRad);
        double lookZ = Mth.cos(yawRad);
        double softStrength = 0.3;
        double softX = -lookX * softStrength;
        double softZ = -lookZ * softStrength;
        player.push(softX, 0.05, softZ);
        player.hurtMarked = true;
        long now = level.getGameTime();
        long lastHit = player.getPersistentData().getLong(NBT_DOD_LAST_HIT);
        if (now - lastHit < 20L) {
            return;
        }
        player.getPersistentData().putLong(NBT_DOD_LAST_HIT, now);
        double hitStrength = 1.2;
        double hitX = -lookX * hitStrength;
        double hitZ = -lookZ * hitStrength;
        player.push(hitX, 0.25, hitZ);
        player.hurtMarked = true;
        float damage = 4.0f;
        player.hurt(new DamageSource(level.registryAccess().holderOrThrow(SRPDamageTypes.DOD_BLOCK)), damage);
        level.playSound(null, hitPos, SRPSounds.ADAPTATION_P.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        player.addEffect(new MobEffectInstance(SRPPotions.DOD_SMOKE_TRAIL_E, 20, 0, false, false));
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide) {
            return;
        }
        if (!(entity instanceof Player player)) {
            return;
        }
        this.pushAndHit(level, pos, player);
    }
}
