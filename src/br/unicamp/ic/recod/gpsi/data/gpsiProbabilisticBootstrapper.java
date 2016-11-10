/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiProbabilisticBootstrapper extends gpsiBootstrapper{
    
    private final double p;

    public gpsiProbabilisticBootstrapper(double p, long seed) {
        super(seed);
        this.p = p;
    }
    
    @Override
    public double[][][] sample(HashMap<Byte, ArrayList<double[]>> entities, Byte[] labels) {
        int n[] = new int[labels.length];
        for(int i = 0; i < labels.length; i++){
            n[i] = (int)(entities.get(labels[i]).size() * this.p);
        }
        return bootstrap(entities, labels, n);
    }
    
}
