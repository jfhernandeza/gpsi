/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.combine;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 * @param <D>
 */
public abstract class gpsiBandCombinator<D> {
    
    protected gpsiVoxelCombinator voxelCombinator;

    public gpsiBandCombinator(gpsiVoxelCombinator voxelCombinator) {
        this.voxelCombinator = voxelCombinator;
    }
    
    public abstract HashMap<Byte, double[][]> combineEntities(HashMap<Byte, ArrayList<D>> entities);
    
}
