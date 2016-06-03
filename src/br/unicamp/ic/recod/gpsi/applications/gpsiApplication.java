/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;

/**
 *
 * @author juan
 */
public abstract class gpsiApplication {
    
    protected final gpsiRawDataset rawDataset;
    protected final String datsetPath, outputPath;
    protected final Byte[] classLabels;

    public gpsiApplication(String datasetPath, gpsiDatasetReader datasetReader, Byte[] classLabels, String outputPath) throws Exception {
        
        this.datsetPath = datasetPath;
        this.outputPath = outputPath;
        
        rawDataset = datasetReader.readDataset(datasetPath, classLabels);
                
        if(classLabels == null)
            this.classLabels = rawDataset.getClassLabels();
        else
            this.classLabels = classLabels;
        
        System.out.println("Loaded dataset hyperspectral image with " + rawDataset.getnBands() + " bands.");
        System.out.println("Loaded " + rawDataset.getNumberOfEntities() + " examples.");
        
    }

    public gpsiRawDataset getRawDataset() {
        return rawDataset;
    }
    
    public abstract void run() throws Exception;
    public abstract void report();
    
}
