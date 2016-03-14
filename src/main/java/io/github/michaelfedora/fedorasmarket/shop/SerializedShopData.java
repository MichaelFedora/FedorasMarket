package io.github.michaelfedora.fedorasmarket.shop;

import com.flowpowered.math.vector.Vector3d;
import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 3/3/2016.
 */
public class SerializedShopData implements FmSerializedData<ShopData> {

    public SerializedTradeForm tradeFormData;
    public ShopModifier modifier;
    public Vector3d position;
    public UUID worldId;
    public boolean isServerShop;

    public SerializedShopData(SerializedTradeForm tradeFormData, ShopModifier modifier, Vector3d position, UUID worldId, boolean isServerShop) {
        this.tradeFormData = tradeFormData;
        this.modifier = modifier;
        this.position = position;
        this.worldId = worldId;
        this.isServerShop = isServerShop;
    }

    public SerializedShopData(SerializedTradeForm tradeFormData, ShopModifier modifier, Vector3d position, UUID worldId) {
        this.tradeFormData = tradeFormData;
        this.modifier = modifier;
        this.position = position;
        this.worldId = worldId;
        this.isServerShop = false;
    }

    @Override
    public Optional<ShopData> safeDeserialize() {

        Optional<ShopData> ret = Optional.empty();

        try {
            ret = Optional.of(this.deserialize());
        } catch (BadDataException e) {
            // do nothing :3
        }

        return ret;
    }

    @Override
    public ShopData deserialize() throws BadDataException {
        return ShopData.fromSerializedData(this);
    }

    public String toString() {
        return "tradeFormData: {" + this.tradeFormData + "}, modifier: {" + this.modifier + "}, worldId: {" + this.worldId + "}, position: {" + this.position + "}";
    }
}
