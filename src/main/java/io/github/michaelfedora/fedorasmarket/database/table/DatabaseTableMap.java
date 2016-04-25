package io.github.michaelfedora.fedorasmarket.database.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by Michael on 4/20/2016.
 */
public interface DatabaseTableMap<K, V> extends DatabaseTable<K, V, Map<K, V>> {

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
}
