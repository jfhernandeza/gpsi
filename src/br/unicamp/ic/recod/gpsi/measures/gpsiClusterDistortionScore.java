/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.stat.descriptive.moment.VectorialMean;

/**
 *
 * @author ra163128
 */
public class gpsiClusterDistortionScore extends gpsiSampleSeparationScore {

    @Override
    public double score(double[][][] samples) {
        
        double score = 0.0;
        double[] centroid;
        double[][] vectors;
        int nClasses = samples.length, m = 0, m_i = 0, i;
        
        VectorialMean meanOperator;
        EuclideanDistance distance = new EuclideanDistance();
        
        for(int label = 0; label < samples.length; label++){
            vectors = samples[label];
            meanOperator = new VectorialMean(vectors[0].length);
            for(i = 0; i < vectors.length; i++)
                meanOperator.increment(vectors[i]);
            centroid = meanOperator.getResult();       
            
            m_i = vectors.length;
            for(i = 0; i < m_i; i++){
                score += distance.compute(centroid, vectors[i]);
            }
            m += m_i;
        }
        
        if(score <= 1E-30)
            return Double.MAX_VALUE;

        return score / m;
        
    }

    
}
