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
    
    private int[] vector;
    
    public gpsiVoxel(int[] vector){
        this.vector = vector;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int[] point) {
        this.vector = point;
    }
    
}
