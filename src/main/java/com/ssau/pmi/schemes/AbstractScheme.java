package com.ssau.pmi.schemes;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.utils.Variable;
import org.apache.commons.math3.complex.Complex;

public abstract class AbstractScheme {
    protected double L;
    protected double R;
    protected double lambda;
    protected double n;
    protected Variable fixedVariableType;
    protected double fixedVariableValue;
    protected double h_z;
    protected double h_r;
    protected double k;

    public AbstractScheme(double R, double L, double lambda, double n,
                          Variable fixedVariableType, double fixedVariableValue) {
        this.L = L;
        this.R = R;
        this.lambda = lambda;
        this.n = n;
        this.fixedVariableType = fixedVariableType;
        this.fixedVariableValue = fixedVariableValue;
        k = 2 * Math.PI / lambda;
    }

    protected double psi(double r, double R) {
        return Math.exp(-1 * Math.pow(5 * r / R, 2));
    }

    public abstract ComplexMatrix calculateResultMatrix();
}
