package djbdd.logic;

import djbdd.*;
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
    String src = "(WATCHDOG && FOOTBRIDGE) || ((false -> 21285_WATCHDOG) && (21285_WATCHDOG -> false))";
    // create a lexer & parser
    LogicLexer lexer = new LogicLexer(new ANTLRStringStream(src));
    LogicParser parser = new LogicParser(new CommonTokenStream(lexer));

    // invoke the entry point of the parser (the parse() method) and get the AST
    CommonTree tree = (CommonTree)parser.parse().getTree();

    //trasverse(tree);
    
    ArrayList<String> variables = new ArrayList<String>();
    variables.add("WATCHDOG");
    variables.add("FOOTBRIDGE");
    variables.add("21285_WATCHDOG");
    variables.add("P");    
    BDD bdd = createBDD(tree, variables);
    System.out.println(bdd.function);
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
