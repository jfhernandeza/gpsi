/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.ml.gpsiClassifier;
import br.unicamp.ic.recod.gpsi.combine.gpsiStringParserVoxelCombiner;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.io.element.gpsiIntegerCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.ml.gpsiNearestCentroidClassificationAlgorithm;
import br.unicamp.ic.recod.gpsi.ml.gpsiOVAEnsembleMethod;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.commons.math3.stat.descriptive.moment.Mean;


/**
 *
 * @author juan
 */
public class gpsiOVAClassifierFromFiles extends gpsiApplication {

    private final gpsiOVAEnsembleMethod ensemble[];

    public gpsiOVAClassifierFromFiles(String datasetPath,
            gpsiDatasetReader datasetReader, Byte[] classLabels,
            String outputPath, String programsPath, double errorScore, long seed) throws Exception {
        super(datasetPath, datasetReader, classLabels, outputPath, errorScore, seed);

        int nClasses;
        gpsiClassifier[] classifiers;

        File[] foldFolders = new File(programsPath).listFiles();
        ensemble = new gpsiOVAEnsembleMethod[foldFolders.length];
        BufferedReader reader;

        for (File dir : foldFolders) {

            File[] files = dir.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".program"));

            nClasses = files.length;
            classifiers = new gpsiClassifier[nClasses];

            String[] labels;

            for (File program : files) {
                reader = new BufferedReader(new FileReader(program));
                labels = program.getName().split("[.]");
                classifiers[Integer.parseInt(labels[0]) - 1] = new gpsiClassifier(new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine())), new gpsiNearestCentroidClassificationAlgorithm(new Mean()));
                //classifiers[i][j] = new gpsiClassifier(new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine())), new gpsiWEKARandomForestClassificationAlgorithm());
                reader.close();
            }

            ensemble[Integer.parseInt(dir.getName()) - 1] = new gpsiOVAEnsembleMethod(classifiers);
        }

    }

    @Override
    public void run() throws Exception {
        
        int n = ensemble.length, f_;
        byte[] train, test;
        for (int f = 0; f < n; f++) {
            
            System.out.println("Processing fold " + (f + 1));
            
            train = new byte[n - 1];
            test = new byte[] {(byte) f};
            for(byte j = 1; j < n; j++)
                train[j - 1] = (byte) ((f + j) % n);
            this.rawDataset.assignFolds(train, null, test);

            f_ = (f + 1) % n;
            
            ensemble[f_].fit(this.rawDataset.getTrainingEntities());
            ensemble[f_].predict(this.rawDataset.getTestEntities());
            int[][] cm = ensemble[f_].getConfusionMatrix();
            
            stream.register(new gpsiIntegerCsvIOElement(cm, null, "confusion_matrices/f" + (1 + (f + 1) % n) + ".csv"));
            ensemble[f_] = null;
        }
    }

}
