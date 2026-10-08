package com.dhanantry.scapeandrunparasites.entity.tile;

import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanister;
import com.dhanantry.scapeandrunparasites.block.BlockParasiteCanisterC;
import com.dhanantry.scapeandrunparasites.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.init.SRPBlockEntities;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import com.dhanantry.scapeandrunparasites.init.SRPSounds;
import com.dhanantry.scapeandrunparasites.network.ParticlePayload;
import com.dhanantry.scapeandrunparasites.phase.DimKeys;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.world.SRPSaveData;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Block entity of the active parasite canister (1.12 {@code TileEntityCanister}): 40 slots, stack limit 100, eats one item
 * every second after {@code cystDelay} seconds and turns into a cyst canister (or vanishes) when empty. The chest screen only
 * shows 36 slots, as in 1.12 ({@code ContainerChest} used {@code size / 9} rows).
 */
public class TileEntityCanister extends RandomizableContainerBlockEntity {
    private static final int SIZE = 40;
    private NonNullList<ItemStack> chestContents = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    public int numPlayersUsing;
    private int ticksSinceSync;

    public TileEntityCanister(BlockPos pos, BlockState state) {
        super(SRPBlockEntities.CANISTER.get(), pos, state);
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public int getMaxStackSize() {
        return 100;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.chest");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.chestContents;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.chestContents = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ChestMenu(MenuType.GENERIC_9x4, id, inventory, this, 4);
    }

    @Override
    protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.loadAdditional(nbt, registries);
        this.chestContents = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(nbt)) {
            ContainerHelper.loadAllItems(nbt, this.chestContents, registries);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
        super.saveAdditional(nbt, registries);
        if (!this.trySaveLootTable(nbt)) {
            ContainerHelper.saveAllItems(nbt, this.chestContents, registries);
        }
    }

    /** 1.12 {@code ITickable.update}; called from the block's ticker. */
    public void update() {
        if (this.level == null) {
            return;
        }
        int i = this.worldPosition.getX();
        int j = this.worldPosition.getY();
        int k = this.worldPosition.getZ();
        ++this.ticksSinceSync;
        if (!this.level.isClientSide && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
            this.numPlayersUsing = 0;
            AABB box = new AABB(i - 5.0f, j - 5.0f, k - 5.0f, i + 1 + 5.0f, j + 1 + 5.0f, k + 1 + 5.0f);
            for (Player player : this.level.getEntitiesOfClass(Player.class, box)) {
                if (player.containerMenu instanceof ChestMenu chest && chest.getContainer() == this) {
                    ++this.numPlayersUsing;
                }
            }
        }
        if (!this.level.isClientSide && this.ticksSinceSync % 20 == 0 && this.ticksSinceSync > 20 * SRPConfig.cystDelay) {
            int ccc = 0;
            for (int in = 0; in < this.getContainerSize(); ++in) {
                ItemStack stack = this.chestContents.get(in);
                if (!stack.isEmpty()) {
                    if (ParasiteEventEntity.checkName(BuiltInRegistries.ITEM.getKey(stack.getItem()).toString(), SRPConfig.cystItemBlackList, false)) {
                        continue;
                    }
                    if (stack.isDamageableItem()) {
                        boolean broke = false;
                        int amount = this.level instanceof ServerLevel server ? EnchantmentHelper.processDurabilityChange(server, stack, 5) : 5;
                        if (amount > 0) {
                            stack.setDamageValue(stack.getDamageValue() + amount);
                            broke = stack.getDamageValue() > stack.getMaxDamage();
                        }
                        if (broke) {
                            stack.shrink(1);
                            if (SRPConfigSystems.useEvolution) {
                                SRPSaveData data = SRPSaveData.get(this.level);
                                data.setTotalKills(DimKeys.of(this.level), SRPConfig.cystConsumePoint, true, this.level, true, 43);
                            }
                        }
                    } else {
                        stack.shrink(1);
                        if (SRPConfigSystems.useEvolution) {
                            SRPSaveData data = SRPSaveData.get(this.level);
                            data.setTotalKills(DimKeys.of(this.level), SRPConfig.cystConsumePoint, true, this.level, true, 44);
                        }
                    }
                    if (this.level.random.nextBoolean()) {
                        this.level.playSound(null, this.worldPosition, SRPSounds.CYST_EATING.get(), SoundSource.HOSTILE, 0.25f, 1.0f);
                    }
                    PacketDistributor.sendToAllPlayers(new ParticlePayload(i + 0.5, j + 0.7, k + 0.5, 0.5f, 0.5f, 2));
                    this.setChanged();
                    return;
                }
                ++ccc;
            }
            if (ccc == this.getContainerSize()) {
                if (!this.level.getBlockState(this.worldPosition.below()).isAir()) {
                    this.level.setBlockAndUpdate(this.worldPosition, SRPBlocks.ParasiteCanister.get().defaultBlockState()
                            .setValue(BlockParasiteCanister.VARIANT, BlockParasiteCanister.EnumType.CYST));
                } else {
                    this.level.setBlockAndUpdate(this.worldPosition, Blocks.AIR.defaultBlockState());
                }
            }
        }
    }

    /** Inserts the stacks into the first 27 slots (merging with equal items, then empty slots), like the item handler of 1.12. */
    public boolean addStack(List<ItemStack> moblist2) {
        InvWrapper handler = new InvWrapper(this);
        for (ItemStack mob : moblist2) {
            if (mob.isEmpty()) {
                continue;
            }
            for (int in = 0; in <= 26; ++in) {
                ItemStack slot = this.chestContents.get(in);
                if (ItemStack.isSameItem(slot, mob)) {
                    mob = handler.insertItem(in, mob, false);
                    continue;
                }
                if (!slot.isEmpty()) {
                    continue;
                }
                mob = handler.insertItem(in, mob, false);
            }
        }
        return false;
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            this.numPlayersUsing = type;
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public void startOpen(Player player) {
        if (player.isCreative() && this.level != null) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }
            ++this.numPlayersUsing;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
    }

    @Override
    public void stopOpen(Player player) {
        if (player.isCreative() && this.getBlockState().getBlock() instanceof BlockParasiteCanisterC && this.level != null) {
            --this.numPlayersUsing;
            this.level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), 1, this.numPlayersUsing);
            this.level.updateNeighborsAt(this.worldPosition, this.getBlockState().getBlock());
        }
    }
}
