package ru.ryabtsev.cloud.client.handlers;

import ru.ryabtsev.cloud.client.FileManagementController;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.interfaces.MessageHandlerFactory;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.file.RenameResponse;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;

import java.util.logging.Logger;

/**
 * Implements handlers factory for messages which received by client.
 */
public class ClientMessageHandlerFactory implements MessageHandlerFactory {

    private static final Logger LOGGER = Logger.getLogger(ClientMessageHandlerFactory.class.getSimpleName());

    private final FileManagementController controller;

    public ClientMessageHandlerFactory(final FileManagementController controller) {
        this.controller = controller;
    }

    @Override
    public MessageHandler getHandler(Message message) {
        if(message.type().equals(DeleteResponse.class)) {
            return new DeleteResponseHandler(controller, (DeleteResponse)message);
        }
        else if(message.type().equals(FileMessage.class)) {
            return new FileMessageHandler(controller, (FileMessage)message);
        }
        else if(message.type().equals(FileStructureResponse.class)) {
            return new FileStructureResponseHandler(controller, (FileStructureResponse)message);
        }
        else if(message.type().equals(RenameResponse.class)) {
            return new RenameResponseHandler(controller, (RenameResponse)message);
        }
        else if(message.type().equals(UploadResponse.class)) {
            return new UploadResponseHandler(controller, (UploadResponse)message);
        }

        LOGGER.warning("Unexpected message received with type " + message.type());
        return null;
    }
}
