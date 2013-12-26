package djbdd.reductors.genetic;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class ChromosomeComparator implements Comparator<Chromosome> {
    
    public ChromosomeComparator(){
    
    }
    
    @Override
    public int compare(Chromosome o1, Chromosome o2) {
        if(o1.treeSize < o2.treeSize){
            return -1;
        }else if(o1.treeSize == o2.treeSize){
            if(o1.hashCode() < o2.hashCode())
                return -1;
            else if(o1.hashCode() > o2.hashCode())
                return 1;
            return 0;
        }else{
            return 1;
        }
    }
    
}
