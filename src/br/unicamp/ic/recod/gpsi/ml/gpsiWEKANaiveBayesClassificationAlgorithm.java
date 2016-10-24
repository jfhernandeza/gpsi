/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author juan
 */
public class gpsiWEKANaiveBayesClassificationAlgorithm extends gpsiClassificationAlgorithm {

    private Classifier wekaClassifier;
    private Instances instances;

    @Override
    public void fit(HashMap<Byte, ArrayList<double[]>> x) {

        int i, j, nEntities = 0;

        this.nClasses = x.size();

        for (byte label : x.keySet()) {
            this.dimensionality = x.get(label).get(0).length;
            nEntities += x.get(label).size();
        }

        FastVector fvWekaAttributes = new FastVector(dimensionality + 1);
        for (i = 0; i < dimensionality; i++)
            fvWekaAttributes.addElement(new Attribute("f" + Integer.toString(i)));

        FastVector fvClassVal = new FastVector(nClasses);
        for (byte c : x.keySet())
            fvClassVal.addElement(Byte.toString(c));

        fvWekaAttributes.addElement(new Attribute("class", fvClassVal));

        instances = new Instances("Rel", fvWekaAttributes, nEntities);
        instances.setClassIndex(dimensionality);

        Instance iExample;
        for (byte label : x.keySet()) {
            for (double[] featureVector : x.get(label)) {
                iExample = new Instance(dimensionality + 1);
                iExample.setDataset(instances);
                for (j = 0; j < dimensionality; j++)
                    iExample.setValue(j, featureVector[j]);
                iExample.setValue(dimensionality, Byte.toString(label));
                instances.add(iExample);
            }
        }

        wekaClassifier = new NaiveBayes();//new SimpleLogistic();//new BayesianLogisticRegression();
        try {
            wekaClassifier.buildClassifier(instances);
        } catch (Exception ex) {
            Logger.getLogger(gpsiWEKANaiveBayesClassificationAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public byte predict(double[] x) {

        byte prediction = 0;
        Instance iExample = new Instance(dimensionality);
        
        for (int i = 0; i < dimensionality; i++){
            iExample.setDataset(instances);
            iExample.setValue(i, x[i]);
        }

        try {
            prediction = Byte.parseByte(instances.classAttribute().value((byte) wekaClassifier.classifyInstance(iExample)));
        } catch (Exception ex) {
            Logger.getLogger(gpsiWEKANaiveBayesClassificationAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return prediction;
    }

}
