/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.img.gpsiHyperspectralImage;

/**
 *
 * @author juan
 * @param <E>
 * @param <L>
 */
public class gpsiRawDataset<E, L> extends gpsiDataset {
    
    private gpsiHyperspectralImage hyperspectralImage;
    
    public gpsiHyperspectralImage getHyperspectralImage() {
        return hyperspectralImage;
    }

    public void setHyperspectralImage(gpsiHyperspectralImage hyperspectralImage) {
        this.hyperspectralImage = hyperspectralImage;
    }
    
}
