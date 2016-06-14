/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.genotype;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.MathCommand;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;

/**
 *
 * @author juan
 */
public class gpsiJGAPProtectedNaturalLogarithm extends MathCommand implements ICloneable {

    private final static String CVS_REVISION = "$Revision: 1.3 $";

    public gpsiJGAPProtectedNaturalLogarithm(final GPConfiguration a_conf, Class a_returnType)
            throws InvalidConfigurationException {
        super(a_conf, 1, a_returnType);
    }

    @Override
    public String toString() {
        return "rlog(&1)";
    }

    /**
     * @return textual name of this command
     *
     * @author Klaus Meffert
     * @since 3.3.4
     */
    @Override
    public String getName() {
        return "Protected Log";
    }

    @Override
    public float execute_float(ProgramChromosome c, int n, Object[] args) {
        float f = c.execute_float(n, 0, args);
        return f == 0.0 ? 0.0f : (float) Math.log(Math.abs(f));
    }

    @Override
    public double execute_double(ProgramChromosome c, int n, Object[] args) {
        double d = c.execute_double(n, 0, args);
        return d == 0.0 ? 0.0 : Math.log(Math.abs(d));
    }

    @Override
    public Object execute_object(ProgramChromosome c, int n, Object[] args) {
        return ((Compatible) c.execute_object(n, 0, args)).execute_rlog();
    }

    protected interface Compatible {

        public Object execute_rlog();
    }

    /**
     * Clones the object. Simple and straight forward implementation here.
     *
     * @return cloned instance of this object
     *
     * @author Klaus Meffert
     * @since 3.4
     */
    @Override
    public Object clone() {
        try {
            gpsiJGAPProtectedNaturalLogarithm result = new gpsiJGAPProtectedNaturalLogarithm(getGPConfiguration(), getReturnType());
            return result;
        } catch (Exception ex) {
            throw new CloneException(ex);
        }
    }

}
