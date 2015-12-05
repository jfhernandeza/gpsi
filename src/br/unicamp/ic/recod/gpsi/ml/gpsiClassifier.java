/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

/**
 *
 * @author jfhernandeza
 */
public abstract class gpsiClassifier<T> {
    
    private T classificationAlgorithm;
    
    public abstract void fit(double[][] X, int[] y);
    public abstract int[] predict( double[][] X );

    public void setClassificationAlgorithm(T classificationAlgorithm) {
        this.classificationAlgorithm = classificationAlgorithm;
    }
        
}
