package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.interfaces.MessageHandlerFactory;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.client.AuthenticationRequest;
import ru.ryabtsev.cloud.common.message.client.file.*;
import ru.ryabtsev.cloud.server.service.UserService;

import java.util.List;
import java.util.logging.Logger;

/**
 * Implements handlers factory for messages which received by server.
 */
public class ServerMessageHandlerFactory implements MessageHandlerFactory {

    private static final Logger LOGGER = Logger.getLogger(ServerMessageHandlerFactory.class.getSimpleName());

    private final ChannelHandlerContext context;
    private final UserService userService;
    private final List<String> downloadList;
    private final List<String> deleteList;

    /**
     * Constructs handlers factory for messages which received by server.
     * @param context channel handler context.
     * @param service user management service.
     * @param downloadList list of files which should be downloaded from server.
     * @param deleteList list of files which should be deleted from server.
     */
    public ServerMessageHandlerFactory(
            ChannelHandlerContext context,
            UserService service,
            List<String> downloadList,
            List<String> deleteList
    ) {
        this.context = context;
        userService = service;
        this.downloadList = downloadList;
        this.deleteList = deleteList;
    }

    /**
     * Returns handler for given message.
     * @param message message.
     * @return handler for given message.
     */
    public MessageHandler getHandler(Message message) {
        AbstractMessage abstractMessage = (AbstractMessage) message;

        if(null == message || null == abstractMessage) {
            LOGGER.warning("Null message received.");
            return null;
        }

        if  (message.type().equals(AuthenticationRequest.class)) {
            AuthenticationRequest request = (AuthenticationRequest)message;
            return new AuthenticationRequestHandler(context, userService, request);
        }
        else if (message.type().equals(DownloadRequest.class)) {
            DownloadRequest request = (DownloadRequest)message;
            return new DownloadRequestHandler(context,
                    userService.getCurrentFolder(request.getLogin()),
                    downloadList,
                    deleteList,
                    request
            );
        }
        else if (message.type().equals(DeleteRequest.class)) {
            DeleteRequest request = (DeleteRequest)message;
            return new DeleteRequestHandler(context,
                    userService.getCurrentFolder(request.getLogin()),
                    downloadList,
                    deleteList,
                    request
            );
        }
        else if (message.type().equals(UploadRequest.class)) {
            UploadRequest request = (UploadRequest)message;
            return new UploadRequestHandler(context, request);
        }
        else if (message.type().equals(FileMessage.class)) {
            FileMessage fileMessage = (FileMessage) message;
            return new FileMessageHandler(context, userService.getCurrentFolder(fileMessage.getLogin()), fileMessage);
        }
        else if (message.type().equals(FileStructureRequest.class)) {
            FileStructureRequest request = (FileStructureRequest)message;
            return new FileStructureRequestHandler(context, userService, request);
        }
        else if(message.type().equals(RenameRequest.class)) {
            RenameRequest request = (RenameRequest)message;
            return new RenameRequestHandler(context, userService.getCurrentFolder(request.getLogin()), request);
        }

        LOGGER.warning("Unexpected message received with type " + message.type());
        return null;
    }
}
