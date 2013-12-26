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
        if(o1.graphSize < o2.graphSize){
            return -1;
        }else if(o1.graphSize == o2.graphSize){
            String o1Hash = o1.key();
            String o2Hash = o2.key();
            return o1Hash.compareTo(o2Hash);
        }else{
            return 1;
        }
    }
    
}
