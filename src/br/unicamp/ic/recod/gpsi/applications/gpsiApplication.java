/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.io.element.gpsiIOStream;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author juan
 */
public abstract class gpsiApplication {
    
    protected final gpsiRawDataset rawDataset;
    protected final String datsetPath;
    protected final Byte[] classLabels;
    protected final gpsiIOStream stream;
    protected String outputPath;

    public gpsiApplication(String datasetPath, gpsiDatasetReader datasetReader, Byte[] classLabels, String outputPath, double errorScore) throws Exception {
        
        this.datsetPath = datasetPath;
        this.outputPath = outputPath;
        
        rawDataset = datasetReader.readDataset(datasetPath, classLabels, errorScore);
                
        if(classLabels == null)
            this.classLabels = rawDataset.getClassLabels();
        else
            this.classLabels = classLabels;
        
        this.stream = new gpsiIOStream();
        
        System.out.println("Loaded dataset hyperspectral image with " + rawDataset.getnBands() + " bands.");
        System.out.println("Loaded " + rawDataset.getNumberOfEntities() + " examples.");
        
    }
    
    public void report() throws Exception{
        if(this.outputPath == null)
            this.outputPath = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime());
        this.stream.setRoot("results/" + this.outputPath + "/");
        this.stream.flush();
    }
    
    public abstract void run() throws Exception;
    
}
