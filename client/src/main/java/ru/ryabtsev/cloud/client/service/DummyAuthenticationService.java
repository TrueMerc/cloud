package ru.ryabtsev.cloud.client.service;

/**
 * Implements dummy authentication service for testing purposes.
 */
public class DummyAuthenticationService implements AuthencticationService {

    private static final String DEFAULT_LOGIN = "admin";
    private static final String DEFAULT_PASSWORD = "admin";

    @Override
    public boolean authenticate(String login, String password) {
        return DEFAULT_LOGIN.equals(login) && DEFAULT_PASSWORD.equals(password);
    }
}
