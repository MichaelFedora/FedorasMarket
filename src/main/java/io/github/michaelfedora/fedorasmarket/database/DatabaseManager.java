package io.github.michaelfedora.fedorasmarket.database;

import com.google.inject.Inject;
import io.github.michaelfedora.fedorasmarket.PluginInfo;
import io.github.michaelfedora.fedorasmarket.database.table.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.sql.SqlService;

import java.sql.*;

/**
 * Created by Michael on 2/27/2016.
 */
public final class DatabaseManager {

    public static final String DB_ID = "jdbc:h2:./mods/FedorasData/market.db";
    public static final String DB_TABLE = "tbl" + PluginInfo.DATA_ROOT;

    /*
    Database layout:
    - Tablename: "type:name", contains it's own layout; e.x.
     - [tradeform:00000-0000-00-0001] {"name", <data>}
     - [modifier:<uuid>] {"name", <data>}
     - [depot:server] {<idx>, <item_data>}
     - [shop:1234-5678-91-01234] {<uuid>, <data>}
     - [tradereq:4444-4656-87-3535] {<idx>, <data>}
     */

    public static final TradeFormTable tradeForm = new TradeFormTable();
    public static final ModifierTable modifier = new ModifierTable();
    public static final DepotTable depot = new DepotTable();
    public static final ShopTable shop = new ShopTable();
    public static final TradeReqTable tradeReq = new TradeReqTable();
    public static final UserdataTable userdata = new UserdataTable();

    @Inject
    private static PluginContainer plugin;

    private DatabaseManager() { }

    private static SqlService SQL;
    public static javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        if(SQL == null)
            SQL = Sponge.getServiceManager().provide(SqlService.class).orElseThrow(() -> new SQLException("Could not 'get' SqlService!"));

        return SQL.getDataSource(jdbcUrl);
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource(DB_ID).getConnection();
    }

    public static void makeIfNotExist(Connection conn, String id) throws SQLException {
        tradeForm.makeIfNotExist(conn, id);
        modifier.makeIfNotExist(conn, id);
        depot.makeIfNotExist(conn, id);
        shop.makeIfNotExist(conn, id);
        tradeReq.makeIfNotExist(conn, id);
        userdata.makeIfNotExist(conn, id);
    }

    public static void initialize() {

        try(Connection conn = getConnection()) {

            for (Player p : Sponge.getServer().getOnlinePlayers()) // not that this is necessary...
                makeIfNotExist(conn, p.getUniqueId().toString());

        } catch(SQLException e) {
            plugin.getLogger().error("SQL Error", e);
        }

        Sponge.getEventManager().registerListener(plugin, ClientConnectionEvent.Join.class, DatabaseManager::OnPlayerJoin);
    }

    private static void OnPlayerJoin(ClientConnectionEvent.Join event) {
        try(Connection conn = getConnection()) {
            makeIfNotExist(conn, event.getTargetEntity().getUniqueId().toString());
        } catch(SQLException e) {
            plugin.getLogger().error("SQL Error", e);
        }
    }
}
