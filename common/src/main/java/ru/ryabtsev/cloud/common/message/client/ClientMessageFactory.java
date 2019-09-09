package ru.ryabtsev.cloud.common.message.client;

import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.MessageFactory;
import ru.ryabtsev.cloud.common.message.Operations;
import ru.ryabtsev.cloud.common.message.client.file.*;

public class ClientMessageFactory implements MessageFactory {
    @Override
    public Message getMessage(Operations operation, Object... args) {
        switch (operation) {
            case AUTHENTICATE:
                return new AuthenticationRequest((String)args[0], (String)args[1]);
            case DELETE:
                return new DeleteRequest((String)args[0], (String)args[1]);
            case DOWNLOAD:
                return new DownloadRequest((String)args[0], (String)args[1], (Integer)args[2]);
            case FILE_STRUCTURE:
                return new FileStructureRequest((String)args[0], (String)args[1]);
            case RENAME:
                return new RenameRequest((String)args[0], (String)args[1], (String)args[2]);
            case UPLOAD:
                return new UploadRequest((String)args[0], (String)args[1], (Integer)args[2]);
        }

        return null;
    }
}
