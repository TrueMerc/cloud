package ru.ryabtsev.cloud.client.service;

import ru.ryabtsev.cloud.common.message.AbstractMessage;

import java.io.IOException;

/**
 * Provides network service interface.
 */
public interface NetworkService {

    /**
     * Starts new network session with given host and port.
     * @param host server host.
     * @param port server port.
     */
    void start(final String host, int port);

    /**
     * Stops network session and releases all I/O resources.
     */
    void stop();

    /**
     * Sends message to server.
     * @param message message to send.
     * @return true if the message is successfully sent or false if it isn't.
     */
    boolean sendMessage(AbstractMessage message);

    /**
     * Receives message from server.
     * @return received message.
     * @throws ClassNotFoundException
     * @throws IOException
     */
     AbstractMessage receiveMessage() throws ClassNotFoundException, IOException;
}
