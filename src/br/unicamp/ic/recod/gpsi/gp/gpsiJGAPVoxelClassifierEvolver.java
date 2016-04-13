/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiJGAPVoxelCombinator;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxelBandCombinator;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
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
public class gpsiJGAPVoxelClassifierEvolver extends gpsiVoxelClassifierEvolver<IGPProgram> {

    private final GPConfiguration config;
    private final gpsiJGAPVoxelFitnessFunction fitness;
    
    private final ArrayList<double[][]> curves;
    private final LinkedBlockingQueue<String> programs;
    private final LinkedBlockingQueue<double[]> accuracies;
    private final LinkedBlockingQueue<int[][]> confusionMatrices;
    
    public gpsiJGAPVoxelClassifierEvolver(String[] args, gpsiDatasetReader datasetReader) throws Exception {
        super(args, datasetReader);
        config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());

        config.setMaxInitDepth(super.maxInitDepth);
        config.setPopulationSize(super.popSize);

        gpsiSampler sampler;
        sampler = (this.bootstrap > 0.0) ? new gpsiBootstrapper(this.bootstrap) : new gpsiWholeSampler();
        fitness = new gpsiJGAPVoxelFitnessFunction((gpsiVoxelRawDataset) dataset, super.classLabels, new gpsiClusterSilhouetteScore(), sampler);
        config.setFitnessFunction(fitness);
        
        curves = new ArrayList<>();
        confusionMatrices = new LinkedBlockingQueue<>();
        programs = new LinkedBlockingQueue<>();
        accuracies = new LinkedBlockingQueue<>();

    }

    @Override
    public void evolve() throws Exception {

        gpsiVoxelRawDataset dataset = (gpsiVoxelRawDataset) super.dataset;

        GPGenotype gp = create(config, dataset.getnBands(), fitness);

        // 0: train, 1: train_val, 2: val
        double[][] fitnessCurves = new double[super.numGenerations][3];
        
        IGPProgram current, best = null;
        double bestScore = -Double.MAX_VALUE, currentScore;

        int i, j, k;
        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();
        double validationScore, trainScore, bestValidationScore = -1.0, bestTrainScore = -1.0;
        ArrayList<double[]> samples;
        gpsiVoxelBandCombinator voxelBandCombinator;
        
        for (int generation = 0; generation < super.numGenerations; generation++) {
            
            gp.evolve(1);
            try{
                gp.getGPPopulation().sortByFitness();
            }catch(IllegalArgumentException e){}
            for (i = 0; i < super.validation; i++) {

                current = gp.getGPPopulation().getGPPrograms()[i];

                voxelBandCombinator = new gpsiVoxelBandCombinator(new gpsiJGAPVoxelCombinator(fitness.getB(), current));
                voxelBandCombinator.combineEntity(dataset.getValidationEntities());

                samples = new ArrayList<>();
                for (String classLabel : super.classLabels)
                    samples.add(this.fitness.getSampler().sample(dataset.getValidationEntities(), classLabel));

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

        curves.add(fitnessCurves);
        programs.put(gp.getAllTimeBest().toStringNorm(0));
        programs.put(best.toStringNorm(0));
        
        System.out.println("Best solution for trainning: " + gp.getAllTimeBest().toStringNorm(0));
        System.out.println("Best solution for trainning and validation: " + best.toStringNorm(0));
        
        double[] accuracy = new double[] {0.0, 0.0}, means;
        int[][] confusionMatrix;
        int l = 0;
        for (IGPProgram program : new IGPProgram[]{gp.getAllTimeBest(), best}) {

            voxelBandCombinator = new gpsiVoxelBandCombinator(new gpsiJGAPVoxelCombinator(fitness.getB(), program));
            voxelBandCombinator.combineEntity(dataset.getTrainingEntities());
            voxelBandCombinator.combineEntity(dataset.getTestEntities());

            confusionMatrix = new int[this.classLabels.length][this.classLabels.length];
            means = new double[this.classLabels.length];

            gpsiSampler sampler = new gpsiWholeSampler();
            
            samples = new ArrayList<>();
            for (String classLabel : super.classLabels)
                samples.add(sampler.sample(dataset.getTrainingEntities(), classLabel));

            for (i = 0; i < samples.size(); i++)
                means[i] = mean.evaluate(samples.get(i));

            samples = new ArrayList<>();
            for (String classLabel : super.classLabels)
                samples.add(sampler.sample(dataset.getTestEntities(), classLabel));

            double value;
            accuracy[l] = 0.0;
            int minDistanceIndex = 0, m = 0;
            for (i = 0; i < samples.size(); i++) {
                for (j = 0; j < samples.get(i).length; j++) {
                    value = samples.get(i)[j];
                    m++;
                    for (k = 1; k < samples.size(); k++) {
                        if (Math.abs(value - means[k]) < Math.abs(value - means[minDistanceIndex])) {
                            minDistanceIndex = k;
                        }
                    }
                    confusionMatrix[i][minDistanceIndex]++;
                }
            }

            for(i = 0; i < samples.size(); i++)
                accuracy[l] += confusionMatrix[i][i];
            
            confusionMatrices.put(confusionMatrix);
            accuracy[l] /= m;
            l++;
        }
        
        accuracies.put(accuracy);
        System.out.println("Total accuracy: " + accuracy[0] + "\t" + accuracy[1]);

    }

    private GPGenotype create(GPConfiguration conf, int n_bands, gpsiJGAPFitnessFunction fitness) throws InvalidConfigurationException {

        Class[] types = {CommandGene.FloatClass};
        Class[][] argTypes = {{}};

        CommandGene[] variables = new CommandGene[n_bands];
        Variable[] b = new Variable[n_bands];
        CommandGene[] functions = {
            new Add(conf, CommandGene.FloatClass),
            new Subtract(conf, CommandGene.FloatClass),
            new Multiply(conf, CommandGene.FloatClass),
            new Divide(conf, CommandGene.FloatClass),
            // new Sine(conf, CommandGene.FloatClass),
            // new Cosine(conf, CommandGene.FloatClass),
            //  new Exp(conf, CommandGene.FloatClass),
            new Terminal(conf, CommandGene.FloatClass, 1.0d, 1000000.0d, false)
        };

        for (int i = 0; i < n_bands; i++) {
            b[i] = Variable.create(conf, "b" + i, CommandGene.FloatClass);
            variables[i] = b[i];
        }

        CommandGene[][] nodeSets = new CommandGene[1][];
        nodeSets[0] = (CommandGene[]) ArrayUtils.addAll(variables, functions);

        fitness.setB(b);

        return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets, 100, true);
    }

    @Override
    public void printResults() throws FileNotFoundException {

        String outRoot = "results/" + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime()) + "/";

        (new File(outRoot)).mkdir();
        (new File(outRoot + "programs/")).mkdir();
        (new File(outRoot + "confusion_matrices/")).mkdir();

        PrintWriter outR = new PrintWriter(outRoot + "report.out");

        outR.println("Data set location:\t" + super.path);

        outR.println("\nGP configuration\n");

        outR.println("Population size:\t" + super.popSize);
        outR.println("Number of generations:\t" + super.numGenerations);
        outR.println("Max initial depth of trees:\t" + super.maxInitDepth);

        outR.println("\nML configuration\n");

        outR.println("Number of individuals used for validation\t" + super.validation);
        outR.print("Classes considered:\t");
        for (String label : this.classLabels) {
            outR.print(label + " ");
        }
        outR.print("\n");
        outR.close();

        int i, j;
        double[] arr;
        
        outR = new PrintWriter(outRoot + "results.csv");
        outR.println("acc_train, acc_train_val");
        while(!accuracies.isEmpty()){
            arr = accuracies.poll();
            outR.println(arr[0] + "," + arr[1]);
        }
        outR.close();
        
        outR = new PrintWriter(outRoot + "curves.csv");
        for(i = 0; i < curves.size(); i++)
            outR.print("train" + i + "," + "train_val" + i + "," + "val" + i + ",");
        for(i = 0; i < numGenerations; i++){
            outR.print("\n");
            for(j = 0; j < curves.size(); j++)
                outR.print(curves.get(j)[i][0] + "," + curves.get(j)[i][1] + "," + curves.get(j)[i][2] + ",");
        }
        outR.close();
        
        i = 0;
        while(!programs.isEmpty()){
            outR = new PrintWriter(outRoot + "programs/" + "train_" + i + ".program");
            outR.println(programs.poll());
            outR.close();
            outR = new PrintWriter(outRoot + "programs/" + "train_val_" + i + ".program");
            outR.println(programs.poll());
            outR.close();
            i++;
        }
        
        String[] names = new String[] {"train_", "train_val_"};
        int[][] cm;
        int k = 0;
        while(!confusionMatrices.isEmpty()){
            for(String name : names){
                outR = new PrintWriter(outRoot + "confusion_matrices/" + name + k + ".csv");
                cm = confusionMatrices.poll();
                for(i = 0; i < cm.length; i++){
                    for(j = 0; j < cm[0].length; j++){
                        outR.print(cm[i][j] + ",");
                    }
                    outR.print("\n");
                }
                outR.close();
            }
            k++;
        }

    }

}
