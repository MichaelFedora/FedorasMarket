package io.github.michaelfedora.fedorasmarket.database.table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Michael on 4/20/2016.
 */
public class UserdataTable implements DatabaseTableMap<String, Object> {

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
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS `tradeform:" + id + "`" +
                "(" + Query.NAME.v + " " + Query.NAME.type + ", " +
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
        return null;
    }

    /**
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @return A map, of all of this table-types tables, and users, with their data.
     * @throws SQLException
     */
    @Override
    public Map<String, Map<String, Object>> getAllData(Connection conn) throws SQLException {
        return null;
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
    public Map<String, Object> getAllFor(Connection conn, String id) throws SQLException {

        Map<String, Object> map = new HashMap<>();

        String statement = "SELECT * FROM `tradeform:" + id + "`";

        ResultSet resultSet = conn.prepareStatement(statement).executeQuery();

        while(resultSet.next())
            map.put(resultSet.getString(Query.NAME.v), resultSet.getObject(Query.DATA.v));

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
    public Optional<Object> get(Connection conn, String id, String key) throws SQLException {

        int i = 0;
        String statement = "SELECT " + Query.DATA.v + " FROM `tradeform:" + id + "` WHERE " + Query.NAME.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return Optional.empty();

        return Optional.of(resultSet.getObject(Query.DATA.v));
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
    public boolean update(Connection conn, String id, String key, Object value) throws SQLException {

        int i = 0;
        String statement = "UPDATE `tradeform:" + id + "` SET " + Query.DATA.v + "=? WHERE " + Query.NAME.v + "=?";

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
        String statement = "DELETE FROM `tradeform:" + id + "` WHERE " + Query.NAME.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);

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
    public boolean insert(Connection conn, String id, String key, Object value) throws SQLException {

        int i = 0;
        String statement = "INSERT INTO `tradeform:" + id + "`(" + Query.NAME.v + ", " + Query.DATA.v + ") values (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, key);
        preparedStatement.setObject(++i, value);

        return preparedStatement.execute();
    }
}
