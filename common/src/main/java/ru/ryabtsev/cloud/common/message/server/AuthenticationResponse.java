package ru.ryabtsev.cloud.common.message.server;

import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements
 */
public class AuthenticationResponse extends AbstractMessage implements Response {
    private boolean isSuccessful;

    /**
     * Constructs new authentication response.
     */
    public AuthenticationResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }

    @Override
    public boolean isSuccessful() {
        return isSuccessful;
    }
}
