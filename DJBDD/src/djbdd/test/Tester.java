package djbdd.test;

import djbdd.reductors.totalsearch.TotalSearchReductor;
import djbdd.reductors.windowpermutation.WindowPermutationReductor;
import djbdd.reductors.sifting.SiftingReductor;
import djbdd.reductors.genetic.*;
import djbdd.reductors.random.RandomSwapperReductor;
import djbdd.core.BDD;
import djbdd.core.BooleanEvaluator;
import djbdd.io.Printer;
import djbdd.timemeasurer.TimeMeasurer;
import djbdd.reductors.*;
import java.util.*;
import org.mvel2.MVEL;

/**
 * Contains a collection of tests very useful for understand and
 * check if this software package is right.
 * @author diegoj
 */
public class Tester {
    
    /**
     * Waste some memory to force the call of the garbage collector.
     * @param kbs Kilobytes of memory to waste.
     */
    private static void wasteMemory(int kbs){
        byte[] waste1;
        byte[] waste2;
        for(int i=0; i<kbs; i++){
            waste1 = new byte[1000000000];
            waste2 = new byte[1000000000];
            
        }
    }
    
    /**
     * Creates a BDD and prints it as png.
     * @param function Boolean logic function that will be represented by the BDD.
     * @return BDD that will contain the function.
     */
    private static BDD makeBDD(String function){
        // Construction of the BDD1
        BDD bdd = new BDD(function);
        bdd.print();
        //Printer.printBDD(bdd, "makeBDD_"+function+"_");
        return bdd;
    }
    
    /**
     * Test 0: Make some basic operations between two BDDs.
     */
    private static void test0(){
        // Variables of the function (some of them not present)
        String[] vars = {"a","b","c","d","e","f","g","h"};
        ArrayList<String> variables = new ArrayList<String>(Arrays.asList(vars));
        
        // Initializing
        BDD.init(variables);
        
        // Construction of BDD1
        System.out.println("============ BDD1 =============");
        String function1 = "(a || b)";
        BDD bdd1 = makeBDD(function1);
        Printer.printBDD(bdd1, "test0_bdd1_"+bdd1.size()+"_");
        
        // Construction of other BDD
        System.out.println("============ BDD2 =============");
        String function2 = "(f || g)";
        BDD bdd2 = makeBDD(function2);
        Printer.printBDD(bdd2, "test0_bdd2_"+bdd2.size()+"_");
        
        System.out.println("============ BDD3 =============");
        BDD bdd3 = bdd1.apply("and", bdd2);
        bdd3.print();
        Printer.printBDD(bdd3, "test0_bdd3_"+bdd3.size()+"_");
        
        System.out.println("============ BDD4 =============");
        BDD bdd4 = makeBDD(bdd3.function);
        bdd4.print();
        Printer.printBDD(bdd4, "test0_bdd4_"+bdd4.size()+"_");
        
        System.out.println("============ not(BDD1) =============");
        BDD bdd1Comp = bdd1.not();
        bdd1Comp.print();
        Printer.printBDD(bdd1Comp, "test0_not(bdd1)_"+bdd1Comp.size()+"_");

        System.out.println("============ BDD5 =============");
        String function5 = "(!a && !f && g)";
        BDD bdd5 = makeBDD(function5);
        bdd5.print();
        Printer.printBDD(bdd5, "test0_bdd5_"+bdd5.size()+"_");
        
        System.out.println("============ BDD6 =============");
        String function6 = "(!a && !b && !(f || g))";
        BDD bdd6 = makeBDD(function6);
        bdd6.print();
        Printer.printBDD(bdd6, "test0_bdd6_"+bdd6.size()+"_");
        
        System.out.println("============ BDD7 =============");
        BDD bdd7 = bdd1.apply("and", bdd5);
        bdd7.print();
        Printer.printBDD(bdd7, "test0_bdd7_"+bdd7.size()+"_");
        
        BDD.T.gc();
        BDD.T.print();//*/
    }
    
    /**
     * Test 1: a test to study variable ordering importance.
     */
    private static void test1(){
        // A test to study variable order
        String function1 = "(a && b) || (c && d)";
        final String[] variables={"a", "b", "c", "d"};
        BDD.init(variables);
        
        // Small BDD
        BDD bdd1 = new BDD(function1);
        bdd1.print();
        Printer.printBDD(bdd1, "test1_bdd1_"+bdd1.size()+"_");
        
        // Big an inefficient BDD
        String function2 = "(a && c) || (b && d)";
        BDD bdd2 = new BDD(function2);
        bdd2.print();
        Printer.printBDD(bdd2, "test1_bdd2_"+bdd2.size()+"_");
        
        /*
        BDD.gc();
        SiftingReductor r = new SiftingReductor();
        r.run();
        */
    }

    /**
     * Test 2: a test to study the variable reordering of Rudell.
     */
    private static void test2(){
        // A test to study variable order
        String function = "(a && b && c) || (d && f)";
        final String[] variables={"a", "d", "b", "f", "c"};
        BDD.init(variables);
        
        // Big an inefficient BDD
        BDD bdd = new BDD(function);
        bdd.print();
        
        System.out.println("The size is "+bdd.size());
        Printer.printBDD(bdd, "test2_bdd");
        
        BDD.gc();
        SiftingReductor r = new SiftingReductor();
        r.run();        
    }
    

    /**
     * Test 3: test if the BDD construction is all right.
     */
    private static void test3(){
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
    
    /**
     * Test 4: a test of the creation of a BDD with many variables, over 500.
     */
    private static void test4(){
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
    
    /**
     * Test a boolean operation between two boolean values.
     * @param a A boolean value.
     * @param b Other boolean value.
     * @param op The operation. It can any operation like "and", "or", "nor" or "nand".
     * @return Returns the result of operationg a <op> b.
     */
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
    
    /**
     * Test 6: tests boolean operations.
     */
    private static void test5(){
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
    
    /**
     * Test 6: test if tautologies are right with this boolean engine.
     */
    private static void test6(){
        String function = "(false <-> false)";
        boolean res1 = BooleanEvaluator.run(function);
        //boolean res2 = (Boolean)MVEL.eval(function);
        System.out.println("Javaluator\t"+function +" = "+res1);
        //System.out.println("MVEL\t\t"+function+" = "+res2);
    }
    
    /**
     * Test 7: see a BDD created with many variables involved in a disjunction.
     */
    private static void test7(){

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
        Printer.printBDD(bdd1, "test7_bdd1_"+bdd1.size());
     }
    
    /**
     * Test 8: test our restrict operation that extracts a new BDD given some
     * constant values for variables.
     */
    private static void test8(){

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
        Printer.printBDD(bdd1, "test8_bdd1_"+bdd1.size());
        
        HashMap<Integer,Boolean> assignement = new HashMap<Integer,Boolean>();
        assignement.put(2, true);
        assignement.put(3, true);
        BDD bdd2 = bdd1.restrict(assignement);
        Printer.printBDD(bdd2, "test8_bdd2_"+bdd2.size()+" "+assignement.toString()+"");
        bdd2.print(true);
    }
    
    /**
     * Test 9: this example shows how the garbage collection works.
     * When the JVM detects more memory is needed, the Java GC is called.
     * We have to periodically call BDD.gc to erase the empty weak references.
     */
    private static void test9(){
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
        Printer.printBDD(bddRes, "test9_bdd3_"+bddRes.size());
        Printer.printTableT("test9_allbdds_"+BDD.T.size());
    }
    
    /**
     * Test 10: test several operations between "handy" BDDs.
     */
    private static void test10(){
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
        Printer.printBDD(bdd3, "test10_bdd3_"+bdd3.size());
        
        // Multitree
        BDD.gc();
        BDD.T.print();
        Printer.printTableT("test10_allbdds_"+BDD.T.size());
    }
    
    /**
     * Test 11: tests a variable swapping in a BDD.
     */
    private static void test11(){
        String[] variables = {"d", "c", "b", "a"};
        BDD.init(variables);

        // BDD1
        String function1 = "(a && c) || (b && d)";
        BDD bdd1 = new BDD(function1);
        Printer.printBDD(bdd1, "test11_bdd1_BEFORE_"+bdd1.size());
        Printer.printTableT("before");
        
        System.out.println(BDD.T.V);
        
        // Reordering
        System.out.println(function1);
        BDD.gc();
        BDD.T.swap(2);
        Printer.printBDD(bdd1, "test11_bdd1_AFTER_"+bdd1.size());
        Printer.printTableT("after");
        
        // Show the variable hash
        System.out.println(BDD.T.V);
    }
    
    /**
     * Test 12: tests if the swapping interferes with the apply algorigthm.
     * It should not have any problem.
     */
    private static void test12(){
        // We are going to test the variable swapping in the context
        // of the APPLY algorithm
        
        String[] variables = {"d", "c", "b", "a"};
        // Variable order that is gotten by swapping
        //String[] variables = {"d", "b", "c", "a"};
        BDD.init(variables);

        // BDD1
        String function1 = "(a && c) || (b && d)";
        BDD bdd1 = new BDD(function1);

        Printer.printBDD(bdd1, "test12_bdd1_BEFORE_"+bdd1.size());
        
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
        
        Printer.printBDD(bdd1, "test12_bdd1_AFTER_"+bdd1.size());
        
        // BDD2
        String function2 = "(a && b)";
        BDD bdd2 = new BDD(function2);
        Printer.printBDD(bdd2, "test12_bdd2_AFTER_"+bdd2.size());
        
        BDD bddRes = bdd1.apply("and", bdd2);
        Printer.printBDD(bddRes, "test12_bddRes_AFTER_"+bddRes.size());
        
        System.out.println("============= BDDRes ============= ");
        bddRes.print(true);
        Printer.printTableT("test12");
        
        BDD.T.debugPrint();
    }
    
    /**
     * Test 14: this test is used to compare with test12.
     */
    private static void test13(){
        // Variable order that is gotten by swapping in test 12
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
     * Test 14: tests if the swapping interferes with the apply algorigthm.
     * It should not have any problem.
     */
    private static void test14(){
        // We are going to test the variable swapping in the context
        // of the APPLY algorithm
        
        String[] variables = {"a", "b", "c", "d"};
        // Variable order that is gotten by swapping
        //String[] variables = {"d", "b", "c", "a"};
        BDD.init(variables);

        // BDD1
        String function1 = "(a && d) || (b && c)";
        BDD bdd1 = new BDD(function1);

        BDD.gc();
        BDD.T.print();
        
        Printer.printBDD(bdd1, "test14_bdd1_BEFORE_"+bdd1.size());
        
        Printer.printTableT("test14_before");
        SiftingReductor reductor = new SiftingReductor();
        reductor.run();
        
        // The order is now
        // 0, 2, 1, 3
        BDD.variables().print();
        
        System.out.println("FIN");
        BDD.gc();
        BDD.T.print();
        
        Printer.printBDD(bdd1, "test14_bdd1_AFTER_"+bdd1.size());
        
        Printer.printTableT("test14_after");
    }
    
    /**
     * Study about reordering of variables.
     * Uses the Ruddel's sifting algorithm
     */
    private static void test15(){
    
        //String[] variables = {"x1", "y1", "x2", "y2", "x3", "y3", "x4", "y4"};
        String[] variables = {"x1", "y1", "x2", "y2", "x3", "x4"};
        BDD.init(variables);

        // BDD1
        //String function1 = "(x1 && x2 && x3 && x4) || (y1 && y2 && y3 && y4)";
        String function1 = "(x1 && x2 && x3 && x4) || (y1 && y2)";
        BDD bdd1 = new BDD(function1);
        
        System.out.println("\nBEFORE the reordering");
        System.out.println("Size of bdd1: "+bdd1.size());
        System.out.println("Variables before the reordering algorithm");
        BDD.variables().print();
        
        // Clean the orphan nodes and print the non-optimal tree
        BDD.gc();
        Printer.printTableT("test15_before");
        
        // Reduce the tree
        SiftingReductor reductor = new SiftingReductor();
        reductor.run();
        
        System.out.println("\nAFTER the reordering");
        System.out.println("Size of bdd1: "+bdd1.size());
        System.out.println("Variables after the reordering algorithm");
        BDD.variables().print();
        
        // Clean the orphan nodes and print the optimal tree
        BDD.gc();
        Printer.printTableT("test15_after");
    }
    
    /**
     * Study about swapping of variables.
     * Uses the Ruddel's swapping
     */
    private static void test16(){
    
        //String[] variables = {"x1", "y1", "x2", "y2", "x3", "y3", "x4", "y4"};
        String[] variables = {"x1", "x2", "y1", "y2", "x4", "x3"};
        BDD.init(variables);

        // BDD1
        //String function1 = "(x1 && x2 && x3 && x4) || (y1 && y2 && y3 && y4)";
        String function1 = "(x1 && x2 && x3 && x4) || (y1 && y2)";
        BDD bdd1 = new BDD(function1);
        
        System.out.println("\nBEFORE the reordering");
        System.out.println("Size of bdd1: "+bdd1.size());
        System.out.println("Variables before the reordering algorithm");
        BDD.variables().print();
        
        // Clean the orphan nodes and print the non-optimal tree
        BDD.gc();
        Printer.printTableT("test16_before");
        
        // Reduce the tree
        //SiftingReductor reductor = new SiftingReductor();
        //reductor.run();
        
        BDD.T.swap(2);
        
        System.out.println("\nAFTER the reordering");
        System.out.println("Size of bdd1: "+bdd1.size());
        System.out.println("Variables after the reordering algorithm");
        BDD.variables().print();
        
        // Clean the orphan nodes and print the optimal tree
        BDD.gc();
        Printer.printTableT("test16_after");
    }
    
    public static String getWorstOrderFormula(int numVariables){
        String function1 = "";
        for(int i=1; i<numVariables; i+=2){
            function1 += "{x"+i+"} && ";
        }
        function1 = function1.substring(0, function1.length()-3);
        
        String function2 = "";
        for(int i=0; i<numVariables-1; i+=2){
            function2 += "{x"+i+"} && ";
        }

        function2 = function2.substring(0, function2.length()-3);
        
        // The logic function is:
        // (Conjunction of even variables) OR (Conjunction of odd variables)
        // i. e. {x2} && {x4} && ... && {x20} ) || ({x1} && {x3} && ... && {x17} && {x19}
        String function = "("+function1+") || ("+function2+")";
        return function;
    }
    
    public static String getProblematicFormula(){
        String s = "";
        
        //s += "(!{x10} || !{x1} || {x11}) && ";
        s += "(!{x10} || !{x3} || {x13}) && ";
        s += "(!{x10} || !{x4} || {x14}) && ";
        s += "(!{x15} || !{x1} || {x16}) && ";
        s += "(!{x15} || !{x2} || {x17}) && ";
        s += "(!{x15} || !{x4} || {x19}) && ";
        s += "(!{x5} || !{x20} || !{x6}) && ";
        s += "(!{x10} || !{x22} || !{x12}) && ";
        s += "(!{x15} || !{x24} || !{x18}) && ";
        s += "(!{x20} || {x27}) && ";
        s += "(!{x21} || {x28}) && ";
        s += "(!{x22} || {x29}) && ";
        s += "(!{x23} || {x30}) && ";
        s += "(!{x24} || {x31})  ";
        /*s += "(!{x25} || {x32}) &&";
        /*s += "(!{x1} || !{x28}) && ";
        s += "(!{x2} || !{x30}) && ";
        s += "(!{x3} || !{x32}) && ";
        s += "(!{x5} || !{x22}) && ";
        s += "(!{x5} || !{x23}) && ";
        s += "(!{x5} || !{x24}) && ";
        s += "(!{x5} || !{x25}) && ";
        s += "(!{x10} || !{x20}) && ";
        s += "(!{x10} || !{x21}) && ";
        s += "(!{x10} || !{x24}) && ";
        s += "(!{x10} || !{x25}) && ";
        s += "(!{x10} || !{x26}) && ";
        s += "(!{x15} || !{x20}) && ";
        s += "(!{x15} || !{x21}) && ";
        s += "(!{x15} || !{x22}) && ";
        s += "(!{x15} || !{x23}) && ";
        s += "(!{x15} || !{x26}) && ";
        s += "({x10} || {x12}) && ";
        s += "({x15} || {x18})";*/
       return s;
    }
    
    /**
     * Study about swapping of variables.
     * Uses the Ruddel's swapping.
     * 
     */
    private static void test17(){
        // Setup the variables
        int numVariables = 50;
        ArrayList<String> variables = new ArrayList<String>(numVariables);
        for(int i=0; i<numVariables; i++){
            variables.add("{x"+i+"}");
        }
        
        //String function = getWorstOrderFormula(numVariables);
        String function = getProblematicFormula();

        // Show it
        System.out.println(function);
        
        // Initializing the system
        BDD.init(variables);
        
        // BDD1
        BDD bdd1 = new BDD(function);
        
        // Shows the old, non-optimal BDD
        int oldSize = bdd1.size();
        System.out.println("\nBEFORE the reordering");
        System.out.println("Size of bdd1: "+oldSize);
        System.out.println("Size of T: "+BDD.T.size());
        System.out.println("Variables before the reordering algorithm");
        BDD.variables().print();
        
        // Clean the orphan nodes and print the non-optimal tree
        BDD.gc();
        //Printer.printTableT("test17_before");
        
        // Reduction type
        int reductionType = ReductionAlgorithm.SIFTING_ALGORIGHTM;
        
        System.out.println("");
        
        random.Random.init(10);
        
        // Reduce the tree
        ReductionAlgorithm reductor = null;
        if(reductionType == ReductionAlgorithm.SIFTING_ALGORIGHTM){
            System.out.println("Sifting reductor");
            reductor = new SiftingReductor();
        }else if(reductionType == ReductionAlgorithm.TOTAL_SEARCH){
            System.out.println("Executing exact reductor");
            reductor = new TotalSearchReductor();
        }else if(reductionType == ReductionAlgorithm.WINDOW_PERMUTATION){
            System.out.println("Executing window permutation algorithm");
            int windowSize = 2;
            reductor = new WindowPermutationReductor(windowSize);
        }else if(reductionType == ReductionAlgorithm.GENETIC_ALGORITHM){
            System.out.println("Genetic algorithm");
            int populationSize = 15;
            int generations = 2;
            double selectionPercentage = 0.20;
            double mutationProbability = 0.50;
            reductor = new GeneticReductor(populationSize, generations, selectionPercentage, mutationProbability);
        }else if(reductionType == ReductionAlgorithm.MEMETIC_ALGORITHM){
            System.out.println("Memetic algorithm");
            int populationSize = 100;
            int generations = 1;
            double selectionPercentage = 0.20;
            double mutationProbability = 0.10;
            reductor = new MemeticReductor(populationSize, generations, selectionPercentage, mutationProbability);            
        }else if(reductionType == ReductionAlgorithm.RANDOM_SWAP_ALGORITHM){
            System.out.println("Random swapper algorithm");
            int iterations = 1000;
            reductor = new RandomSwapperReductor(iterations);
        }else{
            System.err.println("Method not selected");
        }
        reductor.run();
        
        // Shows the new BDD
        int newSize = bdd1.size();
        System.out.println("\nAFTER the reordering");
        System.out.println("Size of bdd1: "+bdd1.size());
        System.out.println("Size of T: "+BDD.T.size());
        System.out.println("Variables after the reordering algorithm");
        BDD.variables().print();
        
        // Clean the orphan nodes and print the optimal tree
        BDD.gc();
        BDD.T.testNumParents();        
        BDD.T.print();
        
        //Printer.printTableT("test17_after");
        Printer.printBDD(bdd1, "test17_bdd1_"+bdd1.size()+"_after");
        
        // Shows how many vertices we are reducing
        int reduction = oldSize-newSize;
        double reductionPercentage = (100.0 - (newSize*100.0) / (oldSize*1.0));
        System.out.println("\n-----------------------------------------------");
        System.out.println("Reduce the size of the BDD in "+reduction+" vertices "+"(Reduction in "+reductionPercentage+"%)");
    }
    
    public static void test18(){
        int numVariables = 50;
        ArrayList<String> variables = new ArrayList<String>(numVariables);
        for(int i=0; i<numVariables; i++){
            variables.add("{x"+i+"}");
        }
        //String function = getWorstOrderFormula(numVariables);
        String function = getProblematicFormula();
        
        // Show it
        System.out.println(function);
        
        // Initializing the system
        BDD.init(variables);
        
        // BDD1
        BDD bdd1 = new BDD(function);
        
        // Shows the old, non-optimal BDD
        int oldSize = bdd1.size();
        System.out.println("\nBEFORE the swap");
        System.out.println("Size of bdd1: "+oldSize);
        System.out.println("Size of T: "+BDD.T.size());
        System.out.println("Variables before the reordering algorithm");
        BDD.variables().print();
        
        // Clean the orphan nodes and print the non-optimal tree
        BDD.gc();
        //Printer.printTableT("test18_before");
        
        BDD.T.swap(10);
        
        int newSize = bdd1.size();
        System.out.println("\nAFTER the swap");
        System.out.println("Size of bdd1: "+newSize);
        System.out.println("Size of T: "+BDD.T.size());
        System.out.println("Variables before the reordering algorithm");
        BDD.variables().print();
        
        //Printer.printTableT("test18_after");
    }
    
    /**************************************************************************/
    /**************************************************************************/
    /**************************************************************************/
    
    /**
     * Ejecuta un test determinado.
     */
    public static void run(int testIndex){
        if (testIndex == 0)
            test0();
        else if (testIndex == 1)
            test1();
        else if (testIndex == 2)
            test2();
        else if (testIndex == 3)
            test3();
        else if (testIndex == 4)
            test4();
        else if (testIndex == 5)
            test5();
        else if (testIndex == 6)
            test6();
        else if (testIndex == 7)
            test7();
        else if (testIndex == 8)
            test8();
        else if (testIndex == 9)
            test9();
        else if (testIndex == 10)
            test10();
        else if (testIndex == 11)
            test11();
        else if (testIndex == 12)
            test12();
        else if (testIndex == 13)
            test13();
        else if (testIndex == 14)
            test14();
        else if (testIndex == 15)
            test15();
        else if (testIndex == 16)
            test16();
        else if (testIndex == 17){
            test17();
        }else if (testIndex == 18){
            test18();
        }else {
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
