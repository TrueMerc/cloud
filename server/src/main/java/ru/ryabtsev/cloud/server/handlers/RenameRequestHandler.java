package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.client.file.RenameRequest;
import ru.ryabtsev.cloud.common.message.server.file.RenameResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles rename request.
 */
public class RenameRequestHandler implements MessageHandler {

    private final ChannelHandlerContext context;
    private final String userCurrentFolder;
    private final RenameRequest request;

    RenameRequestHandler(ChannelHandlerContext context, String userCurrentFolder, RenameRequest request) {
        this.context = context;
        this.userCurrentFolder = userCurrentFolder;
        this.request = request;
    }

    @Override
    public void handle() {
        final Path oldPath = Paths.get(userCurrentFolder, request.getOldName());
        final Path newPath = Paths.get(userCurrentFolder, request.getNewName());

        try {
            Files.move(oldPath, newPath);
            context.writeAndFlush(new RenameResponse(request.getOldName(), request.getNewName(), true));
        } catch (IOException e) {
            context.writeAndFlush(new RenameResponse(request.getOldName(), request.getNewName(), false));
            e.printStackTrace();
        }
    }
}
