/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

/**
 *
 * @author ra163128
 */
public class gpsiWilcoxonRankSumTestScore implements gpsiSampleSeparationScore {
    
    @Override
    public double score(double[][][] samples) {
        double p_value;
        
        RealMatrix m0 = MatrixUtils.createRealMatrix(samples[0]);
        RealMatrix m1 = MatrixUtils.createRealMatrix(samples[1]);
        
        MannWhitneyUTest t = new MannWhitneyUTest();
        p_value = t.mannWhitneyUTest(m0.getColumn(0), m1.getColumn(0));
        
        return p_value;
    }
    
}
