package checker.parser;

import checker.ast.Formula;
import checker.error.SyntacticException;
import java.util.Stack;
import java.io.IOException;
import java.io.InputStream;

/** Parses <code>Token</code>s, representing keywords and values in the
 * input language into an abstract syntax tree, or <code>Formula</code>.<p>
 *
 * Input language:<p>
 * 
 * <code>
 * Prop ::= TT<br>
 *       | FF<br>
 *       | &lt;alpha&gt;&lt;alphanum&gt;*<br>
 *       | ( Prop )<br>
 *       | NOT Prop<br>
 *       | Prop AND Prop<br>
 *       | Prop OR Prop<br>
 *       | Prop =&gt; Prop<br>
 *       | Prop &lt;=&gt; Prop<p>
 * </code>
 * 
 * Abstract syntax tree grammar:<p>
 * 
 * <code>
 * Formula ::= Atom(String) <br>
 *       | True <br>
 *       | False <br>
 *       | Neg(Formula) <br>
 *       | Conj(Formula, Formula) <br>
 *       | Disj(Formula, Formula) <br>
 *       | Impl(Formula, Formula) <br>
 *       | Iff(Formula, Formula) <p>
 * </code>
 * 
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     Lexer
 * @see     Token
 * @see     checker.error.SyntacticException
 * @see     checker.ast.Formula
 * @see     java.util.Stack
 */
public class Parser {

    /** A <code>Lexer</code>, which generates <code>Token</code>s. */
    private Lexer lexer;

    /** Current input token, from the <code>lexer</code>. */
    private Token token = new Dollar();    

    /** The abstract syntax tree returned by <code>parse()</code>.
     *
     * @see #parse()
     * @see #getAST()
     */
    private Formula absyn = null;

    /** Operator stack. 
     *
     * Holds operators of type <code>checker.parser.Token</code>.
     *
     * @see Token
     */
    private Stack op_stack  = new Stack();

    /** Value stack. 
     *
     * Holds values of type <code>checker.ast.Formula</code>.
     * 
     * @see checker.ast.Formula
`     */
    private Stack val_stack  = new Stack();

    /** Shift - used in the operator-precedence table. */
    private static final int S = 0;

    /** Reduce - used in the operator-precedence table. */
    private static final int R = 1;

    /** Missing right parenthesis - used in the operator-precedence table. */
    private static final int E1 = 2;

    /** Unbalanced parentheses - used in the operator-precedence table. */
    private static final int E2 = 3;

    /** Missing expression - used in the operator-precedence table. */
    private static final int E3 = 4;

    /** Missing operator - used in the operator-precedence table. */
    private static final int E4 = 5;

    /** Operator-precedence table.
     *
     * S stands for shift, R for reduce and E for errors.
     *
     * @see #parse()
     */
    private final int[][] o_p_table = {
	/*  ----------------------input token--------------------- */
	/*  -op_stk-  <=>  =>  OR  AND NOT (   )   Val Val Val  $  */
	/*  ----------------------input token--------------------- */
	/*     <=> */{ R,  S,  S,  S,  S,  S,  R,  S,  S,  S,  R },
	/*      => */{ R,  R,  S,  S,  S,  S,  R,  S,  S,  S,  R },
	/*      OR */{ R,  R,  R,  S,  S,  S,  R,  S,  S,  S,  R },
	/*     AND */{ R,  R,  R,  R,  S,  S,  R,  S,  S,  S,  R },
	/*     NOT */{ R,  R,  R,  R,  S,  S,  R,  S,  S,  S,  R },
	/*       ( */{ S,  S,  S,  S,  S,  S,  S,  S,  S,  S,  E1},
	/*       ) */{ R,  R,  R,  R,  R,  E4, R,  S,  S,  S,  R }, 
	/*         */{ /* Vals aren't held on the op_stack but */},
	/*         */{ /* blank lines here mean we can look up */},
	/*         */{ /* Token.DOLLAR tokens correctly.       */},
	/*       $ */{ S,  S,  S,  S,  S,  S,  E2, S,  S,  S,  E3},
    };

    /** Constructs a new <code>Parser</code>, specifying an 
     * <code>InputStream</code> to pass to a new <code>Lexer</code>
     * and sets <code>absyn</code> to be the result of <code>parse()</code>.
     * 
     * @param input the <code>InputStream</code> passed to <code>lexer</code>.
     *
     * @see         #lexer
     * @see         Lexer
     * @see         #parse()
     * @see         #absyn
     * @see         java.io.InputStream
     */
    public Parser(InputStream input) throws IOException {
	lexer = new Lexer(input);
	op_stack.push(new Dollar());
	if (checker.Checker.PARSE_DEBUG) {
	    System.out.println("\nParsing:");
	    System.out.println("-------");
	}
	absyn = parse();
    }

    /** Returns the abstract syntax tree constructed by the 
     * <code>Parser</code>. 
     *
     * @return <code>absyn</code>
     *
     * @see    #absyn
     * @see    #parse()
     * @see    checker.ast.Formula
     */
    public Formula getAST() {
	return absyn;
    }

    /** Parses <code>Token</code>s into a <code>Formula</code>.
     *
     * Listed as algorithm 4.5 from Aho, Sethi, Ullman pp. 206.
     *
     * @return an abstract syntax tree.
     * @exception IOException if there's a problem with the <code>lexer</code>.
     * @exception SyntacticException if there is a syntax error in the
     *                               <code>Token</code> stream.
     *
     * @see    checker.ast.Formula
     * @see    #o_p_table
     * @see    #shift()
     * @see    #reduce()
     */
    private final Formula parse() 
	throws IOException,
	       SyntacticException {
      token = lexer.lex();

      while (true) {
	// Check if we should accept the sentence.
	if (token.value() == Token.DOLLAR &&
	    ((Token)op_stack.peek()).value() == Token.DOLLAR) {
	  if (!(val_stack.peek() instanceof Formula)) {
	    throw new SyntacticException("Syntax error.");
	  } else {
	    return (Formula)val_stack.pop();
	  }
	}

	// Else look at the operator-precedence table to determine
	// whether to shift, reduce or throw an error.
	switch(o_p_table[((Token)op_stack.peek()).value()][token.value()]) {
	case S :
	  shift();
	  token = lexer.lex();
	  break;
	case R :
	  reduce();
	  break;
	case E1 :
	  throw new SyntacticException("Missing right parenthesis.");
	case E2 :
	  throw new SyntacticException("Unbalanced parentheses.");
	case E3 :
	  throw new SyntacticException("No expression in parentheses.");
	case E4 :
	  throw new SyntacticException("Missing operator.");
	default :
	  throw new SyntacticException("Syntax error.");
	}
      }
    }

    /** Places <code>token</code> on the <code>op_stack</code> if it
     * is a connective and on the <code>val_stack</code> if it is a subclass 
     * of <code>Val</code>.
     *
     * @see Token
     * @see Val
     */
    private final void shift() {
	if (checker.Checker.PARSE_DEBUG) {
	    System.out.println("Shifting: " + token.value() + " " 
			       + token.toString());
	}

	// Input token is a value (True, False or Atom).
	if (token instanceof Val) {
	    switch(token.value()) {
	    case Token.TRUE : 
		val_stack.push(checker.ast.True.VALUE);
		break;
	    case Token.FALSE :
		val_stack.push(checker.ast.False.VALUE);
		break;
	    case Token.ATOM :
		val_stack.push(new checker.ast.Atom(((Atom)token).literal));
		break;
	    }
	} else {
	    // Input token is an operator (Conj, Disj, Impl, Iff, (, ), $).
	    op_stack.push(token);
	}

	return;
    }

    /** Examines at the operator on the top of the <code>op_stack</code>,
     * and places the corresponding node of the abstract syntax tree
     * on the <code>val_stack</code>.
     *
     * @see #op_stack
     * @see #val_stack
     * @see #o_p_table
     */
    private final void reduce() {
      if (checker.Checker.PARSE_DEBUG) {
	System.out.println("Reducing: "
			   + ((Token)op_stack.peek()).value() 
			   + " " 
			   + ((Token)op_stack.peek()).toString());
      }

      switch (((Token)op_stack.pop()).value()) {
      case Token.NEG : 
	val_stack.push(new checker.ast.Neg((Formula)val_stack.pop()));
	break;
      case Token.RPAREN :
	op_stack.pop(); // Pop an LParen
	break;
      case Token.CONJ :
	{
	  Formula temp = (Formula)val_stack.pop();
	  val_stack.push(new checker.ast.Conj((Formula)val_stack.pop(), 
					      temp));
	}
	break;
      case Token.DISJ : 
	{
	  Formula temp = (Formula)val_stack.pop();
	  val_stack.push(new checker.ast.Disj((Formula)val_stack.pop(),
					      temp));
	}
	break;
      case Token.IMPL : 
	{
	  Formula temp = (Formula)val_stack.pop();
	  val_stack.push(new checker.ast.Impl((Formula)val_stack.pop(), 
					      temp));
	}
	break;
      case Token.IFF :
	{
	  Formula temp = (Formula)val_stack.pop();
	  val_stack.push(new checker.ast.Iff((Formula)val_stack.pop(), 
					     temp));
	}
	break;
      }

      return;
    }
  
}
