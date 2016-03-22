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

/**
 *
 * @author jfhernandeza
 */
public class gpsiMLDataset extends gpsiDataset<gpsiFeatureVector, Integer>{
    
    private final gpsiLabelEncoder encoder;
    private final gpsiDescriptor descriptor;

    public gpsiMLDataset(gpsiDescriptor descriptor) {
        
        this.trainingEntities = new ArrayList<>();
        this.trainingLabels = new ArrayList<>();        
        this.encoder = new gpsiLabelEncoder();
        this.descriptor = descriptor;
        
    }
    
    public void loadDataset(gpsiRoiRawDataset rawDataset, gpsiCombinedImage combinedImage){
        
        ArrayList<String> rawLabels = rawDataset.getTrainingLabels();
        ArrayList<gpsiMask> rawEntities = rawDataset.getTrainingEntities();
        
        this.encoder.loadLabels(rawLabels);
        
        for(int i = 0; i < rawEntities.size(); i++){
            this.trainingEntities.add(this.descriptor.getFeatureVector(combinedImage, rawEntities.get(i)));
            this.trainingLabels.add(this.encoder.getCode(rawLabels.get(i)));
        }
        
    }
    
    public int getDimensionality(){
        return this.trainingEntities.get(0).getDimensionality();
    }
}
