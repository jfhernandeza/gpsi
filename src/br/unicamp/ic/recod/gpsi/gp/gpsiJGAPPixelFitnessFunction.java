/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.img.gpsiCombinedImage;
import br.unicamp.ic.recod.gpsi.img.gpsiJGAPImageCombinator;
import br.unicamp.ic.recod.gpsi.measures.gpsiSampleSeparationScore;
import java.util.ArrayList;
import org.jgap.gp.IGPProgram;

/**
 *
 * @author juan
 */
public class gpsiJGAPPixelFitnessFunction extends gpsiJGAPFitnessFunction<gpsiVoxelRawDataset> {

    private final String[] classLabels;
    private final gpsiSampleSeparationScore score;

    public gpsiJGAPPixelFitnessFunction(gpsiVoxelRawDataset dataset, String[] classLabels, gpsiSampleSeparationScore score) {
        super(dataset);
        this.classLabels = classLabels;
        this.score = score;
    }
    
    @Override
    protected double evaluate(IGPProgram igpp) {
        
        gpsiCombinedImage combinedImage = gpsiJGAPImageCombinator.getInstance().combineImage(this.dataset.getHyperspectralImage(), super.b, igpp);
        
        ArrayList<double[]> samples = new ArrayList<>();
        samples.add(gpsiSampler.getInstance().sample(super.dataset.getIndexesPerClass(), super.dataset.getTrainingEntities(), this.classLabels[0], combinedImage));
        samples.add(gpsiSampler.getInstance().sample(super.dataset.getIndexesPerClass(), super.dataset.getTestEntities(), this.classLabels[1], combinedImage));
        
        //MannWhitneyUTest t = new MannWhitneyUTest();        
        //double p_value = t.mannWhitneyUTest(samples.get(0), samples.get(1));
        
        return this.score.score(samples);
        
    }

    public String[] getClassLabels() {
        return classLabels;
    }

    public gpsiSampleSeparationScore getScore() {
        return score;
    }
    
}
