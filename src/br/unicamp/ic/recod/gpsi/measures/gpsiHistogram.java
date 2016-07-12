/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

/**
 *
 * @author juan
 */
public class gpsiHistogram {
    
    public double[] distribution(double dist[], int bins, double min, double max){
        
        int[] f = frequencies(dist, bins, min, max);
        double[] d = new double[f.length];
        
        int sum = 0;
        for(int i = 0; i < f.length; i++)
            sum += f[i];
        
        for(int i = 0; i < f.length; i++)
            d[i] = (double) f[i] / sum;
        
        return d;
        
    }
    
    public int[] frequencies(double dist[], int bins, double min, double max){
        
        int frequencies[] = new int[bins];
        double delta = (max - min) / (bins - 1);
        
        int i, j;
        
        for(i = 0; i < dist.length; i++){
            for(j = 0; j < bins; j++){
                if(dist[i] < min + delta * (j + 0.5)){
                    frequencies[j]++;
                    break;
                }
            }
        }
        
        return frequencies;
        
    }
    
}
