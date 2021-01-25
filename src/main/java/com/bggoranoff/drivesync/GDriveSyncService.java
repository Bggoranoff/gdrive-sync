package com.bggoranoff.drivesync;

import com.bggoranoff.drivesync.model.auth.GDriveAuthService;
import com.bggoranoff.drivesync.model.filesystem.Directory;
import com.bggoranoff.drivesync.model.sync.TrackedDirectory;
import com.bggoranoff.drivesync.model.sync.TrackedEntityImpl;
import com.bggoranoff.drivesync.model.sync.TrackedFile;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GDriveSyncService {
    private GDriveAuthService driveAuthService;
    private Directory rootDir;
    private String applicationName;
    private String credentialsPath;
    private String tokensPath;
    private long lastTimeSynced;

    public GDriveSyncService(String applicationName, String credentialsPath, String tokensPath, String rootPath) throws GeneralSecurityException, IOException {
        this.driveAuthService = new GDriveAuthService();
        this.setApplicationName(applicationName);
        this.setCredentialsPath(credentialsPath);
        this.setTokensPath(tokensPath);
        this.setRootDir(rootPath);
        this.driveAuthService.activateDriveService(this.applicationName, this.credentialsPath, this.tokensPath);
        this.lastTimeSynced = 0;
    }

    public void syncFolderWithCloud(String folderPath) throws IOException, NullPointerException, ParseException {
        String parentId = "root";
        Directory dirToSync = new Directory(folderPath);
        String currentId = getFileId(parentId, dirToSync.getContainedFile().getName());
        TrackedDirectory trackedDirToSync = new TrackedDirectory(this.getDriveAuthService().getDriveService(), folderPath, currentId);
        if(currentId == null) {
            trackedDirToSync.upload(parentId);
        }
        syncFolderWithCloud(trackedDirToSync);
    }

    private void syncFolderWithCloud(TrackedDirectory currentDirectory) throws IOException, ParseException {
        List<File> filesList = currentDirectory.listFiles();
        List<File> dirsList = currentDirectory.listDirectories();
        assert filesList != null;
        assert dirsList != null;
        List<java.io.File> localFiles = Arrays.asList(Objects.requireNonNull(currentDirectory.getLocalFile().getContainedFile().listFiles()));
        for(java.io.File child : localFiles) {
            if(child.isDirectory()) {
                String foundFolderId = this.getFileId(currentDirectory.getFileId(), child.getName());
                Directory childDir = new Directory(child.getPath());
                if(foundFolderId == null && this.getLastTimeSynced() > childDir.getLastTimeModified()) {
                    childDir.delete();
                } else {
                    TrackedDirectory trackedChildDir = new TrackedDirectory(this.getDriveAuthService().getDriveService(), childDir.getContainedFile().getPath(), foundFolderId);
                    if(foundFolderId == null) {
                        trackedChildDir.upload(currentDirectory.getFileId());
                    }
                    syncFolderWithCloud(trackedChildDir);
                }
            } else {
                String foundFileId = this.getFileId(currentDirectory.getFileId(), child.getName());
                TrackedFile trackedChildFile = new TrackedFile(this.getDriveAuthService().getDriveService(), child.getPath(), foundFileId);
                if(foundFileId == null) {
                    if(this.getLastTimeSynced() < trackedChildFile.getLocalFile().getLastTimeModified()) {
                        trackedChildFile.upload(currentDirectory.getFileId());
                    } else {
                        trackedChildFile.getLocalFile().delete();
                    }
                } else if(trackedChildFile.getLocalFile().getLastTimeModified() > this.getLastTimeSynced()) {
                    trackedChildFile.delete();
                    trackedChildFile.upload(currentDirectory.getFileId());
                } else if(trackedChildFile.getLocalFile().getLastTimeModified() < this.getLastTimeSynced()) {
                    String dest = trackedChildFile.getLocalFile().getContainedFile().getParent();
                    trackedChildFile.getLocalFile().delete();
                    trackedChildFile.read(dest);
                }
            }
        }
        syncNonExistentFiles(filesList, localFiles, new TrackedFile(this.getDriveAuthService().getDriveService(), null, null), currentDirectory);
        syncNonExistentFiles(dirsList, localFiles, new TrackedDirectory(this.getDriveAuthService().getDriveService(), null, null), currentDirectory);
    }

    private void syncNonExistentFiles(List<File> filesList, List<java.io.File> localFiles, TrackedEntityImpl trackedEntity, TrackedDirectory currentDirectory) throws IOException, ParseException {
        for(File file : filesList) {
            boolean existsLocally = false;
            trackedEntity.setFileId(file.getId());
            for(java.io.File localChild : localFiles) {
                if(file.getName().equals(localChild.getName()) && !localChild.isDirectory()) {
                    existsLocally = true;
                    break;
                }
            }
            if(!existsLocally) {
                if(this.getLastTimeSynced() < trackedEntity.getCreationTime()) {
                    String dest = currentDirectory.getLocalFile().getContainedFile().getPath();
                    trackedEntity.read(dest);
                } else {
                    trackedEntity.delete();
                }
            }
        }
    }

    public String getFileId(String parentId, String fileName) throws IOException {
        List<File> filesList = driveAuthService.getDriveService().files()
                .list()
                .setQ(String.format("'%s' in parents and name = '%s'", parentId, fileName))
                .setSpaces("drive")
                .setFields("nextPageToken, files(id)")
                .execute()
                .getFiles();
        return filesList.isEmpty() ? null : filesList.get(0).getId();
    }

    public GDriveAuthService getDriveAuthService() {
        return driveAuthService;
    }

    public void setDriveAuthService(GDriveAuthService driveAuthService) {
        this.driveAuthService = driveAuthService;
    }

    public Directory getRootDir() {
        return this.rootDir;
    }

    public void setRootDir(String folderPath) {
        this.setLastTimeSynced(0);
        this.rootDir = new Directory(folderPath);
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getCredentialsPath() {
        return this.credentialsPath;
    }

    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    public String getTokensPath() {
        return this.tokensPath;
    }

    public void setTokensPath(String tokensPath) {
        this.tokensPath = tokensPath;
    }

    public long getLastTimeSynced() {
        return this.lastTimeSynced;
    }

    public void setLastTimeSynced(long lastTimeSynced) {
        this.lastTimeSynced = lastTimeSynced;
    }
}
