/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author ra163128
 */
public class gpsiClusterSilhouetteScore implements gpsiSampleSeparationScore{

    @Override
    public double score(ArrayList<double[]> samples) {
        
        double score = 0.0;
        int m = 0, i, j, k;
        
        for(i = 0; i < samples.size(); i++)
            m += samples.get(i).length;
        
        int[] clusterAssignment = new int[m];
        double[] values = new double[m];
                
        double[][] distances = new double[m - 1][];
        for(i = 0; i < m - 1; i++)
            distances[i] = new double[m - i - 1];
        
        int mIndex = 0;
        for(i = 0; i < samples.size(); i++)
            for(j = 0; j < samples.get(i).length; j++){
                values[mIndex] = samples.get(i)[j];
                clusterAssignment[mIndex] = i;
                for(k = 0; k < mIndex; k++)
                    distances[mIndex - k - 1][k] = Math.abs(values[mIndex] - values[mIndex - k - 1]);
                mIndex++;
            }
        
        double dissimilarity[] = new double[samples.size()];
        double a, b;
        for(i = 0; i < m; i++){
            Arrays.fill(dissimilarity, 0.0);
            for(j = 0; j < m; j++)
                if(i != j){
                    k = Math.min(i, j);
                    dissimilarity[clusterAssignment[j]] += distances[k][Math.max(i, j) - 1 - k];
                }

            a = dissimilarity[clusterAssignment[i]] / (samples.get(clusterAssignment[i]).length - 1);
            b = Double.MAX_VALUE;
            for(j = 0; j < dissimilarity.length; j++)
                if(clusterAssignment[i] != j)
                    b = Math.min(b, dissimilarity[j] / samples.get(j).length);
            
            score += Math.max(a, b) == 0.0 ? -1.0 : (b - a) / Math.max(a, b);
            
        }
        
        return score / m;
        
    }
    
}
