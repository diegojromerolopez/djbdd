/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.io;

import java.util.*;
import java.io.*;
import djbdd.timemeasurer.TimeMeasurer;
import djbdd.BDD;

/**
 *
 * @author diegoj
 */
public class FileOptimizerThread implements Runnable {
    private int index;
    private ArrayList<String> formulas;
    private ArrayList<String> variables;
    private boolean verbose = true;
    private ArrayList<BDD> bdds;
    boolean useApplyInCreation = true;
    String outputFilename;
    private static final int ITERATIONS = 100;
    private static final int MAX_VARIABLES_TO_MAKE_ITERATIONS = 20; 
     
    public FileOptimizerThread(int index, String outputFilename, ArrayList<String> formulas, ArrayList<String> variables, boolean useApplyInCreation){
        this.index = index;
        this.formulas = formulas;
        this.variables = variables;
        this.useApplyInCreation = useApplyInCreation;
        this.outputFilename = outputFilename;
        this.bdds = new ArrayList<BDD>(formulas.size());
    }
 
    private synchronized void writeToFile(PrintWriter writer, BDD bdd, int i){
        writer.println("# BEGIN BDD T"+this.index+ " " + (i + 1)+"\n"+bdd.toString()+"# END BDD T"+this.index+ " " + (i + 1)+"\n");
        writer.flush();
        //writer.print(bdd.toString());
        //writer.println("# END BDD T"+this.index+ " " + (i + 1));
    }
    
    private String[] getInitialVariableOrder(String function){
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
        //String[] _variable_order = this.getInitialVariableOrder(formulas.get(0));
        
        try {
            //PrintWriter writer = new PrintWriter(this.outputFilename, "UTF-8");
            PrintWriter writer = new PrintWriter(new FileOutputStream(new File(this.outputFilename),true));
            for (int i = 0; i < formulas.size(); i++) {
                TimeMeasurer t = new TimeMeasurer("\nFMLA " + (i + 1) + "/" + formulas.size());
                String formulaI = formulas.get(i);
                if (verbose) {
                    System.out.println("T"+this.index+ " - Formula " + (i + 1) + "/" + formulas.size() + ": " + formulaI);
                }
                String[] _variable_order = this.getInitialVariableOrder(formulaI);
                // We make ITERATIONS loops, unless we got too many variables
                // in that case, we make 1 only extra iteration
                int LOOPS = ITERATIONS;
                if(_variable_order.length > MAX_VARIABLES_TO_MAKE_ITERATIONS)
                    LOOPS = 1;
                
                // BDD base
                BDD bdd = new BDD(formulaI, _variables, _variable_order, useApplyInCreation);
                // Iterations to get a smaller BDD
                int j = 0;
                while (j < LOOPS) {
                    Collections.shuffle(Arrays.asList(_variable_order));
                    BDD bddI = new BDD(formulaI, _variables, _variable_order, useApplyInCreation);
                    j++;
                    if (bddI.size() < bdd.size()) {
                        bdd = bddI;
                    }
                }
                bdds.add(bdd);
                this.writeToFile(writer, bdd, i);
                /*
                writer.println("# BEGIN BDD " + (i + 1));
                writer.print(bdd.toString());
                writer.println("# END BDD " + (i + 1));*/
                t.end();
                t.show();
            }

            writer.close();
        } catch (Exception e) {
            System.err.println("System has failed!");
            e.printStackTrace();
        }
    }
    
    public String getOutputFilename(){ return this.outputFilename; }
    
    public ArrayList<BDD> getBDDs(){ return this.bdds; }
  
    @Override
    public String toString(){
        return this.index +" thread with"+formulas.size()+" formulas";
    }
}