/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import br.unicamp.ic.recod.gpsi.data.gpsiRoiRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiMask;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author juan
 */
public class gpsiRoiDatasetReader extends gpsiDatasetReader<gpsiFileReader, gpsiRoiRawDataset, gpsiMask> {
    
    public gpsiRoiDatasetReader(gpsiFileReader fileReader) {
        super(fileReader);
    }

    @Override
    public gpsiRoiRawDataset readDataset(String HyperspectralImagePath, String trainingMasksPath, String testMasksPath) throws Exception {
        
        gpsiRoiRawDataset rawDataset = new gpsiRoiRawDataset();
        
        super.loadHiperspectralImage(rawDataset, HyperspectralImagePath);
        
        File dir = new File(trainingMasksPath);
        String[] classes = dir.list((File current, String name) -> new File(current, name).isDirectory());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");

        ArrayList<gpsiMask> masks = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        
        String files[];
        for (String currentClass : classes) {
            files = (new File(trainingMasksPath + '/' + currentClass)).list((File current, String name) -> new File(current, name).getName().endsWith(".mat"));
            for (String file : files) {
                masks.add(new gpsiMask(super.fileReader.read2dStructure(trainingMasksPath + '/' + currentClass + '/' + file)));
                labels.add(currentClass);
            }
        }
        
        rawDataset.setTrainingEntities(masks);
        rawDataset.setTrainingLabels(labels);
        
        
        dir = new File(testMasksPath);
        classes = dir.list((File current, String name) -> new File(current, name).isDirectory());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");

        masks = new ArrayList<>();
        labels = new ArrayList<>();
        
        for (String currentClass : classes) {
            files = (new File(trainingMasksPath + '/' + currentClass)).list((File current, String name) -> new File(current, name).getName().endsWith(".mat"));
            for (String file : files) {
                masks.add(new gpsiMask(super.fileReader.read2dStructure(trainingMasksPath + '/' + currentClass + '/' + file)));
                labels.add(currentClass);
            }
        }
        
        rawDataset.setTestEntities(masks);
        rawDataset.setTestLabels(labels);
        
        return rawDataset;
        
    }
    
}
