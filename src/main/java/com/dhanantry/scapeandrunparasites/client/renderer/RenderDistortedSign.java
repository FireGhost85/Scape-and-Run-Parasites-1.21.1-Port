package com.dhanantry.scapeandrunparasites.client.renderer;

import com.dhanantry.scapeandrunparasites.bestiary.client.gui.GuiDistortionHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;

/** RenderDistortedSign of 1.10.9: the text of the signs is jumbled while the distortion effect is active. The sign model itself is the vanilla renderer. */
public class RenderDistortedSign extends SignRenderer {
    public RenderDistortedSign(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    private static SignText jam(SignText text) {
        SignText out = text;
        for (int i = 0; i < 4; ++i) {
            Component line = out.getMessage(i, false);
            String s = line.getString();
            if (!s.isEmpty()) {
                out = out.setMessage(i, Component.literal(GuiDistortionHelper.jamText(s)).withStyle(line.getStyle()));
            }
        }
        return out;
    }

    @Override
    public void render(SignBlockEntity sign, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!GuiDistortionHelper.shouldDistortSigns(Minecraft.getInstance())) {
            super.render(sign, partialTick, poseStack, buffer, packedLight, packedOverlay);
            return;
        }
        SignText front = sign.frontText;
        SignText back = sign.backText;
        sign.frontText = jam(front);
        sign.backText = jam(back);
        try {
            super.render(sign, partialTick, poseStack, buffer, packedLight, packedOverlay);
        }
        finally {
            sign.frontText = front;
            sign.backText = back;
        }
    }
}
