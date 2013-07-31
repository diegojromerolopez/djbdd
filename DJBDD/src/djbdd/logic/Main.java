package djbdd.logic;

import djbdd.*;
import djbdd.io.Printer;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;
import graphvizjava.*;
import java.io.*;
import java.util.*;

public class Main {
    
    private static void trasverse(CommonTree tree) {
        List<CommonTree> children = (List<CommonTree>) tree.getChildren();
        for (CommonTree child : children) {
            if (child.getChildCount() == 0) {
                System.out.println("Leaf " + child.getText());
            } else {
                trasverse(child);
            }
        }
    }
    
  private static BDD createBDD(CommonTree tree, ArrayList<String> variables) {
      int childCount = tree.getChildCount();
      if (childCount == 0) {
          System.out.println("Leaf " + tree.getText());
          return new BDD(tree.getText(), variables, false);
      }
      
      String op = tree.getText();
      System.out.println("OP es "+op);
      List<CommonTree> children = (List<CommonTree>) tree.getChildren();
      ArrayList<BDD> bdds = new ArrayList<BDD>(childCount);
      for (CommonTree child : children) {
          BDD bddI = createBDD(child, variables);
          System.out.println("Función "+bddI.function);
          bdds.add(bddI);
      }
      BDD bdd = bdds.get(0);
      for(int i=1; i<bdds.size(); i++){
          BDD bddI = bdds.get(i);
          bdd = bdd.apply(op, bddI);
      }
      bdd.print();
      return bdd;
    }
    
    
  public static void main(String[] args) throws Exception {

    // the expression
    //String src = "({PXX_H32} || Q || R) && ((P -> R) -> Q)";
    //String src = "(P && Q)";
    //String src = "(WATCHDOG && FOOTBRIDGE) || ((false -> 21285_WATCHDOG) && (21285_WATCHDOG -> false))";
     String src = "((x1 || x2) && (x3 || x4) && x5)"; 
    // create a lexer & parser
    LogicLexer lexer = new LogicLexer(new ANTLRStringStream(src));
    LogicParser parser = new LogicParser(new CommonTokenStream(lexer));

    // invoke the entry point of the parser (the parse() method) and get the AST
    CommonTree tree = (CommonTree)parser.parse().getTree();

    ArrayList<String> variables = new ArrayList<String>();
    variables.add("x1");
    variables.add("x2");
    variables.add("x3");
    variables.add("x4");
    variables.add("x5"); 
    BDD bdd = createBDD(tree, variables);
    System.out.println(bdd.function);
    Printer.printBDD(bdd, "src.png");
    /*
    // print the DOT representation of the AST 
    DOTTreeGenerator gen = new DOTTreeGenerator();
    StringTemplate st = gen.toDOT(tree);
    System.out.println(st);
    System.out.flush();
    PrintWriter out = new PrintWriter("example.dot");
    out.write(st.toString());
    out.close();*/
  }
}
