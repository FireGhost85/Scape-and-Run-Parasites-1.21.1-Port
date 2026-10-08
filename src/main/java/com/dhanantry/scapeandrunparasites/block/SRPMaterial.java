package com.dhanantry.scapeandrunparasites.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

/**
 * Replacement for the 1.12 {@code Material} constants used by SRP blocks. Carries the map colour, the
 * "needs the right tool to drop" flag and the flammability that the old material implied.
 */
public enum SRPMaterial {
    ROCK(MapColor.STONE, true, false),
    GROUND(MapColor.DIRT, false, false),
    WOOD(MapColor.WOOD, false, true),
    IRON(MapColor.METAL, true, false),
    ICE(MapColor.ICE, false, false),
    CLOTH(MapColor.WOOL, false, true),
    CACTUS(MapColor.PLANT, false, false),
    CLAY(MapColor.CLAY, false, false),
    SPONGE(MapColor.COLOR_YELLOW, false, false),
    GOURD(MapColor.PLANT, false, false),
    PLANTS(MapColor.PLANT, false, true),
    VINE(MapColor.PLANT, false, true),
    LEAVES(MapColor.PLANT, false, true),
    GLASS(MapColor.NONE, false, false),
    SAND(MapColor.SAND, false, false),
    WEB(MapColor.WOOL, true, false),
    CIRCUITS(MapColor.NONE, false, false),
    PACKED_ICE(MapColor.ICE, false, false);

    private final MapColor color;
    private final boolean requiresTool;
    private final boolean burns;

    SRPMaterial(MapColor color, boolean requiresTool, boolean burns) {
        this.color = color;
        this.requiresTool = requiresTool;
        this.burns = burns;
    }

    public MapColor color() {
        return color;
    }

    /** Whether the old material required the correct tool for the block to drop. */
    public boolean requiresTool() {
        return requiresTool;
    }

    /**
     * Properties for {@code setHardness(hardness)}: in 1.12 the explosion resistance then equals the hardness
     * (blockResistance = hardness * 5, divided by 5 on read).
     */
    public BlockBehaviour.Properties props(float hardness) {
        return base().strength(hardness);
    }

    /**
     * Properties for {@code setHardness(hardness)} followed by {@code setResistance(resistance)}: 1.12 stored
     * resistance * 3 and divided it by 5 when read, so the explosion resistance of the block is 0.6 * resistance.
     */
    public BlockBehaviour.Properties props(float hardness, float resistance) {
        return base().strength(hardness, resistance * 0.6f);
    }

    private BlockBehaviour.Properties base() {
        BlockBehaviour.Properties p = BlockBehaviour.Properties.of().mapColor(color);
        if (requiresTool) {
            p.requiresCorrectToolForDrops();
        }
        if (burns) {
            p.ignitedByLava();
        }
        if (this == PLANTS || this == VINE) {
            p.pushReaction(PushReaction.DESTROY);
        }
        return p;
    }
}
