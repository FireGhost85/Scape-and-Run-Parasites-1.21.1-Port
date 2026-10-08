package com.dhanantry.scapeandrunparasites.item;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.dhanantry.scapeandrunparasites.network.MovingSoundPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Music disc. In 1.21 a disc is described by a data driven jukebox song (data/srparasites/jukebox_song/&lt;name&gt;.json).
 * Putting one into a jukebox also sends the moving-sound cue 104 (stops the ambient music) to the dimension, as in 1.10.9.
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public class ItemDiscRecord extends Item {
    protected byte version;

    public ItemDiscRecord(String name, int maxStack, int id, Object soundIn) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
                .jukeboxPlayable(ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(ScapeAndRunParasites.MODID, name))));
        this.version = (byte) id;
    }

    @SubscribeEvent
    public static void onUseJukebox(PlayerInteractEvent.RightClickBlock e) {
        if (!(e.getItemStack().getItem() instanceof ItemDiscRecord) || e.getLevel().isClientSide) {
            return;
        }
        var state = e.getLevel().getBlockState(e.getPos());
        if (state.is(Blocks.JUKEBOX) && !state.getValue(JukeboxBlock.HAS_RECORD) && e.getLevel() instanceof ServerLevel server) {
            PacketDistributor.sendToPlayersInDimension(server, new MovingSoundPayload(104));
        }
    }
}
