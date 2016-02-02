/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiMaskedLocalBinaryPatternDescriptor;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPFitnessFunction;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.io.gpsiMatlabFileReader;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Properties;
import org.apache.commons.lang.ArrayUtils;
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
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author jfhernandeza
 */
public class gpsi {

    public static void main(String[] args) throws Exception {
        
        if (args.length < 2){
            System.out.println("Arguments:\n"
                    + "\t<Configuration code>\n"
                    + "\t<Dataset>");
            System.exit(1);
        }

        Properties propParams = new Properties();
        Properties propDataset = new Properties();
        propParams.load(new FileInputStream("conf/params/" + args[0] + ".properties"));
        propDataset.load(new FileInputStream("conf/datasets/" + args[1] + ".properties"));

        int popSize = Integer.parseInt(propParams.getProperty("pop_size"));
        int numGenerations = Integer.parseInt(propParams.getProperty("num_gen"));

        gpsiDatasetReader reader = new gpsiDatasetReader(new gpsiMatlabFileReader());
        
        gpsiRawDataset dataset = reader.readDataset(propDataset.getProperty("img_path"), propDataset.getProperty("masks_path"));
        
        gpsiDescriptor descriptor = new gpsiMaskedLocalBinaryPatternDescriptor(Integer.parseInt(propParams.getProperty("lbp_neighborhood")));
        
        System.out.println("Loaded " + dataset.getHyperspectralImage().getHeight() + "x" + dataset.getHyperspectralImage().getWidth() + " hyperspectral image with " + dataset.getHyperspectralImage().getN_bands() + " bands.");
        System.out.println("Loaded " + dataset.getEntities().size() + " examples.");
        
        GPConfiguration config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());  //new DeltaGPFitnessEvaluator()
        config.setMaxInitDepth(6);
        config.setPopulationSize(popSize);
        gpsiJGAPFitnessFunction fitness = new gpsiJGAPFitnessFunction(dataset, descriptor);
        config.setFitnessFunction(fitness);
        GPGenotype gp = create(config, dataset.getHyperspectralImage().getN_bands(), fitness);
        
        LinkedList<Double> fitnessCurve = new LinkedList<>();
        IGPProgram best;
        
        for(int generation = 0; generation < numGenerations; generation++){
            gp.evolve(1);
            best = gp.getAllTimeBest();
            System.out.println(best.getFitnessValue());
            fitnessCurve.add(best.getFitnessValue());
        }
        
        best = gp.getAllTimeBest();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        
        PrintWriter outProgram = new PrintWriter("results/" + dateFormat.format(cal.getTime()) + ".program");
        outProgram.println(best.getFitnessValue());
        outProgram.println(best.toStringNorm(0));
        outProgram.close();
        
        PrintWriter outCurve = new PrintWriter("results/" + dateFormat.format(cal.getTime()) + ".curve");
        for(double f : fitnessCurve)
            outCurve.println(f);
        outCurve.close();
        
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
            //new Sine(conf, CommandGene.FloatClass),
            //new Cosine(conf, CommandGene.FloatClass),
            //new Exp(conf, CommandGene.FloatClass),
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
