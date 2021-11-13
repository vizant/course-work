package com.ssau.pmi.utils;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SchemeParameters {
    private double R;
    private double L;
    private double N;
    private double lambda;
    private int stepR;
    private int stepZ;
    private List<Double> fixedValues;
    private Variable variable;
    private SchemeType schemeType;
}
