package io.github.michaelfedora.fedorasmarket.persistance.shopreference;

import static io.github.michaelfedora.fedorasmarket.persistance.FmDataKeys.SHOP_REFERENCE;

import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableSingleData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import java.util.Optional;

/**
 * Created by Michael on 2/27/2016.
 */
public class ImmutableShopReferenceData extends AbstractImmutableSingleData<ShopReference, ImmutableShopReferenceData, ShopReferenceData> {

    protected ImmutableShopReferenceData(ShopReference value) {
        super(value, SHOP_REFERENCE);
    }

    @Override
    public <E> Optional<ImmutableShopReferenceData> with(Key<? extends BaseValue<E>> key, E value) {
        if(this.supports(key)) {
            return Optional.of(asMutable().set(key, value).asImmutable());
        } else
            return Optional.empty();
    }

    @Override
    public int compareTo(ImmutableShopReferenceData o) {
        return 0;
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    protected ImmutableValue<?> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(SHOP_REFERENCE, this.getValue()).asImmutable();
    }

    @Override
    public ShopReferenceData asMutable() {
        return new ShopReferenceData(this.getValue());
    }
}
