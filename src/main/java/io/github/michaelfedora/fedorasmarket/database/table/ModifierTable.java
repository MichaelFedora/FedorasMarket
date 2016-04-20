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

    /**
     * Makes the table if it doesn't exist.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id   the id of the table/owner
     * @throws SQLException
     */
    @Override
    public void makeIfNotExist(Connection conn, String id) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS `modifier:" + id + "`(name varchar(255), data other)").execute();
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
    public List<ShopModifier> getAll(Connection conn, String id) throws SQLException {

        List<ShopModifier> list = new ArrayList<>();

        String statement = "SELECT data FROM `modifier:" + id + "`";

        ResultSet resultSet = conn.prepareStatement(statement).executeQuery();

        Object data;
        while(resultSet.next()) {

            data = resultSet.getObject("data");

            if(!(data instanceof ShopModifier))
                continue;

            list.add((ShopModifier) data);
        }

        return list;
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
        String statement = "SELECT data FROM `modifier:" + id + "` WHERE key=?";

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return Optional.empty();

        return Optional.ofNullable(resultSet.getObject("data")).map(a -> (ShopModifier) a);
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
        String statement = "UPDATE `modifier:" + id + "` SET data=? WHERE name=?";

        int i = 0;
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
        String statement = "DELETE FROM `modifier:" + id + "` WHERE name=?";

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
        String statement = "INSERT INTO `modifier:" + id + "`(name, data) values (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);
        preparedStatement.setObject(++i, value);

        return preparedStatement.execute();
    }
}
