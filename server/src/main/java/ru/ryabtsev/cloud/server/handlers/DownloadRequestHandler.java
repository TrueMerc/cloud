package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.server.ServerHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

public class DownloadRequestHandler implements MessageHandler {

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getSimpleName());

    private final ChannelHandlerContext context;

    private final String userCurrentFolder;
    private final List<String> filesToDownload;
    private final List<String> filesToDelete;

    private final DownloadRequest request;

    DownloadRequestHandler(ChannelHandlerContext context,
                           String userCurrentFolder,
                           List<String> filesToDownload,
                           List<String> filesToDelete,
                           DownloadRequest request)
    {
        this.context = context;
        this.userCurrentFolder = userCurrentFolder;
        this.filesToDownload = filesToDownload;
        this.filesToDelete = filesToDelete;
        this.request = request;
    }

    @Override
    public void handle() {
        final String name = request.getFileName();
        final Path path = Paths.get(userCurrentFolder, name);
        if (Files.exists(path)) {
            try {
                FileMessage fm = new FileMessage(
                        "",
                        path,
                        request.getPartNumber(),
                        NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
                );
                context.writeAndFlush(fm);
                LOGGER.info(
                        "File message sent: " +
                                "\nname = " + fm.getFileName() +
                                "\npartNumber = " + fm.getPartNumber() +
                                "\npayloadLength = " + fm.getData().length
                );
                if(fm.hasNext()) {
                    filesToDownload.add(name);
                }
                else {
                    filesToDownload.remove(name);
                    if(filesToDelete.contains(name)) {
                        boolean result = delete(name);
                        filesToDelete.remove(name);
                        context.writeAndFlush(new DeleteResponse(result));
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            LOGGER.warning("File with given name " + name + " doesn't exists.");
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
