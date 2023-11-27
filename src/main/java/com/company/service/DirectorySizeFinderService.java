package com.company.service;

import com.company.data.FileSizeInfo;
import com.company.util.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DirectorySizeFinderService {

    public static List<FileSizeInfo> run(String baseDir) {
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
                    Logger.log(currentFile.getAbsolutePath());
                    size += Files.size(currentFile.toPath());
                } catch (Exception e) {
                    Logger.log("ERROR cannot determine size of name=" + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }

        FileSizeInfo v = new FileSizeInfo();
        v.path = file.toPath().equals(baseDirPath)
                ? baseDirPath
                : baseDirPath.relativize(file.toPath());
        v.sizeInBytes = size;

        recResult.add(v);

        return size;
    }
}
