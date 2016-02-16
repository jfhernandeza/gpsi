/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author jfhernandeza
 * @param <E>
 * @param <L>
 */
public abstract class gpsiDataset<E, L> {
    
    protected ArrayList<E> entities;
    protected ArrayList<L> labels;
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
        return this.entities.size();
    }
    
    private void loadListOfClasses(){
        if(this.listOfClasses == null){
            this.listOfClasses = new HashSet<>(this.labels);
        }
    }

    public ArrayList<E> getEntities() {
        return entities;
    }

    public ArrayList<L> getLabels() {
        return labels;
    }

    public void setEntities(ArrayList<E> entities) {
        this.entities = entities;
    }

    public void setLabels(ArrayList<L> labels) {
        this.labels = labels;
    }
    
}
