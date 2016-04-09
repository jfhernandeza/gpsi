/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author jfhernandeza
 * @param <E>
 * @param <L>
 */
public abstract class gpsiDataset<E, L> {
    
    protected ArrayList<HashMap<L, ArrayList<E>>> folds;
    
    protected HashMap<L, ArrayList<E>> trainingEntities = null;
    protected HashMap<L, ArrayList<E>> validationEntities = null;
    protected HashMap<L, ArrayList<E>> testEntities = null;
    
    private int nBands;

    public HashMap<L, ArrayList<E>> getTrainingEntities() {
        return trainingEntities;
    }

    public void setFolds(ArrayList<HashMap<L, ArrayList<E>>> folds) {
        this.folds = folds;
    }

    public HashMap<L, ArrayList<E>> getValidationEntities() {
        return validationEntities;
    }

    public HashMap<L, ArrayList<E>> getTestEntities() {
        return testEntities;
    }
    
    public int getNumberOfEntities(){
        int m = 0;
        for(int i = 0; i < this.folds.size(); i++){
            for(L label : this.folds.get(i).keySet()){
                m += this.folds.get(i).get(label).size();
            }
        }
        return m;
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

    public int getnBands() {
        return nBands;
    }

    public int getnFolds(){
        return folds.size();
    }
    
    public void setnBands(int nBands) {
        this.nBands = nBands;
    }
    
    public void assignFolds(int[] trainingFolds, int[] validationFolds, int[] testFolds){
        
        this.trainingEntities = new HashMap<>();
        this.validationEntities = new HashMap<>();
        this.testEntities = new HashMap<>();
        
        for(L label : this.folds.get(0).keySet()){
            
            this.trainingEntities.put(label, new ArrayList<>());
            for(int index : trainingFolds)
                this.trainingEntities.get(label).addAll(this.folds.get(index).get(label));
            
            this.validationEntities.put(label, new ArrayList<>());
            for(int index : validationFolds)
                this.validationEntities.get(label).addAll(this.folds.get(index).get(label));
            
            this.testEntities.put(label, new ArrayList<>());
            for(int index : testFolds)
                this.testEntities.get(label).addAll(this.folds.get(index).get(label));
            
        }
        
    }
    
}
