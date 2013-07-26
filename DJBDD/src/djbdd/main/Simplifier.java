/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.main;

import djbdd.BooleanSimplifier;
import djbdd.io.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author diegoj
 */
public class Simplifier {

    public static void simplifyDimacsFile(String filename, String outputFile, FileLoaderConfiguration config){
        //TODO
    }
    
    private static String clean(String s){
        s = s.replace("{","");
        s = s.replace("}","");
        s = s.replace("||","|");
        s = s.replace("&&","&");
        s = s.replace("&&","&");
        s = s.replace("FALSE","false");
        s = s.replace("TRUE","true");
        s = s.replace("!false","true");
        s = s.trim();
        return s;
    }
    
    public static void simplifySheFile(String filename, String outputFile, FileLoaderConfiguration config){
        BDDSheFileLoader loader = new BDDSheFileLoader(filename);
        loader.init(config);
        
        ArrayList<String> formulas = loader.formulas;
        try{
            PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
            for(String s : loader.named_variables)
                writer.println("@ "+clean(s));
            for(String s : loader.integer_variables)
                writer.println("$ "+clean(s));
            for(int i=0; i<formulas.size(); i++){
                String formula = formulas.get(i);
                System.out.println("Formula "+(i+1)+"/"+formulas.size()+": "+formula);
                formula = BooleanSimplifier.simplifyFormula(formula);
                formula = clean(formula);
                if(!formula.equalsIgnoreCase("true"))
                    writer.println(clean(formula));
            }
            writer.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
