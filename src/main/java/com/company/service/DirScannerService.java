package com.company.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import com.company.data.FileNode;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.ProcessingFilePayload;
import com.company.util.Logger;

public class DirScannerService {

	private static AtomicInteger fileCount = new AtomicInteger(0);
	
	public static FileNode run(String baseDir) {
		FileNode root = scanDir(baseDir, Paths.get(baseDir));
		
		
		
		return root;
	}
	
	public static FileNode scanDir(String currentDirPath, Path baseDirPath) {
        File file = new File(currentDirPath);
        
        FileNode currentDir = new FileNode();
        currentDir.setPath(file.toPath().equals(baseDirPath)
                ? baseDirPath.toString()
                : baseDirPath.relativize(file.toPath()).toString());

        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
        	
            File ithFile = files[i];
            if (Files.isDirectory(file.toPath()) && ithFile.listFiles() != null) {
                FileNode childDir = scanDir(ithFile.getAbsolutePath(), baseDirPath);
                currentDir.addSize(childDir.getSizeInBytes());
                currentDir.getChildren().add(childDir);
            } else {
                try {
                    if (Files.isSymbolicLink(ithFile.toPath())) {
                    	Logger.log(ithFile.getAbsolutePath() + " is symlink.");
                    } else {
                    	Logger.log(ithFile.getAbsolutePath());
                    	
                    	long fileSize = Files.size(ithFile.toPath());
                    	FileNode regularFileNode = FileNode.builder()
                    			.path(ithFile.getPath())
                    			.sizeInBytes(fileSize)
                    			.build();
                    	currentDir.addSize(fileSize);
                    	currentDir.getChildren().add(regularFileNode);
                    }
                } catch (Exception e) {
                    Logger.log("ERROR cannot determine size of name=" + file.getAbsolutePath());
                    e.printStackTrace();
                }
            }
        }

        EventBus.fireOnProcessingFile(new ProcessingFilePayload(file.getAbsolutePath(), fileCount.incrementAndGet()));
        return currentDir;
    }
	
	public static void main(String[] args) {
		FileNode root = DirScannerService.run("/Users/nikolay/git/large-file-finder");
		System.out.println("DEBUG");
	}
	
}