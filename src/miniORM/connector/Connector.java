package miniORM.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector implements AutoCloseable {

    private static final String CREATE_IF_NO_EXIST = "?createDatabaseIfNotExist=true";
    private static final String MYSQL_CONFIG = "&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private static Connection connection = null;

    /**
     * Generate a connection that we would use to connect to the database
     *
     * @param driver - database driver
     * @param username - database username
     * @param password - database password
     * @param host - database IP Address
     * @param port - database connection port
     * @param dbName - database name
     * @throws SQLException
     */
    public static void initConnection(String driver,String username,
                                      String password, String host,
                                      String port, String dbName) throws SQLException {
        Properties connectionProp = new Properties();
        connectionProp.put("user", username);
        connectionProp.put("password", password);
        connection = DriverManager.getConnection("jdbc:" + driver + "://" + host + ":"
        + port + "/" + dbName + CREATE_IF_NO_EXIST + MYSQL_CONFIG,connectionProp);
    }

    public static Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
