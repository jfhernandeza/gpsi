/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.rank.Median;

/**
 * Works only in one dimension
 * @author juan
 */
public class gpsiDistanceOfMediansScore implements gpsiSampleSeparationScore{

    @Override
    public double score(double[][][] input) {
        
        Median median = new Median();
        
        RealMatrix matrix0 = MatrixUtils.createRealMatrix(input[0]);
        RealMatrix matrix1 = MatrixUtils.createRealMatrix(input[1]);
        
        return Math.abs(median.evaluate(matrix0.getColumn(0)) - median.evaluate(matrix1.getColumn(0)));
        
    }
    
}
