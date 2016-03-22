/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.conf;

import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptorFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author juan
 */
public class gpsiConfiguration {
    
    public final String imgPath;
    public final String trainingMasksPath;
    public final String testMasksPath;
    
    public final int popSize;
    public final int numGenerations;
    public final int maxInitDepth;
    
    public final String[] classLabels;
    
    public final gpsiDescriptor descriptor;
    
    public gpsiConfiguration(String[] confCode) throws IOException {
        
        Properties propConfGP = new Properties();
        Properties propDataSet = new Properties();
        Properties propConfDescriptor = new Properties();
        Properties propClasses = new Properties();
        
        propConfGP.load(new FileInputStream("conf/gp/" + confCode[0] + ".properties"));
        propDataSet.load(new FileInputStream("conf/datasets/" + confCode[1] + ".properties"));
        propConfDescriptor.load(new FileInputStream("conf/descriptors/" + confCode[2] + ".properties"));
        propClasses.load(new FileInputStream("conf/classes/" + confCode[3] + ".properties"));
        
        this.imgPath = propDataSet.getProperty("img_path");
        this.trainingMasksPath = propDataSet.getProperty("tr_masks_path");
        this.testMasksPath = propDataSet.getProperty("ts_masks_path");
        
        this.numGenerations = Integer.parseInt(propConfGP.getProperty("num_generations"));
        this.popSize = Integer.parseInt(propConfGP.getProperty("pop_size"));
        this.maxInitDepth = Integer.parseInt(propConfGP.getProperty("max_init_depth"));

        int n_classes = Integer.parseInt(propClasses.getProperty("n_classes"));
        this.classLabels = new String[n_classes];
        
        for(int i = 0; i < n_classes; i++)
            this.classLabels[i] = propClasses.getProperty("c" + Integer.toString(i));
        
        this.descriptor = gpsiDescriptorFactory.getInstance().create(propConfDescriptor);
        
    }
    
}
