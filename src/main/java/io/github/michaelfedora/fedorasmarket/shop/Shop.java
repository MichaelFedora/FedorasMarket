package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by MichaelFedora on 1/23/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class Shop {

    protected Sign sign;
    protected ShopData data;

    //protected ShopReference reference;

    public Shop(Sign sign, ShopData shopData) {

        this.sign = sign;
        this.data = shopData;
    }

    public static Optional<Shop> fromSign(Sign sign) {
        ShopReference ref;
        {
            Optional<ShopReference> opt_ref = sign.get(FmDataKeys.SHOP_REFERENCE);
            if(!opt_ref.isPresent())
                return Optional.empty();
            ref = opt_ref.get();
        }

        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.shopDataDB.select(conn, ref.author, ref.name, ref.instance);
            if(resultSet.next()) {
                try {
                    Shop shop = new Shop(sign, ((SerializedShopData) resultSet.getObject("data")).deserialize());
                    return Optional.of(shop);
                } catch(BadDataException e) {
                    FedorasMarket.getLogger().error("Bad shop data :o", e);
                }
            }

        } catch (SQLException e) {
            FedorasMarket.getLogger().error("SQL Error", e);
        }

        return Optional.empty();
    }

    public static Optional<Shop> fromLocation(Location<World> location) {

        Sign sign;
        {
            Optional<Sign> opt_sign = FmUtil.getSignFromLocation(location);
            if(opt_sign.isPresent())
                return Optional.empty();

            sign = opt_sign.get();
        }

        return fromSign(sign);
    }

    public void refresh() {
        // update with the db reference
    }

    public ShopReference toReference() {
        return new ShopReference();
    }

    public void doPrimary(Player player) {

        EconomyService eco = FedorasMarket.getEconomyService();

        TradeActiveParty owner;
        TradeActiveParty customer;

        if(this.data.ownerData != ShopData.OwnerData.SERVER)
            owner = new TradeActiveParty(this.data.ownerData.account, this.data.ownerData.inventory);
        else
            owner = TradeActiveParty.SERVER;

        customer = new TradeActiveParty(eco.getOrCreateAccount(player.getUniqueId()).get(), player.getInventory());

        this.data.tradeForm.apply(owner, customer);

        player.sendMessage(Text.of(FmUtil.makePrefix(), "[Shop] Did  primary!"));
    }

    public void doSecondary(Player player) {

        if(this.data.modifier != ShopModifier.NONE) {

            EconomyService eco = FedorasMarket.getEconomyService();

            TradeActiveParty owner;
            TradeActiveParty customer;

            if(this.data.ownerData != ShopData.OwnerData.SERVER)
                owner = new TradeActiveParty(this.data.ownerData.account, this.data.ownerData.inventory);
            else
                owner = TradeActiveParty.SERVER;

            customer = new TradeActiveParty(eco.getOrCreateAccount(player.getUniqueId()).get(), player.getInventory());

            this.data.modifier.execute(this.data, owner, customer);
        }

        player.sendMessage(Text.of(FmUtil.makePrefix(), "[Shop] Did  secondary!"));
    }

    /*public void tryMakeShop() {

        Direction dir;
        {
            Optional<Direction> opt_dir = sign_bs.get(Keys.DIRECTION);
            if (!opt_dir.isPresent())
                return;
            dir = opt_dir.get();
        }

        Location<World> chest_loc = sign.getLocation().getRelative(dir.getOpposite());
        BlockState chest_bs = chest_loc.getBlock();

        FedorasMarket.getLogger().info("Direction is " + dir + ", " +
                "Opposite is " + dir.getOpposite() + ", " +
                "Block is " + chest_bs.getType());

        {
            String name = chest_bs.getType().getName();
            getLogger().info("Chest's name is: `" + name + "`");

            boolean bad = true;

            for(String chestName : chestNames) {
                if(name.equals(chestName)) {
                    bad = false;
                    break;
                }
            }

            if(bad)
                return;
        }

        FedorasMarket.getLogger().info("Chest is in the list!");

        TileEntity te;
        {
            Optional<TileEntity> opt_te = chest_loc.getTileEntity();
            if(!opt_te.isPresent())
                return;
            te = opt_te.get();
        }

        if(!(te instanceof TileEntityCarrier))
            return;

        TileEntityCarrier tec = (TileEntityCarrier) te;

        FedorasMarket.getLogger().info("Chest is a tile entity carrier!");
    }*/

    public DataTransactionResult save() {

        // save the data to the sign
        DataTransactionResult dtr = sign.offer(FmDataKeys.SHOP_REFERENCE, this.toReference());

        FedorasMarket.getLogger().info("DTR: " + dtr);

        return dtr;
    }

}
