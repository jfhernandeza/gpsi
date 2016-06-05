/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.combine.gpsiJGAPVoxelCombiner;
import br.unicamp.ic.recod.gpsi.combine.gpsiVoxelBandCombiner;
import br.unicamp.ic.recod.gpsi.data.gpsiBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedDivision;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPFitnessFunction;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPVoxelFitnessFunction;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import java.util.concurrent.LinkedBlockingQueue;
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
public class gpsiJGAPEvolver extends gpsiEvolver{
    
    
    private final int maxInitDepth;
    
    private final GPConfiguration config;
    private final gpsiJGAPVoxelFitnessFunction fitness;
    
    private final double[] curve;
    private String program;
    private final LinkedBlockingQueue<double[][][]> distributions;

    
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

        gpsiSampler sampler = (bootstrap > 0.0) ? new gpsiBootstrapper(bootstrap) : new gpsiWholeSampler();
        
        fitness = new gpsiJGAPVoxelFitnessFunction((gpsiVoxelRawDataset) rawDataset, this.classLabels, new gpsiClusterSilhouetteScore(), sampler);
        config.setFitnessFunction(fitness);
        
        curve = new double[super.numGenerations];
        distributions = new LinkedBlockingQueue<>();
        
    }

    
    
    @Override
    public void run() throws InvalidConfigurationException, InterruptedException {
        
        gpsiDescriptor descriptor;
        gpsiMLDataset mlDataset;
        gpsiVoxelRawDataset dataset = (gpsiVoxelRawDataset) rawDataset;

        GPGenotype gp = create(config, dataset.getnBands(), fitness);

        // 0: train, 1: train_val, 2: val
        double[][] fitnessCurves = new double[super.numGenerations][3];
        
        IGPProgram current, best = null;
        double bestScore = -Double.MAX_VALUE, currentScore;

        int i, j, k;
        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();
        double validationScore, trainScore, bestValidationScore = -1.0, bestTrainScore = -1.0;
        double[][][] samples;
        gpsiVoxelBandCombiner voxelBandCombinator;
        
        for (int generation = 0; generation < super.numGenerations; generation++) {
            
            gp.evolve(1);
            gp.getGPPopulation().sortByFitness();
            
            if(this.dumpGens){
                double[][][] dists = new double[this.classLabels.length][][];
                descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(fitness.getB(), gp.getGPPopulation().getGPPrograms()[0]));
                mlDataset = new gpsiMLDataset(descriptor);
                mlDataset.loadWholeDataset(rawDataset, true);
                this.distributions.put(this.fitness.getSampler().sample(dataset.getTrainingEntities(), this.classLabels));
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
                    best = current;
                    bestScore = currentScore;
                    bestTrainScore = trainScore;
                    bestValidationScore = validationScore;
                }

            }

            fitnessCurves[generation] = new double[] {gp.getAllTimeBest().getFitnessValue() - 1.0, bestTrainScore, bestValidationScore};
            System.out.printf("Gen %d:\t%.5f\t%.5f\t%.5f\n", generation + 1, fitnessCurves[generation][0], fitnessCurves[generation][1] ,fitnessCurves[generation][2]);

        }
        
        //curves.add(fitnessCurves);
        //programs.put(gp.getAllTimeBest().toStringNorm(0));
        //programs.put(best.toStringNorm(0));
        
        System.out.println("Best solution for trainning: " + gp.getAllTimeBest().toStringNorm(0));
        System.out.println("Best solution for trainning and validation: " + best.toStringNorm(0));
        
    }

    @Override
    public void report() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
            //new Divide(conf, CommandGene.DoubleClass),
            //new Sine(conf, CommandGene.DoubleClass),
            //new Cosine(conf, CommandGene.DoubleClass),
            //new Exp(conf, CommandGene.DoubleClass),
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
    
}
