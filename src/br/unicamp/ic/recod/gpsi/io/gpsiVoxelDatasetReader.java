/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    public gpsiVoxelRawDataset readDataset(String path, Byte[] classLabels, double errorScore) throws Exception {
        
        if((new File(path + "img.mat")).exists())
            return readOneSceneDataset(path, classLabels, errorScore);
        
        return readMultipleScenesDataset(path, classLabels, errorScore);
        
    }
    
    private double[] applyError(double[] voxel, double errorScore){
        
        if(errorScore <= 0.0)
            return voxel;
        
        if(voxel[voxel.length - 1] >= errorScore)
            return null;
        
        return Arrays.copyOf(voxel, voxel.length - 1);
        
    }
    
    private gpsiVoxelRawDataset readOneSceneDataset(String path, Byte[] classLabels, double errorScore) throws IOException{
        
        int ova = -1;
        
        double[][][] hyperspectralImage = this.fileReader.read3dStructure(path + "img.mat");
        gpsiVoxelRawDataset rawDataset = new gpsiVoxelRawDataset(hyperspectralImage.length, hyperspectralImage[0].length);
        rawDataset.setnBands(hyperspectralImage[0][0].length - (errorScore > 0.0 ? 1 : 0));
        
        File dir = new File(path);
        String[] foldsFoldersS = dir.list((File current, String name) -> new File(current, name).isDirectory());
        int[] foldsFolders = new int[foldsFoldersS.length];
        
        for(int i = 0; i < foldsFolders.length; i++)
            foldsFolders[i] = Integer.parseInt(foldsFoldersS[i]);
        
        Arrays.sort(foldsFolders);
        
        ArrayList<HashMap<Byte, ArrayList<gpsiVoxel>>> folds = new ArrayList<>();
        
        if(classLabels == null || classLabels.length == 1){
            ova = classLabels != null ? classLabels[0] : -1;
            File dir_ = new File(path + foldsFolders[0]);
            File[] files = dir_.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".mat"));
            classLabels = new Byte[files.length];
            for(int i = 0; i < files.length; i++)
                classLabels[i] = Byte.parseByte(files[i].getName().replace(".mat", ""));
        }
        
        double[] nVoxel;
        double[][] mask;
        HashMap<Byte, ArrayList<gpsiVoxel>> fold;
        for(int foldFolder : foldsFolders){
            fold = new HashMap<>();
            for(Byte label : classLabels){
                mask = super.fileReader.read2dStructure(path + foldFolder + "/" + label + ".mat");
                if(ova > 0 && label != ova)
                    label = (byte) (ova + 1);
                if(!fold.containsKey(label))
                    fold.put(label, new ArrayList<>());
                for(int x = 0; x < mask[0].length; x++)
                    for(int y = 0; y < mask.length; y++)
                        if(mask[y][x] == 1.0){
                            nVoxel = applyError(hyperspectralImage[y][x], errorScore);
                            if(nVoxel != null)
                                fold.get(label).add(new gpsiVoxel(nVoxel, y, x));
                        }
            }
            folds.add(fold);
        }
        
        rawDataset.setFolds(folds);
        
        return rawDataset;
    }
    
    private gpsiVoxelRawDataset readMultipleScenesDataset(String path, Byte[] classLabels, double errorScore) throws IOException{
        
        gpsiVoxelRawDataset rawDataset = new gpsiVoxelRawDataset(0,0);
        
        double[][][] hyperspectralImage;
        int ova = -1;
        Byte label0;
        
        File dir = new File(path);
        String[] foldsFolders = dir.list((File current, String name) -> new File(current, name).isDirectory());
        String[] scenesFiles;
        
        if(classLabels == null || classLabels.length == 1){
            ova = classLabels != null ? classLabels[0] : -1;
            File dir_ = new File(path + foldsFolders[0]);
            File[] files = dir_.listFiles((File dir1, String name) -> new File(dir1, name).isDirectory());
            classLabels = new Byte[files.length];
            for(int i = 0; i < files.length; i++){
                classLabels[i] = Byte.parseByte(files[i].getName());
            }
        }
        
        ArrayList<HashMap<Byte, ArrayList<gpsiVoxel>>> folds = new ArrayList<>();
        double[] nVoxel;
        
        HashMap<Byte, ArrayList<gpsiVoxel>> fold;
        for(String foldFolder : foldsFolders){
            fold = new HashMap<>();
            for(Byte label : classLabels){
                dir = new File(path + foldFolder + "/" + label);
                scenesFiles = dir.list((File dir1, String name) -> name.toLowerCase().endsWith(".mat"));
                
                label0 = (ova > 0 && label != ova) ? (byte) (ova + 1) : label;
                if(!fold.containsKey(label0))
                    fold.put(label0, new ArrayList<>());
                for(String scene : scenesFiles){
                    hyperspectralImage = super.fileReader.read3dStructure(path + foldFolder + "/" + label + "/" + scene);
                    for(int x = 0; x < hyperspectralImage[0].length; x++)
                        for(int y = 0; y < hyperspectralImage.length; y++){
                            nVoxel = applyError(hyperspectralImage[y][x], errorScore);
                            if(nVoxel != null)
                                fold.get(label0).add(new gpsiVoxel(nVoxel, y, x));
                        }
                }
                
            }
            folds.add(fold);
        }
        
        rawDataset.setFolds(folds);
        rawDataset.setnBands(folds.get(0).get((byte) folds.get(0).keySet().toArray()[0]).get(0).getHyperspectralData().length);
        
        return rawDataset;
    }
    
}
