/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic;

/**
 *
 * @author juan
 */
public class gpsiSimpleDistanceToMomentScalarClassificationAlgorithm extends gpsiClassificationAlgorithm {
    
    private final AbstractUnivariateStatistic moment;
    private double[][] centroids;

    public gpsiSimpleDistanceToMomentScalarClassificationAlgorithm(AbstractUnivariateStatistic moment) {
        this.moment = moment;
    }

    @Override
    public void fit(HashMap<Byte, ArrayList<double[]>> x) {
        
        int i;
        RealMatrix entities;
        
        this.nClasses = x.keySet().size();
        this.dimensionality = Integer.MIN_VALUE;
        for(byte label : x.keySet())
            if(this.dimensionality < label)
                this.dimensionality = label;
        
        this.centroids = new double[this.nClasses][this.dimensionality];
        
        for(byte label : x.keySet()){
            entities = MatrixUtils.createRealMatrix(x.get(label).toArray(new double[0][]));
            for(i = 0; i < this.dimensionality; i++)
                this.centroids[label][i] = this.moment.evaluate(entities.getColumn(i));
               
        }
        
    }

    @Override
    public byte predict(double[] x) {
        
        byte minDistanceIndex = 0;
        double distance, minDistance = Double.POSITIVE_INFINITY;
        
        for(byte j = 0; j < this.nClasses; j++){
            distance = 0.0;
            for(int k = 0; k < this.dimensionality; k++)
                distance += Math.abs(x[k] - this.centroids[j][k]);
            if(distance < minDistance){
                minDistanceIndex = j;
                minDistance = distance;
            }
        }
        
        return minDistanceIndex;
                
    }
    
}
