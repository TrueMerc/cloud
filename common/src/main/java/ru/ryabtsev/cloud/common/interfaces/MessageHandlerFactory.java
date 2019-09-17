package ru.ryabtsev.cloud.common.interfaces;

import ru.ryabtsev.cloud.common.message.Message;

/**
 * Provides interface for message handlers factories.
 */
public interface MessageHandlerFactory {
    MessageHandler getHandler(Message message);
}
