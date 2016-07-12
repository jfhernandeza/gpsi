/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.Variance;

/**
 *
 * @author juan
 */
public class gpsiNormalBhattacharyyaDistanceScore implements gpsiSampleSeparationScore{

    @Override
    public double score(double[][][] input) {
        
        Mean mean = new Mean();
        Variance var = new Variance();
        
        double mu0, sigs0, mu1, sigs1;
        double dist[][] = new double[2][];
        
        dist[0] = MatrixUtils.createRealMatrix(input[0]).getColumn(0);
        dist[1] = MatrixUtils.createRealMatrix(input[1]).getColumn(0);
        
        mu0 = mean.evaluate(dist[0]);
        sigs0 = var.evaluate(dist[0]) + Double.MIN_VALUE;
        mu1 = mean.evaluate(dist[1]);
        sigs1 = var.evaluate(dist[1]) + Double.MIN_VALUE;
        
        double distance = (Math.log((sigs0 / sigs1 + sigs1 / sigs0 + 2) / 4) + (Math.pow(mu1 - mu0, 2.0)/(sigs0 + sigs1))) / 4;
        
        return distance == Double.POSITIVE_INFINITY ? 0 : distance;
        
    }
    
}
