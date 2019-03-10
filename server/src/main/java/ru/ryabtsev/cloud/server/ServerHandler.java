package ru.ryabtsev.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.FileOperations;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.client.AuthenticationRequest;
import ru.ryabtsev.cloud.common.message.client.file.*;
import ru.ryabtsev.cloud.common.message.server.AuthenticationResponse;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.file.RenameResponse;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;
import ru.ryabtsev.cloud.server.service.JdbcUserServiceBean;
import ru.ryabtsev.cloud.server.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final String SERVER_STORAGE_ROOT = "./server_storage";

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getSimpleName());

    private static UserService userService = new JdbcUserServiceBean(SERVER_STORAGE_ROOT);

    private String userCurrentFolder = "";

    private List<String> filesToDownload = new LinkedList<>();

    private Set<String> filesToDelete = new LinkedHashSet<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        try {
            AbstractMessage message = (AbstractMessage) object;
            if (message == null) {
                LOGGER.warning("null message received.");
            }
            if  (message.type().equals(AuthenticationRequest.class)) {
                processAuthenticationRequest(ctx, (AuthenticationRequest)message);
            }
            else if (message.type().equals(DownloadRequest.class)) {
                processDownloadRequest(ctx, (DownloadRequest) message);
            }
            else if (message.type().equals(DeleteRequest.class)) {
                processDeleteRequest(ctx, (DeleteRequest) message);
            }
            else if (message.type().equals(UploadRequest.class)) {
                processUploadRequest(ctx, (UploadRequest) message);
            }
            else if (message.type().equals(FileMessage.class)) {
                processFileMessage(ctx, (FileMessage) message);
            }
            else if (message.type().equals(FileStructureRequest.class)) {
                processFileStructureRequest(ctx, (FileStructureRequest) message);
            }
            else if(message.type().equals(RenameRequest.class)) {
                processRenameRequest(ctx, (RenameRequest) message);
            }
            else {
                LOGGER.warning("Unexpected message received with type " + message.type());
            }
        }
        catch(ClassCastException e) {
            e.printStackTrace();
        }
        finally {
            ReferenceCountUtil.release(object);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void processAuthenticationRequest(ChannelHandlerContext ctx, AuthenticationRequest request) {
        logMessage(request);
        boolean result = userService.authenticate(request.getLogin(), request.getPassword());
        if(result) {
            String userLogin = request.getLogin();
            String userRootFolder = userService.getRootFolder(userLogin);
            userCurrentFolder = userService.getCurrentFolder(userLogin);
        }
        ctx.writeAndFlush(new AuthenticationResponse(true));
    }

    private void logMessage(AbstractMessage request) {
        System.out.println(request.getClass().getSimpleName() + " received");
    }

    private void processDownloadRequest(final ChannelHandlerContext ctx, final DownloadRequest request) throws IOException {
        logMessage(request);
        final String name = request.getFileName();
        final Path path = Paths.get(formFolderDependentFileName(name));
        if (Files.exists(path)) {
            FileMessage fm = new FileMessage(
                    path,
                    request.getPartNumber(),
                    NetworkSettings.MAXIMAL_PAYLOAD_SIZE_IN_BYTES
            );
            ctx.writeAndFlush(fm);
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
                if(filesToDownload.contains(name)) {
                    filesToDownload.remove(name);
                }
                if(filesToDelete.contains(name)) {
                    boolean result = delete(name);
                    filesToDelete.remove(name);
                    ctx.writeAndFlush(new DeleteResponse(result));
                }
            }
        }
        else {
            LOGGER.warning("File with given name " + name + " doesn't exists.");
        }
    }

    private void processDeleteRequest(ChannelHandlerContext ctx, DeleteRequest request) {
        final String fileName = request.getFileName();
        if( filesToDownload.contains(fileName)) {
            filesToDelete.add(fileName);
        }
        else {
            boolean result = delete(fileName);
            ctx.writeAndFlush(new DeleteResponse(result));
        }
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

    private void processUploadRequest(final ChannelHandlerContext ctx, final UploadRequest request) {
        ctx.writeAndFlush(new UploadResponse(request.getFileName()));
    }

    private void processFileMessage(ChannelHandlerContext ctx, FileMessage message) {
        try {
            StandardOpenOption openOption = getOpenOption(message);
            Files.write(
                    Paths.get( formFolderDependentFileName(message.getFileName()) ),
                    message.getData(),
                    openOption
            );

            UploadResponse response = message.hasNext() ?
                    new UploadResponse(message.getFileName(), message.getPartNumber()) :
                    new UploadResponse(message.getFileName());

            ctx.writeAndFlush(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formFolderDependentFileName(String fileName) {
        return userCurrentFolder + '/' + fileName;
    }

    private StandardOpenOption getOpenOption(final FileMessage message) {
        return FileOperations.getOpenOption(formFolderDependentFileName(message.getFileName()), message.getPartNumber() == 0);
    }

    private void processFileStructureRequest(final ChannelHandlerContext ctx, final FileStructureRequest request) {
        final String name = userService.getCurrentFolder(request.getLogin()) + request.getFolderName();
        final Path path = Paths.get(name);
        if(Files.exists(path) && Files.isDirectory(path)) {
            final FileDescription description = new FileDescription(path.toFile());
            final FileStructureResponse response = new FileStructureResponse(description);
            ctx.writeAndFlush(response);
        }
    }


    private void processRenameRequest(ChannelHandlerContext ctx, final RenameRequest request) {
        final Path oldPath = Paths.get(formFolderDependentFileName(request.getOldName()));
        final Path newPath = Paths.get(formFolderDependentFileName(request.getNewName()));

        try {
            Files.move(oldPath, newPath);
            ctx.writeAndFlush(new RenameResponse(request.getOldName(), request.getNewName(), true));
        } catch (IOException e) {
            ctx.writeAndFlush(new RenameResponse(request.getOldName(), request.getNewName(), false));
            e.printStackTrace();
        }
    }
}