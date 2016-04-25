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
public class DepotTable implements DatabaseTableSet<ItemStack> {

    public static final String TABLE_ID =  "tblDepot";

    public enum Query {
        USER("user", "uuid"),
        INDEX("index", "int primary key"),
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
     * @param id the id of the table/owner
     * @throws SQLException
     */
    @Override
    public void makeIfNotExist(Connection conn, String id) throws SQLException {

        conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE_ID + " (" +
                Query.USER.v + " " + Query.USER.type + ", " +
                Query.INDEX.v + " " + Query.INDEX.type + ", " +
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

        Set<String> set = new HashSet<>();

        ResultSet resultSet = conn.prepareStatement("SELECT " + Query.USER.v + " FROM " + TABLE_ID).executeQuery();

        while(resultSet.next())
            set.add(resultSet.getString(Query.USER.v));

        return set;
    }

    /**
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @return A map, of all of this table-types tables, and users, with their data.
     * @throws SQLException
     */
    @Override
    public Map<String, Set<ItemStack>> getAllData(Connection conn) throws SQLException {
        return null;
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
    public Set<ItemStack> getAllFor(Connection conn, String id) throws SQLException {
        
        Set<ItemStack> set = new TreeSet<>();

        int i = 0;
        String statement = "SELECT * FROM " + TABLE_ID + " WHERE " + Query.USER.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, id);

        ResultSet resultSet = preparedStatement.executeQuery();

        Object data;
        while(resultSet.next()) {

            data = resultSet.getObject(Query.DATA.v);

            if(!(data instanceof SerializedItemStack))
                continue;

            try {
                set.add(((SerializedItemStack) data).deserialize());
            } catch (BadDataException e) {
                // do nothing
            }
        }

        return set;
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

        int i = 0;
        String statement = "SELECT " + Query.DATA.v + " FROM " + TABLE_ID + " WHERE " + Query.USER.v + " =? AND " + Query.INDEX.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, id);
        preparedStatement.setInt(++i, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return Optional.empty();

        Object data = resultSet.getObject(Query.DATA.v);
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

        int i = 0;
        String statement = "UPDATE " + TABLE_ID + " SET " + Query.DATA.v + "=? WHERE " + Query.USER.v + " =? AND " + Query.INDEX.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, new SerializedItemStack(value));
        preparedStatement.setString(++i, id);
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
        String statement = "DELETE FROM " + TABLE_ID + " WHERE " + Query.USER.v + "=? AND " + Query.INDEX.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, id);
        preparedStatement.setInt(++i, key);

        return preparedStatement.execute();
    }

    /**
     * Inserts a new entry.
     *
     * @param conn  the database connection (to be used inside a try-catch(-finally)
     * @param id    the id of the table/owner
     * @param value the value of the entry to insert
     * @return whether or not it succeeded
     * @throws SQLException
     */
    @Override
    public boolean add(Connection conn, String id, ItemStack value) throws SQLException {
        
        int i = 0;
        String statement = "INSERT INTO " + TABLE_ID + "(" + Query.USER.v + ", " + Query.DATA.v + ") values (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, id);
        preparedStatement.setObject(++i, new SerializedItemStack(value));

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
        // for every item in the depot, see if it can be deserialized, otherwise delete it
    }
}
