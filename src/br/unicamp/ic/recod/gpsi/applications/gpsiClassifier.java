/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.img.gpsiEntity;
import br.unicamp.ic.recod.gpsi.ml.gpsiClassificationAlgorithm;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiClassifier {
    
    private final gpsiClassificationAlgorithm algorithm;
    private final gpsiMLDataset dataset;
    private HashMap<Byte, byte[]> prediction;

    public gpsiClassifier(gpsiDescriptor descriptor, gpsiClassificationAlgorithm algorithm) {
        this.algorithm = algorithm;
        this.dataset = new gpsiMLDataset(descriptor);
    }
    
    public void fit(HashMap<Byte, ArrayList<gpsiEntity>> X) throws Exception{
        
        dataset.loadTrainingSet(X, true);
        algorithm.fit(dataset.getTrainingEntities());
        
    }
    
    public HashMap<Byte, byte[]> predict(HashMap<Byte, ArrayList<gpsiEntity>> X) throws Exception{
        
        dataset.loadTestSet(X, true);
        
        prediction = new HashMap<>();
        ArrayList<double[]> x;
        byte[] Y;
        
        for(byte label : X.keySet()){
            x = dataset.getTestEntities().get(label);
            Y = new byte[x.size()];
            for(int i = 0; i < x.size(); i++)
                Y[i] = algorithm.predict(x.get(i));
            
            prediction.put(label, Y);
            
        }
        
        return prediction;
    }
    
    public int[][] getConfusionMatrix(){
        
        int confusionMatrix[][] = new int [algorithm.getnClasses()][algorithm.getnClasses()];
        
        for(byte label : prediction.keySet())
            for(byte predictedLabel : prediction.get(label))
                confusionMatrix[label][predictedLabel]++;
        
        return confusionMatrix;
                
    }
    
}
