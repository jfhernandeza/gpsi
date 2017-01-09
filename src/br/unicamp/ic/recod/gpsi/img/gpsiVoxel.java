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
public class gpsiVoxel implements gpsiEntity{
    
    private double[] hyperspectralData;
    private final int row, col;

    public gpsiVoxel(double[] hyperspectralData, int row, int col) {
        this.hyperspectralData = hyperspectralData;
        this.row = row;
        this.col = col;
    }
    
    public double[] getHyperspectralData() {
        return hyperspectralData;
    }

    public void setHyperspectralData(double[] hyperspectralData) {
        this.hyperspectralData = hyperspectralData;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
    
}
