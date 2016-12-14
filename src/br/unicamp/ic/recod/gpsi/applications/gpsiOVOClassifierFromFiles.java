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
import br.unicamp.ic.recod.gpsi.ml.gpsiGaussianNaiveBayesClassificationAlgorithm;
import br.unicamp.ic.recod.gpsi.ml.gpsiNearestCentroidClassificationAlgorithm;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
                //classifiers[i][j] = new gpsiClassifier(new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine())), new gpsiGaussianNaiveBayesClassificationAlgorithm());
                reader.close();
            }

            ensemble[Integer.parseInt(dir.getName()) - 1] = new gpsiOVOEnsembleMethod(classifiers);
        }

    }

    @Override
    public void run() throws Exception {
        
        int n = ensemble.length;
        byte[] train, test;
        for (int f = 0; f < n; f++) {
            train = new byte[n - 1];
            test = new byte[] {(byte) f};
            for(byte j = 1; j < n; j++)
                train[j - 1] = (byte) ((f + j) % n);
            this.rawDataset.assignFolds(train, null, test);

            System.out.println("Fitting...");
            ensemble[f].fit(this.rawDataset.getTrainingEntities());
            System.out.println("Predicting...");
            ensemble[f].predict(this.rawDataset.getTestEntities());
            int[][] cm = ensemble[f].getConfusionMatrix();

            for (int i = 0; i < cm.length; i++) {
                for (int j = 0; j < cm[i].length; j++) {
                    System.out.print(cm[i][j] + " ");
                }
                System.out.println("");
            }
        }
    }

}
