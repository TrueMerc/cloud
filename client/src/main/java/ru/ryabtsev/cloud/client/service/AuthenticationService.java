package ru.ryabtsev.cloud.client.service;

/**
 * Provides simple authentication service interface.
 */
public interface AuthenticationService {

    /**
     * Executes authentication procedure.
     * @param login user login
     * @param password user password
     * @return true if authentication is successful and false in the other case.
     */
    boolean authenticate(final String login, final String password);
}
