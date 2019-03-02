package ru.ryabtsev.cloud.common.message;

/**
 * Provides messaging interface for 'client-server' communication.
 */
public interface Message {
    /**
     * Returns message class and such describes message type.
     * @return message class.
     */
    Class<? extends Message> type();
}
