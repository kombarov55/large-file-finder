package com.company.data;

import lombok.Data;

import java.nio.file.Path;

@Data
public class FileSizeInfo {
    public Path path;
    public long sizeInBytes;

    public FileSizeInfo(Path path, long sizeInBytes) {
        this.path = path;
        this.sizeInBytes = sizeInBytes;
    }

    public Path getPath() {
        return path;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }
}
