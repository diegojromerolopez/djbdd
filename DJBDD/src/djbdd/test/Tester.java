/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.test;

import djbdd.io.Printer;
import djbdd.timemeasurer.TimeMeasurer;
import djbdd.*;
import djbdd.reductors.*;
import java.util.*;
import org.mvel2.MVEL;

/**
 *
 * @author diegoj
 */
public class Tester {
    
    static String memoryWasted;
    
    private static void wasteMemory(int kbs){
        memoryWasted = "xxx";
        byte[] waste1;
        byte[] waste2;
        for(int i=0; i<kbs; i++){
            waste1 = new byte[1000000000];
            waste2 = new byte[1000000000];
            
        }
    }
    
    private static BDD makeBDD(String function){
        // Construction of the BDD1
        BDD bdd = new BDD(function);//, variable_ordering);
        bdd.print();
        Printer.printBDD(bdd, "makeBDD_"+function+"_");
        return bdd;
    }
    
    public static void test0(){
        // Variables of the function (some of them not present)
        ArrayList<String> variables = new ArrayList<String>();
        variables.add("a");
        variables.add("b");
        variables.add("c");
        variables.add("d");
        variables.add("e");
        variables.add("f");
        variables.add("g");
        variables.add("h");
        // Variable ordering (pray that's right)
        ArrayList<Integer> variable_ordering = new ArrayList<Integer>();
        variable_ordering.add(2);// c
        variable_ordering.add(3);// d
        variable_ordering.add(0);// a
        variable_ordering.add(1);// b
        variable_ordering.add(4);// e
        variable_ordering.add(5);// f
        variable_ordering.add(6);// g
        variable_ordering.add(7);// h
        
        // Initializing
        BDD.init(variables);
        
        // Construction of the BDD1
        String function1 = "(a || b)";
        BDD bdd1 = makeBDD(function1);
        
        // Construction of other BDD2
        String function2 = "(f || g)";
        BDD bdd2 = makeBDD(function2);
        
        BDD bdd3 = bdd1.apply("and", bdd2);
        bdd3.print();
        Printer.printBDD(bdd3, "test0_bdd3_"+bdd3.size()+"_");
        
        BDD bdd4 = makeBDD(bdd3.function);
        bdd4.print();
        Printer.printBDD(bdd4, "test0_bdd4_"+bdd4.size()+"_");
        
        BDD.T.gc();
        BDD.T.print();//*/
    }
    
    public static void test1(){
        // A test to study variable order
        String function = "(a_ && b_) || (c_ && d_)";
        final String[] variables={"a_", "b_", "c_", "d_"};
        BDD.init(variables);
        
        // Small BDD
        final String[] variable_ordering1={"a_", "b_", "c_", "d_"};
        BDD bdd1 = new BDD(function);
        bdd1.print();
        Printer.printBDD(bdd1, "test1_bdd1_"+bdd1.size()+"_");
        
        // Big an inefficient BDD
        final String[] variable_ordering2={"c_", "a_", "d_", "b_"};
        BDD bdd2 = new BDD(function);
        bdd2.print();
        Printer.printBDD(bdd2, "test1_bdd2_"+bdd2.size()+"_");
        
        /*
        // Heuristic BDD
        String[] variable_orderingH={"c_", "a_", "e_", "b_", "f_", "d_"};
        BDD bddMin = null;
        int iterations = 1000;
        int i = 0;
        while(i< iterations){
            BDD bddH = new BDD(function, variable_orderingH, useApplyInCreation);
            if(bddMin == null || bddH.size() < bddMin.size()){
                bddMin = bddH;
            }
            i++;
            Collections.shuffle( Arrays.asList(variable_orderingH) );
        }
        bddMin.print();
        Printer.printBDD(bddMin, "test1_bddMin_"+bddMin.size()+"_"+bddMin.variable_ordering.toString());
         * 
         */
    }
    
    public static void test2(){
        // A test to study variable order
        String function = "(a_ && b_) || (c_ && d_)";
        //final String[] variables={"a_", "c_", "b_", "d_"};
        final String[] variables={"a_", "c_", "b_", "d_"};
        BDD.init(variables);
        
        // Big an inefficient BDD
        BDD bdd = new BDD(function);
        bdd.print();
        Printer.printBDD(bdd, "test2_bdd_"+bdd.size());
        
        // Swapping
        BDDSiftingReduce.siftOrder();
        //Printer.printBDD(bdd, "test2_bddswapped_"+bdd.size());
        
        /*if(false){
            int var_i = 1;//c
            BDD.T.swap(var_i);
            Printer.printBDD(bdd, "test2_xbdd1_"+bdd.size());
            BDD.T.swap(var_i);
            Printer.printBDD(bdd, "test2_xbdd2_"+bdd.size());
        }*/
    }
    
   public static void test3(){
        // Un test para estudiar el orden de las variables (queremos hacer una heurística)
        String function = "(((PPC?  || MAC?) && (ADB? && MAC?)) || ((false -> ADB_IOP?) && (ADB_IOP? -> false)))";
        //String function = "(( (PPC?  || MAC?) ))";
        final String[] variables={"PPC?", "MAC?", "ADB?", "ADB_IOP?"};
        BDD.init(variables);
        BDD bdd1 = new BDD(function);
        bdd1.print();
        Printer.printBDD(bdd1, "test1_bdd1_"+bdd1.size()+"_");      
   }
    
    public static void test4(){
        String function = "( (!x1 || x2) && (x1 || !x2) ) && ( (!x3 || x4) && (x3 || !x4) )";
        ArrayList<String> variables = new ArrayList<String>();
        variables.add("x1");
        variables.add("x2");
        variables.add("x3");
        variables.add("x4");
        BDD.init(variables);

        BDD bdd = new BDD(function);
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
    
    public static void test5(){
        TimeMeasurer t = new TimeMeasurer("test4", true);

        int num_variables = 1000;
        ArrayList<String> variables = new ArrayList<String>();
        String function1 = "x1";
        for(int i=2; i<num_variables; i++){
            variables.add("x"+i);
            if(i%2==0)
                function1 += " || x"+i;
            else
                function1 += " && x"+i;
        }
        
        // Initialize the variables
        BDD.init(variables);
        
        // First operand
        function1 = "("+function1+")";
        BDD bdd1 = new BDD(function1);
        bdd1.print();//*/
        
        // Second operand
        String function2 = "(x3 && x4)";
        BDD bdd2 = new BDD(function2);
        bdd2.print();
        
        // Result     
        BDD bddRes = bdd1.apply("and", bdd2);
        bddRes.print();
        
        
        String function = "("+function1+" && "+function2+")";
        BDD bdd = new BDD(function);
        bdd.print();//*/
        
        String file = "test4.bdd.txt";
        bdd.toFile(file);
        BDD bddLoaded = BDD.fromFile(file);
        bddLoaded.print();
        t.end().show();
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
    
    public static void test6(){
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
    
    public static void test7(){
        String function = "(false <-> false)";
        boolean res1 = BooleanEvaluator.run(function);
        //boolean res2 = (Boolean)MVEL.eval(function);
        System.out.println("Javaluator\t"+function +" = "+res1);
        //System.out.println("MVEL\t\t"+function+" = "+res2);
    }
    
    public static void test8(){

        int num_variables = 3;
        ArrayList<String> variables = new ArrayList<String>();
        String function1 = "x0";
        variables.add("x0");
        for(int i=1; i<num_variables; i++){
            variables.add("x"+i);
            function1 += " || x"+i;
        }
        
        // First operand
        function1 = "("+function1+")";
        BDD.init(variables);
        BDD bdd1 = new BDD(function1);
        bdd1.print();//*/
        BDD.T.print();
        BDD.T.gc();
        Printer.printBDD(bdd1, "test8_bdd1_"+bdd1.size());
     }
    
    public static void test9(){

        int num_variables = 5;
        ArrayList<String> variables = new ArrayList<String>();
        String function1 = "x0";
        variables.add("x0");
        for(int i=1; i<num_variables; i++){
            variables.add("x"+i);
            function1 += " || x"+i;
        }
        
        // First operand
        function1 = "("+function1+")";
        BDD.init(variables);
        BDD bdd1 = new BDD(function1);
        bdd1.print();//*/
        BDD.T.print();
        Printer.printBDD(bdd1, "test9_bdd1_"+bdd1.size());
        
        HashMap<Integer,Boolean> assignement = new HashMap<Integer,Boolean>();
        assignement.put(2, true);
        assignement.put(3, true);
        BDD bdd2 = bdd1.restrict(assignement);
        Printer.printBDD(bdd2, "test9_bdd2_"+bdd2.size()+" "+assignement.toString()+"");
        bdd2.print(true);
    }
    
    /**
     * This example shows how the garbage collection works.
     * When the JVM detects more memory is needed, the Java GC is called.
     * We have to periodically call BDD.gc to erase the empty weak references.
     */
    private static void test10(){
        // Variable initialization
        int num_variables = 15;
        ArrayList<String> variables = new ArrayList<String>();
        for(int i=1; i<=num_variables; i++){
            variables.add("x"+i);
        }        
        BDD.init(variables);
        
        // First BDD
        BDD bdd1 = new BDD("(x1 && x2) || (x3 && x4) || (x5 && x6)");
        bdd1.print(true);
        
        // Second BDD
        BDD bdd2 = new BDD("(x6 && x7) || (x7 && x8) || (x9 && x6)");
        bdd2.print(true);
        
        // The OR BDD
        BDD bddRes = bdd1.apply("or", bdd2);
        bddRes.print(true);
        
        // Destroy BDD1 and BDD2
        bdd1 = null;
        bdd2 = null;
        
        // Waste some memory to force the garbage collection calling
        wasteMemory(10);
        
        // Sleep some time to give the gc time to erase memory
        try{
            Thread.sleep(2000);
        }catch(Exception e){
            // There will be no exception
        }
        
        // Erase of the empty weak references
        BDD.gc();
        
        // This two graphs must have the same size and be the same
        Printer.printBDD(bddRes, "test10_bdd3_"+bddRes.size());
        Printer.printTableT("test10_allbdds_"+BDD.T.size());
    }
    
    private static void test11(){
        String[] variables = {"f", "g", "a", "b", "c", "d", "e"};
        BDD.init(variables);

        //String[] variableOrdering = {"a","c", "f", "g", "d", "e", "b"};
        //BDD.initVariableOrdering(variableOrdering);
        
        // BDD1
        String function1 = "(a && c)";
        BDD bdd1 = new BDD(function1);
        //Printer.printBDD(bdd1, "test11_bdd1_"+bdd1.size());
        
        // BDD2
        String function2 = "(a && f && g)";
        BDD bdd2 = new BDD(function2);
        //Printer.printBDD(bdd2, "test11_bdd2_"+bdd2.size());
        
        /// BDDRes

        BDD bdd3 = bdd1.apply("and", bdd2);
        bdd1 = null;
        bdd2 = null;
        Printer.printBDD(bdd3, "test11_bdd3_"+bdd3.size());
        
        // Multitree
        BDD.gc();
        BDD.T.print();
        Printer.printTableT("test11_allbdds_"+BDD.T.size());
    }
    
    private static void test12(){
        String[] variables = {"d", "c", "b", "a"};
        BDD.init(variables);

        // BDD1
        String function1 = "(a && c) || (b && d)";
        BDD bdd1 = new BDD(function1);
        Printer.printBDD(bdd1, "test12_bdd1_BEFORE_"+bdd1.size());
        Printer.printTableT("before");
        
        System.out.println(BDD.T.V);
        
        // Reordering
        System.out.println(function1);
        BDD.gc();
        BDD.T.swap(2);
        Printer.printBDD(bdd1, "test12_bdd1_AFTER_"+bdd1.size());
        Printer.printTableT("after");
        
        // Show the variable hash
        System.out.println(BDD.T.V);
    }
    
    /**
     * Tests if the swapping interferes with the apply algorigthm.
     * It should not have any problem.
     */
    private static void test13(){
        // We are going to test the variable swapping in the context
        // of the APPLY algorithm
        
        String[] variables = {"d", "c", "b", "a"};
        // Variable order that is gotten by swapping
        //String[] variables = {"d", "b", "c", "a"};
        BDD.init(variables);

        // BDD1
        String function1 = "(a && c) || (b && d)";
        BDD bdd1 = new BDD(function1);

        Printer.printBDD(bdd1, "test13_bdd1_BEFORE_"+bdd1.size());
        
        BDD.T.swap(1);
        BDD.gc();

        // The order is now
        // 0, 2, 1, 3
        BDD.variables().print();
        
        BDD.T.print();
        
        // Test each posible order comparison
        // 0
        System.out.println("0 < 1 "+BDD.variables().variableComesBeforeThan(0, 1)+" ==? TRUE");
        System.out.println("0 < 2 "+BDD.variables().variableComesBeforeThan(0, 2)+" ==? TRUE");
        System.out.println("0 < 3 "+BDD.variables().variableComesBeforeThan(0, 3)+" ==? TRUE");
        // 1
        System.out.println("1 < 0 "+BDD.variables().variableComesBeforeThan(1, 0)+" ==? FALSE");
        System.out.println("1 < 2 "+BDD.variables().variableComesBeforeThan(1, 2)+" ==? FALSE");
        System.out.println("1 < 3 "+BDD.variables().variableComesBeforeThan(1, 3)+" ==? TRUE");
        // 2
        System.out.println("2 < 0 "+BDD.variables().variableComesBeforeThan(2, 0)+" ==? FALSE");
        System.out.println("2 < 1 "+BDD.variables().variableComesBeforeThan(2, 1)+" ==? TRUE");
        System.out.println("2 < 3 "+BDD.variables().variableComesBeforeThan(2, 3)+" ==? TRUE");
        // 3
        System.out.println("3 < 0 "+BDD.variables().variableComesBeforeThan(3, 0)+" ==? FALSE");
        System.out.println("3 < 1 "+BDD.variables().variableComesBeforeThan(3, 1)+" ==? FALSE");
        System.out.println("3 < 2 "+BDD.variables().variableComesBeforeThan(3, 2)+" ==? FALSE");
        
        
        System.out.println("0 > 1 "+BDD.variables().variableComesAfterThan(0, 1)+" ==? FALSE");
        System.out.println("0 > 2 "+BDD.variables().variableComesAfterThan(0, 2)+" ==? FALSE");
        System.out.println("0 > 3 "+BDD.variables().variableComesAfterThan(0, 3)+" ==? FALSE");
        // 1
        System.out.println("1 > 0 "+BDD.variables().variableComesAfterThan(1, 0)+" ==? TRUE");
        System.out.println("1 > 2 "+BDD.variables().variableComesAfterThan(1, 2)+" ==? TRUE");
        System.out.println("1 > 3 "+BDD.variables().variableComesAfterThan(1, 3)+" ==? FALSE");
        // 2
        System.out.println("2 > 0 "+BDD.variables().variableComesAfterThan(2, 0)+" ==? TRUE");
        System.out.println("2 > 1 "+BDD.variables().variableComesAfterThan(2, 1)+" ==? FALSE");
        System.out.println("2 > 3 "+BDD.variables().variableComesAfterThan(2, 3)+" ==? FALSE");
        // 3
        System.out.println("3 > 0 "+BDD.variables().variableComesAfterThan(3, 0)+" ==? TRUE");
        System.out.println("3 > 1 "+BDD.variables().variableComesAfterThan(3, 1)+" ==? TRUE");
        System.out.println("3 > 2 "+BDD.variables().variableComesAfterThan(3, 2)+" ==? TRUE");
        
        Printer.printBDD(bdd1, "test13_bdd1_AFTER_"+bdd1.size());
        
        // BDD2
        String function2 = "(a && b)";
        BDD bdd2 = new BDD(function2);
        Printer.printBDD(bdd2, "test13_bdd2_AFTER_"+bdd2.size());
        
        BDD bddRes = bdd1.apply("and", bdd2);
        Printer.printBDD(bddRes, "test13_bddRes_AFTER_"+bddRes.size());
        
        System.out.println("============= BDDRes ============= ");
        bddRes.print(true);
        Printer.printTableT("test13");
        
        BDD.T.debugPrint();
    }
    
    /**
     * This test is used to compare with test13.
     */
    public static void test14(){
        // Variable order that is gotten by swapping in test 13
        String[] variables = {"d", "b", "c", "a"};
        BDD.init(variables);
        
        // BDD1
        String function1 = "(a && c) || (b && d)";
        BDD bdd1 = new BDD(function1);
        Printer.printBDD(bdd1, "test14_bdd1_"+bdd1.size());
        
        // BDD2
        String function2 = "(a && b)";
        BDD bdd2 = new BDD(function2);    
        Printer.printBDD(bdd2, "test14_bdd2_"+bdd2.size());
        
        BDD bddRes = bdd1.apply("and", bdd2);
        Printer.printBDD(bddRes, "test14_bddRes_"+bddRes.size());
        
        System.out.println("============= BDDRes ============= ");
        bddRes.print(true);
        Printer.printTableT("test14");
        
        BDD.T.debugPrint();
    }
    
        /**
     * Tests if the swapping interferes with the apply algorigthm.
     * It should not have any problem.
     */
    private static void test15(){
        // We are going to test the variable swapping in the context
        // of the APPLY algorithm
        
        String[] variables = {"a", "b", "c", "d"};
        // Variable order that is gotten by swapping
        //String[] variables = {"d", "b", "c", "a"};
        BDD.init(variables);

        // BDD1
        String function1 = "(a && d) || (b && c)";
        BDD bdd1 = new BDD(function1);

        Printer.printBDD(bdd1, "test15_bdd1_BEFORE_"+bdd1.size());
        
        SiftingReductor reductor = new SiftingReductor();
        reductor.run();
        
        // The order is now
        // 0, 2, 1, 3
        BDD.variables().print();
        
        BDD.T.print();
        
        Printer.printBDD(bdd1, "test15_bdd1_AFTER_"+bdd1.size());
        
        Printer.printTableT("test15_at_the_end");
    }
    
    public static void run(int testIndex){
        if(testIndex == 0)
            test0();
        else if(testIndex == 1)
            test1();
        else if(testIndex == 2)
            test2();
        else if(testIndex == 3)
            test3();
        else if(testIndex == 4)
            test4();
        else if(testIndex == 5)
            test5();
        else if(testIndex == 6)
            test6();
        else if(testIndex == 7)
            test7();
        else if(testIndex == 8)
            test8();
        else if(testIndex == 9)
            test9();
        else if(testIndex == 10)
            test10();
        else if(testIndex == 11)
            test11();
        else if(testIndex == 12)
            test12();
        else if(testIndex == 13)
            test13();
        else if(testIndex == 14)
            test14();
        else if(testIndex == 15)
            test15();
        else {
            System.err.println("This test does NOT exists");
            System.exit(-1);
        }
    }
    
    /**
     * Main class.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length<1){
            System.err.println("Use java -jar DJBDD.jar Tester <ID TEST>");
            System.exit(-1);
        }
        // Run the test 
        Tester.run(Integer.parseInt(args[0]));
    
    }
    
}
