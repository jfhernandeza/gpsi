/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.io.element;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author juan
 */
public class gpsiIOStream {
    
    private final LinkedBlockingQueue<gpsiIOElement> stream;
    private String root;

    public gpsiIOStream() {
        this.stream = new LinkedBlockingQueue<>();
    }
    
    public void register(gpsiIOElement element){
        stream.add(element);
    }
    
    public void flush() throws FileNotFoundException{
        gpsiIOElement element;
        File file;
        while(!stream.isEmpty()){
            element = stream.poll();
            file = new File(root + element.path);
            if(!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            element.setPath(root + element.path);
            element.write();
        }
    }

    public void setRoot(String root) {
        this.root = root;
    }
    
}
