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
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.ml.gpsiNearestCentroidClassificationAlgorithm;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author juan
 */
public class gpsiOVOClassifierFromFiles extends gpsiApplication{

    private final gpsiOVOEnsembleMethod ensemble;
    
    public gpsiOVOClassifierFromFiles(String datasetPath, gpsiDatasetReader datasetReader, Byte[] classLabels, String outputPath, String programsPath, double errorScore) throws Exception {
        super(datasetPath, datasetReader, classLabels, outputPath, errorScore);
        
        int nClasses, i, j;
        gpsiClassifier[][] classifiers;
        File dir = new File(programsPath + "1/");
       
        BufferedReader reader;
        File[] files = dir.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".program"));
        
        nClasses = (int) Math.ceil(Math.sqrt(2 * files.length));
        classifiers = new gpsiClassifier[nClasses - 1][];
        
        String[] labels;
        
        for(i = 0; i < classifiers.length; i++)
            classifiers[i] = new gpsiClassifier[classifiers.length - i];
        
        for(File program : files){
            reader = new BufferedReader(new FileReader(program));
            labels = program.getName().split("[_.]");
            i = Integer.parseInt(labels[0]) - 1;
            j = Integer.parseInt(labels[1]) - i - 2;
            classifiers[i][j] = new gpsiClassifier(new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine())), new gpsiNearestCentroidClassificationAlgorithm(new Mean()));
            reader.close();
        }
        
        ensemble = new gpsiOVOEnsembleMethod(classifiers);
        
        
    }

    @Override
    public void run() throws Exception {
        
        this.rawDataset.assignFolds(new byte[] {0,1,2}, null, new byte[] {4});
        
        System.out.println("Fitting...");
        ensemble.fit(this.rawDataset.getTrainingEntities());
        System.out.println("Predicting...");
        ensemble.predict(this.rawDataset.getTestEntities());
        int[][] cm = ensemble.getConfusionMatrix();
        
        for(int i = 0; i < cm.length; i++){
            for(int j = 0; j < cm[i].length; j++)
                System.out.print(cm[i][j] + " ");
            System.out.println("");
        }
        
    }
    
}
