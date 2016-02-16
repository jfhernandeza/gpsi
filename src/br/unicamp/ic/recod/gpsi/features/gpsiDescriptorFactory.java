/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.features;

import java.util.Properties;

/**
 *
 * @author juan
 */
public class gpsiDescriptorFactory {
    
    private gpsiDescriptorFactory() {
    }
    
    public gpsiDescriptor create(Properties prop){
        
        String className = prop.getProperty("descriptorName");
        
        switch(className){
            case "gpsiMaskedLocalBinaryPatternDescriptor":
                gpsiMaskedLocalBinaryPatternDescriptor descriptor = new gpsiMaskedLocalBinaryPatternDescriptor();
                descriptor.setNeighborhood(Integer.parseInt(prop.getProperty("neighborhood")));
                return descriptor;
        }
        
        return null;
        
    }
    
    public static gpsiDescriptorFactory getInstance() {
        return gpsiDescriptorFactoryHolder.INSTANCE;
    }
    
    private static class gpsiDescriptorFactoryHolder {

        private static final gpsiDescriptorFactory INSTANCE = new gpsiDescriptorFactory();
    }
}
