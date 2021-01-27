package com.bggoranoff.drivesync.model.filesystem;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

public class File implements FileSystemEntity {
    private java.io.File file;

    public File(String filePath) {
        this.file = new java.io.File(filePath);
    }

    @Override
    public void delete() throws FileNotFoundException {
        if (!this.file.exists()) {
            throw new FileNotFoundException(String.format("File %s not found!", this.file.getPath()));
        }
        //noinspection ResultOfMethodCallIgnored
        this.file.delete();
    }

    @Override
    public void create() throws IOException {
        if(this.file.exists()) {
            throw new FileAlreadyExistsException(String.format("File %s already exists!", this.file.getPath()));
        }
        this.file.createNewFile();
    }

    @Override
    public void rename(String name) throws FileAlreadyExistsException {
        java.io.File newFile = new java.io.File(this.file.getParent() + java.io.File.separator + name);
        if(newFile.exists()) {
            throw new FileAlreadyExistsException(String.format("File %s already exists!", newFile.getPath()));
        }
        if(this.file.exists() && this.file.isFile()) {
            this.file.renameTo(newFile);
            this.file = newFile;
        }
    }

    @Override
    public void move(String destination, boolean copy) throws IOException {
        if(!this.file.exists()) {
            throw new FileNotFoundException(String.format("File %s not found!", this.file.getPath()));
        }
        if(!this.file.getPath().equals(destination + java.io.File.separator + this.file.getName())) {
            java.io.File copiedFile = new java.io.File(destination + java.io.File.separator + this.file.getName());
            if(copiedFile.exists()) {
                throw new FileAlreadyExistsException(String.format("File %s already exists!", copiedFile.getPath()));
            }
            InputStream in = new BufferedInputStream(new FileInputStream(this.file));
            OutputStream out = new BufferedOutputStream(new FileOutputStream(copiedFile));
            byte[] buffer = new byte[1024];
            int lengthRead;
            while((lengthRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, lengthRead);
                out.flush();
            }
            if(!copy) {
                this.delete();
            }
        }
    }

    @Override
    public long getLastTimeModified() {
        return this.file.lastModified();
    }

    @Override
    public java.io.File getContainedFile() {
        return file;
    }
}
