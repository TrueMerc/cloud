package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.Message;

/**
 * Base class for requests depend from something.
 */
@Getter
public class UserRequest extends Message {
    private final String login;

    /**
     * Constructs new user request.
     * @param login user login.
     */
    UserRequest(@NotNull final String login) {
        this.login = login;
    }
}
