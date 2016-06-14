/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io.element;

import java.io.FileNotFoundException;

/**
 *
 * @author juan
 */
public abstract class gpsiIOElement<T> {
    
    protected final T element;
    protected String path;

    public gpsiIOElement(T element, String path) {
        this.element = element;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public abstract T read() throws FileNotFoundException;
    public abstract void write() throws FileNotFoundException;
    
}
