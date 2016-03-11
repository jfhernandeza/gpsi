/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import java.util.ArrayList;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author ra163128
 */
public class gpsiClusterDistortionScore implements gpsiSampleSeparationScore {

    @Override
    public double score(ArrayList<double[]> samples) {
        
        double score = 0.0, centroid;
        int nClasses = samples.size(), m = 0, m_i = 0;
        
        Mean meanOperator = new Mean();
        
        for(int i = 0; i < nClasses; i++){
            centroid = meanOperator.evaluate(samples.get(i));
            m_i = samples.get(i).length;
            for(int j = 0; j < m_i; j++){
                score += Math.pow(samples.get(i)[j] - centroid, 2.0);
            }
            m += m_i;
        }
        
        if(score <= 1E-30)
            return Double.MAX_VALUE;

        return score / m;
        
    }

    
}
