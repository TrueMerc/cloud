package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements handshake request from client to server.
 */
@Getter
public class HandshakeRequest extends UserRequest {
    /**
     * Constructs new client handshake request.
     */
    public HandshakeRequest(@NotNull final String login) {
        super(login);
    }
}
