package ru.ryabtsev.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.client.FileRequest;
import ru.ryabtsev.cloud.common.message.client.FileStructureRequest;
import ru.ryabtsev.cloud.common.message.client.HandshakeRequest;
import ru.ryabtsev.cloud.common.message.server.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.HandshakeResponse;
import sun.security.ssl.HandshakeMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final String STORAGE_FOLDER = "/";

    private static final Logger LOGGER = Logger.getLogger(ServerHandler.class.getSimpleName());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        try {
            if (message == null) {
                LOGGER.warning("null message received.");
            }
            if (message instanceof HandshakeRequest) {
                processHandshakeRequest(ctx, (HandshakeRequest)message);
            }
            else if(message instanceof FileRequest) {
                processFileRequest(ctx, (FileRequest)message);
            }
            else if( message instanceof FileStructureRequest) {
                processFileStructureRequest(ctx, (FileStructureRequest)message);
            }
        } finally {
            ReferenceCountUtil.release(message);
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
        final String fileName = STORAGE_FOLDER + request.getFileName();
        if (Files.exists(Paths.get(fileName))) {
            FileMessage fm = new FileMessage(Paths.get(fileName));
            ctx.writeAndFlush(fm);
        }
    }

    private void processFileStructureRequest(final ChannelHandlerContext ctx, final FileStructureRequest request) {
        logRequest(request);
        String name = STORAGE_FOLDER + request.getFolderName();
        Path path = Paths.get(name);
        if(Files.exists(path) && Files.isDirectory(path)) {
            final FileDescription description = new FileDescription(path.toFile());
            final FileStructureResponse response = new FileStructureResponse(description);
            ctx.writeAndFlush(response);
        }
    }
}