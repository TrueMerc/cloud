package ru.ryabtsev.cloud.common.message.server.file;

import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements file deletion response.
 */
public class DeleteResponse extends AbstractMessage implements FileResponse {
    private boolean isSucessful;

    /**
     * Constructs delete file response message.
     */
    public DeleteResponse(boolean isSucessful) {
        this.isSucessful = isSucessful;
    }

    @Override
    public boolean isSuccessful() {
        return isSucessful;
    }
}
