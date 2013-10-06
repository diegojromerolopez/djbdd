/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.core.BDD;
import java.util.*;
import java.io.*;

/**
 * Loads a DIMACS file in a BDD.
 * @author diegoj
 */
public class DimacsFileLoader {
    
    /** Path of the DIMACS file */
    String filename;
    
    /** Should it be verbose? */
    public static final boolean VERBOSE = true;
    
    /** Character of starting variable in a formula */
    final static String START_VAR = "{";
    
    /** Character of ending variable in a formula */
    final static String END_VAR = "}";
    
    /**
     * Constructor: builds a BDDDimacsLoader from the DIMACS file path.
     * @param String filename Path of the dimacs file.
     */
    public DimacsFileLoader(String filename){
        this.filename = filename;
    }
    
    /**
     * Reads a dimacs file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param cnfsByBDD Number of CNFs in each BDD.
     * @param clausules Number of clausules that will be loaded.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD run(int cnfsByBDD, int clausules){
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
          
          if(VERBOSE)
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
                        variables.add(START_VAR+"x"+i+END_VAR);
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
                    formulaI = formulaI.replaceAll("(\\d+)", START_VAR+"x$1"+END_VAR);
                    formulaI = formulaI.trim();
                    CNFS.add(formulaI);
                    
                    if(VERBOSE){
                        System.out.println("Extracting clausule "+(formulaIndex)+" / "+numClausules+": "+formulaI);
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
        
        ArrayList<String> bdd_formula = new ArrayList<String>( CNFS.size()/cnfsByBDD );
        String formulaBDDI = "";
        boolean firstIteration = true;
        for(int i=0; i<CNFS.size(); i++){
            if(!firstIteration)
                formulaBDDI += "&& (" + CNFS.get(i)+") ";
            else{
                formulaBDDI += "(" + CNFS.get(i)+") ";
                firstIteration = false;
            }
            if((i+1) % cnfsByBDD == 0){
                bdd_formula.add(formulaBDDI);
                formulaBDDI = "";
                firstIteration = true;
            }
        }
        
        BDD.init(variables);
        
        BDD bdd = new BDD(bdd_formula.get(0));
        for(int i=1; i<bdd_formula.size(); i++){
            String formulaI = bdd_formula.get(i);
            if(VERBOSE){
                System.out.println("Formula "+i+": "+formulaI);
                System.out.println((i+1)+"/"+bdd_formula.size());
            }
            BDD bddI = new BDD(formulaI);
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
            if(VERBOSE){
                System.out.println("Iteration "+i);
                System.out.flush();
            }
            bdd = bddRes;
        }
        if(VERBOSE){
            System.out.println("BDD constructed: "+bdd.T.getVertices().size()+" vertices and "+BDD.variables().size()+" variables");
        }
        return bdd;    
    }

    /**
     * Reads a dimacs file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param cnfsByBDD Number of CNFs in each BDD during the construction process.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD run(int cnfsByBDD){
        return this.run(cnfsByBDD, FileLoaderConfiguration.ALL_CLAUSULES);
    }
    
    /**
     * Reads a dimacs file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD run(){
        return this.run(1, FileLoaderConfiguration.ALL_CLAUSULES);
    }    
    
    
     /**
     * Reads a dimacs file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param config Configuration readed from command line arguments.
     * @return BDD BDD tree with the formula contained in the filename.
     */  
    public BDD run(FileLoaderConfiguration config){
        int cnfsByBDD = config.numberOfCNFByBDD;
        int clausules = config.numberOfClausules;
        return this.run(cnfsByBDD, clausules);
    }
    

}
