/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.optimizer;

import java.util.*;
import djbdd.*;

/**
 *
 * @author diegoj
 */
public class Separator extends FileOptimizer{
    
    ArrayList<ArrayList<String>> formulaGroupList;
    HashMap<TreeSet<Integer>, ArrayList<String>> formulaGroups;
    
    private void initFormulaGroups(ArrayList<String> formulas){
        boolean useApply = true;
        boolean firstFormula = true;

        formulaGroups = new HashMap<TreeSet<Integer>, ArrayList<String>>();
        for(int i=0; i<formulas.size(); i++){
            String formulaI = formulas.get(i);
            BDD bdd = new BDD(formulaI, useApply);
            TreeSet<Integer> vars = new TreeSet<Integer>(bdd.present_variable_indices);
            if (firstFormula) {
                ArrayList<String> newformulaGroup = new ArrayList<String>(1);
                newformulaGroup.add(formulaI);
                formulaGroups.put(vars, newformulaGroup);
                firstFormula = false;
            } else {
                boolean foundGroup = false;
                for (TreeSet t : formulaGroups.keySet()) {
                    TreeSet<Integer> intersection = new TreeSet<Integer>(vars);
                    intersection.retainAll(t);
                    if (intersection.size() > 0) {
                        formulaGroups.get(t).add(formulaI);
                        foundGroup = true;
                        break;
                    }
                }
                if (!foundGroup) {
                    ArrayList<String> newformulaGroup = new ArrayList<String>(1);
                    newformulaGroup.add(formulaI);
                    formulaGroups.put(vars, newformulaGroup);
                }
            }
        }
        
        // We init the formula groups
        formulaGroupList = new ArrayList<ArrayList<String>>(formulaGroups.values());
        BDD.T.gc();
    }
    
    public Separator(ArrayList<String> formulas, String outputFilename){
        super(formulas,outputFilename);
        this.initFormulaGroups(formulas);
        this.groupBDDsByVariables = false;
        //System.out.println(formulaGroups);
        System.out.println(formulaGroupList.size());
        //System.exit(-1);
    }
    
    
    
    /**
     * Execute optimization and make one file for each BDD group.
     */
    @Override
    public void run() {
        int groupIndex = 1;
        for(ArrayList<String> formulas : formulaGroupList){
            System.out.println("Separator :: Group "+groupIndex+"/"+formulaGroupList.size());
            FileOptimizer.optimizeFormulas(formulas, "bdd_groups/"+this.outputFilename+"-"+groupIndex, this.groupBDDsByVariables);
            BDD.T.gc();
            BDD.T.clear();
            groupIndex++;
        }
    }
}
