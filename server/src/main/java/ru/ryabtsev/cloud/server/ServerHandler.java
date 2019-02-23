package ru.ryabtsev.cloud.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.ryabtsev.cloud.common.message.FileMessage;
import ru.ryabtsev.cloud.common.message.client.FileRequest;
import ru.ryabtsev.cloud.common.message.client.FolderStructureRequest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static final String STORAGE_FOLDER = "server_storage/";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object message) throws Exception {
        try {
            if (message == null) {
                return;
            }
            if (message instanceof FileRequest) {
                FileRequest fr = (FileRequest) message;
                if (Files.exists(Paths.get( STORAGE_FOLDER+ fr.getFileName()))) {
                    FileMessage fm = new FileMessage(Paths.get(STORAGE_FOLDER + fr.getFileName()));
                    ctx.writeAndFlush(fm);
                }
            }
            else if( message instanceof FolderStructureRequest) {
                FolderStructureRequest folderStructureRequest = (FolderStructureRequest)message;
                String name = STORAGE_FOLDER + folderStructureRequest.getFolderName();
                Path path = Paths.get(name);
                if(Files.exists(path) && Files.isDirectory(path)) {
                    
                }
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
}