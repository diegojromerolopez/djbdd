/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import djbdd.timemeasurer.TimeMeasurer;

import java.util.*;
import java.io.*;

import checker.*;
import checker.ast.Formula;
import checker.error.CheckerException;
import checker.parser.Parser;

/**
 *
 * @author diegoj
 */
public class BooleanSimplifier {
    
    final static boolean DEBUG = true;
    final String formula;
    final String inputFormula;
    final HashMap<String,String> inputReplacements;
    final HashMap<String,String> outputReplacements;
    
    public BooleanSimplifier(String formula){
        this.formula = formula;
        // Replacements to make the simplier work
        this.inputReplacements = new HashMap<String,String>();
        this.inputReplacements.put("->"," => ");
        this.inputReplacements.put("||"," OR ");
        this.inputReplacements.put("&&"," AND ");
        this.inputReplacements.put("!","NOT ");
        this.inputReplacements.put("true"," TT ");
        this.inputReplacements.put("false"," FF ");
        // Output replacements to return the output formula to our format
        this.outputReplacements = new HashMap<String,String>();
        this.outputReplacements.put("\\/", "||");
        this.outputReplacements.put("/\\", "&&");
        this.outputReplacements.put("NOT", "!");
        this.outputReplacements.put("TT ","true");
        this.outputReplacements.put("FF ","false");
        // Input replacements
        String inputF = this.formula;
        for(String s : this.inputReplacements.keySet())
            inputF = inputF.replace(s, this.inputReplacements.get(s));
        inputF = inputF.replaceAll("\\s+"," ");
        this.inputFormula = inputF;
    }
    
    private String makeOutputReplacements(String outputFormula){
        String replacedF = outputFormula;
        for(String s : this.outputReplacements.keySet())
            replacedF = replacedF.replace(s, this.outputReplacements.get(s));
        return replacedF;
    }
    
    public String run(){
        if(DEBUG){
            System.out.println(this.formula);
            System.out.println(this.inputFormula);
        }
        try{
            // convert String into InputStream
            InputStream functionIS = new ByteArrayInputStream(this.inputFormula.getBytes());
            // Parse it
            Parser myParser = new Parser(functionIS);
            Formula fmla = myParser.getAST();
            
            fmla = fmla.removeImplications();
            fmla = fmla.toNnf();
            fmla = fmla.nnfToCnf();
            fmla = fmla.simplifyCnf();
            return this.makeOutputReplacements(fmla.toString());
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static String simplifyFormula(String formula){
        TimeMeasurer t = new TimeMeasurer("@@@@@@@@@@ BooleanSimplifier.simplifyFormula @@@@@@@@");
        BooleanSimplifier b = new BooleanSimplifier(formula);
        String result = b.run();
        if(b.formula.length() > result.length())
            System.out.println("@@@@@@@@@@ MENOR TAMAÑO @@@@@@@@@@ de "+b.formula.length()+" a "+result.length());
        t.end().show();
        return result;
    }
    
}
