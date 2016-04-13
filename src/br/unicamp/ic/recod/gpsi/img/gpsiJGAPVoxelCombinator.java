/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.img;

import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author juan
 */
public class gpsiJGAPVoxelCombinator extends gpsiVoxelCombinator<Variable[], IGPProgram> {

    public gpsiJGAPVoxelCombinator(Variable[] b, IGPProgram individual) {
        super(b, individual);
    }

    @Override
    public void combineVoxel(gpsiVoxel voxel) {
        for(int i = 0; i < voxel.getHyperspectralData().length; i++)
            this.b[i].set(voxel.getHyperspectralData()[i]);
        voxel.setCombinedValue(this.individual.execute_double(0, new Object[0]));
    }
    
}