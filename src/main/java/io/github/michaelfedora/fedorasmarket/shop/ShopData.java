package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializable;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopData implements FmSerializable<SerializedShopData> {

    public TradeForm tradeForm;
    public ShopModifier modifier;
    public Location<World> location;
    public Optional<UUID> playerId;

    public ShopData(TradeForm tradeForm, ShopModifier modifier, Location<World> location, Optional<UUID> playerId) {

        if(!modifier.isValidWith(tradeForm.getTradeType())) {
            modifier = ShopModifier.NONE;
        }

        this.tradeForm = tradeForm;
        this.modifier = modifier;
        this.location = location;
        this.playerId = playerId;
    }

    public static ShopData asPlayer(TradeForm tradeForm, ShopModifier shopModifier, Location<World> location, UUID playerId) {
        return new ShopData(tradeForm, shopModifier, location, Optional.of(playerId));
    }

    public static ShopData asServer(TradeForm tradeForm, ShopModifier shopModifier, Location<World> location) {
        return new ShopData(tradeForm, shopModifier, location, Optional.empty());
    }

    public SerializedShopData serialize() {
        return new SerializedShopData(tradeForm.serialize(), modifier, location.getPosition(), location.getExtent().getUniqueId(), playerId);
    }

    public static ShopData fromSerializedData(SerializedShopData data) throws BadDataException {
        World world = Sponge.getServer().getWorld(data.worldId).orElseThrow(() -> new BadDataException("Could not fetch world [" + data.worldId + "]! Could not create ShopData instance!"));
        return new ShopData(data.tradeFormData.deserialize(), data.modifier, new Location<>(world, data.position), Optional.ofNullable(data.playerId));
    }

    public String toString() {
        return "tradeForm: {" + this.tradeForm + "}, modifier: {" + this.modifier + "}, location: {" + this.location + "}, ownerId: " + playerId.orElse(null);
    }
}
