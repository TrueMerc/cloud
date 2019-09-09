package ru.ryabtsev.cloud.common.message;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.client.Request;

/**
 * Base class for requests depend from something.
 */
@Getter
public class UserDependentMessage extends AbstractMessage implements Request {
    private final String login;

    /**
     * Constructs new user request.
     * @param login user login.
     */
    public UserDependentMessage(@NotNull final String login) {
        this.login = login;
    }
}
