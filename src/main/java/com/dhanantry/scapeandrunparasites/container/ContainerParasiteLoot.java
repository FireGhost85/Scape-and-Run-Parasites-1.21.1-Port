package com.dhanantry.scapeandrunparasites.container;

import com.dhanantry.scapeandrunparasites.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.init.SRPMenus;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.tileentity.TileEntityParasiteLoot;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Menu of the parasite loot block: 27 slots that only take mod items. Taking an item hurts the player with magic damage of
 * {@code 0.5 + 7.5 * (1 - fullness)} (when {@code parasiteLootDamageOnTake}) and applies corrosion and viral infection that
 * grow as the chest empties. {@code fullness} and the interaction pulse are synced as data slots.
 */
public class ContainerParasiteLoot extends AbstractContainerMenu {
    @Nullable
    private final TileEntityParasiteLoot te;
    private final Container container;
    private final DataSlot fullnessSlot = DataSlot.standalone();
    private final DataSlot pulseSlot = DataSlot.standalone();
    private int interactionPulse = 0;

    public ContainerParasiteLoot(int id, Inventory playerInv) {
        this(id, playerInv, new SimpleContainer(27), null);
    }

    public ContainerParasiteLoot(int id, Inventory playerInv, TileEntityParasiteLoot te) {
        this(id, playerInv, te, te);
    }

    private ContainerParasiteLoot(int id, Inventory playerInv, Container container, @Nullable TileEntityParasiteLoot te) {
        super(SRPMenus.PARASITE_LOOT.get(), id);
        checkContainerSize(container, 27);
        this.te = te;
        this.container = container;
        int idx = 0;
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 9; ++c) {
                this.addSlot(new Slot(container, idx++, 8 + c * 18, 30 + r * 18) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return TileEntityParasiteLoot.isValidParasiteLootItem(stack);
                    }

                    @Override
                    public void onTake(Player p, ItemStack stack) {
                        if (!p.level().isClientSide && ContainerParasiteLoot.this.te != null) {
                            float f = ContainerParasiteLoot.this.te.getFullness();
                            float dmg = 0.5f + 7.5f * (1.0f - f);
                            ContainerParasiteLoot.this.interactionPulse = ContainerParasiteLoot.this.interactionPulse + 1 & Short.MAX_VALUE;
                            if (SRPConfigWorld.parasiteLootDamageOnTake && dmg > 0.0f) {
                                p.hurt(p.level().damageSources().magic(), dmg);
                            }
                            ContainerParasiteLoot.this.applyParasiteEffects(p, f);
                        }
                        super.onTake(p, stack);
                    }
                });
            }
        }
        for (int r = 0; r < 3; ++r) {
            for (int c = 0; c < 9; ++c) {
                this.addSlot(new Slot(playerInv, c + r * 9 + 9, 8 + c * 18, 88 + r * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 146));
        }
        this.addDataSlot(this.fullnessSlot);
        this.addDataSlot(this.pulseSlot);
    }

    /** Fullness times 1000, as the GUI draws it. */
    public int getFullnessField() {
        return this.fullnessSlot.get();
    }

    /** Counter that changes on every take/insert, drives the screen animation. */
    public int getClientPulse() {
        return this.pulseSlot.get();
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }

    @Override
    public void broadcastChanges() {
        if (this.te != null) {
            this.fullnessSlot.set(this.te.getFullnessField());
            this.pulseSlot.set(this.interactionPulse);
        }
        super.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack ret = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack in = slot.getItem();
            ret = in.copy();
            if (index < 27) {
                float fBefore = this.te == null ? 1.0f : this.te.getFullness();
                if (!this.moveItemStackTo(in, 27, 63, true)) {
                    return ItemStack.EMPTY;
                }
                if (!player.level().isClientSide) {
                    if (SRPConfigWorld.parasiteLootDamageOnTake) {
                        float dmg = 0.5f + 7.5f * (1.0f - fBefore);
                        if (dmg > 0.0f) {
                            player.hurt(player.level().damageSources().magic(), dmg);
                        }
                    }
                    if (fBefore < 1.0f) {
                        int viralDur;
                        int viralAmp;
                        int corrosDur;
                        int corrosAmp;
                        if (fBefore < 0.25f) {
                            corrosAmp = 1;
                            corrosDur = 400;
                            viralAmp = 3;
                            viralDur = 600;
                        } else if (fBefore < 0.5f) {
                            corrosAmp = 1;
                            corrosDur = 200;
                            viralAmp = 2;
                            viralDur = 400;
                        } else if (fBefore < 0.75f) {
                            corrosAmp = 0;
                            corrosDur = 200;
                            viralAmp = 1;
                            viralDur = 200;
                        } else {
                            corrosAmp = 0;
                            corrosDur = 100;
                            viralAmp = 1;
                            viralDur = 100;
                        }
                        player.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, corrosDur, corrosAmp, false, false));
                        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, player, viralDur, viralAmp);
                    }
                    this.interactionPulse = this.interactionPulse + 1 & Short.MAX_VALUE;
                }
            } else if (index < 54) {
                if (!this.moveItemStackTo(in, 0, 27, false)) {
                    if (!this.moveItemStackTo(in, 54, 63, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!player.level().isClientSide) {
                    this.interactionPulse = this.interactionPulse + 1 & Short.MAX_VALUE;
                }
            } else if (index < 63) {
                if (!this.moveItemStackTo(in, 0, 27, false)) {
                    if (!this.moveItemStackTo(in, 27, 54, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!player.level().isClientSide) {
                    this.interactionPulse = this.interactionPulse + 1 & Short.MAX_VALUE;
                }
            }
            if (in.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (in.getCount() == ret.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, in);
        }
        return ret;
    }

    private void applyParasiteEffects(Player p, float fullness) {
        int viralDur;
        int viralAmp;
        int corrosDur;
        int corrosAmp;
        if (p.level().isClientSide) {
            return;
        }
        if (fullness < 0.25f) {
            corrosAmp = 1;
            corrosDur = 400;
            viralAmp = 3;
            viralDur = 600;
        } else if (fullness < 0.5f) {
            corrosAmp = 1;
            corrosDur = 200;
            viralAmp = 2;
            viralDur = 400;
        } else if (fullness < 0.75f) {
            corrosAmp = 0;
            corrosDur = 200;
            viralAmp = 1;
            viralDur = 200;
        } else if (fullness < 1.0f) {
            corrosAmp = 0;
            corrosDur = 100;
            viralAmp = 1;
            viralDur = 100;
        } else {
            return;
        }
        p.addEffect(new MobEffectInstance(SRPPotions.CORRO_E, corrosDur, corrosAmp, false, false));
        SRPPotions.applyStackPotion(SRPPotions.VIRA_E, p, viralDur, viralAmp);
    }
}
