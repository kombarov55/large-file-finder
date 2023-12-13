package com.company.swing;

import java.awt.Dimension;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.company.data.FileInfoDto;
import com.company.swing.event_system.EventBus;

public class DataTable {

    public static JComponent build() {
        DefaultTableModel tableModel = new DefaultTableModel(new String[] {"Путь к файлу", "Размер"}, 0);
        JTable jtable = new JTable(tableModel);
        jtable.setPreferredScrollableViewportSize(new Dimension(500, 70));
        jtable.setFillsViewportHeight(true);

        EventBus.subscribersOfSearchStarted.add((e -> tableModel.setRowCount(0)));

        EventBus.subscribersOfSearchEnded.add(payload -> {
            updateData(tableModel, toRowData(payload.getData()));
        });

        return new JScrollPane(jtable);
    }
    private static Object[][] toRowData(List<FileInfoDto> result) {
        return result.stream()
                .map(v -> {
                    return new Object[] {v.getPath(),
                            v.sizeInHumanText()
                    };
                }).collect(Collectors.toList()).toArray(new Object[result.size()][2]);
    }

    private static void updateData(DefaultTableModel model, Object[][] data) {
        model.setRowCount(0);

        for (int i = 0; i < data.length; i++) {
            model.addRow(data[i]);
        }
    }
}
