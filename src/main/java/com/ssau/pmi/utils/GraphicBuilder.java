package com.ssau.pmi.utils;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.schemes.AbstractScheme;
import com.ssau.pmi.schemes.SchemeCN;
import com.ssau.pmi.schemes.SchemeImplicit;
import org.apache.commons.math3.complex.Complex;
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
import java.io.*;
import java.util.Arrays;
import java.util.function.Function;

public class GraphicBuilder extends JFrame {
    private SchemeParameters schemeParameters;
    private Font mainFont;
    private AbstractScheme scheme;

    public GraphicBuilder(SchemeParameters schemeParameters) {
        this.schemeParameters = schemeParameters;
        this.mainFont = new Font("Arial Unicode MS", Font.BOLD, 18);
    }

    public void initUI() {
        XYDataset dataset = createDataset();
        JFreeChart chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        chartPanel.setBackground(Color.white);
        add(chartPanel);
        pack();
        setTitle(Constants.CHMMF_COURSE);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private XYDataset createDataset() {

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
        printErrorsForCurrentScheme(1, 4, resultMatrix);
        var dataset = new XYSeriesCollection();

        Function<Integer, double[]> getSchemeLine;
        Function<Integer, Integer> getSchemeLayer;
        Function<Integer, String> getSeriesLabel;
        final double h;
        AbstractScheme finalScheme = scheme;
        if (schemeParameters.getVariable() == Variable.Z) {
            h = scheme.getH_r();
            getSchemeLine = resultMatrix::getColumn;
            getSchemeLayer = (x) -> (int) (schemeParameters.getFixedValues().get(x) / finalScheme.getH_z());
            getSeriesLabel = (x) -> (Variable.Z.name().toLowerCase() + Constants.SPACE + Constants.EQUAL
                    + Constants.SPACE + schemeParameters.getFixedValues().get(x));
        } else {
            h = scheme.getH_z();
            getSchemeLine = resultMatrix::getRow;
            getSchemeLayer = (x) -> (int) (schemeParameters.getFixedValues().get(x) / finalScheme.getH_r());
            getSeriesLabel = (x) -> (Variable.R.name().toLowerCase() + Constants.SPACE + Constants.EQUAL
                    + Constants.SPACE + schemeParameters.getFixedValues().get(x));
        }

        for (int i = 0; i < schemeParameters.getFixedValues().size(); i++) {

            int layer = getSchemeLayer.apply(i);
            double[] localArray = getSchemeLine.apply(layer + 1);

            var series = new XYSeries(getSeriesLabel.apply(i));

            for (int j = 0; j < localArray.length; j++) {
                series.add(j * h, localArray[j]);
            }
            dataset.addSeries(series);
        }

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        String title;

        if (schemeParameters.getSchemeType().equals(SchemeType.CN)) {
            title = String.format(Constants.PARAMETERS_INFO_CN,
                    schemeParameters.getStepR(),
                    schemeParameters.getStepZ(),
                    schemeParameters.getVariable().name().toLowerCase(),
                    Constants.FOR_CN);
        } else {
            title = String.format(Constants.PARAMETERS_INFO_IMPLICIT,
                    schemeParameters.getStepR(),
                    schemeParameters.getStepZ(),
                    schemeParameters.getVariable().name().toLowerCase(),
                    Constants.FOR_IMPLICIT);
        }

        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                schemeParameters.getVariable() == Variable.Z ? "r" : "z",
                "|U(r,z)|",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        final Paint[] colors = new Paint[]{
                new Color(255, 0, 0),
                new Color(255, 128, 0),
                new Color(255, 255, 0),
                new Color(128, 255, 0),
                new Color(0, 255, 0),
                new Color(0, 255, 255),
                new Color(0, 0, 255),
                new Color(64, 0, 255),
                new Color(128, 0, 255),
                new Color(255, 0, 191)
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
        chart.setTitle(new TextTitle(title, mainFont));

        return chart;
    }

    private void printErrorsForCurrentScheme(double r, double z, ComplexMatrix resultMatrix) {
        int I = 10000;
        int J = 10000;
        AbstractScheme idealScheme = createIdealScheme(I,J);
        int[] idealLayers = idealScheme.getSchemeLayersPoint(r, z);
        ComplexMatrix idealComplexMatrix = CalculatedComplexMatrix.createComplexMatrix(schemeParameters, idealScheme);
        Complex idealComplex = idealComplexMatrix.get(idealLayers[0] + 1, idealLayers[1] + 1);

        int[] layersPoint = scheme.getSchemeLayersPoint(r, z);
        Complex complex = resultMatrix.get(layersPoint[0] + 1, layersPoint[1] + 1);
        double error = idealComplex.subtract(complex).abs();
        System.out.println("error = " + error);

        AbstractScheme doubleScheme;
        if (schemeParameters.getSchemeType() == SchemeType.CN)
            doubleScheme = createIdealScheme(schemeParameters.getStepR() * 2, schemeParameters.getStepZ() * 2);
        else
            doubleScheme = createIdealScheme(schemeParameters.getStepR() * 2, schemeParameters.getStepZ() * 4);

        int[] doubleSchemeLayersPoint = doubleScheme.getSchemeLayersPoint(r, z);
        ComplexMatrix doubleComplexMatrix = doubleScheme.calculateResultMatrix();
        Complex doubleComplex = doubleComplexMatrix.get(doubleSchemeLayersPoint[0] + 1, doubleSchemeLayersPoint[1] + 1);
        double doubleError = idealComplex.subtract(doubleComplex).abs();
        System.out.println("double error = " + doubleError);

        System.out.println("result = " + error / doubleError + "\n\n");
    }

    private AbstractScheme createIdealScheme(int I, int J) {
        AbstractScheme scheme;

        if (schemeParameters.getSchemeType() == SchemeType.CN) {
            scheme = new SchemeCN(
                    schemeParameters.getR(),
                    schemeParameters.getL(),
                    schemeParameters.getLambda(),
                    schemeParameters.getN(),
                    I,
                    J
            );
        } else {
            scheme = new SchemeImplicit(
                    schemeParameters.getR(),
                    schemeParameters.getL(),
                    schemeParameters.getLambda(),
                    schemeParameters.getN(),
                    I,
                    J
            );
        }

        return scheme;
    }

//    private ComplexMatrix getIdealComplexMatrix(int I, int J) {
//        ComplexMatrix idealComplexMatrix = null;
//        String fileName;
//
//        if (schemeParameters.getSchemeType() == SchemeType.CN) {
//            fileName = I + " " + J + " idealSchemeCN.ser";
//        } else {
//            fileName = I + " " + J + " idealSchemeImplicit.ser";
//        }
//        File file = new File(fileName);
//        System.out.println(file.exists());
//
//        if (!file.exists()) {
//            try (FileOutputStream outputStream = new FileOutputStream(fileName);
//                 BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
//                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream)) {
//                AbstractScheme idealScheme = createIdealScheme(10000, 10000);
//                idealComplexMatrix = idealScheme.calculateResultMatrix();
//                objectOutputStream.writeObject(idealComplexMatrix);
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        } else {
//            try (FileInputStream inputStream = new FileInputStream(fileName);
//                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//                 ObjectInputStream objectInputStream = new ObjectInputStream(bufferedInputStream)) {
//                idealComplexMatrix = (ComplexMatrix) objectInputStream.readObject();
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        }
//        return idealComplexMatrix;
//    }
}
