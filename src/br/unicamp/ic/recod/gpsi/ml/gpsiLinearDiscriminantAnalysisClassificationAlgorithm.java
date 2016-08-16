/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.moment.VectorialMean;

/**
 *
 * @author juan
 */
public class gpsiLinearDiscriminantAnalysisClassificationAlgorithm extends gpsiClassificationAlgorithm{

    private HashMap<Byte, double[]> centroids;
    
    @Override
    public void fit(HashMap<Byte, ArrayList<double[]>> x) {
        
        VectorialMean mean;
        
        this.centroids = new HashMap<>();
        this.nClasses = x.size();
        this.dimensionality = 0;
        for(byte label : x.keySet()){
            if(this.dimensionality <= 0)
                this.dimensionality = x.get(label).get(0).length;

            mean = new VectorialMean(this.dimensionality);
            for(double[] v : x.get(label))
                mean.increment(v);
            
            this.centroids.put(label, mean.getResult());
        }
    }

    @Override
    public byte predict(double[] x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
