package com.company.service;

import com.company.data.FileSizeInfo;
import com.company.swing.DataTable;
import com.company.swing.Params;
import com.company.swing.SearchPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwingService {

    public static void displayData(List<FileSizeInfo> list) {
        JFrame jframe = new JFrame("Список файлов");
        jframe.setSize(Params.width, Params.height);
        jframe.setLocationRelativeTo(null);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jframe.getContentPane().add(BorderLayout.NORTH, SearchPanel.build());
        jframe.getContentPane().add(BorderLayout.CENTER, DataTable.build());


        jframe.setVisible(true);
    }

}
