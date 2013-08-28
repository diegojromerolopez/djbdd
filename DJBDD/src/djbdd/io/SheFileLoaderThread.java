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

/**
 *
 * @author diegoj
 */
class SheFileLoaderThread implements Runnable {
    private int index;
    private ArrayList<String> formulas;
    private boolean verbose = true;
    private BDD bdd;
    boolean useApplyInCreation = false;
     
    public SheFileLoaderThread(int index, ArrayList<String> formulas, boolean useApplyInCreation){
        this.index = index;
        this.formulas = formulas;
        this.useApplyInCreation = useApplyInCreation;
    }
 
    private String[] getVariableOrder(String function){
        ArrayList<String> variable_order = new ArrayList<String>(BDD.variables().size());
        for(int i=0; i<BDD.variables().size(); i++){
            String var = BDD.variables().get(i);
            Boolean exists_variable = function.contains(var);
            if(exists_variable)
                variable_order.add(var);
        }
        return variable_order.toArray(new String[variable_order.size()]);
    }
    
    @Override
    public void run() {
        //String[] _variable_order = getVariableOrder(formulas.get(0));
        bdd = new BDD(formulas.get(0));
        if(verbose){
            System.out.println("\n[Thread "+this.index+"] Formula "+(1)+"/"+formulas.size()+": "+formulas.get(0));
            bdd.print();
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
            BDD bddI = new BDD(formulaI);
            t2.end().show();
            TimeMeasurer t3 = new TimeMeasurer("\n[Thread "+this.index+"] BDD APPLY "+(i+1)+"/"+formulas.size());
            BDD bddRes = bdd.apply("and",bddI);
            bdd = bddRes;
            //bdd.reduce();
            //bdd.print();
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