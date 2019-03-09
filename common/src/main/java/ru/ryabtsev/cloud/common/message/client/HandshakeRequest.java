package ru.ryabtsev.cloud.common.message.client;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * Implements handshake request from client to server.
 */
@Getter
public class HandshakeRequest extends UserDependentRequest {
    /**
     * Constructs new client handshake request.
     */
    public HandshakeRequest(@NotNull final String login) {
        super(login);
    }
}
