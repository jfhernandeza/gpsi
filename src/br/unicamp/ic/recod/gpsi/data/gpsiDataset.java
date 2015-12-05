/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

import br.unicamp.ic.recod.gpsi.ml.gpsiFeatureVector;
import java.util.ArrayList;

/**
 *
 * @author jfhernandeza
 */
public abstract class gpsiDataset<E, L> {
    
    protected ArrayList<E> entities;
    protected ArrayList<L> labels;
    
}
