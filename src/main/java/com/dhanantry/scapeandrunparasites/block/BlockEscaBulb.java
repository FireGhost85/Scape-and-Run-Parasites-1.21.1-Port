package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.client.particle.SRPEnumParticle;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** Esca bulb: glowing glass-like bulb that drips blood particles. */
public class BlockEscaBulb extends BlockBase {
    public BlockEscaBulb() {
        super(prop(SRPMaterial.GLASS.props(0.0f).sound(SRPSoundTypes.FLESH_LIGHT).lightLevel(s -> 15).noOcclusion(), false));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (rand.nextFloat() < 0.5f) {
            double x = pos.getX() + 0.2 + rand.nextDouble() * 0.6;
            double y = pos.getY() + 3.05 + rand.nextDouble() * 0.9;
            double z = pos.getZ() + 0.2 + rand.nextDouble() * 0.6;
            BlockClientHooks.particle(SRPEnumParticle.BLOOD, x, y, z, 0.0, 0.0, 0.0, 0, 0, 0);
        }
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }
}
