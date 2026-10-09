package com.dhanantry.scapeandrunparasites.bestiary.cap;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/** The bestiary progress of a player (the 1.12 capability {@code BestiaryCapability.CAP}) as a data attachment that survives death. */
public final class BestiaryCapability {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ScapeAndRunParasites.MODID);

    public static final Supplier<AttachmentType<BestiaryProgress>> CAP = ATTACHMENTS.register("bestiary", () -> AttachmentType.builder(BestiaryProgress::new)
            .serialize(new IAttachmentSerializer<CompoundTag, BestiaryProgress>() {
                @Override
                public BestiaryProgress read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
                    BestiaryProgress progress = new BestiaryProgress();
                    progress.deserializeNBT(tag);
                    return progress;
                }

                @Override
                public CompoundTag write(BestiaryProgress attachment, HolderLookup.Provider provider) {
                    return attachment.serializeNBT();
                }
            })
            .copyOnDeath()
            .build());

    private BestiaryCapability() {
    }

    public static IBestiaryProgress get(Player player) {
        return player.getData(CAP.get());
    }
}
