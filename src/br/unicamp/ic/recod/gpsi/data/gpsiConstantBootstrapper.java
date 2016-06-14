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
public class gpsiConstantBootstrapper extends gpsiBootstrapper {

    private final int n;

    public gpsiConstantBootstrapper(int n) {
        this.n = n;
    }
    
    @Override
    public double[][][] sample(HashMap<Byte, ArrayList<double[]>> entities, Byte[] labels) {
        return bootstrap(entities, labels, new int[] {n});
    }
    
}
