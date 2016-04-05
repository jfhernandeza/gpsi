/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiJGAPImageCombinator;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public gpsiJGAPVoxelClassifierEvolver(String[] args, gpsiDatasetReader datasetReader) throws Exception {
        super(args, datasetReader);
    }

    @Override
    public void evolve() throws Exception{
        
        gpsiVoxelRawDataset dataset = (gpsiVoxelRawDataset) super.dataset;
        dataset.separateValidationSet(0.5f, 41);
        
        GPConfiguration config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
        
        config.setMaxInitDepth(super.maxInitDepth);
        config.setPopulationSize(super.popSize);
        
        gpsiJGAPVoxelFitnessFunction fitness = new gpsiJGAPVoxelFitnessFunction(dataset, super.classLabels, new gpsiClusterSilhouetteScore());
        config.setFitnessFunction(fitness);
        
        GPGenotype gp = create(config, dataset.getHyperspectralImage().getN_bands(), fitness);
        
        double[] fitnessCurveTrain = new double[super.numGenerations];
        double[] fitnessCurveVal = new double[super.numGenerations];
        
        IGPProgram current;
        double bestScore = -Double.MAX_VALUE, currentScore;
        
        int i, j, k;
        
        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();
        gpsiCombinedImage combinedImage;
        double validationScore, trainScore, bestTestScore = -1.0, bestTrainScore = -1.0;
        ArrayList<double[]> samples;
        for(int generation = 0; generation < super.numGenerations; generation++){
            gp.evolve(1);
            
            if(super.validation > 0)
                gp.getGPPopulation().sortByFitness();
            else
                this.best = gp.getAllTimeBest();
            
            for(i = 0; i < super.validation; i++){

                current = gp.getGPPopulation().getGPPrograms()[i];
                
                combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(dataset.getHyperspectralImage(), fitness.getB(), current);
                samples = new ArrayList<>();
                
                for(String classLabel : super.classLabels)
                    samples.add(gpsiSampler.getInstance().sample(dataset.getValidationEntities(), classLabel, combinedImage));
                
                validationScore = fitness.getScore().score(samples);
                trainScore = current.getFitnessValue() - 1.0;
                
                currentScore = mean.evaluate(new double[] {trainScore, validationScore}) - sd.evaluate(new double[] {trainScore, validationScore});
                
                if(currentScore > bestScore){
                    best = current;
                    bestScore = currentScore;
                    bestTrainScore = trainScore;
                    bestTestScore = validationScore;
                }
                
            }
            
            if(this.validation > 0){
                fitnessCurveTrain[generation] = bestTrainScore;
                fitnessCurveVal[generation] = bestTestScore;
                System.out.println(bestTrainScore + "\t" + bestTestScore);
            }else{
                fitnessCurveTrain[generation] = best.getFitnessValue();
            }
            
        }
        
        combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(dataset.getHyperspectralImage(), fitness.getB(), best);
        
        int[][] confusionMatrix = new int[this.classLabels.length][this.classLabels.length];
        
        double[] means = new double[this.classLabels.length];
        
        samples = new ArrayList<>();
        for(String classLabel : super.classLabels)
            samples.add(ArrayUtils.addAll(gpsiSampler.getInstance().sample(dataset.getTrainingEntities(), classLabel, combinedImage), gpsiSampler.getInstance().sample(dataset.getValidationEntities(), classLabel, combinedImage)));
                
        for(i = 0; i < samples.size(); i++)
            means[i] = mean.evaluate(samples.get(i));
        
        samples = new ArrayList<>();
        for(String classLabel : super.classLabels)
            samples.add(gpsiSampler.getInstance().sample(dataset.getTestEntities(), classLabel, combinedImage));
        
        double value;
        int minDistanceIndex = 0, m = 0;
        for(i = 0; i < samples.size(); i++){
            for(j = 0; j < samples.get(i).length; j++){
                value = samples.get(i)[j];
                m++;
                for(k = 1; k < samples.size(); k++)
                    if(Math.abs(value - means[k]) < Math.abs(value - means[minDistanceIndex]))
                        minDistanceIndex = k;
                confusionMatrix[i][minDistanceIndex]++;
            }
        }
        
        double accuracy = 0.0;
        
        for(i = 0; i < samples.size(); i++)
            accuracy += confusionMatrix[i][i];
        
        accuracy /= m;
        
        
        String outRoot = "results/" + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime()) + "/";
        
        File folder = new File(outRoot);
        folder.mkdir();
        
        PrintWriter outReport = new PrintWriter(outRoot + "report.out");
        
        outReport.println("\nData set\n");
        
        outReport.println("Hyperspectral image:\t" + super.imgPath);
        outReport.println("Training masks location:\t" + super.trainingMasksPath);
        outReport.println("Test masks location:\t" + super.testMasksPath);
        
        outReport.println("\nGP configuration\n");
        
        outReport.println("Population size:\t" + super.popSize);
        outReport.println("Number of generations:\t" + super.numGenerations);
        outReport.println("Max initial depth of trees:\t" + super.maxInitDepth);
        
        outReport.println("\nML configuration\n");
        
        outReport.println("Number of individuals used for validation\t" + super.validation);
        outReport.print("Classes considered:\t");
        for(String label : this.classLabels)
            outReport.print(label + " ");
        outReport.print("\n");
        outReport.close();
        
        outReport = new PrintWriter(outRoot + "results.out");
        outReport.println("Best's fitness measure:\t" + best.getFitnessValue());
        outReport.println("Total accuracy:\t" + accuracy);
        outReport.close();
        
        outReport = new PrintWriter(outRoot + "curves.csv");
        outReport.println("tr, val");
        for(i = 0; i < fitnessCurveTrain.length; i++){
            outReport.println(fitnessCurveTrain[i] + "," + fitnessCurveVal[i]);
        }
        outReport.close();
        
        outReport = new PrintWriter(outRoot + "program.out");
        outReport.println(best.toStringNorm(0));
        outReport.close();
        
        outReport = new PrintWriter(outRoot + "confusion_matrix.csv");
        for(i = 0; i < confusionMatrix.length; i++){
            for(j = 0; j < confusionMatrix[0].length; j++)
                outReport.print(confusionMatrix[i][j] + ",");
            outReport.print("\n");
        }
        outReport.close();
        
        gp.outputSolution(best);
        System.out.println("Total accuracy: " + accuracy);
        
    }
    
    private GPGenotype create(GPConfiguration conf, int n_bands, gpsiJGAPFitnessFunction fitness) throws InvalidConfigurationException{
        
        Class[] types = {CommandGene.FloatClass};
        Class[][] argTypes = {{}};
        
        CommandGene[] variables = new CommandGene[n_bands];
        Variable[] b = new Variable[n_bands];
        CommandGene[] functions = {
            new Add(conf, CommandGene.FloatClass),
            new Subtract(conf, CommandGene.FloatClass),
            new Multiply(conf, CommandGene.FloatClass),
            new Divide(conf,CommandGene.FloatClass),
            // new Sine(conf, CommandGene.FloatClass),
            // new Cosine(conf, CommandGene.FloatClass),
            // new Exp(conf, CommandGene.FloatClass),
            new Terminal(conf,CommandGene.FloatClass, 1.0d, 1000000.0d, false)
        };
        
        for(int i = 0; i < n_bands; i++){
            b[i] = Variable.create(conf, "b" + i, CommandGene.FloatClass);
            variables[i] = b[i];
        }
        
        CommandGene[][] nodeSets = new CommandGene[1][];
        nodeSets[0] = (CommandGene[]) ArrayUtils.addAll(variables, functions);
        
        fitness.setB(b);
        
        return GPGenotype.randomInitialGenotype(conf, types, argTypes, nodeSets, 100, true);
    }
    
}
