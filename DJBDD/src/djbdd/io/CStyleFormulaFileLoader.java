/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.core.BDD;
import java.io.*;
import java.util.*;

/**
 *
 * @author diegoj
 */
public class CStyleFormulaFileLoader {
    /** Path of the CStyle file */
    String filename;
    
    /** Should it be verbose? */
    public static final boolean VERBOSE = false;
    
    /**
     * Constructor: builds a BDDDimacsLoader from the DIMACS file path.
     * @param String filename Path of the dimacs file.
     */
    public CStyleFormulaFileLoader(String filename){
        this.filename = filename;
    }

    /**
     * Loads all the formula in a BDD.
     * @return BDD BDD object that contains the formula in the file.
     */
    public BDD run(){
      
        try{
          // Open the file that is the first 
          // command line parameter
          FileInputStream fstream = new FileInputStream(filename);
          // Get the object of DataInputStream
          DataInputStream in = new DataInputStream(fstream);
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String line;

          if(VERBOSE){
              System.out.println("Extracting clausules");
          }
          
          //Read File Line By Line
          while ((line = br.readLine()) != null && line.length()>0)
          {
              if(line.length()==0)
                  break;
              
              // Ignore if comment
              if (line.charAt(0)=='/' && (line.charAt(1)=='/' || line.charAt(1)=='*')) 
              {
                  if(VERBOSE){
                      System.out.println("Comment: "+line);
                  }
              }
              else{
                  // The first line that is not a CPP style comment
                  // is the variables
                  break;
              }
          }
          
          if(line.length()==0){
              System.err.println("ERROR. Variables are not set");
              return null;
          }
          
          // Set the variables
          String[] variables = line.split(",\\s*");
          BDD.init(variables);
          
          if(VERBOSE){
              System.out.println("Variables:");
              for(String var : variables)
                  System.out.println("\t"+var);
          }
          
          // Gets the formulas
          StringBuilder s = new StringBuilder("");
          int i=1;
          while ((line = br.readLine()) != null && line.length()>0)
          {
              s.append(line);
              if(VERBOSE){
                  System.out.println("Line "+i+": "+line);
              }
              i++;
          }
          // Build the BDD
          System.out.println(s);
          BDD bdd = new BDD(s.toString());

          if(VERBOSE){
              System.out.println("BDD loaded\n");
              bdd.print(true);
          }          
          return bdd;
        }
        catch(Exception e){
            System.err.println("There was a problem loading the C-Style Formula File");
            e.printStackTrace();
        }
        return null;
    }
    
}
