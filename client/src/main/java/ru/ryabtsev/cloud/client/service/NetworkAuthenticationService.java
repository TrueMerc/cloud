package ru.ryabtsev.cloud.client.service;

import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.client.AuthenticationRequest;
import ru.ryabtsev.cloud.common.message.server.AuthenticationResponse;

import java.io.IOException;

/**
 * Implements service which authenticate users by network.
 */
public class NetworkAuthenticationService implements AuthenticationService {

    private final NetworkService networkService;

    /**
     * Constructs network authentication service.
     * @param networkService network service using to send authentication messages.
     */
    public NetworkAuthenticationService(final NetworkService networkService) {
        this.networkService = networkService;
    }

    @Override
    public boolean authenticate(@NotNull final String login,@NotNull final String password) {
        networkService.sendMessage(new AuthenticationRequest(login, password));

        try {
            AbstractMessage response = networkService.receiveMessage();
            if(response.type().equals(AuthenticationResponse.class)) {
                return ((AuthenticationResponse)response).isSuccessful();
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
