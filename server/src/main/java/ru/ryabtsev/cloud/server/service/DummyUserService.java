package ru.ryabtsev.cloud.server.service;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple server user service for test purposes.
 */
public class DummyUserService implements UserService {

    private static final String DEFAULT_STORAGE_FOLDER = "./server_storage";

    @Override
    public boolean authenticate(@NotNull final String login, @NotNull final String password) {
        return ("admin".equals(login) && "password".equals(password));
    }

    @Override
    public @Nullable String getFolder(String login) {
        return DEFAULT_STORAGE_FOLDER;
    }
}
