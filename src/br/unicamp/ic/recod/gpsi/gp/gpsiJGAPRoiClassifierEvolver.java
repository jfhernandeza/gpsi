/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import org.jgap.gp.IGPProgram;

/**
 *
 * @author juan
 */
public class gpsiJGAPRoiClassifierEvolver extends gpsiRoiClassifierEvolver<IGPProgram> {
    
    public gpsiJGAPRoiClassifierEvolver(String[] args, gpsiDatasetReader datasetReader) throws Exception {
        super(args, datasetReader);
    }

    @Override
    public void evolve() {
        // TODO: Implement
    }

    @Override
    public void printResults() {
        // TODO: Implement
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
