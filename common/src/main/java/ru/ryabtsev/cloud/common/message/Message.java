package ru.ryabtsev.cloud.common.message;

import java.io.Serializable;

/**
 * Provides messaging interface for 'client-server' communication.
 */
public interface Message extends Serializable {
    /**
     * Returns message class and such describes message type.
     * @return message class.
     */
    Class<? extends Message> type();
}
