package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.client.file.DeleteRequest;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.server.ServerHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles delete request.
 */
public class DeleteRequestHandler implements MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getSimpleName());

    private final ChannelHandlerContext context;
    private final String userCurrentFolder;
    private final List<String> filesToDownload;
    private final List<String> filesToDelete;
    private final DeleteRequest request;


    DeleteRequestHandler(ChannelHandlerContext context,
                         String userCurrentFolder,
                         List<String> filesToDownload,
                         List<String> filesToDelete,
                         DeleteRequest request)
    {
        this.context = context;
        this.userCurrentFolder = userCurrentFolder;
        this.filesToDownload = filesToDownload;
        this.filesToDelete = filesToDelete;
        this.request = request;
    }

    @Override
    public void handle() {
        final String fileName = request.getFileName();
        if( filesToDownload.contains(fileName)) {
            filesToDelete.add(fileName);
        }
        else {
            boolean result = delete(fileName);
            context.writeAndFlush(new DeleteResponse(result));
        }
    }

    @SneakyThrows
    private boolean delete(@NotNull final String name) {
        final Path path = Paths.get(userCurrentFolder, name);
        if(Files.exists(path)) {
            Files.delete(path);
            return true;
        }
        LOGGER.warning("File with given name " + name + " doesn't exists.");
        return false;
    }
}
