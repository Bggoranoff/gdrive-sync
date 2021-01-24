package com.bggoranoff.drivesync.model.sync;

import com.google.api.client.http.FileContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;

public class TrackedFile extends TrackedEntityImpl {
    public TrackedFile(Drive driveService, String filePath, String fileId) {
        super(driveService, filePath, fileId);
    }

    @Override
    public void upload(String parentId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(this.localFile.getContainedFile().getName());
        fileMetadata.setParents(Collections.singletonList(parentId));
        fileMetadata.setModifiedTime(new DateTime(this.localFile.getLastTimeModified()));
        FileContent mediaContent = new FileContent(null, this.localFile.getContainedFile());
        String currentId = this.driveService.files().create(fileMetadata, mediaContent)
                .setFields("id")
                .execute()
                .getId();
        this.setFileId(currentId);
    }

    @Override
    public void delete() throws IOException {
        driveService.files().delete(this.fileId)
                .execute();
    }

    @Override
    public void read(String destination) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        File cloudFile = driveService.files().get(this.fileId)
                .execute();
        this.setLocalFile(destination + java.io.File.separator + cloudFile.getName());
        driveService.files().get(this.fileId)
                .executeMediaAndDownloadTo(outputStream);
        outputStream.writeTo(new FileOutputStream(this.localFile.getContainedFile()));
        outputStream.close();
    }

    @Override
    public void setLocalFile(String filePath) {
        this.localFile = new com.bggoranoff.drivesync.model.filesystem.File(filePath);
    }

    @Override
    public void setDriveService(Drive driveService) {
        this.driveService = driveService;
    }
}
