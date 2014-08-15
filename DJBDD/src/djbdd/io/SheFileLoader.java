/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import djbdd.core.BDD;
import java.util.*;
import java.util.Comparator;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.*;


class ReverseValueComparator<T1,T2 extends Comparable<T2>> implements Comparator<T1> {
    Map<T1,T2> base;
    
    public ReverseValueComparator(Map<T1,T2> base) {
        this.base = base;
    }

    @Override
    public int compare(T1 k1, T1 k2) {
        T2 val1 = base.get(k1);
        T2 val2 = base.get(k2);
        if(val1.equals(val2)){
            return -1;
        }
        return val1.compareTo(val2);
    }
}

/**
 * Loads a BDD from a Steven She boolean format file extracted from the kconfig.
 * @author diegoj
 */
public class SheFileLoader {
    /** Number of threads used to load the BDD trees */
    public final int NUM_THREADS = 1;
    
    /** Path of the DIMACS file */
    String filename;
    
    public static final int NO_REORDERING = -1;
    public static final int REORDER_BY_OCURRENCE = 1;
    
    /** Should we apply some static reordering to the variables? */
    public int reordering = NO_REORDERING;//SheFileLoader.REORDER_BY_OCURRENCE;
            
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
    
    /** Informs if the loading must be verbose and show extra information or not */
    public static final boolean VERBOSE = false;
    
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
    
    private ArrayList<String> getOrderedVariablesByOcurrence(){
        ////////////////////////////////////////////////////////////////////////
        // Count variables
        HashMap<Integer,Integer> count = new HashMap<Integer,Integer>();
        for(int i=0; i<formulas.size(); i++){
            String formulaI = formulas.get(i);
            for(int v=0; v<variables.size(); v++){
                if(formulaI.contains(variables.get(v))){
                    if(!count.containsKey(v))
                        count.put(v, 0);
                    count.put(v, count.get(v)+1);
                }
            }
        }
        
        ReverseValueComparator<Integer,Integer> reverseComparator = new ReverseValueComparator<Integer,Integer>(count);
        TreeMap<Integer,Integer> sortedMap = new TreeMap<Integer,Integer>(reverseComparator);
        int m = 1;
        for(Integer k : count.keySet()){
            sortedMap.put(k,count.get(k));
            m++;
        }
        
        if(VERBOSE){
            System.out.println("----"+m+"----");
            System.out.println(sortedMap);
            System.out.println("Occurences");
            System.out.println(count.size());
            System.out.println(count);

            System.out.println("SortedMap");
            System.out.println(sortedMap.keySet().size());
            System.out.println(sortedMap);        
        }
        
        ArrayList<Integer> variableOrder = new ArrayList<Integer>();
        
        for(Integer i : sortedMap.descendingKeySet()) {
            if(VERBOSE)
                System.out.println(i);
            variableOrder.add( i );
        }
        
        if(VERBOSE){
            System.out.println("VariableOrder");
            System.out.println(variableOrder.size());
            System.out.println(variableOrder);
        }
       
        ArrayList<String> orderedVariables = new ArrayList<String>(variables.size());
        for(int i=0; i<variableOrder.size(); i++){
            int varIndex = variableOrder.get(i);
            System.out.println(variables.get(varIndex)+" occurs "+count.get(varIndex));
            orderedVariables.add(variables.get(varIndex));
        }

        if(VERBOSE){
            System.out.println("orderedVariables");
            System.out.println(orderedVariables.size());
            System.out.println(orderedVariables);
        
            System.out.println("Variables");
            System.out.println(variables.size());
            System.out.println(variables);
        }
        
        return orderedVariables;
    }
    
    /**
     * Initalize the loader with a configuration.
     * @param numberOfFormulasByBDD Number of formulas that will be used in each BDD.
     * @param numberOfFormulas Number of formulas that will be loaded.
     * @return List of formulas for each BDD.
     */
    public ArrayList<String> getFormulas(int numberOfFormulasByBDD, int numberOfFormulas){
        numVariables = -1;
        numClausules = numberOfFormulas;
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
          if(VERBOSE)
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
                    if(VERBOSE){
                        System.out.println("Extracting clausule: "+(formulaIndex)+" / "+numClausules+": "+formulaI);
                    }
                    
                    formulaIndex++;
                    
                    // Stop condition, yeah there are other ways to do this
                    // but I prefer this one
                    if(formulaIndex > numClausules && numClausules!=-1 && numClausules!=FileLoaderConfiguration.ALL_CLAUSULES)
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
        
        if(this.reordering == SheFileLoader.REORDER_BY_OCURRENCE){
            variables = this.getOrderedVariablesByOcurrence();
        }
        
        // Initialize variables with a given order
        BDD.init(variables);
        
        // Now we are going to create the bdd one clausule at a time
        bdd_formulas = new ArrayList<String>( formulas.size()/numberOfFormulasByBDD );
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
            if((i+1) % numberOfFormulasByBDD == 0){
                bdd_formulas.add(formulaBDDI);
                formulaBDDI = "";
                firstIteration = true;
            }
        }
        return bdd_formulas;
    }
    
    public ArrayList<String> getFormulas(FileLoaderConfiguration config){
        return this.getFormulas(config.numberOfClausules, config.numberOfCNFByBDD);
    }
    
    /**
     * Reads a S. She file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * @see BDD
     * @param numberOfFormulas Number of formulas that will be loaded.
     * @param numberOfFormulasByBDD Number of formulas that will be used in each BDD.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD run(int numberOfFormulasByBDD, int numberOfClausules){
        this.getFormulas(numberOfFormulasByBDD, numberOfClausules);

        // Threaded loading
        int numThreads = Math.min(bdd_formulas.size(), NUM_THREADS);
        int numFormulasByThread = bdd_formulas.size()/numThreads;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        ArrayList<SheFileLoaderThread> workers = new ArrayList<SheFileLoaderThread>();
        if(VERBOSE){
            System.out.println( "There are "+bdd_formulas.size()+" formulas" );
        }
        // For each thread, we assign it some BDDs to make apply AND
        for (int i = 0; i < numThreads; i++) {
            int startFormulaIndex = i*numFormulasByThread;
            int endFormulaIndex = startFormulaIndex + numFormulasByThread;
            if(i==numThreads-1)
                endFormulaIndex = bdd_formulas.size();
            if(VERBOSE){
                System.out.println("Thread "+i+" has clausules ["+startFormulaIndex+", "+endFormulaIndex+"]");
            }
            ArrayList<String> threadFormulas = new ArrayList<String>(bdd_formulas.subList(startFormulaIndex, endFormulaIndex));
            Runnable worker = new SheFileLoaderThread(i, threadFormulas, true);
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
            //bddI.reduce();
            if(VERBOSE){
                System.out.println("BDD "+i+": "+bddI.function);
                System.out.flush();
                bddI.toFile("bdd_"+i+".txt");
            }
        }
        if(VERBOSE){
            System.out.println("Finished all threads");
        }
        
        BDD bdd = workers.get(0).getBDD();
        
        for(int i=1; i<workers.size(); i++){
            if(VERBOSE)
                System.out.println(i+"th Apply");
            
            // Get the ith BDD, computed as a conjuntion of several BDDs by the ith thread
            BDD bddI = workers.get(i).getBDD();
            
            if(VERBOSE)
                System.out.println(bdd.size() +" AND "+bddI.size());
            
            // Apply AND with each one of the BDDs computed by each thread
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
            
            if(VERBOSE)
                System.out.println(i + "th Apply END: "+bdd.size());
        }
        
        // At last we get the complete BDD
        return bdd;
    }
    
    /**
     * Reads a S. She file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * @see BDD
     * @param config Name of the file containing the CNF in dimacs format.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD run(FileLoaderConfiguration config){
        return this.run(config);
    }
    
    /**
     * Reads a S. She file an builds a BDD containing the CNF.
     * This method creates a BDD at once, it DOES use the operator apply of the BDD.
     * @see BDD
     * @param config Name of the file containing the CNF in dimacs format.
     * @return BDD BDD tree with the formula contained in the filename.
     */
    public BDD run(){
        int numberOfFormulasByBDD = 1;
        return this.run(numberOfFormulasByBDD, FileLoaderConfiguration.ALL_CLAUSULES);
    }
    
}
