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


class BDDSheFileLoaderThread implements Runnable {
    private int index;
    private ArrayList<String> formulas;
    private ArrayList<String> variables;
    private boolean verbose = true;
    private BDD bdd;
     
    public BDDSheFileLoaderThread(int index, ArrayList<String> formulas, ArrayList<String> variables){
        this.index = index;
        this.formulas = formulas;
        this.variables = variables;
    }
 
    private String[] getVariableOrder(String function){
        ArrayList<String> variable_order = new ArrayList<String>(variables.size());
        for(int i=0; i<variables.size(); i++){
            String var = variables.get(i);
            Boolean exists_variable = function.contains(var);
            if(exists_variable)
                variable_order.add(var);
        }
        return variable_order.toArray(new String[variable_order.size()]);
    }
    
    @Override
    public void run() {
        String[] _variables = variables.toArray(new String[variables.size()]);
        String[] _variable_order = getVariableOrder(formulas.get(0));
        bdd = new BDD(formulas.get(0), _variables, _variable_order);
        bdd.print();
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
            //_variable_order = getVariableOrder(formulaI);
            //BDD bddI = new BDD(formulaI, _variables, _variable_order);
            TimeMeasurer t2 = new TimeMeasurer("\n[Thread "+this.index+"] BDD Creation "+(i+1)+"/"+formulas.size());
            BDD bddI = new BDD(formulaI, variables);
            t2.end().show();
            TimeMeasurer t3 = new TimeMeasurer("\n[Thread "+this.index+"] BDD APPLY "+(i+1)+"/"+formulas.size());
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
            bdd.print();
            t3.end().show();
            t.end().show();
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
    public int numVariables;
    public int numClausules;
    public ArrayList<String> variables;
    public ArrayList<String> named_variables;
    public ArrayList<String> integer_variables;
    public ArrayList<String> formulas;
    public ArrayList<String> original_formulas;
    public ArrayList<String> bdd_formulas;
    final static String START_VAR = "{";
    final static String END_VAR = "}";
    
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
                    System.out.println(formula);
                    String formulaI = formula;//formula.replaceAll("([^\\w\\d]+)(0)([^\\w\\d]+)", "$1 false $3");
                    //formulaI = formula.replaceAll("([^\\w\\d]+)(1)([^\\w\\d]+)", "$1 true $3");
                    formulaI = formulaI.replace("|", " || ");
                    formulaI = formulaI.replace("&", " && ");
                    formulaI = formulaI.replaceAll("([^\\w_]+)([1-9]+)([^\\w_]+)", "$1 "+START_VAR+"x$2"+END_VAR+"$3");
                    formulaI = formulaI.replaceAll("([\\w_\\d]+)", START_VAR+"$1"+END_VAR);
                    formulaI = formulaI.replace(END_VAR+END_VAR,END_VAR);
                    formulaI = formulaI.replace(START_VAR+"0"+END_VAR," false ");
                    formulaI = formulaI.replace(START_VAR+"1"+END_VAR," true ");
                    formulaI = formulaI.replace(START_VAR+"false"+END_VAR," false ");
                    formulaI = formulaI.replace(START_VAR+"true"+END_VAR," true ");
                    
                    formulaI = formulaI.replaceAll(Pattern.quote(START_VAR)+"([\\w\\d_]+)"+Pattern.quote(END_VAR)+"\\s*\\|\\|\\s*true\\s*","true");
                    //formulaI = formulaI.replaceAll("(true|false)"+END_VAR,"$1");
                    //System.out.println(formulaI);
                    //System.exit(-1);
                    formulas.add(formulaI);
                    if(config.verbose){
                        System.out.println("Extracting clausule: "+(formulaIndex)+" / "+numClausules+": "+formulaI);
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
        int numFormulasByThread = bdd_formulas.size()/numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ArrayList<BDDSheFileLoaderThread> workers = new ArrayList<BDDSheFileLoaderThread>();
        if(config.verbose){
            System.out.println( "There are "+bdd_formulas.size()+" formulas" );
        }
        for (int i = 0; i < numThreads; i++) {
            int startFormulaIndex = i*numFormulasByThread;
            int endFormulaIndex = startFormulaIndex + numFormulasByThread;
            if(i==numThreads-1)
                endFormulaIndex = bdd_formulas.size();
            if(config.verbose){
                System.out.println("Thread "+i+" has clausules ["+startFormulaIndex+", "+endFormulaIndex+"]");
            }
            ArrayList<String> threadFormulas = new ArrayList<String>(bdd_formulas.subList(startFormulaIndex, endFormulaIndex));
            Runnable worker = new BDDSheFileLoaderThread(i,threadFormulas,variables);
            executor.execute(worker);
            workers.add((BDDSheFileLoaderThread)worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        
        
        for(int i=0; i<workers.size(); i++){
            BDD bddI = workers.get(i).getBDD();
            bddI.reduce();
            if(config.verbose){
                System.out.println("BDD "+i+": "+bddI.function);
                //BDDPrinter.printBDD(bddI, "bdd_"+i);
                bddI.toFile("bdd_"+i+".txt");
            }
        }
        System.out.println("Finished all threads");
        BDD bdd = workers.get(0).getBDD();
        for(int i=1; i<workers.size(); i++){
            BDD bddI = workers.get(1).getBDD();
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
        }
        return bdd;//*/
    }
    
}
