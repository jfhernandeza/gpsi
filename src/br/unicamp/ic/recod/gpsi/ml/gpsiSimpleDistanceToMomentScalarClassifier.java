/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.HashMap;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic;

/**
 *
 * @author juan
 */
public class gpsiSimpleDistanceToMomentScalarClassifier {
    
    private final AbstractUnivariateStatistic moment;
    private double[][] centroids;
    private int dimensionality, nClasses;

    public gpsiSimpleDistanceToMomentScalarClassifier(AbstractUnivariateStatistic moment) {
        this.moment = moment;
    }

    public void fit(HashMap<Byte, double[][]> x) {
        
        int i;
        RealMatrix entities;
        
        this.nClasses = x.keySet().size();
        for(byte label : x.keySet()){

            entities = MatrixUtils.createRealMatrix(x.get(label));
            if(dimensionality <= 0){
                this.dimensionality = entities.getColumnDimension();
                this.centroids = new double[this.nClasses][this.dimensionality];
            }
            
            for(i = 0; i < this.dimensionality; i++)
                this.centroids[label][i] = this.moment.evaluate(entities.getColumn(i));
               
        }
        
    }

    public int[][] predictAndEval(HashMap<Byte, double[][]> x) {
        
        if(this.centroids == null)
            return null;
        
        int confusionMatrix[][] = new int [this.nClasses][this.nClasses];
        int minDistanceIndex, i, j, k;
        double minDistance, distance;
        
        double[][] entities;
        for(byte label : x.keySet()){
            entities = x.get(label);
            for(i = 0; i < entities.length; i++){
                minDistanceIndex = 0;
                minDistance = Double.POSITIVE_INFINITY;
                for(j = 0; j < this.nClasses; j++){
                    distance = 0.0;
                    for(k = 0; k < this.dimensionality; k++)
                        distance += Math.abs(entities[i][k] - this.centroids[j][k]);
                    if(distance < minDistance){
                        minDistanceIndex = j;
                        minDistance = distance;
                    }
                }
                confusionMatrix[label][minDistanceIndex]++;
            }
        }
        
        return confusionMatrix;
        
    }
    
}
