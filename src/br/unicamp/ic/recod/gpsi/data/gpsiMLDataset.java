/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.img.gpsiEntity;
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
    
    public void loadTrainingSet(HashMap<Byte, ArrayList<gpsiEntity>> trainingSet, boolean override){

        if(override)
            this.trainingEntities = new HashMap<>();
        
        for(Byte label : trainingSet.keySet()){
            this.trainingEntities.put(label, new ArrayList<>());
            for(gpsiEntity entity : trainingSet.get(label)){
                this.trainingEntities.get(label).add(this.descriptor.getFeatureVector(entity));
            }
        }
        
    }
    
    public void loadValidationSet(HashMap<Byte, ArrayList<gpsiEntity>> validationSet, boolean override){
        
        if(override)
            this.validationEntities = new HashMap<>();
        
        for(Byte label : validationSet.keySet()){
            this.validationEntities.put(label, new ArrayList<>());
            for(gpsiEntity entity : validationSet.get(label)){
                this.validationEntities.get(label).add(this.descriptor.getFeatureVector(entity));
            }
        }
        
    }
    
    public void loadTestSet(HashMap<Byte, ArrayList<gpsiEntity>> testSet, boolean override){
        
        if(override)
            this.testEntities = new HashMap<>();
        
        for(Byte label : testSet.keySet()){
            this.testEntities.put(label, new ArrayList<>());
            for(gpsiEntity entity : testSet.get(label)){
                this.testEntities.get(label).add(this.descriptor.getFeatureVector(entity));
            }
        }
    }
    
    public void loadWholeDataset(gpsiRawDataset rawDataset, boolean override){
        
        loadTrainingSet(rawDataset.getTrainingEntities(), override);
        loadValidationSet(rawDataset.getValidationEntities(), override);
        loadTestSet(rawDataset.getTestEntities(), override);
        
    }
    
    public int getDimensionality(){
        
        for(byte label : this.trainingEntities.keySet())
            return this.trainingEntities.get(label).get(0).length;
        
        return 0;
        
    }
}
