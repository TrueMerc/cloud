package ru.ryabtsev.cloud.client.service;

import ru.ryabtsev.cloud.common.message.Message;

import java.io.IOException;

/**
 * Provides network service interface.
 */
public interface NetworkService {

    /**
     * Starts new network session with given host and port.
     */
    void start();

    /**
     * Stops network session and releases all I/O resources.
     */
    void stop();

    /**
     * Sends message to server.
     * @param message message to send.
     * @return true if the message is successfully sent or false if it isn't.
     */
    boolean sendMessage(Message message);

    /**
     * Receives message from server.
     * @return received message.
     * @throws ClassNotFoundException if class can't be restored after serialization.
     * @throws IOException on input-output error.
     */
     Message receiveMessage() throws ClassNotFoundException, IOException;

    /**
     * Returns true if connection is established and false in the other case.
     * @return true if connection is established and false in the other case.
     */
    boolean isConnected();

    /**
     * Saves network service settings.
     */
    void saveSettings();
}
