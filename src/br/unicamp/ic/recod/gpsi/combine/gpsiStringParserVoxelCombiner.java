/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.combine;

import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import bsh.EvalError;
import bsh.Interpreter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author juan
 */
public class gpsiStringParserVoxelCombiner extends gpsiVoxelCombiner<double[], String>{

    private String exp;
    private final Interpreter interpreter;
    
    public gpsiStringParserVoxelCombiner(double[] b, String expression) throws EvalError, IOException {
        super(b, expression);
        this.exp = expression;
        
        int in, st;
        int[] iPre = new int[2], iPos = new int[2];
        while(exp.contains("%")){
            in = exp.indexOf('%');
            iPre[0] = iPre[1] = in - 2;
            iPos[0] = iPos[1] = in + 2;
            if(exp.charAt(in - 2) == ')'){
                st = 1;
                while(st > 0){
                    iPre[0]--;
                    if(exp.charAt(iPre[0]) == '(')
                        st--;
                    else if(exp.charAt(iPre[0]) == ')')
                        st++;
                }
                iPre[0]--;
            }else
                while(exp.charAt(iPre[0]) != '(')
                    iPre[0]--;
            if(exp.charAt(in + 2) == '('){
                st = 1;
                while(st > 0){
                    iPos[1]++;
                    if(exp.charAt(iPos[1]) == ')')
                        st--;
                    else if(exp.charAt(iPos[1]) == '(')
                        st++;
                }
                iPos[1]++;
            }else
                while(exp.charAt(iPos[1]) != ')')
                    iPos[1]++;
            
            exp = exp.substring(0, iPre[0]) + "pd(" + exp.substring(iPre[0] + 1, iPre[1] + 1) + "," + exp.substring(iPos[0], iPos[1]) + ")" + exp.substring(iPos[1] + 1, exp.length());
            
        }
        
        Pattern p = Pattern.compile("b(\\d+)");
        Matcher m = p.matcher(exp);
        exp = m.replaceAll("b[$1]");
        
        interpreter = new Interpreter();
        interpreter.source("scripts/pd.js");
        
    }

    @Override
    public double combineVoxel(gpsiVoxel voxel){
        try {
            interpreter.set("b", voxel.getHyperspectralData());
            interpreter.eval("val = " + exp);
            return (double) interpreter.get("val");
        } catch (EvalError ex) {
            return Double.NaN;
        }
    }
    
}
