package com.company.service;

import com.company.data.FileSizeInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ReportService {

    public static Path generateCsvReport(String reportBaseDir, List<FileSizeInfo> list) throws IOException {
        if (!Files.exists(Paths.get(reportBaseDir))) {
            Files.createDirectory(Paths.get(reportBaseDir));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME).replace(":", "-");

        Path reportFilePath = Files.createFile(Paths.get(reportBaseDir, timestamp + ".csv"));

        list.sort(Comparator.comparing(FileSizeInfo::getSizeInBytes).reversed());

        try (PrintWriter out = new PrintWriter(Files.newOutputStream(reportFilePath), true, Charset.forName("UTF-8"))) {
            out.println(String.join(";",
                    "path",
                    "size_bytes",
                    "size_mb",
                    "size_gb"
            ));

            for (FileSizeInfo fileSizeInfo : list) {
                double sizeInMb = fileSizeInfo.getSizeInBytes() / 1024.0 / 1024.0;
                double sizeInGb = fileSizeInfo.getSizeInBytes() / 1024.0 / 1024.0 / 1024.0;

                out.println(String.join(";",
                        fileSizeInfo.path.toString(),
                        "" + fileSizeInfo.sizeInBytes,
                        "" + sizeInMb,
                        "" + sizeInGb
                ));
            }
        }

        return reportFilePath;
    }

}
