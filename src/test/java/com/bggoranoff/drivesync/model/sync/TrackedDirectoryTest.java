package com.bggoranoff.drivesync.model.sync;

import com.google.api.services.drive.model.File;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

public class TrackedDirectoryTest {
    @Test
    public void mockedUploadShouldHandleValidData() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doNothing().when(mockDir).upload("123pjks92");
        mockDir.upload("123pjks92");
        verify(mockDir, times(1)).upload("123pjks92");
    }

    @Test(expected = IOException.class)
    public void mockedUploadShouldThrowIOExceptionOnInvalidPath() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doNothing().when(mockDir).setLocalFile("trash");
        mockDir.setLocalFile("trash");
        verify(mockDir, times(1)).setLocalFile("trash");
        doThrow(new IOException("Invalid file path!")).when(mockDir).upload("123pjks92");
        mockDir.upload("123pjks92");
    }

    @Test(expected = IOException.class)
    public void mockedUploadShouldThrowIOExceptionOnInvalidID() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doThrow(new IOException("Invalid parent ID!")).when(mockDir).upload("trash");
        mockDir.upload("trash");
    }

    @Test
    public void mockedListFilesShouldHandleValidData() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doReturn(new ArrayList<File>(){
            {
                add(new File());
            }
        }).when(mockDir).listFiles();
        List<File> filesList = mockDir.listFiles();
        verify(mockDir, times(1)).listFiles();
        assertEquals(1, filesList.size());
    }

    @Test(expected = IOException.class)
    public void mockedListFilesShouldThrowIOExceptionOnOperationError() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doThrow(new IOException("Error listing files!")).when(mockDir).listFiles();
        mockDir.listFiles();
    }

    @Test
    public void mockedListFilesShouldReturnEmptyListOnInvalidID() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        mockDir.setFileId("trash");
        verify(mockDir, times(1)).setFileId("trash");
        doReturn(new ArrayList<File>()).when(mockDir).listFiles();
        List<File> filesList = mockDir.listFiles();
        assertEquals(0, filesList.size());
    }

    @Test
    public void mockedListDirsShouldHandleValidData() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doReturn(new ArrayList<File>(){
            {
                add(new File());
            }
        }).when(mockDir).listDirectories();
        List<File> dirsList = mockDir.listDirectories();
        verify(mockDir, times(1)).listDirectories();
        assertEquals(1, dirsList.size());
    }

    @Test(expected = IOException.class)
    public void mockedListDirsShouldThrowIOExceptionOnOperationError() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doThrow(new IOException("Error listing directories!")).when(mockDir).listDirectories();
        mockDir.listDirectories();
    }

    @Test
    public void mockedListDirsShouldReturnEmptyListOnInvalidID() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        mockDir.setFileId("trash");
        verify(mockDir, times(1)).setFileId("trash");
        doReturn(new ArrayList<File>()).when(mockDir).listFiles();
        List<File> dirsList = mockDir.listFiles();
        assertEquals(0, dirsList.size());
    }

    @Test
    public void  mockedDeleteShouldHandleValidData() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doNothing().when(mockDir).delete();
        mockDir.delete();
        verify(mockDir, times(1)).delete();
    }

    @Test(expected = IOException.class)
    public void mockedDeleteShouldThrowIOExceptionOnInvalidID() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        mockDir.setFileId("trash");
        doThrow(new IOException("Invalid directory ID!")).when(mockDir).delete();
        mockDir.delete();
    }

    @Test
    public void mockedReadShouldHandleValidData() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        doNothing().when(mockDir).read("/valid/file/path");
        mockDir.read("/valid/file/path");
        verify(mockDir, times(1)).read("/valid/file/path");
    }

    @Test(expected = IOException.class)
    public void mockedReadShouldThrowIOExceptionOnInvalidID() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        mockDir.setFileId("trash");
        doThrow(new IOException("Invalid directory ID!")).when(mockDir).read("/valid/file/path");
        mockDir.read("/valid/file/path");
    }

    @Test(expected = IOException.class)
    public void mockedReadShouldThrowIOExceptionOnInvalidPath() throws IOException {
        TrackedDirectory mockDir = mock(TrackedDirectory.class);
        mockDir.setFileId("123pjks92");
        doThrow(new IOException("Invalid destination path!")).when(mockDir).read("uwoeiruow1203+++///");
        mockDir.read("uwoeiruow1203+++///");
    }
}
