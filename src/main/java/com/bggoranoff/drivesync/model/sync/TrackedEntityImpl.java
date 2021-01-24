package com.bggoranoff.drivesync.model.sync;

import com.bggoranoff.drivesync.model.filesystem.FileSystemEntity;
import com.google.api.services.drive.Drive;

public abstract class TrackedEntityImpl implements TrackedEntity {
    FileSystemEntity localFile;
    String fileId;
    Drive driveService;

    public TrackedEntityImpl(Drive driveService, String filePath, String fileId) {
        this.setDriveService(driveService);
        this.setFileId(fileId);
        this.setLocalFile(filePath);
    }

    public void setFileId(String id) {
        this.fileId = id;
    }

    public String getFileId() {
        return this.fileId;
    }

    public FileSystemEntity getLocalFile() {
        return this.localFile;
    }

    public void setDriveService(Drive driveService) {
        this.driveService = driveService;
    }

    abstract void setLocalFile(String filePath);
}
