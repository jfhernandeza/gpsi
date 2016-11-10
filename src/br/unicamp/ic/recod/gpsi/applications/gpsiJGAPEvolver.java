/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.combine.gpsiJGAPVoxelCombiner;
import br.unicamp.ic.recod.gpsi.data.gpsiConstantBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiProbabilisticBootstrapper;
import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedDivision;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedNaturalLogarithm;
import br.unicamp.ic.recod.gpsi.genotype.gpsiJGAPProtectedSquareRoot;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPFitnessFunction;
import br.unicamp.ic.recod.gpsi.gp.gpsiJGAPVoxelFitnessFunction;
import br.unicamp.ic.recod.gpsi.io.element.gpsiConfigurationIOElement;
import br.unicamp.ic.recod.gpsi.io.element.gpsiDoubleCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.element.gpsiIntegerCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.element.gpsiStringIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import br.unicamp.ic.recod.gpsi.measures.gpsiHClustScore;
import br.unicamp.ic.recod.gpsi.measures.gpsiSampleSeparationScore;
import br.unicamp.ic.recod.gpsi.ml.gpsiNearestCentroidClassificationAlgorithm;
import br.unicamp.ic.recod.gpsi.ml.gpsiClassificationAlgorithm;
import br.unicamp.ic.recod.gpsi.ml.gpsiClassifier;
import br.unicamp.ic.recod.gpsi.ml.gpsiGaussianNaiveBayesClassificationAlgorithm;
import br.unicamp.ic.recod.gpsi.ml.gpsiHierarchicalClusteringClassificationAlgorithm;
import java.util.HashMap;
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
import org.jgap.impl.SeededRandomGenerator;

/**
 *
 * @author juan
 */
public class gpsiJGAPEvolver extends gpsiEvolver {

    private final int maxInitDepth;

    private final double optimum;
    private final GPConfiguration config;
    private final gpsiJGAPVoxelFitnessFunction fitness;

    private IGPProgram[] best;

    public gpsiJGAPEvolver(
            String dataSetPath,
            gpsiDatasetReader datasetReader,
            Byte[] classLabels,
            String outputPath,
            int popSize,
            int numGenerations,
            double crossRate,
            double mutRate,
            int validation,
            double bootstrap,
            boolean dumpGens,
            int maxInitDepth,
            gpsiSampleSeparationScore score,
            double errorScore,
            long seed) throws InvalidConfigurationException, Exception {

        super(dataSetPath, datasetReader, classLabels, outputPath, popSize,
                numGenerations, crossRate, mutRate, validation, bootstrap,
                dumpGens, errorScore, seed);
        this.maxInitDepth = maxInitDepth;

        config = new GPConfiguration();
        config.setGPFitnessEvaluator(new DefaultGPFitnessEvaluator());

        config.setMaxInitDepth(this.maxInitDepth);
        config.setPopulationSize(popSize);
        config.setCrossoverProb((float) crossRate);
        config.setMutationProb((float) mutRate);

        gpsiSampler sampler = (bootstrap <= 0.0) ? new gpsiWholeSampler() : (bootstrap < 1.0) ? new gpsiProbabilisticBootstrapper(bootstrap, this.seed) : new gpsiConstantBootstrapper((int) bootstrap, this.seed);

        fitness = new gpsiJGAPVoxelFitnessFunction((gpsiVoxelRawDataset) rawDataset, this.classLabels, score, sampler);
        optimum = fitness.getScore().optimum;
        config.setFitnessFunction(fitness);

        config.setPreservFittestIndividual(true);
        if(this.seed != 0)
            config.setRandomGenerator(new SeededRandomGenerator(this.seed));

        stream.register(new gpsiConfigurationIOElement(null, "report.out"));

    }

    @Override
    public void run() throws InvalidConfigurationException, InterruptedException, Exception {

        int i;
        byte nFolds = 5;
        double bestScore, currentScore, fitnessCurves[][];
        
        gpsiDescriptor descriptor;
        gpsiMLDataset mlDataset;
        gpsiVoxelRawDataset dataset;
        GPGenotype gp;
        
        IGPProgram current, bestVal, elite[] = null;

        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();
        
        double validationScore, trainScore, bestValidationScore, bestTrainScore, samples[][][];
        
        for (byte f = 0; f < nFolds; f++) {

            System.out.println("\nRun " + (f + 1) + "\n");

            rawDataset.assignFolds(new byte[]{f, (byte) ((f + 1) % nFolds), (byte) ((f + 2) % nFolds)},
                                   new byte[]{(byte) ((f + 3) % nFolds)},
                                   new byte[]{(byte) ((f + 4) % nFolds)});
            dataset = (gpsiVoxelRawDataset) rawDataset;
            gp = create(config, dataset.getnBands(), fitness);

            fitnessCurves = new double[super.numGenerations][];
            current = null;
            bestVal = null;
            bestScore = -Double.MAX_VALUE;
            bestValidationScore = -1.0;
            bestTrainScore = -1.0;

            if (validation > 0) {
                elite = new IGPProgram[validation];
            }
            
            for (int generation = 0; generation < super.numGenerations; generation++) {

                gp.evolve(1);
                gp.getGPPopulation().sortByFitness();

                if (validation > 0) {
                    elite = mergeElite(elite, gp.getGPPopulation().getGPPrograms(), generation);
                }

                if (this.dumpGens) {

                    double[][][] dists;
                    descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(fitness.getB(), gp.getGPPopulation().getGPPrograms()[0]));
                    mlDataset = new gpsiMLDataset(descriptor);
                    mlDataset.loadWholeDataset(rawDataset, true);

                    dists = (new gpsiWholeSampler()).sample(mlDataset.getTrainingEntities(), this.classLabels);
                    for (i = 0; i < this.classLabels.length; i++) {
                        stream.register(new gpsiDoubleCsvIOElement(dists[i], null, "gens/f" + (f + 1) + "/" + classLabels[i] + "/" + (generation + 1) + ".csv"));
                    }

                }

                i = this.popSize - 1;
                for(IGPProgram prog : elite)
                    if(!in(gp.getGPPopulation().getGPPrograms(), prog)){
                        gp.getGPPopulation().setGPProgram(i, prog);
                        i--;
                    }
                
                fitnessCurves[generation] = new double[]{gp.getAllTimeBest().getFitnessValue() - 1.0};
                System.out.printf("%3dg: %.5f\n", generation + 1, fitnessCurves[generation][0]);
                
                if(fitnessCurves[generation][0] >= optimum)
                    break;
                
            }

            best = new IGPProgram[2];
            best[0] = gp.getAllTimeBest();
            for (i = 0; i < super.validation; i++) {

                current = elite[i];

                descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(fitness.getB(), current));
                mlDataset = new gpsiMLDataset(descriptor);
                mlDataset.loadWholeDataset(rawDataset, true);

                samples = this.fitness.getSampler().sample(mlDataset.getValidationEntities(), classLabels);

                validationScore = fitness.getScore().score(samples);
                trainScore = current.getFitnessValue() - 1.0;

                currentScore = mean.evaluate(new double[]{trainScore, validationScore}) - sd.evaluate(new double[]{trainScore, validationScore});

                if (currentScore > bestScore) {
                    best[1] = current;
                    bestScore = currentScore;
                }
                
            }

            stream.register(new gpsiDoubleCsvIOElement(fitnessCurves, null, "curves/f" + (f + 1) + ".csv"));

            System.out.println("Best solution tr: " + gp.getAllTimeBest().toStringNorm(0));
            stream.register(new gpsiStringIOElement(gp.getAllTimeBest().toStringNorm(0), "programs/f" + (f + 1) + "train.program"));

            if (validation > 0) {
                System.out.println("Best solution tv: " + best[1].toStringNorm(0));
                stream.register(new gpsiStringIOElement(best[1].toStringNorm(0), "programs/f" + (f + 1) + "train_val.program"));
            }

            rawDataset.assignFolds(new byte[]{f, (byte) ((f + 1) % nFolds), (byte) ((f + 2) % nFolds), (byte) ((f + 3) % nFolds)}, null, new byte[]{(byte) ((f + 4) % nFolds)});

            HashMap<String, gpsiClassificationAlgorithm> classificationAlgorithms = new HashMap<>();
            classificationAlgorithms.put("NCC", new gpsiNearestCentroidClassificationAlgorithm(new Mean()));
            classificationAlgorithms.put("GNB", new gpsiGaussianNaiveBayesClassificationAlgorithm());

            if(this.fitness.getScore() instanceof gpsiHClustScore)
                classificationAlgorithms.put("HC", new gpsiHierarchicalClusteringClassificationAlgorithm(((gpsiHClustScore)this.fitness.getScore()).getLinkType()));
            
            gpsiClassificationAlgorithm classificationAlgorithm;
            gpsiClassifier classifier;
            int[][] confusionMatrix;

            for (String alg : classificationAlgorithms.keySet()) {
                
                descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(fitness.getB(), best[0]));
                
                classificationAlgorithm = classificationAlgorithms.get(alg);
                classifier = new gpsiClassifier(descriptor, classificationAlgorithm);

                classifier.fit(this.rawDataset.getTrainingEntities());
                classifier.predict(this.rawDataset.getTestEntities());

                confusionMatrix = classifier.getConfusionMatrix();

                stream.register(new gpsiIntegerCsvIOElement(confusionMatrix, null, "confusion_matrices/" + alg + "/f" + (f + 1) + "_train.csv"));

                if (validation > 0) {
                    descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(fitness.getB(), best[1]));
                    classificationAlgorithm = classificationAlgorithms.get(alg);
                    classifier = new gpsiClassifier(descriptor, classificationAlgorithm);

                    classifier.fit(this.rawDataset.getTrainingEntities());
                    classifier.predict(this.rawDataset.getTestEntities());

                    confusionMatrix = classifier.getConfusionMatrix();

                    stream.register(new gpsiIntegerCsvIOElement(confusionMatrix, null, "confusion_matrices/" + alg + "/f" + (f + 1) + "_train_val.csv"));

                }
            }

        }

    }

    private boolean in(IGPProgram[] pop, IGPProgram prog){
        for(IGPProgram prog_ : pop){
            if(prog.toStringNorm(0).equals(prog_.toStringNorm(0)))
                return true;
        }
        return false;
    }
    
    private IGPProgram[] mergeElite(IGPProgram[] elite, IGPProgram[] programs, int generation) {

        int i, j, k;

        if (generation == 0) {
            elite[0] = programs[0];
            i = 1;
            j = 1;
            while (i < validation) {
                if (!programs[j].toStringNorm(0).equals(elite[i - 1].toStringNorm(0))) {
                    elite[i] = programs[j];
                    i++;
                }
                j++;
            }
        } else {
            i = -1;
            j = 0;
            while (j < validation) {
                i++;
                for (k = 0; k < validation; k++)
                    if (programs[i].toStringNorm(0).equals(elite[k].toStringNorm(0)))
                        break;
                if (k < validation)
                    continue;
                k = validation - 1;
                while (k >= 0 && programs[i].getFitnessValue() > elite[k].getFitnessValue()) {
                    if (k < validation - 1)
                        elite[k + 1] = elite[k];
                    elite[k] = programs[i];
                    k--;
                }
                j++;
            }
        }

        return elite;

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
            new gpsiJGAPProtectedNaturalLogarithm(conf, CommandGene.DoubleClass),
            new gpsiJGAPProtectedSquareRoot(conf, CommandGene.DoubleClass),
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

    public IGPProgram[] getBest() {
        return best;
    }

}
