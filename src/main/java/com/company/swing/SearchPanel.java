package com.company.swing;

import com.company.data.FileSizeInfo;
import com.company.service.FileSizeFinderService;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.DirectorySelectedPayload;
import com.company.swing.event_system.payload.SearchEndedPayload;
import com.company.swing.event_system.payload.StartSearchPayload;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SearchPanel {

    public static JPanel build() {
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new FlowLayout(FlowLayout.LEADING));

        JButton chooseButton = new JButton("Выбор папки");

        chooseButton.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = jFileChooser.showOpenDialog(jpanel);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
                EventBus.onDirectorySelected(new DirectorySelectedPayload(filePath));

                EventBus.onSearchStarted(new StartSearchPayload(filePath));

                new Thread(() -> {
                    try {
                        List<FileSizeInfo> result = FileSizeFinderService.run(filePath);
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

        jpanel.setMinimumSize(new Dimension(800, 100));

        return jpanel;
    }
}

