package ru.ryabtsev.cloud.common;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Implements simple file operations which are used both by client and server side.
 */
public class FileOperations {

    /**
     * Returns file open option for file with given name.
     * @param fileName file name.
     * @param isFirstPart the first part of a file identifier.
     * @return one of standard file open options.
     */
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
