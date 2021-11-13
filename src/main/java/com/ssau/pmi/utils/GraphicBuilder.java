package com.ssau.pmi.utils;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.schemes.AbstractScheme;
import com.ssau.pmi.schemes.SchemeCN;
import com.ssau.pmi.schemes.SchemeImplicit;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;

public class GraphicBuilder extends JFrame {
    private SchemeParameters schemeParameters;

    public GraphicBuilder(SchemeParameters schemeParameters) {
        this.schemeParameters = schemeParameters;
    }

    public void initUI() {
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
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    private XYDataset createDataset() {
        AbstractScheme scheme = null;

        if (schemeParameters.getSchemeType() == SchemeType.CN) {
            scheme = new SchemeCN(
                    schemeParameters.getR(),
                    schemeParameters.getL(),
                    schemeParameters.getLambda(),
                    schemeParameters.getN(),
                    schemeParameters.getStepR(),
                    schemeParameters.getStepZ()
            );
        } else {
            scheme = new SchemeImplicit(
                    schemeParameters.getR(),
                    schemeParameters.getL(),
                    schemeParameters.getLambda(),
                    schemeParameters.getN(),
                    schemeParameters.getStepR(),
                    schemeParameters.getStepZ()
            );
        }

        ComplexMatrix resultMatrix = scheme.calculateResultMatrix();

        var dataset = new XYSeriesCollection();

        for (int i = 0; i < schemeParameters.getFixedValues().size(); i++) {
            double[] localArray = null;
            double h;
            int layer;

            if (schemeParameters.getVariable() == Variable.Z) {
                h = scheme.getH_r();
                layer = (int) (schemeParameters.getFixedValues().get(i) / scheme.getH_z());
                localArray = resultMatrix.getColumn(layer + 1);
            } else {
                h = scheme.getH_z();
                layer = (int) (schemeParameters.getFixedValues().get(i) / scheme.getH_r());
                localArray = resultMatrix.getRow(layer + 1);
            }

            var series = new XYSeries("значение = " + schemeParameters.getFixedValues().get(i));

            for (int j = 0; j < localArray.length; j++) {
                series.add(j * h, localArray[j]);
            }
            dataset.addSeries(series);
        }

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        String tittle = String.format("График(и) I = %d, J = %d при фиксированном %s, схема %s",
                schemeParameters.getStepR(),
                schemeParameters.getStepZ(),
                schemeParameters.getVariable().name(),
                schemeParameters.getSchemeType().name());

        JFreeChart chart = ChartFactory.createXYLineChart(
                tittle,
                schemeParameters.getVariable() == Variable.Z ? "r" : "z",
                "|U(r,z)|",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final Paint[] colors = new Paint[]{
                Color.magenta,
                Color.blue,
                Color.green,
                Color.black,
                Color.orange,
                Color.red,
                Color.cyan,
                Color.pink,
                Color.gray,
                Color.yellow
        };

        var renderer = new XYLineAndShapeRenderer();
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            renderer.setSeriesPaint(i, colors[i]);
            renderer.setSeriesStroke(i, new BasicStroke(2.0f));
            renderer.setSeriesShapesVisible(i, false);
        }

        XYPlot plot = chart.getXYPlot();
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
