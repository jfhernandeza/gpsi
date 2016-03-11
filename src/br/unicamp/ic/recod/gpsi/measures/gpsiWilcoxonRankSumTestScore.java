/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import java.util.ArrayList;
import org.apache.commons.math3.stat.inference.MannWhitneyUTest;

/**
 *
 * @author ra163128
 */
public class gpsiWilcoxonRankSumTestScore implements gpsiSampleSeparationScore {
    
    @Override
    public double score(ArrayList<double[]> samples) {
        double p_value;
        
        MannWhitneyUTest t = new MannWhitneyUTest();
        p_value = t.mannWhitneyUTest(samples.get(0), samples.get(1));
        
        return p_value;
    }
    
}
