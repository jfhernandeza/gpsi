/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.features;

import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiMask;

/**
 *
 * @author juan
 */
public interface gpsiDescriptor {
    
    public abstract gpsiFeatureVector getFeatureVector(gpsiCombinedImage combinedImage, gpsiMask mask);
    
}
