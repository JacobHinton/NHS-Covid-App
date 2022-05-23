package gui;

import data.DataRepo;

import javax.swing.*;
import java.awt.*;

public class TableFrame extends JFrame {
    public TableFrame(DataRepo data) {
        BorderLayout layout = (BorderLayout) getLayout();
        layout.setHgap(10);
        layout.setVgap(20);
        setSize(400, 300);
        setVisible(true);
        setTitle("Cases");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        TableDisplayPanel displayPanel = new TableDisplayPanel(data);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.add(displayPanel);
        add(scrollPane, BorderLayout.CENTER);
        TableButtonPanel buttonPanel = new TableButtonPanel(this);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
