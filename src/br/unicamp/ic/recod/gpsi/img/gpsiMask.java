/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.img;

/**
 *
 * @author jfhernandeza
 */
public class gpsiMask {
    
    private boolean[][] mask;
    public int min_x, min_y, max_x, max_y;
    private final int height, width;

    public gpsiMask(boolean[][] mask) {
        this.mask = mask;
        this.height = mask.length;
        this.width = mask[0].length;
        calculateBoundingBox();
    }

    public gpsiMask(double[][] mask){
        this.height = mask.length;
        this.width = mask[0].length;
        
        this.mask = new boolean[this.height][this.width];
        
        for(int x = 0; x < this.width; x++)
            for(int y = 0; y < this.height; y++)
                this.mask[y][x] = !(mask[y][x] == 0.0);
        
        calculateBoundingBox();
    }
    
    private void calculateBoundingBox(){
        
        this.min_x = Integer.MAX_VALUE;
        this.min_y = Integer.MAX_VALUE;
        this.max_x = Integer.MIN_VALUE;
        this.max_y = Integer.MIN_VALUE;
        
        for(int y = 0; y < this.height; y++)
            for(int x = 0; x < this.width; x++)
                if(this.mask[y][x]){
                    this.min_x = Math.min(this.min_x, x);
                    this.min_y = Math.min(this.min_y, y);
                    this.max_x = Math.max(this.max_x, x);
                    this.max_y = Math.max(this.max_y, y);
                }
        
    }
    
    public boolean[][] getMask() {
        return mask;
    }
    
}
