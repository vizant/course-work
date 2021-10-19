package com.ssau.pmi.schemes;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.utils.Variable;
import org.apache.commons.math3.complex.Complex;

public class SchemeCN extends AbstractScheme {
    private Integer I;
    private Integer J;

    public SchemeCN(double R, double L, double lambda, double n, Integer I,
                    Integer J, Variable fixedVariableType, double fixedVariableValue) {
        super(R, L, lambda, n, fixedVariableType, fixedVariableValue);
        this.I = I;
        this.J = J;
        h_z = L / J;
        h_r = R / I;
    }

    @Override
    public ComplexMatrix calculateResultMatrix() {

        ComplexMatrix result = new ComplexMatrix(I + 1, J + 1);
        for (int i = 0; i <= I; i++) {
            result.set(psi(i * h_r, R), i + 1, 1);
        }

        Complex theta = new Complex(0, (h_z / (2 * k * n * Math.pow(h_r, 2))));

        ComplexMatrix A = new ComplexMatrix(1, I);
        ComplexMatrix B = new ComplexMatrix(1, I);
        ComplexMatrix C = new ComplexMatrix(1, I);

        A.set(0, 1, 1);
        B.set(theta.multiply(-2).add(1), 1, 1);
        C.set(theta.multiply(2), 1, 1);
        for (int i = 1; i < I; i++) {
            A.set(theta.multiply((1 - 1. / (2 * i)) / 2), 1, i + 1);
            B.set(theta.negate().add(1), 1, i + 1);
            C.set(theta.multiply((1 + 1. / (2 * i)) / 2), 1, i + 1);
        }
        C.set(0, 1, I);

        ComplexMatrix p = new ComplexMatrix(1, I);
        ComplexMatrix q = new ComplexMatrix(1, I);
        ComplexMatrix D = new ComplexMatrix(1, I);

        for (int j = 1; j <= J; j++) {
            D.set(result.get(2, j).multiply(A.get(1, 1)).negate()
                    .add(result.get(1, j).multiply(B.get(1, 1).negate().add(2)))
                    .add(result.get(2, j).multiply(C.get(1, 1)).negate()), 1, 1);

            for (int i = 1; i < I; i++) {
                D.set((result.get(i, j).multiply(A.get(1, i + 1)).negate())
                        .add(result.get(i + 1, j).multiply(B.get(1, i + 1).negate().add(2)))
                        .add(result.get(i + 2, j).multiply(C.get(1, i + 1)).negate()), 1, i + 1);
            }

            p.set(C.get(1, 1).divide(B.get(1, 1)).negate(), 1, 1);
            q.set(D.get(1, 1).divide(B.get(1, 1)), 1, 1);

            for (int i = 1; i < I; i++) {
                p.set(C.get(1, i + 1)
                        .divide(B.get(1, i + 1).add(A.get(1, i + 1)
                                .multiply(p.get(1, i)))).negate(), 1, i + 1);
                q.set((D.get(1, i + 1)
                        .subtract(A.get(1, i + 1).multiply(q.get(1, i))))
                        .divide(B.get(1, i + 1).add(A.get(1, i + 1)
                                .multiply(p.get(1, i)))), 1, i + 1);
            }

            result.set(q.get(1, I), I, j + 1);
            for (int i = I - 2; i >= 0; i--) {
                result.set(p.get(1, i + 1).multiply(result.get(i + 2, j + 1)).add(q.get(1, i + 1)), i + 1, j + 1);
            }
        }

        return result;
    }


}
