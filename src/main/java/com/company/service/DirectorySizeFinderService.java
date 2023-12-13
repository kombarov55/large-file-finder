package com.company.service;

import com.company.data.FileSizeInfo;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.ProcessingFilePayload;
import com.company.util.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DirectorySizeFinderService {

    private static AtomicInteger fileCount = new AtomicInteger(0);

    public static List<FileSizeInfo> scan(String baseDir) {
        Path baseDirPath = Paths.get(baseDir);
        List<FileSizeInfo> result = new ArrayList<>();
        run(baseDir, baseDirPath, result);
        result.sort(Comparator.comparing(FileSizeInfo::getSizeInBytes).reversed());
        return result;
    }

    public static long run(String currentDir, Path baseDirPath, List<FileSizeInfo> recResult) {
        File file = new File(currentDir);

        long size = 0;

        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
        	
            File currentFile = files[i];
            if (Files.isDirectory(file.toPath()) && currentFile.listFiles() != null) {
                size += run(currentFile.getAbsolutePath(), baseDirPath, recResult);
            } else {
                try {
                    if (Files.isSymbolicLink(currentFile.toPath())) {
                    	Logger.log(currentFile.getAbsolutePath() + " is symlink.");
                    } else {
                    	Logger.log(currentFile.getAbsolutePath());
                    	size += Files.size(currentFile.toPath());
                    }
                } catch (Exception e) {
                    Logger.log("ERROR cannot determine size of name=" + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }

        FileSizeInfo v = new FileSizeInfo();
        v.path = file.toPath().equals(baseDirPath)
                ? baseDirPath.toString()
                : baseDirPath.relativize(file.toPath()).toString();
        v.sizeInBytes = size;

        recResult.add(v);
        EventBus.fireOnProcessingFile(new ProcessingFilePayload(file.getAbsolutePath(), fileCount.incrementAndGet()));

        return size;
    }
}
