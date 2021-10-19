package com.ssau.pmi;

import com.ssau.pmi.schemes.AbstractScheme;
import com.ssau.pmi.schemes.CrankNicolsonScheme;
import com.ssau.pmi.schemes.SchemeCN;
import com.ssau.pmi.utils.Variable;

public class MainClass {
    public static void main(String[] args) {
        double R = 4;
        double L = 10;
        double n = 1.7;
        int lambda = 2;

        AbstractScheme cn = new SchemeCN(R, L, lambda, n, 10, 10, Variable.Z, 5);
        System.out.println(cn.calculateResultMatrix().toString());


        CrankNicolsonScheme crankNicolsonScheme =
                new CrankNicolsonScheme(R, L, lambda, n, Variable.Z, new double[]{5},10, 10);
        System.out.println(crankNicolsonScheme.calculateResultMatrix().toString());

//        AbstractScheme impl = new SchemeCN(R, L, lambda, n, 10, 10, Variable.Z, 5);
//        System.out.println(impl.calculateResultMatrix().toString());


    }
}
