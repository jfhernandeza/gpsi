/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.img;

import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author juan
 */
public class gpsiJGAPImageCombinator extends gpsiImageCombinator<Variable[], IGPProgram> {
    
    private gpsiJGAPImageCombinator() {
    }
    
    public static gpsiJGAPImageCombinator getInstance() {
        return gpsiJGAPImageCombinatorHolder.INSTANCE;
    }

    @Override
    public gpsiCombinedImage combineImage(gpsiHyperspectralImage img, Variable[] b, IGPProgram individual) {
        
        double[][] combinedImage = new double[img.getHeight()][img.getWidth()];
        
        for(int y = 0; y < img.getHeight(); y++){
            for(int x = 0; x < img.getWidth(); x++){
                for(int i = 0; i < img.getN_bands(); i++){
                    b[i].set(img.img[y][x][i]);
                }
                combinedImage[y][x] = individual.execute_double(0, new Object[0]);
            }
        }
        
        return new gpsiCombinedImage(combinedImage);
        
    }
    
    private static class gpsiJGAPImageCombinatorHolder {
        private static final gpsiJGAPImageCombinator INSTANCE = new gpsiJGAPImageCombinator();
    }
}
