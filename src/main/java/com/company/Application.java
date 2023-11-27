package com.company;

import com.company.swing.DataTable;
import com.company.swing.TopPanel;

import javax.swing.*;
import java.awt.*;

public class Application {
    public static void main(String[] args) {
        JFrame jframe = new JFrame("Список файлов");
        jframe.setSize(800, 480);
        jframe.setLocationRelativeTo(null);
        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jframe.getContentPane().add(BorderLayout.NORTH, TopPanel.build());
        jframe.getContentPane().add(BorderLayout.CENTER, DataTable.build());


        jframe.setVisible(true);
    }
}
