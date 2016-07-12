/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.stat.descriptive.rank.Max;
import org.apache.commons.math3.stat.descriptive.rank.Min;

/**
 *
 * @author juan
 */
public class gpsiDualScore implements gpsiSampleSeparationScore{

    @Override
    public double score(double[][][] input) {
        
        int least;
        
        Min min = new Min();
        Max max = new Max();
        
        double dist[][] = new double[2][];
        double limits[][] = new double[2][2];
        
        for(int i = 0; i <= 1; i++){
            dist[i] = MatrixUtils.createRealMatrix(input[i]).getColumn(0);
            limits[i] = new double[] {min.evaluate(dist[i]), max.evaluate(dist[i])};
        }
        
        least = limits[0][0] <= limits[1][0] ? 0 : 1;
        double sep = limits[1 - least][0] - limits[least][1];
        
        if(sep >= 0)
            return sep / (max.evaluate(new double[] {limits[0][1], limits[1][1]}) - min.evaluate(new double[] {limits[0][0], limits[1][0]}));
        
        double n = 0;
        
        for(int j = 0; j <= 1; j++)
            for(int i = 0; i < dist[j].length; i++){
                if(dist[j][i] >= limits[1 - least][0] && dist[j][i] <= limits[least][1])
                    n++;
            }
        
        return - n / (dist[0].length + dist[1].length);
        
    }
    
}
