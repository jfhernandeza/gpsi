/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedDivision;
import br.unicamp.ic.recod.gpsi.combine.gpsiJGAPVoxelCombinator;
import br.unicamp.ic.recod.gpsi.combine.gpsiVoxelBandCombinator;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPFitnessFunction;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPVoxelFitnessFunction;
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
public class gpsiJGAPVoxelSeparatorEvolver extends gpsiVoxelClassifierEvolver {
/*
    private final GPConfiguration config;
    private final gpsiJGAPVoxelFitnessFunction fitness;
    
    private final double[] curve;
    private String program;
    private final LinkedBlockingQueue<double[][]> distributions;
    
    public gpsiJGAPVoxelSeparatorEvolver(String[] args, gpsiDatasetReader datasetReader) throws Exception {
        
        config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());

        config.setMaxInitDepth(super.maxInitDepth);
        config.setPopulationSize(super.popSize);

        gpsiSampler sampler;
        sampler = new gpsiWholeSampler();
        fitness = new gpsiJGAPVoxelFitnessFunction((gpsiVoxelRawDataset) dataset, super.classLabels, new gpsiClusterSilhouetteScore(), sampler);
        config.setFitnessFunction(fitness);
        
        curve = new double[super.numGenerations];
        distributions = new LinkedBlockingQueue<>();
        
    }

    public void evolve() throws Exception {
        gpsiVoxelRawDataset dataset = (gpsiVoxelRawDataset) super.dataset;

        GPGenotype gp = create(config, dataset.getnBands(), fitness);
        IGPProgram best = null;

        int i, j, k;
        ArrayList<double[]> samples;
        gpsiVoxelBandCombinator voxelBandCombinator;
        
        for (int generation = 0; generation < super.numGenerations; generation++) {
            
            gp.evolve(1);
            best = gp.getAllTimeBest();
            
            if(this.dumpGens){
                double[][] dists = new double[this.classLabels.length][];
                voxelBandCombinator = new gpsiVoxelBandCombinator(new gpsiJGAPVoxelCombinator(fitness.getB(), best));
                voxelBandCombinator.combineEntity(dataset.getTrainingEntities());
                for(j = 0; j < this.classLabels.length; j++){
                    dists[j] = this.fitness.getSampler().sample(dataset.getTrainingEntities(), this.classLabels[j]);
                }
                this.distributions.put(dists);
            }

            this.curve[generation] = best.getFitnessValue() - 1.0;
            System.out.printf("Gen %d:\t%.5f\n", generation + 1, this.curve[generation]);

        }
        
        this.program = best.toStringNorm(0);

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
    
    public void printResults() throws FileNotFoundException {
        
        String outRoot = "results/" + this.outputPath + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime()) + "/";
        
        (new File(outRoot)).mkdir();
        
        if(this.dumpGens){
            (new File(outRoot + "gens/")).mkdir();
            for(Byte label : this.classLabels)
                (new File(outRoot + "gens/" + label + "/")).mkdir();
        }

        PrintWriter outR = new PrintWriter(outRoot + "report.out");
        
        outR.println("Data set location:\t" + super.path);

        outR.println("\nGP configuration\n");

        outR.println("Population size:\t" + super.popSize);
        outR.println("Number of generations:\t" + super.numGenerations);
        outR.println("Max initial depth of trees:\t" + super.maxInitDepth);

        outR.println("\nML configuration\n");

        outR.println("Number of individuals used for validation\t" + super.validation);
        outR.println("Bootstrapping\t" + super.bootstrap);
        outR.print("Classes considered:\t");
        for (Byte label : this.classLabels) {
            outR.print(label + " ");
        }
        outR.print("\n");
        outR.close();

        int i, j, k;
        double[] arr;
        
        k = 1;
        double[][] dist;
        while(this.dumpGens && !this.distributions.isEmpty()){
            dist = this.distributions.poll();
            for(i = 0; i < this.classLabels.length; i++){
                outR = new PrintWriter(outRoot + "gens/" + this.classLabels[i] + "/" + k + ".csv");
                for(j = 0; j < dist[i].length; j++){
                    outR.println(dist[i][j]);
                }
                outR.close();
            }
            k++;
        }
        
        outR = new PrintWriter(outRoot + "curve.csv");
        for(i = 0; i < numGenerations; i++){
            outR.println(this.curve[i]);
        }
        outR.close();
        
        outR = new PrintWriter(outRoot + "best.program");
        outR.println(this.program);
        outR.close();
        
    }
    */
}
