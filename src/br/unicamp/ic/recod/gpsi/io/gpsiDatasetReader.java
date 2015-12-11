/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiHyperspectralImage;
import br.unicamp.ic.recod.gpsi.img.gpsiMask;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author jfhernandeza
 */
public class gpsiDatasetReader <R extends gpsiFileReader> {
    
    private R fileReader;

    public gpsiDatasetReader(R fileReader) {
        this.fileReader = fileReader;
    }
    
    public gpsiRawDataset readDataset(String HyperspectralImagePath, String masksPath ) throws Exception{
        
        gpsiRawDataset rawDataset = new gpsiRawDataset();
        
        double[][][] hiperspectralImage = this.fileReader.read3dStructure(HyperspectralImagePath);
        
        File dir = new File(masksPath);
        String[] classes = dir.list((File current, String name) -> new File(current, name).isDirectory());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");
        
        ArrayList<gpsiMask> masks = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        
        String currentClass, files[];
        for(int i = 0; i < classes.length; i++){
            currentClass = classes[i];
            files = (new File(masksPath + '/' + currentClass)).list((File current, String name) -> new File(current, name).getName().endsWith(".mat"));
            for(int j = 0; j < files.length; j++){
                masks.add(new gpsiMask(this.fileReader.read2dStructure(masksPath + '/' + currentClass + '/' + files[j])));
                labels.add(currentClass);
            }
        }
        
        rawDataset.setEntities(masks);
        rawDataset.setHyperspectralImage(new gpsiHyperspectralImage(hiperspectralImage));
        rawDataset.setLabels(labels);
        
        return rawDataset;
        
    }
    
    
}
