package ru.ryabtsev.cloud.common;

import org.jetbrains.annotations.NotNull;
import ru.ryabtsev.cloud.common.message.FileMessage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Implements simple file operations which are used both by client and server side.
 */
public class FileOperations {

    public static StandardOpenOption getOpenOption(@NotNull final String fileName, boolean isFirstPart) {
        StandardOpenOption result = StandardOpenOption.CREATE;
        if(Files.exists(Paths.get( fileName ))) {
            if(isFirstPart) {
                result = StandardOpenOption.WRITE;
            }
            else {
                result = StandardOpenOption.APPEND;
            }
        }
        return result;
    }
}
