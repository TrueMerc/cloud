package ru.ryabtsev.cloud.server.service;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.ryabtsev.cloud.common.User;
import ru.ryabtsev.cloud.server.jdbc.JdbcConnectionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implements simple MySQL based
 */
public class JdbcUserServiceBean implements UserService {

    private final String serverRootFolder;

    private Map<String, User> users = new LinkedHashMap<>();
    private static final String DEFAULT_SELECT_QUERY = "SELECT * FROM users WHERE login = ?";
    private final JdbcConnectionManager connectionManager = new JdbcConnectionManager();


    /**
     * Constructs JDBC based user service implementation.
     * @param serverRootFolder root folder of server storage.
     */
    public JdbcUserServiceBean(@NotNull final String serverRootFolder) {
        this.serverRootFolder = serverRootFolder;
    }

    @Override
    @SneakyThrows
    public boolean authenticate(@NotNull String login, @NotNull String password) {
        final String query = DEFAULT_SELECT_QUERY;
        connectionManager.connect();
        PreparedStatement statement = connectionManager.createPreparedStatement(query);
        statement.setString( 1, login );
        ResultSet resultSet = statement.executeQuery();
        boolean result = false;
        while( resultSet.next() ) {
            int id = resultSet.getInt("id");
            final String databaseLogin = resultSet.getString("login");
            final String databasePassword = resultSet.getString("password");
            result = password.equals( databasePassword );
            System.out.println("QUERY RESULT: " + resultSet.toString());
            if(result) {
                final String rootFolder = resultSet.getString("root_folder");
                final String currentFolder = resultSet.getString("current_folder");
                users.put(login, new User(login, password, rootFolder, currentFolder));
            }
        }
        connectionManager.disconnect();
        return result;
    }

    @Override
    public @Nullable String getRootFolder(@NotNull final String login) {
        User user = users.get(login);
        return (user != null) ? fullPathName(users.get(login).getRootFolder()) : null;
    }

    @Override
    public @Nullable String getCurrentFolder(@NotNull final String login) {
        User user = users.get(login);
        return (user != null) ? fullPathName(users.get(login).getCurrentFolder()) : null;
    }

    private String fullPathName(@NotNull final String userFolderPathName) {
        return serverRootFolder + '/' + userFolderPathName;
    }
}
