package com.dhanantry.scapeandrunparasites.datagen;

import com.dhanantry.scapeandrunparasites.ScapeAndRunParasites;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.nio.file.Files;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * Development tool, only runs in the data run ({@code gradlew runData}): dumps the SRP blocks, items and entities with their
 * block state properties to {@code srp_registry_dump.json}, the input of {@code porting/tools/gen_assets.py} (blockstates, lang).
 */
@EventBusSubscriber(modid = ScapeAndRunParasites.MODID)
public final class SRPRegistryDump {
    private SRPRegistryDump() {}

    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"})
    static void gather(GatherDataEvent event) {
        JsonObject root = new JsonObject();
        JsonArray blocks = new JsonArray();
        for (Block block : BuiltInRegistries.BLOCK) {
            var key = BuiltInRegistries.BLOCK.getKey(block);
            if (!ScapeAndRunParasites.MODID.equals(key.getNamespace())) continue;
            JsonObject b = new JsonObject();
            b.addProperty("id", key.getPath());
            JsonArray chain = new JsonArray();
            for (Class<?> c = block.getClass(); c != null && c != Object.class; c = c.getSuperclass()) chain.add(c.getSimpleName());
            b.add("classes", chain);
            JsonObject props = new JsonObject();
            for (Property p : block.defaultBlockState().getProperties()) {
                JsonArray values = new JsonArray();
                for (Object v : p.getPossibleValues()) values.add(p.getName((Comparable) v));
                props.add(p.getName(), values);
            }
            b.add("properties", props);
            BlockState def = block.defaultBlockState();
            JsonObject defs = new JsonObject();
            for (Property p : def.getProperties()) defs.addProperty(p.getName(), p.getName((Comparable) def.getValue(p)));
            b.add("default", defs);
            blocks.add(b);
        }
        root.add("blocks", blocks);
        JsonArray items = new JsonArray();
        for (var item : BuiltInRegistries.ITEM) {
            var key = BuiltInRegistries.ITEM.getKey(item);
            if (!ScapeAndRunParasites.MODID.equals(key.getNamespace())) continue;
            JsonObject i = new JsonObject();
            i.addProperty("id", key.getPath());
            i.addProperty("class", item.getClass().getSimpleName());
            i.addProperty("blockItem", item instanceof BlockItem);
            i.addProperty("descriptionId", item.getDescriptionId());
            items.add(i);
        }
        root.add("items", items);
        JsonArray entities = new JsonArray();
        for (var type : BuiltInRegistries.ENTITY_TYPE) {
            var key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
            if (!ScapeAndRunParasites.MODID.equals(key.getNamespace())) continue;
            JsonObject e = new JsonObject();
            e.addProperty("id", key.getPath());
            e.addProperty("category", type.getCategory().getName());
            e.addProperty("class", type.getDescriptionId());
            entities.add(e);
        }
        root.add("entities", entities);
        try {
            File out = new File(System.getProperty("srp.dump", "srp_registry_dump.json"));
            Files.writeString(out.toPath(), new GsonBuilder().setPrettyPrinting().create().toJson(root));
            ScapeAndRunParasites.LOGGER.info("Registry dump written to {}", out.getAbsolutePath());
        } catch (Exception ex) {
            ScapeAndRunParasites.LOGGER.error("Registry dump failed", ex);
        }
    }
}
