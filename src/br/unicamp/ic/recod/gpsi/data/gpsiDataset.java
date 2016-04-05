/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author jfhernandeza
 * @param <E>
 * @param <L>
 */
public abstract class gpsiDataset<E, L> {
    
    protected HashMap<L, ArrayList<E>> trainingEntities = null;
    protected HashMap<L, ArrayList<E>> validationEntities = null;
    protected HashMap<L, ArrayList<E>> testEntities = null;

    public HashMap<L, ArrayList<E>> getTrainingEntities() {
        return trainingEntities;
    }

    public void setTrainingEntities(HashMap<L, ArrayList<E>> trainingEntities) {
        this.trainingEntities = trainingEntities;
    }

    public HashMap<L, ArrayList<E>> getValidationEntities() {
        return validationEntities;
    }

    public void setValidationEntities(HashMap<L, ArrayList<E>> testEtities) {
        this.validationEntities = testEtities;
    }

    public HashMap<L, ArrayList<E>> getTestEntities() {
        return testEntities;
    }

    public void setTestEntities(HashMap<L, ArrayList<E>> testEntities) {
        this.testEntities = testEntities;
    }
    
    public int getNumberOfTrainingEntities(){
        int m = 0;
        for(L key : this.trainingEntities.keySet())
            m += this.trainingEntities.get(key).size();
        return m;
    }
    
    public int getNumberOfValidationEntities(){
        int m = 0;
        for(L key : this.validationEntities.keySet())
            m += this.validationEntities.get(key).size();
        return m;
    }
    
    public int getNumberOfTestEntities(){
        int m = 0;
        for(L key : this.testEntities.keySet())
            m += this.testEntities.get(key).size();
        return m;
    }
    
    public void separateValidationSet(float validationRatio, long seed){
        
        if(this.validationEntities != null)
            return;
        
        Random randGen = new Random(seed);
        this.validationEntities = new HashMap<>();
        int i, n, randomIndex;
        
        for(L label : this.trainingEntities.keySet()){
            n = (int) (this.trainingEntities.get(label).size() * validationRatio);
            this.validationEntities.put(label, new ArrayList<>());
            for(i = 0; i < n; i++){
                randomIndex = randGen.nextInt(this.trainingEntities.get(label).size());
                this.validationEntities.get(label).add(this.trainingEntities.get(label).remove(randomIndex));
            }
        }
        
    }
    
}
