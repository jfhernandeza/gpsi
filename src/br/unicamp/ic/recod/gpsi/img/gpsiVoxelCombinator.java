/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.img;

/**
 *
 * @author juan
 * @param <V>
 * @param <I>
 */
public abstract class gpsiVoxelCombinator<V, I> {
    
    protected final V b;
    protected final I individual;

    public gpsiVoxelCombinator(V b, I individual) {
        this.b = b;
        this.individual = individual;
    }
    
    public abstract void combineVoxel(gpsiVoxel voxel);
    
}
