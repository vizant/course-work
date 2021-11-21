package com.ssau.pmi.utils;

import com.ssau.pmi.complex.ComplexMatrix;
import com.ssau.pmi.schemes.AbstractScheme;

import java.util.Objects;

public class CalculatedComplexMatrix {
    private static ComplexMatrix complexMatrixCN;
    private static ComplexMatrix complexMatrixImplicit;

    public static ComplexMatrix createComplexMatrix(SchemeParameters schemeParameters, AbstractScheme idealScheme) {
        if(schemeParameters.getSchemeType() == SchemeType.CN){
            if(Objects.isNull(complexMatrixCN)){
                complexMatrixCN = idealScheme.calculateResultMatrix();
            }
            return complexMatrixCN;
        }
        else {
            if(Objects.isNull(complexMatrixImplicit)){
                complexMatrixImplicit = idealScheme.calculateResultMatrix();
            }
            return complexMatrixImplicit;
        }
    }
}
