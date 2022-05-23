package gui;

import data.DataRepo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuOptions extends JPanel {
    private JFrame parent;
    private DataRepo data;

    public MenuOptions(JFrame parent, DataRepo data) {
        this.parent = parent;
        this.data = data;
        FlowLayout layout = new FlowLayout(FlowLayout.CENTER, 10, 10);
        setLayout(layout);
        JButton reloadBtn = new JButton("Reload Data");
        add(reloadBtn, BorderLayout.SOUTH);
        reloadBtn.addActionListener(new ReloadHandler());
        JButton exportBtn = new JButton("Export Pdf");
        add(exportBtn, BorderLayout.SOUTH);
        exportBtn.addActionListener(new ExportHandler());
        JButton dataBtn = new JButton("View Table");
        add(dataBtn, BorderLayout.SOUTH);
        dataBtn.addActionListener(new TableHandler());
    }

    private class TableHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            TableFrame frame = new TableFrame(data);
            frame.setLocationRelativeTo(parent);
        }
    }

    private class ReloadHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            for (; ; ) {
                try {
                    data.refresh();
                    JOptionPane.showMessageDialog(parent, "Successfully downloaded new data", "Success", JOptionPane.INFORMATION_MESSAGE);
                    return;
                } catch (IOException e) {
                    int result = JOptionPane.showConfirmDialog(parent, "Failed to update data, try again?\nError: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    if (result == 0) continue;
                    else break;
                }
            }
        }
    }

    private class ExportHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                PDDocument export = new PDDocument();
                PDPage page_1 = new PDPage();
                export.addPage(page_1);
                PDPage page_2 = new PDPage();
                export.addPage(page_2);
                PDPage page1 = export.getPage(0);
                float yStart = PDRectangle.A4.getHeight() - 425;
                float Width = PDRectangle.A4.getWidth() - 500;
                PDImageXObject pdImage = PDImageXObject.createFromFile("resources/deathsGraph.png", export);
                PDImageXObject pdImage2 = PDImageXObject.createFromFile("resources/casesGraph.png", export);
                PDPageContentStream contentStream = new PDPageContentStream(export, page1);
                contentStream.drawImage(pdImage, Width, yStart);
                contentStream.drawImage(pdImage2, Width, 60);
                contentStream.close();
                TableDisplayPanel displayPanel = new TableDisplayPanel(data);
                displayPanel.setSize(250, 150);
                displayPanel.tableExport("resources/table.png");
                PDImageXObject pdImage3 = PDImageXObject.createFromFile("resources/table.png", export);
                PDPage page2 = export.getPage(1);
                PDPageContentStream contentStream2 = new PDPageContentStream(export, page2);
                contentStream2.drawImage(pdImage3, Width, yStart);
                contentStream2.close();
                String filename = "resources/Results_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH_mm_ss")) + ".pdf";
                export.save(filename);
                export.close();
                Path path = Paths.get(filename);

                JOptionPane.showMessageDialog(parent, "Successfully created: " + path.toAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showConfirmDialog(parent, "Failed to create pdf\nError: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
