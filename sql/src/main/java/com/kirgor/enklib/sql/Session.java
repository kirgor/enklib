package com.kirgor.enklib.sql;

import com.kirgor.enklib.sql.dialect.Dialect;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

/**
 * Wrapper for the SQL {@link Connection}, which executes queries and gets results as POJOs.
 * <p/>
 * This class is useful for executing any SQL query or even build and execute
 */
public class Session implements Closeable {
    private Connection connection;
    private Dialect dialect;

    /**
     * Creates {@link Session}, which will wrap specified Connection.
     *
     * @param connection {@link Connection} to wrap.
     * @param dialect    {@link Dialect} instance, which will convert database field names.
     */
    public Session(Connection connection, Dialect dialect) {
        this.connection = connection;
        this.dialect = dialect;
    }

    /**
     * Creates {@link Session}, which will wrap {@link Connection} retrieved from {@link DataSource}.
     *
     * @param dataSource {@link DataSource}, from which Connection will be retrieved.
     * @param dialect    {@link Dialect} instance, which will convert database field names.
     * @throws SQLException In general SQL error case.
     */
    public Session(DataSource dataSource, Dialect dialect) throws SQLException {
        this(dataSource.getConnection(), dialect);
    }

    /**
     * Creates {@link Session}, which will create {@link Connection} from specified parameters and then wrap it.
     *
     * @param driver   Class name for JDBC driver.
     * @param url      JDBC URL of the data connection.
     * @param user     Connection username.
     * @param password Connection password.
     * @param dialect  {@link Dialect} instance, which will convert database field names.
     * @throws SQLException           In general SQL error case.
     * @throws ClassNotFoundException If driver can't be found.
     */
    public Session(String driver, String url, String user, String password, Dialect dialect) throws SQLException, ClassNotFoundException {
        this(createConnection(driver, url, user, password), dialect);
    }

    /**
     * Gets {@link Dialect} instance, applied to this context.
     */
    public Dialect getDialect() {
        return dialect;
    }

    /**
     * Indicates whether underlying connection is in auto-commit mode.
     *
     * @return True if auto-commit, otherwise false.
     * @throws SQLException
     */
    public boolean isAutoCommit() throws SQLException {
        return connection.getAutoCommit();
    }

    /**
     * Sets underlying connection auto-commit mode.
     *
     * @param autoCommit True if auto-commit, otherwise false.
     * @throws SQLException
     */
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connection.setAutoCommit(autoCommit);
    }

    /**
     * Executes SQL query, which returns nothing.
     *
     * @param query  SQL query string.
     * @param params Parameters, which will be placed instead of '?' signs.
     * @throws SQLException In general SQL error case.
     */
    public void execute(String query, Object... params) throws SQLException {
        createPreparedStatement(query, 0, params).execute();
    }

    /**
     * Executes SQL query, which returns list of entities.
     *
     * @param <T>         Entity type.
     * @param query       SQL query string.
     * @param entityClass Entity class.
     * @param params      Parameters, which will be placed instead of '?' signs.
     * @return List of result entities.
     * @throws SQLException           In general SQL error case.
     * @throws NoSuchFieldException   If result set has field which entity class doesn't.
     * @throws InstantiationException If entity class hasn't default constructor.
     * @throws IllegalAccessException If entity class is not accessible.
     */
    public <T> List<T> getList(String query, Class<T> entityClass, Object... params)
            throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        Cursor<T> cursor = getCursor(query, entityClass, params);
        return cursor.fetchList();
    }

    /**
     * Executes SQL query, which returns single entity.
     *
     * @param <T>         Entity type.
     * @param query       SQL query string.
     * @param entityClass Entity class.
     * @param params      Parameters, which will be placed instead of '?' signs.
     * @return Single result entity.
     * @throws SQLException           In general SQL error case.
     * @throws NoSuchFieldException   If result set has field which entity class doesn't.
     * @throws InstantiationException If entity class hasn't default constructor.
     * @throws IllegalAccessException If entity class is not accessible.
     */
    public <T> T getSingle(String query, Class<T> entityClass, Object... params)
            throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        Cursor<T> cursor = getCursor(query, entityClass, params);
        return cursor.fetchSingle();
    }

    /**
     * Executes SQL query, which returns single entity or null if result is not there.
     *
     * @param <T>         Entity type.
     * @param query       SQL query string.
     * @param entityClass Entity class.
     * @param params      Parameters, which will be placed instead of '?' signs.
     * @return Single result entity or null.
     * @throws SQLException           In general SQL error case.
     * @throws NoSuchFieldException   If result set has field which entity class doesn't.
     * @throws InstantiationException If entity class hasn't default constructor.
     * @throws IllegalAccessException If entity class is not accessible.
     */
    public <T> T getSingleOrNull(String query, Class<T> entityClass, Object... params)
            throws SQLException, NoSuchFieldException, InstantiationException, IllegalAccessException {
        Cursor<T> cursor = getCursor(query, entityClass, params);
        return cursor.fetchSingleOrNull();
    }

    /**
     * Creates cursor, which can be used for fetching data in partial mode.
     *
     * @param <T>         Entity type.
     * @param query       SQL query string.
     * @param entityClass Entity class.
     * @param fetchSize   Fetch size, which defines how many records will buffered in cursor.
     * @param params      Parameters, which will be placed instead of '?' signs.
     * @return Single result entity or null.
     * @throws SQLException         In general SQL error case.
     * @throws NoSuchFieldException If result set has field which entity class doesn't.
     */
    public <T> Cursor<T> getCursor(String query, Class<T> entityClass, int fetchSize, Object... params) throws NoSuchFieldException, SQLException {
        PreparedStatement stat = createPreparedStatement(query, fetchSize, params);
        ResultSet resultSet = stat.executeQuery();
        return new Cursor<T>(resultSet, entityClass, dialect);
    }

    /**
     * Creates cursor, which can be used for fetching data in partial mode.
     *
     * @param <T>         Entity type.
     * @param query       SQL query string.
     * @param entityClass Entity class.
     * @param params      Parameters, which will be placed instead of '?' signs.
     * @return Single result entity or null.
     * @throws SQLException         In general SQL error case.
     * @throws NoSuchFieldException If result set has field which entity class doesn't.
     */
    public <T> Cursor<T> getCursor(String query, Class<T> entityClass, Object... params) throws NoSuchFieldException, SQLException {
        return getCursor(query, entityClass, 20, params);
    }

    /**
     * Commits changes in the current transaction
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Closes SQL connection.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
        try {
            connection.close();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Creates {@link Connection} instance from supplied driver, JDBC URL and user credentials.
     */
    private static Connection createConnection(String driver, String url, String user, String password)
            throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Creates {@link PreparedStatement} instance from supplied query string and parameters.
     */
    private PreparedStatement createPreparedStatement(String query, int fetchSize, Object... params) throws SQLException {
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setFetchSize(fetchSize);
        for (int i = 0; i < params.length; i++) {
            stat.setObject(i + 1, params[i]);
        }
        return stat;
    }
}
