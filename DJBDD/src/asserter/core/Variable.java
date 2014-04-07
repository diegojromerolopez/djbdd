package asserter.core;

/**
 *
 * @author diegoj
 */
public class Variable {
    
    private String name;
    
    private Object obj;
    
    public Variable(String name, Object obj){
        this.name = name;
        this.obj = obj;
    }
    
    public boolean value()
    {
        return false;
    }
    
    public String name(){
        return this.name;
    }
    
}
