package com.bggoranoff.drivesync.model.filesystem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

public class Directory implements FileSystemEntity{
    private java.io.File file;

    public Directory(String directoryPath) {
        this.file = new java.io.File(directoryPath);
    }

    @Override
    public void delete() throws FileNotFoundException {
        if(!file.exists()) {
            throw new FileNotFoundException(String.format("Folder %s not found!", this.file.getPath()));
        }
        String[] fileEntries = this.file.list();
        if(fileEntries != null) {
            for(String entry : fileEntries) {
                java.io.File child = new java.io.File(this.file.getPath(), entry);
                if(child.isDirectory()) {
                    new Directory(child.getPath()).delete();
                } else {
                    new File(child.getPath()).delete();
                }
            }
        }
        this.file.delete();
    }

    @Override
    public void create() throws IOException {
        if(this.file.exists()) {
            throw new FileAlreadyExistsException(String.format("Folder %s already exists!", this.file.getPath()));
        }
        this.file.mkdir();
    }

    @Override
    public void rename(String name) throws IOException {
        if(!this.file.exists()) {
            throw new FileNotFoundException(String.format("Folder %s does not exist!", this.file.getPath()));
        }
        java.io.File newFolder = new java.io.File(this.file.getParent() + java.io.File.separator + name);
        if(newFolder.exists()) {
            throw new FileAlreadyExistsException(String.format("File %s already exists!", newFolder.getPath()));
        }
        if(this.file.exists() && this.file.isDirectory()) {
            this.file.renameTo(newFolder);
            this.file = newFolder;
        }
    }

    @Override
    public void move(String destination, boolean copy) throws IOException {
        if(!this.file.exists()) {
            throw new FileNotFoundException(String.format("File %s does not exist!", this.file.getPath()));
        }
        String destinationPath = destination + java.io.File.separator + this.file.getName();
        if(!this.file.getPath().equals(destinationPath)) {
            Directory newFolder = new Directory(destinationPath);
            newFolder.create();
            String[] entries = this.file.list();
            if(entries != null) {
                for(String entry : entries) {
                    java.io.File child = new java.io.File(this.file.getPath(), entry);
                    if(child.isDirectory()) {
                        new Directory(child.getPath()).move(destinationPath, copy);
                    } else {
                        new File(child.getPath()).move(destinationPath, copy);
                    }
                }
            }
            if(!copy) {
                this.delete();
            }
        }
    }

    @Override
    public long getLastTimeModified() {
        Path folder = Paths.get(this.file.getPath());
        try {
            BasicFileAttributes attributes = Files.readAttributes(folder, BasicFileAttributes.class);
            return attributes.creationTime().toMillis();
        } catch(IOException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    @Override
    public java.io.File getContainedFile() {
        return this.file;
    }
}
