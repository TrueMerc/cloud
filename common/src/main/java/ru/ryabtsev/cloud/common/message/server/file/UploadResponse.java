package ru.ryabtsev.cloud.common.message.server.file;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements 'upload response' message from server to client.
 */
@Getter
public class UploadResponse extends AbstractMessage implements FileResponse {
    private String fileName;
    private int nextNumber;
    private boolean isComplete;

    /**
     * Constructs upload request with given file name.
     * Use this constructor if the file upload is NOT complete.
     * @param fileName name of the file, which is requested by client.
     * @param partNumber number of the part of the file.
     */
    public UploadResponse(@NotNull final String fileName, final int partNumber) {
        this.fileName = fileName;
        this.nextNumber = partNumber + 1;
        this.isComplete = false;
    }

    /**
     * Constructs upload request with given file name.
     * Use this constructor if the file upload is complete.
     * @param fileName name of the file, which is requested by client.
     */
    public UploadResponse(@NotNull final String fileName) {
        this.fileName = fileName;
        this.nextNumber = -1;
        this.isComplete = true;
    }

    @Override
    public boolean isSuccessful() {
        return true;
    }


}
