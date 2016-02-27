package io.github.michaelfedora.fedorasmarket.database;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.shop.ShopForm;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.util.Tuple;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 2/27/2016.
 */
public final class DatabaseManager {

    public static final String DB_ID = "jdbc:h2:./mods/FedorasData/market.db";
    public static final String DB_TABLE_TRADE_FORMS = "fm_trade_forms";
    public static final String DB_TABLE_SHOP_FORMS = "fm_shop_forms";

    private DatabaseManager() { }

    private static SqlService SQL;
    public static javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        if(SQL == null)
            SQL = Sponge.getServiceManager().provide(SqlService.class).get();

        return SQL.getDataSource(jdbcUrl);
    }

    public static void initialize() throws SQLException {

        tradeForms.initialize();
        shopForms.initialize();
    }

    private static void initialize_generic(String table, Map<String,Tuple<String,Type>> params) throws SQLException {
        Connection conn = getDataSource(DB_ID).getConnection();

        StringBuilder sb = new StringBuilder("");
        sb.append("CREATE TABLE IF NOT EXISTS ");
        sb.append(table);

        int i = 0;
        for(Map.Entry<String,Tuple<String,Type>> entry : params.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(params.get(entry.getValue().getFirst()).toString());
            if(i++ < params.size())
                sb.append(", ");
        }
        sb.append(")");


        try {
            conn.prepareStatement(sb.toString()).execute();

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + table).executeQuery();

            FedorasMarket.getLogger().info("Table [" + table + "]: ");
            FedorasMarket.getLogger().info(resultSet.toString()); // FIXME: resultset thing

            // DEBUG THING
            /*while(resultSet.next()) {
                sb.setLength(0);
                i = 0;
                for(Map.Entry<String,Tuple<String,Type>> entry : params.entrySet()) {
                    sb.append(resultSet.getObject(entry.getKey()));
                    if(++i < params.size())
                        sb.append(" | ");
                }
                FedorasMarket.getLogger().info(sb.toString());
            }*/

        } finally {
            conn.close();
        }


    }

    public static final class tradeForms {

        private static Map<String,Tuple<String,Type>> PARAMS = new LinkedHashMap<>();
        static {
            PARAMS.put("id", new Tuple<>("uuid", UUID.class));
            PARAMS.put("name", new Tuple<>("varchar(255)", String.class)); // FIXME: String or varchar(255)?
            PARAMS.put("data", new Tuple<>("other", TradeForm.Data.class)); // FIXME: Object or other?
        }

        public static Map<String,Tuple<String,Type>> getParams() { return PARAMS; }

        public static void initialize() throws SQLException {
            //DatabaseManager.initialize_generic(DB_TABLE_TRADE_FORMS, PARAMS);
            Connection conn = getDataSource(DB_ID).getConnection();

            try {
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + DB_TABLE_TRADE_FORMS + "(id uuid, name varchar(255), data other)").execute();

                ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + DB_TABLE_TRADE_FORMS).executeQuery();

                FedorasMarket.getLogger().info("Table [" + DB_TABLE_TRADE_FORMS + "]: ");
                FedorasMarket.getLogger().info(resultSet.toString()); // FIXME: resultset thing

                // DEBUG THING
                /*while(resultSet.next()) {
                    sb.setLength(0);
                    i = 0;
                    for(Map.Entry<String,Tuple<String,Type>> entry : params.entrySet()) {
                        sb.append(resultSet.getObject(entry.getKey()));
                        if(++i < params.size())
                            sb.append(" | ");
                    }
                    FedorasMarket.getLogger().info(sb.toString());
                }*/

            } finally {
                conn.close();
            }
        }

        public static ResultSet selectWithMore(UUID id, String name, String more) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ? WHERE id=? AND name=? " + more);
                preparedStatement.setString(++i, DB_TABLE_TRADE_FORMS);
                preparedStatement.setObject(++i, id);
                preparedStatement.setString(++i, name);
                return preparedStatement.executeQuery();

            } finally {
                conn.close();
            }
        }

        public static ResultSet select(UUID id, String name) throws SQLException {
            return selectWithMore(id, name, "");
        }

        public static ResultSet selectWithMore(UUID id, String more) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ? WHERE id=?" + more);
                preparedStatement.setString(++i, DB_TABLE_TRADE_FORMS);
                preparedStatement.setObject(++i, id);
                return preparedStatement.executeQuery();

            } finally {
                conn.close();
            }
        }

        public static ResultSet select(UUID id) throws SQLException {
            return selectWithMore(id, "");
        }

        public static boolean update(TradeForm.Data data, UUID id, String name) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("UPDATE ? SET data=? WHERE id=? AND name=?");
                preparedStatement.setString(++i, DB_TABLE_TRADE_FORMS);
                preparedStatement.setObject(++i, data);
                preparedStatement.setObject(++i, id);
                preparedStatement.setString(++i, name);
                return preparedStatement.execute();

            } finally {
                conn.close();
            }
        }

        public static boolean delete(UUID id, String name) throws SQLException {
            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM ? WHERE id=? AND name=?");
                preparedStatement.setString(++i, DB_TABLE_TRADE_FORMS);
                preparedStatement.setObject(++i, id);
                preparedStatement.setString(++i, name);
                return preparedStatement.execute();

            } finally {
                conn.close();
            }
        }

        public static boolean insert(UUID id, String name, TradeForm.Data data) throws SQLException {
            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO ?(id, name, data) values (?, ?, ?)");
                preparedStatement.setString(++i, DB_TABLE_TRADE_FORMS);
                preparedStatement.setObject(++i, id);
                preparedStatement.setString(++i, name);
                preparedStatement.setObject(++i, data);
                return preparedStatement.execute();

            } finally {
                conn.close();
            }
        }
    }

    public static final class shopForms {

        private static Map<String,Tuple<String,Type>> PARAMS = new LinkedHashMap<>();
        static {
            PARAMS.put("author", new Tuple<>("uuid", UUID.class));
            PARAMS.put("name", new Tuple<>("varchar(255)", String.class)); // FIXME: String or varchar(255)?
            PARAMS.put("instance", new Tuple<>("uuid", UUID.class));
            PARAMS.put("data", new Tuple<>("other", ShopForm.Data.class));
        }

        public static Map<String,Tuple<String,Type>> getParams() { return PARAMS; }

        public static void initialize() throws SQLException {
            DatabaseManager.initialize_generic(DB_TABLE_SHOP_FORMS, PARAMS);
        }

        public static ResultSet selectWithMore(UUID author, String name, UUID instance, String more) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ? WHERE author=? AND name=? AND instance=? " + more);
                preparedStatement.setString(++i, DB_TABLE_SHOP_FORMS);
                preparedStatement.setObject(++i, author);
                preparedStatement.setString(++i, name);
                preparedStatement.setObject(++i, instance);
                return preparedStatement.executeQuery();

            } finally {
                conn.close();
            }
        }

        public static ResultSet select(UUID author, String name, UUID instance) throws SQLException {
            return selectWithMore(author, name, instance, "");
        }

        public static ResultSet selectWithMore(UUID author, String name, String more) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ? WHERE author=? AND name=? " + more);
                preparedStatement.setString(++i, DB_TABLE_SHOP_FORMS);
                preparedStatement.setObject(++i, author);
                preparedStatement.setString(++i, name);
                return preparedStatement.executeQuery();

            } finally {
                conn.close();
            }
        }

        public static ResultSet select(UUID author, String name) throws SQLException {
            return selectWithMore(author, name, "");
        }

        public static ResultSet selectWithMore(UUID author, String more) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ? WHERE author=? " + more);
                preparedStatement.setString(++i, DB_TABLE_SHOP_FORMS);
                preparedStatement.setObject(++i, author);
                return preparedStatement.executeQuery();

            } finally {
                conn.close();
            }
        }

        public static ResultSet select(UUID author) throws SQLException {
            return selectWithMore(author, "");
        }

        public static ResultSet selectWithMore(String more) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ? " + more);
                preparedStatement.setString(++i, DB_TABLE_SHOP_FORMS);
                return preparedStatement.executeQuery();

            } finally {
                conn.close();
            }
        }

        public static ResultSet select() throws SQLException {
            return selectWithMore("");
        }

        public static ResultSet selectWithMore(ShopReference shopReference, String more) throws SQLException {
            return selectWithMore(shopReference.author, shopReference.name, shopReference.instance, more);
        }

        public static ResultSet select(ShopReference shopReference) throws SQLException {
            return select(shopReference.author, shopReference.name, shopReference.instance);
        }

        public static boolean update(ShopForm.Data data, UUID author, String name, UUID instance) throws SQLException {

            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("UPDATE ? SET data=? WHERE author=? AND name=? AND instance=?");
                preparedStatement.setString(++i, DB_TABLE_SHOP_FORMS);
                preparedStatement.setObject(++i, data);
                preparedStatement.setObject(++i, author);
                preparedStatement.setString(++i, name);
                preparedStatement.setObject(++i, instance);
                return preparedStatement.execute();

            } finally {
                conn.close();
            }
        }

        public static boolean delete(UUID author, String name, UUID instance) throws SQLException {
            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM ? WHERE author=? AND name=? AND instance=?");
                preparedStatement.setString(++i, DB_TABLE_SHOP_FORMS);
                preparedStatement.setObject(++i, author);
                preparedStatement.setString(++i, name);
                preparedStatement.setObject(++i, instance);
                return preparedStatement.execute();

            } finally {
                conn.close();
            }
        }

        public static boolean insert(UUID author, String name, UUID instance, ShopForm.Data data) throws SQLException {
            Connection conn = getDataSource(DB_ID).getConnection();

            try {

                int i = 0;
                PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO ?(author, name, instance, data) values (?, ?, ?, ?)");
                preparedStatement.setString(++i, DB_TABLE_SHOP_FORMS);
                preparedStatement.setObject(++i, author);
                preparedStatement.setString(++i, name);
                preparedStatement.setObject(++i, instance);
                preparedStatement.setObject(++i, data);
                return preparedStatement.execute();

            } finally {
                conn.close();
            }
        }
    }

}
