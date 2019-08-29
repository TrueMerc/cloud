package ru.ryabtsev.cloud.server.jdbc;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import java.sql.Connection;
import java.sql.DriverManager;


/**
 * JDBC connections management class.
 */
public class JdbcConnectionManager {
    private Configuration configuration;
    private Connection connection;

    public JdbcConnectionManager() {
        this( new JdbcDefaultConfiguration() );
    }

    public JdbcConnectionManager( Configuration configuration ) {
        this.configuration = configuration;
    }

    public Statement createStatement() throws Exception {
        return (Statement) connection.createStatement();
    }

    public PreparedStatement createPreparedStatement(final String sqlQuery) throws Exception {
        return (PreparedStatement) connection.prepareStatement(sqlQuery);
    }

    public void connect() throws Exception {
        Class.forName( configuration.getDriverName() );
        connection = DriverManager.getConnection( configuration.toString(), configuration.getUsername(), configuration.getPassword() );
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void disconnect() throws Exception {
        connection.close();
    }
}
