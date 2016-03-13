package io.github.michaelfedora.fedorasmarket.database;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.shop.SerializedShopData;
import io.github.michaelfedora.fedorasmarket.shop.ShopReference;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.util.Tuple;

import java.lang.reflect.Type;
import java.sql.*;
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

    public static void initialize() {

        try {
            tradeFormDB.initialize();
        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error: ", e);
        }

        try {
            shopDataDB.initialize();
        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error: ", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource(DB_ID).getConnection();
    }

    public static final class tradeFormDB {

        private static Map<String,Tuple<String,Type>> PARAMS = new LinkedHashMap<>();
        static {
            PARAMS.put("author", new Tuple<>("uuid", UUID.class));
            PARAMS.put("name", new Tuple<>("varchar(255)", String.class)); // FIXME: String or varchar(255)?
            PARAMS.put("data", new Tuple<>("other", SerializedTradeForm.class)); // FIXME: Object or other?
        }

        public static Map<String,Tuple<String,Type>> getParams() { return PARAMS; }
        public static String getKey(int i) {
            return (String) PARAMS.keySet().toArray()[1];
        }

        public static void initialize() throws SQLException {
            //DatabaseManager.initialize_generic(DB_TABLE_TRADE_FORMS, PARAMS);

            try(Connection conn = getDataSource(DB_ID).getConnection()) {
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + DB_TABLE_TRADE_FORMS + "(author uuid, name varchar(255), data other)").execute();

                ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + DB_TABLE_TRADE_FORMS).executeQuery();
                ResultSetMetaData metaData = resultSet.getMetaData();

                FedorasMarket.getLogger().info("Table [" + DB_TABLE_TRADE_FORMS + "]: ");
                FedorasMarket.getLogger().info(resultSet.toString()); // FIXME: resultset thing

                StringBuilder sb = new StringBuilder();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {

                    sb.append(metaData.getColumnName(i));
                    if (i < metaData.getColumnCount())
                        sb.append(" | ");
                }
                FedorasMarket.getLogger().info(sb.toString());

                while(resultSet.next()) {

                    sb.setLength(0);
                    for(int i = 1; i <= metaData.getColumnCount(); i++) {

                        sb.append(resultSet.getObject(i));
                        if(i < metaData.getColumnCount())
                            sb.append(" | ");
                    }
                    FedorasMarket.getLogger().info(sb.toString());
                }

            }
        }

        public static ResultSet selectWithMore(Connection conn, UUID author, String name, String more) throws SQLException {

            int i = 0;
            String statement = "SELECT * FROM " + DB_TABLE_TRADE_FORMS + " WHERE author=? AND name=? " + more;

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            return preparedStatement.executeQuery();

        }

        public static ResultSet select(Connection conn, UUID author, String name) throws SQLException {
            return selectWithMore(conn, author, name, "");
        }

        public static ResultSet selectWithMore(Connection conn, UUID author, String more) throws SQLException {

            int i = 0;
            String statement = "SELECT * FROM " + DB_TABLE_TRADE_FORMS + " WHERE author=? " + more;


        PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            return preparedStatement.executeQuery();

        }

        public static ResultSet select(Connection conn, UUID author) throws SQLException {
            return selectWithMore(conn, author, "");
        }

        public static boolean update(Connection conn, SerializedTradeForm data, UUID author, String name) throws SQLException {

            int i = 0;
            String statement = "UPDATE " + DB_TABLE_TRADE_FORMS + " SET data=? WHERE author=? AND name=?";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, data);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            return preparedStatement.execute();
        }

        public static boolean delete(Connection conn, UUID author, String name) throws SQLException {

            int i = 0;
            String statement = "DELETE FROM " + DB_TABLE_TRADE_FORMS + " WHERE author=? AND name=?";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            return preparedStatement.execute();
        }

        public static boolean insert(Connection conn, UUID author, String name, SerializedTradeForm data) throws SQLException {

            int i = 0;
            String statement = "INSERT INTO " + DB_TABLE_TRADE_FORMS + "(author, name, data) values (?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            preparedStatement.setObject(++i, data);
            return preparedStatement.execute();
        }
    }

    public static final class shopDataDB {

        private static Map<String,Tuple<String,Type>> PARAMS = new LinkedHashMap<>();
        static {
            PARAMS.put("author", new Tuple<>("uuid", UUID.class));
            PARAMS.put("name", new Tuple<>("varchar(255)", String.class)); // FIXME: String or varchar(255)?
            PARAMS.put("instance", new Tuple<>("uuid", UUID.class));
            PARAMS.put("data", new Tuple<>("other", SerializedShopData.class));
        }

        public static Map<String,Tuple<String,Type>> getParams() { return PARAMS; }

        public static void initialize() throws SQLException {
            //DatabaseManager.initialize_generic(DB_TABLE_TRADE_FORMS, PARAMS);

            try(Connection conn = getDataSource(DB_ID).getConnection()) {
                conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + DB_TABLE_SHOP_FORMS + "(author uuid, name varchar(255), instance uuid, data other)").execute();

                ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + DB_TABLE_SHOP_FORMS).executeQuery();
                ResultSetMetaData metaData = resultSet.getMetaData();
                FedorasMarket.getLogger().info("Table [" + DB_TABLE_SHOP_FORMS + "]: ");
                FedorasMarket.getLogger().info(resultSet.toString()); // FIXME: resultset thing

                // DEBUG THING
                StringBuilder sb = new StringBuilder();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {

                    sb.append(metaData.getColumnName(i));
                    if (i < metaData.getColumnCount())
                        sb.append(" | ");
                }
                FedorasMarket.getLogger().info(sb.toString());

                while(resultSet.next()) {

                    sb.setLength(0);
                    for(int i = 1; i <= metaData.getColumnCount(); i++) {

                        sb.append(resultSet.getObject(i));
                        if(i < metaData.getColumnCount())
                            sb.append(" | ");
                    }
                    FedorasMarket.getLogger().info(sb.toString());
                }

            }
        }

        public static ResultSet selectWithMore(Connection conn, UUID author, String name, UUID instance, String more) throws SQLException {

            int i = 0;
            String statement = "SELECT * FROM " + DB_TABLE_SHOP_FORMS + " WHERE author=? AND name=? AND instance=? " + more;

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            preparedStatement.setObject(++i, instance);
            return preparedStatement.executeQuery();
        }

        public static ResultSet select(Connection conn, UUID author, String name, UUID instance) throws SQLException {
            return selectWithMore(conn, author, name, instance, "");
        }

        public static ResultSet selectWithMore(Connection conn, UUID author, String name, String more) throws SQLException {

            int i = 0;
            String statement = "SELECT * FROM " + DB_TABLE_SHOP_FORMS + " WHERE author=? AND name=? " + more;

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            return preparedStatement.executeQuery();
        }

        public static ResultSet select(Connection conn, UUID author, String name) throws SQLException {
            return selectWithMore(conn, author, name, "");
        }

        public static ResultSet selectWithMore(Connection conn, UUID author, String more) throws SQLException {

            int i = 0;
            String statement = "SELECT * FROM " + DB_TABLE_SHOP_FORMS + " WHERE author=? " + more;

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            return preparedStatement.executeQuery();
        }


        public static ResultSet select(Connection conn, UUID author) throws SQLException {
            return selectWithMore(conn, author, "");
        }

        public static ResultSet selectWithMore(Connection conn, String more) throws SQLException {

            String statement = "SELECT * FROM " + DB_TABLE_SHOP_FORMS + " " + more;

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            return preparedStatement.executeQuery();
        }

        public static ResultSet select(Connection conn) throws SQLException {
            return selectWithMore(conn, "");
        }

        public static ResultSet selectWithMore(Connection conn, ShopReference shopReference, String more) throws SQLException {
            return selectWithMore(conn, shopReference.author, shopReference.name, shopReference.instance, more);
        }

        public static ResultSet select(Connection conn, ShopReference shopReference) throws SQLException {
            return select(conn, shopReference.author, shopReference.name, shopReference.instance);
        }

        public static boolean update(Connection conn, SerializedShopData data, UUID author, String name, UUID instance) throws SQLException {

            int i = 0;
            String statement = "UPDATE " + DB_TABLE_SHOP_FORMS + " SET data=? WHERE author=? AND name=? AND instance=?";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, data);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            preparedStatement.setObject(++i, instance);
            return preparedStatement.execute();
        }

        public static boolean delete(Connection conn, UUID author, String name, UUID instance) throws SQLException {

            int i = 0;
            String statement = "DELETE FROM " + DB_TABLE_SHOP_FORMS + " WHERE author=? AND name=? AND instance=?";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            preparedStatement.setObject(++i, instance);
            return preparedStatement.execute();
        }

        public static boolean insert(Connection conn, UUID author, String name, UUID instance, SerializedShopData data) throws SQLException {

            int i = 0;
            String statement = "INSERT INTO " + DB_TABLE_SHOP_FORMS + "(author, name, instance, data) values (?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, author);
            preparedStatement.setString(++i, name);
            preparedStatement.setObject(++i, instance);
            preparedStatement.setObject(++i, data);
            return preparedStatement.execute();
        }
    }

}
