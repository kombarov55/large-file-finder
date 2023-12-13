package com.company.data;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileNode {
	private String path;
	private long sizeInBytes;
	private List<FileNode> children = new ArrayList<>();
	
	public void addSize(float x) {
		sizeInBytes += x;
	}
}
