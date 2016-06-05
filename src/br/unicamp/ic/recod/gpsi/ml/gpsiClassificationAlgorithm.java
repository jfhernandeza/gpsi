/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public abstract class gpsiClassificationAlgorithm {
    
    protected int dimensionality, nClasses;

    public int getDimensionality() {
        return dimensionality;
    }

    public int getnClasses() {
        return nClasses;
    }
    
    public abstract void fit(HashMap<Byte, ArrayList<double[]>> x);
    public abstract byte predict(double[] x);
    
}
