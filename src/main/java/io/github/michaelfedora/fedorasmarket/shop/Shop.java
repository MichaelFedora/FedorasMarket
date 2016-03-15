package io.github.michaelfedora.fedorasmarket.shop;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceData;
import io.github.michaelfedora.fedorasmarket.data.shopreference.ShopReferenceDataManipulatorBuilder;
import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import io.github.michaelfedora.fedorasmarket.trade.GoodType;
import io.github.michaelfedora.fedorasmarket.trade.TradeActiveParty;
import io.github.michaelfedora.fedorasmarket.util.FmUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by MichaelFedora on 1/23/2016.
 *
 * This file is released under the MIT License. Please see the LICENSE file for
 * more information. Thank you.
 */
public class Shop {

    protected Sign sign;
    protected ShopData data;

    public Shop(Sign sign, ShopData shopData) {

        this.sign = sign;
        this.data = shopData;
    }

    public Sign getSign() { return this.sign; }
    public ShopData getData() { return this.data; }

    public boolean isServerShop() {
        return this.data.playerId.isPresent();
    }

    public boolean shouldHaveInventory() {
        return !this.isServerShop() && data.tradeForm.getTradeType().has(GoodType.ITEM);
    }

    public boolean shouldHaveAccount() {
        return !this.isServerShop() && data.tradeForm.getTradeType().has(GoodType.CURRENCY);
    }

    public Optional<Inventory> getInventory() {

        if(!this.shouldHaveInventory())
            return Optional.empty();

        Optional<Direction> opt_dir = sign.get(Keys.DIRECTION);
        Optional<TileEntityCarrier> opt_tec = Optional.empty();
        if(opt_dir.isPresent())
            opt_tec = sign.getLocation().getRelative(opt_dir.get().getOpposite()).getTileEntity().map((te) -> (TileEntityCarrier) te);

        if(!opt_tec.isPresent())
            return Optional.empty();
        if(!FedorasMarket.getChestNames().contains(opt_tec.get().getBlock().getName()))
            return Optional.empty();

        return Optional.of(opt_tec.get().getInventory());
    }

    public Optional<Account> getAccount() {

        if(!this.shouldHaveAccount())
            return Optional.empty();

        EconomyService eco = FedorasMarket.getEconomyService();

        return eco.getOrCreateAccount(this.data.playerId.get()).map((a) -> (Account) a);
    }

    /**
     * Initializes the shop (on the sign, using the data).
     * Attaches references and other things.
     * @param conn the connection to the database
     * @return success
     * @throws SQLException
     */
    public boolean initialize(Connection conn) throws SQLException {

        if(shouldHaveInventory())
            if(!getInventory().isPresent())
                return false;

        if(shouldHaveAccount())
            if(!getAccount().isPresent())
                return false;

        // =====

        ResultSet resultSet = DatabaseManager.selectAll(conn, data.playerId.orElse(null), DatabaseCategory.SHOPDATA);

        UUID instance;

        boolean foundUnique;
        while(true) { // find a unique uuid;

            instance = UUID.randomUUID();
            foundUnique = true;
            while (resultSet.next()) {

                if (instance.equals(resultSet.getObject(DatabaseQuery.NAME.v))) {
                    foundUnique = false;
                    break;
                }
            }

            if (foundUnique)
                break;
        }

        // =====

        ShopReferenceDataManipulatorBuilder builder = (ShopReferenceDataManipulatorBuilder) Sponge.getDataManager().getManipulatorBuilder(ShopReferenceData.class).get();
        ShopReferenceData data = builder.createFrom(new ShopReference(this.data.playerId.orElse(null), instance));
        DataTransactionResult dtr = sign.offer(data);

        if(dtr.isSuccessful()) {
            DatabaseManager.insert(conn, this.data.playerId.orElse(null), DatabaseCategory.SHOPDATA, instance, this.data.serialize());
            this.writeToSign();
            //msg(player, "Made the " + ((isServerOwned) ? "server-" : "")+ "shop!");
            return true;
        } else {
            //error(player, "Could not pass data to sign!");
            return false;
        }
    }

    public void writeToSign() {

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

            ResultSet resultSet = DatabaseManager.select(conn, 1, ref.author, DatabaseCategory.SHOPDATA, ref.instance);
            if(resultSet.next()) {
                try {
                    Shop shop = new Shop(sign, ((SerializedShopData) resultSet.getObject(DatabaseQuery.DATA.v)).deserialize());
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
            Optional<Sign> opt_sign = FmUtil.getShopSignFromLocation(location);
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

        if(!this.isServerShop())
            try {
                owner = new TradeActiveParty(this.getAccount().orElseThrow(Exception::new), this.getInventory().orElseThrow(Exception::new));
            } catch (Exception e) {
                player.sendMessage(FmUtil.makeMessageError("Shop", "Can't get account/inventory.. :c"));
                return;
            }
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

            if(!this.isServerShop())
                try {
                    owner = new TradeActiveParty(this.getAccount().orElseThrow(Exception::new), this.getInventory().orElseThrow(Exception::new));
                } catch (Exception e) {
                    player.sendMessage(FmUtil.makeMessageError("Shop", "Can't get account/inventory.. :c"));
                    return;
                }
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
