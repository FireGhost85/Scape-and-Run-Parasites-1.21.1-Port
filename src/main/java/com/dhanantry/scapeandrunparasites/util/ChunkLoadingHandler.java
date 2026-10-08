package com.dhanantry.scapeandrunparasites.util;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ChunkLoadingHandler
implements ForgeChunkManager.LoadingCallback {
    public void ticketsLoaded(List<ForgeChunkManager.Ticket> tickets, Level world) {
        for (ForgeChunkManager.Ticket ticket : tickets) {
            CompoundTag tag = ticket.getModData();
            if (!tag.contains("x") || !tag.contains("z") || !tag.contains("e")) continue;
            int x = tag.getInt("x");
            int z = tag.getInt("z");
            ticket.bindEntity(world.getEntity(tag.getInt("e")));
            BlockPos pos = BlockPos.containing(x, 0, z);
            ChunkPos chunkPos = new ChunkPos(pos);
            ForgeChunkManager.forceChunk((ForgeChunkManager.Ticket)ticket, (ChunkPos)chunkPos);
        }
    }
}

