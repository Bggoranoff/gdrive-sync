package com.bggoranoff.drivesync.model.filesystem;

import org.junit.Test;

import static org.junit.Assert.*;
import com.bggoranoff.drivesync.model.filesystem.File;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Objects;

public class FileTest {
    private String getResourcePath(String fileName) {
        return Objects.requireNonNull(FileTest.class.getClassLoader().getResource("filesystem")).getPath() + java.io.File.separator + fileName;
    }

    @Test
    public void createFileShouldPerformValidFileCreation() throws IOException {
        File file = new File(getResourcePath("fileCreated.txt"));
        file.create();
        file.delete();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createFileShouldThrowFileAlreadyExistsException() throws IOException {
        File file = new File(getResourcePath("testRepetitiveCreation.txt"));
        file.create();
        File repetitiveFile = new File(getResourcePath("testRepetitiveCreation.txt"));
        try {
            repetitiveFile.create();
        } finally {
            file.delete();
        }
    }

    @Test
    public void deleteFileShouldPerformValidFileDeletion() throws IOException {
        File file = new File(getResourcePath("testFileDeleted.txt"));
        file.create();
        file.delete();
    }

    @Test(expected = FileNotFoundException.class)
    public void deleteFileShouldThrowFileNotFoundException() throws FileNotFoundException {
        File file = new File(getResourcePath("testFileNotFound.txt"));
        file.delete();
    }

    @Test
    public void renameFileShouldPerformValidFileRenaming() throws IOException {
        File file = new File(getResourcePath("testFileRenamed.txt"));
        file.create();
        file.rename("anotherName.txt");
        file.delete();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void renameFileShouldThrowFileAlreadyExistsException() throws IOException {
        File file = new File(getResourcePath("renameFail.txt"));
        File existingFile = new File(getResourcePath("anotherNameFail.txt"));
        existingFile.create();
        file.create();
        try {
            file.rename("anotherNameFail.txt");
        } finally {
            file.delete();
            existingFile.delete();
        }
    }

    @Test
    public void moveFileShouldCopyFileSuccessfully() throws IOException {
        File file = new File(getResourcePath("fileToCopy.txt"));
        file.create();
        file.move(getResourcePath("filemove"), true);
        java.io.File copiedFile = new java.io.File(getResourcePath("filemove/fileToCopy.txt"));
        java.io.File originalFile = file.getContainedFile();
        assertTrue(originalFile.exists() && copiedFile.exists());
        originalFile.delete();
        copiedFile.delete();
    }

    @Test
    public void moveFileShouldPerformValidFileMoving() throws IOException {
        File file = new File(getResourcePath("fileToMove.txt"));
        file.create();
        file.move(getResourcePath("filemove"), false);
        java.io.File copiedFile = new java.io.File(getResourcePath("filemove/fileToMove.txt"));
        java.io.File originalFile = file.getContainedFile();
        assertTrue(!originalFile.exists() && copiedFile.exists());
        copiedFile.delete();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void moveFileShouldThrowFileAlreadyExistsException() throws IOException {
        File file = new File(getResourcePath("failedMove.txt"));
        file.create();
        System.out.println(file.getContainedFile().exists());
        File repetitiveFile = new File(getResourcePath("filemove/failedMove.txt"));
        repetitiveFile.create();
        try {
            file.move(repetitiveFile.getContainedFile().getParent(), false);
        } finally {
            file.delete();
            repetitiveFile.delete();
        }
    }
}
