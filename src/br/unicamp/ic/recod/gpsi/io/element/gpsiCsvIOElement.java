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
public class gpsiCsvIOElement extends gpsiIOElement<double[][]>{

    String[] columnNames;
    
    public gpsiCsvIOElement(double[][] element, String[] columnNames, String path) {
        super(element, path);
        this.columnNames = columnNames;
    }
    
    @Override
    public double[][] read() throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write() throws FileNotFoundException {
        int i;
        PrintWriter outR = new PrintWriter(path);
        if(columnNames != null){
            outR.print(columnNames[0]);
            for(i = 1; i < element[0].length; i++)
                outR.print("," + columnNames[i]);
            outR.print("\n");
        }
        for(i = 0; i < element.length; i++){
            outR.print(element[i][0]);
            for(int j = 1; j < element[i].length; j++)
                outR.print("," + element[i][j]);
            outR.print("\n");
        }
        outR.close();
    }
    
}
