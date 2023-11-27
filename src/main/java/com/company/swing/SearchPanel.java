package com.company.swing;

import com.company.data.FileSizeInfo;
import com.company.service.DirectorySizeFinderService;
import com.company.service.FileSizeFinderService;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.DirectorySelectedPayload;
import com.company.swing.event_system.payload.SearchEndedPayload;
import com.company.swing.event_system.payload.StartSearchPayload;
import com.company.util.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SearchPanel {

    private static boolean selectedByDirectory = false;

    private static List<FileSizeInfo> copy = new ArrayList<>();

    private static String lastPath = ".";

    public static JPanel build() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new FlowLayout(FlowLayout.LEADING));

        JButton chooseButton = new JButton("Выбор папки");

        chooseButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser(lastPath);
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = jFileChooser.showOpenDialog(jpanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
                lastPath = filePath;
                EventBus.onDirectorySelected(new DirectorySelectedPayload(filePath));

                EventBus.onSearchStarted(new StartSearchPayload(filePath));
                copy = new ArrayList<>();

                new Thread(() -> {
                    try {
                        List<FileSizeInfo> result = selectedByDirectory
                                ? DirectorySizeFinderService.run(filePath)
                                : FileSizeFinderService.run(filePath);

                        copy = result;

                        EventBus.onSearchEnded(new SearchEndedPayload(result));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }).start();
            }
        });
        jpanel.add(chooseButton);

        JLabel dirLabel = new JLabel();
        EventBus.subscribersOfDirectorySelected.add(v -> dirLabel.setText(v.getPath()));
        jpanel.add(dirLabel);

        JComboBox<String> combobox = new JComboBox<>(new String[]{"Считать размеры файлов", "Считать размеры папок"});
        combobox.addActionListener(e -> {
            selectedByDirectory = combobox.getSelectedItem().equals("Считать размеры папок");
            Logger.log("selected " + combobox.getSelectedItem());
        });
        jpanel.add(combobox);

        JComboBox<String> sortCombobox = new JComboBox<>(new String[]{"Сортировка по размеру", "Сортировка по имени"});
        sortCombobox.addActionListener(e -> {
            if (sortCombobox.getSelectedItem().equals("Сортировка по имени")) {
                copy.sort(Comparator.comparing(FileSizeInfo::getPath));
            } else {
                copy.sort(Comparator.comparing(FileSizeInfo::getSizeInBytes).reversed());
            }

            EventBus.onSearchEnded(new SearchEndedPayload(copy));
        });
        jpanel.add(sortCombobox);

        jpanel.setMinimumSize(new Dimension(800, 100));

        return jpanel;
    }
}

