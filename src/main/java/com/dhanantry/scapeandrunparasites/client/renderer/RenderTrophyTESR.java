package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.block.BlockEntityTrophy;
import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityTrophy;
import com.dhanantry.scapeandrunparasites.util.SRPEntityUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/** RenderTrophyTESR of 1.10.9: the trophy blocks draw their mob (frozen, or animated), scaled and offset by the block. */
public class RenderTrophyTESR implements BlockEntityRenderer<TileEntityTrophy> {
    public RenderTrophyTESR(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEntityTrophy te, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Level world = te.getLevel();
        if (world == null) {
            return;
        }
        BlockState state = te.getBlockState();
        if (!(state.getBlock() instanceof BlockEntityTrophy trophy)) {
            return;
        }
        Entity entity = this.getOrCreateEntity(te, trophy.getMobId());
        if (entity == null) {
            return;
        }
        entity.setSilent(true);
        entity.noPhysics = true;
        if (!trophy.isAnimate()) {
            entity.tickCount = 0;
        } else {
            entity.tickCount = (int)(world.getGameTime() % 100000L);
        }
        float yaw = Minecraft.getInstance().player != null ? Minecraft.getInstance().player.getYRot() : 0.0f;
        entity.setYRot(-yaw);
        entity.yRotO = -yaw;
        int light = packedLight;
        if (trophy.getTextureMode() == BlockEntityTrophy.TrophyTextureMode.STONE) {
            int b = (int)(LightTexture.block(light) * 0.75f);
            int s = (int)(LightTexture.sky(light) * 0.75f);
            light = LightTexture.pack(b, s);
        }
        poseStack.pushPose();
        poseStack.translate(0.5f, 1.0f + trophy.getYOffset(), 0.5f);
        float s = trophy.getScale();
        poseStack.scale(s, s, s);
        Minecraft.getInstance().getEntityRenderDispatcher().render(entity, 0.0, 0.0, 0.0, 0.0f, partialTick, poseStack, buffer, light);
        poseStack.popPose();
    }

    private Entity getOrCreateEntity(TileEntityTrophy te, ResourceLocation mobId) {
        if (te.cachedRenderEntity != null) {
            return te.cachedRenderEntity;
        }
        Level w = Minecraft.getInstance().level;
        if (w == null) {
            return null;
        }
        Entity e = SRPEntityUtil.create(mobId, w);
        if (e == null) {
            return null;
        }
        if (e instanceof Mob mob) {
            mob.setNoAi(true);
        }
        te.cachedRenderEntity = e;
        return e;
    }
}
