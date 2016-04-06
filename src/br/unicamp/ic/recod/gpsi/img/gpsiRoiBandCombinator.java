/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.img;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiRoiBandCombinator extends gpsiBandCombinator<gpsiRoi>{

    public gpsiRoiBandCombinator(gpsiVoxelCombinator voxelCombinator) {
        super(voxelCombinator);
    }

    @Override
    public void combineEntity(HashMap<String, ArrayList<gpsiRoi>> entities) {
        int i, j;
        gpsiVoxel[][] mask;
        for(String label : entities.keySet())
            for(gpsiRoi roi : entities.get(label)){
                mask = roi.getRoi();
                for(i = 0; i< mask.length; i++)
                    for(j = 0; j < mask[0].length; j++)
                        this.voxelCombinator.combineVoxel(mask[i][j]);
            }
    }
    
}
