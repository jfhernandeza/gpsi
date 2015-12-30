/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.gp;

import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.terminal.Variable;

/**
 *
 * @author juan
 */
public class gpsiFitnessFunction_ extends GPFitnessFunction{

    private Variable vx;
    private Float[] x = new Float[20];
    private float[] y = new float[20];
    
    @Override
    protected double evaluate(IGPProgram individual) {
        return computeRawFitness(individual);
    }
    
    private double computeRawFitness(final IGPProgram ind) {
            double error = 0.0f;
            Object[] noargs = new Object[0];
            // Evaluate function for input numbers 0 to 20.
            // --------------------------------------------
            for (int i = 0; i < 20; i++) {
                // Provide the variable X with the input number.
                // See method create(), declaration of "nodeSets" for where X is
                // defined.
                // -------------------------------------------------------------
                vx.set(x[i]);
                try {
                    // Execute the GP program representing the function to be evolved.
                    // As in method create(), the return type is declared as float (see
                    // declaration of array "types").
                    // ----------------------------------------------------------------
                    double result = ind.execute_float(0, noargs);
                    // Sum up the error between actual and expected result to get a defect
                    // rate.
                    // -------------------------------------------------------------------
                    error += Math.abs(result - y[i]);
                    // If the error is too high, stop evlauation and return worst error
                    // possible.
                    // ----------------------------------------------------------------
                    if (Double.isInfinite(error)) {
                        return Double.MAX_VALUE;
                    }
                } catch (ArithmeticException ex) {
                    // This should not happen, some illegal operation was executed.
                    // ------------------------------------------------------------
                    System.out.println("x = " + x[i].floatValue());
                    System.out.println(ind);
                    throw ex;
                }
            }
            // In case the error is small enough, consider it perfect.
            // -------------------------------------------------------
            if (error < 0.001) {
                error = 0.0d;
            }
            return error;
        }

    public Variable getVx() {
        return vx;
    }

    public void setVx(Variable vx) {
        this.vx = vx;
    }

    public Float[] getX() {
        return x;
    }

    public void setX(Float[] x) {
        this.x = x;
    }

    public float[] getY() {
        return y;
    }

    public void setY(float[] y) {
        this.y = y;
    }
    
    
    
}
