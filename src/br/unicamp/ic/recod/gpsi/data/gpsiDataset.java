/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author jfhernandeza
 * @param <E>
 * @param <L>
 */
public abstract class gpsiDataset<E, L> {
    
    protected ArrayList<E> trainingEntities;
    protected ArrayList<L> trainingLabels;
    protected ArrayList<E> testEntities;
    protected ArrayList<L> testLabels;
    protected HashMap<L,ArrayList<Integer>> indexesPerClass;
    private HashSet<L> listOfClasses;
    
    public int getNumberOfClasses(){
        loadListOfClasses();
        return this.listOfClasses.size();
    }

    public HashSet<L> getListOfClasses() {
        loadListOfClasses();
        return listOfClasses;
    }
    
    public int getNumberOfEntities(){
        return this.trainingEntities.size();
    }

    public HashMap<L, ArrayList<Integer>> getIndexesPerClass() {
        
        L currentClass;
        
        if(this.indexesPerClass == null){
            this.indexesPerClass = new HashMap<>();
            for(int i = 0 ; i < this.trainingEntities.size(); i++){
                currentClass = this.trainingLabels.get(i);
                if(!this.indexesPerClass.keySet().contains(currentClass)){
                    this.indexesPerClass.put(currentClass, new ArrayList<>());
                }
                this.indexesPerClass.get(currentClass).add(i);
            }
        }
        
        return this.indexesPerClass;
    }
    
    private void loadListOfClasses(){
        if(this.listOfClasses == null){
            this.listOfClasses = new HashSet<>(this.trainingLabels);
        }
    }

    public ArrayList<E> getTrainingEntities() {
        return trainingEntities;
    }

    public ArrayList<L> getTrainingLabels() {
        return trainingLabels;
    }

    public void setTrainingEntities(ArrayList<E> entities) {
        this.trainingEntities = entities;
    }

    public void setTrainingLabels(ArrayList<L> labels) {
        this.trainingLabels = labels;
    }

    public ArrayList<E> getTestEntities() {
        return testEntities;
    }

    public void setTestEntities(ArrayList<E> testEntities) {
        this.testEntities = testEntities;
    }

    public ArrayList<L> getTestLabels() {
        return testLabels;
    }

    public void setTestLabels(ArrayList<L> testLabels) {
        this.testLabels = testLabels;
    }
    
}
