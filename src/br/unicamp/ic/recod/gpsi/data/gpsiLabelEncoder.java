/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

/**
 *
 * @author juan
 */
public class gpsiLabelEncoder {
    
    private final TreeMap<Integer, String> decoder;
    private final TreeMap<String, Integer> encoder;
    private int lastKey;

    public gpsiLabelEncoder() {
        this.decoder = new TreeMap<>();
        this.encoder = new TreeMap<>();
        this.lastKey = 0;
    }
    
    public void loadLabels(ArrayList<String> rawLabels){
        
        HashSet<String> labels = new HashSet<>(rawLabels);
        
        for(String label : labels){
            addLabel(label);
        }
        
    }
    
    public int addLabel(String label){
        
        if(encoder.containsKey(label))
            return encoder.get(label);
        
        encoder.put(label, this.lastKey);
        decoder.put(this.lastKey, label);
        
        this.lastKey++;
        
        return this.lastKey - 1;
        
    }
    
    public int getCode(String label){
        return encoder.get(label);
    }
    
    public String getLabel(int code){
        return decoder.get(code);
    }

}
