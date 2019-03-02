package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Base class for requests depend from something.
 */
@Getter
public class UserRequest extends AbstractMessage implements Request{
    private final String login;

    /**
     * Constructs new user request.
     * @param login user login.
     */
    public UserRequest(@NotNull final String login) {
        this.login = login;
    }
}
