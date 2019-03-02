package ru.ryabtsev.cloud.common.message;

import lombok.Getter;
import ru.ryabtsev.cloud.common.message.Message;

/**
 * Implements response from server to client.
 */
@Getter
public class HandshakeResponse extends Message {
    boolean isSuccessful;

    /**
     * Constructs new handshake response.
     * @param isSuccessful
     */
    public HandshakeResponse(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
    }
}
