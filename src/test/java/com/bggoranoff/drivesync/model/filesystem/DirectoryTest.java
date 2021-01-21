package com.bggoranoff.drivesync.model.filesystem;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Objects;

public class DirectoryTest {
    private String getResourcePath(String fileName) {
        return Objects.requireNonNull(FileTest.class.getClassLoader().getResource("filesystem")).getPath() + java.io.File.separator + fileName;
    }

    @Test
    public void createDirectoryShouldCreateAnEmptyDirectory() throws IOException {
        Directory dir = new Directory(getResourcePath("createdFolder"));
        dir.create();
        assertTrue(dir.getContainedFile().exists());
        dir.delete();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createDirectoryShouldThrowFileAlreadyExistsException() throws IOException {
        Directory dir = new Directory(getResourcePath("repetitiveDirTest"));
        Directory repetitiveDir = new Directory(getResourcePath("repetitiveDirTest"));
        dir.create();
        try {
            repetitiveDir.create();
        } finally {
            dir.delete();
        }
    }

    @Test
    public void deleteDirectoryShouldDeleteAnEmptyDirectory() throws IOException {
        Directory dir = new Directory(getResourcePath("deletedFolder"));
        dir.create();
        dir.delete();
        assertFalse(dir.getContainedFile().exists());
    }

    @Test
    public void deleteDirectoryShouldDeleteADirectoryWithFiles() throws IOException {
        Directory dir = new Directory(getResourcePath("filledDeletedFolder"));
        dir.create();
        for(int i = 0; i < 20; i++) {
            new File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).create();
        }
        dir.delete();
        assertFalse(dir.getContainedFile().exists());
        for(int i = 0; i < 20; i++) {
            File child = new File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i);
            assertFalse(child.getContainedFile().exists());
        }
    }

    @Test(expected = FileNotFoundException.class)
    public void deleteDirectoryShouldThrowFileNotFoundException() throws FileNotFoundException {
        Directory dir = new Directory(getResourcePath("nonExistentFolderDeleted"));
        dir.delete();
    }

    @Test
    public void renameDirectoryShouldChangeDirName() throws IOException {
        Directory dir = new Directory(getResourcePath("folderToRename"));
        dir.create();
        dir.rename("folderRenamed");
        assertEquals("folderRenamed", dir.getContainedFile().getName());
        dir.delete();
    }

    @Test
    public void renameDirectoryShouldNotAlterDirContent() throws IOException {
        Directory dir = new Directory(getResourcePath("folderWithContentToRename"));
        dir.create();
        for(int i = 0; i < 20; i++) {
            new File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).create();
        }
        String oldPath = dir.getContainedFile().getPath();
        dir.rename("folderWithContentRenamed");
        assertTrue(dir.getContainedFile().exists());
        assertFalse(new java.io.File(oldPath).exists());
        for(int i = 0; i < 20; i++) {
            File child = new File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i);
            assertTrue(child.getContainedFile().exists());
            File oldChild = new File(oldPath + java.io.File.separator + "fileTest_" + i);
            assertFalse(oldChild.getContainedFile().exists());
        }
        dir.delete();
    }

    @Test(expected = FileNotFoundException.class)
    public void renameDirectoryShouldThrowFileNotFoundException() throws IOException {
        Directory dir = new Directory(getResourcePath("nonexistentDirToRename"));
        dir.rename("nonexistentDirRenamed");
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void renameDirectoryShouldThrowFileAlreadyExistsException() throws IOException {
        Directory dir = new Directory(getResourcePath("repetitiveDirectoryToRename"));
        dir.create();
        Directory repetitiveDir = new Directory(getResourcePath("repetitiveDirectoryRenamed"));
        repetitiveDir.create();
        try {
            dir.rename("repetitiveDirectoryRenamed");
        } finally {
            dir.delete();
            repetitiveDir.delete();
        }
    }

    @Test
    public void moveDirectoryShouldCopyDirectory() throws IOException {
        Directory dir = new Directory(getResourcePath("directoryToCopy"));
        dir.create();
        for(int i = 0; i < 20; i++) {
            new File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).create();
        }
        dir.move(getResourcePath("filemove"), true);
        Directory copiedDir = new Directory(getResourcePath("filemove") + java.io.File.separator + dir.getContainedFile().getName());
        assertTrue(dir.getContainedFile().exists());
        assertTrue(copiedDir.getContainedFile().exists());
        for(int i = 0; i < 20; i++) {
            assertTrue(new java.io.File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).exists());
            assertTrue(new java.io.File(copiedDir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).exists());
        }
        dir.delete();
        copiedDir.delete();
    }

    @Test
    public void moveDirectoryShouldChangeDirLocation() throws IOException {
        Directory dir = new Directory(getResourcePath("directoryToMoveSuccessfully"));
        dir.create();
        for(int i = 0; i < 20; i++) {
            new File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).create();
        }
        dir.move(getResourcePath("filemove"), false);
        Directory movedDir = new Directory(getResourcePath("filemove") + java.io.File.separator + dir.getContainedFile().getName());
        assertFalse(dir.getContainedFile().exists());
        assertTrue(movedDir.getContainedFile().exists());
        for(int i = 0; i < 20; i++) {
            assertFalse(new java.io.File(dir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).exists());
            assertTrue(new java.io.File(movedDir.getContainedFile().getPath() + java.io.File.separator + "fileTest_" + i).exists());
        }
        movedDir.delete();
    }

    @Test(expected = FileNotFoundException.class)
    public void moveDirectoryShouldThrowFileNotFoundException() throws IOException {
        Directory dir = new Directory(getResourcePath("nonexistentDirToMove"));
        dir.move(getResourcePath("filemove"), false);
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void moveDirectoryShouldThrowFileAlreadyExistsException() throws IOException {
        Directory dir = new Directory(getResourcePath("repetitiveDirToMove"));
        Directory repetitiveDir = new Directory(getResourcePath("filemove/repetitiveDirToMove"));
        dir.create();
        repetitiveDir.create();
        try {
            dir.move(getResourcePath("filemove"), false);
        } finally {
            dir.delete();
            repetitiveDir.delete();
        }
    }
}

