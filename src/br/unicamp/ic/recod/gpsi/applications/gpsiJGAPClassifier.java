/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.data.gpsiConstantBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiProbabilisticBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPVoxelFitnessFunction;
import br.unicamp.ic.recod.gpsi.io.element.gpsiConfigurationIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;

/**
 *
 * @author juan
 */
public class gpsiJGAPClassifier extends gpsiEvolver{
    
    private final int maxInitDepth;
    
    private final GPConfiguration config;
    private final gpsiJGAPVoxelFitnessFunction fitness;
    
    private IGPProgram[] best;
    
    public gpsiJGAPClassifier(
            String dataSetPath,
            gpsiDatasetReader datasetReader,
            Byte[] classLabels,
            String outputPath,
            int popSize,
            int numGenerations,
            double crossRate,
            double mutRate,
            int validation,
            double bootstrap,
            boolean dumpGens,
            int maxInitDepth) throws Exception {
        
        super(dataSetPath, datasetReader, classLabels, outputPath, popSize, numGenerations, crossRate, mutRate, validation, bootstrap, dumpGens);
        this.maxInitDepth = maxInitDepth;
        
        config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());

        config.setMaxInitDepth(this.maxInitDepth);
        config.setPopulationSize(popSize);

        gpsiSampler sampler = (bootstrap <= 0.0) ? new gpsiWholeSampler() : (bootstrap < 1.0) ? new gpsiProbabilisticBootstrapper(bootstrap) : new gpsiConstantBootstrapper((int) bootstrap);
        
        fitness = new gpsiJGAPVoxelFitnessFunction((gpsiVoxelRawDataset) rawDataset, this.classLabels, new gpsiClusterSilhouetteScore(), sampler);
        config.setFitnessFunction(fitness);
        
        stream.register(new gpsiConfigurationIOElement(null, "results.report"));
        
    }

    @Override
    public void run() throws Exception {
        
        for(byte i = 0; i < 5; i++){
            System.out.println("\nRun " + (i + 1) + "\n");
            rawDataset.assignFolds(new byte[]{i, (byte) ((i + 1) % 5), (byte) ((i + 2) % 5)}, new byte[]{ (byte) ((i + 3) % 5) }, new byte[]{(byte) ((i + 4) % 5) });
            
            
            
        }
        
    }
    
}
