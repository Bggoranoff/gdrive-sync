package com.bggoranoff.drivesync.model.sync;

import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class TrackedFileTest {
    @Test
    public void mockedUploadShouldHandleExistingId() throws IOException {
        TrackedFile mockFile = mock(TrackedFile.class);
        doNothing().when(mockFile).upload("123pjks92");
        mockFile.upload("123pjks92");
        verify(mockFile, times(1)).upload("123pjks92");
    }

    @Test(expected = IOException.class)
    public void mockedUploadShouldThrowIOExceptionOnInvalidInput() throws IOException {
        TrackedFile mockFile = mock(TrackedFile.class);
        doThrow(new IOException("Invalid ID!")).when(mockFile).upload("trash");
        mockFile.upload("trash");
    }

    @Test
    public void mockedDeleteShouldHandleExistingId() throws IOException {
        TrackedFile mockFile = mock(TrackedFile.class);
        mockFile.setFileId("123pjks92");
        verify(mockFile, times(1)).setFileId("123pjks92");
        doNothing().when(mockFile).delete();
        mockFile.delete();
        verify(mockFile, times(1)).delete();
    }

    @Test(expected = IOException.class)
    public void mockedDeleteShouldThrowIOExceptionOnInvalidData() throws IOException {
        TrackedFile mockFile = mock(TrackedFile.class);
        mockFile.setFileId("trash");
        verify(mockFile, times(1)).setFileId("trash");
        doThrow(new IOException("Invalid ID!")).when(mockFile).delete();
        mockFile.delete();
    }

    @Test
    public void mockedReadShouldHandleExistingId() throws IOException {
        TrackedFile mockFile = mock(TrackedFile.class);
        doNothing().when(mockFile).read("/valid/dest/path");
        mockFile.read("/valid/dest/path");
        verify(mockFile, times(1)).read("/valid/dest/path");
    }

    @Test(expected = IOException.class)
    public void mockedReadShouldThrowIOExceptionOnInvalidInput() throws IOException {
        TrackedFile mockFile = mock(TrackedFile.class);
        doThrow(new IOException("Invalid path!")).when(mockFile).read("jdslfk+2$$£?///");
        mockFile.read("jdslfk+2$$£?///");
    }

    @Test(expected = IOException.class)
    public void mockedReadShouldThrowIOExceptionOnInvalidData() throws IOException {
        TrackedFile mockFile = mock(TrackedFile.class);
        mockFile.setFileId("trash");
        verify(mockFile, times(1)).setFileId("trash");
        doThrow(new IOException("Invalid ID!")).when(mockFile).read("/valid/dest/path");
        mockFile.read("/valid/dest/path");
    }
}
