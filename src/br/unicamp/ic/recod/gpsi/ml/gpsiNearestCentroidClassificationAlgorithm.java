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
public class gpsiNearestCentroidClassificationAlgorithm extends gpsiClassificationAlgorithm {
    
    private final AbstractUnivariateStatistic moment;
    private HashMap<Byte, double[]> centroids;

    public gpsiNearestCentroidClassificationAlgorithm(AbstractUnivariateStatistic moment) {
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
        
        confidence = new HashMap<>();
        
        for(byte j : this.centroids.keySet()){
            distance = 0.0;
            for(int k = 0; k < this.dimensionality; k++)
                distance += Math.abs(x[k] - this.centroids.get(j)[k]);
            if(distance < minDistance){
                minDistanceIndex = j;
                minDistance = distance;
            }
        }
        
        if(centroids.size() == 2 && this.dimensionality == 1){
            byte minC, maxC;
            Byte[] l = centroids.keySet().toArray(new Byte[] {});
            double conf;
            
            minC = centroids.get(l[0])[0] <= centroids.get(l[1])[0] ? l[0] : l[1];
            maxC = centroids.get(l[0])[0] > centroids.get(l[1])[0] ? l[0] : l[1];
            
            if(x[0] <= centroids.get(minC)[0]){
                this.confidence.put(minC, 1.0);
                this.confidence.put(maxC, 0.0);
            }else if (x[0] >= centroids.get(maxC)[0]){
                this.confidence.put(minC, 0.0);
                this.confidence.put(maxC, 1.0);
            }else{
                conf = minDistance / (centroids.get(maxC)[0] - centroids.get(minC)[0]);
                this.confidence.put(minDistanceIndex, 1 - conf);
                if(minDistanceIndex == minC)
                    this.confidence.put(maxC, conf);
                else
                    this.confidence.put(minC, conf);
            }
            
        }
        
        return minDistanceIndex;
                
    }
    
}
