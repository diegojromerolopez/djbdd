/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.prob;

import djbdd.*;
import java.util.*;

/**
 *
 * @author diegoj
 */
public class ProbComputer {
    
    /** BDD whose variable probabilites will be computed */
    private final BDD bdd;
    private final ArrayList<Vertex> vertices;
    private final int size;
    private final HashMap<Integer,Integer> ids;
    private final HashMap<Integer,Integer> probVariableIndices;
    private HashMap<Integer,Boolean> visited;
    private final int numVariables;
    
    /** Should we have to show aditional information */
    private static final boolean VERBOSE = true;
    
    public ProbComputer(BDD bdd){
        
        // Erase orphan nodes
        BDD.gc();
        
        // BDD
        this.bdd = bdd;
        
        ArrayList<Vertex> bddVertices = bdd.vertices();
        
        // Number of variables
        this.numVariables = BDD.variables().size();
        
        // Number of vertices
        this.size = bddVertices.size();
        
        // Assign contigous ids to the vertices
        
        // First we sort the vertices in ascending index order
        TreeMap<Integer,Vertex> sorter = new TreeMap<Integer,Vertex>();
        for(Vertex v : bdd.vertices()){
            sorter.put(v.index, v);
        }
        
        // Later we assign to each one a new id that is contigous
        Vertex root = bdd.root();
        this.vertices = new ArrayList<Vertex>(this.size);
        this.ids = new HashMap<Integer,Integer>(this.size);
        this.probVariableIndices = new HashMap<Integer,Integer>(this.size);
        int i = 0;
        for (Integer index : sorter.keySet()) {
            if(index != root.index){
                Vertex v = sorter.get(index);
                this.vertices.add(v);
                this.ids.put(index, i);
                this.probVariableIndices.put(index, v.variable());
            }
            i++;
        }
        
        this.probVariableIndices.put(Vertex.FALSE_INDEX, this.numVariables);
        this.probVariableIndices.put(Vertex.TRUE_INDEX, this.numVariables);
        
        this.vertices.add(root);
        this.ids.put(root.index, this.vertices.size()-1);
        this.probVariableIndices.put(root.index, root.variable());
        
        // Have been some vertex visited?
        this.visited = new HashMap<Integer,Boolean>();
        for(Vertex v : this.vertices){
            this.visited.put(v.index,false);
        }
        
        if(VERBOSE){
            System.out.println("BDD Array");
            System.out.println("pos\tvar\tvar_name\tlow\thigh");
            for(Vertex v : this.vertices){
                Vertex low = v.low();
                Vertex high = v.high();
                int lowId = -1;
                if(low!=null)
                    lowId = id(low);
                int highId = -1;
                if(high!=null)
                    highId = id(high);
                int varIndex = this.probVariableIndices.get(v.index);
                String variable = "";
                if(varIndex < this.numVariables){
                    variable = BDD.variables().get(varIndex);
                }
                System.out.println(id(v)+"\t"+varIndex+"\t"+variable+"\t"+lowId+"\t"+highId);
            }
        
        }
    }

    private int id(Vertex v){
        return this.ids.get(v.index);
    }
    
    private int pVariableIndex(Vertex v){
        return this.probVariableIndices.get(v.index);
    }
    
    private boolean visited(Vertex v){
        return this.visited.get(v.index);
    }
    
    private boolean setVisited(Vertex v, boolean value){
        this.visited.put(v.index, value);
        return value;
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /* Algorithms of R. Heradio & D. Amorós */

    /**
     * Algorithm 2: P(ψ)
     */
    protected double[] P(){
        /*
        Gets the probability of having False or True in the BDD.
        Returns a list whose
        - First element (index 0) is the probability of getting False in the function.
        - Second element (index 1) is the probability of getting True in the function.
        */
        
        int numVertices = this.size;
        double[] formulaSatProb = new double[numVertices];
        for(int i=0; i<numVertices; i++){
            formulaSatProb[i] = 0.0;
        }

        int i = numVertices-1;
        formulaSatProb[numVertices-1] = 1.0;   // root vertex
        while(i > 1){
            double increment = formulaSatProb[i]/2.0;
            Vertex vI = this.vertices.get(i);
            Vertex low = vI.low();
            Vertex high = vI.high();
            int lowId = id(low);
            int highId = id(high);
            formulaSatProb[lowId] += increment;
            formulaSatProb[highId] += increment;
            i -= 1;
        }

        return formulaSatProb;
    }

    /**
     * Marginal probabilites.
     */
    protected void MP_xi(int vIndex, double[] total_prob, double[] formula_sat_prob, double[] prob){
        double prob_low = 0.0;
        double prob_high = 0.0;
        // Avoid visited nodes
        Vertex w = this.vertices.get(vIndex);
        //w.visited = !w.visited;
        setVisited(w, !visited(w));

        // Base case 1
        Vertex low = w.low();
        int lowId = id(low);
        if (lowId==1){
            prob_low = formula_sat_prob[vIndex]/2.0;
        }else if (lowId!=0){
            if (visited(w) != visited(low))
                this.MP_xi(lowId, total_prob, formula_sat_prob, prob);
            prob_low = (total_prob[lowId] * formula_sat_prob[vIndex]/2.0) / formula_sat_prob[lowId];
        }
        // Base case 2
        Vertex high = w.high();
        int highId = id(high);
        if (highId==1){
            prob_high = formula_sat_prob[vIndex]/2.0;
        }else if (highId!=0){
            if (visited(w) != visited(high))
                this.MP_xi(highId, total_prob, formula_sat_prob, prob);
            prob_high = (total_prob[highId] * formula_sat_prob[vIndex]/2.0) / formula_sat_prob[highId];
        }

        total_prob[vIndex] = prob_low + prob_high;
        
        prob[pVariableIndex(w)] += prob_high;

        // Trasversal
        int i = pVariableIndex(w)+1;
        while (i<pVariableIndex(low)){
            prob[i] += prob_low/2.0;
            i += 1;
        }
        i = pVariableIndex(w)+1;
        while (i<pVariableIndex(high)){
            prob[i] += prob_high/2.0;
            i += 1;
        }
    }

    public double[] P_xi(){
        int numLiterals = this.numVariables;
        int numVertices = this.vertices.size();

        // Incialización
        double [] totalProb = new double[numVertices];
        Arrays.fill(totalProb, 0.0);
        double [] prob = new double[numLiterals];
        Arrays.fill(totalProb, 0.0);

        // Total probabilities
        double[] formulaSatProb = this.P();

        // Marginal probabilities
        this.MP_xi(numVertices-1, totalProb, formulaSatProb, prob);

        for(int i=0; i<numLiterals; i++)
            prob[i] = prob[i]/formulaSatProb[1];
        return prob;
    }
    
    public void run(){
        double[] probs = this.P_xi();
        
        for(double p : probs){
            System.out.print(p+", ");
        }
        System.out.println("");
    }
    
}
