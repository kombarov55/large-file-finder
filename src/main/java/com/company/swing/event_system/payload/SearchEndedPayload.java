package com.company.swing.event_system.payload;

import com.company.data.FileSizeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchEndedPayload {
    private List<FileSizeInfo> data;
}
