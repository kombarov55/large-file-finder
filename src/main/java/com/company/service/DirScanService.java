package com.company.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.company.data.FileInfoDto;
import com.company.data.FileNode;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.ProcessingFilePayload;
import com.company.util.Logger;

public class DirScanService implements IScanService {

	private static AtomicInteger fileCount = new AtomicInteger(0);
	
	public FileNode run(String baseDir) {
		FileNode root = scanDir(baseDir, Paths.get(baseDir));
		sortBySize(root);
		return root;
	}
	
	@Override
	public List<FileInfoDto> scan(String baseDirPath) {
		FileNode root = scanDir(baseDirPath, Paths.get(baseDirPath));
		sortBySize(root);
		return toList(root);
	}



	public FileNode scanDir(String currentDirPath, Path baseDirPath) {
        File file = new File(currentDirPath);
        
        FileNode currentDir = FileNode.builder()
        		.path(pathToString(file, baseDirPath))
        		.children(new ArrayList<>())
        		.build();

        File[] files = file.listFiles();

        for (int i = 0; i < files.length; i++) {
        	
            File ithFile = files[i];
            if (Files.isDirectory(file.toPath()) && ithFile.listFiles() != null && !Files.isSymbolicLink(file.toPath())) {
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
                    			.path(pathToString(ithFile, baseDirPath))
                    			.sizeInBytes(fileSize)
                    			.children(new ArrayList<>())
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
	
	private void sortBySize(FileNode fileNode) {
		if (!fileNode.getChildren().isEmpty()) {
			fileNode.getChildren().sort(Comparator.comparing(FileNode::getSizeInBytes).reversed());
			
			fileNode.getChildren().forEach(this::sortBySize);
		}
	}
	
	private List<FileInfoDto> toList(FileNode root) {
		List<FileInfoDto> result = new ArrayList<>();
		result.add(root);
		
		for (FileNode child : root.getChildren()) {
			result.addAll(toList(child));
		}
		
		return result;
	}
	
	private String pathToString(File file, Path basePath) {
		if (file.toPath().equals(basePath)) {
			return basePath.getFileName().toString();
		}
		
		return basePath.relativize(file.toPath()).toString();
	}
	
	public static void main(String[] args) {
		DirScanService dirScanService = new DirScanService();
		
		FileNode root = dirScanService.run("/Users/nikolay/git/large-file-finder");
		dirScanService.sortBySize(root);
		System.out.println("DEBUG");
	}
	
}
