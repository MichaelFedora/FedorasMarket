package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.FmSerializable;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by Michael on 2/27/2016.
 */
public class ShopData implements FmSerializable<SerializedShopData> {

    public static class OwnerData {
        Account account; // the owner's account
        Inventory inventory; // the owner's inventory

        public static final OwnerData SERVER = new OwnerData(null, null);

        public OwnerData(Account account, Inventory inventory) {
            this.account = account;
            this.inventory = inventory;
        }
    }

    public OwnerData ownerData;
    public TradeForm tradeForm;
    public ShopModifier modifier;
    public Location<World> location;
    //boolean serverOwned = false; // just deposit/withdraw/give/take (i.e. owned by the server :3)

    public ShopData(TradeForm tradeForm, ShopModifier modifier, Location<World> location, OwnerData ownerData) {

        if(!modifier.isValidWith(tradeForm.getTradeType())) {
            modifier = ShopModifier.NONE;
        }

        this.tradeForm = tradeForm;
        this.modifier = modifier;
        this.location = location;
        this.ownerData = ownerData;
    }

    public SerializedShopData serialize() {
        return new SerializedShopData(tradeForm.serialize(), modifier, location.getPosition(), location.getExtent().getUniqueId());
    }

    public static ShopData fromSerializedData(SerializedShopData data) throws BadDataException {
        World world = Sponge.getServer().getWorld(data.worldId).orElseThrow(() -> new BadDataException("Could not fetch world [" + data.worldId + "]! Could not create ShopData instance!"));
        return new ShopData(data.tradeFormData.deserialize(), data.modifier, new Location<>(world, data.position), OwnerData.SERVER);
    }

    public String toString() {
        return "tradeForm: {" + this.tradeForm + "}, modifier: {" + this.modifier + "}, location: {" + this.location + "}";
    }
}
