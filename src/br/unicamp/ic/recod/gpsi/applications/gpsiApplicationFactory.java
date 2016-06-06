/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.io.gpsiMatlabFileReader;
import br.unicamp.ic.recod.gpsi.io.gpsiRoiDatasetReader;
import br.unicamp.ic.recod.gpsi.io.gpsiVoxelDatasetReader;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 *
 * @author juan
 */
public class gpsiApplicationFactory {
    
    @Option(name = "-type", usage = "Path to the scene")
    private String type;
    
    @Option(name = "-dataset", usage = "Path to the scene")
    protected String datasetPath;
    
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
    
    @Option(name = "-dumpGens", usage = "Whether to save the distribution of samples through generations")
    protected boolean dumpGens;
    
    @Option(name = "-out", usage = "Path where the results must be saved")
    protected String outputPath = "";
    
    @Option(name = "-dataType", usage = "Type of dataset: (v) Voxel or (r) Roi")
    protected String dataType = "v";
    
    @Option(name = "-programs", usage = "Path to stored programs")
    protected String programsPath;
    
    @Argument
    protected Byte[] classLabels;
    
    public gpsiApplication create() throws Exception{
        
        gpsiDatasetReader reader;
        
        switch(dataType){
            case "v":
                reader = new gpsiVoxelDatasetReader(new gpsiMatlabFileReader());
                break;
            case "r":
                reader = new gpsiRoiDatasetReader(new gpsiMatlabFileReader());
                break;
            default:
                reader = new gpsiVoxelDatasetReader(new gpsiMatlabFileReader());
        }
        
        switch(type){
            case "JGAPEvolver":
                return new gpsiJGAPEvolver(datasetPath, reader, classLabels, outputPath, popSize, numGenerations, validation, bootstrap, dumpGens, maxInitDepth);
            case "OVOFromFiles":
                return new gpsiOVOClassifierFromFiles(datasetPath, reader, classLabels, outputPath, programsPath);
        }
        return null;
    }
    
}
