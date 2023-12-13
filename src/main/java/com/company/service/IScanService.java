package com.company.service;

import java.util.List;

import com.company.data.FileInfoDto;

public interface IScanService {
	List<FileInfoDto> scan(String baseDirPath);
}
