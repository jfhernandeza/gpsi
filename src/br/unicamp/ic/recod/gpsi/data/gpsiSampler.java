/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public interface gpsiSampler {
    
    public double[] sample(HashMap<String, ArrayList<gpsiVoxel>> entities, String label);
    
}
