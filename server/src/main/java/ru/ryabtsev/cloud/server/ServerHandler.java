package ru.ryabtsev.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.FileOperations;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.*;
import ru.ryabtsev.cloud.common.message.client.AuthenticationRequest;
import ru.ryabtsev.cloud.common.message.client.file.*;
import ru.ryabtsev.cloud.common.message.server.ServerMessageFactory;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.server.handlers.Handler;
import ru.ryabtsev.cloud.server.handlers.HandlerFactory;
import ru.ryabtsev.cloud.server.service.JdbcUserServiceBean;
import ru.ryabtsev.cloud.server.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final String SERVER_STORAGE_ROOT = "./server_storage";

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getSimpleName());

    private static UserService userService = new JdbcUserServiceBean(SERVER_STORAGE_ROOT);

    private String userCurrentFolder = "";

    private List<String> filesToDownload = new LinkedList<>();

    private List<String> filesToDelete = new LinkedList<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        final HandlerFactory handlerFactory = new HandlerFactory(ctx, userService, filesToDownload, filesToDelete);
        try {
            Message message = (Message) object;
            final Handler handler = handlerFactory.getHandler(message);
            handler.handle();
        }
        catch(ClassCastException e) {
            e.printStackTrace();
        }
        finally {
            ReferenceCountUtil.release(object);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void logMessage(AbstractMessage request) {
        System.out.println(request.getClass().getSimpleName() + " received");
    }

    @SneakyThrows
    private boolean delete(@NotNull final String name) {
        final Path path = Paths.get(formFolderDependentFileName(name));
        if(Files.exists(path)) {
            Files.delete(path);
            return true;
        }
        LOGGER.warning("File with given name " + name + " doesn't exists.");
        return false;
    }

    private String formFolderDependentFileName(String fileName) {
        return userCurrentFolder + '/' + fileName;
    }

    private StandardOpenOption getOpenOption(final FileMessage message) {
        return FileOperations.getOpenOption(formFolderDependentFileName(message.getFileName()), message.getPartNumber() == 0);
    }
}