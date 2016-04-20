package io.github.michaelfedora.fedorasmarket.database.table;

import io.github.michaelfedora.fedorasmarket.shop.modifier.ShopModifier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 4/20/2016.
 */
public class ModifierTable implements DatabaseTable<ShopModifier, String> {

    public enum Query {
        NAME("name", "varchar(255)"),
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
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS `modifier:" + id + "`" +
                "(" + Query.NAME.v + " " + Query.NAME.type + ", " +
                Query.DATA.v + Query.DATA.type + ")").execute();
    }

    /**
     * Gets all the data for a particular id.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id   the id of the table/owner
     * @return the list of values
     * @throws SQLException
     */
    @Override
    public Map<String, ShopModifier> getAll(Connection conn, String id) throws SQLException {

        Map<String, ShopModifier> map = new HashMap<>();

        String statement = "SELECT * FROM `modifier:" + id + "`";

        ResultSet resultSet = conn.prepareStatement(statement).executeQuery();

        Object data;
        while(resultSet.next()) {

            data = resultSet.getObject(Query.DATA.v);

            if(!(data instanceof ShopModifier))
                continue;

            map.put(resultSet.getString(Query.NAME.v), (ShopModifier) data);
        }

        return map;
    }

    /**
     * Gets a specific data entry for a particular id and key.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id   the id of the table/owner
     * @param key  the key of the value
     * @return the value
     * @throws SQLException
     */
    @Override
    public Optional<ShopModifier> get(Connection conn, String id, String key) throws SQLException {

        int i = 0;
        String statement = "SELECT " + Query.DATA.v + " FROM `modifier:" + id + "` WHERE " + Query.NAME.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return Optional.empty();

        return Optional.ofNullable(resultSet.getObject(Query.DATA.v)).map(a -> (ShopModifier) a);
    }

    /**
     * Updates a particular entry with a new value.
     *
     * @param conn  the database connection (to be used inside a try-catch(-finally))
     * @param id    the id of the table/owner
     * @param key   the key of the entry to update
     * @param value the new value of the specified key
     * @return whether or not it succeeded
     * @throws SQLException
     */
    @Override
    public boolean update(Connection conn, String id, String key, ShopModifier value) throws SQLException {

        int i = 0;
        String statement = "UPDATE `modifier:" + id + "` SET " + Query.DATA.v + "=? WHERE " + Query.NAME.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, value);
        preparedStatement.setString(++i, key);

        return preparedStatement.execute();
    }

    /**
     * Deletes a particular entry.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id   the id of the table/owner
     * @param key  the key of the entry to delete
     * @return whether or not it succeeded
     * @throws SQLException
     */
    @Override
    public boolean delete(Connection conn, String id, String key) throws SQLException {

        int i = 0;
        String statement = "DELETE FROM `modifier:" + id + "` WHERE " + Query.NAME.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);

        return preparedStatement.execute();
    }

    /**
     * Inserts a new entry.
     *
     * @param conn  the database connection (to be used inside a try-catch(-finally))
     * @param id    the id of the table/owner
     * @param key   the key of the entry to insert into
     * @param value the value of the entry to insert
     * @return whether or not it succeeded
     * @throws SQLException
     */
    @Override
    public boolean insert(Connection conn, String id, String key, ShopModifier value) throws SQLException {

        int i = 0;
        String statement = "INSERT INTO `modifier:" + id + "`(" + Query.NAME.v + ", " + Query.DATA.v + ") values (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);
        preparedStatement.setObject(++i, value);

        return preparedStatement.execute();
    }
}
