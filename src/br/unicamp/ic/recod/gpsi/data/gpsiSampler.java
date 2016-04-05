/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiSampler {
    
    private gpsiSampler() {
    }
    
    public static gpsiSampler getInstance() {
        return gpsiSamplerHolder.INSTANCE;
    }
    
    private static class gpsiSamplerHolder {
        private static final gpsiSampler INSTANCE = new gpsiSampler();
    }
    
    public double[] sample(HashMap<String, ArrayList<gpsiVoxel>> entities, String className, gpsiCombinedImage combImage){
                
        ArrayList<gpsiVoxel> samplingEntities = entities.get(className);
        
        double[] vector = new double[samplingEntities.size()];
        
        int i = 0;
        for(gpsiVoxel voxel : samplingEntities)
            vector[i++] = combImage.img[voxel.getVector()[0]][voxel.getVector()[1]];
        
        return vector;
        
    }
    
}
