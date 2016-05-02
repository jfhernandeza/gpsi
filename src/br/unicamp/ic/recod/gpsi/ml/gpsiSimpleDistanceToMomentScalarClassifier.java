/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.AbstractStorelessUnivariateStatistic;

/**
 *
 * @author juan
 */
public class gpsiSimpleDistanceToMomentScalarClassifier {
    
    // TODO: Use only integer numbers to identify classes and generalize classifiers
    
    private final AbstractStorelessUnivariateStatistic moment;
    private double[] centroids;

    public gpsiSimpleDistanceToMomentScalarClassifier(AbstractStorelessUnivariateStatistic moment) {
        this.moment = moment;
    }

    public void fit(HashMap<String, double[]> X, String[] classLabels) {
        
        this.centroids = new double[classLabels.length];
        for(int i = 0; i < classLabels.length; i++)
            centroids[i] = this.moment.evaluate(X.get(classLabels[i]));
        
    }

    public String[] predict(double[] X, String[] classLabels) {
        
        if(this.centroids == null)
            return null;
        
        String[] labels = new String[X.length];
        
        int minDistanceIndex, i ,j;
        for(i = 0; i < X.length; i++){
            minDistanceIndex = 0;
            for(j = 1; j < classLabels.length; j++){
                if(Math.abs(X[i] - this.centroids[j]) < Math.abs(X[i] - this.centroids[minDistanceIndex]))
                    minDistanceIndex = j;
            }
            labels[i] = classLabels[minDistanceIndex];
        }
        
        return labels;
    }
    
    
    
}
