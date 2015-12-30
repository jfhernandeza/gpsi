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
public class gpsiCombinedImage {
    
    private double[][] img;
    private int width, height;

    public gpsiCombinedImage(double[][] img) {
        this.img = img;
        this.height = img.length;
        this.width = img[0].length;
    }

    public double[][] getImg() {
        return img;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    
    
}
