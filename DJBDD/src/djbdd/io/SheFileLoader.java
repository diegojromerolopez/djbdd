/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.BDD;
import djbdd.timemeasurer.TimeMeasurer;
import java.util.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.*;
import djbdd.parallel.*;


/**
 * Loads a BDD from a Steven She boolean format file extracted from the kconfig.
 * @author diegoj
 */
public class SheFileLoader {
    /** Number of threads used to load the BDD trees */
    public final int NUM_THREADS = 1;
    
    /** Path of the DIMACS file */
    String filename;
    
    /** Number of variables of the BDDs */
    public int numVariables;
    
    /** Number of formulas in the SHE file */
    public int numClausules;
    
    /** Defined variables */
    public ArrayList<String> variables;
    
    /** Variables with the @ as prefix */
    public ArrayList<String> named_variables;
    
    /** Variables with the $ as prefix */
    public ArrayList<String> integer_variables;
    
    /** List of converted formulas in bdd-friendly format of the she file */
    public ArrayList<String> formulas;
    
    /** List of formulas of the she file */
    public ArrayList<String> original_formulas;
    
    /** List of formulas of each BDD. Note that we can get more than one formula for BDD */
    public ArrayList<String> bdd_formulas;
    
    /** Character of starting variable in a formula */
    final static String START_VAR = "{";
    
    /** Character of ending variable in a formula */
    final static String END_VAR = "}";
    
    /**
     * Constructor: builds a BDDDimacsLoader from the DIMACS file path.
     * @param String filename Path of the dimacs file.
     */
    public SheFileLoader(String filename){
        this.filename = filename;
    }
    
    /**
     * Returns the variables that exists in the function.
     * @param function Boolean function to get its present variables.
     * @return ArrayList perfect for initializing the present variables of a BDD.
     */
    public String[] getVariableOrder(String function){
        ArrayList<String> variable_order = new ArrayList<String>(variables.size());
        for(int i=0; i<variables.size(); i++){
            String var = variables.get(i);
            Boolean exists_variable = function.contains(var);
            if(exists_variable)
                variable_order.add(var);
        }
        return variable_order.toArray(new String[variable_order.size()]);
    }
    
    /**
     * Initalize the loader with a configuration.
     * @param config Configuration of the parameteres of loading the she file.
     */
    public void init(FileLoaderConfiguration config){
        numVariables = -1;
        numClausules = config.numberOfClausules;
        this.variables = new ArrayList<String>();
        this.named_variables = new ArrayList<String>();
        this.integer_variables = new ArrayList<String>();
        this.formulas = new ArrayList<String>();
        this.original_formulas = new ArrayList<String>();
        try{
          // Open the file that is the first 
          // command line parameter
          FileInputStream fstream = new FileInputStream(filename);
          // Get the object of DataInputStream
          DataInputStream in = new DataInputStream(fstream);
          BufferedReader br = new BufferedReader(new InputStreamReader(in));
          String line;

          int formulaIndex = 1;
          if(config.verbose)
              System.out.println("Extracting clausules");
          //Read File Line By Line
          while ((line = br.readLine()) != null && line.length()>0)
          {
              if(line.length()==0)
                  break;
              // Asume that lines starting with # area comments
              // (not defined in that way by S. She)
              if (line.charAt(0)!='#')
              {
                  // Boolean named variable
                  if(line.charAt(0)=='@')
                  {
                      String[] content = line.split(" ");
                      String variable = START_VAR+content[1]+END_VAR;
                      variables.add(variable);
                      named_variables.add(variable);
                      numVariables++;
                      //System.out.println(variable);
                  }
                  // Integer variable? x_i
                  else if(line.charAt(0)=='$')
                  {
                      String[] content = line.split(" ");
                      String variable = START_VAR+content[1]+END_VAR;
                      variables.add(variable);
                      integer_variables.add(variable);
                      numVariables++;
                      //System.out.println(variable);
                  }
                  // Clausules
                  else
                  {
                    String formula = line.trim();
                    original_formulas.add(formula);
                    
                    // Okay, do not touch this. This regular expressions have been
                    // carved in stone gotten of the Mount Doom
                    // No, seriously, DO NOT TOUCH THEM, they work
                    String formulaI = formula;
                    formulaI = formulaI.replace("|", " || ");
                    formulaI = formulaI.replace("!!", " ");
                    formulaI = formulaI.replace("&", " && ");
                    formulaI = formulaI.replaceAll("([^\\w_]+)(x[1-9]+)([^\\w_]+)", "$1 "+START_VAR+"$2"+END_VAR+"$3");
                    formulaI = formulaI.replaceAll("(\\!?)([\\w_\\d]+)", "$1"+START_VAR+"$2"+END_VAR);
                    formulaI = formulaI.replace(START_VAR+START_VAR,START_VAR);
                    formulaI = formulaI.replace(END_VAR+END_VAR,END_VAR);
                    formulaI = formulaI.replace(START_VAR+"0"+END_VAR," false ");
                    formulaI = formulaI.replace(START_VAR+"1"+END_VAR," true ");
                    formulaI = formulaI.replace(START_VAR+"false"+END_VAR," false ");
                    formulaI = formulaI.replace(START_VAR+"true"+END_VAR," true ");
                    formulaI = formulaI.replaceAll(Pattern.quote(START_VAR)+"([\\w\\d_]+)"+Pattern.quote(END_VAR)+"\\s*\\|\\|\\s*true\\s*","true");
                    formulaI = formulaI.replace("<=>","<->");

                    formulas.add(formulaI);
                    
                    // Warn the user everything runs smoothly
                    if(config.verbose){
                        System.out.println("Extracting clausule: "+(formulaIndex)+" / "+numClausules+": "+formulaI);
                    }
                    
                    formulaIndex++;
                    
                    // Stop condition, yeah there are other ways to do this
                    // but I prefer this one
                    if(formulaIndex > numClausules && numClausules!=-1)
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
        
        BDD.initVariables(variables);
        // Now we are going to create the bdd one clausule at a time
        bdd_formulas = new ArrayList<String>( formulas.size()/config.numberOfCNFByBDD );
        String formulaBDDI = "";
        boolean firstIteration = true;
        for(int i=0; i<formulas.size(); i++)
        {
            if(!firstIteration)
                formulaBDDI += "&& (" + formulas.get(i)+") ";
            else{
                formulaBDDI += "(" + formulas.get(i)+") ";
                firstIteration = false;
            }
            if((i+1) % config.numberOfCNFByBDD == 0){
                bdd_formulas.add(formulaBDDI);
                formulaBDDI = "";
                firstIteration = true;
            }
        }
    
    }
    
    /**
     * Reads a S. She file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param config Name of the file containing the CNF in dimacs format.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD loadFile(FileLoaderConfiguration config){
        this.init(config);

        // Threaded loading
        int numThreads = Math.min(numClausules, NUM_THREADS);
        int numFormulasByThread = bdd_formulas.size()/numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ArrayList<SheFileLoaderThread> workers = new ArrayList<SheFileLoaderThread>();
        if(config.verbose){
            System.out.println( "There are "+bdd_formulas.size()+" formulas" );
        }
        // For each thread, we assign it some BDDs to make apply AND
        for (int i = 0; i < numThreads; i++) {
            int startFormulaIndex = i*numFormulasByThread;
            int endFormulaIndex = startFormulaIndex + numFormulasByThread;
            if(i==numThreads-1)
                endFormulaIndex = bdd_formulas.size();
            if(config.verbose){
                System.out.println("Thread "+i+" has clausules ["+startFormulaIndex+", "+endFormulaIndex+"]");
            }
            ArrayList<String> threadFormulas = new ArrayList<String>(bdd_formulas.subList(startFormulaIndex, endFormulaIndex));
            Runnable worker = new SheFileLoaderThread(i, threadFormulas, config.useApplyInCreation);
            executor.execute(worker);
            workers.add((SheFileLoaderThread)worker);
        }
        
        // Please, wait until all threads have ended.
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        
        // When we are ready, 
        for(int i=0; i<workers.size(); i++){
            BDD bddI = workers.get(i).getBDD();
            bddI.reduce();
            if(config.verbose){
                System.out.println("BDD "+i+": "+bddI.function);
                bddI.toFile("thread_bdds/bdd_"+i+".txt");
            }
        }
        if(config.verbose){
            System.out.println("Finished all threads");
        }
        
        BDD bdd = workers.get(0).getBDD();
        
        for(int i=1; i<workers.size(); i++){
            if(config.verbose)
                System.out.println(i+"th Apply");
            
            // Get the ith BDD, computed as a conjuntion of several BDDs by the ith thread
            BDD bddI = workers.get(i).getBDD();
            
            if(config.verbose)
                System.out.println(bdd.size() +" AND "+bddI.size());
            
            // Apply AND with each one of the BDDs computed by each thread
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
            
            if(config.verbose)
                System.out.println(i + "th Apply END: "+bdd.size());
        }
        
        // At last we get the complete BDD
        return bdd;
    }
    
}
