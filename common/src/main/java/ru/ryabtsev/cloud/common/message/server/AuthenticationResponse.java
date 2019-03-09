package ru.ryabtsev.cloud.common.message.server;

import lombok.Getter;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements
 */
public class AuthenticationResponse extends AbstractMessage implements Response {
    private boolean isSucessful;

    /**
     * Constructs new authentication response.
     */
    public AuthenticationResponse(boolean isSucessful) {
        this.isSucessful = isSucessful;
    }

    @Override
    public boolean isSuccessful() {
        return isSucessful;
    }
}
