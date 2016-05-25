/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi;

import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPVoxelClassifierEvolver;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPVoxelSeparatorEvolver;
import br.unicamp.ic.recod.gpsi.io.gpsiMatlabFileReader;
import br.unicamp.ic.recod.gpsi.io.gpsiVoxelDatasetReader;

/**
 *
 * @author jfhernandeza
 */
public class gpsi {

    public static void main(String[] args) throws Exception {
        
        //gpsiJGapVoxelClassifierEvolver evolver = new gpsiJGapVoxelClassifierEvolver(new gpsiVoxelDatasetReader(new gpsiMatlabFileReader()));
        
        gpsiJGAPVoxelSeparatorEvolver evolver = new gpsiJGAPVoxelSeparatorEvolver(args, new gpsiVoxelDatasetReader(new gpsiMatlabFileReader()));
        evolver.getDataset().assignFolds(new int[]{0}, null, null);
        evolver.evolve();
        evolver.printResults();
        
        /*
        gpsiJGAPVoxelClassifierEvolver evolver = new gpsiJGAPVoxelClassifierEvolver(args, new gpsiVoxelDatasetReader(new gpsiMatlabFileReader()));
        int nFolds = evolver.getDataset().getnFolds(), i;
        
        boolean dumpGens = evolver.isDumpGens();
        
        for(i = 0; i < 5; i++){
            System.out.println("\nRun " + (i + 1) + "\n");
            evolver.getDataset().assignFolds(new int[]{i, (i + 1) % nFolds, (i + 2) % nFolds}, new int[]{(i + 3) % nFolds}, new int[]{(i + 4) % nFolds});
            evolver.evolve();
            evolver.setDumpGens(false);
        }
        evolver.setDumpGens(dumpGens);
        evolver.printResults();
        System.exit(0);
        */
        
        // TODO: Generalize measures to support high dimensionalities
        // TODO: Make the best individual persist
/*
        GPConfiguration config = new GPConfiguration();
        
        //config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());
        
        config.setMaxInitDepth(conf.maxInitDepth);
        config.setPopulationSize(conf.popSize);
        //gpsiJGAPRoiFitnessFunction fitness = new gpsiJGAPRoiFitnessFunction(dataset, conf.descriptor);
        gpsiJGAPVoxelFitnessFunction fitness = new gpsiJGAPVoxelFitnessFunction(dataset, conf.classLabels, new gpsiClusterSilhouetteScore());
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
                
                for(String classLabel : conf.classLabels)
                    samples.add(gpsiSampler.getInstance().sample(dataset.getTestEntities(), classLabel, combinedImage));
                
                testScore = fitness.getScore().score(samples);
                trainScore = current.getFitnessValue() - 1.0;
                
                currentScore = mean.evaluate(new double[] {trainScore, testScore}) - sd.evaluate(new double[] {trainScore, testScore});
                
                if(currentScore > bestScore){
                    best = current;
                    bestScore = currentScore;
                    bestTrainScore = trainScore;
                    bestTestScore = testScore;
                }
                
            }
            
            fitnessCurve[generation] = bestTrainScore;
            fitnessCurveTest[generation] = bestTestScore;
            
            System.out.println(bestTrainScore + "\t" + bestTestScore);
            
        }
        
        combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(dataset.getHyperspectralImage(), fitness.getB(), best);
        
        String outRoot = "results/" + (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(Calendar.getInstance().getTime()) + "/";
        
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
        for(Object className : dataset.getTrainingEntities().keySet()){
            
            String classLabel = (String) className;
            outSample = new PrintWriter(outRoot + "train_samples/" + "sample_" + classLabel + ".out");
            for(double f : gpsiSampler.getInstance().sample(dataset.getTrainingEntities(), classLabel, combinedImage))
                outSample.println(f);
            outSample.close();
        }
        
        for(Object className : dataset.getTrainingEntities().keySet()){
            
            String classLabel = (String) className;
            outSample = new PrintWriter(outRoot + "test_samples/" + "sample_" + classLabel + ".out");
            for(double f : gpsiSampler.getInstance().sample(dataset.getTestEntities(), classLabel, combinedImage))
                outSample.println(f);
            outSample.close();
        }
        
        for(i = 0; i < fitness.getClassLabels().length; i++){
            folder = new File(outRoot + String.valueOf(fitness.getClassLabels()[i]) + ".train");
            folder.createNewFile();
        }
        
        gp.outputSolution(best);
        System.exit(0);*/
        
    }

    /*public static GPGenotype create(GPConfiguration conf, int n_bands, gpsiJGAPFitnessFunction fitness) throws InvalidConfigurationException{
        
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
    }*/

}
