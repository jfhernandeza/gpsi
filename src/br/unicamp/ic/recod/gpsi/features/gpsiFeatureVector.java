/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.features;

/**
 *
 * @author jfhernandeza
 */
public class gpsiFeatureVector {
    
    private int dimensionality;
    private double[] features;

    public gpsiFeatureVector(double[] features) {
        this.features = features;
        this.dimensionality = features.length;
    }

    public int getDimensionality() {
        return dimensionality;
    }

    public double[] getFeatures() {
        return features;
    }
    
}
