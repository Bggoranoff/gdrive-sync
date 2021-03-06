package com.bggoranoff.drivesync.model.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface FileSystemEntity {
    void delete() throws FileNotFoundException;

    void create() throws IOException;

    void rename(String name) throws IOException;

    void move(String destination, boolean copy) throws IOException;

    long getLastTimeModified();

    java.io.File getContainedFile();
}
