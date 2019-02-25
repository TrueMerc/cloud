package ru.ryabtsev.cloud.common.message;

import java.io.Serializable;

/**
 * Provides interface for messages which send between client and server.
 */
public abstract class Message implements Serializable {
    public Class<? extends Message> type() {
        return this.getClass();
    }
}
