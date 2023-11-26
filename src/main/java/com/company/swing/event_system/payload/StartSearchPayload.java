package com.company.swing.event_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartSearchPayload {
    private String baseDirectoryPath;
}
