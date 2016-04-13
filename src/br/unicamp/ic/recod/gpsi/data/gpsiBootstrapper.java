/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
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
    public double[] sample(HashMap<String, ArrayList<gpsiVoxel>> entities, String label) {
        ArrayList<gpsiVoxel> samplingEntities = entities.get(label);
        
        int n = (int) (samplingEntities.size() * p);
        
        double[] vector = new double[n];
        Random rand = new Random();
        for(int i = 0; i < n; i++)
            vector[i] = samplingEntities.get(rand.nextInt(samplingEntities.size())).getCombinedValue();
        
        return vector;
    }
    
}
