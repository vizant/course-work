package com.ssau.pmi.schemes;

import com.ssau.pmi.complex.ComplexMatrix;

public abstract class AbstractScheme {
    protected double L;
    protected double R;
    protected double lambda;
    protected double n;
    protected double h_z;
    protected double h_r;
    protected double k;

    public AbstractScheme(double R, double L, double lambda, double n) {
        this.L = L;
        this.R = R;
        this.lambda = lambda;
        this.n = n;
        k = 2 * Math.PI / lambda;
    }

    protected double psi(double r, double R) {
        return Math.exp(-1 * Math.pow(5 * r / R, 2));
    }

    public abstract ComplexMatrix calculateResultMatrix();

    public double getH_z() {
        return h_z;
    }

    public double getH_r() {
        return h_r;
    }

    public int[] getSchemeLayersPoint(double r, double z) {
        int rLayer = (int) (r / h_r);
        int zLayer = (int) (z / h_z);
        return new int[]{rLayer, zLayer};
    }
}
