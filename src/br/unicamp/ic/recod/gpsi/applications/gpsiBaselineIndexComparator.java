/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.combine.gpsiStringParserVoxelCombiner;
import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.io.element.gpsiDoubleCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author juan
 */
public class gpsiBaselineIndexComparator extends gpsiApplication{

    private final gpsiScalarSpectralIndexDescriptor[] descriptors;
    private final String names[];
    
    public gpsiBaselineIndexComparator(String datasetPath, gpsiDatasetReader datasetReader, Byte[] classLabels, String outputPath, String programsPath, double errorScore, long seed) throws Exception {
        super(datasetPath, datasetReader, classLabels, outputPath, errorScore, seed);
        
        int i, j;
        File dir = new File(programsPath);
       
        BufferedReader reader;
        File[] files = dir.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".program"));
        
        descriptors = new gpsiScalarSpectralIndexDescriptor[files.length];
        names = new String[files.length];
        

        for(i = 0; i < names.length; i++){
            reader = new BufferedReader(new FileReader(files[i]));
            names[i] = files[i].getName().replace(".program", "");
            descriptors[i] = new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine()));
            reader.close();
        }
        
    }

    @Override
    public void run() throws Exception {
        
        this.rawDataset.assignFolds(new byte[] {0}, null, new byte[] {0});
        
        gpsiClusterSilhouetteScore score = new gpsiClusterSilhouetteScore();
        double[][][] sample;
        gpsiMLDataset dataset;
        gpsiWholeSampler sampler = new gpsiWholeSampler();
        double[][] scores = new double[1][names.length];
        
        for(int i = 0; i < descriptors.length; i++){
            dataset = new gpsiMLDataset(descriptors[i]);
            dataset.loadWholeDataset(rawDataset, true);
            sample = sampler.sample(dataset.getTrainingEntities(), classLabels);
            scores[0][i] = score.score(sample);
            System.out.println(names[i] + "\t" + scores[0][i]);
        }
        this.stream.register(new gpsiDoubleCsvIOElement(scores, names, "baselines.csv"));
    }
    
}
