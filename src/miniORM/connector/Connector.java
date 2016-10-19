package miniORM.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {

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
        + port + "/" + dbName,connectionProp);
    }

    public static Connection getConnection() {
        return connection;
    }
}
