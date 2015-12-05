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
public class gpsiROI {
    
    private boolean[][] mask;
    private String label;

    public gpsiROI(boolean[][] image, String label) {
        this.mask = image;
        this.label = label;
    }
    
    public boolean[][] getMask() {
        return mask;
    }

    public void setMask(boolean[][] mask) {
        this.mask = mask;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
}
