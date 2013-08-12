/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

/**
 *
 * @author diegoj
 */
public class Vertex {

    /** Index of the false leaf node */
    public static final int FALSE_INDEX = 0;
    
    /** Index of the true leaf node */
    public static final int TRUE_INDEX = 1;
    
    /** Initial null index */
    public static final int NULL_INDEX = -1; 
    
    /** Index of the variable of the false node */
    public static final int FALSE_VARIABLE = -2;
    
    /** Index of the variable of the true node */
    public static final int TRUE_VARIABLE = -1;
    
    /** Separator for the computation of the unique key */
    public static final String UNIQUE_KEY_SEPARATOR="-";
    
    /** Unique key of the vertex in the hash T of the TBDD */
    public final int index;
    
    /** Index of the variable of this vertex in the variables */
    public int variable = NULL_INDEX;
    
    /** Index of the low child of this vertex in the hash T of the TBDD */
    private Vertex low = null;
    
    /** Index of the high child of this vertex in the hash T of the TBDD */
    private Vertex high = null;
    
    
    /** Constructs the vertex */
    public Vertex(int index, int var, Vertex low, Vertex high){
        this.index = index;
        this.variable = var;
        this.setLow(low);
        this.setHigh(high);
    }
    
    /** Construct a leaf vertex */
    public Vertex(boolean value){
        if(value){
            this.index = TRUE_INDEX;
            this.variable = TRUE_VARIABLE;
        }else{
            this.index = FALSE_INDEX;
            this.variable = FALSE_VARIABLE;
        }
    }
    
    public boolean isLeaf(){
        return this.index == FALSE_INDEX || this.index == TRUE_INDEX;
    }
    
    public boolean isFalse(){
        return this.index == FALSE_INDEX;
    }
    
    public boolean isTrue(){
        return this.index == TRUE_INDEX;
    }
    
    public boolean value(){
        return (this.index == TRUE_INDEX);
    }
    
    public boolean isRedundant(){
        return (this.low == this.high && this.low!=null);
    }
    
    public boolean isDuplicate(Vertex v){
        return (this.low == v.low && this.high == v.high && this.variable == v.variable);
    }
    
    public boolean equals(Vertex v){
        return (this.index == v.index && this.low == v.low && this.high == v.high && this.variable == v.variable);
    }
    
    public String uniqueKey(){
        return Vertex.computeUniqueKey(this.variable, this.low, this.high);
    }
    
    public static String computeUniqueKey(int variable, Vertex low, Vertex high){
        if(low == null && high==null){
            return variable+UNIQUE_KEY_SEPARATOR+"NULL"+UNIQUE_KEY_SEPARATOR+"NULL";
        }
        return variable+UNIQUE_KEY_SEPARATOR+low.index+UNIQUE_KEY_SEPARATOR+high.index;
    }
    
    /**************************************************************************/
    /**************************************************************************/
    
    /**************************************************************************/
    /**************************************************************************/   
    public Vertex high(){ return this.high; }
    public Vertex highVertex(){ return this.high; }
    public int highIndex(){
        if(this.high!=null)
            return this.high.index;
        return -1;
    }
    
    public Vertex low(){ return this.low; }
    public Vertex lowVertex(){ return this.low; }
    public int lowIndex(){
        if(this.low!=null)
            return this.low.index;
        return -1;
    }
    
    /**************************************************************************/
    /**************************************************************************/

    public final void setHigh(Vertex newHigh){
        /*if(this.high != NULL_INDEX && BDD.T.containsKey(this.high)){
            BDD.T.get(this.high).decParents();
        }
        this.high = newHigh.index;
        newHigh.incParents();*/
        this.high = newHigh;
    }
    
    /*
    public final void setHigh(int newHighIndex){
        Vertex newHigh = BDD.T.get(newHighIndex);
        this.setHigh(newHigh);
    }*/
    
    public final void setLow(Vertex newLow){
        /*if(this.low != NULL_INDEX && BDD.T.containsKey(this.low)){
            BDD.T.get(this.low).decParents();
        }
        this.low = newLow.index;
        newLow.incParents();*/
        this.low = newLow;
    }
    
    /*
    public final void setLow(int newLowIndex){
        Vertex newLow = BDD.T.get(newLowIndex);
        this.setLow(newLow);
    }*/

    /**************************************************************************/
    /**************************************************************************/
    
    /**
     * Gets the string representation of a vertex.
     * @return String Vertex in human legible format.
     */
    @Override
    public String toString(){
        if(this.index == FALSE_INDEX)
            return "<Vertex FALSE>";
        else if(this.index == TRUE_INDEX)
            return "<Vertex TRUE>";
        String variableName = BDD.variables().get(this.variable);
        return "<Vertex index="+this.index+", var="+this.variable+" ("+variableName+"), (low="+this.low.index+", high="+this.high.index+")>";
    }
}
