/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io.element;

import br.unicamp.ic.recod.gpsi.applications.gpsiApplicationFactory;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 *
 * @author juan
 */
public class gpsiConfigurationIOElement extends gpsiIOElement<Object> {

    public gpsiConfigurationIOElement(Object element, String path) {
        super(null, path);
    }

    @Override
    public Object read() throws FileNotFoundException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void write() throws FileNotFoundException {
        
        gpsiApplicationFactory element = gpsiApplicationFactory.getInstance();
        
        PrintWriter outR = new PrintWriter(path);
        StringBuilder field;
        
        if(element.datasetPath != null)
            outR.println("Dataset=" + element.datasetPath);
        if(element.type != null)
            outR.println("type=" + element.type);
        if(element.dataType != null)
            outR.println("dataType=" + element.dataType);
        
        outR.println("maxInitDepth=" + element.maxInitDepth);
        outR.println("numGenerations=" + element.numGenerations);
        outR.println("popSize=" + element.popSize);
        outR.println("crossRate=" + element.crossRate);
        outR.println("mutRate=" + element.mutRate);
        outR.println("score=" + element.scoreName);
        outR.println("validation=" + element.validation);
        outR.println("bootstrap=" + element.bootstrap);
        outR.println("errorScore=" + element.errorScore);
        
        if(element.programsPath != null)
            outR.println("programsPath=" + element.programsPath);
        
        if(element.classLabels != null){
            field = new StringBuilder();
            for(int i = 0; i < element.classLabels.length; i++){
                field.append(element.classLabels[i]);
                field.append(" ");
            }
            outR.println("classes=[" + field + "]");
        }
        
        outR.close();
        
    }
    
}
