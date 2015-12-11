/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import com.jmatio.io.MatFileReader;
import com.jmatio.types.MLNumericArray;
import java.io.IOException;
import java.util.Map;

/**
 *
 * @author jfhernandeza
 */
public class gpsiMatlabFileReader extends gpsiFileReader {
    
    MatFileReader reader;

    private MLNumericArray read(String path) throws IOException{
        this.reader = new MatFileReader(path);
        Map elements = this.reader.getContent();
        for(Object element : elements.keySet()){
            return (MLNumericArray) this.reader.getMLArray((String) element);
        }
        return null;
    }
    
    @Override
    public double[][] read2dStructure(String path) throws IOException {
        
        MLNumericArray element = read(path);
        
        int dimensions[] = element.getDimensions();
        if(dimensions.length != 2)
            throw new IOException("No 2D object.");
        
        double data[][] = new double[dimensions[0]][dimensions[1]];
        
        for(int x = 0; x < dimensions[1]; x++)
            for(int y = 0; y < dimensions[0]; y++)
                data[y][x] = element.get(y + x * dimensions[0]).doubleValue();
        
        return data;
        
    }

    @Override
    public double[][][] read3dStructure(String path) throws IOException {
        
        MLNumericArray element = read(path);
        
        int dimensions[] = element.getDimensions();
        if(dimensions.length != 3)
            throw new IOException("No 3D object.");
        
        double data[][][] = new double[dimensions[0]][dimensions[1]][dimensions[2]];
        
        for(int z = 0; z < dimensions[2]; z++)
            for(int x = 0; x < dimensions[1]; x++)
                for(int y = 0; y < dimensions[0]; y++)
                    data[y][x][z] = element.get(y + x * dimensions[0] + z * dimensions[0] * dimensions[1]).doubleValue();
        
        return data;
        
    }
    
}
