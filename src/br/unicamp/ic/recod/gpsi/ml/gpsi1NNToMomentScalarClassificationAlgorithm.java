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
public class gpsi1NNToMomentScalarClassificationAlgorithm extends gpsiClassificationAlgorithm {
    
    private final AbstractUnivariateStatistic moment;
    private HashMap<Byte, double[]> centroids;

    public gpsi1NNToMomentScalarClassificationAlgorithm(AbstractUnivariateStatistic moment) {
        this.moment = moment;
    }

    @Override
    public void fit(HashMap<Byte, ArrayList<double[]>> x) {
        
        int i;
        double[] centroid;
        RealMatrix entities;
        
        this.centroids = new HashMap<>();
        this.nClasses = x.size();
        this.dimensionality = 0;
        for(byte label : x.keySet()){
            if(this.dimensionality <= 0)
                this.dimensionality = x.get(label).get(0).length;
            entities = MatrixUtils.createRealMatrix(x.get(label).toArray(new double[0][]));
            centroid = new double[this.dimensionality];
            for(i = 0; i < this.dimensionality; i++)
                centroid[i] = this.moment.evaluate(entities.getColumn(i));
            this.centroids.put(label, centroid);
        }
        
    }

    @Override
    public byte predict(double[] x) {
        
        //TODO: consider Euclidean disance instead of Manhattan for higher dimensionalities.
        
        byte minDistanceIndex = 0;
        double distance, minDistance = Double.POSITIVE_INFINITY;
        
        for(byte j : this.centroids.keySet()){
            distance = 0.0;
            for(int k = 0; k < this.dimensionality; k++)
                distance += Math.abs(x[k] - this.centroids.get(j)[k]);
            if(distance < minDistance){
                minDistanceIndex = j;
                minDistance = distance;
            }
        }
        
        return minDistanceIndex;
                
    }
    
}
