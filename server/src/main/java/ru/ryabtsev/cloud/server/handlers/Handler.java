package ru.ryabtsev.cloud.server.handlers;

import java.io.IOException;

/**
 * Provides interface for client message handlers.
 */
public interface Handler {

    /**
     * Handles data which should be given to handler when it is created.
     */
    void handle();

}
