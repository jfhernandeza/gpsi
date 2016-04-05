/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.features.gpsiDescriptor;
import br.unicamp.ic.recod.gpsi.features.gpsiDescriptorFactory;
import br.unicamp.ic.recod.gpsi.io.gpsiDatasetReader;
import java.util.HashMap;
import org.kohsuke.args4j.Option;

/**
 *
 * @author juan
 */
public abstract class gpsiRoiClassifierEvolver<I> extends gpsiEvolver<I>{
    
    @Option(name="-desc0", usage="Parameter 0 of descriptor")
    protected String desc0;
    
    @Option(name="-desc1", usage="Parameter 1 of descriptor")
    protected String desc1;
    
    protected gpsiDescriptor descriptor;
    
    public gpsiRoiClassifierEvolver(String[] args, gpsiDatasetReader datasetReader) throws Exception {
        super(args, datasetReader);
        HashMap<String, String> props = new HashMap<>();
        props.put("desc0", desc0);
        props.put("desc1", desc1);
        this.descriptor = gpsiDescriptorFactory.getInstance().create(props);
    }
    
}
