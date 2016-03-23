/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi;

import br.unicamp.ic.recod.gpsi.conf.gpsiConfiguration;
import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPFitnessFunction;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPPixelFitnessFunction;
import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiJGAPImageCombinator;
import br.unicamp.ic.recod.gpsi.io.gpsiMatlabFileReader;
import br.unicamp.ic.recod.gpsi.io.gpsiVoxelDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
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
import org.jgap.gp.function.Cosine;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Exp;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Sine;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author jfhernandeza
 */
public class gpsi {

    public static void main(String[] args) throws Exception {
        
        if (args.length < 4){
            System.out.println("Arguments:\n"
                    + "\t<Configuration code>\n"
                    + "\t<Dataset>\n"
                    + "\t<Descriptor code>\n"
                    + "\t<Considered classes code>");
            System.exit(1);
        }
        
        gpsiConfiguration conf = new gpsiConfiguration(args);

        //gpsiRoiDatasetReader reader = new gpsiRoiDatasetReader(new gpsiMatlabFileReader());
        //gpsiRoiRawDataset dataset = reader.readDataset(conf.imgPath, conf.masksPath);
        
        gpsiVoxelDatasetReader reader = new gpsiVoxelDatasetReader(new gpsiMatlabFileReader());
        gpsiVoxelRawDataset dataset = reader.readDataset(conf.imgPath, conf.trainingMasksPath, conf.testMasksPath);
        
        System.out.println("Loaded " + dataset.getHyperspectralImage().getHeight() + "x" + dataset.getHyperspectralImage().getWidth() + " hyperspectral image with " + dataset.getHyperspectralImage().getN_bands() + " bands.");
        System.out.println("Loaded " + dataset.getTrainingEntities().size() + " examples for training.");
        System.out.println("Loaded " + dataset.getTestEntities().size() + " examples for testing.");
        
        GPConfiguration config = new GPConfiguration();
        
        //config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
        
        config.setMaxInitDepth(conf.maxInitDepth);
        config.setPopulationSize(conf.popSize);
        //gpsiJGAPRoiFitnessFunction fitness = new gpsiJGAPRoiFitnessFunction(dataset, conf.descriptor);
        gpsiJGAPPixelFitnessFunction fitness = new gpsiJGAPPixelFitnessFunction(dataset, conf.classLabels, new gpsiClusterSilhouetteScore());
        config.setFitnessFunction(fitness);
        GPGenotype gp = create(config, dataset.getHyperspectralImage().getN_bands(), fitness);
        
        double[] fitnessCurve = new double[conf.numGenerations];
        double[] fitnessCurveTest = new double[conf.numGenerations];
        IGPProgram best = null, current;
        double bestScore = -Double.MAX_VALUE, currentScore;
        
        int n_top = 5, i, j;
        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();
        gpsiCombinedImage combinedImage;
        double testScore, trainScore, bestTestScore = -1.0, bestTrainScore = -1.0;
        ArrayList<double[]> samples;
        for(int generation = 0; generation < conf.numGenerations; generation++){
            gp.evolve(1);
            gp.getGPPopulation().sortByFitness();
            
            for(i = 0; i < n_top; i++){

                current = gp.getGPPopulation().getGPPrograms()[i];
                
                combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(dataset.getHyperspectralImage(), fitness.getB(), current);
                samples = new ArrayList<>();
                samples.add(gpsiSampler.getInstance().sample(dataset.getIndexesPerClass(), dataset.getTestEntities(), fitness.getClassLabels()[0], combinedImage));
                samples.add(gpsiSampler.getInstance().sample(dataset.getIndexesPerClass(), dataset.getTestEntities(), fitness.getClassLabels()[1], combinedImage));
                testScore = fitness.getScore().score(samples);
                trainScore = current.getFitnessValue() - 1.0;
                
                currentScore = mean.evaluate(new double[] {trainScore, testScore}) - sd.evaluate(new double[] {trainScore, testScore});
                
                if(currentScore > bestScore){
                    best = current;
                    bestTrainScore = trainScore;
                    bestTestScore = testScore;
                }
                
            }
            
            if(best == null)
                best = gp.getGPPopulation().getGPPrograms()[0];
            
            fitnessCurve[generation] = bestTrainScore;
            fitnessCurveTest[generation] = bestTestScore;
            
            //TODO: Fix!
            System.out.println(bestTrainScore + "\t" + bestTestScore);
            
        }
        
        combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(dataset.getHyperspectralImage(), fitness.getB(), best);
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String outRoot = "results/" + dateFormat.format(Calendar.getInstance().getTime()) + "/";
        
        File folder = new File(outRoot);
        folder.mkdir();
        
        PrintWriter outResults = new PrintWriter(outRoot + "results.out");
        outResults.println("Configuration code:\t" + args[0]);
        outResults.println("Dataset code:\t" + args[1]);
        outResults.println("Descriptor code:\t" + args[2]);
        outResults.println("Classes code:\t" + args[3]);
        outResults.println("Best fitness:\t" + (best.getFitnessValue() - 1.0));
        outResults.close();
        
        PrintWriter outProgram = new PrintWriter(outRoot + "program.out");
        outProgram.println(best.toStringNorm(0));
        outProgram.close();
        
        PrintWriter outCurveTrain = new PrintWriter(outRoot + "curve_tr.out");
        PrintWriter outCurveTest = new PrintWriter(outRoot + "curve_ts.out");
        for(i = 0; i < conf.numGenerations; i++){
            outCurveTrain.println(fitnessCurve[i]);
            outCurveTest.println(fitnessCurveTest[i]);
        }
        outCurveTrain.close();
        outCurveTest.close();
        
        folder = new File(outRoot + "train_samples/");
        folder.mkdir();
        folder = new File(outRoot + "test_samples/");
        folder.mkdir();
        
        PrintWriter outSample;
        for(Object className : dataset.getListOfClasses()){
            
            String classLabel = (String) className;
            outSample = new PrintWriter(outRoot + "train_samples/" + "sample_" + classLabel + ".out");
            for(double f : gpsiSampler.getInstance().sample(dataset.getIndexesPerClass(), dataset.getTrainingEntities(), classLabel, combinedImage))
                outSample.println(f);
            outSample.close();
        }
        
        for(Object className : dataset.getListOfClasses()){
            
            String classLabel = (String) className;
            outSample = new PrintWriter(outRoot + "test_samples/" + "sample_" + classLabel + ".out");
            for(double f : gpsiSampler.getInstance().sample(dataset.getIndexesPerClass(), dataset.getTestEntities(), classLabel, combinedImage))
                outSample.println(f);
            outSample.close();
        }
        
        for(i = 0; i < fitness.getClassLabels().length; i++){
            folder = new File(outRoot + String.valueOf(fitness.getClassLabels()[i]) + ".train");
            folder.createNewFile();
        }
        
        gp.outputSolution(best);
        System.exit(0);

    }

    public static GPGenotype create(GPConfiguration conf, int n_bands, gpsiJGAPFitnessFunction fitness) throws InvalidConfigurationException{
        
        Class[] types = {CommandGene.FloatClass};
        Class[][] argTypes = {{}};
        
        CommandGene[] variables = new CommandGene[n_bands];
        Variable[] b = new Variable[n_bands];
        CommandGene[] functions = {
            new Add(conf, CommandGene.FloatClass),
            new Subtract(conf, CommandGene.FloatClass),
            new Multiply(conf, CommandGene.FloatClass),
            new Divide(conf,CommandGene.FloatClass),
            new Sine(conf, CommandGene.FloatClass),
            new Cosine(conf, CommandGene.FloatClass),
            new Exp(conf, CommandGene.FloatClass),
            //new Terminal(conf,CommandGene.FloatClass, 2.0d, 10.0d, false)
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
