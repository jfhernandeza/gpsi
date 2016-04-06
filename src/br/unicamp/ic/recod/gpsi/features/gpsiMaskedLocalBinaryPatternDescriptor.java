/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.features;

import br.unicamp.ic.recod.gpsi.img.gpsiRoi;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author juan
 */
public class gpsiMaskedLocalBinaryPatternDescriptor implements gpsiLocalDescriptor{
    
    private ArrayList<int[]> neighborhood;

    public gpsiMaskedLocalBinaryPatternDescriptor() {
        predefinedNeighborhood(8);
    }
    
    public void setNeighborhood(int type){
        predefinedNeighborhood(type);
    }
    
    public void setNeighborhood(ArrayList<int[]> neighborhood){
        this.neighborhood = neighborhood;
    }
    
    private void predefinedNeighborhood(int type){
        
        type = type != 4 && type != 8 ? 8 : type;
        
        this.neighborhood = new ArrayList<>();
        this.neighborhood.add(new int[] {-1, 0});
        if(type == 8) this.neighborhood.add(new int[] {-1, 1});
        this.neighborhood.add(new int[] { 0, 1});
        if(type == 8) this.neighborhood.add(new int[] { 1, 1});
        this.neighborhood.add(new int[] { 1, 0});
        if(type == 8) this.neighborhood.add(new int[] { 1,-1});
        this.neighborhood.add(new int[] { 0,-1});
        if(type == 8) this.neighborhood.add(new int[] {-1,-1});
    }
    
    @Override
    public gpsiFeatureVector getFeatureVector(gpsiRoi roi) {
        
        double[] vector = new double[(int) Math.pow(2, this.neighborhood.size())];
        int binaryPattern;
        Arrays.fill(vector, 0.0);
        
        gpsiVoxel[][] img = roi.getRoi();
        
        boolean consider;
        
        int h, k;
        for(int y = 1; y < img.length - 1; y++){
            for(int x = 1; x < img[0].length - 1; x++){
                
                if(img[y][x] == null) continue;
                
                consider = true;
                for(int[] n : this.neighborhood)
                    consider &= img[y + n[0]][x + n[1]] != null;
                if( !consider ) continue;
                
                binaryPattern = 0;
                
                int pow = 0;
                for(int[] n : this.neighborhood){
                    binaryPattern += (img[y][x].getCombinedValue() > img[y + n[0]][x + n[1]].getCombinedValue()) ? Math.pow(2, pow) : 0;
                    pow++;
                }
                
                vector[binaryPattern]++;
                    
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
