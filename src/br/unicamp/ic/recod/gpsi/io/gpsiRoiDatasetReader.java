/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import br.unicamp.ic.recod.gpsi.data.gpsiRoiRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiRoi;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiRoiDatasetReader extends gpsiDatasetReader<gpsiFileReader, gpsiRoiRawDataset, gpsiRoi> {
    
    public gpsiRoiDatasetReader(gpsiFileReader fileReader) {
        super(fileReader);
    }

    @Override
    public gpsiRoiRawDataset readDataset(String path, String[] classLabels) throws Exception {
        
        gpsiRoiRawDataset rawDataset = new gpsiRoiRawDataset();
        
        double[][][] hyperspectralImage = this.fileReader.read3dStructure(path + "img.mat");
        rawDataset.setnBands(hyperspectralImage[0][0].length);
        
        File dir = new File(path);
        String[] foldsFolders = dir.list((File current, String name) -> new File(current, name).isDirectory());
        
        ArrayList<HashMap<String, ArrayList<gpsiRoi>>> folds = new ArrayList<>();
        
        String files[], classes[];
        HashMap<String, ArrayList<gpsiRoi>> fold;
        for(String foldFolder : foldsFolders){
            dir = new File(path + foldFolder + "/");
            classes = dir.list((File current, String name) -> new File(current, name).isDirectory());
            fold = new HashMap<>();
            for(String label : classes){
                fold.put(label, new ArrayList<>());
                files = (new File(path + foldFolder + "/" + "label" + "/")).list((File current, String name) -> new File(current, name).getName().endsWith(".mat"));
                for (String file : files)
                    fold.get(label).add(new gpsiRoi(super.fileReader.read2dStructure(path + foldFolder + "/" + "label" + "/" + file), hyperspectralImage));
            }
            
            folds.add(fold);
        }
        
        rawDataset.setFolds(folds);
        
        return rawDataset;
        
    }
    
}
