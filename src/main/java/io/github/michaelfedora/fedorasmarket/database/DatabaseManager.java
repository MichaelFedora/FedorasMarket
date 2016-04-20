package io.github.michaelfedora.fedorasmarket.database;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.shop.ShopModifier;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.sql.SqlService;

import java.sql.*;
import java.util.*;

/**
 * Created by Michael on 2/27/2016.
 */
public final class DatabaseManager {

    public static final String DB_ID = "jdbc:h2:./mods/FedorasData/market.db";
    public static final String DB_TABLE = "data";

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

    public static void initialize() {

        try(Connection conn = getConnection()) {

            conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + DB_TABLE + DatabaseQuery.makeConstructor()).execute();

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + DB_TABLE).executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            FedorasMarket.getLogger().info("Table [" + DB_TABLE + "]: ");
            FedorasMarket.getLogger().info(resultSet.toString());

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

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error", e);
        }
    }

    /*
    TODO: fix database layout

    Database layout:
    - Tablename: "type:name", contains it's own layout; e.x.
     - [tradeform:00000-0000-00-0001] {"name", <data>}
     - [modifier:<uuid>] {"name", <data>}
     - [depot:server] {<idx>, <item_data>}
     - [shop:1234-5678-91-01234] {<uuid>, <data>}
     - [tradereq:4444-4656-87-3535] {<idx>, <data>}
     */

    public interface Db<V, K> {

        /**
         * Gets all the data for a particular id
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id the id of the table/owner
         * @return the list of values
         * @throws SQLException
         */
        List<V> getAll(Connection conn, String id) throws SQLException;

        /**
         * Gets a specific data entry for a particular id and key
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id the id of the table/owner
         * @param key the key of the value
         * @return the value
         * @throws SQLException
         */
        Optional<V> get(Connection conn, String id, K key) throws SQLException;

        /**
         * Updates a particular entry with a new value
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id the id of the table/owner
         * @param key the key of the entry to update
         * @param value the new value of the specified key
         * @return whether or not it succeeded
         * @throws SQLException
         */
        boolean update(Connection conn, String id, K key, V value) throws SQLException;

        /**
         * Deletes a particular entry
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id the id of the table/owner
         * @param key the key of the entry to delete
         * @return whether or not it succeeded
         * @throws SQLException
         */
        boolean delete(Connection conn, String id, K key) throws SQLException;

        /**
         * Inserts a new entry
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id the id of the table/owner
         * @param key the key of the entry to insert into
         * @param value the value of the entry to insert
         * @return whether or not it succeeded
         * @throws SQLException
         */
        boolean insert(Connection conn, String id, K key, V value) throws SQLException;
    }

    // [tradeform:<string>] {"name", <data>}
    public static class DbTradeForm implements Db<TradeForm, String> {

        private DbTradeForm() { }


        /**
         * Gets all the data for a particular id
         *
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id   the id of the table/owner
         * @return the list of values
         * @throws SQLException
         */
        @Override
        public List<TradeForm> getAll(Connection conn, String id) throws SQLException {

            List<TradeForm> list = new ArrayList<>();

            String statement = "SELECT data FROM `tradeform:" + id + "`";

            ResultSet resultSet = conn.prepareStatement(statement).executeQuery();

            Object data;
            while(resultSet.next()) {

                data = resultSet.getObject("data");

                if(!(data instanceof SerializedTradeForm))
                    continue;

                try {
                    list.add(((SerializedTradeForm) data).deserialize());
                } catch (BadDataException e) {
                    // do nothing
                }
            }

            return list;
        }

        /**
         * Gets a specific data entry for a particular id and key
         *
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id   the id of the table/owner
         * @param key  the key of the value
         * @return the value
         * @throws SQLException
         */
        @Override
        public Optional<TradeForm> get(Connection conn, String id, String key) throws SQLException {
            String statement = "SELECT data FROM `tradeform:" + id + "` WHERE key=?";

            int i = 0;
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(++i, key);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next())
                return Optional.empty();

            Object data = resultSet.getObject("data");
            if(!(data instanceof SerializedTradeForm))
                return Optional.empty();

            try {
                return Optional.of(((SerializedTradeForm) data).deserialize());
            } catch(BadDataException e) {
                return Optional.empty();
            }
        }

        /**
         * Updates a particular entry with a new value
         *
         * @param conn  the database connection (to be used inside a try-catch(-finally)
         * @param id    the id of the table/owner
         * @param key   the key of the entry to update
         * @param value the new value of the specified key
         * @return whether or not it succeeded
         * @throws SQLException
         */
        @Override
        public boolean update(Connection conn, String id, String key, TradeForm value) throws SQLException {
            String statement = "UPDATE `tradeform:" + id + "` SET data=? WHERE name=?";

            int i = 0;
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setObject(++i, value);
            preparedStatement.setString(++i, key);

            return preparedStatement.execute();
        }

        /**
         * Deletes a particular entry
         *
         * @param conn the database connection (to be used inside a try-catch(-finally)
         * @param id   the id of the table/owner
         * @param key  the key of the entry to delete
         * @return whether or not it succeeded
         * @throws SQLException
         */
        @Override
        public boolean delete(Connection conn, String id, String key) throws SQLException {
            int i = 0;
            String statement = "DELETE FROM `tradeform:" + id + "` WHERE name=?";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(++i, key);

            return preparedStatement.execute();
        }

        /**
         * Inserts a new entry
         *
         * @param conn  the database connection (to be used inside a try-catch(-finally)
         * @param id    the id of the table/owner
         * @param key   the key of the entry to insert into
         * @param value the value of the entry to insert
         * @return whether or not it succeeded
         * @throws SQLException
         */
        @Override
        public boolean insert(Connection conn, String id, String key, TradeForm value) throws SQLException {
            int i = 0;
            String statement = "INSERT INTO `tradeform:" + id + "(name, data) values (?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            preparedStatement.setString(++i, key); // FIXME: try this
            preparedStatement.setObject(++i, value);

            return preparedStatement.execute();
        }
    }

    // [modifier:<string>] {"name", <data>}
    public static class DbModifier implements Db<ShopModifier, String> {

    }

    // [depot:<string>] {<idx>, <item_data>}
    public static class DbDepot implements Db<ItemStack, int> {

    }

    // [shop:<string>] {<uuid>, <data>}

    public static class DbShop implements Db<ShopData, UUID> {

    }

    // [tradereq:<string>] {<idx>, <data>}

    public static class DbTradeReq implements Db<TradeForm, int> {

    }

    public static boolean update(Connection conn, String id, )

    public static boolean update(Connection conn, Object data, UUID author, DatabaseCategory category, Object name) throws SQLException {

        String statement = "UPDATE " + DB_TABLE + " SET data=? WHERE author=? AND category=? AND name=?";

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, data);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name.toString());

        return preparedStatement.execute();
    }

    public static boolean delete(Connection conn, UUID author, DatabaseCategory category, Object name) throws SQLException {

        int i = 0;
        String statement = "DELETE FROM " + DB_TABLE + " WHERE author=? AND category=? AND name=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name.toString());

        return preparedStatement.execute();
    }

    public static boolean insert(Connection conn, UUID author, DatabaseCategory category, Object name, Object data) throws SQLException {

        int i = 0;
        String statement = "INSERT INTO " + DB_TABLE + "(author, category, name, data) values (?, ?, ?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name.toString()); // FIXME: try this

        preparedStatement.setObject(++i, data);

        return preparedStatement.execute();
    }

}
