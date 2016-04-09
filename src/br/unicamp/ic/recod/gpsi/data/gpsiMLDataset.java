/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiFeatureVector;
import br.unicamp.ic.recod.gpsi.img.gpsiRoi;
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
    
    // TODO: Modify to consider folds
    public void loadDataset(gpsiRoiRawDataset rawDataset){
        
        this.encoder.loadLabels(rawDataset.getValidationEntities().keySet());
        
        HashMap<String, ArrayList<gpsiRoi>> trainingSet = rawDataset.getTrainingEntities();
        HashMap<String, ArrayList<gpsiRoi>> validationSet = rawDataset.getValidationEntities();
        HashMap<String, ArrayList<gpsiRoi>> testSet = rawDataset.getTestEntities();
        
        for(String label : trainingSet.keySet()){
            this.trainingEntities.put(this.encoder.getCode(label), new ArrayList<>());
            for(gpsiRoi mask : trainingSet.get(label)){
                this.trainingEntities.get(this.encoder.getCode(label)).add(this.descriptor.getFeatureVector(mask));
            }
        }
        
        for(String label : validationSet.keySet()){
            this.validationEntities.put(this.encoder.getCode(label), new ArrayList<>());
            for(gpsiRoi mask : validationSet.get(label)){
                this.validationEntities.get(this.encoder.getCode(label)).add(this.descriptor.getFeatureVector(mask));
            }
        }
        
        for(String label : testSet.keySet()){
            this.testEntities.put(this.encoder.getCode(label), new ArrayList<>());
            for(gpsiRoi mask : testSet.get(label)){
                this.testEntities.get(this.encoder.getCode(label)).add(this.descriptor.getFeatureVector(mask));
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
