package ru.ryabtsev.cloud.common.message;

/**
 * Implements some messaging interface for messages which send between client and server.
 */
public abstract class AbstractMessage implements Message {
    public Class<? extends Message> type() {
        return this.getClass();
    }
}
