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
    
    private double[] hyperspectralData;
    private double combinedValue;

    public gpsiVoxel(double[] hyperspectralData) {
        this.hyperspectralData = hyperspectralData;
    }
    
    public double[] getHyperspectralData() {
        return hyperspectralData;
    }

    public void setHyperspectralData(double[] hyperspectralData) {
        this.hyperspectralData = hyperspectralData;
    }

    public double getCombinedValue() {
        return combinedValue;
    }

    public void setCombinedValue(double combinedValue) {
        this.combinedValue = combinedValue;
    }
    
}
