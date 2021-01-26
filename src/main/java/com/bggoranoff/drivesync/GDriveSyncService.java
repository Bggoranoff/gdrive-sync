package com.bggoranoff.drivesync;

import com.bggoranoff.drivesync.model.auth.GDriveAuthService;
import com.bggoranoff.drivesync.model.filesystem.Directory;
import com.bggoranoff.drivesync.model.sync.TrackedDirectory;
import com.bggoranoff.drivesync.model.sync.TrackedEntityImpl;
import com.bggoranoff.drivesync.model.sync.TrackedFile;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.*;

public class GDriveSyncService {
    private GDriveAuthService driveAuthService;
    private Directory rootDir;
    private String applicationName;
    private String credentialsPath;
    private String tokensPath;
    private long lastTimeSynced;
    private long currentTimeSync = 0;

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
        this.currentTimeSync = new Date().getTime();
        String parentId = "root";
        Directory dirToSync = new Directory(folderPath);
        String currentId = getFileId(parentId, dirToSync.getContainedFile().getName());
        TrackedDirectory trackedDirToSync = new TrackedDirectory(this.getDriveAuthService().getDriveService(), folderPath, currentId);
        if(currentId == null) {
            System.out.println("CREATING DIR IN DRIVE");
            trackedDirToSync.upload(parentId);
        }
        syncFolderWithCloud(trackedDirToSync);
        setLastTimeSynced(currentTimeSync);
    }

    private void syncFolderWithCloud(TrackedDirectory currentDirectory) throws IOException, ParseException {
        List<File> filesList = currentDirectory.listFiles();
        List<File> dirsList = currentDirectory.listDirectories();
        assert filesList != null;
        assert dirsList != null;
        java.io.File[] localFilesArray = currentDirectory.getLocalFile().getContainedFile().listFiles();
        List<java.io.File> localFiles = new ArrayList<>();
        if(localFilesArray != null) {
            localFiles.addAll(Arrays.asList(localFilesArray));
        }
        for(java.io.File child : localFiles) {
            if(child.isDirectory()) {
                String foundFolderId = this.getFileId(currentDirectory.getFileId(), child.getName());
                TrackedDirectory trackedChildDir = new TrackedDirectory(this.getDriveAuthService().getDriveService(), child.getPath(), foundFolderId);
                if(foundFolderId == null) {
                    System.out.println("UPLOADING LOCAL DIR");
                    trackedChildDir.upload(currentDirectory.getFileId());
                    new java.io.File(trackedChildDir.getLocalFile().getContainedFile().getPath()).setLastModified(this.currentTimeSync);
                }
                syncFolderWithCloud(trackedChildDir);
            } else {
                String foundFileId = this.getFileId(currentDirectory.getFileId(), child.getName());
                TrackedFile trackedChildFile = new TrackedFile(this.getDriveAuthService().getDriveService(), child.getPath(), foundFileId);
                if(foundFileId == null) {
                    System.out.println("UPLOADING LOCAL FILE");
                    trackedChildFile.upload(currentDirectory.getFileId());
                    new java.io.File(trackedChildFile.getLocalFile().getContainedFile().getPath()).setLastModified(this.currentTimeSync);
                } else if(trackedChildFile.getLocalFile().getLastTimeModified() > trackedChildFile.getCreationTime()) {
                    System.out.println("UPDATING CLOUD FILE");
                    trackedChildFile.delete();
                    trackedChildFile.upload(currentDirectory.getFileId());
                    new java.io.File(trackedChildFile.getLocalFile().getContainedFile().getPath()).setLastModified(this.currentTimeSync);
                } else if(trackedChildFile.getLocalFile().getLastTimeModified() < trackedChildFile.getCreationTime()) {
                    System.out.println("UPDATING LOCAL FILE");
                    String dest = trackedChildFile.getLocalFile().getContainedFile().getParent();
                    trackedChildFile.getLocalFile().delete();
                    trackedChildFile.read(dest);
                }
            }
        }
        syncNonExistentFiles(filesList, localFiles, new TrackedFile(this.getDriveAuthService().getDriveService(), "", null), currentDirectory);
        syncNonExistentFiles(dirsList, localFiles, new TrackedDirectory(this.getDriveAuthService().getDriveService(), "", null), currentDirectory);
    }

    private void syncNonExistentFiles(List<File> filesList, List<java.io.File> localFiles, TrackedEntityImpl trackedEntity, TrackedDirectory currentDirectory) throws IOException, ParseException {
        for(File file : filesList) {
            boolean existsLocally = false;
            trackedEntity.setFileId(file.getId());
            System.out.println(file.getName());
            for(java.io.File localChild : localFiles) {
                if(file.getName().equals(localChild.getName())) {
                    existsLocally = true;
                    break;
                }
            }
            if(!existsLocally) {
                if(this.getLastTimeSynced() < trackedEntity.getCreationTime()) {
                    System.out.println("DOWNLOADING FILE FROM CLOUD");
                    String dest = currentDirectory.getLocalFile().getContainedFile().getPath();
                    trackedEntity.read(dest);
                } else {
                    System.out.println("DELETING FILE FROM CLOUD");
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
