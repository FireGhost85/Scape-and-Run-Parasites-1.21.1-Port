package com.dhanantry.scapeandrunparasites.world.gen.feature;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteLoot;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubble;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteRubbleDense;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteStain;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.util.BlockIds;
import com.dhanantry.scapeandrunparasites.util.LegacyMaterial;
import com.dhanantry.scapeandrunparasites.world.gen.WorldGenCustomStructures;
import com.dhanantry.scapeandrunparasites.world.gen.feature.WorldGenParasiteColonyBase;
import com.dhanantry.scapeandrunparasites.world.gen.feature.util.WorldGenMeteorImpactUtil;
import com.dhanantry.scapeandrunparasites.world.gen.structure.WorldGenStructure;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class WorldGenParasiteMeteorCrash
extends WorldGenParasiteColonyBase {
    public WorldGenParasiteMeteorCrash(boolean notify, int stage) {
        super(notify, stage);
        this.wall = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
        this.tacle = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.FEELER));
        this.floor = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.DIRT));
    }

    public boolean generate(Level worldIn, RandomSource rand, BlockPos posss) {
        int adjustedR;
        int depthDrivenR;
        int openNeeded;
        int baseDepth;
        BlockPos og = posss;
        BlockPos impactCenter = com.dhanantry.scapeandrunparasites.world.gen.feature.util.WorldGenMeteorImpactUtil.topSolidOrLiquid(worldIn, posss).below();
        if (this.type != 5) {
            String out = "meteor_fragment_large1";
            switch (worldIn.random.nextInt(9)) {
                case 1: {
                    out = "meteor_fragment_large2";
                    break;
                }
                case 2: {
                    out = "meteor_fragment_large3";
                    break;
                }
                case 3: {
                    out = "meteor_fragment_small1";
                    break;
                }
                case 4: {
                    out = "meteor_fragment_small2";
                    break;
                }
                case 5: {
                    out = "meteor_fragment_small3";
                    break;
                }
                case 6: {
                    out = "meteor_fragment_small4";
                    break;
                }
                case 7: {
                    out = "meteor_fragment_small5";
                    break;
                }
                case 8: {
                    out = "meteor_fragment_small6";
                }
            }
            BlockPos surface = impactCenter;
            BlockPos origin = surface.offset(2, 2, 2);
            WorldGenCustomStructures.generateInPosition(new WorldGenStructure(out), rand, worldIn, origin, -2, -2, -2);
            int fires = 18 + rand.nextInt(18);
            int fireRadius = 10;
            for (int i = 0; i < fires; ++i) {
                BlockState at;
                BlockState below;
                BlockPos firePos;
                BlockPos top;
                int dz;
                int dx = rand.nextInt(fireRadius * 2 + 1) - fireRadius;
                if (dx * dx + (dz = rand.nextInt(fireRadius * 2 + 1) - fireRadius) * dz > fireRadius * fireRadius || (top = com.dhanantry.scapeandrunparasites.world.gen.feature.util.WorldGenMeteorImpactUtil.topSolidOrLiquid(worldIn, surface.offset(dx, 0, dz)).below()).getY() <= 5 || !worldIn.hasChunkAt(firePos = top.above()) || LegacyMaterial.of(below = worldIn.getBlockState(top)) == LegacyMaterial.air || LegacyMaterial.of(below) == LegacyMaterial.water || LegacyMaterial.of(below) == LegacyMaterial.lava || LegacyMaterial.of(at = worldIn.getBlockState(firePos)) != LegacyMaterial.air) continue;
                worldIn.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 2);
            }
            return true;
        }
        BlockPos enter = impactCenter.below(10);
        for (int i = 0; i < 20; ++i) {
            this.replaceCircleGround(worldIn, enter.above(i), this.type * 7, SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED)));
        }
        int rad = this.type;
        posss = impactCenter.below(rad + rad);
        int minCenterY = rad * 16 + 6;
        if (posss.getY() < minCenterY) {
            posss = BlockPos.containing(posss.getX(), minCenterY, posss.getZ());
        }
        this.generateSphere(worldIn, posss, rad * 16, rad * 16, rand, false, 1, false, 1, 1, 5, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), 2);
        float yaw = rand.nextFloat() * 360.0f;
        double dirX = -Mth.sin((float)(yaw * ((float)Math.PI / 180)));
        double dirZ = Mth.cos((float)(yaw * ((float)Math.PI / 180)));
        float steepness = 0.25f + rand.nextFloat() * 0.75f;
        double dirY = -steepness;
        BlockState rim = SRPBlocks.ParasiteRubbleDense.get().defaultBlockState().setValue(BlockParasiteRubbleDense.VARIANT, (BlockParasiteRubbleDense.EnumType.WALL));
        BlockState rubble = SRPBlocks.ParasiteRubble.get().defaultBlockState().setValue(BlockParasiteRubble.VARIANT, (BlockParasiteRubble.EnumType.BRICKS));
        BlockState stainRed = SRPBlocks.ParasiteStain.get().defaultBlockState().setValue(BlockParasiteStain.VARIANT, (BlockParasiteStain.EnumType.RED));
        BlockState cooked = SRPBlocks.CookedFlesh.get().defaultBlockState();
        BlockPos craterSurface = impactCenter;
        BlockPos tunnelStart = craterSurface.below(3);
        WorldGenMeteorImpactUtil.TunnelResult tunnel = WorldGenMeteorImpactUtil.carveAngledTunnel(worldIn, tunnelStart, 10, 30, dirX, dirY, dirZ);
        int baseR = rad * 8;
        int adjustedDepth = baseDepth = (int)((float)baseR * (0.4f + rand.nextFloat() * 0.2f));
        if (tunnel.anyBroken && (openNeeded = craterSurface.getY() - tunnel.lowestY) > adjustedDepth) {
            adjustedDepth = openNeeded + 3;
        }
        if ((depthDrivenR = (int)((float)adjustedDepth * 1.6f)) > (adjustedR = baseR)) {
            adjustedR = depthDrivenR;
        }
        int bottomY = craterSurface.getY() - adjustedDepth;
        int placeY = Math.max(bottomY + 1, 6);
        BlockPos structPos = BlockPos.containing(craterSurface.getX(), placeY, craterSurface.getZ());
        WorldGenMeteorImpactUtil.clearVegetationInArea(worldIn, craterSurface, adjustedR * 2, craterSurface.getY() - adjustedDepth - 12, craterSurface.getY() + 50);
        WorldGenMeteorImpactUtil.carveCraterBowl(worldIn, rand, craterSurface, adjustedR, adjustedDepth, steepness, rim, stainRed, cooked);
        WorldGenMeteorImpactUtil.scorchRings(worldIn, rand, craterSurface, adjustedR, stainRed);
        WorldGenMeteorImpactUtil.spawnEjecta(worldIn, rand, craterSurface, adjustedR, dirX, dirZ, rubble, stainRed);
        WorldGenMeteorImpactUtil.microCraters(worldIn, rand, craterSurface, adjustedR, dirX, dirZ, stainRed);
        int poolR = Math.max(4, adjustedR / 6);
        int poolRR = poolR * poolR;
        int skipR = 10;
        int skipRR = skipR * skipR;
        int bottomY2 = craterSurface.getY() - adjustedDepth + 1;
        if (bottomY2 < 6) {
            bottomY2 = 6;
        }
        int poolHeight = 4;
        int topY2 = bottomY2 + poolHeight;
        for (int x = -poolR; x <= poolR; ++x) {
            for (int z = -poolR; z <= poolR; ++z) {
                int d2 = x * x + z * z;
                if (d2 > poolRR || d2 <= skipRR) continue;
                for (int y = bottomY2; y <= topY2; ++y) {
                    BlockState s;
                    BlockPos p = BlockPos.containing(craterSurface.getX() + x, y, craterSurface.getZ() + z);
                    if (!worldIn.hasChunkAt(p) || LegacyMaterial.of(s = worldIn.getBlockState(p)) != LegacyMaterial.air) continue;
                    worldIn.setBlock(p, SRPBlocks.DeadBlood.get().defaultBlockState(), 2);
                }
            }
        }
        int half = 22;
        int fix = 2;
        BlockPos meteorPos = structPos.above(14).offset(-half - fix, 0, -half - fix);
        WorldGenCustomStructures.generateInPosition(new WorldGenStructure("meteor"), rand, worldIn, meteorPos, 0, 0, 0);
        int i1 = meteorPos.above(14).getY();
        double l1 = meteorPos.above(14).offset(half, 0, 0).getX();
        double i2 = meteorPos.above(14).offset(0, 0, half).getZ();
        int BGrange = 11;
        for (int k2 = -1 * BGrange; k2 <= 1 * BGrange; ++k2) {
            for (int l2 = -1 * BGrange; l2 <= 1 * BGrange; ++l2) {
                for (int j = -1 * BGrange; j <= 1 * BGrange; ++j) {
                    double i3 = l1 + (double)k2;
                    double k = i1 + j;
                    double l = i2 + (double)l2;
                    BlockPos blockpos = BlockPos.containing(i3, k, l);
                    BlockState iblockstate = worldIn.getBlockState(blockpos);
                    Block block = iblockstate.getBlock();
                    if (block == Blocks.GLASS || block == Blocks.WHITE_STAINED_GLASS || block == Blocks.GLASS_PANE || block == Blocks.WHITE_STAINED_GLASS_PANE || block instanceof TransparentBlock || block instanceof IronBarsBlock && LegacyMaterial.of(iblockstate) == LegacyMaterial.glass) {
                        worldIn.setBlock(blockpos, BlockIds.legacyState(SRPBlocks.ParasiteStain.get(), 2), 2);
                        continue;
                    }
                    String name = block.builtInRegistryHolder().key().location().toString();
                    if (block != Blocks.IRON_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.DIAMOND_BLOCK) continue;
                    int rollRare = 10;
                    int rollUncommon = 4;
                    if (block == Blocks.GOLD_BLOCK) {
                        rollRare = 7;
                        rollUncommon = 3;
                    }
                    if (block == Blocks.DIAMOND_BLOCK) {
                        rollRare = 4;
                        rollUncommon = 2;
                    }
                    if (rand.nextInt(rollRare) == 0) {
                        this.placeLoot(worldIn, blockpos, SRPConfigWorld.blockLootRare, SRPBlocks.ParasiteLoot.get().defaultBlockState().setValue(BlockParasiteLoot.VARIANT, (BlockParasiteLoot.EnumType.RARE)));
                        worldIn.setBlockAndUpdate(blockpos.above(), SRPBlocks.DeadBlood.get().defaultBlockState());
                        continue;
                    }
                    if (rand.nextInt(rollUncommon) == 0) {
                        this.placeLoot(worldIn, blockpos, SRPConfigWorld.blockLootUncommon, SRPBlocks.ParasiteLoot.get().defaultBlockState().setValue(BlockParasiteLoot.VARIANT, (BlockParasiteLoot.EnumType.UNCOMMON)));
                        worldIn.setBlockAndUpdate(blockpos.above(), SRPBlocks.DeadBlood.get().defaultBlockState());
                        continue;
                    }
                    this.placeLoot(worldIn, blockpos, SRPConfigWorld.blockLootCommon, SRPBlocks.ParasiteLoot.get().defaultBlockState().setValue(BlockParasiteLoot.VARIANT, (BlockParasiteLoot.EnumType.COMMON)));
                    worldIn.setBlockAndUpdate(blockpos.above(), SRPBlocks.DeadBlood.get().defaultBlockState());
                }
            }
        }
        WorldGenMeteorImpactUtil.updateWaterAfterImpact(worldIn, craterSurface, adjustedR, adjustedDepth);
        return true;
    }
}

