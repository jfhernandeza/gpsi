/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.features;

import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiMask;
import java.util.Arrays;

/**
 *
 * @author juan
 */
public class gpsiMaskedLocalBinaryPatternDescriptor implements gpsiLocalDescriptor{

    @Override
    public gpsiFeatureVector getFeatureVector(gpsiCombinedImage combinedImage, gpsiMask roi) {
        
        double[] vector = new double[256];
        int binaryPattern;
        Arrays.fill(vector, 0.0);
        
        double[][] img = combinedImage.getImg();
        boolean[][] mask = roi.getMask();
        
        boolean consider;
        
        int h, k;
        for(int y = 1; y < combinedImage.getHeight() - 1; y++){
            for(int x = 1; x < combinedImage.getWidth() - 1; x++){
                consider = true;
                for(h = -1; h <= 1; h++)
                    for(k = -1; k <= 1; k++)
                        consider &= mask[y + h][x + k];
                if( consider ){
                    binaryPattern = 0;
                    binaryPattern += (img[y][x] > img[y - 1][  x  ]) ? 1 : 0;
                    binaryPattern += (img[y][x] > img[y - 1][x + 1]) ? 2 : 0;
                    binaryPattern += (img[y][x] > img[  y  ][x + 1]) ? 4 : 0;
                    binaryPattern += (img[y][x] > img[y + 1][x + 1]) ? 8 : 0;
                    binaryPattern += (img[y][x] > img[y + 1][  x  ]) ? 16 : 0;
                    binaryPattern += (img[y][x] > img[y + 1][x - 1]) ? 32 : 0;
                    binaryPattern += (img[y][x] > img[  y  ][x - 1]) ? 64 : 0;
                    binaryPattern += (img[y][x] > img[y - 1][x - 1]) ? 128 : 0;
                    
                    vector[binaryPattern]++;
                    
                }
            }
        }
        
        double norm = 0.0;
        for(h = 0; h < vector.length; h++)
            norm += Math.pow(vector[h], 2);
        
        norm = Math.sqrt(norm);
        
        for(h = 0; h < vector.length; h++)
            vector[h] /= norm;
        
        return new gpsiFeatureVector(vector);
    }
    
}
