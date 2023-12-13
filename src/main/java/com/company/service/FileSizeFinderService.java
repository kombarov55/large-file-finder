package com.company.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.company.data.FileInfoDto;
import com.company.data.FileSizeInfo;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.ProcessingFilePayload;
import com.company.util.Logger;

public class FileSizeFinderService implements IScanService {

    private AtomicInteger fileCount = new AtomicInteger(0);
    
    @Override
	public List<FileInfoDto> scan(String baseDir) {
    	Path baseDirPath = Paths.get(baseDir);
        List<FileInfoDto> result = run(baseDir, baseDirPath);
        result.sort(Comparator.comparing(FileInfoDto::getSizeInBytes).reversed());
        return result;
    }

    public List<FileInfoDto> run(String currentDir, Path baseDirPath) {
        List<FileInfoDto> result = new ArrayList<>();

        File dir = new File(currentDir);

        File[] files = new File[]{};
        try {
            files = dir.listFiles();
        } catch (Exception e) {
            Logger.log("ERROR at dir=" + dir.getAbsolutePath());
        }

        for (File file : files) {
            if (file.isDirectory() && file.listFiles() != null) {
                result.addAll(run(file.getAbsolutePath(), baseDirPath));
            }

            try {
                FileSizeInfo v = extract(baseDirPath, file);
                result.add(v);
                EventBus.fireOnProcessingFile(new ProcessingFilePayload(file.getAbsolutePath(), fileCount.incrementAndGet()));
            } catch (Exception e) {
                Logger.log("ERROR AT file=" + file.getAbsolutePath());
                e.printStackTrace();
            }
        }

        return result;
    }

    private FileSizeInfo extract(Path baseDirPath, File file) throws IOException {
        String path = baseDirPath.relativize(file.toPath()).toString();
        long size = Files.size(file.toPath());

        Logger.log("name=" + path + " size=" + size);
        return new FileSizeInfo(path, size);
    }
}
