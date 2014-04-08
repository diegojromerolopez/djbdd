package asserter.core;

import djbdd.reductors.ReductionAlgorithm;
import djbdd.core.BDD;
import djbdd.io.Printer;
import java.util.*;

import java.lang.reflect.*;

/**
 * Contains the engine of the asserter that uses the DJBDD package.
 * @author diegoj
 */
public class Asserter {
    
    /** List of variables */
    private static ArrayList<String> VARIABLES;
    
    /** Index assignament to variables */
    private HashMap<String,Integer> variableIndex;
    
    /** Boolean logical assignament to variables */
    private HashMap<Integer,Boolean> variableAssignement;
    
    /** BDD that contains the logical expression */
    private final BDD bdd;
    
    /** BDD reduction method. Set as our algorithm. */
    private static final int BDD_REDUCTION_METHOD = ReductionAlgorithm.ITERATIVE_SIFTING_ALGORITHM;
    
    /** Informs if the assertion system is running */
    private static boolean BDD_SYSTEM_INITIALIZED = false;
    
    /**
     * Object that implements the predicates that will be used to construct
     * the logical propositions.
     */
    private Object logicEngine;
    
    public static void init(ArrayList<String> variables){
        if(!Asserter.BDD_SYSTEM_INITIALIZED){
            BDD.init(variables);
            Asserter.VARIABLES = variables;
            Asserter.BDD_SYSTEM_INITIALIZED = true;
        }
    }
    
    /**
     * Init the assertion system.
     * @param variables List of varibles that the logical formula contains.
     */
    public static void init(String[] variables){
        Asserter.init(new ArrayList<String>(Arrays.asList(variables)));
    }
    
    /**
     * Constructs the asserter with a formula.
     * @param formula Boolean logic formula that has to be followed.
     */
    public Asserter(String formula, Object logicEngine){
        // Constructs the BDD based on this formula
        this.bdd = new BDD(formula);
        
        //Printer.printBDD(this.bdd, "bdd_"+this.bdd.size()+"_");
        // Reduce the formula using the 
        BDD.reduce(Asserter.BDD_REDUCTION_METHOD);
        
        // Object that implements the logical variables as methods
        // and is called when it is needed the current state of the system
        this.logicEngine = logicEngine;
        
        // For debugging
        //Printer.printBDD(this.bdd, "bdd_reduced_"+this.bdd.size()+"_");
        
        // Assign to each variable the FALSE value
        // (we assume that what we don't know is always FALSE)
        this.variableAssignement = new HashMap<Integer,Boolean>();
        // We keep the index of the variables
        this.variableIndex = new HashMap<String,Integer>();
        int i = 0;
        for(String v : Asserter.VARIABLES){
            this.variableAssignement.put(i, Boolean.FALSE);
            this.variableIndex.put(v, i);
            i++;
        }
    }
    
    /**
     * Computes the value of the variable.
     * @param variableName Name of the variable whose boolean value will be gotten.
     * @return Boolean value of the variable variableName.
     */
    private boolean value(String variableName){
        // We calls a method of the logicEngine objecet whose name is
        // variableName using Java's reflection
        try {
          Method method = this.logicEngine.getClass().getMethod(variableName);
          return (boolean)method.invoke(this.logicEngine);
        } catch (SecurityException e) {
            System.err.println("Method "+variableName+" is wrong");
            return false;
        } catch (NoSuchMethodException e) {
            System.err.println("Method "+variableName+" does not exists");   
            return false;
        } catch(IllegalAccessException e){
            System.err.println("Method "+variableName+" has not public access");   
            return false;
        } catch(InvocationTargetException e){
            System.err.println("There was an exception when executing "+variableName+"");   
            return false;
        }
    }
    
    /**
     * Runs the assertion system.
     * @return True if the formula has the logical true value, false otherwise.
     */
    public boolean run(){
        // We store the current truth value assignement to restore it later if
        // the formula is not satisfacible
        HashMap<Integer,Boolean> lastVariableAssignement = (HashMap<Integer,Boolean>)this.variableAssignement.clone();
        
        // For each variable, we got its value from calling a method of the
        // logicEngine object with this name
        for(String v : Asserter.VARIABLES){
            int vIndex = this.variableIndex.get(v);
            this.variableAssignement.put(vIndex, this.value(v));
            //System.out.println("Assigning "+v+" = "+this.value(v));
        }
        
        // Restricts the BDD to this assignement and see if the BDD is the
        // constant TRUE
        BDD restricted = this.bdd.restrict(this.variableAssignement);
        
        // Debug prints
        //this.bdd.print(true);
        //restricted.print(true);
        
        // Test if the formula is consistent
        boolean isOk = restricted.isTautology();
        //System.out.println("Is tautology? "+isOk);
        
        // If returns FALSE, return to the last variable assignement
        if(!isOk){
            this.variableAssignement = lastVariableAssignement;
        }
        
        // At last, return if the formula is consistent or it is not.
        return isOk;
    }
    
}
