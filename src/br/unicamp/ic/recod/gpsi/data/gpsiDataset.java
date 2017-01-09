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
 */
public abstract class gpsiDataset<E> {
    
    protected ArrayList<HashMap<Byte, ArrayList<E>>> folds;
    
    protected HashMap<Byte, ArrayList<E>> trainingEntities = null;
    protected HashMap<Byte, ArrayList<E>> validationEntities = null;
    protected HashMap<Byte, ArrayList<E>> testEntities = null;
    
    private Byte[] classLabels;
    
    private int nBands;

    public HashMap<Byte, ArrayList<E>> getTrainingEntities() {
        return trainingEntities;
    }

    public void setFolds(ArrayList<HashMap<Byte, ArrayList<E>>> folds) {
        this.folds = folds;
        Object[] labels = folds.get(0).keySet().toArray();
        classLabels = new Byte[folds.get(0).keySet().size()];
        for(int i = 0; i < classLabels.length; i++)
            classLabels[i] = (Byte) labels[i];
            
    }

    public ArrayList<HashMap<Byte, ArrayList<E>>> getFolds() {
        return folds;
    }

    public HashMap<Byte, ArrayList<E>> getValidationEntities() {
        return validationEntities;
    }

    public HashMap<Byte, ArrayList<E>> getTestEntities() {
        return testEntities;
    }
    
    public int getNumberOfEntities(){
        int m = 0;
        for(int i = 0; i < this.folds.size(); i++){
            for(Byte label : this.folds.get(i).keySet()){
                m += this.folds.get(i).get(label).size();
            }
        }
        return m;
    }
    
    public int getNumberOfTrainingEntities(){
        int m = 0;
        for(Byte key : this.trainingEntities.keySet())
            m += this.trainingEntities.get(key).size();
        return m;
    }
    
    public int getNumberOfValidationEntities(){
        int m = 0;
        for(Byte key : this.validationEntities.keySet())
            m += this.validationEntities.get(key).size();
        return m;
    }
    
    public int getNumberOfTestEntities(){
        int m = 0;
        for(Byte key : this.testEntities.keySet())
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
    
    public void assignFolds(byte[] trainingFolds, byte[] validationFolds, byte[] testFolds){
        
        this.trainingEntities = new HashMap<>();
        this.validationEntities = new HashMap<>();
        this.testEntities = new HashMap<>();
        
        for(Byte label : this.folds.get(0).keySet()){
            
            if(trainingFolds != null){
                this.trainingEntities.put(label, new ArrayList<>());
                for(int index : trainingFolds)
                    this.trainingEntities.get(label).addAll(this.folds.get(index).get(label));
            }
            
            if(validationFolds != null){
                this.validationEntities.put(label, new ArrayList<>());
                for(int index : validationFolds)
                    this.validationEntities.get(label).addAll(this.folds.get(index).get(label));
            }
            
            if(testFolds != null){
                this.testEntities.put(label, new ArrayList<>());
                for(int index : testFolds)
                    this.testEntities.get(label).addAll(this.folds.get(index).get(label));
            }
            
        }
        
    }

    public Byte[] getClassLabels() {
        return classLabels;
    }
    
    public void freeTrainingEntities(){
        this.trainingEntities = null;
    }
    
    public void freeValidationEntities(){
        this.validationEntities = null;
    }
    
    public void freeTestEntities(){
        this.testEntities = null;
    }
    
}
