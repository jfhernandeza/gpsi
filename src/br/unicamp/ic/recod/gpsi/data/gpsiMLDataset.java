/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiFeatureVector;
import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiMask;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jfhernandeza
 */
public class gpsiMLDataset extends gpsiDataset<gpsiFeatureVector, Integer>{
    
    private final gpsiLabelEncoder encoder;
    private final gpsiDescriptor descriptor;

    public gpsiMLDataset(gpsiDescriptor descriptor) {
             
        this.encoder = new gpsiLabelEncoder();
        this.descriptor = descriptor;
        
    }
    
    public void loadDataset(gpsiRoiRawDataset rawDataset, gpsiCombinedImage combinedImage){
        
        this.encoder.loadLabels(rawDataset.getValidationEntities().keySet());
        
        HashMap<String, ArrayList<gpsiMask>> trainingSet = rawDataset.getTrainingEntities();
        HashMap<String, ArrayList<gpsiMask>> testSet = rawDataset.getValidationEntities();
        
        for(String label : trainingSet.keySet()){
            this.trainingEntities.put(this.encoder.getCode(label), new ArrayList<>());
            for(gpsiMask mask : trainingSet.get(label)){
                this.trainingEntities.get(this.encoder.getCode(label)).add(this.descriptor.getFeatureVector(combinedImage, mask));
            }
        }
        
        for(String label : testSet.keySet()){
            this.validationEntities.put(this.encoder.getCode(label), new ArrayList<>());
            for(gpsiMask mask : testSet.get(label)){
                this.validationEntities.get(this.encoder.getCode(label)).add(this.descriptor.getFeatureVector(combinedImage, mask));
            }
        }
        
        
    }
    
    public int getDimensionality(){
        
        for(int label : this.trainingEntities.keySet()){
            return this.trainingEntities.get(label).get(0).getDimensionality();
        }
        
        return 0;
        
    }
}
