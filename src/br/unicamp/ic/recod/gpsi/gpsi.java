/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi;

import br.unicamp.ic.recod.gpsi.applications.gpsiApplication;
import br.unicamp.ic.recod.gpsi.applications.gpsiApplicationFactory;
import org.kohsuke.args4j.CmdLineParser;

/**
 *
 * @author jfhernandeza
 */
public class gpsi {
    
    // TODO: Generalize the conversion from raw dataset to MLdataset
    // TODO: Create arguments for the activities (separate, learn and classify, classify)
    // TODO: Create a result printer registry
    // TODO: Separate the genome creator in JGAP-based evolvers/classifiers
    // TODO: Generalize measures to support high dimensionalities
    // TODO: Make the best individual persist
    
    public static void main(String[] args) throws Exception {
        
        gpsiApplicationFactory factory = new gpsiApplicationFactory();
        
        CmdLineParser parser = new CmdLineParser(factory);
        parser.parseArgument(args);
        
        gpsiApplication app = factory.create();
        app.getRawDataset().assignFolds(new byte[] {4, 0, 1}, null, new byte[] {3});
        app.run();
        
        System.exit(0);
        
        /*gpsiJGAPVoxelSeparatorEvolver evolver = new gpsiJGAPVoxelSeparatorEvolver(args, new gpsiVoxelDatasetReader(new gpsiMatlabFileReader()));
        evolver.getDataset().assignFolds(new int[]{0}, null, null);
        evolver.evolve();
        evolver.printResults();*/
        
        /*
        gpsiJGAPVoxelClassifierEvolver evolver = new gpsiJGAPVoxelClassifierEvolver(args, new gpsiVoxelDatasetReader(new gpsiMatlabFileReader()));
        int nFolds = evolver.getDataset().getnFolds(), i;
        
        boolean dumpGens = evolver.isDumpGens();
        
        for(i = 0; i < 5; i++){
            System.out.println("\nRun " + (i + 1) + "\n");
            evolver.getDataset().assignFolds(new int[]{i, (i + 1) % nFolds, (i + 2) % nFolds}, new int[]{(i + 3) % nFolds}, new int[]{(i + 4) % nFolds});
            evolver.evolve();
            evolver.setDumpGens(false);
        }
        evolver.setDumpGens(dumpGens);
        evolver.printResults();
        System.exit(0);
        */
        
    }

}
