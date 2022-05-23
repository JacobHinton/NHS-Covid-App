package gui;

import data.DataRepo;
import linear_regression.Estimator;
import linear_regression.Model;
import linear_regression.OLSEstimator;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class TableDisplayPanel extends JPanel {

    private JTable table;

    public TableDisplayPanel(DataRepo data) {
        LayoutManager layout = new BorderLayout();
        setLayout(layout);
        String[] columnNames = {"Day", "Cases", "Deaths"};
        String[][] tableData = new String[7][];
        double[] dates = data.getDateOffsets();
        double[] cases = data.getCases();
        double[] deaths = data.getDeaths();
        Estimator estimator = new OLSEstimator();
        Model casesModel = estimator.getModel(dates, cases);
        Model deathsModel = estimator.getModel(dates, deaths);
        DecimalFormat df = new DecimalFormat("###,###");
        df.setMaximumIntegerDigits(15);
        for (int i = 0; i < 7; i++) {
            double day = dates[0] + i;
            double predictedCases = casesModel.predict(day + 1);
            double predictedDeaths = deathsModel.predict(day + 1);
            tableData[i] = new String[3];
            tableData[i][0] = "+" + Integer.toString(i + 1) + " days";
            tableData[i][1] = df.format(predictedCases);
            tableData[i][2] = df.format(predictedDeaths);
        }
        this.table = new JTable(tableData, columnNames);
        table.setFillsViewportHeight(true);
        add(table.getTableHeader(), BorderLayout.NORTH);
        add(table, BorderLayout.CENTER);
    }

    public void tableExport(String filename) {
        table.setSize(250, 150);
        JTableHeader header = table.getTableHeader();
        int width = Math.max(table.getWidth(), header.getWidth());
        int height = table.getHeight() + header.getHeight();
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        header.paint(g);
        g.translate(0, header.getHeight());
        table.paint(g);
        g.dispose();
        try {
            ImageIO.write(bi, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
