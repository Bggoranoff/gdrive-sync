package com.bggoranoff.drivesync.model.sync;

import com.bggoranoff.drivesync.model.filesystem.FileSystemEntity;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public abstract class TrackedEntityImpl implements TrackedEntity {
    FileSystemEntity localFile;
    String fileId;
    Drive driveService;

    public TrackedEntityImpl(Drive driveService, String filePath, String fileId) {
        this.setDriveService(driveService);
        this.setFileId(fileId);
        this.setLocalFile(filePath);
    }

    public long getCreationTime() throws IOException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        File file = this.driveService.files().get(this.getFileId())
                .execute();
        dateFormat.setTimeZone(TimeZone.getTimeZone(TimeZone.getAvailableIDs(file.getCreatedTime().getTimeZoneShift())[0]));
        return dateFormat.parse(file.getCreatedTime().toString()).getTime();
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
