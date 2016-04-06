/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiVoxelDatasetReader extends gpsiDatasetReader<gpsiFileReader, gpsiVoxelRawDataset, gpsiVoxel> {

    public gpsiVoxelDatasetReader(gpsiFileReader fileReader) {
        super(fileReader);
    }

    @Override
    public gpsiVoxelRawDataset readDataset(String HyperspectralImagePath, String trainingMasksPath, String testMasksPath, String[] classLabels) throws Exception {
        
        gpsiVoxelRawDataset rawDataset = new gpsiVoxelRawDataset();
        
        double[][][] hyperspectralImage = this.fileReader.read3dStructure(HyperspectralImagePath);
        rawDataset.setnBands(hyperspectralImage[0][0].length);
        
        File dir = new File(trainingMasksPath);
        String[] classes = dir.list((File current, String name) -> new File(current, name).isFile());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");
        
        HashMap<String, ArrayList<gpsiVoxel>> trainingEntities = new HashMap<>();
        HashMap<String, ArrayList<gpsiVoxel>> testEntities = new HashMap<>();
        
        for(int i = 0; i < classLabels.length; i++){
            
            double[][] mask = super.fileReader.read2dStructure(trainingMasksPath + classLabels[i] + ".mat");
            
            trainingEntities.put(classLabels[i], new ArrayList<>());
            
            for(int x = 0; x < mask[0].length; x++)
                for(int y = 0; y < mask.length; y++)
                    if(mask[y][x] == 1.0)
                        trainingEntities.get(classLabels[i]).add(new gpsiVoxel(hyperspectralImage[y][x]));
            
        }
        
        rawDataset.setTrainingEntities(trainingEntities);
        
        
        dir = new File(testMasksPath);
        classes = dir.list((File current, String name) -> new File(current, name).isFile());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");
        
        for(int i = 0; i < classLabels.length; i++){
            
            double[][] mask = super.fileReader.read2dStructure(testMasksPath + classLabels[i] + ".mat");
            
            testEntities.put(classLabels[i], new ArrayList<>());
            
            for(int x = 0; x < mask[0].length; x++)
                for(int y = 0; y < mask.length; y++)
                    if(mask[y][x] == 1.0)
                        testEntities.get(classLabels[i]).add(new gpsiVoxel(hyperspectralImage[y][x]));
            
        }
        
        rawDataset.setTestEntities(testEntities);
        
        return rawDataset;
        
    }
    
}
