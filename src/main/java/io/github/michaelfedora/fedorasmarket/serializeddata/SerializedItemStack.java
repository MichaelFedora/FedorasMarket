package io.github.michaelfedora.fedorasmarket.serializeddata;

import com.google.common.reflect.TypeToken;
import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.item.inventory.ItemStack;

import java.io.*;
import java.util.Optional;

/**
 * Created by Michael on 3/16/2016.
 */
public class SerializedItemStack implements FmSerializedData<ItemStack> {

    private String serializedItemStack;

    public SerializedItemStack(ItemStack itemStack) {
        StringWriter sink = new StringWriter();
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
        ConfigurationNode node = loader.createEmptyNode();

        try {
            node.setValue(TypeToken.of(ItemStack.class), itemStack);
            loader.save(node);
        } catch(IOException | ObjectMappingException e) {
            throw new IllegalStateException("Something went wrong trying to serialize the item", e);
        }

        this.serializedItemStack = sink.toString();
    }

    @Override
    public ItemStack deserialize() throws BadDataException {

        StringReader source = new StringReader(serializedItemStack);
        GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();

        try {
            ConfigurationNode node = loader.load();
            return node.getValue(TypeToken.of(ItemStack.class));
        } catch (IOException | ObjectMappingException e) {
            throw new BadDataException("Error deserializing itemStack", e);
        }
    }

    @Override
    public Optional<ItemStack> safeDeserialize() {
        try {

            return Optional.of(deserialize());

        } catch(BadDataException e) {
            FedorasMarket.getLogger().error("ItemStack SafeDeserialize failed: ", e);
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        return serializedItemStack;
    }
}
