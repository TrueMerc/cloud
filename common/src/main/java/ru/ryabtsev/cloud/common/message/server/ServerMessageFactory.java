package ru.ryabtsev.cloud.common.message.server;

import ru.ryabtsev.cloud.common.FileDescription;
import ru.ryabtsev.cloud.common.message.Message;
import ru.ryabtsev.cloud.common.message.MessageFactory;
import ru.ryabtsev.cloud.common.message.Operations;

import ru.ryabtsev.cloud.common.message.server.file.DeleteResponse;
import ru.ryabtsev.cloud.common.message.server.file.FileStructureResponse;
import ru.ryabtsev.cloud.common.message.server.file.RenameResponse;
import ru.ryabtsev.cloud.common.message.server.file.UploadResponse;

public class ServerMessageFactory implements MessageFactory {
    @Override
    public Message getMessage(Operations operation, Object... args) {
        switch (operation) {
            case AUTHENTICATE:
                return new AuthenticationResponse((Boolean) args[0]);
            case DELETE:
                return new DeleteResponse((Boolean) args[0]);
//            case DOWNLOAD:
//                return new DownloadResponse((String)args[0], (String)args[1], (Integer)args[2]);
            case FILE_STRUCTURE:
                return new FileStructureResponse((FileDescription) args[0]);
            case RENAME:
                return new RenameResponse((String) args[0], (String) args[1], (Boolean) args[2]);
            case UPLOAD:
                return new UploadResponse((String) args[0], (Integer) args[1]);
        }
        return null;
    }
}
