package io.github.michaelfedora.fedorasmarket.database.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Michael on 4/20/2016.
 */
public interface DatabaseTable<V, K> {

    /**
     * Makes the table if it doesn't exist.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id the id of the table/owner
     * @throws SQLException
     */
    void makeIfNotExist(Connection conn, String id) throws SQLException;

    /**
     * Get's all the "users" of the table (that is, using this prefix).
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @return the set of the users of this table-type
     * @throws SQLException
     */
    Set<String> getUsers(Connection conn) throws SQLException;

    /**
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @return A map, of all of this table-types tables, and users, with their data.
     * @throws SQLException
     */
    Map<String, Map<K,V>> getAllData(Connection conn) throws SQLException;

    /**
     * Gets all the data for a particular id.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id the id of the table/owner
     * @return the list of values
     * @throws SQLException
     */
    Map<K, V> getAllFor(Connection conn, String id) throws SQLException;

    /**
     * Gets a specific data entry for a particular id and key.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id the id of the table/owner
     * @param key the key of the value
     * @return the value
     * @throws SQLException
     */
    Optional<V> get(Connection conn, String id, K key) throws SQLException;

    /**
     * Updates a particular entry with a new value.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id the id of the table/owner
     * @param key the key of the entry to update
     * @param value the new value of the specified key
     * @return whether or not it succeeded
     * @throws SQLException
     */
    boolean update(Connection conn, String id, K key, V value) throws SQLException;

    /**
     * Deletes a particular entry.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id the id of the table/owner
     * @param key the key of the entry to delete
     * @return whether or not it succeeded
     * @throws SQLException
     */
    boolean delete(Connection conn, String id, K key) throws SQLException;

    /**
     * Inserts a new entry.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id the id of the table/owner
     * @param key the key of the entry to insert into
     * @param value the value of the entry to insert
     * @return whether or not it succeeded
     * @throws SQLException
     */
    boolean insert(Connection conn, String id, K key, V value) throws SQLException;

    /**
     * Cleans a table of "bad data"; i.e. if you can't {@link #get} the data, then it should be removed from the table.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @throws SQLException
     */
    void clean(Connection conn) throws SQLException;
}
