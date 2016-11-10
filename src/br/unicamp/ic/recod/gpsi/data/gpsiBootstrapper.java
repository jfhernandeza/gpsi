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
public abstract class gpsiBootstrapper implements gpsiSampler{
    
    protected static Random rand;

    public gpsiBootstrapper(long seed) {
        gpsiBootstrapper.rand = seed != 0 ? new Random(seed) : new Random();
    }
    
    protected double[][][] bootstrap(HashMap<Byte, ArrayList<double[]>> entities, Byte[] labels, int[] n){
        double[][][] vector = new double[labels.length][][];
        ArrayList<double[]> samplingEntities;

        for(int i = 0; i < labels.length; i++){
            samplingEntities = entities.get(labels[i]);
            vector[i] = new double[n[i % n.length]][];
            for(int j = 0; j < n[i % n.length]; j++)
                vector[i][j] = samplingEntities.get(rand.nextInt(samplingEntities.size()));
        }
        
        return vector;
    }
    
}
