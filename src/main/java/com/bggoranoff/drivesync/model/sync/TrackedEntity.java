package com.bggoranoff.drivesync.model.sync;

import com.bggoranoff.drivesync.model.filesystem.FileSystemEntity;

import java.io.IOException;

public interface TrackedEntity {
    void upload(String parentId) throws IOException;

    void delete() throws IOException;

    void read(String destination) throws IOException;
}
