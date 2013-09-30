package djbdd;

/**
 * Representation of a vertex of the graph that contains the BDDs.
 * @author diegoj
 */
public class Vertex {

    /** Debug boolean flag */
    public static final boolean VERBOSE = false;
    
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
    
    /**
     * Index of the variable of this vertex in the variables.
     * Note this attribute is only accesible from this package.
     */
    int variable = NULL_INDEX;
    
    /** Low child of this vertex in the hash T of the TBDD */
    private Vertex low = null;
    
    /** High child of this vertex in the hash T of the TBDD */
    private Vertex high = null;

    /** Number of parents that this vertex has */
    private int num_parents = 0;
    
    /** Number of BDDs that have this vertex as root */
    private int num_rooted_bdds = 0;
    
    /**
     * Constructs the vertex
     * @param index Unique index of the vertex. Should use  {@link djbdd.TableT#getNextKey} to assign a new index.
     * @param var Variable of the vertex.  See {@link djbdd.BDD#VARIABLES}.
     * @param low Low descendant vertex.
     * @param high High descendant vertex.
     */
    public Vertex(int index, int var, Vertex low, Vertex high){
        this.index = index;
        this.variable = var;
        this.setLow(low);
        this.setHigh(high);
        this.num_parents = 0;
    }
    
    /**
     * Construct a leaf vertex
     * @param value A leaf can be true or false. This parameter identifies the leaf vertex.
     */
    public Vertex(boolean value){
        if(value){
            this.index = TRUE_INDEX;
            this.variable = TRUE_VARIABLE;
        }else{
            this.index = FALSE_INDEX;
            this.variable = FALSE_VARIABLE;
        }
        this.num_parents = 0;
    }
    
    /**
     * Informs if this vertex is a leaf.
     * @return true if the vertex is the true or false vertex, false otherwise.
     */
    public boolean isLeaf(){
        return this.index == FALSE_INDEX || this.index == TRUE_INDEX;
    }

    /**
     * Informs if this vertex is a leaf and the false leaf.
     * @return true if the vertex is the false leaf, false otherwise.
     */
    public boolean isFalse(){
        return this.index == FALSE_INDEX;
    }

    /**
     * Informs if this vertex is a leaf and the true leaf.
     * @return true if the vertex is the true leaf, false otherwise.
     */
    public boolean isTrue(){
        return this.index == TRUE_INDEX;
    }

    /**
     * Gets the internal value of this vertex.
     * NOTE: use only when vertex is a leaf, otherwise this value should be ignored.
     * @return true if the vertex is the true leaf, false otherwise.
     */
    public boolean value(){
        return (this.index == TRUE_INDEX);
    }

    /**
     * Informs if the low and high descendants are the same vertex.
     * @return true if the vertex has only one descendant as low and high child, false otherwise.
     */
    public boolean isRedundant(){
        return (this.low == this.high && this.low!=null);
    }

    /**
     * Informs if the vertex is duplicate of the vertex parameter.
     * @param v Vertex to test if is duplicate of current vertex.
     * @return true if both vertices has the same low and high descendants, and the same variable. False otherwise.
     */
    public boolean isDuplicate(Vertex v){
        return (this.low == v.low && this.high == v.high && this.variable == v.variable);
    }
    
    /**
     * Informs if the vertex is the same vertex of the vertex parameter.
     * @param v Vertex to test if is equal to current vertex.
     * @return true if both vertices has the same low and high descendants, the same variable and the same index. False otherwise.
     */    
    public boolean equals(Vertex v){
        return (this.index == v.index && this.low == v.low && this.high == v.high && this.variable == v.variable);
    }
    
    /**
     * Computes the unique key for this vertex.
     * This unique key will be used in {@link djbdd.TableT#U} table hash.
     * @return Unique key for this vertex.
     */
    public String uniqueKey(){
        return Vertex.computeUniqueKey(this.variable, this.low, this.high);
    }

    /**
     * Computes the unique key for a vertex given its parameters.
     * @param var Variable of the vertex.  See {@link djbdd.BDD#VARIABLES}.
     * @param low Low descendant vertex.
     * @param high High descendant vertex.
     * @return Unique key for the vertex that will be created with var, low and high.
     */
    public static String computeUniqueKey(int variable, Vertex low, Vertex high){
        if(low==null && high==null){
            return variable+UNIQUE_KEY_SEPARATOR+"N"+UNIQUE_KEY_SEPARATOR+"N";
        }
        return variable+UNIQUE_KEY_SEPARATOR+low.index+UNIQUE_KEY_SEPARATOR+high.index;
    }
    
    /**************************************************************************/
    /**************************************************************************/
    
    /**************************************************************************/
    /**************************************************************************/   

    /**
     * Gets the high child.
     * @return The high vertex child.
     */
    public Vertex high(){ return this.high; }
    
    /**
     * Gets the high child index.
     * @return The high vertex child index.
     */
    public int highIndex(){
        if(this.high!=null)
            return this.high.index;
        // There are no high index, this vertex is a leaf
        // Return the null index        
        return Vertex.NULL_INDEX;
    }

    /**
     * Gets the low child.
     * @return The low vertex child.
     */
    public Vertex low(){ return this.low; }

    /**
     * Gets the low child index.
     * @return The low vertex child index.
     */
    public int lowIndex(){
        if(this.low!=null)
            return this.low.index;
        // There are no low index, this vertex is a leaf
        // Return the null index
        return Vertex.NULL_INDEX;
    }

    /**
     * Informs if the current vertex is child of the parameter.
     * @param v Vertex we want to test if is parent of current one.
     * @return true if current vertex is child of v, false otherwise.
     */
    public boolean isChildOf(Vertex v){
        return v.isParentOf(this);
    }
    
    /**
     * Informs if the current vertex is parent of the parameter.
     * @param v Vertex we want to test if is child of current one.
     * @return true if v is child of current vertex, false otherwise.
     */
    public boolean isParentOf(Vertex v){
        if(this.isLeaf()){
            return false;
        }
        return (this.low==v || this.high==v);
    }
    
    /**
     * Gets the variable of the vertex. See {@link djbdd.Vertex#variable} and {@link djbdd.BDD#VARIABLES}
     * @return The index of the variable in this vertex.
     */
    public int variable(){
        return this.variable;
    }
    
    /**
     * Gets the number of parents this vertex has.
     * @return The number of parents this vertex has.
     */
    public int numberOfParents(){
        return this.num_parents;
    }
    
    /**
     * Gets the number of BDDs that has this vertex as root.
     * @return The number of BDDs that has this vertex as root.
     */
    public int numberOfRootedBDDs(){
        return this.num_rooted_bdds;
    }    
    
    /**************************************************************************/
    /**************************************************************************/
    /* Operations about the number of BDDs that has this vertex as root */

    /**
     * Decrement the number of BDDs that has this vertex as root.
     * See {@link djbdd.Vertex#num_rooted_bdds}.
     */
    public void decNumRootedBDDs(){
        this.num_rooted_bdds--;
    }

    /**
     * Increment the number of BDDs that has this vertex as root.
     * See {@link djbdd.Vertex#num_rooted_bdds}. 
     */
    public void incNumRootedBDDs(){
        this.num_rooted_bdds++;
    }    
    
    /**************************************************************************/
    /**************************************************************************/
    /* Operations about the number of parents of a vertex */
    
    /**
     * Decrement the number of parents of a given vertex.  See {@link djbdd.Vertex#num_parents}.
     * @param v Vertex whose number of parents will be decremented in one.
     */    
    public static void decNumParentsOfVertex(Vertex v){
        if(v != null){
            if(VERBOSE){
                System.out.println("Decrementing "+v+" num_parents to "+(v.num_parents-1));
            }
            v.num_parents--;
        }
    }
    
    /**
     * Increment the number of parents of a given vertex. See {@link djbdd.Vertex#num_parents}.
     * @param v Vertex whose number of parents will be incremented in one.
     */        
    public static void incNumParentsOfVertex(Vertex v){
        if(v != null){
            if(VERBOSE){
                System.out.println("Incrementing "+v+" num_parents to "+(v.num_parents+1));
            }
            v.num_parents++;
        }
    }
    
    /**
     * Informs if this vertex is orphan. That is, it has no vertex parents and
     * is not the root of any BDD.
     * @return true if has no parents and is not the root of any BDD.
     */
    public boolean isOrphan(){
        // An orphan vertex is one that has not got:
        // - Any vertex parent
        // - BDD that has it as root
        return (this.num_parents==0 && this.num_rooted_bdds==0);
    }
    
    /**
     * Sets the high descendant. See  {@link djbdd.Vertex#high}.
     * @param newHigh Vertex that will be assigned as high descendant of this vertex.
     */
    public final void setHigh(Vertex newHigh){
        Vertex.decNumParentsOfVertex(this.high);
        this.high = newHigh;
        Vertex.incNumParentsOfVertex(this.high);
    }
    
    /**
     * Sets the low descendant. See  {@link djbdd.Vertex#low}.
     * @param newLow Vertex that will be assigned as low descendant of this vertex.
     */
    public final void setLow(Vertex newLow){
        Vertex.decNumParentsOfVertex(this.low);
        this.low = newLow;
        Vertex.incNumParentsOfVertex(this.low);
    }
    
    /**
     * Sets the variable of this vertex. See {@link djbdd.Vertex#variable} and {@link djbdd.BDD#VARIABLES}.
     * @param var_i Index of a variable in {@link djbdd.BDD#VARIABLES}.
     */
    void setVariable(int var_i){
        this.variable=var_i;
    }

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
