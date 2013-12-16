/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;
import djbdd.core.*;

    
/**
 *
 * @author diegoj
 */
public class BDDReader {
    
    String format;
    String filename;
    
    public BDDReader(String format, String filename){
        this.format = format;
        this.filename = filename;
    }
    
    public BDD read(){
        // Loading of a DIMACS file
        if(format.equals("dimacs")){
            DimacsFileLoader loader = new DimacsFileLoader(filename);
            BDD bdd = loader.run();
            return bdd;
        }
        
        // Loading of a She file
        if(format.equals("she")){
            SheFileLoader loader = new SheFileLoader(filename);
            BDD bdd = loader.run();
            return bdd;            
        }
        
        // Loading of a C-Style formula in BDD
        if(format.equals("cstyle") || format.equals("c-style")){
            CStyleFormulaFileLoader loader = new CStyleFormulaFileLoader(filename);
            BDD bdd = loader.run();
            return bdd;
        }
        
        // Loading of a BDD in a djbdd file
        if(format.equals("djbdd")){
            return BDD.fromFile(filename);
        }
        
        System.err.println("Type "+format+" not recognized");
        return null;
    }
    
}
