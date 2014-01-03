package djbdd.core;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class VertexComparator implements Comparator<Vertex> {
    
    public VertexComparator(){
    
    }
    
    @Override
    public int compare(Vertex o1, Vertex o2) {
        if(o1.index < o2.index){
            return -1;
        }else if(o1.index == o2.index){
            String o1Hash = o1.toString();
            String o2Hash = o2.toString();
            int val = o1Hash.compareTo(o2Hash);
            if(val == 0)
                return -1;
            return val;
        }else{
            return 1;
        }
    }
    
}
