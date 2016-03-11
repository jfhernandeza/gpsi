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
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterDistortionScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiClusterSilhouetteScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiWilcoxonRankSumTestScore;
import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import org.apache.commons.lang.ArrayUtils;
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
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
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
        gpsiVoxelRawDataset dataset = reader.readDataset(conf.imgPath, conf.masksPath);
        
        System.out.println("Loaded " + dataset.getHyperspectralImage().getHeight() + "x" + dataset.getHyperspectralImage().getWidth() + " hyperspectral image with " + dataset.getHyperspectralImage().getN_bands() + " bands.");
        System.out.println("Loaded " + dataset.getEntities().size() + " examples.");
        
        GPConfiguration config = new GPConfiguration();
        
        //config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
        
        config.setMaxInitDepth(conf.maxInitDepth);
        config.setPopulationSize(conf.popSize);
        //gpsiJGAPRoiFitnessFunction fitness = new gpsiJGAPRoiFitnessFunction(dataset, conf.descriptor);
        gpsiJGAPPixelFitnessFunction fitness = new gpsiJGAPPixelFitnessFunction(dataset, conf.classLabels, new gpsiClusterSilhouetteScore());
        config.setFitnessFunction(fitness);
        GPGenotype gp = create(config, dataset.getHyperspectralImage().getN_bands(), fitness);
        
        LinkedList<Double> fitnessCurve = new LinkedList<>();
        IGPProgram best;
        
        for(int generation = 0; generation < conf.numGenerations; generation++){
            gp.evolve(1);
            best = gp.getAllTimeBest();
            System.out.println(best.getFitnessValue());
            fitnessCurve.add(best.getFitnessValue());
        }
        
        best = gp.getAllTimeBest();
        
        gpsiCombinedImage combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(dataset.getHyperspectralImage(), fitness.getB(), best);
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String outRoot = "results/" + dateFormat.format(Calendar.getInstance().getTime()) + "/";
        
        File folder = new File(outRoot);
        folder.mkdir();
        
        PrintWriter outResults = new PrintWriter(outRoot + "results.out");
        outResults.println("Configuration code:\t" + args[0]);
        outResults.println("Dataset code:\t" + args[1]);
        outResults.println("Descriptor code:\t" + args[2]);
        outResults.println("Classes code:\t" + args[3]);
        outResults.println("Best fitness:\t" + best.getFitnessValue());
        outResults.close();
        
        PrintWriter outProgram = new PrintWriter(outRoot + "program.out");
        outProgram.println(best.toStringNorm(0));
        outProgram.close();
        
        PrintWriter outCurve = new PrintWriter(outRoot + "curve.out");
        for(double f : fitnessCurve)
            outCurve.println(f);
        outCurve.close();
        
        PrintWriter outSample;
        for(Object className : dataset.getListOfClasses()){
            
            String classLabel = (String) className;
            outSample = new PrintWriter(outRoot + "sample_" + classLabel + ".out");
            for(double f : gpsiSampler.getInstance().sample(dataset, classLabel, combinedImage))
                outSample.println(f);
            outSample.close();
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
