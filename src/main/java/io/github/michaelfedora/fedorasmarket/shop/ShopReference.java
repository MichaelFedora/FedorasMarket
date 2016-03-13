package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.UUID;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopReference implements DataSerializable {

    public UUID author;
    public String name;
    public UUID instance;

    public ShopReference() {
        author = null;
        name = null;
        instance = null;
    }

    public ShopReference(UUID author, String name, UUID instance) {
        this.author = author;
        this.name = name;
        this.instance = instance;
    }

    public ShopReference(String author, String name, String instance) {
        this.author = UUID.fromString(author);
        this.name = name;
        this.instance = UUID.fromString(instance);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(ShopReferenceDataQueries.AUTHOR, this.author.toString())
                .set(ShopReferenceDataQueries.NAME, this.name)
                .set(ShopReferenceDataQueries.INSTANCE, this.instance.toString());
    }
}
