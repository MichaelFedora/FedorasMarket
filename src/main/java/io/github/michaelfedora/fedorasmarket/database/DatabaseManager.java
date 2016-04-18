package io.github.michaelfedora.fedorasmarket.database;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import java.sql.*;
import java.util.*;

/**
 * Created by Michael on 2/27/2016.
 */
public final class DatabaseManager {

    public static final String DB_ID = "jdbc:h2:./mods/FedorasData/market.db";
    public static final String DB_TABLE = "data";

    private DatabaseManager() { }

    private static SqlService SQL;
    public static javax.sql.DataSource getDataSource(String jdbcUrl) throws SQLException {
        if(SQL == null)
            SQL = Sponge.getServiceManager().provide(SqlService.class).orElseThrow(() -> new SQLException("Could not 'get' SqlService!"));

        return SQL.getDataSource(jdbcUrl);
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource(DB_ID).getConnection();
    }

    public static void initialize() {

        try(Connection conn = getConnection()) {

            conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + DB_TABLE + DatabaseQuery.makeConstructor()).execute();

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + DB_TABLE).executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            FedorasMarket.getLogger().info("Table [" + DB_TABLE + "]: ");
            FedorasMarket.getLogger().info(resultSet.toString());

            StringBuilder sb = new StringBuilder();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {

                sb.append(metaData.getColumnName(i));
                if (i < metaData.getColumnCount())
                    sb.append(" | ");
            }
            FedorasMarket.getLogger().info(sb.toString());

            while(resultSet.next()) {

                sb.setLength(0);
                for(int i = 1; i <= metaData.getColumnCount(); i++) {

                    sb.append(resultSet.getObject(i));
                    if(i < metaData.getColumnCount())
                        sb.append(" | ");
                }
                FedorasMarket.getLogger().info(sb.toString());
            }

        } catch(SQLException e) {
            FedorasMarket.getLogger().error("SQL Error", e);
        }
    }

    /*
    TODO: fix database layout

    Database layout:
    - Tablename: "type:name", contains it's own layout; e.x.
     - [tradeform:00000-0000-00-0001] {"name", <data>}
     - [modifier:<uuid>] {"name", <data>}
     - [depot:server] {<idx>, <item_data>}
     - [shop:1234-5678-91-01234] {<uuid>, <data>}
     - [tradereq:4444-4656-87-3535] {<idx>, <data>}
     */

    /**
     * Selects a number of values which match the given parameters, and has an additional entry if the user wants to specify
     * something more
     * @param conn the connection
     * @param columns the columns to select
     * @param author the author
     * @param category the category of the data
     * @param name the name of the data
     * @param more something more to put onto the statement
     * @return all the values which match the given criteria
     * @throws SQLException
     */
    public static ResultSet selectWithMore(Connection conn, String columns, UUID author, DatabaseCategory category, Object name, String more) throws SQLException {

        String statement = "SELECT " + columns + " FROM " + DB_TABLE + " WHERE author=? AND category=? AND name=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name.toString());

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String columns, UUID author, DatabaseCategory category, String more) throws SQLException {

        String statement = "SELECT " + columns + " FROM " + DB_TABLE + " WHERE author=? AND category=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String columns, UUID author, String more) throws SQLException {

        String statement = "SELECT " + columns + " FROM " + DB_TABLE + " WHERE author=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String columns, DatabaseCategory category, String more) throws SQLException {

        String statement = "SELECT " + columns + " FROM " + DB_TABLE + " WHERE category=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setString(++i, category.toString());

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String columns, String more) throws SQLException {

        String statement = "SELECT " + columns + " FROM " + DB_TABLE + " " + more;

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        return preparedStatement.executeQuery();
    }

    /**
     * Selects all the values which match the given parameters, and has an additional entry if the user wants to specify
     * something more
     * @param conn the connection
     * @param author the author
     * @param category the category of the data
     * @param name the name of the data
     * @param more something more to put onto the statement
     * @return all the values which match the given criteria
     * @throws SQLException
     */
    public static ResultSet selectAllWithMore(Connection conn, UUID author, DatabaseCategory category, Object name, String more) throws SQLException {
        return selectWithMore(conn, "*", author, category, name, more);
    }

    public static ResultSet selectAllWithMore(Connection conn, UUID author, DatabaseCategory category, String more) throws SQLException {
        return selectWithMore(conn, "*", author, category, more);
    }

    public static ResultSet selectAllWithMore(Connection conn, UUID author, String more) throws SQLException {
        return selectWithMore(conn, "*", author, more);
    }

    public static ResultSet selectAllWithMore(Connection conn, String more) throws SQLException {
        return selectWithMore(conn, "*", more);
    }

    /**
     * Select the specified amount of values from the given parameters
     * @param conn the connection
     * @param columns the columns to select
     * @param author the author
     * @param category the category of the data
     * @param name the name of the data
     * @return all the values which match the given criteria
     * @throws SQLException
     */
    public static ResultSet select(Connection conn, String columns, UUID author, DatabaseCategory category, Object name) throws SQLException {
        return selectWithMore(conn, columns, author, category, name, "");
    }

    public static ResultSet select(Connection conn, String columns, UUID author, DatabaseCategory category) throws SQLException {
        return selectWithMore(conn, columns, author, category, "");
    }


    public static ResultSet select(Connection conn, String columns, UUID author) throws SQLException {
        return selectWithMore(conn, columns, author, "");
    }

    public static ResultSet select(Connection conn, String columns, DatabaseCategory category) throws SQLException {
        return selectWithMore(conn, columns, category, "");
    }

    public static ResultSet select(Connection conn, String columns) throws SQLException {
        return selectWithMore(conn, columns, "");
    }

    /**
     * Selects all the values which match the given parameters
     * @param conn the connection
     * @param author the author
     * @param category the category of the data
     * @param name the name of the data
     * @return all the values which match the given criteria
     * @throws SQLException
     */
    public static ResultSet selectAll(Connection conn, UUID author, DatabaseCategory category, Object name) throws SQLException {
        return selectWithMore(conn, "*", author, category, name, "");
    }

    public static ResultSet selectAll(Connection conn, UUID author, DatabaseCategory category) throws SQLException {
        return selectWithMore(conn, "*", author, category, "");
    }

    public static ResultSet selectAll(Connection conn, UUID author) throws SQLException {
        return selectWithMore(conn, "*", author, "");
    }

    public static ResultSet selectAll(Connection conn, DatabaseCategory category) throws SQLException {
        return selectWithMore(conn, "*", category, "");
    }

    public static ResultSet selectAll(Connection conn) throws SQLException {
        return selectWithMore(conn, "*", "");
    }

    public static boolean update(Connection conn, Object data, UUID author, DatabaseCategory category, Object name) throws SQLException {

        String statement = "UPDATE " + DB_TABLE + " SET data=? WHERE author=? AND category=? AND name=?";

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, data);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name.toString());

        return preparedStatement.execute();
    }

    public static boolean delete(Connection conn, UUID author, DatabaseCategory category, Object name) throws SQLException {

        int i = 0;
        String statement = "DELETE FROM " + DB_TABLE + " WHERE author=? AND category=? AND name=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name.toString());

        return preparedStatement.execute();
    }

    public static boolean insert(Connection conn, UUID author, DatabaseCategory category, Object name, Object data) throws SQLException {

        int i = 0;
        String statement = "INSERT INTO " + DB_TABLE + "(author, category, name, data) values (?, ?, ?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name.toString()); // FIXME: try this

        preparedStatement.setObject(++i, data);

        return preparedStatement.execute();
    }

}
