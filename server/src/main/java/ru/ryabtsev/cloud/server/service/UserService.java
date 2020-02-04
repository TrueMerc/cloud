package ru.ryabtsev.cloud.server.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides user management service interface.
 */
public interface UserService {

    /**
     * Executes authentication procedure.
     * @param login user login
     * @param password user password
     * @return true if authentication is successful and false in the other case.
     */
    boolean authenticate(final @NotNull String login, final @NotNull String password);

    /**
     * Returns root folder for user with the given name.
     */
    @Nullable String getRootFolder(final String login);

    /**
     * Returns current folder for user with the given name.
     */
    @Nullable String getCurrentFolder(final String login);
}
