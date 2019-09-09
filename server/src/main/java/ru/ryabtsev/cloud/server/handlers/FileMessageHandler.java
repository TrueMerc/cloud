package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.ryabtsev.cloud.common.FileOperations;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.Operations;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Handles file message.
 */
public class FileMessageHandler implements Handler {

    private final ChannelHandlerContext context;
    private final String userCurrentFolder;
    private final FileMessage message;

    FileMessageHandler(ChannelHandlerContext context, String userCurrentFolder, FileMessage message) {
       this.context = context;
       this.userCurrentFolder = userCurrentFolder;
       this.message = message;
    }

    @Override
    public void handle() {
        try {
            StandardOpenOption openOption = getOpenOption(message);
            Files.write(Paths.get(userCurrentFolder, message.getFileName()), message.getData(), openOption);

            context.writeAndFlush(
                    message.hasNext() ?
                            new UploadResponse(message.getFileName(), message.getPartNumber()) :
                            new UploadResponse(message.getFileName())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StandardOpenOption getOpenOption(final FileMessage message) {
        return FileOperations.getOpenOption(
                Paths.get(userCurrentFolder, message.getFileName()).toString(),
                message.getPartNumber() == 0
        );
    }
}
