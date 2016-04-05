/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;

/**
 *
 * @author juan
 * @param <I>
 */
public abstract class gpsiVoxelClassifierEvolver<I> extends gpsiEvolver<I> {

    public gpsiVoxelClassifierEvolver(String[] args, gpsiDatasetReader datasetReader) throws Exception {
        super(args, datasetReader);
    }  
    
}
