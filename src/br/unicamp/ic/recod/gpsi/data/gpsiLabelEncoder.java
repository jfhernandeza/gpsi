/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.Collection;
import java.util.TreeMap;

/**
 *
 * @author juan
 */
public class gpsiLabelEncoder {
    
    private final TreeMap<Byte, String> decoder;
    private final TreeMap<String, Byte> encoder;
    private Byte lastKey;

    public gpsiLabelEncoder() {
        this.decoder = new TreeMap<>();
        this.encoder = new TreeMap<>();
        this.lastKey = 0;
    }
    
    public void loadLabels(Collection<String> rawLabels){
        rawLabels.stream().forEach((label) -> { addLabel(label); });
    }
    
    public byte addLabel(String label){
        
        if(encoder.containsKey(label))
            return encoder.get(label);
        
        encoder.put(label, this.lastKey);
        decoder.put(this.lastKey, label);
        
        this.lastKey++;
        
        return (byte) (this.lastKey - 1);
        
    }
    
    public int getCode(String label){
        return encoder.get(label);
    }
    
    public String getLabel(byte code){
        return decoder.get(code);
    }

}
