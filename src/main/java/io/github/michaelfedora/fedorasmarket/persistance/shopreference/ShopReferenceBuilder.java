package io.github.michaelfedora.fedorasmarket.persistance.shopreference;

import static io.github.michaelfedora.fedorasmarket.persistance.shopreference.ShopReferenceDataQueries.OWNER;
import static io.github.michaelfedora.fedorasmarket.persistance.shopreference.ShopReferenceDataQueries.INSTANCE;

import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopReferenceBuilder extends AbstractDataBuilder<ShopReference> implements DataBuilder<ShopReference> {

    public ShopReferenceBuilder() {
        super(ShopReference.class, 1);
    }

    @Override
    public Optional<ShopReference> buildContent(DataView container) throws InvalidDataException {
        if(!container.contains(OWNER, INSTANCE))
            return Optional.empty();

        ShopReference data = new ShopReference(
                container.getString(OWNER).get(),
                container.getString(INSTANCE).get());
        return Optional.of(data);
    }
}
