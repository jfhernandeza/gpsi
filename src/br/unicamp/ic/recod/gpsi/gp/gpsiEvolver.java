/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 *
 * @author juan
 * @param <I>
 */
public abstract class gpsiEvolver<I> {
    
    protected gpsiDatasetReader datasetReader;
    protected gpsiRawDataset dataset;
    protected I best = null;
    
    @Option(name = "-imPath", usage = "Path for the hyperspectral image")
    protected String imgPath;
    
    @Option(name = "-trMasksPath", usage = "Path for the trainning masks")
    protected String trainingMasksPath;
    
    @Option(name = "-tsMasksPath", usage = "Path for the test masks")
    protected String testMasksPath;
    
    @Option(name = "-popSize", usage = "Population size")
    protected int popSize = 10;
    
    @Option(name = "-numGens", usage = "Number of generations")
    protected int numGenerations = 50;
    
    @Option(name = "-maxInitDepth", usage = "Max initial depth of trees")
    protected int maxInitDepth = 6;
    
    @Option(name = "-val", usage = "Number of individuals used for validation")
    protected int validation = 0;
    
    @Argument
    protected String[] classLabels;

    public gpsiEvolver(String[] args, gpsiDatasetReader datasetReader) throws CmdLineException, Exception {
        this.datasetReader = datasetReader;
        
        CmdLineParser parser = new CmdLineParser(this);
        parser.parseArgument(args);
        
        this.dataset = this.datasetReader.readDataset(this.imgPath, this.trainingMasksPath, this.testMasksPath, this.classLabels);
        
        System.out.println("Loaded dataset hyperspectral image with " + this.dataset.getnBands() + " bands.");
        System.out.println("Loaded " + this.dataset.getNumberOfTrainingEntities() + " examples for training.");
        System.out.println("Loaded " + this.dataset.getNumberOfTestEntities() + " examples for testing.");
        
    }
    
    public abstract void evolve() throws Exception;
    
}
