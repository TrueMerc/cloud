package ru.ryabtsev.cloud.common;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

/**
 * Implements simple file description using both client and server side.
 */
@Getter
@Setter
public class FileDescription implements Serializable {
    private String name;
    private String extension;
    private String size;
    private String date;
    private String attributes;

    boolean isDirectory;
    LinkedList<FileDescription> childDescriptionList;

    /**
     * Constructs simple file description.
     * @param file file information.
     */
    public FileDescription(final File file) {
        this(file, true);
    }

    private FileDescription(final File file, boolean expandDirectories) {
        childDescriptionList = new LinkedList<>();
        if( file.isDirectory() ) {
            isDirectory = true;
            name = file.getName();
            extension = "";

            if( expandDirectories ) {
                File[] files = file.listFiles();
                if(files != null) {
                    for (File childFile : files) {
                        childDescriptionList.add(new FileDescription(childFile, false));
                    }
                }
            }
        }
        else {
            isDirectory = false;
            defineNameAndExtension(file.getName());
        }

        this.size = file.isFile() ? String.valueOf( file.length() ) : "<DIR>";
        this.date = new Date( file.lastModified() ).toString();
        this.attributes = "";
    }

    private void defineNameAndExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if( index > 0 ) {
            this.name = fileName.substring(0, index);
            this.extension = fileName.substring(index + 1);
        }
        else {
            this.name = fileName;
        }
    }
}
