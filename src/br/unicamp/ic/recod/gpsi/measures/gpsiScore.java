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
public interface gpsiScore<T> {
    
    public double score(T input);
    
}