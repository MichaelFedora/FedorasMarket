package io.github.michaelfedora.fedorasmarket.cmdexecutors.shop;

import io.github.michaelfedora.fedorasmarket.cmdexecutors.FmExecutorBase;
import io.github.michaelfedora.fedorasmarket.data.FmDataKeys;
import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.database.DatabaseCategory;
import io.github.michaelfedora.fedorasmarket.database.DatabaseManager;
import io.github.michaelfedora.fedorasmarket.database.DatabaseQuery;
import io.github.michaelfedora.fedorasmarket.shop.SerializedShopData;
import io.github.michaelfedora.fedorasmarket.shop.Shop;
import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import org.spongepowered.api.block.tileentity.Sign;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.Tuple;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Michael on 3/3/2016.
 */
public class FmShopCleanExecutor extends FmExecutorBase {

    public static void cleanAll() {
        boolean failed = false;
        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectAll(conn, DatabaseCategory.SHOPDATA);
            List<Tuple<ShopReference,ShopData>> results = new ArrayList<>();
            while(resultSet.next()) {

                UUID playerId = (UUID) resultSet.getObject(DatabaseQuery.AUTHOR.v);
                UUID instance = (UUID) resultSet.getObject(DatabaseQuery.NAME.v);
                Optional<ShopData> opt_data = Optional.empty();
                try {
                    opt_data = Optional.of(((SerializedShopData) resultSet.getObject(DatabaseQuery.DATA.v)).deserialize());
                } catch (BadDataException e) {
                    failed = true;
                    //logWarn("Found bad database entry!");
                }

                if(opt_data.isPresent() && !failed) {

                    results.add(new Tuple<>(new ShopReference(playerId, instance), opt_data.get()));

                } else {

                    failed = false;

                    DatabaseManager.delete(conn, playerId, DatabaseCategory.SHOPDATA, instance);
                }
            }

            for(Tuple<ShopReference,ShopData> result : results) {
                //msg(src, "Looking at shop [" + resultSet.getString("name") + "::" + resultSet.getObject("instance") + "]");
                UUID playerId = result.getFirst().author;
                UUID instance = result.getFirst().instance;
                ShopData data = result.getSecond();

                //String prefix = "[" + name + "::" + instance + "]" + ": ";
                Optional<TileEntity> opt_te = data.getLocation().getTileEntity();
                Sign sign;
                if(!opt_te.isPresent()) {
                    failed = true;
                    //log(prefix + "Location does not have a tile entity!");
                } else if(!(opt_te.get() instanceof Sign)) {
                    failed = true;
                    //log(prefix + "Tile Entity is not a Sign!");
                } else if(!((sign = (Sign) opt_te.get()).get(FmDataKeys.SHOP_REFERENCE).isPresent())) {
                    failed = true;
                    //log(prefix + "Sign does not support the data key!");
                } else if(!(sign.get(FmDataKeys.SHOP_REFERENCE).get().instance.equals(instance))) {
                    failed = true;
                    //log(prefix + "Instances do not match up!");
                } else if(!(Shop.fromSign(sign).isPresent())) {
                    failed = true;
                    //log(prefix + "Bad data key entry!");
                } //else
                    //log(prefix + "Good!");

                if(failed) {
                    failed = false;
                    //log(prefix + "Bad, deleting :c");
                    DatabaseManager.delete(conn, playerId, DatabaseCategory.SHOPDATA, instance);
                }
            }

        } catch (SQLException e) {
            logError("[fm shop clean]: SQL Error", e);
        }
    }

    @Override
    protected String getName() {
        return "shop clean";
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext ctx) throws CommandException {

        if(!(src instanceof Player)) {
            throw makeSourceNotPlayerException();
        }

        UUID playerId = ((Player) src).getUniqueId();

        boolean failed = false;
        try(Connection conn = DatabaseManager.getConnection()) {

            ResultSet resultSet = DatabaseManager.selectAll(conn, playerId, DatabaseCategory.SHOPDATA);
            List<Tuple<UUID,ShopData>> results = new ArrayList<>();
            while(resultSet.next()) {

                UUID instance = (UUID) resultSet.getObject(DatabaseQuery.NAME.v);
                Optional<ShopData> opt_data = Optional.empty();
                try {
                    opt_data = Optional.of(((SerializedShopData) resultSet.getObject(DatabaseQuery.DATA.v)).deserialize());
                } catch (BadDataException e) {
                    failed = true;
                    warn(src, "Found bad database entry!");
                }

                if(opt_data.isPresent() && !failed) {

                    results.add(new Tuple<>(instance, opt_data.get()));

                } else {

                    failed = false;

                    DatabaseManager.delete(conn, playerId, DatabaseCategory.SHOPDATA, instance);
                }
            }

            for(Tuple<UUID,ShopData> result : results) {
                //msg(src, "Looking at shop [" + resultSet.getString("name") + "::" + resultSet.getObject("instance") + "]");
                UUID instance = result.getFirst();
                ShopData data = result.getSecond();

                String prefix = "[" + instance + "]" + ": ";
                Optional<TileEntity> opt_te = data.getLocation().getTileEntity();
                Sign sign;
                if(!opt_te.isPresent()) {
                    failed = true;
                    msg(src, prefix + "Location does not have a tile entity!");
                } else if(!(opt_te.get() instanceof Sign)) {
                    failed = true;
                    msg(src, prefix + "Tile Entity is not a Sign!");
                } else if(!((sign = (Sign) opt_te.get()).get(FmDataKeys.SHOP_REFERENCE).isPresent())) {
                    failed = true;
                    msg(src, prefix + "Sign does not support the data key!");
                } else if(!(sign.get(FmDataKeys.SHOP_REFERENCE).get().instance.equals(instance))) {
                    failed = true;
                    msg(src, prefix + "Instances do not match up!");
                } else if(!(Shop.fromSign(sign).isPresent())) {
                    failed = true;
                    msg(src, prefix + "Bad data key entry!");
                } else
                    msg(src, prefix + "Good!");

                if(failed) {
                    failed = false;

                    DatabaseManager.delete(conn, playerId, DatabaseCategory.SHOPDATA, instance);
                }
            }

        } catch (SQLException e) {
            throw makeException("SQL Error", e, src);
        }

        return CommandResult.success();
    }
}
