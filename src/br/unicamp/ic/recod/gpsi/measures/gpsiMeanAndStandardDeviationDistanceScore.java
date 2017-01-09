/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.VectorialMean;

/**
 *
 * @author juan
 */
public class gpsiMeanAndStandardDeviationDistanceScore extends gpsiSampleSeparationScore{

    @Override
    public double score(double[][][] input) {
        
        double[][] means  = new double[2][];
        double[] sDistances;
        VectorialMean mean;
        Mean mean_ = new Mean();
        EuclideanDistance distance = new EuclideanDistance();
        
        int i, j;
        double d = 0;
        
        for(i = 0; i < 2; i++){
            mean = new VectorialMean(input[i][0].length);
            for(j = 0; j < input[i].length; j++)
                mean.increment(input[i][j]);
            means[i] = mean.getResult();
        }
        
        d = distance.compute(means[0], means[1]);
        
        double deviations = Double.NEGATIVE_INFINITY;
        
        for(i = 0; i < 2; i++){
            sDistances = new double[input[i].length];
            for(j = 0; j < input[i].length; j++)
                sDistances[j] = distance.compute(means[i], input[i][j]);
            deviations = Math.max(deviations, mean_.evaluate(sDistances));
        }
        
        return d / deviations;
        
    }
    
}
