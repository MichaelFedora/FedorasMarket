package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.persistance.shopreference.ShopReferenceDataQueries;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.MemoryDataContainer;

import java.util.UUID;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopReference implements DataSerializable {

    public String owner;
    public UUID instance;

    public ShopReference() {
        owner = null;
        instance = null;
    }

    public ShopReference(String owner, UUID instance) {
        this.owner = owner;
        this.instance = instance;
    }

    public ShopReference(String owner, String instance) {
        this.owner = owner;
        this.instance = UUID.fromString(instance);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return new MemoryDataContainer()
                .set(ShopReferenceDataQueries.AUTHOR, this.owner)
                .set(ShopReferenceDataQueries.INSTANCE, this.instance.toString());
    }
}
