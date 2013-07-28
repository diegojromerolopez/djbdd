/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd.logic;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
//import org.antlr.stringtemplate.*;

/**
 *
 * @author diegoj
 */
public class FormulaParser {
    
    private final String formula;
    
    public FormulaParser(String formula){
        this.formula = formula;
    }
    
    public CommonTree run() throws Exception {
        // create a lexer & parser
        LogicLexer lexer = new LogicLexer(new ANTLRStringStream(this.formula));
        LogicParser parser = new LogicParser(new CommonTokenStream(lexer));

        // invoke the entry point of the parser (the parse() method) and get the AST
        CommonTree tree = (CommonTree)parser.parse().getTree();    
        return tree;
    }
    
}
