/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.test;

import djbdd.io.BDDPrinter;
import djbdd.*;
import java.util.*;
import org.mvel2.MVEL;

/**
 *
 * @author diegoj
 */
public class Tester {
    
    public static void test0(){
        // Function
        String function = "(a || b) && c && d";
        // Variables of the function (some of them not present)
        ArrayList<String> variables = new ArrayList<String>();
        variables.add("a");
        variables.add("b");
        variables.add("c");
        variables.add("d");
        variables.add("e");
        variables.add("f");
        // Variable ordering (pray that's right)
        ArrayList<Integer> variable_ordering = new ArrayList<Integer>();
        variable_ordering.add(2);// c
        variable_ordering.add(3);// d
        variable_ordering.add(0);// a
        variable_ordering.add(1);// b
        variable_ordering.add(4);// e
        variable_ordering.add(5);// f
        // Construction of the BDD
        BDD bdd = new BDD(function, variables, variable_ordering);
        bdd.print();
        BDDPrinter.printBDD(bdd, "test0_"+bdd.variable_ordering.toString());
    }
    
    public static void test1(){
        // Un test para estudiar el orden de las variables
        String function = "(a_ && b_) || (c_ && d_) || (e_ && f_)";
        final String[] variables={"a_", "b_", "c_", "d_", "e_", "f_"};
        // Small BDD
        final String[] variable_ordering1={"a_", "b_", "c_", "d_", "e_", "f_"};
        BDD bdd1 = new BDD(function, variables, variable_ordering1);
        bdd1.print();
        BDDPrinter.printBDD(bdd1, "test1_bdd1_"+bdd1.size()+"_"+bdd1.variable_ordering.toString());
        // Big an inefficient BDD
        final String[] variable_ordering2={"c_", "a_", "e_", "b_", "f_", "d_"};
        BDD bdd2 = new BDD(function, variables, variable_ordering2);
        bdd2.print();
        BDDPrinter.printBDD(bdd2, "test1_bdd2_"+bdd2.size()+"_"+bdd2.variable_ordering.toString());
        // Heuristic BDD
        String[] variable_orderingH={"c_", "a_", "e_", "b_", "f_", "d_"};
        BDD bddMin = null;
        int iterations = 1000;
        int i = 0;
        while(i< iterations){
            BDD bddH = new BDD(function, variables, variable_orderingH);
            if(bddMin == null || bddH.size() < bddMin.size()){
                bddMin = bddH;
            }
            i++;
            Collections.shuffle( Arrays.asList(variable_orderingH) );
        }
        bddMin.print();
        BDDPrinter.printBDD(bddMin, "test1_bddMin_"+bddMin.size()+"_"+bddMin.variable_ordering.toString());
    }
    
   public static void test2(){
        // Un test para estudiar el orden de las variables (queremos hacer una heurística)
        String function = "(((PPC?  || MAC?) && (ADB? && MAC?)) || ((false -> ADB_IOP?) && (ADB_IOP? -> false)))";
        //String function = "(( (PPC?  || MAC?) ))";
        final String[] variables={"PPC?", "MAC?", "ADB?", "ADB_IOP?"};
        // Small BDD
        final String[] variable_ordering1={"PPC?", "MAC?", "ADB?", "ADB_IOP?"};
        BDD bdd1 = new BDD(function, variables, variable_ordering1);
        bdd1.print();
        BDDPrinter.printBDD(bdd1, "test1_bdd1_"+bdd1.size()+"_"+bdd1.variable_ordering.toString());      
   }
    
    public static void test3(){
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
    
    public static void test4(){
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
    
    private static boolean testBooleanOperation(Boolean a, Boolean b, String op){
        String _function1 = a.toString()+" "+op+" "+b.toString();
        String _function2 = a.toString()+" "+op+" "+b.toString();
        if(op.equals("->"))
            _function2 = "!"+a.toString()+" || "+b.toString();
        boolean res1 = BooleanEvaluator.run(_function1);
        boolean res2 = (Boolean)MVEL.eval(_function2);
        System.out.println("Javaluator\t"+_function1 +" = "+res1);
        System.out.println("MVEL\t\t"+_function2+" = "+res2);
        if(res1!=res2)
        {
            System.out.println("WRONG");
            System.exit(-1);
        }
        return res1==res2;
    }
    
    public static void test5(){
        System.out.println("--- AND ---");
        for(int i=0; i<=1; i++){
            for(int j=0; j<=1; j++){
                testBooleanOperation(i==0, j==0, "||");
            }
        }
        System.out.println("--- OR ---");
        for(int i=0; i<=1; i++){
            for(int j=0; j<=1; j++){
                testBooleanOperation(i==0, j==0, "&&");
            }
        }
        System.out.println("--- IMP ---");
        for(int i=0; i<=1; i++){
            for(int j=0; j<=1; j++){
                testBooleanOperation(i==0, j==0, "->");
            }
        }
    }
    
    public static void test6(){
  
    }
    
    public static void run(int testIndex){
        if(testIndex == 0)
            test0();
        if(testIndex == 1)
            test1();
        if(testIndex == 2)
            test2();
        if(testIndex == 3)
            test3();
        if(testIndex == 4)
            test4();
        if(testIndex == 5)
            test5();
        if(testIndex == 6)
            test6();
    }
    
}
