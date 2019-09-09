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

    private MessageFactory messageFactory = new ServerMessageFactory();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        final HandlerFactory handlerFactory = new HandlerFactory(ctx, userService, filesToDownload, filesToDelete);
        try {
            Message message = (Message) object;
            if (message == null) {
                LOGGER.warning("null message received.");
                return;
            }
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

//    private void processAuthenticationRequest(ChannelHandlerContext ctx, AuthenticationRequest request) {
//        logMessage(request);
//        boolean result = userService.authenticate(request.getLogin(), request.getPassword());
//        if(result) {
//            String userLogin = request.getLogin();
//            String userRootFolder = userService.getRootFolder(userLogin);
//            userCurrentFolder = userService.getCurrentFolder(userLogin);
//        }
//        ctx.writeAndFlush(messageFactory.getMessage(Operations.AUTHENTICATE, true));
//    }

    private void logMessage(AbstractMessage request) {
        System.out.println(request.getClass().getSimpleName() + " received");
    }

//    private void processDownloadRequest(final ChannelHandlerContext ctx, final DownloadMessage request) throws IOException {
//        logMessage(request);
//        final String name = request.getFileName();
//        final Path path = Paths.get(formFolderDependentFileName(name));
//        if (Files.exists(path)) {
//            FileMessage fm = new FileMessage(
//                    path,
//                    request.getPartNumber(),
//                    NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
//            );
//            ctx.writeAndFlush(fm);
//            LOGGER.info(
//                    "File message sent: " +
//                            "\nname = " + fm.getFileName() +
//                            "\npartNumber = " + fm.getPartNumber() +
//                            "\npayloadLength = " + fm.getData().length
//            );
//            if(fm.hasNext()) {
//                filesToDownload.add(name);
//            }
//            else {
//                filesToDownload.remove(name);
//                if(filesToDelete.contains(name)) {
//                    boolean result = delete(name);
//                    filesToDelete.remove(name);
//                    ctx.writeAndFlush(new DeleteResponse(result));
//                }
//            }
//        }
//        else {
//            LOGGER.warning("File with given name " + name + " doesn't exists.");
//        }
//    }
//
//    private void processDeleteRequest(ChannelHandlerContext ctx, DeleteMessage request) {
//        final String fileName = request.getFileName();
//        if( filesToDownload.contains(fileName)) {
//            filesToDelete.add(fileName);
//        }
//        else {
//            boolean result = delete(fileName);
//            ctx.writeAndFlush(messageFactory.getMessage(Operations.DELETE, result));
//        }
//    }

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

//    private void processUploadRequest(final ChannelHandlerContext ctx, final UploadMessage request) {
//        ctx.writeAndFlush(messageFactory.getMessage(Operations.UPLOAD, request));
//    }
//
//    private void processFileMessage(ChannelHandlerContext ctx, FileMessage message) {
//        try {
//            StandardOpenOption openOption = getOpenOption(message);
//            Files.write(
//                    Paths.get( formFolderDependentFileName(message.getFileName()) ),
//                    message.getData(),
//                    openOption
//            );
//
//            ctx.writeAndFlush(
//                    message.hasNext() ?
//                            messageFactory.getMessage(Operations.UPLOAD, message.getFileName(), message.getPartNumber()) :
//                            messageFactory.getMessage(Operations.UPLOAD, message.getFileName())
//            );
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private String formFolderDependentFileName(String fileName) {
        return userCurrentFolder + '/' + fileName;
    }

    private StandardOpenOption getOpenOption(final FileMessage message) {
        return FileOperations.getOpenOption(formFolderDependentFileName(message.getFileName()), message.getPartNumber() == 0);
    }

//    private void processFileStructureRequest(final ChannelHandlerContext ctx, final FileStructureMessage request) {
//        final String name = userService.getCurrentFolder(request.getLogin()) + request.getFolderName();
//        final Path path = Paths.get(name);
//        if(Files.exists(path) && Files.isDirectory(path)) {
//            final FileDescription description = new FileDescription(path.toFile());
//            final FileStructureResponse response = new FileStructureResponse(description);
//            ctx.writeAndFlush(messageFactory.getMessage(Operations.FILE_STRUCTURE, description));
//        }
//    }
//
//
//    private void processRenameRequest(ChannelHandlerContext ctx, final RenameMessage request) {
//        final Path oldPath = Paths.get(formFolderDependentFileName(request.getOldName()));
//        final Path newPath = Paths.get(formFolderDependentFileName(request.getNewName()));
//
//        try {
//            Files.move(oldPath, newPath);
//            ctx.writeAndFlush(messageFactory.getMessage(Operations.RENAME, request.getOldName(), request.getNewName(), true));
//        } catch (IOException e) {
//            ctx.writeAndFlush(messageFactory.getMessage(Operations.RENAME, request.getOldName(), request.getNewName(), false));
//            e.printStackTrace();
//        }
//    }
}