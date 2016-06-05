/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.combine;

import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;

/**
 *
 * @author juan
 * @param <V>
 * @param <I>
 */
public abstract class gpsiVoxelCombiner<V, I> {
    
    protected final V b;
    protected final I expression;

    public gpsiVoxelCombiner(V b, I expression) {
        this.b = b;
        this.expression = expression;
    }
    
    public abstract double combineVoxel(gpsiVoxel voxel);
    
}
