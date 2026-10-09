package com.dhanantry.scapeandrunparasites.client.model;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

/**
 * The mobility armor model of 1.10.9 (ModelMobilityArmor), one model for the four pieces: the parts that show depend on the slot
 * (the armor layer sets the visibility of the vanilla parts, which this model copies). The boxes have fractional sizes, so the
 * model draws its own {@link Box}es with the 1.12 UV layout instead of vanilla cubes. The tentacles sway with the age of the entity
 * and its walk. Also draws the arm of the first person view ({@link #renderFirstPersonArm}).
 */
public class ModelMobilityArmor extends HumanoidModel<LivingEntity> {
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "textures/models/armor/mobility_armor.png");
    private static final float SCALE = 0.0625f;
    private static final int TEX_W = 124;
    private static final int TEX_H = 55;

    private final Part cHead;
    private final Part cBody;
    private final Part cRightArm;
    private final Part cLeftArm;
    private final Part cRightLeg;
    private final Part cLeftLeg;
    private final Part H4_r1;
    private final Part H3_r1;
    private final Part H5_r1;
    private final Part H3_r2;
    private final Part TENDRIL1;
    private final Part TDR1;
    private final Part TDR2;
    private final Part TENDRIL2;
    private final Part TDR3;
    private final Part TDR4;
    private final Part Teeth;
    private final Part H4_r2;
    private final Part H5_r2;
    private final Part H6_r1;
    private final Part B5_r1;
    private final Part B5_r2;
    private final Part TENDRIL3;
    private final Part TDR5;
    private final Part TDR6;
    private final Part TENDRIL4;
    private final Part TDR7;
    private final Part TDR8;
    private final Part B6_r1;
    private final Part B5_r3;
    private final Part B7_r1;
    private final Part B6_r2;

    private float ageInTicks;
    private float limbSwing;
    private float limbSwingAmount;
    private boolean invisible;

    public ModelMobilityArmor() {
        super(LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f), 64, 32).bakeRoot());
        this.cHead = new Part("head");
        this.cHead.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.cHead.setLocalOffset(0.0f, -6.0f, 0.0f);
        this.cHead.addBox(0, 0, -4.2f, -2.2f, -4.2f, 8.4f, 8.4f, 8.4f, 0.0f, false);
        this.cHead.addBox(24, 0, -4.0f, -1.5f, -6.1f, 8.0f, 2.0f, 2.0f, 0.0f, false);
        this.H4_r1 = new Part("H4_r1");
        this.H4_r1.setRotationPoint(-4.0f, 0.5f, 0.5f);
        this.H4_r1.setRotationAngle(0.0f, 0.3491f, 0.0f);
        this.H4_r1.addBox(44, 0, -1.2f, -1.7f, -1.7f, 2.4f, 3.4f, 3.4f, 0.0f, true);
        this.H3_r1 = new Part("H3_r1");
        this.H3_r1.setRotationPoint(4.0f, 0.5f, 0.5f);
        this.H3_r1.setRotationAngle(0.0f, -0.3491f, 0.0f);
        this.H3_r1.addBox(32, 8, -1.2f, -1.7f, -1.7f, 2.4f, 3.4f, 3.4f, 0.0f, false);
        this.H5_r1 = new Part("H5_r1");
        this.H5_r1.setRotationPoint(-0.1f, 4.9183f, -4.7977f);
        this.H5_r1.setRotationAngle(-0.3491f, 0.0f, 0.0f);
        this.H5_r1.addBox(0, 0, 2.4f, -1.0f, 0.0f, 2.2f, 2.0f, 2.0f, 0.0f, true);
        this.H5_r1.addBox(0, 4, -4.4f, -1.0f, 0.0f, 2.2f, 2.0f, 2.0f, 0.0f, false);
        this.H3_r2 = new Part("H3_r2");
        this.H3_r2.setRotationPoint(0.0f, -0.9183f, -4.7977f);
        this.H3_r2.setRotationAngle(0.3491f, 0.0f, 0.0f);
        this.H3_r2.addBox(24, 4, -4.1f, -1.0f, -1.0f, 8.2f, 2.0f, 2.0f, 0.0f, false);
        this.TENDRIL1 = new Part("TENDRIL1");
        this.TENDRIL1.setRotationPoint(5.25f, 0.75f, 1.5f);
        this.TENDRIL1.setRotationAngle(-0.3927f, 0.1745f, 0.0f);
        this.TENDRIL1.addBox(76, 0, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 6.4f, 0.0f, false);
        this.TENDRIL1.addBox(86, 0, -0.95f, -1.05f, -1.8f, 2.2f, 2.2f, 2.1f, 0.0f, false);
        this.TDR1 = new Part("TDR1");
        this.TDR1.setRotationPoint(0.05f, 0.05f, 6.5f);
        this.TDR1.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR1.addBox(92, 0, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 6.4f, 0.0f, false);
        this.TDR2 = new Part("TDR2");
        this.TDR2.setRotationPoint(0.0f, 0.0f, 6.3f);
        this.TDR2.setRotationAngle(0.3927f, 0.0f, 0.0f);
        this.TDR2.addBox(108, 0, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 7.4f, 0.0f, false);
        this.TENDRIL2 = new Part("TENDRIL2");
        this.TENDRIL2.setRotationPoint(-5.25f, 0.75f, 1.5f);
        this.TENDRIL2.setRotationAngle(-0.3927f, -0.2618f, 0.0f);
        this.TENDRIL2.addBox(76, 8, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 6.4f, 0.0f, true);
        this.TENDRIL2.addBox(86, 8, -1.25f, -1.05f, -1.8f, 2.2f, 2.2f, 2.1f, 0.0f, true);
        this.TDR3 = new Part("TDR3");
        this.TDR3.setRotationPoint(-0.05f, 0.05f, 6.5f);
        this.TDR3.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR3.addBox(92, 8, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 6.4f, 0.0f, true);
        this.TDR4 = new Part("TDR4");
        this.TDR4.setRotationPoint(0.0f, 0.0f, 6.3f);
        this.TDR4.setRotationAngle(0.3927f, 0.0f, 0.0f);
        this.TDR4.addBox(108, 8, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 7.4f, 0.0f, true);
        this.Teeth = new Part("Teeth");
        this.Teeth.setRotationPoint(0.0f, 1.4f, 0.3f);
        this.H4_r2 = new Part("H4_r2");
        this.H4_r2.setRotationPoint(0.0f, 1.1f, -5.8f);
        this.H4_r2.setRotationAngle(-0.0873f, 0.0f, 0.0f);
        this.H4_r2.addBox(76, 0, 1.8f, -2.1f, -0.6f, 2.0f, 4.0f, 1.0f, 0.0f, true);
        this.H4_r2.addBox(54, 0, -3.8f, -2.1f, -0.6f, 2.0f, 4.0f, 1.0f, 0.0f, false);
        this.H5_r2 = new Part("H5_r2");
        this.H5_r2.setRotationPoint(0.0f, 0.6f, -5.8f);
        this.H5_r2.setRotationAngle(0.0873f, 0.0f, 0.0f);
        this.H5_r2.addBox(72, 0, 0.3f, -1.6f, -0.4f, 1.0f, 3.0f, 1.0f, 0.0f, true);
        this.H5_r2.addBox(60, 0, -1.3f, -1.6f, -0.4f, 1.0f, 3.0f, 1.0f, 0.0f, false);
        this.H6_r1 = new Part("H6_r1");
        this.H6_r1.setRotationPoint(0.0f, 2.6f, -5.5f);
        this.H6_r1.setRotationAngle(-0.0873f, 0.0f, 0.0f);
        this.H6_r1.addBox(68, 0, 3.3f, -1.5f, -0.5f, 1.0f, 3.0f, 1.0f, 0.0f, true);
        this.H6_r1.addBox(64, 0, -4.3f, -1.5f, -0.5f, 1.0f, 3.0f, 1.0f, 0.0f, false);
        this.cLeftArm = new Part("leftArm");
        this.cLeftArm.setRotationPoint(5.0f, 2.0f, 0.0f);
        this.cLeftArm.addBox(108, 16, -1.2f, -2.1f, -2.2f, 4.4f, 12.2f, 4.4f, 0.0f, false);
        this.cLeftArm.addBox(0, 29, 2.6f, 3.1f, -2.5f, 1.5f, 8.0f, 5.0f, 0.0f, false);
        this.cLeftArm.addBox(87, 19, -0.9f, -2.3f, -2.5f, 5.0f, 5.0f, 5.0f, 0.0f, false);
        this.cLeftArm.addBox(24, 16, -1.4f, 8.1f, -2.3f, 2.0f, 2.2f, 2.0f, 0.0f, false);
        this.cLeftArm.addBox(0, 19, 0.9f, 8.1f, -2.3f, 2.5f, 2.2f, 1.2f, 0.0f, false);
        this.cLeftArm.addBox(0, 16, 0.9f, 8.1f, -0.8f, 2.5f, 2.2f, 1.2f, 0.0f, false);
        this.cLeftArm.addBox(54, 23, 0.9f, 8.1f, 0.7f, 2.5f, 2.2f, 1.2f, 0.0f, false);
        this.cRightArm = new Part("rightArm");
        this.cRightArm.setRotationPoint(-5.0f, 2.0f, 0.0f);
        this.cRightArm.addBox(28, 28, -3.2f, -2.1f, -2.2f, 4.4f, 12.2f, 4.4f, 0.0f, true);
        this.cRightArm.addBox(44, 28, -4.1f, 3.1f, -2.5f, 1.5f, 8.0f, 5.0f, 0.0f, true);
        this.cRightArm.addBox(56, 27, -4.1f, -2.3f, -2.5f, 5.0f, 5.0f, 5.0f, 0.0f, true);
        this.cRightArm.addBox(12, 16, -0.6f, 8.1f, -2.3f, 2.0f, 2.2f, 2.0f, 0.0f, true);
        this.cRightArm.addBox(44, 6, -3.4f, 8.1f, -2.3f, 2.5f, 2.2f, 1.2f, 0.0f, true);
        this.cRightArm.addBox(76, 8, -3.4f, 8.1f, -0.8f, 2.5f, 2.2f, 1.2f, 0.0f, true);
        this.cRightArm.addBox(76, 11, -3.4f, 8.1f, 0.7f, 2.5f, 2.2f, 1.2f, 0.0f, true);
        this.cLeftArm.setRenderScale(1.1f, 1.02f, 1.1f);
        this.cRightArm.setRenderScale(1.1f, 1.02f, 1.1f);
        this.cBody = new Part("body");
        this.cBody.setRotationPoint(0.0f, 0.0f, 0.0f);
        this.cBody.setLocalOffset(0.0f, 24.0f, 0.0f);
        this.cBody.addBox(0, 16, 4.0f, -25.0f, -3.0f, 2.0f, 7.0f, 6.0f, 0.0f, false);
        this.cBody.addBox(12, 26, -6.0f, -25.0f, -3.0f, 2.0f, 7.0f, 6.0f, 0.0f, false);
        this.cBody.addBox(32, 14, -4.0f, -24.0f, -3.0f, 8.0f, 8.0f, 1.0f, 0.0f, false);
        this.cBody.addBox(16, 16, 3.5f, -18.0f, -2.0f, 1.0f, 6.0f, 4.0f, 0.0f, false);
        this.cBody.addBox(22, 22, -4.5f, -18.0f, -2.0f, 1.0f, 6.0f, 4.0f, 0.0f, false);
        this.cBody.addBox(58, 5, -4.0f, -24.0f, 2.0f, 8.0f, 10.0f, 1.0f, 0.0f, false);
        this.cBody.addBox(50, 16, 0.5f, -24.0f, 3.0f, 4.0f, 4.0f, 2.0f, 0.0f, false);
        this.cBody.addBox(62, 16, -4.5f, -24.0f, 3.0f, 4.0f, 4.0f, 2.0f, 0.0f, false);
        this.cBody.addBox(32, 23, -4.0f, -24.5f, -2.0f, 8.0f, 1.0f, 4.0f, 0.0f, false);
        this.cBody.addBox(56, 22, -4.0f, -12.9f, -2.0f, 8.0f, 1.0f, 4.0f, 0.0f, false);
        this.cBody.addBox(74, 19, -4.0f, -16.0f, -2.7f, 8.0f, 2.0f, 1.0f, 0.0f, false);
        this.cBody.addBox(92, 16, -4.0f, -14.0f, -2.3f, 8.0f, 2.0f, 1.0f, 0.0f, false);
        this.cBody.addBox(74, 16, -4.0f, -14.0f, 1.7f, 8.0f, 2.0f, 1.0f, 0.0f, false);
        this.cBody.addBox(42, 9, -1.7f, -22.0f, -3.9f, 3.5f, 4.0f, 1.0f, 0.0f, false);
        this.B5_r1 = new Part("B5_r1");
        this.B5_r1.setRotationPoint(2.5f, -22.5f, -3.5f);
        this.B5_r1.setRotationAngle(0.0f, 0.3927f, 0.0f);
        this.B5_r1.addBox(50, 8, -1.5f, 1.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, true);
        this.B5_r1.addBox(50, 10, -1.5f, 3.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, true);
        this.B5_r2 = new Part("B5_r2");
        this.B5_r2.setRotationPoint(-2.5f, -22.5f, -3.5f);
        this.B5_r2.setRotationAngle(0.0f, -0.3927f, 0.0f);
        this.B5_r2.addBox(50, 12, -1.5f, 3.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, false);
        this.B5_r2.addBox(50, 14, -1.5f, 1.0f, 0.1f, 3.0f, 1.0f, 1.0f, 0.0f, false);
        this.TENDRIL3 = new Part("TENDRIL3");
        this.TENDRIL3.setRotationPoint(-2.25f, -22.25f, 3.5f);
        this.TENDRIL3.setRotationAngle(-0.6981f, -0.5236f, 0.0f);
        this.TENDRIL3.addBox(87, 29, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 7.4f, 0.0f, true);
        this.TDR5 = new Part("TDR5");
        this.TDR5.setRotationPoint(-0.05f, 0.05f, 7.5f);
        this.TDR5.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR5.addBox(70, 36, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 7.4f, 0.0f, true);
        this.TDR6 = new Part("TDR6");
        this.TDR6.setRotationPoint(0.0f, 0.0f, 7.3f);
        this.TDR6.setRotationAngle(-0.2182f, 0.0f, 0.0f);
        this.TDR6.addBox(96, 29, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 9.4f, 0.0f, true);
        this.TENDRIL4 = new Part("TENDRIL4");
        this.TENDRIL4.setRotationPoint(2.25f, -22.25f, 3.5f);
        this.TENDRIL4.setRotationAngle(-0.6981f, 0.5236f, 0.0f);
        this.TENDRIL4.addBox(81, 44, -1.45f, -1.45f, 0.3f, 2.9f, 2.9f, 7.4f, 0.0f, false);
        this.TDR7 = new Part("TDR7");
        this.TDR7.setRotationPoint(0.05f, 0.05f, 7.5f);
        this.TDR7.setRotationAngle(-0.3054f, 0.0f, 0.0f);
        this.TDR7.addBox(59, 38, -1.2f, -1.2f, 0.1f, 2.4f, 2.4f, 7.4f, 0.0f, false);
        this.TDR8 = new Part("TDR8");
        this.TDR8.setRotationPoint(0.0f, 0.0f, 7.3f);
        this.TDR8.setRotationAngle(-0.2182f, 0.0f, 0.0f);
        this.TDR8.addBox(48, 42, -0.8f, -0.8f, 0.1f, 1.6f, 1.6f, 9.4f, 0.0f, false);
        this.cLeftLeg = new Part("leftLeg");
        this.cLeftLeg.setRotationPoint(2.0f, 12.0f, 0.0f);
        this.cLeftLeg.addBox(16, 40, -2.1f, -0.1f, -2.1f, 4.2f, 11.1f, 4.2f, 0.0f, false);
        this.cLeftLeg.addBox(76, 29, -2.2f, 10.0785f, -3.0892f, 4.4f, 2.0f, 5.3f, 0.0f, false);
        this.cLeftLeg.addBox(14, 29, 0.2f, 10.3785f, -4.2892f, 1.7f, 2.0f, 1.2f, 0.0f, false);
        this.cLeftLeg.addBox(28, 20, -1.9f, 10.1785f, -4.4892f, 1.7f, 2.2f, 1.4f, 0.0f, false);
        this.cLeftLeg.addBox(28, 23, -2.9f, 10.1785f, 0.2108f, 1.7f, 2.2f, 1.4f, 0.0f, false);
        this.B6_r1 = new Part("B6_r1");
        this.B6_r1.setRotationPoint(0.0f, 5.0419f, 4.9088f);
        this.B6_r1.setRotationAngle(-0.2618f, 0.0f, 0.0f);
        this.B6_r1.addBox(55, 27, -1.2f, 2.7f, -2.4f, 2.4f, 4.0f, 1.4f, 0.0f, false);
        this.B5_r3 = new Part("B5_r3");
        this.B5_r3.setRotationPoint(0.0f, 4.0f, -1.0f);
        this.B5_r3.setRotationAngle(-0.1745f, 0.0f, 0.0f);
        this.B5_r3.addBox(0, 42, -2.2f, -4.0f, -2.0f, 4.4f, 8.0f, 4.0f, 0.0f, false);
        this.cRightLeg = new Part("rightLeg");
        this.cRightLeg.setRotationPoint(-2.0f, 12.0f, 0.0f);
        this.cRightLeg.addBox(108, 39, -2.1f, -0.1f, -2.1f, 4.2f, 11.1f, 4.2f, 0.0f, true);
        this.cRightLeg.addBox(39, 41, -2.2f, 10.0785f, -3.0892f, 4.4f, 2.0f, 5.3f, 0.0f, true);
        this.cRightLeg.addBox(62, 42, -1.9f, 10.3785f, -4.2892f, 1.7f, 2.0f, 1.2f, 0.0f, true);
        this.cRightLeg.addBox(62, 39, 0.2f, 10.1785f, -4.4892f, 1.7f, 2.2f, 1.4f, 0.0f, true);
        this.cRightLeg.addBox(56, 37, 1.2f, 10.1785f, 0.2108f, 1.7f, 2.2f, 1.4f, 0.0f, true);
        this.B7_r1 = new Part("B7_r1");
        this.B7_r1.setRotationPoint(0.0f, 5.0419f, 4.9088f);
        this.B7_r1.setRotationAngle(-0.2618f, 0.0f, 0.0f);
        this.B7_r1.addBox(71, 38, -1.2f, 2.7f, -2.4f, 2.4f, 4.0f, 1.4f, 0.0f, true);
        this.B6_r2 = new Part("B6_r2");
        this.B6_r2.setRotationPoint(0.0f, 4.0f, -1.0f);
        this.B6_r2.setRotationAngle(-0.1745f, 0.0f, 0.0f);
        this.B6_r2.addBox(92, 39, -2.2f, -4.0f, -2.0f, 4.4f, 8.0f, 4.0f, 0.0f, true);
        this.cHead.addChild(this.H4_r1);
        this.cHead.addChild(this.H3_r1);
        this.cHead.addChild(this.H5_r1);
        this.cHead.addChild(this.H3_r2);
        this.cHead.addChild(this.TENDRIL1);
        this.TENDRIL1.addChild(this.TDR1);
        this.TDR1.addChild(this.TDR2);
        this.cHead.addChild(this.TENDRIL2);
        this.TENDRIL2.addChild(this.TDR3);
        this.TDR3.addChild(this.TDR4);
        this.cHead.addChild(this.Teeth);
        this.Teeth.addChild(this.H4_r2);
        this.Teeth.addChild(this.H5_r2);
        this.Teeth.addChild(this.H6_r1);
        this.cBody.addChild(this.B5_r1);
        this.cBody.addChild(this.B5_r2);
        this.cBody.addChild(this.TENDRIL3);
        this.TENDRIL3.addChild(this.TDR5);
        this.TDR5.addChild(this.TDR6);
        this.cBody.addChild(this.TENDRIL4);
        this.TENDRIL4.addChild(this.TDR7);
        this.TDR7.addChild(this.TDR8);
        this.cLeftLeg.addChild(this.B6_r1);
        this.cLeftLeg.addChild(this.B5_r3);
        this.cRightLeg.addChild(this.B7_r1);
        this.cRightLeg.addChild(this.B6_r2);

    }

    /** Called by the item extension before the model is used for an entity (the pose comes from NeoForge, which copies it from the vanilla armor model). */
    public ModelMobilityArmor prepare(LivingEntity entity, float partialTick) {
        this.invisible = entity.isInvisible();
        this.ageInTicks = (float) entity.tickCount + partialTick;
        this.limbSwing = entity.walkAnimation.position(partialTick);
        this.limbSwingAmount = entity.walkAnimation.speed(partialTick);
        return this;
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer buffer, int light, int overlay, int color) {
        if (this.invisible) {
            return;
        }
        this.syncPose();
        this.animateTentacles();
        pose.pushPose();
        if (this.crouching) {
            pose.translate(0.0f, 0.2f, 0.0f);
        }
        this.cHead.render(pose, buffer, light, overlay, color);
        this.cBody.render(pose, buffer, light, overlay, color);
        this.cRightArm.render(pose, buffer, light, overlay, color);
        this.cLeftArm.render(pose, buffer, light, overlay, color);
        this.cRightLeg.render(pose, buffer, light, overlay, color);
        this.cLeftLeg.render(pose, buffer, light, overlay, color);
        pose.popPose();
    }

    /** The arm of the first person view: the arm part placed at its resting point with no rotation, the caller has applied the arm transform. */
    public void renderFirstPersonArm(PoseStack pose, VertexConsumer buffer, int light, int overlay, int color, HumanoidArm side) {
        Part arm = side == HumanoidArm.RIGHT ? this.cRightArm : this.cLeftArm;
        boolean oldShow = arm.showModel;
        float px = arm.rotationPointX;
        float py = arm.rotationPointY;
        float pz = arm.rotationPointZ;
        float rx = arm.rotationX;
        float ry = arm.rotationY;
        float rz = arm.rotationZ;
        arm.showModel = true;
        arm.rotationPointX = side == HumanoidArm.RIGHT ? -5.0f : 5.0f;
        arm.rotationPointY = 2.0f;
        arm.rotationPointZ = 0.0f;
        arm.rotationX = 0.0f;
        arm.rotationY = 0.0f;
        arm.rotationZ = 0.0f;
        arm.render(pose, buffer, light, overlay, color);
        arm.showModel = oldShow;
        arm.rotationPointX = px;
        arm.rotationPointY = py;
        arm.rotationPointZ = pz;
        arm.rotationX = rx;
        arm.rotationY = ry;
        arm.rotationZ = rz;
    }

    // ------------------------------------------------------------------ animation

    private void animateTentacles() {
        float move = Mth.clamp(this.limbSwingAmount, 0.0f, 1.0f);
        float idleSpeed = this.ageInTicks * 0.095f;
        float walkSpeed = this.limbSwing * 0.38f;
        float strength = 0.045f + move * 0.09f;
        float walkStrength = move * 0.075f;
        this.swayChain(this.TENDRIL1, this.TDR1, this.TDR2, idleSpeed, walkSpeed, strength, walkStrength, 0.0f, 1.0f);
        this.swayChain(this.TENDRIL2, this.TDR3, this.TDR4, idleSpeed, walkSpeed, strength, walkStrength, 1.6f, -1.0f);
        this.swayChain(this.TENDRIL3, this.TDR5, this.TDR6, idleSpeed, walkSpeed, strength * 1.25f, walkStrength * 1.15f, 3.1f, -1.0f);
        this.swayChain(this.TENDRIL4, this.TDR7, this.TDR8, idleSpeed, walkSpeed, strength * 1.25f, walkStrength * 1.15f, 4.7f, 1.0f);
    }

    private void swayChain(Part base, Part mid, Part tip, float idleSpeed, float walkSpeed, float strength, float walkStrength, float phase, float side) {
        float idleA = Mth.sin(idleSpeed + phase);
        float idleB = Mth.sin(idleSpeed * 1.23f + phase + 0.85f);
        float idleC = Mth.sin(idleSpeed * 1.47f + phase + 1.7f);
        float walkA = Mth.sin(walkSpeed + phase) * walkStrength;
        base.rotationX = base.baseRotationX + idleB * strength * 0.55f + walkA * 0.45f;
        base.rotationY = base.baseRotationY + side * (idleA * strength * 0.95f + walkA * 0.65f);
        base.rotationZ = base.baseRotationZ + side * idleC * strength * 0.35f;
        mid.rotationX = mid.baseRotationX + idleB * strength * 1.05f + walkA * 0.75f;
        mid.rotationY = mid.baseRotationY + side * (idleC * strength * 1.15f + walkA * 0.85f);
        mid.rotationZ = mid.baseRotationZ + side * idleA * strength * 0.55f;
        tip.rotationX = tip.baseRotationX + idleC * strength * 1.65f + walkA;
        tip.rotationY = tip.baseRotationY + side * (idleB * strength * 1.45f + walkA);
        tip.rotationZ = tip.baseRotationZ + side * idleA * strength * 0.75f;
    }

    private void syncPose() {
        this.copyPose(this.head, this.cHead);
        this.copyPose(this.body, this.cBody);
        this.copyPose(this.rightArm, this.cRightArm);
        this.copyPose(this.leftArm, this.cLeftArm);
        this.copyPose(this.rightLeg, this.cRightLeg);
        this.copyPose(this.leftLeg, this.cLeftLeg);
        this.cHead.showModel = this.head.visible && this.cHead.slotVisible;
        this.cBody.showModel = this.body.visible && this.cBody.slotVisible;
        this.cRightArm.showModel = this.rightArm.visible && this.cRightArm.slotVisible;
        this.cLeftArm.showModel = this.leftArm.visible && this.cLeftArm.slotVisible;
        this.cRightLeg.showModel = this.rightLeg.visible && this.cRightLeg.slotVisible;
        this.cLeftLeg.showModel = this.leftLeg.visible && this.cLeftLeg.slotVisible;
    }

    private void copyPose(ModelPart vanilla, Part custom) {
        custom.rotationPointX = vanilla.x;
        custom.rotationPointY = vanilla.y;
        custom.rotationPointZ = vanilla.z;
        custom.rotationX = custom.baseRotationX + vanilla.xRot;
        custom.rotationY = custom.baseRotationY + vanilla.yRot;
        custom.rotationZ = custom.baseRotationZ + vanilla.zRot;
    }

    // ------------------------------------------------------------------ boxes and parts of 1.10.9 (float sizes)

    private static final class Box {
        private final int texU;
        private final int texV;
        private final float x1;
        private final float y1;
        private final float z1;
        private final float x2;
        private final float y2;
        private final float z2;
        private final float uvW;
        private final float uvH;
        private final float uvD;
        private final boolean mirror;

        private Box(int texU, int texV, float x, float y, float z, float width, float height, float depth, float inflate, boolean mirror) {
            this.texU = texU;
            this.texV = texV;
            this.uvW = width + inflate * 2.0f;
            this.uvH = height + inflate * 2.0f;
            this.uvD = depth + inflate * 2.0f;
            this.mirror = mirror;
            float minX = x - inflate;
            float minY = y - inflate;
            float minZ = z - inflate;
            float maxX = x + width + inflate;
            float maxY = y + height + inflate;
            float maxZ = z + depth + inflate;
            if (mirror) {
                float oldMinX = minX;
                minX = maxX;
                maxX = oldMinX;
            }
            this.x1 = minX;
            this.y1 = minY;
            this.z1 = minZ;
            this.x2 = maxX;
            this.y2 = maxY;
            this.z2 = maxZ;
        }

        private void render(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay, int color) {
            float x1 = this.x1 * SCALE;
            float y1 = this.y1 * SCALE;
            float z1 = this.z1 * SCALE;
            float x2 = this.x2 * SCALE;
            float y2 = this.y2 * SCALE;
            float z2 = this.z2 * SCALE;
            // north
            float u1 = (float) this.texU + this.uvD;
            float v1 = (float) this.texV + this.uvD;
            float u2 = u1 + this.uvW;
            float v2 = v1 + this.uvH;
            this.face(pose, vc, light, overlay, color, 0.0f, 0.0f, -1.0f, x2, y1, z1, u1, v1, x1, y1, z1, u2, v1, x1, y2, z1, u2, v2, x2, y2, z1, u1, v2);
            // south
            u1 = (float) this.texU + this.uvD + this.uvW + this.uvD;
            v1 = (float) this.texV + this.uvD;
            u2 = u1 + this.uvW;
            v2 = v1 + this.uvH;
            this.face(pose, vc, light, overlay, color, 0.0f, 0.0f, 1.0f, x1, y1, z2, u1, v1, x2, y1, z2, u2, v1, x2, y2, z2, u2, v2, x1, y2, z2, u1, v2);
            // west
            u1 = this.texU;
            v1 = (float) this.texV + this.uvD;
            u2 = u1 + this.uvD;
            v2 = v1 + this.uvH;
            this.face(pose, vc, light, overlay, color, -1.0f, 0.0f, 0.0f, x1, y1, z1, u1, v1, x1, y1, z2, u2, v1, x1, y2, z2, u2, v2, x1, y2, z1, u1, v2);
            // east
            u1 = (float) this.texU + this.uvD + this.uvW;
            v1 = (float) this.texV + this.uvD;
            u2 = u1 + this.uvD;
            v2 = v1 + this.uvH;
            this.face(pose, vc, light, overlay, color, 1.0f, 0.0f, 0.0f, x2, y1, z2, u1, v1, x2, y1, z1, u2, v1, x2, y2, z1, u2, v2, x2, y2, z2, u1, v2);
            // up
            u1 = (float) this.texU + this.uvD;
            v1 = this.texV;
            u2 = u1 + this.uvW;
            v2 = v1 + this.uvD;
            this.face(pose, vc, light, overlay, color, 0.0f, -1.0f, 0.0f, x1, y1, z2, u1, v1, x2, y1, z2, u2, v1, x2, y1, z1, u2, v2, x1, y1, z1, u1, v2);
            // down
            u1 = (float) this.texU + this.uvD + this.uvW;
            v1 = this.texV;
            u2 = u1 + this.uvW;
            v2 = v1 + this.uvD;
            this.face(pose, vc, light, overlay, color, 0.0f, 1.0f, 0.0f, x1, y2, z1, u1, v2, x2, y2, z1, u2, v2, x2, y2, z2, u2, v1, x1, y2, z2, u1, v1);
        }

        private void face(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay, int color, float nx, float ny, float nz,
                float ax, float ay, float az, float au, float av, float bx, float by, float bz, float bu, float bv,
                float cx, float cy, float cz, float cu, float cv, float dx, float dy, float dz, float du, float dv) {
            float n = this.mirror ? -nx : nx;
            this.vertex(pose, vc, light, overlay, color, n, ny, nz, ax, ay, az, au, av);
            this.vertex(pose, vc, light, overlay, color, n, ny, nz, bx, by, bz, bu, bv);
            this.vertex(pose, vc, light, overlay, color, n, ny, nz, cx, cy, cz, cu, cv);
            this.vertex(pose, vc, light, overlay, color, n, ny, nz, dx, dy, dz, du, dv);
        }

        private void vertex(PoseStack.Pose pose, VertexConsumer vc, int light, int overlay, int color, float nx, float ny, float nz, float x, float y, float z, float u, float v) {
            vc.addVertex(pose, x, y, z).setColor(color).setUv(u / (float) TEX_W, v / (float) TEX_H).setOverlay(overlay).setLight(light).setNormal(pose, nx, ny, nz);
        }
    }

    private static final class Part {
        private final List<Box> boxes = new ArrayList<>();
        private final List<Part> children = new ArrayList<>();
        private float rotationPointX;
        private float rotationPointY;
        private float rotationPointZ;
        private float renderScaleX = 1.0f;
        private float renderScaleY = 1.0f;
        private float renderScaleZ = 1.0f;
        private float localOffsetX;
        private float localOffsetY;
        private float localOffsetZ;
        private float baseRotationX;
        private float baseRotationY;
        private float baseRotationZ;
        private float rotationX;
        private float rotationY;
        private float rotationZ;
        private boolean showModel = true;
        /** The visibility the slot gives the part (the vanilla part visibility is read at render time). */
        private boolean slotVisible = true;

        private Part(String name) {
        }

        private void setRenderScale(float x, float y, float z) {
            this.renderScaleX = x;
            this.renderScaleY = y;
            this.renderScaleZ = z;
        }

        private void setRotationPoint(float x, float y, float z) {
            this.rotationPointX = x;
            this.rotationPointY = y;
            this.rotationPointZ = z;
        }

        private void setLocalOffset(float x, float y, float z) {
            this.localOffsetX = x;
            this.localOffsetY = y;
            this.localOffsetZ = z;
        }

        private void setRotationAngle(float x, float y, float z) {
            this.baseRotationX = x;
            this.baseRotationY = y;
            this.baseRotationZ = z;
            this.rotationX = x;
            this.rotationY = y;
            this.rotationZ = z;
        }

        private void addChild(Part child) {
            this.children.add(child);
        }

        private void addBox(int texU, int texV, float x, float y, float z, float width, float height, float depth, float inflate, boolean mirror) {
            this.boxes.add(new Box(texU, texV, x, y, z, width, height, depth, inflate, mirror));
        }

        private void render(PoseStack pose, VertexConsumer vc, int light, int overlay, int color) {
            if (!this.showModel) {
                return;
            }
            pose.pushPose();
            pose.translate(this.rotationPointX * SCALE, this.rotationPointY * SCALE, this.rotationPointZ * SCALE);
            if (this.rotationZ != 0.0f) {
                pose.mulPose(Axis.ZP.rotation(this.rotationZ));
            }
            if (this.rotationY != 0.0f) {
                pose.mulPose(Axis.YP.rotation(this.rotationY));
            }
            if (this.rotationX != 0.0f) {
                pose.mulPose(Axis.XP.rotation(this.rotationX));
            }
            if (this.renderScaleX != 1.0f || this.renderScaleY != 1.0f || this.renderScaleZ != 1.0f) {
                pose.scale(this.renderScaleX, this.renderScaleY, this.renderScaleZ);
            }
            if (this.localOffsetX != 0.0f || this.localOffsetY != 0.0f || this.localOffsetZ != 0.0f) {
                pose.translate(this.localOffsetX * SCALE, this.localOffsetY * SCALE, this.localOffsetZ * SCALE);
            }
            PoseStack.Pose last = pose.last();
            for (Box box : this.boxes) {
                box.render(last, vc, light, overlay, color);
            }
            for (Part child : this.children) {
                child.render(pose, vc, light, overlay, color);
            }
            pose.popPose();
        }
    }
}
