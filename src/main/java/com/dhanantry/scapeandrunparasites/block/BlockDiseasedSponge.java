package com.dhanantry.scapeandrunparasites.block;

import java.util.ArrayDeque;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

/** Diseased sponge: absorbs dead blood (used by the sponge to dead blood brewing handler). */
public class BlockDiseasedSponge extends BlockBase {
    public BlockDiseasedSponge() {
        super(prop(SRPMaterial.SPONGE.props(0.6f).sound(SoundType.GRASS), false));
    }

    /** Flood fill of up to 64 dead blood blocks within depth 6 of {@code origin}; true if any was removed. */
    public static boolean absorbDeadBlood(Level world, BlockPos origin, Block deadBlood) {
        if (deadBlood == null || deadBlood == Blocks.AIR) {
            return false;
        }
        int removed = 0;
        ArrayDeque<Node> q = new ArrayDeque<>();
        q.add(new Node(origin, 0));
        while (!q.isEmpty() && removed < 64) {
            Node n = q.poll();
            for (Direction f : Direction.values()) {
                BlockPos p = n.pos.relative(f);
                BlockState st = world.getBlockState(p);
                if (st.getBlock() != deadBlood) {
                    continue;
                }
                world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                ++removed;
                if (n.depth >= 6) {
                    continue;
                }
                q.add(new Node(p, n.depth + 1));
            }
        }
        return removed > 0;
    }

    private record Node(BlockPos pos, int depth) {
    }
}
