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
        
        double score = -1.0;
        int m = 0;
        
        for(int i = 0; i < samples.size(); i++)
            m += samples.get(i).length;
        
        int[] clusterAssignment = new int[m];
        double[] values = new double[m];
        double[][] distanceMatrix = new double[m][m];
        
        int mIndex = 0;
        for(int i = 0; i < samples.size(); i++)
            for(int j = 0; j < samples.get(i).length; j++){
                values[mIndex] = samples.get(i)[j];
                clusterAssignment[mIndex] = i;
                mIndex++;
            }
        
        for(int i = 0; i < m - 1; i++)
            for(int j = i + 1; j < m; j++)
                distanceMatrix[i][j] = Math.abs(values[i] - values[j]);
        
        double dissimilarity[] = new double[samples.size()];
        double a, b;
        for(int i = 0; i < m; i++){
            Arrays.fill(dissimilarity, 0.0);
            for(int j = 0; j < m; j++)
                if(i != j)
                    dissimilarity[clusterAssignment[j]] += distanceMatrix[Math.min(i, j)][Math.max(i, j)];
            
            for(int j = 0; j < dissimilarity.length; j++)
                if(clusterAssignment[i] == j)
                    dissimilarity[j] /= (samples.get(j).length - 1);
                else
                    dissimilarity[j] /= samples.get(j).length;
            
            a = dissimilarity[clusterAssignment[i]];
            b = Double.MAX_VALUE;
            for(int j = 0; j < dissimilarity.length; j++)
                if(clusterAssignment[i] != j)
                    b = Math.min(b, dissimilarity[j]);

            score += (b - a) / Math.max(a, b);
            
        }
        
        return 1.0 + score / m;
        
    }
    
}
