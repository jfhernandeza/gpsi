/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.features;

import br.unicamp.ic.recod.gpsi.combine.gpsiVoxelCombiner;
import br.unicamp.ic.recod.gpsi.img.gpsiEntity;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;

/**
 *
 * @author juan
 */
public class gpsiScalarSpectralIndexDescriptor implements gpsiDescriptor{

    private gpsiVoxelCombiner combinator;

    public gpsiScalarSpectralIndexDescriptor(gpsiVoxelCombiner combinator) {
        this.combinator = combinator;
    }
    
    @Override
    public double[] getFeatureVector(gpsiEntity entity) throws Exception {
        
        gpsiVoxel voxel = (gpsiVoxel) entity;
        double[] vector = new double[1];
        
        vector[0] = combinator.combineVoxel(voxel);
        
        return vector;
        
    }

    public void setCombinator(gpsiVoxelCombiner combinator) {
        this.combinator = combinator;
    }
    
}
