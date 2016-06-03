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
    public HashMap<Byte, double[][]> combineEntities(HashMap<Byte, ArrayList<gpsiVoxel>> entities) {
        
        HashMap<Byte, double[][]> combinedEntities = new HashMap<>();
        double[][] vectors;
        
        for(Byte label : entities.keySet()){
            vectors = new double[entities.get(label).size()][1];
            for(int i = 0; i < entities.get(label).size(); i++){
                vectors[i][1] = this.voxelCombinator.combineVoxel(entities.get(label).get(i));
            }
            combinedEntities.put(label, vectors);
        }
        
        return combinedEntities;
        
    }

    public void setVoxelCombinator(gpsiVoxelCombinator voxelCombinator) {
        this.voxelCombinator = voxelCombinator;
    }
    
}
