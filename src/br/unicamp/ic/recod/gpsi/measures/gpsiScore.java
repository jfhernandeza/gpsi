/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

/**
 *
 * @author ra163128
 * @param <T>
 */
public abstract class gpsiScore<T> {
    
    public double optimum = Double.POSITIVE_INFINITY;
    
    public abstract double score(T input);
    
}
