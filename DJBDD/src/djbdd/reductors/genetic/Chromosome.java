/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.reductors.genetic;

import java.util.*;

import random.Random;
import djbdd.core.BDD;
import djbdd.core.VariableList;
import djbdd.reductors.sifting.SiftingReductor;

/**
 * Chromosome for Genetic Algorithm.
 * @author diegoj
 */
public class Chromosome extends VariableList {
    
    /** Size that will have the BDD using this order. */
    int graphSize;
    
    /** Cache used to avoid repeated graph size computaltions */
    private static HashMap<String,Integer> GRAPH_SIZE_CACHE = new HashMap<String,Integer>();
    
    public String s;
    public long id;
    
    /**
     * Assign the order given by this chromosome.
     * @return The new size of the graph.
     */    
    public int applyOrderToGraph() {
        return super.applyOrderToGraph();
    }
    
    /**
     * Get the graph size given this order.
     */
    public void computeGraphSize(){
        String thisKey = this.key();
        if(Chromosome.GRAPH_SIZE_CACHE.containsKey(thisKey)){
            //System.out.println(thisKey+" "+Chromosome.GRAPH_SIZE_CACHE.get(thisKey));
            this.graphSize = Chromosome.GRAPH_SIZE_CACHE.get(thisKey);
        }
        else{
            this.graphSize = this.applyOrderToGraph();
            Chromosome.GRAPH_SIZE_CACHE.put(thisKey, this.graphSize);
        }
        
        //System.out.println("T size for chromosome "+this.order+": "+this.graphSize);
    }
    
    /**
     * Initialize the ordered variables member.
     */
    private void initOrderedVariables(){
        this.orderedVariables = new ArrayList<String>(this.size);
        for(int i=0; i<this.size; i++){
            orderedVariables.add("");
        }
        for(int var_index=0; var_index<this.size; var_index++){
            int position = this.order.get(var_index);
            String var = this.variables.get(var_index);
            orderedVariables.set(position, var);
        }
    }
    
    /**
     * Init the order of the variables in a randomized way
     */
    private void initRandomOrder(){
        // Order hash that gives the position of each variable
        this.order = new ArrayList<Integer>(this.size);
        for(int i=0; i<this.size; i++){
            this.order.add(i);
        }
        // Randomize the order
        Collections.shuffle(this.order, random.Random.getRandom());
        this.initOrderedVariables();
        this.computeGraphSize();
    }
    
    /**
     * Constructor of the Chromosome.
     * Creates a random order.
     */
    public Chromosome(){
        super(BDD.variables());
        VariableList variableList = BDD.variables();
        this.size = variableList.size();
        this.variables = new ArrayList<String>(this.size);
        this.order = new ArrayList<Integer>(this.size);
        this.orderedVariables = new ArrayList<String>(this.size);
        
        for(int i=0; i<this.size; i++){
            this.variables.add(variableList.get(i));
            this.order.add(variableList.getOrder().get(i));
            this.orderedVariables.add(variableList.getOrderedVariables().get(i));
        }
        this.initRandomOrder();
        this.id = UUID.randomUUID().getMostSignificantBits();
    }
    
    public Chromosome(VariableList variables){
        super(variables);
        this.computeGraphSize();
    }

    /**
     * Copy constructor of the Chromosome.
     * @param original Order that will be copied
     */
    public Chromosome(Chromosome original){
        super(BDD.variables());
        this.variables = original.variables;
        this.order = new ArrayList<Integer>(this.size);
        this.orderedVariables = new ArrayList<String>(this.size);
        for(int i=0; i<this.size; i++){
            this.order.add(original.order.get(i));
            this.orderedVariables.add(original.orderedVariables.get(i));
        }
        this.graphSize = original.graphSize;
    }

   /*  public Chromosome spawnMutant(double percentage){
        Chromosome mutant = new Chromosome(this);
        
        int mutatedNumberOfGenes = (int)Math.round(this.size*percentage);
        for(int i=0; i<mutatedNumberOfGenes; i++){
            int variableI = Random.randInt(0,this.size-1);
            int variableJ = Random.randInt(0,this.size-1);
            Collections.swap(mutant.order, variableI, variableJ);
        }
        
        mutant.initOrderedVariables();
        mutant.computeGraphSize();
        return mutant;
    }*/
    
    public void mutate(double probability){
        for(int i=0; i<this.size; i++){
            if(random.Random.rand()<probability){
                int variableI = Random.randInt(0,this.size-1);
                int variableJ = Random.randInt(0,this.size-1);
                Collections.swap(this.order, variableI, variableJ);
            }
        }
        
        this.initOrderedVariables();
        this.computeGraphSize();
    }
    
    public Chromosome cross(Chromosome other){
        Chromosome spawn = new Chromosome(this);
        
        int thisI = 0;
        int otherI = 0;
        spawn.order = new ArrayList<Integer>(this.size);
        for(int i =0; i<this.size; i++){
            spawn.order.add(-1);
        }
        
        HashSet<Integer> usedPositions = new HashSet<Integer>();
        for(int varIndex=0; varIndex<this.size; varIndex++){
            //System.out.println(spawn.order);
            int varPosition = -1;
            if(varIndex % 2 == 0){
                do{
                    varPosition = this.order.get(thisI);
                    thisI++;
                }while(usedPositions.contains(varPosition));
                usedPositions.add(varPosition);
                
                //System.out.println(thisI+ " "+varPosition);
            }
            else{
                do{
                    varPosition = other.order.get(otherI);
                    otherI++;
                }while(usedPositions.contains(varPosition));
                usedPositions.add(varPosition);
            }
            
            spawn.order.set(varIndex,varPosition);
        }
        
        //System.out.println(this.order+" + "+other.order);
        //System.out.println(spawn.order);
        
        spawn.initOrderedVariables();
        spawn.computeGraphSize();
        spawn.id = UUID.randomUUID().getMostSignificantBits();
        return spawn;
    }
    
    public void optimizeBySifting(){
        this.applyOrderToGraph();
        SiftingReductor reductor = new SiftingReductor();
        reductor.execute();
        this.order = new ArrayList<Integer>(BDD.variables().getOrder());
        this.orderedVariables = new ArrayList<String>(BDD.variables().getOrderedVariables());
        this.graphSize = BDD.T.gc();
    }
    
    public void print(){
        System.out.println(this.orderedVariables+" S:"+this.graphSize);
    }
    
    public String key(){
        return this.order.toString();
    }
    
    /**
     * Returns the graph size associated.
     */
    public int getGraphSize(){
        return this.graphSize;
    }
    
}
