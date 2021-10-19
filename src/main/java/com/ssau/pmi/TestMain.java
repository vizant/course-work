package com.ssau.pmi;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Random;
import javax.swing.JFrame;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.Regression;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class TestMain {

    private static final int N = 16;
    private static final Random R = new Random();

    private static XYDataset createDataset() {
        XYSeries series = new XYSeries("Data");
        for (int i = 0; i < N; i++) {
            series.add(i, R.nextGaussian() + i);
        }
        XYSeriesCollection xyData = new XYSeriesCollection(series);
        double[] coefficients = Regression.getOLSRegression(xyData, 0);
        double b = coefficients[0]; // intercept
        double m = coefficients[1]; // slope
        XYSeries trend = new XYSeries("Trend");
        double x = series.getDataItem(0).getXValue();
        trend.add(x, m * x + b);
        x = series.getDataItem(series.getItemCount() - 1).getXValue();
        trend.add(x, m * x + b);
        xyData.addSeries(trend);
        return xyData;
    }

    private static JFreeChart createChart(final XYDataset dataset) {
        JFreeChart chart = ChartFactory.createScatterPlot("Test", "X", "Y",
                dataset, PlotOrientation.VERTICAL, true, false, false);
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
        r.setSeriesLinesVisible(1, Boolean.TRUE);
        r.setSeriesShapesVisible(1, Boolean.FALSE);
        return chart;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame f = new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                XYDataset dataset = createDataset();
                JFreeChart chart = createChart(dataset);
                ChartPanel chartPanel = new ChartPanel(chart) {
                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(640, 480);
                    }
                };
                f.add(chartPanel);
                f.pack();
                f.setLocationRelativeTo(null);
                f.setVisible(true);
            }
        });
    }
}