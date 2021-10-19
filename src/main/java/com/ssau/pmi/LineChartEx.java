package com.ssau.pmi;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.schemes.AbstractScheme;
import com.ssau.pmi.schemes.CrankNicolsonScheme;
import com.ssau.pmi.schemes.SchemeCN;
import com.ssau.pmi.schemes.SchemeImplicit;
import com.ssau.pmi.utils.Variable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;


public class LineChartEx extends JFrame {

    public LineChartEx() {
        initUI();
    }

    private void initUI() {
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);

        pack();
        setTitle("Line chart");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private XYDataset createDataset() {
        double R = 4;
        double L = 10;
        double n = 1.7;
        int lambda = 2;

        Variable variable = Variable.Z;
        double fixedVariableValue = 5;

        int step1 = 100;
        int step2 = 100;

        AbstractScheme schemeImplicit = new SchemeImplicit(R, L, lambda, n, step1, step2, variable, fixedVariableValue);
        ComplexMatrix resultImplicit = schemeImplicit.calculateResultMatrix();

        AbstractScheme schemeCN = new SchemeCN(R, L, lambda, n, step1, step2, variable, fixedVariableValue);
        ComplexMatrix resultMatrixCN = schemeCN.calculateResultMatrix();

        double[] arrayCN = null;
        double[] arrayImplicit = null;
        double h;

        if (variable == Variable.Z) {
            arrayCN = resultMatrixCN.getColumn((int) fixedVariableValue);
            arrayImplicit = resultImplicit.getColumn((int) fixedVariableValue);
            h = schemeCN.getH_r();
        } else {
            arrayCN = resultMatrixCN.getRow((int) fixedVariableValue);
            arrayImplicit = resultImplicit.getRow((int) fixedVariableValue);
            h = schemeCN.getH_z();
        }


        var seriesCN = new XYSeries("CN");
        var seriesImplicit = new XYSeries("Implicit");

        for (int i = 0; i < arrayCN.length; i++) {
            seriesCN.add(i * h, arrayCN[i]);
            seriesImplicit.add(i * h, arrayImplicit[i]);
        }

        var dataset = new XYSeriesCollection();
        dataset.addSeries(seriesCN);
        dataset.addSeries(seriesImplicit);

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "Scheme",
                "r",
                "|U|",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.magenta);
        renderer.setSeriesStroke(0, new BasicStroke(2.0f));

        renderer.setSeriesPaint(1, Color.green);
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("Scheme",
                        new Font("Serif", java.awt.Font.BOLD, 18)
                )
        );

        return chart;
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(() -> {

            var ex = new LineChartEx();
            ex.setVisible(true);
        });
    }
}
