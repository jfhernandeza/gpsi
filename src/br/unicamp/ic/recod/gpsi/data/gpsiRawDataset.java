/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.img.gpsiHyperspectralImage;
import br.unicamp.ic.recod.gpsi.img.gpsiMask;
import java.util.ArrayList;

/**
 *
 * @author jfhernandeza
 */
public class gpsiRawDataset extends gpsiDataset<gpsiMask, String> {
    
    private gpsiHyperspectralImage hyperspectralImage;

    public gpsiHyperspectralImage getHyperspectralImage() {
        return hyperspectralImage;
    }

    public void setHyperspectralImage(gpsiHyperspectralImage hyperspectralImage) {
        this.hyperspectralImage = hyperspectralImage;
    }

    public ArrayList<gpsiMask> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<gpsiMask> entities) {
        this.entities = entities;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }
    
    
}
