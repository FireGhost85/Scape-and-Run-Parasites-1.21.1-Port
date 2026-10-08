package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.entity.tile.TileEntityTrophy;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.InteractionResult;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;

/** Trophy block: renders a mob (block entity renderer) and plays its living sound when used. */
public class BlockEntityTrophy extends Block implements EntityBlock {
    public static final MapCodec<BlockEntityTrophy> CODEC = simpleCodec(p -> new BlockEntityTrophy(p, "minecraft:pig", false, TrophyTextureMode.DEFAULT, false, 0.0f, 1.0f));

    private final ResourceLocation mobId;
    private final boolean animate;
    private final TrophyTextureMode textureMode;
    private final boolean hasSounds;
    private final float yOffset;
    private final float scale;

    public BlockEntityTrophy(BlockBehaviour.Properties properties, String mobId, boolean animate, TrophyTextureMode textureMode, boolean hasSounds, float yOffset, float scale) {
        super(properties);
        this.mobId = ResourceLocation.parse(mobId);
        this.animate = animate;
        this.textureMode = textureMode;
        this.hasSounds = hasSounds;
        this.yOffset = yOffset;
        this.scale = scale;
    }

    public BlockEntityTrophy(SRPMaterial material, String mobId, boolean animate, TrophyTextureMode textureMode, boolean hasSounds, float yOffset, float scale) {
        this(material.props(2.0f, 10.0f).sound(SoundType.STONE), mobId, animate, textureMode, hasSounds, yOffset, scale);
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityTrophy(pos, state);
    }

    public ResourceLocation getMobId() {
        return this.mobId;
    }

    public boolean isAnimate() {
        return this.animate;
    }

    public TrophyTextureMode getTextureMode() {
        return this.textureMode;
    }

    public boolean hasSounds() {
        return this.hasSounds;
    }

    public float getYOffset() {
        return this.yOffset;
    }

    public float getScale() {
        return this.scale;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.phys.BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!this.hasSounds) {
            return InteractionResult.SUCCESS;
        }
        Entity e = BuiltInRegistries.ENTITY_TYPE.get(this.mobId).create(level);
        if (e instanceof Mob living) {
            living.setPos(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            living.playAmbientSound();
        }
        return InteractionResult.SUCCESS;
    }

    public enum TrophyTextureMode {
        DEFAULT,
        STONE
    }
}
