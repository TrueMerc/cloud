package ru.ryabtsev.cloud.server.handlers;

import io.netty.channel.ChannelHandlerContext;
import ru.ryabtsev.cloud.common.message.client.file.UploadRequest;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;

public class UploadRequestHandler implements Handler {


    private final ChannelHandlerContext context;
    private final UploadRequest request;

    UploadRequestHandler(ChannelHandlerContext context, UploadRequest request) {
        this.context = context;
        this.request = request;
    }

    @Override
    public void handle() {
        context.writeAndFlush(new UploadResponse(request.getFileName()));
    }
}
