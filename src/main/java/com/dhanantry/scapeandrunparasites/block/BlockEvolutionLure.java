package com.dhanantry.scapeandrunparasites.block;

import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPEntities;
import com.dhanantry.scapeandrunparasites.init.SRPSoundTypes;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.PacketDistributor;

/** Evolution lure: right clicked with an empty hand in a square of four, it pauses the evolution points. */
public class BlockEvolutionLure extends BlockBase implements IVariantBlock<BlockEvolutionLure.EnumType> {
    public static final EnumProperty<EnumType> VARIANT = EnumProperty.create("variant", EnumType.class);

    public BlockEvolutionLure(SRPMaterial material, float hardness, boolean tickRandom) {
        super(prop(material.props(hardness).sound(SRPSoundTypes.LURE).lightLevel(s -> (int) (15 * 0.1f)), tickRandom));
        this.registerDefaultState(this.stateDefinition.any().setValue(VARIANT, EnumType.ONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    protected boolean raisesBreakEvent() {
        return false;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide && SRPConfigSystems.useEvolution) {
            SRPSaveData data;
            String dim = DimKeys.of(level);
            if (!player.getMainHandItem().isEmpty()) {
                player.displayClientMessage(Component.translatable("message.srparasites.lure_empty_hand"), true);
                return InteractionResult.PASS;
            }
            if (this.checkBlocks(level, pos, state.getValue(VARIANT))) {
                if (SRPSaveData.get(level).getCooldown(level, dim) > 0) {
                    player.displayClientMessage(Component.translatable("message.srparasites.lure_cooldown"), true);
                    return InteractionResult.PASS;
                }
                level.playSound(null, pos, SRPSounds.LURE_USE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                level.setBlock(pos.north(3).east(3), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.north(3).west(3), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.south(3).east(3), Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(pos.south(3).west(3), Blocks.AIR.defaultBlockState(), 3);
                sendBurst(pos.north(3).east(3), pos);
                sendBurst(pos.north(3).west(3), pos);
                sendBurst(pos.south(3).east(3), pos);
                sendBurst(pos.south(3).west(3), pos);
                player.displayClientMessage(Component.translatable("message.srparasites.lurec"), true);
                data = SRPSaveData.get(level);
                level.playSound(null, pos, SRPSounds.CARCASS_USE.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
                switch (state.getValue(VARIANT)) {
                    case EIGHT:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueEightCool, true, level, true, 23);
                        break;
                    case FIVE:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueFiveCool, true, level, true, 24);
                        break;
                    case FOUR:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueFourCool, true, level, true, 25);
                        break;
                    case ONE:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueOneCool, true, level, true, 26);
                        break;
                    case SEVEN:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueSevenCool, true, level, true, 27);
                        break;
                    case SIX:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueSixCool, true, level, true, 28);
                        break;
                    case THREE:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueThreeCool, true, level, true, 29);
                        break;
                    case TWO:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueTwoCool, true, level, true, 30);
                        break;
                    case NINE:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueNineCool, true, level, true, 31);
                        break;
                    case TEN:
                        data.setTotalKills(dim, -SRPConfigSystems.luredValueTenCool, true, level, true, 32);
                        break;
                }
                if (!player.getAbilities().invulnerable && SRPConfigSystems.useScent) {
                    EntityParasiticScent sss = SRPEntities.SCENT.get().create(level);
                    sss.setScentLevel(this.getLevelByPhase(state));
                    sss.copyPosition(player);
                    sss.setTargetToKill(player, false);
                    sss.setDieToE(true);
                    sss.setCanFollow(true);
                    level.addFreshEntity(sss);
                }
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                bolt.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                bolt.setVisualOnly(true);
                level.addFreshEntity(bolt);
            } else {
                player.displayClientMessage(Component.translatable("message.srparasites.lure_structure_fail"), true);
                data = SRPSaveData.get(level);
                if (data.getEvolutionPhase(dim) <= -1) {
                    return InteractionResult.PASS;
                }
                switch (state.getValue(VARIANT)) {
                    case EIGHT:
                        data.addCooldown(SRPConfigSystems.luredValueEight, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case FIVE:
                        data.addCooldown(SRPConfigSystems.luredValueFive, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case FOUR:
                        data.addCooldown(SRPConfigSystems.luredValueFour, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case ONE:
                        data.addCooldown(SRPConfigSystems.luredValueOne, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case SEVEN:
                        data.addCooldown(SRPConfigSystems.luredValueSeven, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case SIX:
                        data.addCooldown(SRPConfigSystems.luredValueSix, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case THREE:
                        data.addCooldown(SRPConfigSystems.luredValueThree, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case TWO:
                        data.addCooldown(SRPConfigSystems.luredValueTwo, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case NINE:
                        data.addCooldown(SRPConfigSystems.luredValueNine, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                    case TEN:
                        data.addCooldown(SRPConfigSystems.luredValueTen, level, dim, true);
                        player.displayClientMessage(Component.translatable("message.srparasites.lureb"), true);
                        break;
                }
            }
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            for (int i = 0; i <= 3; ++i) {
                sendBurst(pos, pos);
            }
        }
        return InteractionResult.PASS;
    }

    /** 1.12 {@code SRPPacketParticle(x + 0.5, pos.getY(), z + 0.5, 0.5, 0.5, 2)} sent to all players; the y is the lure's. */
    private static void sendBurst(BlockPos at, BlockPos lure) {
        PacketDistributor.sendToAllPlayers(new ParticlePayload(at.getX() + 0.5, lure.getY(), at.getZ() + 0.5, 0.5f, 0.5f, 2));
    }

    @Override
    public EnumType[] getVariants() {
        return EnumType.values();
    }

    @Override
    public EnumProperty<EnumType> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return this.variantStack(state);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        BlockItemStateProperties props = stack.get(DataComponents.BLOCK_STATE);
        EnumType type = props == null ? null : props.get(VARIANT);
        if (type == null) {
            type = EnumType.ONE;
        }
        int i = 0;
        switch (type) {
            case ONE:
                i = SRPConfigSystems.luredValueOne;
                break;
            case TWO:
                i = SRPConfigSystems.luredValueTwo;
                break;
            case THREE:
                i = SRPConfigSystems.luredValueThree;
                break;
            case FOUR:
                i = SRPConfigSystems.luredValueFour;
                break;
            case FIVE:
                i = SRPConfigSystems.luredValueFive;
                break;
            case SIX:
                i = SRPConfigSystems.luredValueSix;
                break;
            case SEVEN:
                i = SRPConfigSystems.luredValueSeven;
                break;
            case EIGHT:
                i = SRPConfigSystems.luredValueEight;
                break;
            case NINE:
                i = SRPConfigSystems.luredValueNine;
                break;
            case TEN:
                i = SRPConfigSystems.luredValueTen;
                break;
        }
        tooltip.add(Component.translatable("tooltip.tile.srparasites.evolutionlure", i));
    }

    private int getLevelByPhase(BlockState state) {
        switch (state.getValue(VARIANT)) {
            case EIGHT:
                return SRPConfigSystems.eightLevelDeploy;
            case FIVE:
                return SRPConfigSystems.fiveLevelDeploy;
            case FOUR:
                return SRPConfigSystems.fourLevelDeploy;
            case ONE:
                return SRPConfigSystems.oneLevelDeploy;
            case SEVEN:
                return SRPConfigSystems.sevenLevelDeploy;
            case SIX:
                return SRPConfigSystems.sixLevelDeploy;
            case THREE:
                return SRPConfigSystems.threeLevelDeploy;
            case TWO:
                return SRPConfigSystems.twoLevelDeploy;
            case NINE:
                return SRPConfigSystems.nineLevelDeploy;
            case TEN:
                return SRPConfigSystems.tenLevelDeploy;
        }
        return 0;
    }

    private boolean checkBlocks(Level level, BlockPos pos, EnumType t) {
        Block lure = SRPBlocks.evolutionLure.get();
        return level.getBlockState(pos.north(3).east(3)).getBlock() == lure
                && level.getBlockState(pos.north(3).west(3)).getBlock() == lure
                && level.getBlockState(pos.south(3).east(3)).getBlock() == lure
                && level.getBlockState(pos.south(3).west(3)).getBlock() == lure;
    }

    public enum EnumType implements StringRepresentable {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        TEN;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase();
        }

        @Override
        public String toString() {
            return this.getSerializedName();
        }
    }
}
