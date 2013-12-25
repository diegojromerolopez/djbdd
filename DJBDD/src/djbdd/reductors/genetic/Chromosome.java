/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors.genetic;

import java.util.*;

import random.Random;
import djbdd.core.BDD;
import djbdd.core.VariableList;

/**
 * Chromosome for Genetic Algorithm.
 * @author diegoj
 */
public class Chromosome extends VariableList {
    
    /** Size that will have the BDD using this order. */
    int treeSize;
    
    /**
     * Assign the order given by this chromosome.
     */
    public void computeTreeSize(){
        for(int varIndex=0; varIndex<this.size; varIndex++){
            int varPosition = this.order.get(varIndex);
            BDD.T.moveVariable(varIndex, varPosition);
        }
        BDD.T.gc();
        this.treeSize = BDD.T.size();
    }
    
    private void initOrderedVariables(){
        for(int var_index=0; var_index<this.size; var_index++){
            int position = this.order.get(var_index);
            String var = this.variables.get(var_index);
            orderedVariables.set(position, var);
        }
    }
    
    private void initOrder(){
        // Order hash that gives the position of each variable
        this.order = new ArrayList<Integer>(this.size);
        for(int i=0; i<this.size; i++){
            this.order.add(i);
        }
        // Randomize the order
        Collections.shuffle(this.order);
        // Assign to each position its variable
        this.orderedVariables = new ArrayList<String>(this.size);
        for(int i=0; i<this.size; i++){
            orderedVariables.add("");
        }
        this.initOrderedVariables();
        this.computeTreeSize();
    }
    
    public Chromosome(){
        super(BDD.variables());
        this.initOrder();
    }
    
    public Chromosome(Chromosome original){
        super(BDD.variables());
        this.variables = original.variables;
        for(int i=0; i<this.size; i++){
            this.order.add(original.order.get(i));
            this.orderedVariables.add(original.orderedVariables.get(i));
        }
    }
 
     public Chromosome spawnMutant(double percentage){
        Chromosome mutant = new Chromosome(this);
        
        int mutatedNumberOfGenes = (int)Math.round(this.size*percentage);
        for(int i=0; i<mutatedNumberOfGenes; i++){
            int variableI = Random.randInt(0,this.size-1);
            int variableJ = Random.randInt(0,this.size-1);
            Collections.swap(mutant.order, variableI, variableJ);
        }
        
        mutant.initOrderedVariables();
        mutant.computeTreeSize();
        return mutant;
    }
    
    public void mutate(double percentage){
       
        int mutatedNumberOfGenes = (int)Math.round(this.size*percentage);
        for(int i=0; i<mutatedNumberOfGenes; i++){
            int variableI = Random.randInt(0,this.size-1);
            int variableJ = Random.randInt(0,this.size-1);
            Collections.swap(this.order, variableI, variableJ);
        }
        
        this.initOrderedVariables();
        this.computeTreeSize();
    }
    
    public Chromosome cross(Chromosome other){
        Chromosome spawn = new Chromosome(this);
        
        int thisI = 0;
        int otherI = 0;
        spawn.order = new ArrayList<Integer>(this.size);
        HashSet<Integer> usedPositions = new HashSet<Integer>();
        for(int varIndex=0; varIndex<this.size; varIndex++){
            
            int varPosition = -1;
            if(varIndex % 2 == 0){
                while(usedPositions.contains(varPosition) || varPosition==-1){
                    varPosition = this.order.get(thisI);
                    usedPositions.add(varPosition);
                    thisI++;
                }
            }
            else{
                while(usedPositions.contains(varPosition) || varPosition==-1){
                    varPosition = this.order.get(otherI);
                    usedPositions.add(varPosition);
                    otherI++;
                }
            }
            
            spawn.order.add(varPosition);
        }
        
        spawn.initOrderedVariables();
        spawn.computeTreeSize();
        return spawn;
    }
    
}
