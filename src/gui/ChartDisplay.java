package gui;

import data.DataRepo;
import linear_regression.PiecewiseEstimator;
import linear_regression.PiecewiseModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.Color.BLUE;

public class ChartDisplay extends JPanel {
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private DataRepo data;
    private String ylabel;
    private Mode mode;
    private PiecewiseModel model;

    public ChartDisplay(DataRepo data, Mode mode) {
        this.data = data;
        this.mode = mode;
        switch (mode) {
            case CASES:
                ylabel = "Cases";
                break;
            case DEATHS:
                ylabel = "Deaths";
                break;
            default:
                assert false; // unreachable
        }

        refreshChart();
        add(chartPanel);
    }

    public void refreshChart() {
        double[] dayOffsets = data.getDateOffsets();
        double[] yvalues = mode == Mode.CASES ? data.getCases() : data.getDeaths();
        XYSeries yseries = new XYSeries(ylabel);
        for (int i = 0; i < dayOffsets.length; i++) yseries.add(dayOffsets[i], yvalues[i]);
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(yseries);
        model = refreshModel(dayOffsets, yvalues);
        XYSeries extrapolationSeries = new XYSeries(ylabel);
        for (int i = dayOffsets.length; i < dayOffsets.length + 14; i++) {
            extrapolationSeries.add(i, model.predict(i));
        }
        dataset.addSeries(extrapolationSeries);
        chart = ChartFactory.createScatterPlot("Covid-19", "Days since First Case", ylabel, dataset, PlotOrientation.VERTICAL, false, false, false);
        if (dayOffsets.length > 1) {
            XYPlot plot = chart.getXYPlot();
            double first = 0;
            for (double bound : model.getBounds()) {
                double x1 = first;
                double x2 = bound;
                double y1 = model.predict(x1);
                double y2 = model.predict(x2);
                XYAnnotation modelLine = new XYLineAnnotation(x1, y1, x2, y2);
                plot.addAnnotation(modelLine);
                first = bound;
            }
        }

        XYPlot plot2 = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot2.getRenderer();
        renderer.setSeriesPaint(1, BLUE);
        chartPanel = new ChartPanel(chart);
    }

    public JFreeChart getChart() {
        return chart;
    }

    public void exportGraph(JFreeChart chart, String filename) {
        try {
            BufferedImage ylabel = chart.createBufferedImage(450, 325);
            File outputfile = new File(filename);
            ImageIO.write(ylabel, "png", outputfile);
        } catch (IOException e) {
            System.out.println("ERROR, could not create file. ");
        }
    }


    private PiecewiseModel refreshModel(double[] x, double[] y) {
        PiecewiseEstimator estimator = new PiecewiseEstimator();
        return (PiecewiseModel) estimator.getModel(x, y);
    }

    enum Mode {
        DEATHS,
        CASES
    }
}