package com.bggoranoff.drivesync.model.auth;

import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.mockito.Mockito.*;

public class GDriveAuthServiceTest {
    @Test
    public void mockedActivateDriveServiceShouldConfigureServiceSuccessfullyOnValidCredentials() throws GeneralSecurityException, IOException {
        GDriveAuthService mockService = mock(GDriveAuthService.class);
        doNothing().when(mockService).activateDriveService("app", "/valid/credentials", "/valid/tokens/path");
        mockService.activateDriveService("app", "/valid/credentials", "/valid/tokens/path");
        verify(mockService, times(1)).activateDriveService("app", "/valid/credentials", "/valid/tokens/path");
    }

    @Test(expected = IOException.class)
    public void mockedActivateDriveServiceShouldThrowIOExceptionOnInvalidArguments() throws GeneralSecurityException, IOException {
        GDriveAuthService mockService = mock(GDriveAuthService.class);
        doThrow(new IOException("Invalid credentials path!")).when(mockService).activateDriveService("app", "/invalid/credentials", "/valid/tokens/path");
        mockService.activateDriveService("app", "/invalid/credentials", "/valid/tokens/path");
    }

    @Test
    public void mockedActivateDriveServiceShouldThrowGeneralSecurityExceptionOnInvalidCredentials() throws GeneralSecurityException, IOException {
        GDriveAuthService mockService = mock(GDriveAuthService.class);
        doThrow(new GeneralSecurityException("Invalid credentials!")).when(mockService).activateDriveService("app", "/valid/wrong_credentials", "/valid/tokens/path");
        mockService.activateDriveService("app", "/valid/wrong_credentials", "/invalid/tokens/path");
    }
}
