package com.bggoranoff.drivesync.model.sync;

import com.google.api.client.util.DateTime;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class TrackedDirectory extends TrackedEntityImpl {
    public TrackedDirectory(Drive driveService, String filePath, String fileId) {
        super(driveService, filePath, fileId);
    }

    @Override
    public void upload(String parentId) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(this.localFile.getContainedFile().getName());
        fileMetadata.setParents(Collections.singletonList(parentId));
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setCreatedTime(new DateTime(this.localFile.getLastTimeModified()));
        File result = driveService.files().create(fileMetadata)
                .setFields("id, parents")
                .execute();
        this.setFileId(result.getId());
    }

    @Override
    public void delete() throws IOException {
        List<File> filesList = this.listFiles();
        List<File> dirsList = this.listDirectories();
        assert filesList != null;
        assert dirsList != null;
        for(File file : filesList) {
            new TrackedFile(driveService, this.getLocalFile().getContainedFile().getPath() + java.io.File.separator + file.getName(), file.getId()).delete();
        }
        for(File dir : dirsList) {
            new TrackedDirectory(driveService, this.getLocalFile().getContainedFile().getPath() + java.io.File.separator + dir.getName(), dir.getId()).delete();
        }
        this.driveService.files().delete(this.getFileId())
                .execute();
    }

    @Override
    public void read(String destination) throws IOException {
        File fileMetadata = this.driveService.files().get(this.getFileId())
                .execute();
        this.setLocalFile(destination + java.io.File.separator + fileMetadata.getName());
        this.getLocalFile().create();
        List<File> filesList = this.listFiles();
        List<File> dirsList = this.listDirectories();
        assert filesList != null;
        assert dirsList != null;
        for(File file : filesList) {
            new TrackedFile(driveService, this.getLocalFile().getContainedFile().getPath() + java.io.File.separator + file.getName(), file.getId()).read(this.getLocalFile().getContainedFile().getPath());
        }
        for(File dir : dirsList) {
            new TrackedDirectory(driveService, this.getLocalFile().getContainedFile().getPath() + java.io.File.separator + dir.getName(), dir.getId()).read(this.getLocalFile().getContainedFile().getPath());
        }
    }

    @Override
    void setLocalFile(String filePath) {
        this.localFile = new com.bggoranoff.drivesync.model.filesystem.Directory(filePath);
    }

    public List<File> listFiles() throws IOException {
        return driveService.files().list()
                .setQ(String.format("'%s' in parents and mimeType != 'application/vnd.google-apps.file' and trashed = false", this.getFileId()))
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, parents, modifiedTime)")
                .execute()
                .getFiles();
    }

    public List<File> listDirectories() throws IOException {
        return driveService.files().list()
                .setQ(String.format("'%s' in parents and mimeType = 'application/vnd.google-apps.folder' and trashed = false", this.getFileId()))
                .setSpaces("drive")
                .setFields("nextPageToken, files(id, name, parents)")
                .execute()
                .getFiles();
    }
}
