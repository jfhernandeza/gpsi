/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author juan
 */
public class gpsiBootstrapper implements gpsiSampler{

    private double p;

    public gpsiBootstrapper(double p) {
        this.p = p;
    }
    
    @Override
    public double[][][] sample(HashMap<Byte, ArrayList<double[]>> entities, Byte[] labels) {
        
        double[][][] vector = new double[labels.length][][];
        ArrayList<double[]> samplingEntities;
        Random rand = new Random();
        int n;
        
        for(int i = 0; i < labels.length; i++){
            samplingEntities = entities.get(labels[i]);
            n = (int) (samplingEntities.size() * p);
            vector[i] = new double[n][];
            for(int j = 0; j < n; j++)
                vector[i][j] = samplingEntities.get(rand.nextInt(samplingEntities.size()));
        }
        
        return vector;
    }
    
}
