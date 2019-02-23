package ru.ryabtsev.cloud.common;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implements simple file description using both client and server side.
 */
@Getter
@Setter
public class FileDescription {
    private String name;
    private String extension;
    private String size;
    private String date;
    private String attributes;

    boolean isDirectory;
    List<FileDescription> childDescriptionList;

    /**
     * Constructs simple file description.
     * @param file
     */
    public FileDescription(final File file) {
        this(file, true);
    }

    private FileDescription(final File file, boolean expandDirectories) {
        if( file.isDirectory() ) {
            isDirectory = true;
            name = file.getName();
            extension = "";

            if( expandDirectories ) {
                File[] files = file.listFiles();
                childDescriptionList = new ArrayList<>();
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
