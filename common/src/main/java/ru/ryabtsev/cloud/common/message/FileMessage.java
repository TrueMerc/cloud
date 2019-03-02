package ru.ryabtsev.cloud.common.message;

import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implements message which contains file or its part.
 */
@Getter
public class FileMessage extends AbstractMessage {
    private String fileName;
    private byte[] data;
    int partNumber;
    @Getter(AccessLevel.PRIVATE) boolean isTail;

    /**
     * Constructs file message with given file name.
     * @param path path corresponding to the file,
     * @param partNumber partNumber of file path which should be placed into this file message.
     * @param maximalSize maximal size of this message in bytes (should be the same in one file transmission time).
     * which is contained in the message in whole or in part.
     * @throws IOException
     */
    public FileMessage(Path path, int partNumber, int maximalSize) throws IOException {
        this.fileName = path.getFileName().toString();
        this.partNumber = partNumber;

        long tailSize = determineTailSize( path, partNumber, maximalSize );

        if(tailSize >= 0) {
            isTail = tailSize < maximalSize;
            int payloadSize = (int)((isTail) ? tailSize : maximalSize);
            data = new byte[payloadSize];
            ByteBuffer buffer = ByteBuffer.wrap(data);
            FileChannel channel = FileChannel.open(path);
            channel.position((long)partNumber * maximalSize);
            int bytesRead = channel.read(buffer);
            if( bytesRead < payloadSize ) {
                throw new IOException("Can't read enough bytes.");
            }
        }
        else {
            throw new IOException(
                    "Can't build file message for file" + path.getFileName() +
                    ". Part " + partNumber + " doesn't exist."
            );
        }
    }

    private long determineTailSize(Path path, int partNumber, int maximalSize) throws IOException {
        final long fileSize = Files.size(path);
        return fileSize - (long)(partNumber * maximalSize);
    }

    /**
     * Returns true if this file message contains the last part of the file.
     */
    public boolean hasNext() {
        return !isTail;
    }
}
