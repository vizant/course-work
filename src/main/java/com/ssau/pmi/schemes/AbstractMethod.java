package com.ssau.pmi.schemes;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.utils.Variable;
import org.apache.commons.math3.complex.Complex;

public abstract class AbstractMethod {
    protected Integer J;
    protected Integer K;
    protected Integer nEigenfunction;
    protected double L;
    protected double R;
    protected double λ;
    protected double n;
    protected double[] fixedVariable;
    protected Variable fixedVariableType;
    protected Complex α;
    ComplexMatrix A;
    ComplexMatrix B;
    ComplexMatrix C;
    ComplexMatrix p;
    ComplexMatrix q;
    ComplexMatrix U;

    public AbstractMethod(double r, double l, double λ, double n, Variable fixedVariableType, double[] fixedVariable,
            Integer j, Integer k) {
        J = j;
        K = k;
        L = l;
        R = r;
        this.λ = λ;
        this.n = n;
        this.fixedVariable = fixedVariable;
        this.fixedVariableType = fixedVariableType;
        A = new ComplexMatrix(1, J);
        B = new ComplexMatrix(1, J);
        C = new ComplexMatrix(1, J);
        p = new ComplexMatrix(1, J);
        q = new ComplexMatrix(1, J);
        U = new ComplexMatrix(J + 1, K + 1);

        double h_z = L/K;
        double h_r = R/J;
//        Complex theta = new Complex(0, (h_z / (2 * (2 * Math.PI / λ) * n * Math.pow(h_r, 2))));
        α = new Complex(0, L * λ / (4 * K * Math.PI * n * Math.pow(R / J, 2)));
    }


    protected double psi(double r, double R) {
        return Math.exp(-1 * Math.pow(5 * r / R, 2));
    }


}
