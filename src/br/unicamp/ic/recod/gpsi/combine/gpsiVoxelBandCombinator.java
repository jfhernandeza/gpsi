/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.combine;

import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiVoxelBandCombinator extends gpsiBandCombinator<gpsiVoxel>{
    
    public gpsiVoxelBandCombinator(gpsiVoxelCombinator voxelCombinator) {
        super(voxelCombinator);
    }

    @Override
    public void combineEntity(HashMap<Byte, ArrayList<gpsiVoxel>> entities) {
        
        for(Byte label : entities.keySet())
            for(gpsiVoxel v : entities.get(label))
                this.voxelCombinator.combineVoxel(v);
        
    }

    public void setVoxelCombinator(gpsiVoxelCombinator voxelCombinator) {
        this.voxelCombinator = voxelCombinator;
    }
    
}
