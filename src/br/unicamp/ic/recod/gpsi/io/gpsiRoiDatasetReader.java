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
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiRoiDatasetReader extends gpsiDatasetReader<gpsiFileReader, gpsiRoiRawDataset, gpsiMask> {
    
    public gpsiRoiDatasetReader(gpsiFileReader fileReader) {
        super(fileReader);
    }

    @Override
    public gpsiRoiRawDataset readDataset(String HyperspectralImagePath, String trainingMasksPath, String testMasksPath, String[] classLabels) throws Exception {
        
        gpsiRoiRawDataset rawDataset = new gpsiRoiRawDataset();
        
        super.loadHiperspectralImage(rawDataset, HyperspectralImagePath);
        
        File dir = new File(trainingMasksPath);
        String[] classes = dir.list((File current, String name) -> new File(current, name).isDirectory());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");
        
        HashMap<String, ArrayList<gpsiMask>> trainingEntities = new HashMap<>();
        HashMap<String, ArrayList<gpsiMask>> testEntities = new HashMap<>();
        
        String files[];
        for (String currentClass : classLabels) {
            files = (new File(trainingMasksPath + '/' + currentClass)).list((File current, String name) -> new File(current, name).getName().endsWith(".mat"));
            trainingEntities.put(currentClass, new ArrayList<>());
            for (String file : files)
                trainingEntities.get(currentClass).add(new gpsiMask(super.fileReader.read2dStructure(trainingMasksPath + '/' + currentClass + '/' + file)));
        }
        
        rawDataset.setTrainingEntities(trainingEntities);
        
        dir = new File(testMasksPath);
        classes = dir.list((File current, String name) -> new File(current, name).isDirectory());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");
        
        for (String currentClass : classLabels) {
            files = (new File(trainingMasksPath + '/' + currentClass)).list((File current, String name) -> new File(current, name).getName().endsWith(".mat"));
            testEntities.put(currentClass, new ArrayList<>());
            for (String file : files)
                testEntities.get(currentClass).add(new gpsiMask(super.fileReader.read2dStructure(trainingMasksPath + '/' + currentClass + '/' + file)));
        }
        
        rawDataset.setTestEntities(testEntities);
        
        return rawDataset;
        
    }
    
}
