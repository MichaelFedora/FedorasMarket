package io.github.michaelfedora.fedorasmarket.database.table;

import io.github.michaelfedora.fedorasmarket.database.BadDataException;
import io.github.michaelfedora.fedorasmarket.trade.SerializedTradeForm;
import io.github.michaelfedora.fedorasmarket.trade.TradeForm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Michael on 4/20/2016.
 */
public class TradeReqTable implements DatabaseTableSet<TradeForm> {

    public enum Query {
        IDX("idx", "int identity"),
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
        conn.prepareStatement("CREATE TABLE IF NOT EXISTS `tradereq:" + id + "`" +
                "(" + Query.IDX.v + " " + Query.IDX.type + ", " +
                Query.DATA.v + Query.DATA.type + ")").execute();
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
    public Map<Integer, TradeForm> getAllFor(Connection conn, String id) throws SQLException {

        Map<Integer, TradeForm> map = new HashMap<>();

        String statement = "SELECT * FROM `tradereq:" + id + "`";

        ResultSet resultSet = conn.prepareStatement(statement).executeQuery();

        Object data;
        while(resultSet.next()) {

            data = resultSet.getObject(Query.DATA.v);

            if(!(data instanceof SerializedTradeForm))
                continue;

            try {
                map.put(resultSet.getInt(Query.IDX.v), ((SerializedTradeForm) data).deserialize());
            } catch (BadDataException e) {
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
    public Optional<TradeForm> get(Connection conn, String id, Integer key) throws SQLException {

        int i = 0;
        String statement = "SELECT " + Query.DATA.v + " FROM `tradereq:" + id + "` WHERE " + Query.IDX.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setInt(++i, key);

        ResultSet resultSet = preparedStatement.executeQuery();
        if(!resultSet.next())
            return Optional.empty();

        Object data = resultSet.getObject(Query.DATA.v);
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
    public boolean update(Connection conn, String id, Integer key, TradeForm value) throws SQLException {

        int i = 0;
        String statement = "UPDATE `tradereq:" + id + "` SET " + Query.DATA.v + "=? WHERE " + Query.IDX.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, value.serialize());
        preparedStatement.setInt(++i, key);

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
    public boolean delete(Connection conn, String id, Integer key) throws SQLException {

        int i = 0;
        String statement = "DELETE FROM `tradereq:" + id + "` WHERE " + Query.IDX.v + "=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setInt(++i, key);

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
    public boolean insert(Connection conn, String id, Integer key, TradeForm value) throws SQLException {

        int i = 0;
        String statement = "INSERT INTO `tradereq:" + id + "`(" + Query.IDX.v + ", " + Query.DATA.v + ") values (?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setInt(++i, key);
        preparedStatement.setObject(++i, value.serialize());

        return preparedStatement.execute();
    }
}
