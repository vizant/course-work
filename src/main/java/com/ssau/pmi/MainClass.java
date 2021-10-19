package com.ssau.pmi;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.schemes.AbstractScheme;
import com.ssau.pmi.schemes.CrankNicolsonScheme;
import com.ssau.pmi.schemes.SchemeCN;
import com.ssau.pmi.utils.Variable;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.DomainOrder;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.Arrays;

public class MainClass {
    public static void main(String[] args) {
        double R = 4;
        double L = 10;
        double n = 1.7;
        int lambda = 2;

        Variable variable = Variable.Z;
        double fixedVariableValue = 5;

        AbstractScheme cn = new SchemeCN(R, L, lambda, n, 10, 10, variable, fixedVariableValue);
        ComplexMatrix resultMatrix = cn.calculateResultMatrix();
//        System.out.println(cn.calculateResultMatrix().toString());

//        CrankNicolsonScheme crankNicolsonScheme =
//                new CrankNicolsonScheme(R, L, lambda, n, variable, new double[]{fixedVariableValue}, 10, 10);
//        System.out.println(crankNicolsonScheme.calculateResultMatrix().toString());


        JFreeChart chart = ChartFactory.createScatterPlot("", "U", "Z/R", createDataset());
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer r = (XYLineAndShapeRenderer) plot.getRenderer();
        r.setSeriesLinesVisible(1, Boolean.TRUE);
        r.setSeriesShapesVisible(1, Boolean.FALSE);

//        AbstractScheme impl = new SchemeCN(R, L, lambda, n, 10, 10, Variable.Z, 5);
//        System.out.println(impl.calculateResultMatrix().toString());
    }

    private static XYDataset createDataset() {
        var series = new XYSeries("2016");
        series.add(1, 2);
        series.add(3, 4);
        series.add(25, 800);
        series.add(30, 980);
        series.add(40, 1410);
        series.add(50, 2350);

        var dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    }
}

