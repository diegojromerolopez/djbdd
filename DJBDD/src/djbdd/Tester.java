/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import java.util.*;

/**
 *
 * @author diegoj
 */
public class Tester {
    
    public static void test1(){
        String function = "( (!x1 || x2) && (x1 || !x2) ) && ( (!x3 || x4) && (x3 || !x4) )";
        ArrayList<String> variables = new ArrayList<String>();
        variables.add("x1");
        variables.add("x2");
        variables.add("x3");
        variables.add("x4");
        BDD bdd = new BDD(function, variables);
        bdd.print();
        System.out.println(
        "Tree for "+function+"\n"+
        "u	var_i	var	low	high\n"+
        "0	-2		-1	-1\n"+
        "1	-1		-1	-1\n"+
        "2	3	x4	1	0\n"+
        "3	3	x4	0	1\n"+
        "4	2	x3	2	3\n"+
        "5	1	x2	4	0\n"+
        "6	1	x2	0	4\n"+
        "7	0	x1	5	6\n"
        );
    }
    
    public static void test2(){
        ArrayList<String> variables = new ArrayList<String>();
        variables.add("x1");
        variables.add("x2");
        //variables.add("x3");
        //variables.add("x4");
        
        //String function1 = "( (!x1 || x2) && (x1 || !x2) )";
        String function1 = "x1";
        BDD bdd1 = new BDD(function1, variables);
        bdd1.print();//*/
        
        //String function2 = "( (!x3 || x4) && (x3 || !x4) )";
        String function2 = "x2";
        BDD bdd2 = new BDD(function2, variables);
        bdd2.print();
        
        System.out.flush();
        
        BDD bdd = bdd1.apply("and", bdd2);
        bdd.print();
    }
    
    public static void run(int testIndex){
        if(testIndex == 1)
            test1();
        if(testIndex == 2)
            test2();
    }
    
}
