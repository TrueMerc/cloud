package ru.ryabtsev.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.NetworkSettings;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.client.FileRequest;
import ru.ryabtsev.cloud.common.message.client.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.client.HandshakeRequest;
import ru.ryabtsev.cloud.common.message.server.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.HandshakeResponse;
import ru.ryabtsev.cloud.server.service.DummyUserService;
import ru.ryabtsev.cloud.server.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class ServerHandler extends ChannelInboundHandlerAdapter {


    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getSimpleName());

    private static UserService userService = new DummyUserService();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
        try {
            Message message = (Message) object;
            if (message == null) {
                LOGGER.warning("null message received.");
            }
            if (message.type().equals(HandshakeRequest.class)) {
                processHandshakeRequest(ctx, (HandshakeRequest) message);
            }
            else if (message.type().equals(FileRequest.class)) {
                processFileRequest(ctx, (FileRequest) message);
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

    private void processHandshakeRequest(ChannelHandlerContext ctx, HandshakeRequest request) {
        logRequest(request);
        HandshakeResponse response = new HandshakeResponse(true);
        ctx.writeAndFlush(response);
    }

    private void logRequest(Message request) {
        System.out.println(request.getClass().getSimpleName() + " received");
    }

    private void processFileRequest(final ChannelHandlerContext ctx, final FileRequest request) throws IOException {
        logRequest(request);
        final String fileName = userService.getFolder(request.getLogin()) + '/' + request.getFileName();
        if (Files.exists(Paths.get(fileName))) {
            FileMessage fm = new FileMessage(
                    Paths.get(fileName),
                    request.getPartNumber(),
                    NetworkSettings.MAXIMAL_MESSAGE_SIZE_IN_BYTES
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

    private void processFileStructureRequest(final ChannelHandlerContext ctx, final FileStructureRequest request) {
        logRequest(request);
        String name = userService.getFolder(request.getLogin()) + request.getFolderName();
        Path path = Paths.get(name);
        if(Files.exists(path) && Files.isDirectory(path)) {
            final FileDescription description = new FileDescription(path.toFile());
            final FileStructureResponse response = new FileStructureResponse(description);
            ctx.writeAndFlush(response);
        }
    }
}