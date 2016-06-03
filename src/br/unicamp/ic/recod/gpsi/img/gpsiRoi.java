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
public class gpsiRoi implements gpsiEntity{
    
    private final gpsiVoxel[][] roi;
    public int min_x, min_y, max_x, max_y;

    public gpsiRoi(double[][] mask, double[][][] hyperspectralImage){
        
        this.roi = new gpsiVoxel[mask.length][mask[0].length];
        
        for(int x = 0; x < this.roi[0].length; x++)
            for(int y = 0; y < this.roi.length; y++)
                    this.roi[y][x] = mask[y][x] != 0.0 ? new gpsiVoxel(hyperspectralImage[y][x]): null;
        
        calculateBoundingBox();
    }
    
    private void calculateBoundingBox(){
        
        this.min_x = Integer.MAX_VALUE;
        this.min_y = Integer.MAX_VALUE;
        this.max_x = Integer.MIN_VALUE;
        this.max_y = Integer.MIN_VALUE;
        
        for(int y = 0; y < this.roi.length; y++)
            for(int x = 0; x < this.roi[0].length; x++)
                if(this.roi[y][x] != null){
                    this.min_x = Math.min(this.min_x, x);
                    this.min_y = Math.min(this.min_y, y);
                    this.max_x = Math.max(this.max_x, x);
                    this.max_y = Math.max(this.max_y, y);
                }
        
    }
    
    public gpsiVoxel[][] getRoi() {
        return this.roi;
    }
    
}
