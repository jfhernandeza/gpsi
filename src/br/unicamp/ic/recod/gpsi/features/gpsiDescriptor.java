/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.features;

import br.unicamp.ic.recod.gpsi.img.gpsiRoi;

/**
 *
 * @author juan
 */
public interface gpsiDescriptor {
    
    public abstract double[] getFeatureVector(gpsiRoi mask);
    
}
