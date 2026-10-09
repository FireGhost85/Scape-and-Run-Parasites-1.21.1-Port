package com.dhanantry.scapeandrunparasites.client.legacy;


/** The part of the 1.12 ModelBiped that SRPModelBiped relies on (the humanoid parasite models build their own parts). */
public class ModelBiped extends ModelBase {
    public enum ArmPose { EMPTY, ITEM, BLOCK, BOW_AND_ARROW }

    public ArmPose leftArmPose = ArmPose.EMPTY;
    public ArmPose rightArmPose = ArmPose.EMPTY;
    public boolean isSneak;

    public ModelBiped() {
    }

    public ModelBiped(float modelSize) {
    }

    public ModelBiped(float modelSize, float p_i1149_2_, int textureWidthIn, int textureHeightIn) {
        this.textureWidth = textureWidthIn;
        this.textureHeight = textureHeightIn;
    }

    public void setInvisible(boolean invisible) {
    }
}
