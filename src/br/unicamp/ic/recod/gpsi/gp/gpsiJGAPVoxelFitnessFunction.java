/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.combine.gpsiJGAPVoxelCombinator;
import br.unicamp.ic.recod.gpsi.combine.gpsiVoxelBandCombinator;
import br.unicamp.ic.recod.gpsi.measures.gpsiSampleSeparationScore;
import java.util.ArrayList;
import org.jgap.gp.IGPProgram;

/**
 *
 * @author juan
 */
public class gpsiJGAPVoxelFitnessFunction extends gpsiJGAPFitnessFunction<gpsiVoxelRawDataset> {

    private final Byte[] classLabels;
    protected final gpsiSampler sampler;
    private final gpsiSampleSeparationScore score;

    public gpsiJGAPVoxelFitnessFunction(gpsiVoxelRawDataset dataset, Byte[] classLabels, gpsiSampleSeparationScore score, gpsiSampler sampler) {
        super(dataset);
        this.sampler = sampler;
        this.classLabels = classLabels;
        this.score = score;
    }
    
    @Override
    protected double evaluate(IGPProgram igpp) {
        
        gpsiVoxelBandCombinator voxelBandCombinator = new gpsiVoxelBandCombinator(new gpsiJGAPVoxelCombinator(super.b, igpp));
        voxelBandCombinator.combineEntity(super.dataset.getTrainingEntities());
        
        ArrayList<double[]> samples = new ArrayList<>();
        
        for(Byte classLabel : this.classLabels)
            samples.add(this.sampler.sample(super.dataset.getTrainingEntities(), classLabel));
            
        return this.score.score(samples) + 1.0;
        
    }

    public Byte[] getClassLabels() {
        return classLabels;
    }

    public gpsiSampleSeparationScore getScore() {
        return score;
    }

    public gpsiSampler getSampler() {
        return sampler;
    }
}
