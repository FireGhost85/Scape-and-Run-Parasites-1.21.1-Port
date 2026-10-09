package com.dhanantry.scapeandrunparasites.util;

import net.minecraft.world.level.GameRules;

/** The game rule of the original that forces the harlequin biome onto every converted column ({@code /gamerule srpForceHarlequin}). */
public final class SRPDebugRules {
    public static final String RULE_FORCE_HARLEQUIN = "srpForceHarlequin";
    public static final GameRules.Key<GameRules.BooleanValue> FORCE_HARLEQUIN = GameRules.register(RULE_FORCE_HARLEQUIN, GameRules.Category.MISC, GameRules.BooleanValue.create(false));

    private SRPDebugRules() {
    }

    /** Loads the class (the rule has to be registered before a world is created). */
    public static void init() {
    }
}
