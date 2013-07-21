/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import java.util.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Configuration of the loading.
 */
class SheLoaderConfiguration{
    /**  */
    public String text;
    
    /** Must saves a file with the BDD as image */
    boolean outputInFile = false;
    
    /** Is verbose mode? */
    public boolean verbose = true;
    
    /** Number of clausules by BDD (1 by default) */
    public int numberOfCNFByBDD = 1;
    
    /** Number of clausules in total */
    public int numberOfClausules = -1;
}

 
class BDDSheFileLoaderThread implements Runnable {
    private int index;
    private ArrayList<String> formulas;
    private ArrayList<String> variables;
    private boolean verbose = false;
    private BDD bdd;
     
    public BDDSheFileLoaderThread(int index, ArrayList<String> formulas, ArrayList<String> variables){
        this.index = index;
        this.formulas = formulas;
        this.variables = variables;
    }
 
    @Override
    public void run() {
        bdd = new BDD(formulas.get(0),variables);
        if(verbose){
            System.out.println("\n[Thread "+this.index+"] Formula "+(1)+"/"+formulas.size()+": "+formulas.get(0));
        }
        // Loop throught formulas whose index > 0
        for(int i=1; i<formulas.size(); i++)
        {
            TimeMeasurer t = new TimeMeasurer("\n[Thread "+this.index+"] LOOP "+(i+1)+"/"+formulas.size());
            String formulaI = formulas.get(i);
            if(verbose){
                System.out.println("[Thread "+this.index+"] Formula "+(i+1)+"/"+formulas.size()+": "+formulaI);
            }
            BDD bddI = new BDD(formulaI, variables);
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
            t.end();
            t.show();
        }
    }
    
    public BDD getBDD(){ return this.bdd; }
  
    @Override
    public String toString(){
        return this.index +" thread with"+formulas.size()+" formulas";
    }
}




/**
 * Loads a BDD from a Steven She boolean format file extracted from the kconfig.
 * @author diegoj
 */
public class BDDSheFileLoader {
    /** Number of threads used to load the BDD trees */
    public final int NUM_THREADS = 8;
    
    /** Path of the DIMACS file */
    String filename;
    
    /**
     * Constructor: builds a BDDDimacsLoader from the DIMACS file path.
     * @param String filename Path of the dimacs file.
     */
    public BDDSheFileLoader(String filename){
        this.filename = filename;
    }
    
    /**
     * Reads a S. She file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * See http://people.sc.fsu.edu/~jburkardt/data/cnf/cnf.html for a dimacs format description.
     * @see BDD
     * @param config Name of the file containing the CNF in dimacs format.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD loadFile(SheLoaderConfiguration config){
        String END_VAR = "_";
        int numVariables = -1;
        int numClausules = config.numberOfClausules;
        ArrayList<String> variables = new ArrayList<String>();
        ArrayList<String> formulas = new ArrayList<String>();
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
                      String variable = content[1]+END_VAR;
                      variables.add(variable);
                      numVariables++;
                      //System.out.println(variable);
                  }
                  // Integer variable? x_i
                  else if(line.charAt(0)=='$')
                  {
                      String[] content = line.split(" ");
                      String variable = content[1]+END_VAR;
                      variables.add(variable);
                      numVariables++;
                      //System.out.println(variable);
                  }
                  // Clausules
                  else
                  {
                    String formulaI = line.trim();
                    //System.out.println(formulaI);
                    formulaI = formulaI.replaceAll("([^\\w\\d]+)(0)([^\\w\\d]+)", "$1 false $3");
                    formulaI = formulaI.replaceAll("\\|", " || ");
                    formulaI = formulaI.replaceAll("&", " && ");
                    formulaI = formulaI.replaceAll("([^\\w_]+)([0-9]+)([^\\w_]+)", "$1 x$2"+END_VAR+"$3");
                    formulaI = formulaI.replaceAll("([\\w_\\d]+)", "$1"+END_VAR);
                    formulaI = formulaI.replaceAll("__","_");
                    formulaI = formulaI.replaceAll("false_","false");
                    //System.out.println(formulaI);
                    //System.exit(-1);
                    formulas.add(formulaI);
                    if(config.verbose){
                        System.out.println("Extracting clausule: "+(formulaIndex)+" / "+numClausules);
                    }
                    formulaIndex++;
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
        // Now we are going to create the bdd one clausule at a time
        ArrayList<String> bdd_formula = new ArrayList<String>( formulas.size()/config.numberOfCNFByBDD );
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
                bdd_formula.add(formulaBDDI);
                formulaBDDI = "";
                firstIteration = true;
            }
        }
        
        /*
        // Construction of the BDD
        BDD bdd = new BDD(bdd_formula.get(0),variables);
        if(config.verbose){
            System.out.println("\nFormula "+(1)+"/"+bdd_formula.size()+": "+bdd_formula.get(0));
        }
        // Loop throught formulas whose index > 0
        for(int i=1; i<bdd_formula.size(); i++)
        {
            TimeMeasurer t = new TimeMeasurer("\nLOOP "+i);
            String formulaI = bdd_formula.get(i);
            if(config.verbose){
                System.out.println("Formula "+(i+1)+"/"+bdd_formula.size()+": "+formulaI);
            }
            BDD bddI = new BDD(formulaI, variables);
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
            t.end();
            t.show();
        }
        if(config.verbose){
            System.out.println("BDD constructed: "+bdd.T.getVertices().size()+" vertices and "+bdd.variables.size()+" variables");
        }
        return bdd;
        //*/
        
        int numThreads = Math.min(numClausules, NUM_THREADS);
        int numFormulasByThread = bdd_formula.size()/numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ArrayList<BDDSheFileLoaderThread> workers = new ArrayList<BDDSheFileLoaderThread>();
        System.out.println( bdd_formula.size() );
        for (int i = 0; i < numThreads; i++) {
            int startFormulaIndex = i*numFormulasByThread;
            int endFormulaIndex = Math.min(startFormulaIndex + numFormulasByThread, bdd_formula.size());
            System.out.println("["+startFormulaIndex+", "+endFormulaIndex+"]");
            ArrayList<String> threadFormulas = new ArrayList<String>(bdd_formula.subList(startFormulaIndex, endFormulaIndex));
            Runnable worker = new BDDSheFileLoaderThread(i,threadFormulas,variables);
            executor.execute(worker);
            workers.add((BDDSheFileLoaderThread)worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        System.out.println("Finished all threads");
        
        for(int i=0; i<workers.size(); i++){
            BDD bddI = workers.get(i).getBDD();
            bddI.reduce();
            if(config.verbose){
                System.out.println("BDD "+i+": "+bddI.function);
                //BDDPrinter.printBDD(bddI, "bdd_"+i);
                bddI.toFile("bdd_"+i+".txt");
            }
        }
        
        BDD bdd = workers.get(0).getBDD();
        for(int i=1; i<workers.size(); i++){
            BDD bddI = workers.get(1).getBDD();
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
        }
        return bdd;//*/
    }
    
}
