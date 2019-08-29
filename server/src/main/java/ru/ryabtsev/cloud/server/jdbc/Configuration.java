package ru.ryabtsev.cloud.server.jdbc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Database connection configuration base class.
 */
@Getter
@Setter
@NoArgsConstructor
public class Configuration {

    private String driverName;

    private String databaseType;

    private String databaseName;

    private String host;

    private String username;

    private String password;

    private Integer port;
}