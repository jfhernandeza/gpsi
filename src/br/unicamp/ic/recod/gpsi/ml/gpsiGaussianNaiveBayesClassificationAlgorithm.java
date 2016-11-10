/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

/**
 *
 * @author juan
 */
public class gpsiGaussianNaiveBayesClassificationAlgorithm extends gpsiClassificationAlgorithm {
    
    private HashMap<Byte, Double> priors;
    private HashMap<Byte, NormalDistribution[]> normalDistributions;
    private double confidence;

    @Override
    public void fit(HashMap<Byte, ArrayList<double[]>> x) {
        
        Mean mean = new Mean();
        StandardDeviation sd = new StandardDeviation();
        
        this.priors = new HashMap<>();
        this.normalDistributions = new HashMap<>();
        
        int i, m = 0;
        NormalDistribution[] nd;
        RealMatrix entities;
        
        this.nClasses = x.size();
        this.dimensionality = 0;
        for(byte label : x.keySet()){
            if(this.dimensionality <= 0)
                this.dimensionality = x.get(label).get(0).length;
            entities = MatrixUtils.createRealMatrix(x.get(label).toArray(new double[0][]));
            nd = new NormalDistribution[this.dimensionality];
            for(i = 0; i < this.dimensionality; i++)
                nd[i] = new NormalDistribution(mean.evaluate(entities.getColumn(i)), sd.evaluate(entities.getColumn(i)));
            this.normalDistributions.put(label, nd);
            this.priors.put(label, (double) entities.getRowDimension());
            m += entities.getRowDimension();
        }
        
        for(byte label : this.priors.keySet())
            this.priors.put(label, this.priors.get(label) / m);
        
    }

    @Override
    public byte predict(double[] x) {
        
        HashMap<Byte, Double> conditionals = new HashMap<>();
        
        int i = 0;
        double cond, total;
        for(byte label : normalDistributions.keySet()){
            cond = 1.0;
            for(i = 0; i < x.length; i++)
                cond *= normalDistributions.get(label)[i].density(x[i]);
            conditionals.put(label, cond);
        }
        
        total = 0.0;
        confidence = Double.NEGATIVE_INFINITY;
        for(byte label : conditionals.keySet()){
            cond = conditionals.get(label) * priors.get(label);
            total += cond;
            if(cond > confidence){
                confidence = cond;
                i = label;
            }
        }
        
        confidence /= total;
        
        return (byte) i;
                
    }

    public double getConfidence() {
        return confidence;
    }
    
}
