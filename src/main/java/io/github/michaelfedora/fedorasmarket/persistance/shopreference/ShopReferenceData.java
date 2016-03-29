package io.github.michaelfedora.fedorasmarket.persistance.shopreference;

import static io.github.michaelfedora.fedorasmarket.persistance.FmDataKeys.SHOP_REFERENCE;

import com.google.common.base.Preconditions;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopReferenceData extends AbstractSingleData<ShopReference, ShopReferenceData, ImmutableShopReferenceData> {

    protected ShopReferenceData(ShopReference value) {
        super(value, SHOP_REFERENCE);
    }

    @Override
    public ShopReferenceData copy() {
        return new ShopReferenceData(this.getValue());
    }

    @Override
    public Optional<ShopReferenceData> fill(DataHolder dataHolder, MergeFunction mergeFunction) {
        ShopReferenceData shopReferenceData = Preconditions.checkNotNull(mergeFunction).merge(copy(), dataHolder.get(ShopReferenceData.class).orElse(copy()));
        return Optional.of(this.set(SHOP_REFERENCE, shopReferenceData.get(SHOP_REFERENCE).get()));
    }

    @Override
    public Optional<ShopReferenceData> from(DataContainer container) {
        if(container.contains(SHOP_REFERENCE.getQuery())) {
            return Optional.of(this.set(SHOP_REFERENCE, container.getSerializable(SHOP_REFERENCE.getQuery(), ShopReference.class).orElse(getValue())));
        }
        return Optional.empty();
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public ImmutableShopReferenceData asImmutable() {
        return new ImmutableShopReferenceData(this.getValue());
    }

    @Override
    public int compareTo(ShopReferenceData data) {
        return 0;
    }

    @Override
    protected Value<ShopReference> getValueGetter() {
        return Sponge.getRegistry().getValueFactory().createValue(SHOP_REFERENCE, this.getValue());
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer().set(SHOP_REFERENCE, this.getValue());
    }
}
