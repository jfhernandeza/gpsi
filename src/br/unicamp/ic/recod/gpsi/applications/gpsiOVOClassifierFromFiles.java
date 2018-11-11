    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.ml.gpsiOVOEnsembleMethod;
import br.unicamp.ic.recod.gpsi.ml.gpsiClassifier;
import br.unicamp.ic.recod.gpsi.combine.gpsiStringParserVoxelCombiner;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import br.unicamp.ic.recod.gpsi.io.element.gpsiIntegerCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.ml.gpsiNearestCentroidClassificationAlgorithm;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import org.apache.commons.math3.stat.descriptive.moment.Mean;


/**
 *
 * @author juan
 */
public class gpsiOVOClassifierFromFiles extends gpsiApplication {

    private final gpsiOVOEnsembleMethod ensemble[];

    public gpsiOVOClassifierFromFiles(String datasetPath,
            gpsiDatasetReader datasetReader, Byte[] classLabels,
            String outputPath, String programsPath, double errorScore, long seed) throws Exception {
        super(datasetPath, datasetReader, classLabels, outputPath, errorScore, seed);

        int nClasses, i, j;
        gpsiClassifier[][] classifiers;

        File[] foldFolders = new File(programsPath).listFiles();
        ensemble = new gpsiOVOEnsembleMethod[foldFolders.length];
        BufferedReader reader;

        for (File dir : foldFolders) {

            File[] files = dir.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".program"));

            nClasses = (int) Math.ceil(Math.sqrt(2 * files.length));
            classifiers = new gpsiClassifier[nClasses - 1][];

            String[] labels;

            for (i = 0; i < classifiers.length; i++) {
                classifiers[i] = new gpsiClassifier[classifiers.length - i];
            }

            for (File program : files) {
                reader = new BufferedReader(new FileReader(program));
                labels = program.getName().split("[-.]");
                i = Integer.parseInt(labels[0]) - 1;
                j = Integer.parseInt(labels[1]) - i - 2;
                classifiers[i][j] = new gpsiClassifier(new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine())), new gpsiNearestCentroidClassificationAlgorithm(new Mean()));
                //classifiers[i][j] = new gpsiClassifier(new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine())), new gpsiWEKARandomForestClassificationAlgorithm());
                reader.close();
            }

            ensemble[Integer.parseInt(dir.getName()) - 1] = new gpsiOVOEnsembleMethod(classifiers);
        }

    }

    @Override
    public void run() throws Exception {
        
        int n = ensemble.length, f_;
        byte[] train, test;
        
        gpsiVoxel voxel;
        
        int img[][] = new int[this.rawDataset.getImgRows()][this.rawDataset.getImgCols()];
        boolean raster = this.rawDataset.getImgRows() > 0 && this.rawDataset.getImgCols() > 0;
        
        for (int f = 0; f < n; f++) {
            
            System.out.println("Processing fold " + (f + 1));
            
            train = new byte[n - 1];
            test = new byte[] {(byte) f};
            for(byte j = 1; j < n; j++)
                train[j - 1] = (byte) ((f + j) % n);
            this.rawDataset.assignFolds(train, null, test);

            f_ = (f + 3) % n;
            
            ensemble[f_].fit(this.rawDataset.getTrainingEntities());
            ensemble[f_].predict(this.rawDataset.getTestEntities());
            int[][] cm = ensemble[f_].getConfusionMatrix();
            
            for(byte label : ensemble[f_].getPrediction().keySet()){
                for(int i = 0; i < ensemble[f_].getPrediction().get(label).length; i++){
                    voxel = (gpsiVoxel) ((ArrayList) this.rawDataset.getTestEntities().get(label)).get(i);
                    if(raster)
                        img[voxel.getRow()][voxel.getCol()] = ensemble[f_].getPrediction().get(label)[i];
                }
            }
            
            stream.register(new gpsiIntegerCsvIOElement(cm, null, "confusion_matrices/f" + (1 + (f + 1) % n) + ".csv"));
            ensemble[f_] = null;
        }
        
        if(raster)
            stream.register(new gpsiIntegerCsvIOElement(img, null, "img.csv"));
        
    }

}
