package ru.ryabtsev.cloud.common;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Implements cloud application user.
 */
@Getter
@Setter
public class User {

    private String login;
    private String password;
    private String rootFolder;
    private String currentFolder;

    /**
     * Constructs cloud application user.
     */
    public User(@NotNull final String login,
                @NotNull final String password,
                @NotNull final String rootFolder,
                @NotNull final String currentFolder
    )
    {
        this.login = login;
        this.password = password;
        this.rootFolder = rootFolder;
        this.currentFolder = currentFolder;
    }
}
