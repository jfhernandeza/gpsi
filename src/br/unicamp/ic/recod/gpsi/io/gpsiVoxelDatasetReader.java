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

/**
 *
 * @author juan
 */
public class gpsiVoxelDatasetReader extends gpsiDatasetReader<gpsiFileReader, gpsiVoxelRawDataset, gpsiVoxel> {

    public gpsiVoxelDatasetReader(gpsiFileReader fileReader) {
        super(fileReader);
    }

    @Override
    public gpsiVoxelRawDataset readDataset(String HyperspectralImagePath, String masksPath) throws Exception {
        
        gpsiVoxelRawDataset rawDataset = new gpsiVoxelRawDataset();
        
        super.loadHiperspectralImage(rawDataset, HyperspectralImagePath);
        
        File dir = new File(masksPath);
        String[] classes = dir.list((File current, String name) -> new File(current, name).isFile());
        
        if(classes.length <= 0)
            throw new Exception("Directory has not the suggested structure.");

        ArrayList<gpsiVoxel> voxels = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        
        String currentClass, files[];
        for(int i = 0; i < classes.length; i++){
            currentClass = classes[i].replace(".mat", "");
            
            double[][] mask = super.fileReader.read2dStructure(masksPath + classes[i]);
            
            for(int x = 0; x < mask[0].length; x++){
                for(int y = 0; y < mask.length; y++){
                    if(mask[y][x] == 1.0){
                        voxels.add(new gpsiVoxel(new int[] {y,x}));
                        labels.add(currentClass);
                    }
                }
            }
            
        }
        
        rawDataset.setEntities(voxels);
        rawDataset.setLabels(labels);
        
        return rawDataset;
        
    }
    
}
