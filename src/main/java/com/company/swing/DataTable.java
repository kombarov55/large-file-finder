package com.company.swing;

import com.company.data.FileSizeInfo;
import com.company.swing.event_system.EventBus;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

public class DataTable {

    public static JComponent build() {
        DefaultTableModel tableModel = new DefaultTableModel(new String[] {"Путь к файлу", "Размер в ГБ", "Размер в МБ", "Размер в байтах"}, 0);
        JTable jtable = new JTable(tableModel);
        jtable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        jtable.setFillsViewportHeight(true);

        EventBus.subscribersOfSearchStarted.add((e -> tableModel.setRowCount(0)));

        EventBus.subscribersOfSearchEnded.add(payload -> {
            updateData(tableModel, toRowData(payload.getData()));
        });

        return new JScrollPane(jtable);
    }
    private static Object[][] toRowData(List<FileSizeInfo> result) {
        return result.stream()
                .map(v -> {
                    String sizeInMb = BigDecimal.valueOf(v.getSizeInBytes() / 1024.0 / 1024.0)
                            .setScale(3, RoundingMode.HALF_UP).toString();
                    String sizeInGb = BigDecimal.valueOf(v.getSizeInBytes() / 1024.0 / 1024.0 / 1024.0)
                            .setScale(3, RoundingMode.HALF_UP).toString();;

                    return new Object[] {v.path,
                            sizeInGb,
                            sizeInMb,
                            v.sizeInBytes
                    };
                }).collect(Collectors.toList()).toArray(new Object[result.size()][4]);
    }

    private static void updateData(DefaultTableModel model, Object[][] data) {
        model.setRowCount(0);

        for (int i = 0; i < data.length; i++) {
            model.addRow(data[i]);
        }
    }
}
