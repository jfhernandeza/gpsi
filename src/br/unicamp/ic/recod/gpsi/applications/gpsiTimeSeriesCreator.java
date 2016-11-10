/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.combine.gpsiStringParserVoxelCombiner;
import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.data.gpsiRawDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.io.element.gpsiDoubleCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.VectorialMean;

/**
 *
 * @author juan
 */
public class gpsiTimeSeriesCreator extends gpsiApplication {

    private final gpsiScalarSpectralIndexDescriptor[] descriptors;
    private final String names[];
    
    public gpsiTimeSeriesCreator(String datasetPath,
            gpsiDatasetReader datasetReader, Byte[] classLabels,
            String outputPath, String programsPath, double errorScore,
            long seed) throws Exception {
        super(datasetPath, datasetReader, classLabels, outputPath, errorScore, seed);
        
        int i, j;
        File dir = new File(programsPath);
        
        BufferedReader reader;
        File[] files = dir.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".program"));
        
        descriptors = new gpsiScalarSpectralIndexDescriptor[files.length];
        names = new String[files.length];
        

        for(i = 0; i < names.length; i++){
            reader = new BufferedReader(new FileReader(files[i]));
            names[i] = files[i].getName().replace(".program", "");
            descriptors[i] = new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine()));
            reader.close();
        }
    }

    @Override
    public void run() throws Exception {
        String datasetsPath = Paths.get(super.datasetPath).getParent().getParent().toString();
        System.out.println(datasetsPath);
        
        HashMap<Byte, double[][]> indicesMap = new HashMap<>();
        ArrayList<double[]> entities;
        double[][] series;
        
        int daysCount = 0, i, dayIndex;
        
        gpsiRawDataset rawDataset;
        gpsiMLDataset dataset;
        
        VectorialMean vmean;
        Mean mean = new Mean();
        
        File[] days, years = new File(datasetsPath).listFiles(File::isDirectory);
        Arrays.sort(years);
        
        for(File year : years)
            daysCount += 365 + (Integer.parseInt(year.getName()) % 4 == 0 ? 1 : 0);
        
        for(Byte label : classLabels){
            series = new double[daysCount][descriptors.length];
            for(double[] row : series)
                Arrays.fill(row, Double.NaN);
            indicesMap.put(label, series);
        }
        
        
        
        daysCount = 0;
        for(File year : years){
            days = new File(year.getAbsolutePath()).listFiles(File::isDirectory);
            Arrays.sort(days);
            for(File day : days){
                dayIndex = daysCount + Integer.parseInt(day.getName()) - 1;
                rawDataset = datasetReader.readDataset(day.getAbsolutePath() + "/", null, 0.0);
                rawDataset.assignFolds(new byte[] {0}, null, null);
                for(i = 0; i < descriptors.length; i++){
                    dataset = new gpsiMLDataset(descriptors[i]);
                    dataset.loadTrainingSet(rawDataset.getTrainingEntities(), true);
                    for(Byte label : classLabels){
                        
                        if(!dataset.getTrainingEntities().containsKey(label)){
                            indicesMap.get(label)[dayIndex][i] = Double.NaN;
                            continue;
                        }
                        vmean = new VectorialMean(dataset.getDimensionality());
                        entities = (ArrayList<double[]>) dataset.getTrainingEntities().get(label);
                        for(double[] v : entities)
                            vmean.increment(v);
                        indicesMap.get(label)[dayIndex][i] = mean.evaluate(vmean.getResult());
                    }
                }
            }
            daysCount += 365 + (Integer.parseInt(year.getName()) % 4 == 0 ? 1 : 0);
        }
        
        for(Byte label : classLabels)
            stream.register(new gpsiDoubleCsvIOElement(indicesMap.get(label), names, "series/" + label + ".csv"));
        
    }
    
}
