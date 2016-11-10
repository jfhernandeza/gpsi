/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;


import java.util.Arrays;
import org.apache.commons.math3.ml.distance.ManhattanDistance;

/**
 *
 * @author ra163128
 */
public class gpsiClusterSilhouetteScore extends gpsiSampleSeparationScore{

    public gpsiClusterSilhouetteScore() {
        this.optimum = 1.0;
    }
    
    @Override
    public double score(double[][][] samples) {
        
        ManhattanDistance distance = new ManhattanDistance();
        double score = 0.0;
        int m = 0, i, j, k;
        
        for(i = 0; i < samples.length; i++)
            m += samples[i].length;
        
        byte[] clusterAssignment = new byte[m];
        double[][] values = new double[m][];
                
        double[][] distances = new double[m - 1][];
        for(i = 0; i < m - 1; i++)
            distances[i] = new double[m - i - 1];
        
        int mIndex = 0;
        
        for(byte label = 0; label < samples.length; label++)
            for(i = 0; i < samples[label].length; i++){
                values[mIndex] = samples[label][i];
                clusterAssignment[mIndex] = label;
                for(j = 0; j < mIndex; j++)
                    distances[mIndex - j - 1][j] = distance.compute(values[mIndex], values[mIndex - j - 1]);
                mIndex++;
            }

        
        double dissimilarity[] = new double[samples.length];
        double a, b;
        for(i = 0; i < m; i++){
            Arrays.fill(dissimilarity, 0.0);
            for(j = 0; j < m; j++)
                if(i != j){
                    k = Math.min(i, j);
                    dissimilarity[clusterAssignment[j]] += distances[k][Math.max(i, j) - 1 - k];
                }

            a = dissimilarity[clusterAssignment[i]] / (samples[clusterAssignment[i]].length - 1);
            b = Double.MAX_VALUE;
            for(j = 0; j < dissimilarity.length; j++)
                if(clusterAssignment[i] != j)
                    b = Math.min(b, dissimilarity[j] / samples[j].length);
            
            score += Math.max(a, b) == 0.0 ? -1.0 : (b - a) / Math.max(a, b);
            
        }
        
        return score / m;
        
    }
    
}
