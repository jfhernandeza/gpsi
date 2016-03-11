/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiRoiRawDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiFeatureVector;
import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiJGAPImageCombinator;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jgap.gp.IGPProgram;
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
public class gpsiJGAPRoiFitnessFunction extends gpsiJGAPFitnessFunction<gpsiRoiRawDataset> {
    
    private final gpsiDescriptor descriptor;

    public gpsiJGAPRoiFitnessFunction(gpsiRoiRawDataset dataset, gpsiDescriptor descriptor) {
        super(dataset);
        this.descriptor = descriptor;
    }
    
    @Override
    protected double evaluate(IGPProgram igpp) {
        
        double mean_accuracy = 0.0;
        Object[] noargs = new Object[0];
        
        gpsiCombinedImage combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(this.dataset.getHyperspectralImage(), super.b, igpp);
        
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
            Logger.getLogger(gpsiJGAPRoiFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        mean_accuracy /= (folds * 100);
        
        return mean_accuracy;
        
    }
    
}
