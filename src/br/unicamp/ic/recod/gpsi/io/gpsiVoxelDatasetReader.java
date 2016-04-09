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
    public gpsiVoxelRawDataset readDataset(String path, String[] classLabels) throws Exception {
        
        gpsiVoxelRawDataset rawDataset = new gpsiVoxelRawDataset();
        
        double[][][] hyperspectralImage = this.fileReader.read3dStructure(path + "img.mat");
        rawDataset.setnBands(hyperspectralImage[0][0].length);
        
        File dir = new File(path);
        String[] foldsFolders = dir.list((File current, String name) -> new File(current, name).isDirectory());
        
        ArrayList<HashMap<String, ArrayList<gpsiVoxel>>> folds = new ArrayList<>();
        
        double[][] mask;
        String files[];
        HashMap<String, ArrayList<gpsiVoxel>> fold;
        for(String foldFolder : foldsFolders){
            fold = new HashMap<>();
            for(String label : classLabels){
                mask = super.fileReader.read2dStructure(path + foldFolder + "/" + label + ".mat");
                fold.put(label, new ArrayList<>());
                for(int x = 0; x < mask[0].length; x++)
                    for(int y = 0; y < mask.length; y++)
                        if(mask[y][x] == 1.0)
                            fold.get(label).add(new gpsiVoxel(hyperspectralImage[y][x]));
            }
            folds.add(fold);
        }
        
        rawDataset.setFolds(folds);
        
        return rawDataset;
        
    }
    
}
