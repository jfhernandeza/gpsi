/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.measures;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author juan
 */
public class gpsiHClustScore extends gpsiSampleSeparationScore {

    private final String linkType;

    public gpsiHClustScore(String linkType) throws Exception {
        this.linkType = linkType;
        this.optimum = 1.0;
    }

    @Override
    public double score(double[][][] input) {

        int dimensionality = input[0][0].length, i, j;

        HierarchicalClusterer clusterer = new HierarchicalClusterer();
        try {
            clusterer.setOptions(new String[]{"-L", linkType});
        } catch (Exception ex) {
            Logger.getLogger(gpsiHClustScore.class.getName()).log(Level.SEVERE, null, ex);
        }
        clusterer.setNumClusters(input.length);

        FastVector attributes = new FastVector();
        for (i = 0; i < dimensionality; i++) {
            attributes.addElement(new Attribute("x" + i));
        }

        Instances data = new Instances("Clust", attributes, 0);

        for (i = 0; i < input.length; i++) {
            for (j = 0; j < input[i].length; j++) {
                data.add(new Instance(1.0, input[i][j]));
            }
        }

        try {
            clusterer.buildClusterer(data);
        } catch (Exception ex) {
            Logger.getLogger(gpsiHClustScore.class.getName()).log(Level.SEVERE, null, ex);
        }

        int[][] cm = new int[input.length][input.length];
        for(int line[] : cm)
            Arrays.fill(line, 0);
        
        try {
            for (i = 0; i < input.length; i++) {
                for (j = 0; j < input[i].length; j++) {
                    cm[i][clusterer.clusterInstance(new Instance(1.0, input[i][j]))]++;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(gpsiHClustScore.class.getName()).log(Level.SEVERE, null, ex);
        }

        double nAcc0 = ((double) cm[0][0] / (cm[0][0] + cm[0][1]) + (double) cm[1][1] / (cm[1][0] + cm[1][1])) / 2.0;
        double nAcc1 = ((double) cm[0][1] / (cm[0][0] + cm[0][1]) + (double) cm[1][0] / (cm[1][0] + cm[1][1])) / 2.0;
        
        //double nAcc0 = (double) (cm[0][0] + cm[1][1]) / (cm[0][0] + cm[0][1] + cm[1][0] + cm[1][1]);
        //double nAcc1 = (double) (cm[0][1] + cm[1][0]) / (cm[0][0] + cm[0][1] + cm[1][0] + cm[1][1]);
        
        return Math.max(nAcc0, nAcc1);
    }

    public String getLinkType() {
        return linkType;
    }
    
}
