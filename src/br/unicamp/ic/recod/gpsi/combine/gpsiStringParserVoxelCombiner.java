/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.unicamp.ic.recod.gpsi.combine;

import br.unicamp.ic.recod.gpsi.img.gpsiVoxel;
import bsh.EvalError;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 *
 * @author juan
 */
public class gpsiStringParserVoxelCombiner extends gpsiVoxelCombiner<double[], String>{

    private String exp;
    ScriptEngine engine;
    
    public gpsiStringParserVoxelCombiner(double[] b, String expression) throws EvalError, IOException, ScriptException {
        super(b, expression);
        
        StringBuilder expBuilder = new StringBuilder(expression);
        
        int in, st;
        
        while(expBuilder.indexOf("%") >= 0){
            
            in = expBuilder.indexOf("%");
            
            int pos = in;
            st = 0;
            while(pos >= 0){
                if(st == 0 && expBuilder.charAt(pos) == '(')
                    break;
                if(expBuilder.charAt(pos) == ')')
                    st++;
                if(expBuilder.charAt(pos) == '(')
                    st--;
                pos--;
            }
            
            expBuilder.setCharAt(in, ',');
            
            if(pos < 0){
                expBuilder.insert(0, "pd(");
                expBuilder.append(')');
            }else
                expBuilder.insert(pos, "pd");

        }
        
        exp = expBuilder.toString();
        
        Pattern p = Pattern.compile("b(\\d+)");
        Matcher m = p.matcher(expBuilder);
        exp = m.replaceAll("b[$1]");
        
        ScriptEngineManager manager = new ScriptEngineManager();
        engine = manager.getEngineByName("JavaScript");
        engine.eval("function pd(a, b) { return b == 0.0 ? 1.0 : a / b; }");
        engine.eval("function srt(a) { return Math.sqrt(Math.abs(a)); }");
        engine.eval("function rlog(a) { return a == 0.0 ? 0.0 : Math.log(Math.abs(a)); }");
        
    }

    @Override
    public double combineVoxel(gpsiVoxel voxel) throws EvalError, ScriptException{
        
        engine.getBindings(ScriptContext.ENGINE_SCOPE).put("b", voxel.getHyperspectralData());
        double r = (double)engine.eval(exp);
        return r;

    }
    
}
