/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;

/**
 *
 * @author jfhernandeza
 * @param <R>
 * @param <D>
 * @param <E>
 */
public abstract class gpsiDatasetReader <R extends gpsiFileReader, D extends gpsiRawDataset, E> {
    
    protected R fileReader;

    public gpsiDatasetReader() {
    }
    
    public gpsiDatasetReader(R fileReader) {
        this.fileReader = fileReader;
    }
    
    public abstract D readDataset(String path, Byte[] classLabels ) throws Exception;

    public void setFileReader(R fileReader) {
        this.fileReader = fileReader;
    }
    
}
