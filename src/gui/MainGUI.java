package gui;

import data.DataRepo;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainGUI extends JFrame {
    private JTabbedPane tp = new JTabbedPane();
    private ChartDisplay deathsDisplayPanel;
    private ChartDisplay casesDisplayPanel;
    private DataRepo data;
    private MenuOptions menuOptions;

    public MainGUI() {
        initData();
        menuOptions = new MenuOptions(this, data);
        casesDisplayPanel = new ChartDisplay(data, ChartDisplay.Mode.CASES);
        casesDisplayPanel.exportGraph(casesDisplayPanel.getChart(), "resources/casesGraph.png");
        deathsDisplayPanel = new ChartDisplay(data, ChartDisplay.Mode.DEATHS);
        deathsDisplayPanel.exportGraph(deathsDisplayPanel.getChart(), "resources/deathsGraph.png");

        tp.addTab("Cases", null, casesDisplayPanel);
        tp.addTab("Deaths", null, deathsDisplayPanel);
        add(tp, BorderLayout.CENTER);
        add(menuOptions, BorderLayout.SOUTH);
        setSize(700, 600);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        new MainGUI();
    }

    private void initData() {
        for (; ; ) {
            try {
                data = new DataRepo();
                break;
            } catch (IOException e) {
                int userOption = JOptionPane.showConfirmDialog(this, "Failed to get data, try again?\nError: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                if (userOption == JOptionPane.OK_OPTION) continue;
                System.exit(-1);
            }
        }
    }
}
