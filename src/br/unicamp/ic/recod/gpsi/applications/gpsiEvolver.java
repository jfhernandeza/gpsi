/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;

/**
 *
 * @author juan
 */
public abstract class gpsiEvolver extends gpsiApplication{
    
    protected int popSize;
    protected int numGenerations;
    protected int validation;
    protected double crossRate;
    protected double mutRate;
    protected double bootstrap;
    protected boolean dumpGens;

    public gpsiEvolver(
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
            boolean dumpGens) throws Exception {
        super(dataSetPath, datasetReader, classLabels, outputPath);
        this.popSize = popSize;
        this.numGenerations = numGenerations;
        this.crossRate = crossRate;
        this.mutRate = mutRate;
        this.validation = validation;
        this.bootstrap = bootstrap;
        this.dumpGens = dumpGens;
    }
    
}
