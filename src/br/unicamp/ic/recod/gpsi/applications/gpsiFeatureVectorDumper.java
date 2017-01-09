/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.applications;

import br.unicamp.ic.recod.gpsi.combine.gpsiStringParserVoxelCombiner;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.img.gpsiEntity;
import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import br.unicamp.ic.recod.gpsi.io.element.gpsiDoubleCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.element.gpsiIntegerCsvIOElement;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author juan
 */
public class gpsiFeatureVectorDumper extends gpsiApplication {

    private final File[][] files;

    public gpsiFeatureVectorDumper(String datasetPath,
            gpsiDatasetReader datasetReader, Byte[] classLabels,
            String outputPath, String programsPath, double errorScore, long seed) throws Exception {
        super(datasetPath, datasetReader, classLabels, outputPath, errorScore, seed);

        File[] foldFolders = new File(programsPath).listFiles();

        files = new File[foldFolders.length][];

        for (File dir : foldFolders) {
            files[Integer.parseInt(dir.getName()) - 1] = dir.listFiles((File dir1, String name) -> name.toLowerCase().endsWith(".program"));
        }

    }

    @Override
    public void run() throws Exception {

        gpsiScalarSpectralIndexDescriptor descs[];
        BufferedReader reader;
        HashMap<Byte, ArrayList<gpsiEntity>> fold;

        double data[][][];

        this.stream.setRoot("results/" + this.outputPath + "/");

        /*for (int p = 0; p < files.length; p++) {

            descs = new gpsiScalarSpectralIndexDescriptor[files[p].length];
            for (int j = 0; j < files[p].length; j++) {
                reader = new BufferedReader(new FileReader(files[p][j]));
                descs[j] = new gpsiScalarSpectralIndexDescriptor(new gpsiStringParserVoxelCombiner(null, reader.readLine()));
            }

            for (int f = 0; f < files.length; f++) {

                fold = (HashMap<Byte, ArrayList<gpsiEntity>>) this.rawDataset.getFolds().get(f);
                data = new double[fold.size()][][];

                for (int i = 0; i < descs.length; i++) {

                    for (byte label : fold.keySet()) {
                        if (data[label - 1] == null) {
                            data[label - 1] = new double[fold.get(label).size()][files[f].length];
                        }
                        for (int j = 0; j < fold.get(label).size(); j++) {
                            data[label - 1][j][i] = descs[i].getFeatureVector(fold.get(label).get(j))[0];
                        }
                    }

                }

                for (int i = 0; i < data.length; i++) {
                    this.stream.register(new gpsiDoubleCsvIOElement(data[i], null, "p" + (p + 1) + "/" + (f + 1) + "/" + (i + 1) + ".csv"));
                }

                this.stream.flush();

            }

        }*/

        int coord[][][][];

        coord = new int[files.length][][][];
        for (int f = 0; f < files.length; f++) {
            fold = (HashMap<Byte, ArrayList<gpsiEntity>>) this.rawDataset.getFolds().get(f);
            coord[f] = new int[fold.size()][][];
            for (byte label : fold.keySet()) {
                coord[f][label - 1] = new int[fold.get(label).size()][];
                for (int j = 0; j < fold.get(label).size(); j++) {
                    coord[f][label - 1][j] = new int[2];
                    coord[f][label - 1][j][0] = ((gpsiVoxel) fold.get(label).get(j)).getRow();
                    coord[f][label - 1][j][1] = ((gpsiVoxel) fold.get(label).get(j)).getCol();
                }
                this.stream.register(new gpsiIntegerCsvIOElement(coord[f][label - 1], null, "coordinates/" + (f + 1) + "/" + label + ".csv"));
            }
        }

    }

}
