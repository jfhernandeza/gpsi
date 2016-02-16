/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.img;

/**
 *
 * @author juan
 */
public class gpsiVoxel {
    
    private final int dimensionality;
    private int[] vector;

    public gpsiVoxel(int dimensionality) {
        this.dimensionality = dimensionality;
        this.vector = new int[this.dimensionality];
    }
    
    public gpsiVoxel(int[] vector){
        this.dimensionality = vector.length;
        this.vector = vector;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] point) {
        this.vector = point;
    }
    
}
