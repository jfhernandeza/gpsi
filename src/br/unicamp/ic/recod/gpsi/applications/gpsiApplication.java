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
    protected final String datasetPath;
    protected final Byte[] classLabels;
    protected final gpsiIOStream stream;
    protected final gpsiDatasetReader datasetReader;
    protected final double errorScore;
    protected final long seed;
    protected String outputPath;

    public gpsiApplication(String datasetPath, gpsiDatasetReader datasetReader, Byte[] classLabels, String outputPath, double errorScore, long seed) throws Exception {
        
        this.datasetPath = datasetPath;
        this.outputPath = outputPath;
        
        rawDataset = datasetReader.readDataset(datasetPath, classLabels, errorScore);

        this.datasetReader = datasetReader;
        this.errorScore = errorScore;
        this.seed = seed;
        
        if(classLabels == null)
            this.classLabels = rawDataset.getClassLabels();
        else if (classLabels.length == 1)
            this.classLabels = new Byte[] {classLabels[0], (byte) (classLabels[0] + 1)};
        else
            this.classLabels = classLabels;
        
        this.stream = new gpsiIOStream();
        
        System.out.println("Loaded dataset hyperspectral image with " + rawDataset.getnBands() + " bands.");
        System.out.println("Loaded " + rawDataset.getNumberOfEntities() + " examples.");
        
    }
    
    public void report() throws Exception{
        if(this.outputPath == null)
            this.outputPath = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime());
        this.stream.setRoot(this.outputPath);
        this.stream.flush();
    }
    
    public abstract void run() throws Exception;
    
}
