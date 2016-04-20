package io.github.michaelfedora.fedorasmarket.database.table;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.serializeddata.SerializedItemStack;
import org.spongepowered.api.item.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 4/20/2016.
 */
public class DepotTable implements DatabaseTable<ItemStack, Integer> {

    /**
     * Makes the table if it doesn't exist.
     *
     * @param id the id of the table/owner
     * @throws SQLException
     */
    @Override
    public void makeIfNotExist(Connection conn, String id) throws SQLException {
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS `depot:" + id + "`(idx integer), data other)").execute();
    }

    /**
     * Gets all the data for a particular id.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally)
     * @param id a  the id of the table/owner
     * @return the list of values
     * @throws SQLException
     */
    @Override
    public List<ItemStack> getAll(Connection conn, String id) throws SQLException {

        makeIfNotExist(conn, id);
        
        List<ItemStack> list = new ArrayList<>();

        String statement = "SELECT data FROM `depot:" + id + "`";

        ResultSet resultSet = conn.prepareStatement(statement).executeQuery();

        Object data;
        while(resultSet.next()) {

            data = resultSet.getObject("data");

            if(!(data instanceof SerializedItemStack))
                continue;

            try {
                list.add(((SerializedItemStack) data).deserialize());
            } catch (BadDataException e) {
                // do nothing
            }
        }

        return list;
    }

    /**
     * Gets a specific data entry for a particular id and key.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally)
     * @param id   the id of the table/owner
     * @param key  the key of the value
     * @return the value
     * @throws SQLException
     */
    @Override
    public Optional<ItemStack> get(Connection conn, String id, Integer key) throws SQLException {

        makeIfNotExist(conn, id);
        
        String statement = "SELECT data FROM `depot:" + id + "` WHERE key=?";

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setInt(++i, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return Optional.empty();

        Object data = resultSet.getObject("data");
        if(!(data instanceof SerializedItemStack))
            return Optional.empty();

        try {
            return Optional.of(((SerializedItemStack) data).deserialize());
        } catch(BadDataException e) {
            return Optional.empty();
        }
    }

    /**
     * Updates a particular entry with a new value.
     *
     * @param conn  the database connection (to be used inside a try-catch(-finally)
     * @param id    the id of the table/owner
     * @param key   the key of the entry to update
     * @param value the new value of the specified key
     * @return whether or not it succeeded
     * @throws SQLException
     */
    @Override
    public boolean update(Connection conn, String id, Integer key, ItemStack value) throws SQLException {
        
        String statement = "UPDATE `depot:" + id + "` SET data=? WHERE name=?";

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, value);
        preparedStatement.setInt(++i, key);

        return preparedStatement.execute();
    }

    /**
     * Deletes a particular entry.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally)
     * @param id   the id of the table/owner
     * @param key  the key of the entry to delete
     * @return whether or not it succeeded
     * @throws SQLException
     */
    @Override
    public boolean delete(Connection conn, String id, Integer key) throws SQLException {
        
        int i = 0;
        String statement = "DELETE FROM `depot:" + id + "` WHERE name=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setInt(++i, key);

        return preparedStatement.execute();
    }

    /**
     * Inserts a new entry.
     *
     * @param conn  the database connection (to be used inside a try-catch(-finally)
     * @param id    the id of the table/owner
     * @param key   the key of the entry to insert into
     * @param value the value of the entry to insert
     * @return whether or not it succeeded
     * @throws SQLException
     */
    @Override
    public boolean insert(Connection conn, String id, Integer key, ItemStack value) throws SQLException {

        makeIfNotExist(conn, id);
        
        int i = 0;
        String statement = "INSERT INTO `depot:" + id + "`(name, data) values (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setInt(++i, key);
        preparedStatement.setObject(++i, value);

        return preparedStatement.execute();
    }
}
