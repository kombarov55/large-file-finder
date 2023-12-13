package com.company.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileSizeInfo extends FileInfoDto {
    public String path;
    public long sizeInBytes;
}
