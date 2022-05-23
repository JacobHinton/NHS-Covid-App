package gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TableButtonPanel extends JPanel {
    private JFrame parent;

    public TableButtonPanel(JFrame parent) {
        this.parent = parent;
        JButton closeBtn = new JButton("Close");
        add(closeBtn);
        closeBtn.addActionListener(new CloseHandler());
    }

    private class CloseHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            parent.dispose();
        }
    }
}
