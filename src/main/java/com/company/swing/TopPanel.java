package com.company.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.company.data.FileInfoDto;
import com.company.service.DirScanService;
import com.company.service.FileSizeFinderService;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.DirectorySelectedPayload;
import com.company.swing.event_system.payload.SearchEndedPayload;
import com.company.swing.event_system.payload.StartSearchPayload;

public class TopPanel {

    private static ScanStrategy scanStrategy = ScanStrategy.DIRECTORIES;
    
    private static String lastPath = ".";

    public static JPanel build() {
        JPanel verticalPanel = new JPanel();
        verticalPanel.setLayout(new GridLayout(2, 1));

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        searchPanel.add(fileChooseButton(searchPanel));
        searchPanel.add(dirLabel());
        searchPanel.add(searchTypeCombobox());
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

                new Thread(() -> {
                    try {
                        List<FileInfoDto> result = runRequest(filePath);

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
        JComboBox<String> combobox = new JComboBox<>(ScanStrategy.descriptions());
        combobox.addActionListener(e -> scanStrategy = ScanStrategy.findByDescription(combobox.getSelectedItem()));
        return combobox;
    }

    private static JLabel lastFileLabel() {
        JLabel jlabel = new JLabel("");

        EventBus.subscribersOfProcessingFile.add(v -> jlabel.setText(
                "Обработано " + v.getFileCount() + " " + v.getFilename()
        ));
        EventBus.subscribersOfSearchEnded.add(v -> jlabel.setText(""));

        return jlabel;
    }
    
    private static List<FileInfoDto> runRequest(String dirPath) {
    	switch (scanStrategy) {
    		case DIRECTORIES:
    			return new DirScanService().scan(dirPath);
    		case FILES:
    			return new FileSizeFinderService().scan(dirPath);
    		default: 
    			return new ArrayList<>();
    	} 
    }
}
