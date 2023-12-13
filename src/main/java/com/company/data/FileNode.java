package com.company.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileNode extends FileInfoDto {
	private String path;
	private long sizeInBytes;
	private List<FileNode> children;
	
	public void addSize(float x) {
		sizeInBytes += x;
	}
}
