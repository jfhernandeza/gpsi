/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.img.gpsiRoi;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jfhernandeza
 */
public class gpsiMLDataset extends gpsiDataset<double[]>{
    
    private final gpsiDescriptor descriptor;

    public gpsiMLDataset(gpsiDescriptor descriptor) {
             
        this.descriptor = descriptor;
        
    }
    
    // TODO: Modify to consider folds
    public void loadDataset(gpsiRoiRawDataset rawDataset){
        
        HashMap<Byte, ArrayList<gpsiRoi>> trainingSet = rawDataset.getTrainingEntities();
        HashMap<Byte, ArrayList<gpsiRoi>> validationSet = rawDataset.getValidationEntities();
        HashMap<Byte, ArrayList<gpsiRoi>> testSet = rawDataset.getTestEntities();
        
        for(Byte label : trainingSet.keySet()){
            this.trainingEntities.put(label, new ArrayList<>());
            for(gpsiRoi mask : trainingSet.get(label)){
                this.trainingEntities.get(label).add(this.descriptor.getFeatureVector(mask));
            }
        }
        
        for(Byte label : validationSet.keySet()){
            this.validationEntities.put(label, new ArrayList<>());
            for(gpsiRoi mask : validationSet.get(label)){
                this.validationEntities.get(label).add(this.descriptor.getFeatureVector(mask));
            }
        }
        
        for(Byte label : testSet.keySet()){
            this.testEntities.put(label, new ArrayList<>());
            for(gpsiRoi mask : testSet.get(label)){
                this.testEntities.get(label).add(this.descriptor.getFeatureVector(mask));
            }
        }
        
    }
    
    public int getDimensionality(){
        
        for(byte label : this.trainingEntities.keySet())
            return this.trainingEntities.get(label).get(0).length;
        
        return 0;
        
    }
}
