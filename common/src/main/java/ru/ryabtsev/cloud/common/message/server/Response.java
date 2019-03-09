package ru.ryabtsev.cloud.common.message.server;

import ru.ryabtsev.cloud.common.message.Message;

/**
 * Provides server responses interface.
 */
public interface Response extends Message {

    /**
     * Returns true if client request is successful or false in the other case.
     * @return true if client request is successful or false in the other case.
     */
    boolean isSuccessful();
}
