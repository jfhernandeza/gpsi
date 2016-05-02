/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

/**
 *
 * @author juan
 * @param <L>
 */
public abstract class gpsiEvaluator<L> {
    
    private int[][] confusionMatrix;

    public abstract void buildConfusionMatrix(L prediction, L groundtruth);
    public abstract double calculateTotalAccuracy();
    
}
