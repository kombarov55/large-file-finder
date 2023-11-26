package com.company;

import com.company.service.SwingService;

import java.io.IOException;
import java.util.Collections;

public class Application {
    public static void main(String[] args) throws IOException {
//        List<FileSizeInfo> list = FileSizeFinderService.run("C:\\Users\\komba\\AppData");
//        ReportService.generateCsvReport("reports", list);
        SwingService.displayData(Collections.emptyList());
    }
}
