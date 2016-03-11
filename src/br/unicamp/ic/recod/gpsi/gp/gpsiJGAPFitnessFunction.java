/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author juan
 */
public abstract class gpsiJGAPFitnessFunction <D extends gpsiRawDataset> extends GPFitnessFunction {
    
    protected final D dataset;
    protected Variable b[];

    public gpsiJGAPFitnessFunction(D dataset) {
        this.dataset = dataset;
    }

    public Variable[] getB() {
        return b;
    }
    
    public void setB(Variable[] b) {
        this.b = b;
    }
    
}
