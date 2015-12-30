/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiMask;
import java.util.ArrayList;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

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
                mean_accuracy += combined_image[y][x];
            }
        }
        combinedImage = new gpsiCombinedImage(combined_image);
        
        gpsiMLDataset mlDataset = new gpsiMLDataset(this.descriptor);
        mlDataset.loadDataset(this.dataset, combinedImage);
        
        return Math.abs(mean_accuracy);
        
    }

    public void setB(Variable[] b) {
        this.b = b;
    }
    
}
