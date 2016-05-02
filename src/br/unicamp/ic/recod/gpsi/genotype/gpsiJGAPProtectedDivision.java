/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.genotype;

import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.IMutateable;
import org.jgap.gp.MathCommand;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.ProgramChromosome;
import org.jgap.util.CloneException;
import org.jgap.util.ICloneable;

/**
 *
 * @author juan
 */
public class gpsiJGAPProtectedDivision extends MathCommand implements IMutateable, ICloneable {

    private static final String CVS_REVISION = "$Revision: 1.10 $";

    public gpsiJGAPProtectedDivision(final GPConfiguration a_conf, Class a_returnType)
            throws InvalidConfigurationException {
        super(a_conf, 2, a_returnType);
    }

    public CommandGene applyMutation(int index, double a_percentage)
            throws InvalidConfigurationException {
        Multiply mutant = new Multiply(getGPConfiguration(), getReturnType());
        return mutant;
    }

    public Object clone() {
        try {
            gpsiJGAPProtectedDivision result = new gpsiJGAPProtectedDivision(getGPConfiguration(), getReturnType());
            return result;
        } catch (Exception ex) {
            throw new CloneException(ex);
        }
    }

    public String toString() {
        return "%";
    }

    /**
     * @return textual name of this command
     *
     * @author Klaus Meffert
     * @since 3.2
     */
    public String getName() {
        return "Protected Divide";
    }

    public int execute_int(ProgramChromosome c, int n, Object[] args) {
        int c1 = c.execute_int(n, 0, args);
        if (c1 == 0) {
            return 0;
        }
        int c2 = c.execute_int(n, 1, args);
        if (c2 == 0) {
            throw new IllegalStateException("Division by zero");
        }
        return c1 / c2;
    }

    public long execute_long(ProgramChromosome c, int n, Object[] args) {
        long divisor = c.execute_long(n, 1, args);
        if(divisor == 0.0)
            return 1;
        return c.execute_long(n, 0, args) / divisor;
    }

    public float execute_float(ProgramChromosome c, int n, Object[] args) {
        float divisor = c.execute_float(n, 1, args);
        if(divisor == 0.0)
            return 1.0f;
        return c.execute_float(n, 0, args) / divisor;
    }

    public double execute_double(ProgramChromosome c, int n, Object[] args) {
        double divisor = c.execute_double(n, 1, args);
        if(divisor == 0.0)
            return 1.0;
        return c.execute_double(n, 0, args) / divisor;
    }

    public Object execute_object(ProgramChromosome c, int n, Object[] args) {
        return ((Compatible) c.execute_object(n, 0, args)).execute_divide(c.
                execute_object(n, 1, args));
    }

    protected interface Compatible {
        public Object execute_divide(Object o);
    }

}
