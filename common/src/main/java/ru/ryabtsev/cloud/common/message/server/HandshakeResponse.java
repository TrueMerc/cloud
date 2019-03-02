package ru.ryabtsev.cloud.common.message.server;

import lombok.Getter;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements response from server to client.
 */
@Getter
public class HandshakeResponse extends AbstractMessage implements Response {
    boolean isSuccessful;

    /**
     * Constructs new handshake response.
     * @param isSuccessful
     */
    public HandshakeResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}
