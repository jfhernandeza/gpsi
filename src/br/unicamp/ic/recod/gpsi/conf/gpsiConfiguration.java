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
    public final String masksPath;
    
    public final int popSize;
    public final int numGenerations;
    public final int maxInitDepth;
    
    public final gpsiDescriptor descriptor;
    
    public gpsiConfiguration(String gpConfCode, String dataSetCode, String descriptorConfCode) throws IOException {
        
        Properties propConfGP = new Properties();
        Properties propDataSet = new Properties();
        Properties propConfDescriptor = new Properties();
        
        propConfGP.load(new FileInputStream("conf/gp/" + gpConfCode + ".properties"));
        propDataSet.load(new FileInputStream("conf/datasets/" + dataSetCode + ".properties"));
        propConfDescriptor.load(new FileInputStream("conf/descriptors/" + descriptorConfCode + ".properties"));
        
        this.imgPath = propDataSet.getProperty("img_path");
        this.masksPath = propDataSet.getProperty("masks_path");
        
        this.numGenerations = Integer.parseInt(propConfGP.getProperty("num_generations"));
        this.popSize = Integer.parseInt(propConfGP.getProperty("pop_size"));
        this.maxInitDepth = Integer.parseInt(propConfGP.getProperty("max_init_depth"));
                
        this.descriptor = gpsiDescriptorFactory.getInstance().create(propConfDescriptor);
        
    }
    
}
