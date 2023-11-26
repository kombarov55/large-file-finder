package com.company.swing;

import com.company.data.FileSizeInfo;
import com.company.service.FileSizeFinderService;
import com.company.swing.event_system.EventBus;
import com.company.swing.event_system.payload.DirectorySelectedPayload;
import com.company.swing.event_system.payload.SearchEndedPayload;
import com.company.swing.event_system.payload.StartSearchPayload;
import com.company.swing.event_system.TickEverySecond;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SearchPanel {

//    private static int seconds = 0;

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

//        JLabel elapsedTimeLabel = new JLabel();
//        EventBus.subscribersOfSearchStarted.add(v -> {
//            elapsedTimeLabel.setText(formatElapsedTime(0));
//            elapsedTimeLabel.updateUI();
//        });
//        EventBus.subscribersOfTickEverySecond.add(v -> {
//            seconds = v.getSeconds();
//            elapsedTimeLabel.setText(formatElapsedTime(v.getSeconds()));
//            elapsedTimeLabel.updateUI();
//        });
//        EventBus.subscribersOfSearchEnded.add(v -> {
//            elapsedTimeLabel.setText("Выполнено. " + formatElapsedTime(seconds));
//            elapsedTimeLabel.updateUI();
//        });
//        jpanel.add(elapsedTimeLabel);

        jpanel.setMinimumSize(new Dimension(Params.width, 100));

        TickEverySecond.start();

        return jpanel;
    }

//    private static String formatElapsedTime(long diff) {
//        int seconds = (int) diff / 1000;
//        int minutes = seconds / 60;
//
//        String secondsString = seconds < 10 ? "0" + seconds : "" + seconds;
//        String minutesString = minutes < 10 ? "0" + minutes : "" + minutes;
//
//        return "Время выполнения: " + minutesString + ":" + secondsString;
//    }

}

