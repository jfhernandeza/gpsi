/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiFeatureVector;
import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SimpleLogistic;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author juan
 */
public class gpsiJGAPFitnessFunction extends GPFitnessFunction {
    
    private final gpsiRawDataset dataset;
    private final gpsiDescriptor descriptor;
    private Variable[] b;

    public gpsiJGAPFitnessFunction(gpsiRawDataset dataset, gpsiDescriptor descriptor) {
        this.dataset = dataset;
        this.descriptor = descriptor;
    }
    
    @Override
    protected double evaluate(IGPProgram igpp) {
        
        double mean_accuracy = 0.0;
        Object[] noargs = new Object[0];
        
        double[][][] image = this.dataset.getHyperspectralImage().getImg();
        int height = this.dataset.getHyperspectralImage().getHeight();
        int width = this.dataset.getHyperspectralImage().getWidth();
        int n_bands = this.dataset.getHyperspectralImage().getN_bands();
        
        double[][] combined_image = new double[height][width];
        gpsiCombinedImage combinedImage;
        
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                for(int i = 0; i < n_bands; i++){
                    this.b[i].set(image[y][x][i]);
                }
                combined_image[y][x] = igpp.execute_double(0, noargs);
            }
        }
        combinedImage = new gpsiCombinedImage(combined_image);
        
        gpsiMLDataset mlDataset = new gpsiMLDataset(this.descriptor);
        mlDataset.loadDataset(this.dataset, combinedImage);
        
        int dimensionality = mlDataset.getDimensionality();
        int n_classes = mlDataset.getNumberOfClasses();
        int n_entities = mlDataset.getNumberOfEntities();
        ArrayList<Integer> listOfClasses = new ArrayList<>(mlDataset.getListOfClasses());
        
        Attribute[] attributes = new Attribute[dimensionality];
        FastVector fvClassVal = new FastVector(n_classes);
        
        int i, j;
        for(i = 0; i < dimensionality; i++)
            attributes[i] = new Attribute("f" + Integer.toString(i));
        for(i = 0; i < n_classes; i++)
            fvClassVal.addElement(Integer.toString(listOfClasses.get(i)));
        
        Attribute classes = new Attribute("class", fvClassVal);
        
        FastVector fvWekaAttributes = new FastVector(dimensionality + 1);
        
        for(i = 0; i < dimensionality; i++)
            fvWekaAttributes.addElement(attributes[i]);
        fvWekaAttributes.addElement(classes);
        
        Instances instances = new Instances("Rel", fvWekaAttributes, n_entities);
        instances.setClassIndex(dimensionality);
        
        ArrayList<gpsiFeatureVector> entities = mlDataset.getEntities();
        ArrayList<Integer> labels = mlDataset.getLabels();
        
        Instance iExample;
        double[] features;
        for(i = 0; i < n_entities; i++){
            iExample = new Instance(dimensionality + 1);
            features = entities.get(i).getFeatures();
            for(j = 0; j < dimensionality; j++)
                iExample.setValue(j, features[j]);
            iExample.setValue(dimensionality, labels.get(i));
            instances.add(iExample);
        }
        
        int folds = 5;
        Random rand = new Random();
        Instances randData = new Instances(instances);
        randData.randomize(rand);
        
        Instances trainingSet, testingSet;
        Classifier cModel;
        Evaluation eTest;
        try {
            for(i = 0; i < folds; i++){
                    cModel = (Classifier) new SimpleLogistic();
                    trainingSet = randData.trainCV(folds, i);
                    testingSet = randData.testCV(folds, i);
                
                    cModel.buildClassifier(trainingSet);
                    
                    eTest = new Evaluation(trainingSet);
                    eTest.evaluateModel(cModel, testingSet);
                    
                    mean_accuracy += eTest.pctCorrect();
                    
            }
        } catch (Exception ex) {
            Logger.getLogger(gpsiJGAPFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mean_accuracy /= (folds * 100);
        
        System.out.println(mean_accuracy);
        
        return mean_accuracy;
        
    }

    public void setB(Variable[] b) {
        this.b = b;
    }
    
}
