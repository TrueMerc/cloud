package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * User authentication request.
 */
@Getter
public class AuthenticationRequest extends AbstractMessage implements Request {
    private final String login;
    private final String password;

    /**
     * Constructs user authentication request.
     */
    public AuthenticationRequest(@NotNull final String login, @NotNull final String password) {
        this.login = login;
        this.password = password;
    }
}
