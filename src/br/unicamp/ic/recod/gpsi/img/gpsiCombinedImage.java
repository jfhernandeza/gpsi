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
    
    public double[][] img;
    private final int width, height;

    public gpsiCombinedImage(double[][] img) {
        this.img = img;
        this.height = img.length;
        this.width = img[0].length;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
}
