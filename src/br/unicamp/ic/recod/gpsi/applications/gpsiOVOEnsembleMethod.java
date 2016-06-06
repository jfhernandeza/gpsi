/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.img.gpsiEntity;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiOVOEnsembleMethod {
    
    private final gpsiClassifier classifiers[][];
    private HashMap<Byte, byte[]> prediction;
    int nClasses;

    public gpsiOVOEnsembleMethod(gpsiClassifier[][] classifiers) {
        this.classifiers = classifiers;
    }
    
    public void fit(HashMap<Byte, ArrayList<gpsiEntity>> X) throws Exception{
        
        HashMap<Byte, ArrayList<gpsiEntity>> samples;
        byte c1, c2;
        
        this.nClasses = X.keySet().size();
        
        for(byte i = 0; i < classifiers.length; i++){
            for(byte j = 0; j < classifiers[i].length; j++){
                c1 = (byte) (i + 1);
                c2 = (byte) (j + c1 + 1);
                samples = new HashMap<>();
                samples.put(c1, X.get(c1));
                samples.put(c2, X.get(c2));
                classifiers[i][j].fit(samples);
            }
        }
        
    }
    
    public HashMap<Byte, byte[]> predict(HashMap<Byte, ArrayList<gpsiEntity>> X) throws Exception{
        
        int i, j;
        int votes[];
        prediction = new HashMap<>();
        
        HashMap<Byte, byte[]> currentPrediction;
        HashMap<Byte, int[][]> vote = new HashMap<>();

        for(byte label : X.keySet()){
            vote.put(label, new int[X.get(label).size()][this.nClasses]);
            prediction.put(label, new byte[X.get(label).size()]);
        }
        
        for(i = 0; i < classifiers.length; i++)
            for(j = 0; j < classifiers[i].length; j++){
                currentPrediction = classifiers[i][j].predict(X);
                for(byte label : currentPrediction.keySet())
                    for(int k = 0; k < currentPrediction.get(label).length; k++)
                        vote.get(label)[k][currentPrediction.get(label)[k] - 1]++;
            }

        byte maxVoteIndex;
        
        for(byte label : vote.keySet()){
            for(i = 0; i < vote.get(label).length; i++){
                votes = vote.get(label)[i];
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
