package com.ssau.pmi.complex;

import org.apache.commons.math3.complex.Complex;

import java.io.Serializable;
import java.util.Arrays;

public class ComplexMatrix implements Serializable {
    private final int rows;
    private final int columns;
    private final Complex[][] elements;

    public ComplexMatrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        elements = new Complex[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elements[i][j] = Complex.ZERO;
            }
        }
    }

    public int getRowDimension() {
        return rows;
    }

    public int getColumnDimension() {
        return columns;
    }

    public void set(Complex element, int row, int column) throws IllegalArgumentException {
        elements[row - 1][column - 1] = element;
    }

    public void set(double element, int row, int column) throws IllegalArgumentException {
        elements[row - 1][column - 1] = new Complex(element, 0);
    }

    public Complex get(int row, int column) throws ArrayIndexOutOfBoundsException {
        return elements[row - 1][column - 1];
    }

    public double[] getRow(int row) {
        return Arrays.stream(elements[row - 1]).mapToDouble(Complex::abs).toArray();
    }

    public double[] getColumn(int column) {
        return Arrays.stream(elements).mapToDouble(array -> array[column - 1].abs()).toArray();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Complex[] element : elements) {
            for (int j = 0; j < elements[0].length; j++) {
                Complex complex = element[j];
                result.append(String.format("%.2f %.2fi  ", complex.getReal(), complex.getImaginary()));
            }
            result.append("\n");
        }
        return result.toString();
    }
}