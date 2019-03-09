package ru.ryabtsev.cloud.server.jdbc;


/**
 * JDBC connection default configuration.
 */
public class JdbcDefaultConfiguration extends Configuration {
    private static final String DEFAULT_DRIVER_NAME = "com.mysql.jdbc.Driver";
    private static final String DEFAULT_DATABASE_TYPE = "mysql";
    private static final String DEFAULT_DATABASE_NAME = "cloud_users";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_USERNAME = "cloud_user_service";
    private static final String DEFAULT_PASSWORD = "!Mega@destroyer123123";
    private static final Integer DEFAULT_PORT = 3306;

    {
        setDriverName( DEFAULT_DRIVER_NAME );
        setDatabaseType( DEFAULT_DATABASE_TYPE );
        setDatabaseName( DEFAULT_DATABASE_NAME );
        setHost( DEFAULT_HOST );
        setUsername( DEFAULT_USERNAME );
        setPassword( DEFAULT_PASSWORD );
        setPort( DEFAULT_PORT );
    }

    public String toString() {
        return "jdbc:" + getDatabaseType() + "://" + getHost() + ":" + this.getPort() + "/" + getDatabaseName();
    }
}