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
import java.util.ArrayList;
import java.util.List;

public class GraphicBuilder extends JFrame {
    private SchemeParameters schemeParameters;
    private Font mainFont;
    private Font labelFont;
    private List<AbstractScheme> schemes;

    public GraphicBuilder(SchemeParameters schemeParameters) {
        this.schemeParameters = schemeParameters;
        this.mainFont = new Font("Arial Unicode MS", Font.BOLD, 20);
        this.labelFont = new Font("Arial Unicode MS", Font.BOLD, 15);
    }

    public void initUI() {
        schemes = new ArrayList<>();
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
            for (int i = 0; i < schemeParameters.getListStepR().size(); i++) {
                AbstractScheme localScheme = new SchemeCN(
                        schemeParameters.getR(),
                        schemeParameters.getL(),
                        schemeParameters.getLambda(),
                        schemeParameters.getN(),
                        schemeParameters.getListStepR().get(i),
                        schemeParameters.getListStepZ().get(i)
                );
                schemes.add(localScheme);
            }

        } else {
            for (int i = 0; i < schemeParameters.getListStepR().size(); i++) {
                AbstractScheme localScheme = new SchemeImplicit(
                        schemeParameters.getR(),
                        schemeParameters.getL(),
                        schemeParameters.getLambda(),
                        schemeParameters.getN(),
                        schemeParameters.getListStepR().get(i),
                        schemeParameters.getListStepZ().get(i)
                );
                schemes.add(localScheme);
            }
        }

        List<ComplexMatrix> resultMatrixes = new ArrayList<>();
        for (int i = 0; i < schemes.size(); i++) {
            resultMatrixes.add(schemes.get(i).calculateResultMatrix());
        }

        //printErrorsForCurrentScheme(1, 4);
        var dataset = new XYSeriesCollection();

        //Function<Integer, double[]> getSchemeLine;
        /*Function<Integer, Integer> getSchemeLayer;
        Function<Integer, String> getSeriesLabel;
        Function<Integer, Double> getH;
        BiFunction<Integer, Integer, double[]> getSchemeLine;
        if (schemeParameters.getVariable() == Variable.Z) {
            getH = (x) -> schemes.get(x).getH_r();
            getSchemeLine = (x, y) -> (resultMatrixes.get(x).getColumn(y));
            getSchemeLayer = (x) -> (int) (schemeParameters.getFixedValues().get(x) / finalScheme.getH_z());
            getSeriesLabel = (x) -> (Variable.Z.name().toLowerCase() + Constants.SPACE + Constants.EQUAL
                    + Constants.SPACE + schemeParameters.getFixedValues().get(x));
        } else {
            getH = (x) -> schemes.get(x).getH_z();
            getSchemeLine = resultMatrix::getRow;
            getSchemeLayer = (x) -> (int) (schemeParameters.getFixedValues().get(x) / finalScheme.getH_r());
            getSeriesLabel = (x) -> (Variable.R.name().toLowerCase() + Constants.SPACE + Constants.EQUAL
                    + Constants.SPACE + schemeParameters.getFixedValues().get(x));
        }*/

        for (int i = 0; i < schemeParameters.getFixedValues().size(); i++) {
            for (int j = 0; j < schemes.size(); j++) {
                final double h;
                AbstractScheme currentScheme = schemes.get(j);
                ComplexMatrix currentResultMatrix = resultMatrixes.get(j);
                if (schemeParameters.getVariable() == Variable.Z) {
                    h = currentScheme.getH_r();
                    int layer = (int) (schemeParameters.getFixedValues().get(i) / currentScheme.getH_z());
                    double[] schemeLine = currentResultMatrix.getColumn(layer + 1);
                    String label = Variable.Z.name().toLowerCase() + Constants.SPACE + Constants.EQUAL
                            + Constants.SPACE + schemeParameters.getFixedValues().get(i)
                            + " ( I=" + schemeParameters.getListStepR().get(j) + ", J=" + schemeParameters.getListStepZ().get(j) + ")";
                    var series = new XYSeries(label);
                    for (int k = 0; k < schemeLine.length; k++) {
                        series.add(k * h, schemeLine[k]);
                    }
                    dataset.addSeries(series);
                } else {
                    h = currentScheme.getH_z();
                    int layer = (int) (schemeParameters.getFixedValues().get(i) / currentScheme.getH_r());
                    double[] schemeLine = currentResultMatrix.getRow(layer + 1);
                    String label = Variable.R.name().toLowerCase() + Constants.SPACE + Constants.EQUAL
                            + Constants.SPACE + schemeParameters.getFixedValues().get(i)
                            + " ( I=" + schemeParameters.getListStepR().get(j) + ", J=" + schemeParameters.getListStepZ().get(j) + ")";
                    var series = new XYSeries(label);
                    for (int k = 0; k < schemeLine.length; k++) {
                        series.add(k * h, schemeLine[k]);
                    }
                    dataset.addSeries(series);
                }
            }

            /*int layer = getSchemeLayer.apply(i);
            double[] localArray = getSchemeLine.apply(layer + 1);

            var series = new XYSeries(getSeriesLabel.apply(i));

            for (int j = 0; j < localArray.length; j++) {
                series.add(j * h, localArray[j]);
            }
            dataset.addSeries(series);*/
        }

        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        String title;

        if (schemeParameters.getSchemeType().equals(SchemeType.CN)) {
            title = String.format(Constants.PARAMETERS_INFO_CN,
                    schemeParameters.getVariable().name().toLowerCase(),
                    Constants.FOR_CN);
        } else {
            title = String.format(Constants.PARAMETERS_INFO_IMPLICIT,
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
        chart.getLegend().setItemFont(labelFont);
        chart.setTitle(new TextTitle(title, mainFont));

        return chart;
    }

    /*private void printErrorsForCurrentScheme(double r, double z) {
        AbstractScheme firstScheme = createIdealScheme(schemeParameters.getStepR(), schemeParameters.getStepZ());
        int[] firstLayers = firstScheme.getSchemeLayersPoint(r, z);
        ComplexMatrix firstComplexMatrix = firstScheme.calculateResultMatrix();
        Complex firstComplex = firstComplexMatrix.get(firstLayers[0] + 1, firstLayers[1] + 1);

        AbstractScheme secondScheme = createIdealScheme(schemeParameters.getStepR(), schemeParameters.getStepZ() * 2);
        int[] secondLayers = secondScheme.getSchemeLayersPoint(r, z);
        ComplexMatrix secondComplexMatrix = secondScheme.calculateResultMatrix();
        Complex secondComplex = secondComplexMatrix.get(secondLayers[0] + 1, secondLayers[1] + 1);

        double firstError = firstComplex.subtract(secondComplex).abs();
        System.out.println("first  error = " + firstError);

        AbstractScheme thirdScheme = createIdealScheme(schemeParameters.getStepR(), schemeParameters.getStepZ() * 4);
        int[] thirdLayers = thirdScheme.getSchemeLayersPoint(r, z);
        ComplexMatrix thirdComplexMatrix = thirdScheme.calculateResultMatrix();
        Complex thirdComplex = thirdComplexMatrix.get(thirdLayers[0] + 1, thirdLayers[1] + 1);

        double secondError = thirdComplex.subtract(secondComplex).abs();
        System.out.println("second error = " + secondError);

        System.out.println("result = " + firstError / secondError + "\n\n");
    }*/

    private AbstractScheme createIdealScheme(int stepsI, int stepsJ) {
        AbstractScheme scheme;

        AbstractScheme doubleStepScheme;

        if (schemeParameters.getSchemeType() == SchemeType.CN) {
            scheme = new SchemeCN(
                    schemeParameters.getR(),
                    schemeParameters.getL(),
                    schemeParameters.getLambda(),
                    schemeParameters.getN(),
                    stepsI,
                    stepsJ
            );

        } else {
            scheme = new SchemeImplicit(
                    schemeParameters.getR(),
                    schemeParameters.getL(),
                    schemeParameters.getLambda(),
                    schemeParameters.getN(),
                    stepsI,
                    stepsJ
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
