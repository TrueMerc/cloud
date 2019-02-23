package ru.ryabtsev.cloud.common.message;

import java.io.Serializable;

/**
 * Provides interface for messages which send between client and server.
 */
public interface Message extends Serializable {
    Class<? extends Message> type();
}
