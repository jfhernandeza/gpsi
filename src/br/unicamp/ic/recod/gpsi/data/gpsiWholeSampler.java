/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiWholeSampler implements gpsiSampler{
    
    public gpsiWholeSampler() {
    }
    
    @Override
    public double[] sample(HashMap<Byte, ArrayList<gpsiVoxel>> entities, Byte label) {
        
        ArrayList<gpsiVoxel> samplingEntities = entities.get(label);
        double[] vector = new double[samplingEntities.size()];
        
        int i = 0;
        for(gpsiVoxel voxel : samplingEntities)
            vector[i++] = voxel.getCombinedValue();
        
        return vector;
    }
    
}
