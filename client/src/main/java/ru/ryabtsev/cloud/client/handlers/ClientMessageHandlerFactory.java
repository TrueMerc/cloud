package ru.ryabtsev.cloud.client.handlers;

import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.interfaces.MessageHandlerFactory;
import ru.ryabtsev.cloud.common.message.Message;

/**
 * Implements handlers factory for messages which received by client.
 */
public class ClientMessageHandlerFactory implements MessageHandlerFactory {
    @Override
    public MessageHandler getHandler(Message message) {
        return null;
    }
}
