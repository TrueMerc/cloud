package ru.ryabtsev.cloud.common.interfaces;

/**
 * Provides interface for client or server side message handlers.
 */
public interface MessageHandler {
    /**
     * Handles data which should be given to handler when it is created.
     */
    void handle();
}
