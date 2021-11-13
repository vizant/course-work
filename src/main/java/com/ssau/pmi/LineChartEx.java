package com.ssau.pmi;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.schemes.AbstractScheme;
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

public class LineChartEx extends JFrame {

    private double R = 4;
    private double L = 10;
    private double n = 1.7;
    private int lambda = 2;

//    private Variable variable = Variable.R;
//    private double fixedVariableValue = 2;

    private Variable variable = Variable.Z;
    private double fixedVariableValue = 10;

    private int step1 = 100;
    private int step2 = 100;

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
        setTitle("ЧММФ курсовая");
        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultCloseOperation(JFrame.ABORT);
    }

    private XYDataset createDataset() {
//        AbstractScheme schemeImplicit = new SchemeImplicit(R, L, lambda, n, step1, step2, variable, fixedVariableValue);
        AbstractScheme schemeImplicit = null;
        ComplexMatrix resultImplicit = schemeImplicit.calculateResultMatrix();

//        AbstractScheme schemeCN = new SchemeCN(R, L, lambda, n, step1, step2, variable, fixedVariableValue);
        AbstractScheme schemeCN = null;
        ComplexMatrix resultMatrixCN = schemeCN.calculateResultMatrix();

        double[] arrayCN = null;
        double[] arrayImplicit = null;
        double h;
        int layer;

        if (variable == Variable.Z) {
            h = schemeCN.getH_r();
            layer = (int) (fixedVariableValue / schemeImplicit.getH_z());
            arrayCN = resultMatrixCN.getColumn(layer + 1);
            arrayImplicit = resultImplicit.getColumn(layer + 1);
        } else {
            h = schemeCN.getH_z();
            layer = (int) (fixedVariableValue / schemeCN.getH_r());
            arrayCN = resultMatrixCN.getRow(layer + 1);
            arrayImplicit = resultImplicit.getRow(layer + 1);
        }

        var seriesCN = new XYSeries("схема Кранка-Николсона");
        var seriesImplicit = new XYSeries("неявная схема");

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

        String tittle = String.format("График при %s = %.2f, I = %d, J = %d", variable.name().toLowerCase(),
                fixedVariableValue, step1, step2);

        JFreeChart chart = ChartFactory.createXYLineChart(
                tittle,
                variable == Variable.Z ? "r" : "z",
                "|U(r,z)|",
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
        renderer.setSeriesShapesVisible(0, false);

        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(2.0f));
        renderer.setSeriesShapesVisible(1, false);

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle(tittle,
                        new Font("Serif", Font.BOLD, 18)
                )
        );

        return chart;
    }
}





