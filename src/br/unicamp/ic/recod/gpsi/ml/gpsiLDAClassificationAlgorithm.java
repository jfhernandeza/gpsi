/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.ml;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.VectorialMean;

/**
 *
 * @author juan
 */
public class gpsiLDAClassificationAlgorithm extends gpsiClassificationAlgorithm {

    
    @Override
    public void fit(HashMap<Byte, ArrayList<double[]>> x) {
        
        int i;
        double[] centroid;
        RealMatrix entities, mu, Si, Sw, m, Sb, dif, mi;
        Mean mean = new Mean();
        
        HashMap<Byte, RealMatrix> M, D, S;
        
        for(byte label : x.keySet()){
            this.dimensionality = x.get(label).get(0).length;
            break;
        }
        
        VectorialMean vMean = new VectorialMean(dimensionality);
        
        M = new HashMap<>();
        D = new HashMap<>();
        S = new HashMap<>();
        Sw = MatrixUtils.createRealMatrix(dimensionality, dimensionality);
        this.nClasses = x.size();
        for(byte label : x.keySet()){
            centroid = new double[dimensionality];
            entities = MatrixUtils.createRealMatrix(x.get(label).toArray(new double[0][])).transpose();
            
            for(i = 0; i < entities.getColumnDimension(); i++)
                vMean.increment(entities.getColumn(i));
                
            for(i = 0; i < dimensionality; i++)
                centroid[i] = mean.evaluate(entities.getRow(i));
            mu = MatrixUtils.createColumnRealMatrix(centroid);
            M.put(label, mu);
            D.put(label, entities);
            
            Si = MatrixUtils.createRealMatrix(dimensionality, dimensionality);
            for(i = 0; i < entities.getColumnDimension(); i++){
                dif = entities.getColumnMatrix(i).subtract(mu);
                Si = Si.add(dif.multiply(dif.transpose()));
            }
            
            S.put(label, Si);
            Sw = Sw.add(Si);
            
        }
        
        Sb = MatrixUtils.createRealMatrix(dimensionality, dimensionality);
        m = MatrixUtils.createColumnRealMatrix(vMean.getResult());
        
        for(byte l : M.keySet()){
            mi = M.get(l);
            dif = mi.subtract(m);
            Sb = Sb.add(dif.multiply(dif.transpose()).scalarMultiply(x.get(l).size()));
        }
        
        EigenDecomposition e = new EigenDecomposition(MatrixUtils.inverse(Sw).multiply(Sb));
        
    }

    @Override
    public byte predict(double[] x) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
