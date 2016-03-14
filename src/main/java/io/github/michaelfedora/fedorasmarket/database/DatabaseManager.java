package io.github.michaelfedora.fedorasmarket.database;

import io.github.michaelfedora.fedorasmarket.FedorasMarket;
import io.github.michaelfedora.fedorasmarket.enumtype.DatabaseCategory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;
import org.spongepowered.api.util.Tuple;

import java.lang.reflect.Type;
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
            SQL = Sponge.getServiceManager().provide(SqlService.class).get();

        return SQL.getDataSource(jdbcUrl);
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource(DB_ID).getConnection();
    }

    public static final Map<String,Tuple<String,Type>> PARAMS;
    static {
        Map<String, Tuple<String,Type>> p = new LinkedHashMap<>();
        p.put("author", new Tuple<>("uuid", Object.class));
        p.put("category", new Tuple<>("varchar(255)", String.class));
        p.put("name", new Tuple<>("varchar(255)", String.class));
        p.put("data", new Tuple<>("other", Object.class));
        PARAMS = p;
    }

    public static void initialize() throws SQLException {

        StringBuilder constructor = new StringBuilder("(");
        int count = 0;
        for(Map.Entry<String, Tuple<String,Type>> entry : PARAMS.entrySet()) {
            constructor.append(entry.getKey()).append(" ").append(entry.getValue().getFirst());
            if(count < PARAMS.size())
                constructor.append(" ");
        }
        constructor.append(")");

        try(Connection conn = getDataSource(DB_ID).getConnection()) {

            conn.prepareStatement("CREATE TABLE IF NOT EXISTS " + DB_TABLE + constructor).execute();

            ResultSet resultSet = conn.prepareStatement("SELECT * FROM " + DB_TABLE).executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            FedorasMarket.getLogger().info("Table [" + DB_TABLE + "]: ");
            FedorasMarket.getLogger().info(resultSet.toString()); // FIXME: resultset thing

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

        }
    }

    /**
     * Selects a number of values which match the given parameters, and has an additional entry if the user wants to specify
     * something more
     * @param conn the connection
     * @param amt the amount of items to specify (* or number)
     * @param author the author
     * @param category the category of the data
     * @param name the name of the data
     * @param more something more to put onto the statement
     * @return all the values which match the given criteria
     * @throws SQLException
     */
    public static ResultSet selectWithMore(Connection conn, String amt, UUID author, DatabaseCategory category, String name, String more) throws SQLException {

        String statement = "SELECT " + amt + " FROM " + DB_TABLE + " WHERE author=? AND category=? AND name=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name);

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String amt, UUID author, DatabaseCategory category, String more) throws SQLException {

        String statement = "SELECT " + amt + " FROM " + DB_TABLE + " WHERE author=? AND category=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String amt, UUID author, String more) throws SQLException {

        String statement = "SELECT " + amt + " FROM " + DB_TABLE + " WHERE author=? " + more;

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);

        return preparedStatement.executeQuery();
    }

    public static ResultSet selectWithMore(Connection conn, String amt, String more) throws SQLException {

        String statement = "SELECT " + amt + " FROM " + DB_TABLE + " " + more;

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        return preparedStatement.executeQuery();
    }

    /**
     * Select the specified amount of values from the given parameters
     * @param conn the connection
     * @param amt the amount of items to select
     * @param author the author
     * @param category the category of the data
     * @param name the name of the data
     * @return all the values which match the given criteria
     * @throws SQLException
     */
    public static ResultSet select(Connection conn, String amt, UUID author, DatabaseCategory category, String name) throws SQLException {
        return selectWithMore(conn, amt, author, category, name, "");
    }

    public static ResultSet select(Connection conn, String amt, UUID author, DatabaseCategory category) throws SQLException {
        return selectWithMore(conn, amt, author, category, "");
    }


    public static ResultSet select(Connection conn, String amt, UUID author) throws SQLException {
        return selectWithMore(conn, amt, author, "");
    }

    public static ResultSet select(Connection conn, String amt) throws SQLException {
        return selectWithMore(conn, amt, "");
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
    public static ResultSet selectAllWithMore(Connection conn, UUID author, DatabaseCategory category, String name, String more) throws SQLException {
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
     * Selects all the values which match the given parameters
     * @param conn the connection
     * @param author the author
     * @param category the category of the data
     * @param name the name of the data
     * @return all the values which match the given criteria
     * @throws SQLException
     */
    public static ResultSet selectAll(Connection conn, UUID author, DatabaseCategory category, String name) throws SQLException {
        return select(conn, "*", author, category, name);
    }

    public static ResultSet selectAll(Connection conn, UUID author, DatabaseCategory category) throws SQLException {
        return select(conn, "*", author, category);
    }

    public static ResultSet selectAll(Connection conn, UUID author) throws SQLException {
        return select(conn, "*", author);
    }

    public static ResultSet selectAll(Connection conn) throws SQLException {
        return select(conn, "*");
    }

    public static boolean update(Connection conn, Object data, UUID author, DatabaseCategory category, String name) throws SQLException {

        String statement = "UPDATE " + DB_TABLE + " SET data=? WHERE author=? AND category=? AND name=?";

        int i = 0;
        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, data);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name);
        return preparedStatement.execute();
    }

    public static boolean delete(Connection conn, UUID author, DatabaseCategory category, String name) throws SQLException {

        int i = 0;
        String statement = "DELETE FROM " + DB_TABLE + " WHERE author=? AND category=? AND name=?";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name);
        return preparedStatement.execute();
    }

    public static boolean insert(Connection conn, UUID author, String name, DatabaseCategory category, Object data) throws SQLException {

        int i = 0;
        String statement = "INSERT INTO " + DB_TABLE + "(author, category, name, data) values (?, ?, ?, ?)";

        PreparedStatement preparedStatement = conn.prepareStatement(statement);
        preparedStatement.setObject(++i, author);
        preparedStatement.setString(++i, category.toString());
        preparedStatement.setString(++i, name);
        preparedStatement.setObject(++i, data);
        return preparedStatement.execute();
    }

}
