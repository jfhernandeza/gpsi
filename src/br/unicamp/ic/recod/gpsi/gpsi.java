/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiMaskedLocalBinaryPatternDescriptor;
import br.unicamp.ic.recod.gpsi.gp.gpsiFitnessFunction_;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPFitnessFunction;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.io.gpsiMatlabFileReader;
import java.util.Random;
import org.apache.commons.lang.ArrayUtils;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DefaultGPFitnessEvaluator;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author jfhernandeza
 */
public class gpsi {

    public static void main(String[] args) throws Exception {
        
        if (args.length < 4){
            System.out.println("Arguments:\n"
                    + "\t<Path to the hyperspectral image>\n"
                    + "\t<Path to the directory with the examples grouped by class>\n"
                    + "\t<Population size>\n"
                    + "\t<Number of generations>");
            System.exit(1);
        }

        gpsiDatasetReader reader = new gpsiDatasetReader(new gpsiMatlabFileReader());

        String HyperspectralImagePath = args[0];
        String masksPath = args[1];
        int popSize = Integer.parseInt(args[2]);
        int numGenerations = Integer.parseInt(args[3]);

        gpsiRawDataset dataset = reader.readDataset(HyperspectralImagePath, masksPath);
        gpsiDescriptor descriptor = new gpsiMaskedLocalBinaryPatternDescriptor();
        
        System.out.println("Loaded " + dataset.getHyperspectralImage().getHeight() + "x" + dataset.getHyperspectralImage().getWidth() + " hyperspectral image with " + dataset.getHyperspectralImage().getN_bands() + " bands.");
        System.out.println("Loaded " + dataset.getEntities().size() + " examples.");
        
        GPConfiguration config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());  //new DefaultGPFitnessEvaluator()
        config.setMaxInitDepth(6);
        config.setPopulationSize(popSize);
        //gpsiFitnessFunction_ fitness = new gpsiFitnessFunction_();
        gpsiJGAPFitnessFunction fitness = new gpsiJGAPFitnessFunction(dataset, descriptor);
        config.setFitnessFunction(fitness);
        
        GPGenotype gp = create(config, dataset.getHyperspectralImage().getN_bands(), fitness);
        gp.evolve(numGenerations);
        gp.outputSolution(gp.getAllTimeBest());
        
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
    
    public static GPGenotype create_(GPConfiguration a_conf, gpsiFitnessFunction_ fitness) throws InvalidConfigurationException{
        Class[] types = {CommandGene.FloatClass};
        Class[][] argTypes = {{}};
        
        Variable vx;
        Float[] x = new Float[20];
        float[] y = new float[20];
        
        CommandGene[][] nodeSets = {{
            vx = Variable.create(a_conf, "X", CommandGene.FloatClass),
            new Add(a_conf, CommandGene.FloatClass),
            new Subtract(a_conf, CommandGene.FloatClass),
            new Multiply(a_conf, CommandGene.FloatClass),
            new Divide(a_conf,CommandGene.FloatClass),
            //new Sine(a_conf, CommandGene.FloatClass),
            //new Cosine(a_conf, CommandGene.FloatClass),
            //new Exp(a_conf, CommandGene.FloatClass),
            new Terminal(a_conf,CommandGene.FloatClass, 2.0d, 10.0d, false)
        }};
        CommandGene[] conc = (CommandGene[]) ArrayUtils.addAll(nodeSets[0], nodeSets[0]);
        nodeSets[0] = conc;
        Random random = new Random();
        for (int i = 0; i < 20; i++){
            float f = 2.0f* (random.nextFloat() - 0.5f);
            x[i] = new Float(f);
            y[i] = f * f * f * f + f * f * f + f * f - f;
            System.out.println(i+ ") " + x[i] + " " + y[i]);
        }
        fitness.setVx(vx);
        fitness.setX(x);
        fitness.setY(y);
        return GPGenotype.randomInitialGenotype(a_conf, types, argTypes, nodeSets, 100, true);
    }

}
