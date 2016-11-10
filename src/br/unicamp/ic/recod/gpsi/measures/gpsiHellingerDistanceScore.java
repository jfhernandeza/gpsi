/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;

/**
 *
 * @author juan
 */
public class gpsiHellingerDistanceScore extends gpsiSampleSeparationScore{

    @Override
    public double score(double[][][] input) {
        
        double dist[][] = new double[2][];
        
        int bins = 1000;
        
        dist[0] = MatrixUtils.createRealMatrix(input[0]).getColumn(0);
        dist[1] = MatrixUtils.createRealMatrix(input[1]).getColumn(0);
        
        gpsiHistogram hist = new gpsiHistogram();
        double globalMin = (new Min()).evaluate(ArrayUtils.addAll(dist[0], dist[1]));
        double globalMax = (new Max()).evaluate(ArrayUtils.addAll(dist[0], dist[1]));
        
        double[] h0 = hist.distribution(dist[0], bins, globalMin, globalMax);
        double[] h1 = hist.distribution(dist[1], bins, globalMin, globalMax);
        
        double BC = 0.0;
        
        for(int i = 0; i < bins; i++)
            BC += Math.sqrt(h0[i] * h1[i]);
        
        return Math.sqrt(1 - BC);
        
    }
    
}
