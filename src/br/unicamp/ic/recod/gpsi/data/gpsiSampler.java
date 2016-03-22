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
    
    public double[] sample(HashMap indexesPerClass, ArrayList entities, String className, gpsiCombinedImage combImage){
                
        ArrayList<Integer> indices = (ArrayList) indexesPerClass.get(className);
        
        int size = indices.size();
        double[] vector = new double[size];
        
        int v[];
        for(int i = 0; i < size; i++){
            v = ((gpsiVoxel) entities.get(indices.get(i))).getVector();
            vector[i] = combImage.img[v[0]][v[1]];
        }
        
        return vector;
        
    }
    
}
