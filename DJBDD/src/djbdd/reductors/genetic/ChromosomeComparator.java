/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
        if(o1.treeSize <= o2.treeSize){
            return -1;
        }else if(o1.treeSize == o2.treeSize){
            return 0;
        }else{
            return 1;
        }
    }
    
}
