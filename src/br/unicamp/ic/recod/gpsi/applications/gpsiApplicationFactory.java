/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.measures.gpsiHClustScore;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.io.gpsiMatlabFileReader;
import br.unicamp.ic.recod.gpsi.io.gpsiRoiDatasetReader;
import br.unicamp.ic.recod.gpsi.io.gpsiVoxelDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiDistanceOfMediansScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiDualScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiHellingerDistanceScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiMeanAndStandardDeviationDistanceScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiNormalBhattacharyyaDistanceScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiSampleSeparationScore;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 *
 * @author juan
 */
public class gpsiApplicationFactory {

    @Option(name = "-type", usage = "Path to the scene")
    public String type;

    @Option(name = "-dataset", usage = "Path to the scene")
    public String datasetPath;

    @Option(name = "-popSize", usage = "Population size")
    public int popSize = 10;

    @Option(name = "-numGens", usage = "Number of generations")
    public int numGenerations = 50;

    @Option(name = "-numGensSel", usage = "Number of generations for selection phase")
    public int numGenerationsSel = 50;

    @Option(name = "-maxInitDepth", usage = "Max initial depth of trees")
    public int maxInitDepth = 6;

    @Option(name = "-crossRate", usage = "Crossover probability")
    public double crossRate = 0.9;

    @Option(name = "-mutRate", usage = "Mutation probability")
    public double mutRate = 0.1;

    @Option(name = "-val", usage = "Number of individuals used for validation")
    public int validation = 0;

    @Option(name = "-bootstrap", usage = "Number of boostrapped samples during evolution")
    public double bootstrap = 0;

    @Option(name = "-dumpGens", usage = "Whether to save the distribution of samples through generations")
    public boolean dumpGens;

    @Option(name = "-out", usage = "Path where the results must be saved")
    public String outputPath;

    @Option(name = "-dataType", usage = "Type of dataset: (v) Voxel or (r) Roi")
    public String dataType = "v";

    @Option(name = "-programs", usage = "Path to stored programs")
    public String programsPath;

    @Option(name = "-score", usage = "Distance measure to be considered in the fitness function")
    public String scoreName = "None";

    @Option(name = "-errorScore", usage = "Threshold of error score to consider a pixel. This value must be in the last spectral band.")
    public double errorScore = 0.0;

    @Option(name = "-testDataset", usage = "Path to the test dataset")
    public String testDatasetPath;

    @Option(name = "-seed", usage = "Seed of the algorithm")
    public long seed;
    
    @Argument
    public Byte[] classLabels;

    public gpsiApplication create() throws Exception {

        gpsiDatasetReader reader;

        switch (dataType) {
            case "v":
                reader = new gpsiVoxelDatasetReader(new gpsiMatlabFileReader());
                break;
            case "r":
                reader = new gpsiRoiDatasetReader(new gpsiMatlabFileReader());
                break;
            default:
                reader = new gpsiVoxelDatasetReader(new gpsiMatlabFileReader());
        }

        gpsiSampleSeparationScore score = null;

        switch (scoreName) {
            case "Silhouette":
                score = new gpsiClusterSilhouetteScore();
                break;
            case "Hellinger":
                score = new gpsiHellingerDistanceScore();
                break;
            case "Medians":
                score = new gpsiDistanceOfMediansScore();
                break;
            case "Bhattacharyya":
                score = new gpsiNormalBhattacharyyaDistanceScore();
                break;
            case "Dual":
                score = new gpsiDualScore();
                break;
            case "NormMeanDistance":
                score = new gpsiMeanAndStandardDeviationDistanceScore();
                break;
            case "SINGLE":
            case "COMPLETE":
            case "AVERAGE":
            case "MEAN":
            case "CENTROID":
            case "WARD":
            case "ADJCOMPLETE":
            case "NEIGHBOR_JOINING":
                score = new gpsiHClustScore(scoreName);
                break;
        }

        switch (type) {
            case "JGAPEvolver":
                return new gpsiJGAPEvolver(datasetPath, reader, classLabels, outputPath, popSize, numGenerations, crossRate, mutRate, validation, bootstrap, dumpGens, maxInitDepth, score, errorScore, seed);
            case "JGAPSelectorEvolver":
                return new gpsiJGAPSelectorEvolver(datasetPath, reader, classLabels, outputPath, popSize, numGenerations, crossRate, mutRate, validation, bootstrap, dumpGens, maxInitDepth, score, errorScore, numGenerationsSel, seed);
            case "OVOFromFiles":
                return new gpsiOVOClassifierFromFiles(datasetPath, reader, classLabels, outputPath, programsPath, errorScore, seed);
            case "JGAPClassifier":
                return new gpsiJGAPClassifier(datasetPath, reader, classLabels, outputPath, popSize, numGenerations, crossRate, mutRate, validation, bootstrap, dumpGens, maxInitDepth, errorScore, seed);
            case "BaselineComparator":
                return new gpsiBaselineIndexComparator(datasetPath, reader, classLabels, outputPath, programsPath, errorScore, seed);
            case "TimeSeriesCreator":
                return new gpsiTimeSeriesCreator(datasetPath, reader, classLabels, outputPath, programsPath, errorScore, seed);
            case "DistCMGenerator":
                return new gpsiDistCMGenerator(datasetPath, reader, classLabels, outputPath, programsPath, errorScore, testDatasetPath, seed);
        }
        return null;
    }

    private gpsiApplicationFactory() {
    }

    public static gpsiApplicationFactory getInstance() {
        return gpsiApplicationFactoryHolder.INSTANCE;
    }

    private static class gpsiApplicationFactoryHolder {

        private static final gpsiApplicationFactory INSTANCE = new gpsiApplicationFactory();
    }
}
