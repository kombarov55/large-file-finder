package com.company.service;

import com.company.data.FileSizeInfo;
import com.company.util.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSizeFinderService {


    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static List<FileSizeInfo> run(String baseDir) {
        Path baseDirPath = Paths.get(baseDir);
        List<FileSizeInfo> result = run(baseDir, baseDirPath);
        result.sort(Comparator.comparing(FileSizeInfo::getSizeInBytes).reversed());
        return result;
    }

    public static List<FileSizeInfo> run(String currentDir, Path baseDirPath) {
        List<FileSizeInfo> result = new ArrayList<>();

        File dir = new File(currentDir);

        File[] files = new File[]{};
        try {
            files = dir.listFiles();
        } catch (Exception e) {
            Logger.log("ERROR at dir=" + dir.getAbsolutePath());
        }

        List<CompletableFuture<List<FileSizeInfo>>> parts = new ArrayList<>();

        for (File file : files) {
            if (file.isDirectory() && file.listFiles() != null) {
                result.addAll(run(file.getAbsolutePath(), baseDirPath));
            }

            try {
                FileSizeInfo v = extract(baseDirPath, file);
                result.add(v);
            } catch (Exception e) {
                Logger.log("ERROR AT file=" + file.getAbsolutePath());
                e.printStackTrace();
            }
        }

        return result;
    }

    private static FileSizeInfo extract(Path baseDirPath, File file) throws IOException {
        Path path = baseDirPath.relativize(file.toPath());
        long size = Files.size(file.toPath());

        Logger.log("name=" + path + " size=" + size);
        return new FileSizeInfo(path, size);
    }

}
