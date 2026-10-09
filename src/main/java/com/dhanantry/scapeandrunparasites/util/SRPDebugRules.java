package com.dhanantry.scapeandrunparasites.util;

import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid="srparasites")
public final class SRPDebugRules {
    public static final String RULE_FORCE_HARLEQUIN = "srpForceHarlequin";

    private SRPDebugRules() {
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load e) {
        if (e.getLevel().isClientSide) {
            return;
        }
        GameRules rules = e.getLevel().getGameRules();
        if (!rules.hasRule(RULE_FORCE_HARLEQUIN)) {
            rules.addGameRule(RULE_FORCE_HARLEQUIN, "false", GameRules.ValueType.BOOLEAN_VALUE);
        }
    }
}

