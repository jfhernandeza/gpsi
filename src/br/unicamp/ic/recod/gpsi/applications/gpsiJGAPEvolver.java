/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.combine.gpsiJGAPVoxelCombiner;
import br.unicamp.ic.recod.gpsi.data.gpsiConstantBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiProbabilisticBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedDivision;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedNaturalLogarithm;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedSquareRoot;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPFitnessFunction;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPVoxelFitnessFunction;
import br.unicamp.ic.recod.gpsi.io.element.gpsiConfigurationIOElement;
import br.unicamp.ic.recod.gpsi.io.element.gpsiCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.element.gpsiStringIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author juan
 */
public class gpsiJGAPEvolver extends gpsiEvolver {

    private final int maxInitDepth;

    private final GPConfiguration config;
    private final gpsiJGAPVoxelFitnessFunction fitness;

    private IGPProgram[] best;

    public gpsiJGAPEvolver(
            String dataSetPath,
            gpsiDatasetReader datasetReader,
            Byte[] classLabels,
            String outputPath,
            int popSize,
            int numGenerations,
            int validation,
            double bootstrap,
            boolean dumpGens,
            int maxInitDepth) throws InvalidConfigurationException, Exception {

        super(dataSetPath, datasetReader, classLabels, outputPath, popSize, numGenerations, validation, bootstrap, dumpGens);
        this.maxInitDepth = maxInitDepth;

        config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());

        config.setMaxInitDepth(this.maxInitDepth);
        config.setPopulationSize(popSize);

        gpsiSampler sampler = (bootstrap <= 0.0) ? new gpsiWholeSampler() : (bootstrap < 1.0) ? new gpsiProbabilisticBootstrapper(bootstrap) : new gpsiConstantBootstrapper((int) bootstrap);

        fitness = new gpsiJGAPVoxelFitnessFunction((gpsiVoxelRawDataset) rawDataset, this.classLabels, new gpsiClusterSilhouetteScore(), sampler);
        config.setFitnessFunction(fitness);

        stream.register(new gpsiConfigurationIOElement(null, "report.out"));

    }

    @Override
    public void run() throws InvalidConfigurationException, InterruptedException, Exception {

        int i, j, k;
        byte nFolds = 5;
        gpsiDescriptor descriptor;
        gpsiMLDataset mlDataset;
        gpsiVoxelRawDataset dataset;
        GPGenotype gp;
        double[][] fitnessCurves;
        String[] curveLabels = new String[]{"train", "train_val", "val"};
        double bestScore, currentScore;
        IGPProgram current, bestVal;

        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();

        double validationScore, trainScore, bestValidationScore, bestTrainScore;
        double[][][] samples;

        for (byte f = 0; f < nFolds; f++) {
            
            System.out.println("\nRun " + (f + 1) + "\n");

            rawDataset.assignFolds(new byte[]{f, (byte) ((f + 1) % nFolds), (byte) ((f + 2) % nFolds)}, new byte[]{(byte) ((f + 3) % nFolds)}, new byte[]{(byte) ((f + 4) % nFolds)});
            dataset = (gpsiVoxelRawDataset) rawDataset;
            gp = create(config, dataset.getnBands(), fitness);

            // 0: train, 1: train_val, 2: val
            fitnessCurves = new double[super.numGenerations][];
            current = null;
            bestVal = null;
            bestScore = -Double.MAX_VALUE;
            bestValidationScore = -1.0;
            bestTrainScore = -1.0;

            for (int generation = 0; generation < super.numGenerations; generation++) {

                gp.evolve(1);
                gp.getGPPopulation().sortByFitness();

                if (this.dumpGens) {
                    
                    double[][][] dists;
                    descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(fitness.getB(), gp.getGPPopulation().getGPPrograms()[0]));
                    mlDataset = new gpsiMLDataset(descriptor);
                    mlDataset.loadWholeDataset(rawDataset, true);

                    dists = (new gpsiWholeSampler()).sample(mlDataset.getTrainingEntities(), this.classLabels);;
                    for (i = 0; i < this.classLabels.length; i++) {
                        stream.register(new gpsiCsvIOElement(dists[i], null, "gens/" + classLabels[i] + "/" + (generation + 1) + ".csv"));
                    }

                }

                for (i = 0; i < super.validation; i++) {

                    current = gp.getGPPopulation().getGPPrograms()[i];

                    descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(fitness.getB(), current));
                    mlDataset = new gpsiMLDataset(descriptor);
                    mlDataset.loadWholeDataset(rawDataset, true);

                    samples = this.fitness.getSampler().sample(mlDataset.getValidationEntities(), classLabels);

                    validationScore = fitness.getScore().score(samples);
                    trainScore = current.getFitnessValue() - 1.0;

                    currentScore = mean.evaluate(new double[]{trainScore, validationScore}) - sd.evaluate(new double[]{trainScore, validationScore});

                    if (currentScore > bestScore) {
                        bestVal = current;
                        bestScore = currentScore;
                        bestTrainScore = trainScore;
                        bestValidationScore = validationScore;
                    }

                }

                if (validation > 0) {
                    best = new IGPProgram[2];
                    best[0] = gp.getAllTimeBest();
                    best[1] = bestVal;
                    fitnessCurves[generation] = new double[]{best[0].getFitnessValue() - 1.0, bestTrainScore, bestValidationScore};
                    System.out.printf("Gen %d:\t%.5f\t%.5f\t%.5f\n", generation + 1, fitnessCurves[generation][0], fitnessCurves[generation][1], fitnessCurves[generation][2]);
                } else {
                    best = new IGPProgram[1];
                    best[0] = gp.getAllTimeBest();
                    fitnessCurves[generation] = new double[]{gp.getAllTimeBest().getFitnessValue() - 1.0};
                    System.out.printf("Gen %d:\t%.5f\n", generation + 1, fitnessCurves[generation][0]);
                }

            }

            stream.register(new gpsiCsvIOElement(fitnessCurves, curveLabels, "curves/f" + (f + 1) + ".csv"));

            System.out.println("Best solution for trainning: " + gp.getAllTimeBest().toStringNorm(0));
            stream.register(new gpsiStringIOElement(gp.getAllTimeBest().toStringNorm(0), "programs/f" + (f + 1) + "train.program"));

            if (validation > 0) {
                System.out.println("Best solution for trainning and validation: " + bestVal.toStringNorm(0));
                stream.register(new gpsiStringIOElement(bestVal.toStringNorm(0), "programs/f" + (f + 1) + "train_val.program"));
            }

        }

    }

    private GPGenotype create(GPConfiguration conf, int n_bands, gpsiJGAPFitnessFunction fitness) throws InvalidConfigurationException {

        Class[] types = {CommandGene.DoubleClass};
        Class[][] argTypes = {{}};

        CommandGene[] variables = new CommandGene[n_bands];
        Variable[] b = new Variable[n_bands];
        CommandGene[] functions = {
            new Add(conf, CommandGene.DoubleClass),
            new Subtract(conf, CommandGene.DoubleClass),
            new Multiply(conf, CommandGene.DoubleClass),
            new gpsiJGAPProtectedDivision(conf, CommandGene.DoubleClass),
            new gpsiJGAPProtectedNaturalLogarithm(conf, CommandGene.DoubleClass),
            new gpsiJGAPProtectedSquareRoot(conf, CommandGene.DoubleClass),
            new Terminal(conf, CommandGene.DoubleClass, 1.0d, 1000000.0d, false)
        };

        for (int i = 0; i < n_bands; i++) {
            b[i] = Variable.create(conf, "b" + i, CommandGene.DoubleClass);
            variables[i] = b[i];
        }

        CommandGene[][] nodeSets = new CommandGene[1][];
        nodeSets[0] = (CommandGene[]) ArrayUtils.addAll(variables, functions);

        fitness.setB(b);

        return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets, 100, true);
    }

    public IGPProgram[] getBest() {
        return best;
    }

}
