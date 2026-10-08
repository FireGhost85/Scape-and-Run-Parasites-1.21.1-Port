package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.block.BlockBase;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteBush;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteLoot;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteGenAbstract;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WorldGenParasiteColonyBase
extends WorldGenParasiteGenAbstract {
    protected int type;
    protected BlockState floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    protected BlockState tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
    protected BlockState wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
    protected BlockState floorColony = SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.FLESH));

    public WorldGenParasiteColonyBase(boolean notify, int stage) {
        super(notify);
        this.type = stage;
    }

    protected void placeBlock(Level worldIn, BlockPos pos, BlockState state) {
        this.setBlockAndNotifyAdequately(worldIn, pos, state);
    }

    protected void placeVine(Level worldIn, BlockPos pos) {
        this.setBlockAndNotifyAdequately(worldIn, pos, SRPBlocks.ParasiteBush.get().defaultBlockState().setValue(BlockParasiteBush.VARIANT, (BlockParasiteBush.EnumType.BINE)));
    }

    protected void placeReplacement(Level worldIn, BlockPos pos, BlockState in) {
        this.setBlockAndNotifyAdequately(worldIn, pos, in);
    }

    protected BlockPos getDirectionRoot(BlockPos center, int direction, int times) {
        switch (direction) {
            case 0: {
                return center.north(times);
            }
            case 1: {
                return center.east(times);
            }
            case 2: {
                return center.south(times);
            }
        }
        return center.west(times);
    }

    protected BlockPos placeColumn(Level worldIn, BlockPos pos, int in, RandomSource rand, double extraChance, BlockState state) {
        int current;
        int atm = current = pos.getY();
        int times = 0;
        BlockPos newPos = pos;
        while (current < atm + in) {
            this.placeBlock(worldIn, newPos, state);
            newPos = extraChance == 1.0 ? newPos.above() : newPos.below();
            ++current;
            ++times;
        }
        this.placeBlock(worldIn, newPos, state);
        return newPos;
    }

    protected BlockPos directionToGrow(BlockPos atm, int choice, boolean sideCurse) {
        if (sideCurse) {
            switch (choice) {
                case 0: {
                    atm = atm.north();
                    atm = atm.east();
                    break;
                }
                case 1: {
                    atm = atm.north();
                    atm = atm.west();
                    break;
                }
                case 10: {
                    atm = atm.east();
                    atm = atm.south();
                    break;
                }
                case 11: {
                    atm = atm.east();
                    atm = atm.north();
                    break;
                }
                case 20: {
                    atm = atm.south();
                    atm = atm.west();
                    break;
                }
                case 21: {
                    atm = atm.south();
                    atm = atm.east();
                    break;
                }
                case 30: {
                    atm = atm.west();
                    atm = atm.north();
                    break;
                }
                default: {
                    atm = atm.west();
                    atm = atm.south();
                }
            }
            return atm;
        }
        switch (choice) {
            case 0: {
                atm = atm.north();
                break;
            }
            case 1: {
                atm = atm.east();
                break;
            }
            case 3: {
                atm = atm.west();
                break;
            }
            default: {
                atm = atm.south();
            }
        }
        return atm;
    }

    protected void addVines(Level worldIn, BlockPos position, RandomSource rand, int longer) {
        if (worldIn.getBlockState(position).getBlock() != Blocks.AIR) {
            return;
        }
        this.placeVine(worldIn, position);
        int chance = longer;
        while (worldIn.getBlockState(position.below()).getBlock() == Blocks.AIR && worldIn.getBlockState(position.below(2)).getBlock() == Blocks.AIR) {
            if (rand.nextInt(chance) == 0) {
                return;
            }
            position = position.below();
            this.placeVine(worldIn, position);
            --chance;
        }
    }

    protected void replaceLayer(Level worldIn, BlockPos position, int range, BlockState toReplace, BlockState in) {
        int xx = position.getX();
        int zz = position.getZ();
        int yy = position.getY();
        for (int x = xx - range; x <= xx + range; ++x) {
            for (int z = zz - range; z <= zz + range; ++z) {
                BlockPos neww = BlockPos.containing(x, yy, z);
                if (worldIn.getBlockState(neww) != toReplace) continue;
                this.placeReplacement(worldIn, neww, in);
            }
        }
    }

    protected void addFloor(Level worldIn, BlockPos position, int range, boolean fill) {
        BlockPos atm;
        if (fill) {
            this.placeReplacement(worldIn, position, this.floorColony);
        }
        int offsetN = 2;
        BlockPos atm2 = position.north();
        while (!(worldIn.getBlockState(atm2).getBlock() instanceof BlockBase)) {
            this.placeReplacement(worldIn, atm2, this.floorColony);
            this.genFloorFloor(worldIn, atm2.below(), 15, fill);
            this.placeReplacement(worldIn, atm2.south(offsetN), this.floorColony);
            this.genFloorFloor(worldIn, atm2.south(offsetN).below(), 15, fill);
            atm2 = atm2.north();
            offsetN += 2;
        }
        BlockPos here = position.east(1);
        while (!(worldIn.getBlockState(here).getBlock() instanceof BlockBase)) {
            int offsetN2 = 2;
            atm = here.north();
            while (!(worldIn.getBlockState(atm).getBlock() instanceof BlockBase)) {
                this.placeReplacement(worldIn, atm, this.floorColony);
                this.genFloorFloor(worldIn, atm.below(), 15, fill);
                this.placeReplacement(worldIn, atm.south(offsetN2), this.floorColony);
                this.genFloorFloor(worldIn, atm.south(offsetN2).below(), 15, fill);
                atm = atm.north();
                offsetN2 += 2;
            }
            this.placeReplacement(worldIn, here, this.floorColony);
            this.genFloorFloor(worldIn, here.below(), 15, fill);
            here = here.east();
        }
        here = position.west(1);
        while (!(worldIn.getBlockState(here).getBlock() instanceof BlockBase)) {
            int offsetN3 = 2;
            atm = here.north();
            while (!(worldIn.getBlockState(atm).getBlock() instanceof BlockBase)) {
                this.placeReplacement(worldIn, atm, this.floorColony);
                this.genFloorFloor(worldIn, atm.below(), 15, fill);
                this.placeReplacement(worldIn, atm.south(offsetN3), this.floorColony);
                this.genFloorFloor(worldIn, atm.south(offsetN3).below(), 15, fill);
                atm = atm.north();
                offsetN3 += 2;
            }
            this.placeReplacement(worldIn, here, this.floorColony);
            this.genFloorFloor(worldIn, here.below(), 15, fill);
            here = here.west();
        }
        if (!fill) {
            this.addFloorSpace(worldIn, position);
        }
    }

    protected void genFloorFloor(Level worldIn, BlockPos position, int range, boolean fill) {
        if (fill) {
            BlockPos filler = position;
            while ((worldIn.getBlockState(filler).getBlock() == Blocks.AIR || worldIn.getBlockState(filler).getBlock() == SRPBlocks.ParasiteBush.get() || worldIn.getBlockState(filler).getBlock() instanceof BlockLeaves || worldIn.getBlockState(filler).getBlock() instanceof BushBlock) && range > 0) {
                this.placeReplacement(worldIn, filler, this.floor);
                filler = filler.below();
                --range;
            }
        }
    }

    protected void addFloorSpace(Level worldIn, BlockPos position) {
        int xx = position.getX();
        int zz = position.getZ();
        int yy = position.getY();
        int range = 1;
        for (int x = xx - range; x <= xx + range; ++x) {
            for (int z = zz - range; z <= zz + range; ++z) {
                BlockPos neww = BlockPos.containing(x, yy, z);
                if (worldIn.getBlockState(neww) != this.floorColony) continue;
                this.placeReplacement(worldIn, neww, Blocks.AIR.defaultBlockState());
            }
        }
    }

    protected void addMobSpawner(Level worldIn, BlockPos position, int type, double chance) {
    }

    protected void generateSphere(Level worldIn, BlockPos posss, int innerHeight, int outerHeight, RandomSource rand, boolean invertedTip, int valueStarting, boolean random, int heightBelow, int heightAbove, int tip, BlockState state1, BlockState state2, BlockState state3, int incomplete) {
        int heig;
        int xx = valueStarting;
        int zz = valueStarting;
        int test = innerHeight;
        int ticc = 2;
        while (test > 0) {
            --test;
            heig = heightBelow;
            while (heig > 0) {
                --heig;
                this.generateCircle(state1, state2, worldIn, worldIn.random, posss, xx, zz, 1, incomplete, 6);
                this.generateCircle(state3, state3, worldIn, worldIn.random, posss, xx - ticc, zz - ticc, 1, 50000, 6);
                posss = posss.above();
            }
            xx = rand.nextBoolean() && random ? (xx += 2) : ++xx;
            if (rand.nextBoolean() && random) {
                zz += 2;
                continue;
            }
            ++zz;
        }
        test = outerHeight;
        while (test > 0) {
            --test;
            this.generateCircle(state1, state2, worldIn, worldIn.random, posss, xx, zz, 1, incomplete, 0);
            this.generateCircle(state3, state3, worldIn, worldIn.random, posss, xx - 2, zz - 2, 1, 50000, 0);
            posss = posss.above();
        }
        test = valueStarting + tip;
        if (invertedTip) {
            tip = 0;
            heightAbove = (int)((double)heightAbove * 0.5);
        }
        while (test > 0) {
            --test;
            heig = heightAbove;
            while (heig > 0) {
                --heig;
                this.generateCircle(state1, state2, worldIn, worldIn.random, posss, xx, zz, 1, incomplete, invertedTip ? 9 : 0);
                this.generateCircle(state3, state3, worldIn, worldIn.random, posss, xx - ticc, zz - ticc, 1, 50000, invertedTip ? 9 : 0);
                posss = posss.above();
            }
            if (invertedTip) {
                xx = rand.nextBoolean() && random ? (xx += 2) : ++xx;
                if (rand.nextBoolean() && random) {
                    zz += 2;
                    continue;
                }
                ++zz;
                continue;
            }
            xx = rand.nextBoolean() && random ? (xx -= 2) : --xx;
            if (rand.nextBoolean() && random) {
                zz -= 2;
                continue;
            }
            --zz;
        }
    }

    protected boolean generateCircle(BlockState state, BlockState state2, Level world, RandomSource rand, BlockPos pos, int radiusX, int radiusZ, int height, int incomplete, int veins) {
        if (pos.getY() <= 2 || pos.getY() >= 240) {
            return false;
        }
        for (int y = 0; y < height; ++y) {
            BlockPos layerPos = pos.above(y);
            for (int x = -radiusX; x <= radiusX; ++x) {
                for (int z = -radiusZ; z <= radiusZ; ++z) {
                    boolean flagAir;
                    double normalizedX = (double)x / (double)radiusX;
                    double normalizedZ = (double)z / (double)radiusZ;
                    if (!(normalizedX * normalizedX + normalizedZ * normalizedZ <= 1.0)) continue;
                    BlockPos newBlockPos = layerPos.offset(x, 0, z);
                    boolean bl = flagAir = state.getBlock() == Blocks.AIR && state2.getBlock() == Blocks.AIR;
                    if (world.getBlockState(newBlockPos).getBlock() != Blocks.AIR && !(world.getBlockState(newBlockPos).getBlock() instanceof BlockBase) && !flagAir || rand.nextInt(incomplete) == 0) continue;
                    if ((x == radiusX || z == radiusZ || x == -radiusX || z == -radiusZ || x + 1 == radiusX || z + 1 == radiusZ || x - 1 == -radiusX || z - 1 == -radiusZ) && rand.nextInt(60) == 0) {
                        if (rand.nextInt(4) == 0) {
                            if (flagAir) continue;
                            world.setBlockAndUpdate(newBlockPos, SRPBlocks.DeadBlood.get().defaultBlockState());
                            if (veins <= 0) continue;
                            this.addVines(world, newBlockPos.below(), rand, veins);
                            continue;
                        }
                        if (flagAir) continue;
                        if (rand.nextInt(10) == 0) {
                            this.placeLoot(world, newBlockPos, SRPConfigWorld.blockLootRare, SRPBlocks.ParasiteLoot.get().defaultBlockState().setValue(BlockParasiteLoot.VARIANT, (BlockParasiteLoot.EnumType.RARE)));
                        } else if (rand.nextInt(4) == 0) {
                            this.placeLoot(world, newBlockPos, SRPConfigWorld.blockLootUncommon, SRPBlocks.ParasiteLoot.get().defaultBlockState().setValue(BlockParasiteLoot.VARIANT, (BlockParasiteLoot.EnumType.UNCOMMON)));
                        } else {
                            this.placeLoot(world, newBlockPos, SRPConfigWorld.blockLootCommon, SRPBlocks.ParasiteLoot.get().defaultBlockState().setValue(BlockParasiteLoot.VARIANT, (BlockParasiteLoot.EnumType.COMMON)));
                        }
                        if (veins <= 0) continue;
                        this.addVines(world, newBlockPos.below(), rand, veins);
                        continue;
                    }
                    if (rand.nextBoolean()) {
                        world.setBlockAndUpdate(newBlockPos, state);
                        if (veins <= 0 || flagAir) continue;
                        this.addVines(world, newBlockPos.below(), rand, veins);
                        continue;
                    }
                    world.setBlockAndUpdate(newBlockPos, state2);
                    if (veins <= 0 || flagAir) continue;
                    this.addVines(world, newBlockPos.below(), rand, veins);
                }
            }
        }
        return true;
    }

    protected void placeLoot(Level world, BlockPos pos, String[] list, BlockState state) {
        world.setBlockAndUpdate(pos, state);
        BlockEntity tileentity = world.getBlockEntity(pos);
        if (tileentity instanceof TileEntityParasiteLoot) {
            TileEntityParasiteLoot cyst = (TileEntityParasiteLoot)tileentity;
            for (int i = 0; i < cyst.getSizeInventory(); ++i) {
                if (world.random.nextInt(2) == 0) continue;
                cyst.setInventorySlotContents(i, new ItemStack(this.loot(world.random, list)));
            }
        }
    }

    private Item loot(RandomSource rand, String[] drop) {
        try {
            Item item;
            if (drop.length != 0 && (item = Item.getByNameOrId((String)drop[rand.nextInt(drop.length)])) != null) {
                return item;
            }
        }
        catch (Exception e) {
            SRPMain.logger.log(Level.ERROR, "Problem with loot event", (Throwable)e);
        }
        return null;
    }

    protected boolean generateDNAHelix(BlockState state, Level world, RandomSource rand, BlockPos pos, double radius, int numTurns, double pitch) {
        if (pos.getY() <= 2 || pos.getY() >= 240) {
            return false;
        }
        double tStep = 0.1;
        for (double t = 0.0; t <= (double)(numTurns * 2) * Math.PI; t += tStep) {
            double yOffset = pitch / (Math.PI * 2) * t;
            int y = pos.getY() + (int)Math.round(yOffset);
            int x1 = pos.getX() + (int)Math.round(radius * Math.cos(t));
            int z1 = pos.getZ() + (int)Math.round(radius * Math.sin(t));
            int x2 = pos.getX() + (int)Math.round(radius * Math.cos(t + Math.PI));
            int z2 = pos.getZ() + (int)Math.round(radius * Math.sin(t + Math.PI));
            BlockPos pos1 = BlockPos.containing(x1, y, z1);
            BlockPos pos2 = BlockPos.containing(x2, y, z2);
            world.setBlockAndUpdate(pos1, state);
            world.setBlockAndUpdate(pos2, state);
            if ((int)Math.round(t / Math.PI) % 2 != 0) continue;
            int midX = (x1 + x2) / 2;
            int midZ = (z1 + z2) / 2;
            BlockPos blockPos = BlockPos.containing(midX, y, midZ);
        }
        return true;
    }

    public BlockPos getCirclePoint(BlockPos center, int radius, double theta) {
        int x = center.getX() + (int)Math.round(Math.cos(theta) * (double)radius);
        int z = center.getZ() + (int)Math.round(Math.sin(theta) * (double)radius);
        return BlockPos.containing(x, center.getY(), z);
    }

    public List<BlockPos> getCirclePoints(BlockPos center, int radius, int steps) {
        ArrayList<BlockPos> points = new ArrayList<BlockPos>();
        double cx = center.getX();
        double cz = center.getZ();
        double cy = center.getY();
        for (int i = 0; i < steps; ++i) {
            double theta = Math.PI * 2 * (double)i / (double)steps;
            int x = center.getX() + (int)Math.round(Math.cos(theta) * (double)radius);
            int z = center.getZ() + (int)Math.round(Math.sin(theta) * (double)radius);
            points.add(BlockPos.containing(x, (int)cy, z));
        }
        return points;
    }

    public void generatePillar(Level world, BlockPos basePos, int height, BlockState blockState, BlockState blockState2) {
        for (int dy = 0; dy < height; ++dy) {
            BlockPos pos = basePos.above(dy);
            if (world.random.nextBoolean()) {
                world.setBlockAndUpdate(pos, blockState);
                continue;
            }
            world.setBlockAndUpdate(pos, blockState2);
        }
    }

    public void replaceCircleGround(Level world, BlockPos center, int radius, BlockState targetBlock) {
        int cx = center.getX();
        int cz = center.getZ();
        int cy = center.getY();
        if (cy <= 2 || cy >= 240) {
            return;
        }
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dz = -radius; dz <= radius; ++dz) {
                BlockPos pos;
                BlockState current;
                if (dx * dx + dz * dz > radius * radius || (current = world.getBlockState(pos = BlockPos.containing(cx + dx, cy, cz + dz))).getBlock() instanceof BlockBase || current.getBlock() == Blocks.AIR) continue;
                world.setBlockAndUpdate(pos, targetBlock);
            }
        }
    }

    public void generateVerticalCircle(Level world, BlockPos center, int radius, boolean useXZPlane) {
        double step = Math.PI / (double)(radius * 4);
        for (double theta = 0.0; theta < Math.PI * 2; theta += step) {
            int dy = (int)Math.round((double)radius * Math.sin(theta));
            int dPrimary = (int)Math.round((double)radius * Math.cos(theta));
            BlockPos target = useXZPlane ? center.offset(dPrimary, dy, 0) : center.offset(0, dy, dPrimary);
            world.setBlockAndUpdate(target, Blocks.STONE.defaultBlockState());
        }
    }

    public void generateFilledVerticalDisk(Level world, BlockPos center, int radius, boolean useXZPlane) {
        int rSq = radius * radius;
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius; dy <= radius; ++dy) {
                BlockPos target;
                if (dx * dx + dy * dy > rSq || world.getBlockState(target = useXZPlane ? center.offset(dx, dy, 0) : center.offset(0, dy, dx)).getBlock() == SRPBlocks.DeadBlood.get() || world.getBlockState(target).getBlock() == SRPBlocks.ParasiteBush.get()) continue;
                world.setBlockAndUpdate(target, Blocks.AIR.defaultBlockState());
            }
        }
    }

    public void addEntrance(Level worldIn, RandomSource rand, BlockPos position, int entrance) {
        int direction = rand.nextInt(4);
        int offset = 3;
        while (entrance > 0) {
            if (offset > 0) {
                position = this.directionToGrow(position, direction, false);
                --offset;
                continue;
            }
            --entrance;
            position = this.directionToGrow(position, direction, false);
            this.generateFilledVerticalDisk(worldIn, position.above(1), 3, direction != 1 && direction != 3);
        }
    }
}

