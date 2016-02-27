package io.github.michaelfedora.fedorasmarket.data.shopreference;

import static io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataQueries.AUTHOR;
import static io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataQueries.NAME;
import static io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataQueries.INSTANCE;

import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.util.persistence.DataBuilder;
import org.spongepowered.api.util.persistence.InvalidDataException;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopReferenceBuilder implements DataBuilder<ShopReference> {

    @Override
    public Optional<ShopReference> build(DataView container) throws InvalidDataException {
        if(container.contains(AUTHOR, NAME, INSTANCE)) {
            ShopReference data = new ShopReference(
                    (UUID) container.get(AUTHOR).get(),
                    container.getString(NAME).get(),
                    (UUID) container.get(INSTANCE).get());
            return Optional.of(data);
        } else
            return Optional.empty();
    }
}
