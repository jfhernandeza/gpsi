/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io.element;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author juan
 */
public class gpsiStringIOElement extends gpsiIOElement<String> {

    public gpsiStringIOElement(String element, String path) {
        super(element, path);
    }

    @Override
    public String read() throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write() throws FileNotFoundException {
        PrintWriter outR = new PrintWriter(path);
        outR.println(element);
        outR.close();
    }
    
    
    
}
