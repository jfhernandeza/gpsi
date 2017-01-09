/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.data;

/**
 *
 * @author juan
 * @param <E>
 */
public class gpsiRawDataset<E> extends gpsiDataset {
    
    private final int imgCols, imgRows;

    public gpsiRawDataset(int imgRows, int imgCols) {
        this.imgCols = imgCols;
        this.imgRows = imgRows;
    }

    public int getImgCols() {
        return imgCols;
    }

    public int getImgRows() {
        return imgRows;
    }
    
}
