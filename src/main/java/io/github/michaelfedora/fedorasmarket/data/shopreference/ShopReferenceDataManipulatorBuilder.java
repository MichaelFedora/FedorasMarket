package io.github.michaelfedora.fedorasmarket.data.shopreference;

import static io.github.michaelfedora.fedorasmarket.data.FmDataKeys.SHOP_REFERENCE;

import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;

import java.util.Optional;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopReferenceDataManipulatorBuilder implements DataManipulatorBuilder<ShopReferenceData, ImmutableShopReferenceData> {

    @Override
    public Optional<ShopReferenceData> build(DataView container) throws InvalidDataException {
        if(!container.contains(SHOP_REFERENCE.getQuery()))
            return Optional.empty();

        ShopReference data = container.getSerializable(SHOP_REFERENCE.getQuery(), ShopReference.class).get();
        return Optional.of(new ShopReferenceData(data));
    }

    @Override
    public ShopReferenceData create() {
        return new ShopReferenceData(new ShopReference());
    }

    public ShopReferenceData createFrom(ShopReference data) {
        return new ShopReferenceData(data);
    }

    @Override
    public Optional<ShopReferenceData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }
}
