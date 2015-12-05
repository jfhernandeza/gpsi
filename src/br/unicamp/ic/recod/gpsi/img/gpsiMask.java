/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.img;

import java.awt.geom.Point2D;

/**
 *
 * @author jfhernandeza
 */
public class gpsiMask {
    
    private boolean[][] mask;
    private Point2D min, max;
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
        
        double min_x, min_y, max_x, max_y;
        
        min_x = Double.POSITIVE_INFINITY;
        min_y = Double.POSITIVE_INFINITY;
        max_x = Double.NEGATIVE_INFINITY;
        max_y = Double.NEGATIVE_INFINITY;
        
        for(int y = 0; y < this.height; y++)
            for(int x = 0; x < this.width; x++)
                if(this.mask[y][x]){
                    min_x = Math.min(min_x, x);
                    min_y = Math.min(min_y, y);
                    max_x = Math.max(max_x, x);
                    max_y = Math.max(max_y, y);
                }
        
        this.min.setLocation(min_x, min_y);
        this.max.setLocation(max_x, max_y);
        
    }
    
    public boolean[][] getMask() {
        return mask;
    }

    public Point2D getMin() {
        return min;
    }

    public void setMin(Point2D min) {
        this.min = min;
    }

    public Point2D getMax() {
        return max;
    }

    public void setMax(Point2D max) {
        this.max = max;
    }
    
}
