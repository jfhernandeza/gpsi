/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import java.io.FileNotFoundException;
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
    
    @Option(name = "-path", usage = "Path to the scene")
    protected String path;
    
    @Option(name = "-popSize", usage = "Population size")
    protected int popSize = 10;
    
    @Option(name = "-numGens", usage = "Number of generations")
    protected int numGenerations = 50;
    
    @Option(name = "-maxInitDepth", usage = "Max initial depth of trees")
    protected int maxInitDepth = 6;
    
    @Option(name = "-val", usage = "Number of individuals used for validation")
    protected int validation = 0;
    
    @Option(name = "-bootstrap", usage = "Number of boostrapped samples during evolution")
    protected double bootstrap = 0;
    
    @Argument
    protected String[] classLabels;

    public gpsiEvolver(String[] args, gpsiDatasetReader datasetReader) throws CmdLineException, Exception {
        this.datasetReader = datasetReader;
        
        CmdLineParser parser = new CmdLineParser(this);
        parser.parseArgument(args);
        
        this.dataset = this.datasetReader.readDataset(this.path, this.classLabels);
        
        System.out.println("Loaded dataset hyperspectral image with " + this.dataset.getnBands() + " bands.");
        System.out.println("Loaded " + this.dataset.getNumberOfEntities() + " examples.");
        
    }

    public gpsiRawDataset getDataset() {
        return dataset;
    }
    
    public abstract void evolve() throws Exception;
    
    public abstract void printResults() throws FileNotFoundException;
    
}
