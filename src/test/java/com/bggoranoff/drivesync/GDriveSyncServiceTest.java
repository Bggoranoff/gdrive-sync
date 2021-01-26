package com.bggoranoff.drivesync;

import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class GDriveSyncServiceTest {
    @Test
    public void mockedGetFileIdShouldHandleValidInput() throws IOException {
        GDriveSyncService mockService = mock(GDriveSyncService.class);
        doReturn("123pjks92").when(mockService).getFileId("123pjks91", "existingFile.txt");
        String result = mockService.getFileId("123pjks91", "existingFile.txt");
        verify(mockService, times(1)).getFileId("123pjks91", "existingFile.txt");
        assertEquals("123pjks92", result);
    }

    @Test
    public void mockedGetFileIdShouldReturnNullOnNonExistentFile() throws IOException {
        GDriveSyncService mockService = mock(GDriveSyncService.class);
        doReturn(null).when(mockService).getFileId("123pjks91", "nonExistingFile.txt");
        String result = mockService.getFileId("123pjks91", "nonExistingFile.txt");
        verify(mockService, times(1)).getFileId("123pjks91", "nonExistingFile.txt");
        assertNull(result);
    }

    @Test(expected = IOException.class)
    public void mockedGetFileIdShouldThrowIOExceptionOnInvalidParentID() throws IOException {
        GDriveSyncService mockService = mock(GDriveSyncService.class);
        doThrow(new IOException("Invalid parent ID!")).when(mockService).getFileId("trash", "nonExistingFile.txt");
        mockService.getFileId("trash", "nonExistingFile.txt");
    }

    @Test
    public void mockedSyncFolderWithCloudShouldHandleValidInput() throws IOException, ParseException {
        GDriveSyncService mockService = mock(GDriveSyncService.class);
        doNothing().when(mockService).setRootDir("/valid/root/path");
        mockService.setRootDir("/valid/root/path");
        doNothing().when(mockService).syncFolderWithCloud();
        mockService.syncFolderWithCloud();
        verify(mockService, times(1)).syncFolderWithCloud();
    }

    @Test(expected = IOException.class)
    public void mockedSyncFolderWithCloudShouldThrowIOExceptionOnInvalidFolder() throws IOException, ParseException {
        GDriveSyncService mockService = mock(GDriveSyncService.class);
        doNothing().when(mockService).setRootDir("trash");
        mockService.setRootDir("trash");
        doThrow(new IOException("Invalid folder path!")).when(mockService).syncFolderWithCloud();
        mockService.syncFolderWithCloud();
    }
}
