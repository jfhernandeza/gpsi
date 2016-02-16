/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io;

import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiHyperspectralImage;
import java.io.IOException;

/**
 *
 * @author jfhernandeza
 * @param <R>
 * @param <D>
 */
public abstract class gpsiDatasetReader <R extends gpsiFileReader, D extends gpsiRawDataset, E> {
    
    protected final R fileReader;

    public gpsiDatasetReader(R fileReader) {
        this.fileReader = fileReader;
    }
    
    protected void loadHiperspectralImage(D rawDataset, String path) throws IOException{
        double[][][] hiperspectralImage = this.fileReader.read3dStructure(path);
        rawDataset.setHyperspectralImage(new gpsiHyperspectralImage(hiperspectralImage));
    }
    
    public abstract D readDataset(String HyperspectralImagePath, String masksPath ) throws Exception;
    
}
