/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.combine.gpsiStringParserVoxelCombiner;
import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.io.element.gpsiDoubleCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.element.gpsiIntegerCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.ml.gpsiNearestCentroidClassificationAlgorithm;
import br.unicamp.ic.recod.gpsi.ml.gpsiClassifier;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

/**
 *
 * @author juan
 */
public class gpsiDistCMGenerator extends gpsiApplication {

    private final gpsiScalarSpectralIndexDescriptor[] descriptors;
    private final String names[], testDatasetPath;

    public gpsiDistCMGenerator(String datasetPath, gpsiDatasetReader datasetReader, Byte[] classLabels, String outputPath, String programsPath, double errorScore, String testDatasetPath) throws Exception {
        super(datasetPath, datasetReader, classLabels, outputPath, errorScore);

        int i, j;
        File dir = new File(programsPath);

        BufferedReader reader;
        File[] files = dir.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".program"));

        descriptors = new gpsiScalarSpectralIndexDescriptor[files.length];
        names = new String[files.length];
        this.testDatasetPath = testDatasetPath;

        for (i = 0; i < names.length; i++) {
            reader = new BufferedReader(new FileReader(files[i]));
            names[i] = files[i].getName().replace(".program", "");
            descriptors[i] = new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine()));
            reader.close();
        }
    }

    @Override
    public void run() throws Exception {

        int i, cm[][];
        gpsiMLDataset dataset;
        gpsiClassifier classifier;
        gpsiRawDataset localRawDataset;
        super.rawDataset.assignFolds(new byte[]{0, 1, 2, 3, 4}, null, null);

        gpsiWholeSampler sampler = new gpsiWholeSampler();
        
        File[] locations, days, years = new File(testDatasetPath).listFiles(File::isDirectory);

        for (i = 0; i < descriptors.length; i++) {
            classifier = new gpsiClassifier(descriptors[i], new gpsiNearestCentroidClassificationAlgorithm(new Mean()));
            classifier.fit(super.rawDataset.getTrainingEntities());

            for (File year : years) {
                days = new File(year.getAbsolutePath()).listFiles(File::isDirectory);
                for (File day : days) {
                    locations = new File(day.getAbsolutePath()).listFiles(File::isDirectory);
                    for (File location : locations) {
                        localRawDataset = datasetReader.readDataset(location.getAbsolutePath() + "/", null, 0.0);
                        localRawDataset.assignFolds(null, null, new byte[]{0});
                        classifier.predict(localRawDataset.getTestEntities());
                        cm = classifier.getConfusionMatrix();
                        stream.register(new gpsiIntegerCsvIOElement(cm, null, names[i] + '/' + year.getName() + '/' + day.getName() + '/' + location.getName() + "/cm.csv"));
                        
                        dataset = new gpsiMLDataset(descriptors[i]);
                        dataset.loadTestSet(localRawDataset.getTestEntities(), true);
                        
                        stream.register(new gpsiDoubleCsvIOElement(sampler.sample(dataset.getTestEntities(), null)[0], null, names[i] + '/' + year.getName() + '/' + day.getName() + '/' + location.getName() + "/sample.csv"));
                        
                    }
                    System.out.println("Done: " + year.getName() + " " + day.getName());
                }
            }

        }
        
    }

}
