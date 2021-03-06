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
public class FileMessage extends UserDependentMessage {
    private String fileName;
    private byte[] data;
    int partNumber;
    @Getter(AccessLevel.PRIVATE) boolean isTail;

    /**
     * Constructs file message with given file name.
     * @param login user login.
     * @param path path corresponding to the file.
     * @param partNumber partNumber of file path which should be placed into this file message.
     * @param maximalPayloadSize maximal size of this message payload in bytes (should be the same in one file transmission time).
     * which is contained in the message in whole or in part.
     * @throws IOException if can't read enough bytes from file.
     */
    public FileMessage(String login, Path path, int partNumber, int maximalPayloadSize) throws IOException {
        super(login);
        this.fileName = path.getFileName().toString();
        this.partNumber = partNumber;

        long tailSize = determineTailSize( path, partNumber, maximalPayloadSize );

        if(tailSize >= 0) {
            isTail = tailSize < maximalPayloadSize;
            int payloadSize = (int)((isTail) ? tailSize : maximalPayloadSize);
            data = new byte[payloadSize];
            ByteBuffer buffer = ByteBuffer.wrap(data);
            FileChannel channel = FileChannel.open(path);
            channel.position((long)partNumber * maximalPayloadSize);
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

    public boolean isFirstPart() {
        return 0 == partNumber;
    }
}
