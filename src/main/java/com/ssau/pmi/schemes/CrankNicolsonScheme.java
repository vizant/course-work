package com.ssau.pmi.schemes;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.utils.Variable;

public class CrankNicolsonScheme extends AbstractMethod {

    public CrankNicolsonScheme(double r, double l, double λ, double n, Variable fixedVariableType,
                               double[] fixedVariable, Integer j, Integer k) {
        super(r, l, λ, n, fixedVariableType, fixedVariable, j, k);
    }

    public ComplexMatrix calculateResultMatrix() {
        ComplexMatrix F = new ComplexMatrix(1, J);
        A.set(0, 1, 1);
        B.set(α.multiply(-2).add(1), 1, 1);
        C.set(α.multiply(2), 1, 1);
        for (int j = 1; j < J; j++) {
            A.set(α.multiply((1 - 1. / (2 * j)) / 2), 1, j + 1);
            B.set(α.negate().add(1), 1, j + 1);
            C.set(α.multiply((1 + 1. / (2 * j)) / 2), 1, j + 1);
        }
        C.set(0, 1, J);
        for (int j = 0; j <= J; j++) {
            U.set(psi(j * R / J, R), j + 1, 1);
        }
        for (int k = 1; k <= K; k++) {
            F.set(U.get(2, k).multiply(A.get(1, 1)).negate()
                    .add(U.get(1, k).multiply(B.get(1, 1).negate().add(2)))
                    .add(U.get(2, k).multiply(C.get(1, 1)).negate()), 1, 1);
            for (int j = 1; j < J; j++) {
                F.set((U.get(j, k).multiply(A.get(1, j + 1)).negate())
                        .add(U.get(j + 1, k).multiply(B.get(1, j + 1).negate().add(2)))
                        .add(U.get(j + 2, k).multiply(C.get(1, j + 1)).negate()), 1, j + 1);
            }
            p.set(C.get(1, 1).divide(B.get(1, 1)).negate(), 1, 1);
            q.set(F.get(1, 1).divide(B.get(1, 1)), 1, 1);
            for (int j = 1; j < J; j++) {
                p.set(C.get(1, j + 1)
                        .divide(B.get(1, j + 1).add(A.get(1, j + 1)
                                .multiply(p.get(1, j)))).negate(), 1, j + 1);
                q.set((F.get(1, j + 1)
                        .subtract(A.get(1, j + 1).multiply(q.get(1, j))))
                        .divide(B.get(1, j + 1).add(A.get(1, j + 1)
                                .multiply(p.get(1, j)))), 1, j + 1);
            }
            U.set(q.get(1, J), J, k + 1);
            for (int j = J - 2; j >= 0; j--) {
                U.set(p.get(1, j + 1).multiply(U.get(j + 2, k + 1)).add(q.get(1, j + 1)), j + 1, k + 1);
            }
        }
        return U;
    }

}
