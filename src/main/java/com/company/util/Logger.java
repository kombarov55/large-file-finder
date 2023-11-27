package com.company.util;

import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.ProcessingFilePayload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    public static void log(String msg) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println(timestamp + " " + msg);
        EventBus.fireOnProcessingFile(new ProcessingFilePayload(msg));
    }
}
