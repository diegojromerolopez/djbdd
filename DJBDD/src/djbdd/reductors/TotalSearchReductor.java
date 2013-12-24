package djbdd.reductors;

/**
 * Reduces the graph using a exact search algorithm.
 * Realizes a full search.
 * NOTE: expect this algorightm to be very slow.
 * @author diegoj
 */
public class TotalSearchReductor extends SiftingReductor {
  
    public TotalSearchReductor(){
        super();
    }
    
    @Override
    public void run(){
        // For each variable find its better position given that
        // the other variables have moving positions
        for(int var1Index: this.variableOrder){
            this.findBestPositionForVariable(var1Index);
            for(int var2Index: this.variableOrder){
                if(var1Index != var2Index){
                    this.findBestPositionForVariable(var2Index);
                }
            }
        }
        // The result have to be the best posible ordering
    }
    
}
