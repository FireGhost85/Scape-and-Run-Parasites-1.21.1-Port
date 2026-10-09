package com.dhanantry.scapeandrunparasites.network;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.bestiary.cap.BestiaryCapability;
import com.dhanantry.scapeandrunparasites.bestiary.cap.IBestiaryProgress;
import com.dhanantry.scapeandrunparasites.init.SRPItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** MsgReconfigureProgressUnlock: the player inserts an evolution / development clock into the "Current Progress" page. */
public record ReconfigureProgressUnlockPayload(boolean unlockPhase, boolean unlockUD) implements CustomPacketPayload {
    public static final Type<ReconfigureProgressUnlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, "reconfigure_progress_unlock"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ReconfigureProgressUnlockPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ReconfigureProgressUnlockPayload::unlockPhase,
            ByteBufCodecs.BOOL, ReconfigureProgressUnlockPayload::unlockUD,
            ReconfigureProgressUnlockPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ReconfigureProgressUnlockPayload msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (!(ctx.player() instanceof ServerPlayer player)) {
                return;
            }
            IBestiaryProgress prog = BestiaryCapability.get(player);
            if (prog == null) {
                return;
            }
            if (msg.unlockPhase() && !prog.hasUnlockedProgressPhase() && consumeOne(player, SRPItems.itemEVClock.get())) {
                prog.setUnlockedProgressPhase(true);
            }
            if (msg.unlockUD() && !prog.hasUnlockedProgressUD() && consumeOne(player, SRPItems.itemLevelClock.get())) {
                prog.setUnlockedProgressUD(true);
            }
            SRPSend.sendToPlayer(player, new BestiarySyncPayload(prog.serializeNBT()));
            SRPSend.sendToPlayer(player, new SyncProgressSnapshotPayload(SRPProgressSnapshot.collect(player.level(), player).toNBT()));
        });
    }

    private static boolean consumeOne(ServerPlayer player, Item wanted) {
        for (int i = 0; i < player.getInventory().items.size(); ++i) {
            ItemStack stack = player.getInventory().items.get(i);
            if (stack.isEmpty() || stack.getItem() != wanted) continue;
            stack.shrink(1);
            if (stack.getCount() <= 0) {
                player.getInventory().items.set(i, ItemStack.EMPTY);
            }
            player.getInventory().setChanged();
            player.containerMenu.broadcastChanges();
            return true;
        }
        return false;
    }
}
