package com.dhanantry.scapeandrunparasites.entity.projectile;

import com.dhanantry.scapeandrunparasites.block.BlockGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpawner;
import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityCanister;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.Mot;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class EntityGore
extends Entity {
    private static final EntityDataAccessor<Integer> SKIN = SynchedEntityData.defineId(EntityGore.class, EntityDataSerializers.INT);
    private int groun;
    private byte type;
    public ArrayList<String> inbBlockName;
    public ArrayList<Integer> inbBlockNumber;
    public String entityName;

    public EntityGore(EntityType<? extends EntityGore> type, Level worldIn) {
        super(type, worldIn);
    }

    public EntityGore(EntityType<? extends EntityGore> type, Level worldIn, double x, double y, double z, LivingEntity igniter, float stren) {
        this(type, worldIn);
        this.setPos(x, y, z);
        float f = (float)(Math.random() * (Math.PI * 2));
        Mot.setX(this, -((float)Math.sin(f)) * 0.02f);
        Mot.setY(this, 0.2f);
        Mot.setZ(this, -((float)Math.cos(f)) * 0.02f);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.inbBlockName = new ArrayList();
        this.inbBlockNumber = new ArrayList();
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SKIN, 0);
    }

    protected boolean isMovementNoisy() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    public void tick() {
        if (this.tickCount % 20 == 0 && this.level().getBlockState(this.blockPosition()).getFluidState().is(FluidTags.WATER)) {
            this.discard();
            return;
        }
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        if (!this.isNoGravity()) {
            Mot.addY(this, -((double)0.04f));
        }
        this.move(MoverType.SELF, new Vec3(this.getDeltaMovement().x, this.getDeltaMovement().y, this.getDeltaMovement().z));
        Mot.mulX(this, (double)0.98f);
        Mot.mulY(this, (double)0.98f);
        Mot.mulZ(this, (double)0.98f);
        if (this.onGround()) {
            Mot.mulX(this, (double)0.7f);
            Mot.mulZ(this, (double)0.7f);
        }
        this.updateInWaterStateAndDoFluidPushing();
        if (this.level().isClientSide && this.tickCount % 5 == 0 && !this.onGround()) {
            switch (this.getSkin()) {
                case 1: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                    }
                    break;
                }
                case 2: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 150, 0, 0);
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                    }
                    break;
                }
                case 3: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GCLOUD, 200, 200, 0);
                        this.spawnParticles(SRPEnumParticle.GSPLASH, 3, -1, -1);
                    }
                    break;
                }
                case 4: {
                    int i;
                    for (i = 0; i <= 0; ++i) {
                        this.spawnParticles(SRPEnumParticle.GSPLASH, 0, 0, 0);
                    }
                    break;
                }
            }
        }
        if (this.onGround()) {
            ++this.groun;
        }
        if (this.tickCount >= 200) {
            this.discard();
        }
        if (this.groun >= 1) {
            if (!this.level().isClientSide) {
                switch (this.type) {
                    case 1: {
                        this.placeBlood(SRPBlocks.goreSim.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 2: {
                        this.placeBlood(SRPBlocks.gorePri.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 3: {
                        this.placeBlood(SRPBlocks.goreAda.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 4: {
                        this.placeBlood(SRPBlocks.gorePur.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 5: {
                        this.placeBlood(SRPBlocks.goreFer.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 6: {
                        this.placeBlood(SRPBlocks.goreMar.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.SMALL)));
                        break;
                    }
                    case 10: {
                        Block block = this.level().getBlockState(this.blockPosition()).getBlock();
                        if (block == Blocks.AIR || block == Blocks.GRASS_BLOCK) {
                            this.level().setBlock(this.blockPosition(), SRPBlocks.ParasiteCanisterActive.get().defaultBlockState(), 3);
                            this.addCystItems(this.blockPosition());
                            break;
                        }
                        BlockPos helper = this.blockPosition();
                        int rand = this.getRandom().nextInt(4);
                        for (int i = 4; i >= 0; --i) {
                            helper = EntityGore.directionToSpread(this.blockPosition(), rand);
                            block = this.level().getBlockState(helper).getBlock();
                            if (block == Blocks.AIR || block == Blocks.GRASS_BLOCK) {
                                this.level().setBlock(helper, SRPBlocks.ParasiteCanisterActive.get().defaultBlockState(), 3);
                                this.addCystItems(helper);
                                this.discard();
                                return;
                            }
                            rand = (rand + 1) % 4;
                        }
                        this.level().setBlock(this.blockPosition(), SRPBlocks.ParasiteCanisterActive.get().defaultBlockState(), 3);
                        this.addCystItems(this.blockPosition());
                        break;
                    }
                    case 11: {
                        Block block = this.level().getBlockState(this.blockPosition()).getBlock();
                        if (block == Blocks.AIR || block == Blocks.GRASS_BLOCK) {
                            this.level().setBlock(this.blockPosition(), SRPBlocks.dodN.get().defaultBlockState(), 3);
                            break;
                        }
                        BlockPos helper = this.blockPosition();
                        int rand = this.getRandom().nextInt(4);
                        for (int i = 4; i >= 0; --i) {
                            helper = EntityGore.directionToSpread(this.blockPosition(), rand);
                            block = this.level().getBlockState(helper).getBlock();
                            if (block == Blocks.AIR || block == Blocks.GRASS_BLOCK) {
                                this.level().setBlock(helper, SRPBlocks.dodN.get().defaultBlockState(), 3);
                                this.discard();
                                return;
                            }
                            rand = (rand + 1) % 4;
                        }
                        break;
                    }
                    case 12: {
                        BlockState state = BlockIds.legacyState(SRPBlocks.InfestRemain.get(), 1);
                        Block block22 = this.level().getBlockState(this.blockPosition()).getBlock();
                        if ((block22 == Blocks.AIR || block22 instanceof BushBlock) && block22 != SRPBlocks.goreSim.get()) {
                            this.level().setBlockAndUpdate(this.blockPosition(), state);
                            break;
                        }
                        BlockPos helper = this.blockPosition();
                        int rand = this.getRandom().nextInt(4);
                        int ccc = 0;
                        for (int i = 4; i >= 0; --i) {
                            helper = EntityGore.directionToSpread(this.blockPosition(), rand);
                            block22 = this.level().getBlockState(helper).getBlock();
                            if ((block22 == Blocks.AIR || block22 instanceof BushBlock) && block22 != SRPBlocks.goreSim.get()) {
                                this.level().setBlockAndUpdate(helper, state);
                                if (++ccc > 2) {
                                    return;
                                }
                                rand = (rand + 1) % 4;
                                continue;
                            }
                            rand = (rand + 1) % 4;
                        }
                        break;
                    }
                    case 111: {
                        if (this.entityName == null || !this.level().getBlockState(this.blockPosition().below()).isCollisionShapeFullBlock(this.level(), this.blockPosition().below()) || !(this.level().getBlockState(this.blockPosition()).getBlock() instanceof BushBlock) && this.level().getBlockState(this.blockPosition()).getBlock() != Blocks.AIR) break;
                        this.level().setBlockAndUpdate(this.blockPosition(), SRPBlocks.goreSim.get().defaultBlockState().setValue(BlockGore.VARIANT, (BlockGore.EnumType.BIG)));
                        EntityRemain nnn = new EntityRemain(SRPEntities.REMAIN.get(), this.level());
                        nnn.moveTo((double)this.blockPosition().getX() + 0.5, this.blockPosition().getY(), (double)this.blockPosition().getZ() + 0.5, 0.0f, 0.0f);
                        nnn.setParasite(this.entityName);
                        nnn.setSkin((byte)this.getSkin());
                        nnn.setGoal(20 * SRPConfig.feralRemainValue);
                        this.level().addFreshEntity((Entity)nnn);
                    }
                }
            }
            this.discard();
            if (this.level().isClientSide) {
                for (int k = 0; k < 10; ++k) {
                    this.spawnParticles(SRPEnumParticle.GCLOUD, 127, 0, 0);
                }
            }
        }
    }

    private void addCystItems(BlockPos pos) {
        if (this.inbBlockName == null) {
            return;
        }
        if (this.inbBlockName.isEmpty()) {
            return;
        }
        BlockEntity tileentity = this.level().getBlockEntity(pos);
        if (tileentity instanceof TileEntityCanister) {
            TileEntityCanister cyst = (TileEntityCanister)tileentity;
            String[] here = new String[2];
            ArrayList<ItemStack> preDrops = new ArrayList<ItemStack>();
            int loop = 0;
            for (int i = 0; i < this.inbBlockName.size(); ++i) {
                String name = this.inbBlockName.get(i);
                BlockState dropState = BlockIds.tryParse(name);
                while (loop < this.inbBlockNumber.get(i)) {
                    boolean fat = true;
                    try {
                        if (dropState != null && this.level() instanceof ServerLevel serverLevel) {
                            preDrops.addAll(Block.getDrops(dropState, serverLevel, pos, null));
                            fat = false;
                        }
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (fat && dropState != null) {
                        preDrops.add(new ItemStack(dropState.getBlock()));
                    }
                    ++loop;
                }
                cyst.addStack(preDrops);
                loop = 0;
                preDrops = new ArrayList<ItemStack>();
            }
        }
    }

    private void placeBlood(BlockState state) {
        Block block = this.level().getBlockState(this.blockPosition()).getBlock();
        if ((block == Blocks.AIR || block instanceof BushBlock) && !(block instanceof BlockGore)) {
            this.level().setBlockAndUpdate(this.blockPosition(), state);
        } else {
            BlockPos helper = this.blockPosition();
            int rand = this.getRandom().nextInt(4);
            for (int i = 4; i >= 0; --i) {
                helper = EntityGore.directionToSpread(this.blockPosition(), rand);
                block = this.level().getBlockState(helper).getBlock();
                if ((block == Blocks.AIR || block instanceof BushBlock) && !(block instanceof BlockGore)) {
                    this.level().setBlockAndUpdate(helper, state);
                    return;
                }
                rand = (rand + 1) % 4;
            }
        }
    }

    private static BlockPos directionToSpread(BlockPos atm, int choice) {
        switch (choice) {
            case 0: {
                atm = atm.north();
                break;
            }
            case 1: {
                atm = atm.east();
                break;
            }
            case 2: {
                atm = atm.west();
                break;
            }
            default: {
                atm = atm.south();
            }
        }
        return atm;
    }

    public void setType(byte in) {
        this.type = in;
        if (this.type == 12) {
            in = (byte)4;
        }
        if (this.type == 111) {
            in = 1;
        }
        this.setSkin(in);
    }

    public void aiStep() {
    }

    protected void collideWithNearbyEntities() {
    }

    protected void doPush(Entity entityIn) {
        entityIn.push((Entity)this);
    }

    public void baseTick() {
        super.baseTick();
    }

    public void setMotion(double xSpeedIn, double ySpeedIn, double zSpeedIn, double capX, double capY) {
        xSpeedIn = Math.min(xSpeedIn, capX);
        ySpeedIn = Math.min(ySpeedIn, capY);
        zSpeedIn = Math.min(zSpeedIn, capX);
        Mot.setX(this, xSpeedIn * (Math.random() * 2.0 - 1.0));
        Mot.setY(this, ySpeedIn);
        Mot.setZ(this, zSpeedIn * (Math.random() * 2.0 - 1.0));
    }

    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putInt("parasitetype", this.getSkin());
        compound.putInt("bloodtype", (int)this.type);
        ListTag allResS = new ListTag();
        ListTag allResI = new ListTag();
        if (this.inbBlockName == null) {
            return;
        }
        for (int i = 0; i < this.inbBlockName.size(); ++i) {
            String res = this.inbBlockName.get(i);
            CompoundTag resT = new CompoundTag();
            resT.putString("block" + i, res);
            allResS.add((Tag)resT);
            int resi = this.inbBlockNumber.get(i);
            CompoundTag resU = new CompoundTag();
            resU.putInt("block" + i, resi);
            allResI.add((Tag)resU);
        }
        compound.put("srpinvblocksname", (Tag)allResS);
        compound.put("srpinvblocksnumber", (Tag)allResI);
    }

    protected void readAdditionalSaveData(CompoundTag compound) {
        if (compound.contains("parasitetype", 99)) {
            this.setSkin(compound.getInt("parasitetype"));
        }
        if (compound.contains("bloodtype", 99)) {
            this.type = (byte)compound.getInt("bloodtype");
        }
        if (compound.contains("srpinvblocks")) {
            ListTag allResS = compound.getList("srpinvblocksname", 10);
            ListTag allResI = compound.getList("srpinvblocksnumber", 10);
            if (allResS.size() != allResI.size()) {
                return;
            }
            for (int i = 0; i < allResS.size(); ++i) {
                CompoundTag resT = allResS.getCompound(i);
                String res = resT.getString("block" + i);
                this.inbBlockName.add(i, res);
                CompoundTag resU = allResI.getCompound(i);
                int resi = resU.getInt("block" + i);
                this.inbBlockNumber.add(i, resi);
            }
        }
    }

    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.0f;
    }

    public int getSkin() {
        return (Integer)this.entityData.get(SKIN);
    }

    public void setSkin(int texture) {
        this.entityData.set(SKIN, texture);
    }

    public void spawnParticles(SRPEnumParticle particleType, int r, int g, int b) {
        double d0 = this.getRandom().nextGaussian() * 0.02;
        double d1 = this.getRandom().nextGaussian() * 0.02;
        double d2 = this.getRandom().nextGaussian() * 0.02;
        ParticleSpawner.spawnParticle(particleType, this.getX() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), this.getY() + 0.5 + (double)(this.getRandom().nextFloat() * this.getBbHeight()), this.getZ() + (double)(this.getRandom().nextFloat() * this.getBbWidth() * 2.0f) - (double)this.getBbWidth(), d0, d1, d2, r, g, b);
    }
}

