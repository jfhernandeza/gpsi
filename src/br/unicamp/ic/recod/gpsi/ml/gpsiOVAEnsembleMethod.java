/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import br.unicamp.ic.recod.gpsi.img.gpsiEntity;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiOVAEnsembleMethod {
    
    private final gpsiClassifier classifiers[];
    private HashMap<Byte, byte[]> prediction;
    int nClasses;

    public gpsiOVAEnsembleMethod(gpsiClassifier[] classifiers) {
        this.classifiers = classifiers;
    }
    
    public void fit(HashMap<Byte, ArrayList<gpsiEntity>> X) throws Exception{
        
        HashMap<Byte, ArrayList<gpsiEntity>> samples;
        ArrayList<gpsiEntity> other;
        byte c;
        
        this.nClasses = X.keySet().size();
        
        for(byte i = 0; i < classifiers.length; i++){
            
            c = (byte) (i + 1);
            samples = new HashMap<>();
            other = new ArrayList<>();
            
            samples.put(c, X.get(c));
            
            for(byte j = 0; j < classifiers.length; j++){
                if(j == i)
                    continue;
                other.addAll(X.get((byte) (j + 1)));
            }
            
            samples.put((byte) 0, other);
                
            classifiers[i].fit(samples);
        }
        
    }
    
    public HashMap<Byte, byte[]> predict(HashMap<Byte, ArrayList<gpsiEntity>> X) throws Exception{
        
        int i, j;
        double votes[];
        prediction = new HashMap<>();
        
        HashMap<Byte, byte[]> currentPrediction;
        HashMap<Byte, double[][]> softVote = new HashMap<>();
        HashMap<Byte, ArrayList<HashMap<Byte, Double>>> currrentConfidence;

        for(byte label : X.keySet()){
            softVote.put(label, new double[X.get(label).size()][this.nClasses]);
            prediction.put(label, new byte[X.get(label).size()]);
        }
        
        for(i = 0; i < classifiers.length; i++){
            currentPrediction = classifiers[i].predict(X);
            currrentConfidence = classifiers[i].getConfidence();
            for(byte label : currentPrediction.keySet())
                for(int k = 0; k < currentPrediction.get(label).length; k++)
                    for(byte l : currrentConfidence.get(label).get(k).keySet())
                        if(l != 0)
                            softVote.get(label)[k][l - 1] += currrentConfidence.get(label).get(k).get(l);
        }

        byte maxVoteIndex;
        
        for(byte label : softVote.keySet()){
            for(i = 0; i < softVote.get(label).length; i++){
                votes = softVote.get(label)[i];
                maxVoteIndex = 0;
                for(j = 1; j < votes.length; j++){
                    if(votes[j] >= votes[maxVoteIndex])
                        maxVoteIndex = (byte) j;
                }
                prediction.get(label)[i] = (byte) (maxVoteIndex + 1);
            }
        }
        
        return prediction;
        
    }
    
    public int[][] getConfusionMatrix(){
        
        int confusionMatrix[][] = new int [nClasses][nClasses];
        
        for(byte label : prediction.keySet())
            for(byte predictedLabel : prediction.get(label))
                confusionMatrix[label - 1][predictedLabel - 1]++;
        
        return confusionMatrix;
                
    }
    
}
