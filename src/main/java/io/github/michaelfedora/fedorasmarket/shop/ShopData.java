package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializable;
import io.github.michaelfedora.fedorasmarket.shop.modifier.ShopModifier;
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

    protected TradeForm tradeForm;
    protected ShopModifier modifier;
    protected Location<World> location;
    protected String ownerId;

    public ShopData(TradeForm tradeForm, ShopModifier modifier, Location<World> location, String ownerId) {

        if(!modifier.isValidWith(tradeForm.getTradeType())) {
            modifier = ShopModifier.NONE;
        }

        this.tradeForm = tradeForm;
        this.modifier = modifier;
        this.location = location;
        this.ownerId = ownerId;
    }

    public static ShopData asPlayer(TradeForm tradeForm, ShopModifier shopModifier, Location<World> location, UUID playerId) {
        return new ShopData(tradeForm, shopModifier, location, playerId.toString());
    }

    public static ShopData asServer(TradeForm tradeForm, ShopModifier shopModifier, Location<World> location) {
        return new ShopData(tradeForm, shopModifier, location, "server");
    }

    public TradeForm getTradeForm() {
        return this.tradeForm;
    }

    public ShopModifier getShopModifier() {
        return this.modifier;
    }

    public Location<World> getLocation() {
        return this.location;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public ShopData setTradeForm(TradeForm tradeForm) {
        this.tradeForm = tradeForm;
        return this;
    }

    public ShopData setModifier(ShopModifier modifier) {
        this.modifier = modifier;
        return this;
    }

    public SerializedShopData serialize() {
        return new SerializedShopData(tradeForm.serialize(), modifier, location.getPosition(), location.getExtent().getUniqueId(), ownerId);
    }

    public static ShopData fromSerializedData(SerializedShopData data) throws BadDataException {
        World world = Sponge.getServer().getWorld(data.worldId).orElseThrow(() -> new BadDataException("Could not fetch world [" + data.worldId + "]! Could not create ShopData instance!"));
        return new ShopData(data.tradeFormData.deserialize(), data.modifier, new Location<>(world, data.position), data.ownerId);
    }

    public String toString() {
        return "tradeForm: {" + this.tradeForm + "}, modifier: {" + this.modifier + "}, location: {" + this.location + "}, ownerId: \"" + ownerId + "\"";
    }
}
