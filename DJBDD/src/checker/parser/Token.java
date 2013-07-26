package checker.parser;

/** Superclass for <code>Token</code>s, which are returned by the
 * <code>Lexer</code> and parsed into <code>Formula</code>s by the 
 * <code>Parser</code>.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     Lexer
 * @see     Parser
 * @see     checker.ast.Formula
 * @see     Dollar
 * @see     LParen
 * @see     RParen
 * @see     Neg
 * @see     Conj
 * @see     Disj
 * @see     Impl
 * @see     Iff
 * @see     Val
 */
abstract class Token {

    static final int DOLLAR = 10;
    static final int ATOM   = 9;
    static final int FALSE  = 8;
    static final int TRUE   = 7;
    static final int RPAREN = 6;
    static final int LPAREN = 5;
    static final int NEG    = 4;
    static final int CONJ   = 3;
    static final int DISJ   = 2;
    static final int IMPL   = 1;
    static final int IFF    = 0;

    /** Return a non-negative integer, unique to each subtype of 
     * <code>Token</code>.
     *
     * This is used in the <code>Parser</code> to switch between 
     * tokens of different type and to lookup the appropriate shift / 
     * reduce action in the operator-precedence table.<p>
     *
     * The return value should be one of the <code>static</code>
     * <code>final</code> attributes of <code>Token</code>.
     * 
     * @return a non-negative integer, unique to each subtype of 
     *         <code>Token</code>.
     *
     * @see    Parser#o_p_table
     * @see    #DOLLAR
     * @see    #ATOM
     * @see    #FALSE
     * @see    #TRUE
     * @see    #RPAREN
     * @see    #LPAREN
     * @see    #NEG
     * @see    #CONJ
     * @see    #DISJ
     * @see    #IMPL
     * @see    #IFF
     */
    abstract int value();

    /** Return a <code>String</code> representing the object type, so that
     * the <code>Lexer</code> and <code>Parser</code> can print meaningful 
     * debugging information.
     *
     * @return a string representing the object type.
     *
     * @see    checker.Checker#PARSE_DEBUG
     * @see    checker.parser.Lexer
     * @see    checker.parser.Parser
     */
    abstract public String toString();
}
