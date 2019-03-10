package ru.ryabtsev.cloud.common.message.server.file;

import ru.ryabtsev.cloud.common.message.AbstractMessage;

/**
 * Implements file deletion response.
 */
public class DeleteResponse extends AbstractMessage implements FileResponse {
    private boolean isSuсcessful;

    /**
     * Constructs delete file response message.
     * @param isSuccessful operation success tag.
     */
    public DeleteResponse(boolean isSuccessful) {
        this.isSuсcessful = isSuccessful;
    }

    @Override
    public boolean isSuccessful() {
        return isSuсcessful;
    }
}
