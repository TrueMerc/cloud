package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.interfaces.MessageHandler;
import ru.ryabtsev.cloud.common.message.client.file.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.server.service.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Handles file structure request.
 */
public class FileStructureRequestHandler implements MessageHandler {

    private final ChannelHandlerContext context;
    private final UserService userService;
    private final FileStructureRequest request;

    FileStructureRequestHandler(ChannelHandlerContext context, UserService service, FileStructureRequest request) {
        this.context = context;
        this.userService = service;
        this.request = request;
    }

    @Override
    public void handle() {
        final String name = userService.getCurrentFolder(request.getLogin()) + request.getFolderName();
        final Path path = Paths.get(name);
        if(Files.exists(path) && Files.isDirectory(path)) {
            final FileDescription description = new FileDescription(path.toFile());
            final FileStructureResponse response = new FileStructureResponse(description);
            context.writeAndFlush(new FileStructureResponse(description));
        }
    }
}
