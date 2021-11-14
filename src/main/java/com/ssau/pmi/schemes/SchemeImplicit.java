package com.ssau.pmi.schemes;

import com.ssau.pmi.complex.ComplexMatrix;
import org.apache.commons.math3.complex.Complex;

public class SchemeImplicit extends AbstractScheme {
    private final Integer J;
    private final Integer K;

    public SchemeImplicit(double R, double L, double lambda, double n, Integer J, Integer K) {
        super(R, L, lambda, n);
        this.J = J;
        this.K = K;
        h_z = L / K;
        h_r = R / J;
    }

    @Override
    public ComplexMatrix calculateResultMatrix() {
        ComplexMatrix result = new ComplexMatrix(J + 1, K + 1);
        for (int j = 0; j <= J; j++) {
            result.set(psi(j * h_r, R), j + 1, 1);
        }

        Complex theta = new Complex(0, (h_z / (2 * k * n * Math.pow(h_r, 2))));

        ComplexMatrix A = new ComplexMatrix(1, J);
        ComplexMatrix B = new ComplexMatrix(1, J);
        ComplexMatrix C = new ComplexMatrix(1, J);

        A.set(0, 1, 1);
        B.set(theta.multiply(-4).add(1), 1, 1);
        C.set(theta.multiply(4), 1, 1);
        for (int j = 1; j < J; j++) {
            A.set(theta.multiply(1 - 1. / (2 * j)), 1, j + 1);
            B.set(theta.multiply(-2).add(1), 1, j + 1);
            C.set(theta.multiply(1 + 1. / (2 * j)), 1, j + 1);
        }
        C.set(0, 1, J);

        ComplexMatrix p = new ComplexMatrix(1, J);
        ComplexMatrix q = new ComplexMatrix(1, J);

        for (int k = 1; k <= K; k++) {

            p.set(C.get(1, 1).divide(B.get(1, 1)).negate(), 1, 1);
            q.set(result.get(1, k).divide(B.get(1, 1)), 1, 1);

            for (int j = 1; j < J; j++) {
                p.set(C.get(1, j + 1)
                        .divide(B.get(1, j + 1).add(A.get(1, j + 1)
                                .multiply(p.get(1, j)))).negate(), 1, j + 1);
                q.set((result.get(j + 1, k)
                        .subtract(A.get(1, j + 1).multiply(q.get(1, j))))
                        .divide(B.get(1, j + 1).add(A.get(1, j + 1)
                                .multiply(p.get(1, j)))), 1, j + 1);
            }

            result.set(q.get(1, J), J, k + 1);
            for (int j = J - 2; j >= 0; j--) {
                result.set(p.get(1, j + 1).multiply(result.get(j + 2, k + 1)).add(q.get(1, j + 1)), j + 1, k + 1);
            }

        }
        return result;

    }
}
