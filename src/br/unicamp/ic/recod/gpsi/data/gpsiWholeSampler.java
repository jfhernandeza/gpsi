/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiWholeSampler implements gpsiSampler{

    @Override
    public double[][][] sample(HashMap<Byte, ArrayList<double[]>> entities, Byte[] labels) {
        
        double[][][] sample = new double[entities.size()][][];
        
        int i = 0, j;
        for(byte label : entities.keySet()){
            sample[i] = new double[entities.get(label).size()][];
            for(j = 0; j < entities.get(label).size(); j++){
                sample[i][j] = entities.get(label).get(j);
            }
            i++;
        }
        
        return sample;
        
    }
    
}
