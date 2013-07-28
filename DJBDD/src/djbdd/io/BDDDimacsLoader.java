/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.BDD;
import java.util.*;
import java.io.*;

/**
 * Loads a DIMACS file in a BDD.
 * @author diegoj
 */
public class BDDDimacsLoader {
    
    /** Path of the DIMACS file */
    String filename;
    
    public static final boolean VERBOSE = true;
    
    /**
     * Constructor: builds a BDDDimacsLoader from the DIMACS file path.
     * @param String filename Path of the dimacs file.
     */
    public BDDDimacsLoader(String filename){
        this.filename = filename;
    }
    
     /**
     * Reads a dimacs file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it does not use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param filename Name of the file containing the CNF in dimacs format.
     */
    private BDD loadFileWithoutApply(){
        int numVariables = 0;
        int numClausules = 0;
        String formula = "";
        
        try{
          // Open the file that is the first 
          // command line parameter
          FileInputStream fstream = new FileInputStream(filename);
          // Get the object of DataInputStream
          DataInputStream in = new DataInputStream(fstream);
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String line;
          
          //Read File Line By Line
          while ((line = br.readLine()) != null && line.length()>0)
          {
              if(line.length()==0)
                  break;
              if (line.charAt(0)!='c'){
                  if(line.charAt(0)=='p')
                  {
                      String[] content = line.split(" ");
                      numVariables = Integer.parseInt(content[2]);
                      numClausules = Integer.parseInt(content[3]);
                  }
                  else
                  {
                    String content = line.substring(0, line.length()-2).trim();
                    content = content.replaceAll(" ", " || ");
                    content = content.replaceAll("-", " !");
                    content = content.replaceAll("  ", " ");
                    formula += "("+content +")"+ "&&";
                  }
              }
          }
          formula = formula.substring(0, formula.length()-2);
          //Close the input stream
          in.close();
         }
        catch (Exception e)
         {
             //Catch exception if any
             System.err.println("Error: " + e.getMessage());
             e.printStackTrace();
        }
      
        ArrayList<String> variables = new ArrayList<String>();
        
        formula = formula.replaceAll("(\\d+)", "x$1_");
        formula = formula.trim();
        
        for(int i=1; i<=numVariables; i++)
            variables.add("x"+i+"_");
        
        BDD bdd = new BDD(formula, variables, false);
        return bdd;
    }
   
    
    /**
     * Reads a dimacs file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param filename Name of the file containing the CNF in dimacs format.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD loadFile(FileLoaderConfiguration config){
        int cnfs_by_bdd = config.numberOfCNFByBDD;
        int clausules = config.numberOfClausules;
        if(!config.useApply)
            return this.loadFileWithoutApply();

        int numVariables = 0;
        int numClausules = 0;
        String formula = "";
        ArrayList<String> variables = new ArrayList<String>();
        ArrayList<String> CNFS = new ArrayList<String>();
        try{
          // Open the file that is the first 
          // command line parameter
          FileInputStream fstream = new FileInputStream(filename);
          // Get the object of DataInputStream
          DataInputStream in = new DataInputStream(fstream);
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String line;

          int formulaIndex = 1;
          String formulaBDD = "";
          if(config.verbose)
              System.out.println("Extracting clausules");
          //Read File Line By Line
          while ((line = br.readLine()) != null && line.length()>0)
          {
              if(line.length()==0)
                  break;
              //System.out.println("Otra línea");
              if (line.charAt(0)!='c')
              {
                  if(line.charAt(0)=='p')
                  {
                      String[] content = line.split(" ");
                      numVariables = Integer.parseInt(content[2]);
                      numClausules = Integer.parseInt(content[3]);
                      if(clausules != FileLoaderConfiguration.ALL_CLAUSULES && clausules>0 && clausules<numClausules)
                          numClausules = clausules;
                      for(int i=1; i<=numVariables; i++){
                        variables.add("x"+i+"_");
                      }
                  }
                  else
                  {
                    String formulaI = line.substring(0, line.length()-2).trim();
                    formulaI = formulaI.replaceAll(" ", " || ");
                    formulaI = formulaI.replaceAll("-", " !");
                    formulaI = formulaI.replaceAll("  ", " ");
                    formulaI += " ";
                    //System.out.println(formulaI);
                    formulaI = formulaI.replaceAll("(\\d+)", "x$1_");
                    formulaI = formulaI.trim();
                    CNFS.add(formulaI);
                    
                    if(config.verbose){
                        System.out.println("Extracting clausule: "+(formulaIndex+1)+" / "+numClausules);
                    }
                    formulaIndex++;
                    if(formulaIndex > numClausules)
                        break;
                  }
              }
          }
          //Close the input stream
          in.close();
         }catch (Exception e){//Catch exception if any
             System.err.println("Error: "+e.getMessage());
             e.printStackTrace();
        }
        
        // We got in CNFS the CNFs of the formula
        // So, we build one BDD for each CNF and use apply algorithm
        // to make the logical AND between them.
        
        ArrayList<String> bdd_formula = new ArrayList<String>( CNFS.size()/cnfs_by_bdd );
        String formulaBDDI = "";
        boolean firstIteration = true;
        for(int i=0; i<CNFS.size(); i++){
            if(!firstIteration)
                formulaBDDI += "&& (" + CNFS.get(i)+") ";
            else{
                formulaBDDI += "(" + CNFS.get(i)+") ";
                firstIteration = false;
            }
            if((i+1) % cnfs_by_bdd == 0){
                bdd_formula.add(formulaBDDI);
                formulaBDDI = "";
                firstIteration = true;
            }
        }
        
        BDD bdd = new BDD(bdd_formula.get(0),variables, config.useApplyInCreation);
        for(int i=1; i<bdd_formula.size(); i++){
            String formulaI = bdd_formula.get(i);
            if(config.verbose){
                System.out.println("Formula "+i+": "+formulaI);
                System.out.println((i+1)+"/"+bdd_formula.size());
            }
            BDD bddI = new BDD(formulaI, variables, config.useApplyInCreation);
            /*
            if(bddI == null){
                System.out.println("bddI es null");
                System.exit(-1);
            }
            if(bdd == null){
                System.out.println("bdd antes del apply es null");
                System.exit(-1);
            }*/
            BDD bddRes = bdd.apply("and",bddI);
            /*
            if(bddRes == null){
                System.out.println("bddRes obtenido del apply es null");
                System.exit(-1);
            }*/
            
            //System.out.println("Iteración "+i);
            //System.out.println(bddRes);
            //System.out.flush();
            bdd = bddRes;
        }
        if(config.verbose){
            System.out.println("BDD constructed: "+bdd.T.getVertices().size()+" vertices and "+bdd.getVariables().size()+" variables");
        }
        return bdd;    
    }
}
