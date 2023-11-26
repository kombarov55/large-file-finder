package com.company.swing.event_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectorySelectedPayload {
    private String path;
}
