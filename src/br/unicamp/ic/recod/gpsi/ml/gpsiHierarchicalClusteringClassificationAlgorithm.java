/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiWholeSampler;
import br.unicamp.ic.recod.gpsi.measures.gpsiHClustScore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author juan
 */
public class gpsiHierarchicalClusteringClassificationAlgorithm extends gpsiClassificationAlgorithm {
    
    private final HierarchicalClusterer clusterer;
    private final HashMap<Integer, Byte> labelIndices = new HashMap<>();
    private final gpsiSampler sampler;

    public gpsiHierarchicalClusteringClassificationAlgorithm(String linkType, gpsiSampler sampler) {
        this.clusterer = new HierarchicalClusterer();
        if(sampler != null)
            this.sampler = sampler;
        else
            this.sampler = new gpsiWholeSampler();
        try {
            this.clusterer.setOptions(new String[]{"-L", linkType});
        } catch (Exception ex) {
            Logger.getLogger(gpsiHClustScore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void fit(HashMap<Byte, ArrayList<double[]>> x) {
        
        int i = 0, j;
        
        this.clusterer.setNumClusters(x.size());
        this.nClasses = x.size();
        
        for(byte label : x.keySet())
            labelIndices.put(i++, label);
        
        for(byte label : x.keySet()){
            this.dimensionality = x.get(label).get(0).length;
            break;
        }

        double[][][] x_ = sampler.sample(x, x.keySet().toArray(new Byte[0]));
        
        FastVector attributes = new FastVector();
        for (i = 0; i < dimensionality; i++)
            attributes.addElement(new Attribute("x" + i));

        Instances data = new Instances("Clust", attributes, 0);

        for(i = 0; i < x_.length; i++)
            for (j = 0; j < x_[i].length; j++)
                data.add(new Instance(1.0, x_[i][j]));

        try {
            clusterer.buildClusterer(data);
        } catch (Exception ex) {
            Logger.getLogger(gpsiHClustScore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    public byte predict(double[] x) {
        
        int prediction = -1;
        
        try {
            prediction = clusterer.clusterInstance(new Instance(1.0, x));
        } catch (Exception ex) {
            Logger.getLogger(gpsiHierarchicalClusteringClassificationAlgorithm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return this.labelIndices.get(prediction);
                
    }

}
