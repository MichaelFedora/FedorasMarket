package io.github.michaelfedora.fedorasmarket.database.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by Michael on 4/25/2016.
 */
public interface DatabaseTableSet<V> extends DatabaseTable<Integer, V, Set<V>> {

    /**
     * Inserts a new entry.
     *
     * @param conn the database connection (to be used inside a try-catch(-finally))
     * @param id the id of the table/owner
     * @param value the value of the entry to insert
     * @return whether or not it succeeded
     * @throws SQLException
     */
    boolean add(Connection conn, String id, V value) throws SQLException;
}
