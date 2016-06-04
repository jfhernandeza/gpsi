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
public interface gpsiClassificationAlgorithm {
    
    public void fit(HashMap<Byte, ArrayList<double[]>> x);
    public int[][] predictAndEval(HashMap<Byte, ArrayList<double[]>> x);
    
}
