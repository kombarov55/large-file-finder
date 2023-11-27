package com.company.swing;

import com.company.data.FileSizeInfo;
import com.company.service.DirectorySizeFinderService;
import com.company.service.FileSizeFinderService;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.DirectorySelectedPayload;
import com.company.swing.event_system.payload.SearchEndedPayload;
import com.company.swing.event_system.payload.StartSearchPayload;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TopPanel {

    private static boolean selectedByDirectory = false;

    private static List<FileSizeInfo> copy = new ArrayList<>();

    private static String lastPath = ".";

    public static JPanel build() {
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new GridLayout(2, 1));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        searchPanel.add(fileChooseButton(searchPanel));
        searchPanel.add(dirLabel());
        searchPanel.add(searchTypeCombobox());
        searchPanel.add(sortCombobox());
        searchPanel.setMinimumSize(new Dimension(800, 100));

        JPanel logPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        logPanel.add(lastFileLabel());

        verticalPanel.add(searchPanel);
        verticalPanel.add(logPanel);

        return verticalPanel;
    }

    private static JButton fileChooseButton(JPanel jpanel) {
        JButton chooseButton = new JButton("Выбор папки");

        chooseButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser(lastPath);
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = jFileChooser.showOpenDialog(jpanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
                lastPath = filePath;
                EventBus.fireOnDirectorySelected(new DirectorySelectedPayload(filePath));

                EventBus.fireOnSearchStarted(new StartSearchPayload(filePath));
                copy = new ArrayList<>();

                new Thread(() -> {
                    try {
                        List<FileSizeInfo> result = selectedByDirectory
                                ? DirectorySizeFinderService.run(filePath)
                                : FileSizeFinderService.run(filePath);

                        copy = result;

                        EventBus.fireOnSearchEnded(new SearchEndedPayload(result));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }).start();
            }
        });

        return chooseButton;
    }

    private static JLabel dirLabel() {
        JLabel dirLabel = new JLabel();
        EventBus.subscribersOfDirectorySelected.add(v -> dirLabel.setText(v.getPath()));
        return dirLabel;
    }

    private static JComboBox<String> searchTypeCombobox() {
        JComboBox<String> combobox = new JComboBox<>(new String[]{"Считать размеры файлов", "Считать размеры папок"});
        combobox.addActionListener(e -> selectedByDirectory = combobox.getSelectedItem().equals("Считать размеры папок"));
        return combobox;
    }

    private static JComboBox<String> sortCombobox() {
        JComboBox<String> sortCombobox = new JComboBox<>(new String[]{"Сортировка по размеру", "Сортировка по имени"});
        sortCombobox.addActionListener(e -> {
            if (sortCombobox.getSelectedItem().equals("Сортировка по имени")) {
                copy.sort(Comparator.comparing(FileSizeInfo::getPath).thenComparing(FileSizeInfo::getSizeInBytes).reversed());
            } else {
                copy.sort(Comparator.comparing(FileSizeInfo::getSizeInBytes).reversed());
            }

            EventBus.fireOnSearchEnded(new SearchEndedPayload(copy));
        });

        return sortCombobox;
    }

    private static JLabel lastFileLabel() {
        JLabel jlabel = new JLabel("");

        EventBus.subscribersOfProcessingFile.add(v -> jlabel.setText(
                "Обработано " + v.getFileCount() + " " + v.getFilename()
        ));
        EventBus.subscribersOfSearchEnded.add(v -> jlabel.setText(""));

        return jlabel;
    }
}

