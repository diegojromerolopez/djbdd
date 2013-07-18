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

    public static final int FALSE_INDEX = 0;
    public static final int TRUE_INDEX = 1;
    
    public static final int FALSE_VARIABLE = -2;
    public static final int TRUE_VARIABLE = -1;
    public static final String HKEY_SEPARATOR="$";
    
    public static final int NULL_INDEX = -1; 
    
    /** Unique key of the vertex in the hash T of the TBDD */
    int index = NULL_INDEX;
    
    /** Index of the variable of this vertex in the variables */
    int variable = NULL_INDEX;
    
    /** Index of the low child of this vertex in the hash T of the TBDD */
    int low = NULL_INDEX;
    
    /** Index of the high child of this vertex in the hash T of the TBDD */
    int high = NULL_INDEX;
    

    /** Constructs the vertex */
    public Vertex(int index, int var, int low, int high){
        this.index = index;
        this.variable = var;
        this.low = low;
        this.high = high;
    }
    
    /** Constructs the vertex */
    public Vertex(int index, int var, Vertex low, Vertex high){
        this.index = index;
        this.variable = var;
        this.low = low.index;
        this.high = high.index;
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
        return (this.low == this.high && this.low!=Vertex.NULL_INDEX);
    }
    
    public boolean isDuplicate(Vertex v){
        return (this.low == v.low && this.high == v.high && this.variable == v.variable);
    }
    
    public boolean equals(Vertex v){
        return (this.index == v.index && this.low == v.low && this.high == v.high && this.variable == v.variable);
    }
    
    public Vertex getHighParent(TableT T){
        for(Vertex v : T.getVertices())
            if(v.low == this.index)
                return v;
        return null;
    }
    
    public Vertex getLowParent(TableT T){
        for(Vertex v : T.getVertices())
            if(v.high == this.index)
                return v;
        return null;    
    }
    
    public String keyH(){
        return Vertex.kH(this.variable, this.low, this.high);
    }
    
    public static String kH(int variable, int low, int high){
        return variable+HKEY_SEPARATOR+low+HKEY_SEPARATOR+high;
    }
    
    @Override
    public String toString(){
        if(this.index == FALSE_INDEX)
            return "<Vertex FALSE>";
        else if(this.index == TRUE_INDEX)
            return "<Vertex TRUE>";
        return "<Vertex index="+this.index+", var="+this.variable+", (low="+this.low+", high="+this.high+")>";
    }
}
