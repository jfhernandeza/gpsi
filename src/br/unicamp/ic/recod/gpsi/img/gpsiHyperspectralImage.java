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
public class gpsiHyperspectralImage {
    
    public double[][][] img;
    private int n_bands, width, height;

    public gpsiHyperspectralImage(double[][][] img) {
        this.img = img;
        this.height = img.length;
        this.width = img[0].length;
        this.n_bands = img[0][0].length;
    }

    public int getN_bands() {
        return n_bands;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
}
