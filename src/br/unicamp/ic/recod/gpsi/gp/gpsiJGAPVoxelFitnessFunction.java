/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import br.unicamp.ic.recod.gpsi.data.gpsiSampler;
import br.unicamp.ic.recod.gpsi.data.gpsiVoxelRawDataset;
import br.unicamp.ic.recod.gpsi.combine.gpsiJGAPVoxelCombiner;
import br.unicamp.ic.recod.gpsi.data.gpsiMLDataset;
import br.unicamp.ic.recod.gpsi.features.gpsiScalarSpectralIndexDescriptor;
import br.unicamp.ic.recod.gpsi.measures.gpsiSampleSeparationScore;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        gpsiScalarSpectralIndexDescriptor descriptor = new gpsiScalarSpectralIndexDescriptor(new gpsiJGAPVoxelCombiner(super.b, igpp));
        gpsiMLDataset mlDataset = new gpsiMLDataset(descriptor);
        try {
            mlDataset.loadWholeDataset(super.dataset, true);
        } catch (Exception ex) {
            Logger.getLogger(gpsiJGAPVoxelFitnessFunction.class.getName()).log(Level.SEVERE, null, ex);
        }
        double[][][] samples = this.sampler.sample(mlDataset.getTrainingEntities(), classLabels);
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
