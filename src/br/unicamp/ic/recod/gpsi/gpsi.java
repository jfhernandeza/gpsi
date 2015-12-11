/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.gp.gpsiFitnessFunction;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.io.gpsiMatlabFileReader;
import java.util.Random;
import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Cosine;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Exp;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Sine;
import org.jgap.gp.function.Subtract;
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

        //gpsiDatasetReader reader = new gpsiDatasetReader(new gpsiMatlabFileReader());

        //String HyperspectralImagePath = "/home/juan/Documentos/matlab/salinas/Salinas.mat";
        //String masksPath = "/home/juan/Documentos/matlab/salinas/classes_m/";

        //gpsiRawDataset dataset = reader.readDataset(HyperspectralImagePath, masksPath);

        GPConfiguration config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setMaxInitDepth(6);
        config.setPopulationSize(200);
        gpsiFitnessFunction fitness = new gpsiFitnessFunction();
        config.setFitnessFunction(fitness);
        
        GPGenotype gp = create(config, fitness);
        gp.evolve(100);
        gp.outputSolution(gp.getAllTimeBest());

    }

    public static GPGenotype create(GPConfiguration a_conf, gpsiFitnessFunction fitness) throws InvalidConfigurationException{
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
