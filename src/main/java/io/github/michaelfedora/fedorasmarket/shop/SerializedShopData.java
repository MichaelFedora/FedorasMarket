package io.github.michaelfedora.fedorasmarket.shop;

import com.flowpowered.math.vector.Vector3d;
import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializedData;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;

import javax.annotation.Nullable;
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
    @Nullable public UUID playerId;

    public SerializedShopData(SerializedTradeForm tradeFormData, ShopModifier modifier, Vector3d position, UUID worldId, UUID playerId) {
        this.tradeFormData = tradeFormData;
        this.modifier = modifier;
        this.position = position;
        this.worldId = worldId;
        this.playerId = playerId;
    }

    public SerializedShopData(SerializedTradeForm tradeFormData, ShopModifier modifier, Vector3d position, UUID worldId, Optional<UUID> playerId) {
        this.tradeFormData = tradeFormData;
        this.modifier = modifier;
        this.position = position;
        this.worldId = worldId;
        this.playerId = playerId.orElse(null);
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

