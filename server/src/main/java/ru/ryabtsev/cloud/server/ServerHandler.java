package ru.ryabtsev.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.SneakyThrows;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.FileOperations;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.AbstractMessage;
import ru.ryabtsev.cloud.common.message.client.AuthenticationRequest;
import ru.ryabtsev.cloud.common.message.client.file.DeleteRequest;
import ru.ryabtsev.cloud.common.message.client.file.DownloadRequest;
import ru.ryabtsev.cloud.common.message.client.file.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.client.HandshakeRequest;
import ru.ryabtsev.cloud.common.message.client.file.UploadRequest;
import ru.ryabtsev.cloud.common.message.server.AuthenticationResponse;
import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.HandshakeResponse;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;
import ru.ryabtsev.cloud.server.service.JdbcUserServiceBean;
import ru.ryabtsev.cloud.server.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final String SERVER_STORAGE_ROOT = "./server_storage";

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getSimpleName());

    private static UserService userService = new JdbcUserServiceBean(SERVER_STORAGE_ROOT);

    private String userCurrentFolder = "";

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
            if (message.type().equals(HandshakeRequest.class)) {
                processHandshakeRequest(ctx, (HandshakeRequest) message);
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

    private void processHandshakeRequest(ChannelHandlerContext ctx, HandshakeRequest request) {
        logMessage(request);
        HandshakeResponse response = new HandshakeResponse(true);
        ctx.writeAndFlush(response);
    }

    private void logMessage(AbstractMessage request) {
        System.out.println(request.getClass().getSimpleName() + " received");
    }

    private void processDownloadRequest(final ChannelHandlerContext ctx, final DownloadRequest request) throws IOException {
        logMessage(request);
        //final String fileName = userService.getCurrentFolder(request.getLogin()) + '/' + request.getFileName();
        final String fileName = formFolderDependentFileName(request.getFileName());
        final Path path = Paths.get(fileName);
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
        }
        else {
            LOGGER.warning("File with given name " + fileName + " doesn't exists.");
        }
    }

    @SneakyThrows
    private void processDeleteRequest(ChannelHandlerContext ctx, DeleteRequest request) {
        final String fileName = formFolderDependentFileName(request.getFileName());
        final Path path = Paths.get(fileName);
        if(Files.exists(path)) {
            Files.delete(path);
            ctx.writeAndFlush(new DeleteResponse(true));
        }
        else {
            ctx.writeAndFlush(new DeleteResponse(false));
            LOGGER.warning("File with given name " + fileName + " doesn't exists.");
        }
    }

    private void processUploadRequest(final ChannelHandlerContext ctx, final UploadRequest request) {
        logMessage(request);
        final String fileName = userService.getCurrentFolder(request.getLogin()) + '/' + request.getFileName();
        ctx.writeAndFlush(new UploadResponse(request.getFileName()));
    }

    private void processFileMessage(ChannelHandlerContext ctx, FileMessage message) {
        logMessage(message);
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
        logMessage(request);
        final String name = userService.getCurrentFolder(request.getLogin()) + request.getFolderName();
        final Path path = Paths.get(name);
        if(Files.exists(path) && Files.isDirectory(path)) {
            final FileDescription description = new FileDescription(path.toFile());
            final FileStructureResponse response = new FileStructureResponse(description);
            ctx.writeAndFlush(response);
        }
    }

}