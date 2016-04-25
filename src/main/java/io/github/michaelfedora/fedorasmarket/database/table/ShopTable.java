package io.github.michaelfedora.fedorasmarket.database.table;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.shop.ShopData;
import io.github.michaelfedora.fedorasmarket.shop.SerializedShopData;

import java.sql.*;
import java.util.*;

/**
 * Created by Michael on 4/20/2016.
 */
public class ShopTable implements DatabaseTableMap<UUID, ShopData> {

    public enum Query {
        ID("id", "uuid"),
        DATA("data", "other");

        public final String v;
        public final String type;

        Query(String name, String type) {
            this.v = name;
            this.type = type;
        }
    }

    /**
     * Makes the table if it doesn't exist.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id   the id of the table/owner
     * @throws SQLException
     */
    @Override
    public void makeIfNotExist(Connection conn, String id) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS `shop:" + id + "`" +
                "(" + Query.ID.v + " " + Query.ID.type + ", " +
                Query.DATA.v + Query.DATA.type + ")").execute();
    }

    /**
     * Get's all the "users" of the table (that is, using this prefix).
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @return the set of the users of this table-type
     * @throws SQLException
     */
    @Override
    public Set<String> getUsers(Connection conn) throws SQLException {

        Set<String> users = new HashSet<>();

        ResultSet resultSet = conn.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.TABLES").executeQuery();
        while(resultSet.next()) {
            String name = resultSet.getString("NAME");
            if (name.startsWith("shop:"))
                users.add(name.substring(5));
        }

        return users;
    }

    /**
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @return A map, of all of this table-types tables, and users, with their data.
     * @throws SQLException
     */
    @Override
    public Map<String, Map<UUID, ShopData>> getAllData(Connection conn) throws SQLException {

        Map<String, Map<UUID, ShopData>> map = new HashMap<>();

        for(String user : getUsers(conn))
            map.put(user, getAllFor(conn, user));

        return map;
    }

    /**
     * Gets all the data for a particular id
     *
     * @param conn the database connection (to be used inside a try-catch(-finally)
     * @param id   the id of the table/owner
     * @return the list of values
     * @throws SQLException
     */
    @Override
    public Map<UUID, ShopData> getAllFor(Connection conn, String id) throws SQLException {

        Map<UUID, ShopData> map = new HashMap<>();

        String statement = "SELECT * FROM `shop:" + id + "`";

        ResultSet resultSet = conn.prepareStatement(statement).executeQuery();

        Object data;
        while(resultSet.next()) {

            data = resultSet.getObject(Query.DATA.v);

            if(!(data instanceof SerializedShopData))
                continue;

            try {
                map.put((UUID) resultSet.getObject(Query.ID.v), ((SerializedShopData) data).deserialize());
            } catch (Exception e) {
                // do nothing
            }
        }

        return map;
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
    public Optional<ShopData> get(Connection conn, String id, UUID key) throws SQLException {

        int i = 0;
        String statement = "SELECT " + Query.DATA.v + " FROM `shop:" + id + "` WHERE " + Query.ID.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return Optional.empty();

        Object data = resultSet.getObject(Query.DATA.v);
        if(!(data instanceof SerializedShopData))
            return Optional.empty();

        try {
            return Optional.of(((SerializedShopData) data).deserialize());
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
    public boolean update(Connection conn, String id, UUID key, ShopData value) throws SQLException {

        int i = 0;
        String statement = "UPDATE `shop:" + id + "` SET " + Query.DATA.v + "=? WHERE " + Query.ID.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, value.serialize());
        preparedStatement.setObject(++i, key);

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
    public boolean delete(Connection conn, String id, UUID key) throws SQLException {

        int i = 0;
        String statement = "DELETE FROM `shop:" + id + "` WHERE " + Query.ID.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, key);

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
    public boolean insert(Connection conn, String id, UUID key, ShopData value) throws SQLException {

        int i = 0;
        String statement = "INSERT INTO `shop:" + id + "`(" + Query.ID.v + ", " + Query.DATA.v + ") values (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, key);
        preparedStatement.setObject(++i, value.serialize());

        return preparedStatement.execute();
    }

    /**
     * Cleans a table of "bad data"; i.e. if you can't {@link #get} the data, then it should be removed from the table.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @throws SQLException
     */
    @Override
    public void clean(Connection conn) throws SQLException {

        for(String user : getUsers(conn)) {

            Set<UUID> toRemove = new HashSet<>();

            ResultSet resultSet_sub = conn.prepareStatement("SELECT * FROM `shop:" + user + "`").executeQuery();

            Object data;
            while(resultSet_sub.next()) {

                data = resultSet_sub.getObject(Query.DATA.v);

                if(!(data instanceof SerializedShopData)) {
                    toRemove.add((UUID) resultSet_sub.getObject(Query.ID.v));
                    continue;
                }

                try {
                    ((SerializedShopData) data).deserialize();
                } catch (Exception e) {
                    toRemove.add((UUID) resultSet_sub.getObject(Query.ID.v));
                }
            }

            for(UUID uuid : toRemove)
                delete(conn, user, uuid);
        }
    }
}
