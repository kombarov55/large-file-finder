package com.company.swing.event_system.payload;

import java.util.List;

import com.company.data.FileInfoDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SearchEndedPayload {
    private List<FileInfoDto> data;
}
