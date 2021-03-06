/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.img.gpsiEntity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiClassifier {
    
    private final gpsiClassificationAlgorithm algorithm;
    private final gpsiMLDataset dataset;
    private HashMap<Byte, byte[]> prediction;
    private HashMap<Byte, ArrayList<HashMap<Byte, Double>>> confidence;

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
        confidence = new HashMap<>();
        ArrayList<double[]> x;
        byte[] Y;
        ArrayList<HashMap<Byte, Double>> C;
        
        for(byte label : X.keySet()){
            x = dataset.getTestEntities().get(label);
            Y = new byte[x.size()];
            C = new ArrayList<>();
            for(int i = 0; i < x.size(); i++){
                Y[i] = algorithm.predict(x.get(i));
                C.add(algorithm.getConfidence());
            }
            
            prediction.put(label, Y);
            confidence.put(label, C);
            
        }
        
        return prediction;
    }
    
    public int[][] getConfusionMatrix(){
        
        HashMap<Byte, Integer> indices = new HashMap<>();
        
        int confusionMatrix[][] = new int [algorithm.getnClasses()][algorithm.getnClasses()];
        
        Byte[] labels = dataset.getTrainingEntities().keySet().toArray(new Byte[] {});
        Arrays.sort(labels);
        
        for(int i = 0; i < labels.length; i++){
            indices.put(labels[i], i);
        }
        
        for(byte label : prediction.keySet())
            for(byte predictedLabel : prediction.get(label))
                confusionMatrix[indices.get(label)][indices.get(predictedLabel)]++;
        
        return confusionMatrix;
                
    }

    public HashMap<Byte, ArrayList<HashMap<Byte, Double>>> getConfidence() {
        return confidence;
    }
    
}
