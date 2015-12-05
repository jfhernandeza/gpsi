/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import java.io.IOException;

/**
 *
 * @author jfhernandeza
 */
public abstract class gpsiFileReader {
    
    public abstract double[][] read2dStructure(String path) throws IOException;
    public abstract double[][][] read3dStructure(String path) throws IOException;
    
}
