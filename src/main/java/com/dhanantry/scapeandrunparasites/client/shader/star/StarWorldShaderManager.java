package com.dhanantry.scapeandrunparasites.client.shader.star;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.client.shader.SRPPostEffect;
import com.dhanantry.scapeandrunparasites.client.world.SRPClientStarWorldState;
import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/** The cold star fog shader (shaders/post/star_cold.json): fades in outdoors in a cold star world, the held light source clears the centre. */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID, value = Dist.CLIENT)
public final class StarWorldShaderManager {
    private static final SRPPostEffect COLD = new SRPPostEffect("star_cold");
    private static float heldLightFade = 0.0f;
    private static float fade = 0.0f;
    private static Object lastWorld = null;

    private StarWorldShaderManager() {
    }

    private static BlockPos eyePos(Minecraft mc) {
        return BlockPos.containing(mc.player.getX(), mc.player.getEyeY(), mc.player.getZ());
    }

    private static boolean wanted(Minecraft mc) {
        if (!SRPConfigWorld.enableStarWorldShaders || mc.level == null || mc.player == null) {
            return false;
        }
        if (mc.level.dimension() != net.minecraft.world.level.Level.OVERWORLD || !SRPClientStarWorldState.isCold() || !SRPConfigWorld.enableColdStarFogShader) {
            return false;
        }
        return mc.level.canSeeSky(eyePos(mc));
    }

    private static float exposure(Minecraft mc) {
        BlockPos pos = eyePos(mc);
        if (!mc.level.canSeeSky(pos)) {
            return 0.0f;
        }
        return Math.max(0.0f, Math.min(1.0f, (float)mc.level.getMaxLocalRawBrightness(pos) / 15.0f));
    }

    private static float heldLightStrength(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return 0.0f;
        }
        if (stack.getItem() == Items.LAVA_BUCKET) {
            return 1.0f;
        }
        if (stack.getItem() instanceof BlockItem bi) {
            return Math.max(0.0f, Math.min(1.0f, (float)bi.getBlock().defaultBlockState().getLightEmission() / 15.0f));
        }
        return 0.0f;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        if (lastWorld != mc.level) {
            lastWorld = mc.level;
            COLD.update(false);
            fade = 0.0f;
            heldLightFade = 0.0f;
            if (mc.level == null) {
                SRPClientStarWorldState.reset();
            }
        }
        if (mc.level == null || mc.player == null) {
            return;
        }
        boolean want = wanted(mc);
        float heldTarget = want && (heldLightStrength(mc.player.getMainHandItem()) > 0.0f || heldLightStrength(mc.player.getOffhandItem()) > 0.0f) ? 1.0f : 0.0f;
        heldLightFade = Math.max(0.0f, Math.min(1.0f, heldLightFade + (heldTarget - heldLightFade) * 0.085f));
        if (want) {
            COLD.update(true);
            fade = Math.min(1.0f, fade + 0.035f);
        } else {
            fade = Math.max(0.0f, fade - 0.05f);
            if (fade <= 0.0f) {
                COLD.update(false);
                return;
            }
        }
        if (COLD.isApplied()) {
            COLD.setUniform("SRP_Time", COLD.timeSeconds());
            COLD.setUniform("SRP_Exposure", want ? exposure(mc) : 1.0f);
            COLD.setUniform("SRP_Fade", fade);
            COLD.setUniform("SRP_HandLight", heldLightFade);
        }
    }
}
