package checker.ast;

import checker.error.CheckerException;
import java.util.Vector;

/** Superclass of all the types in abstract syntax trees.
 *
 * Abstract syntax trees - <code>Formula</code>s - are returned by the
 * parser. The <code>private</code> <code>check()</code> method in 
 * <code>checker.Checker</code> calls the relevant methods in 
 * <code>Formula</code> to perform a correct tautology check.<p>
 *
 * The grammar of the AST is as follows: <br>
 * Formula ::= Atom(String) <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| True <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| False <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Neg(Formula) <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Conj(Formula, Formula) <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Disj(Formula, Formula) <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Impl(Formula, Formula) <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;| Iff(Formula, Formula) <p>
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     checker.parser.Parser#parse()
 * @see     checker.Checker#check(Formula formula)
 * @see     checker.error.CheckerException
 * @see     java.util.Vector
 * @see     Neg
 * @see     Conj
 * @see     Disj
 * @see     Impl
 * @see     Iff
 * @see     Literal
 */
public abstract class Formula {

    /** Removes implications from this <code>Formula</code>. 
     *
     * @return this <code>Formula</code> with implications removed.
     */
    abstract public Formula removeImplications();

    /** Converts this <code>Formula</code> to Negative Normal Form. 
     *
     * @return    this <code>Formula</code> in Negative Normal Form.
     * @exception CheckerException If this formula still contains any
     *            implications.
     */
    abstract public Formula toNnf() throws CheckerException;

    /** Converts this <code>Formula</code> from Negative Normal Form to 
     * Conjunctive Normal Form.
     *
     * @return    this <code>Formula</code> in Conjunctive Normal Form.
     * @exception CheckerException If this formula is not in NNF or if it
     *            still contains any implications.
     */
    abstract public Formula nnfToCnf() throws CheckerException;

    /** Simplifies this CNF <code>Formula</code>. 
     *
     * @return    this <code>Formula</code> in its simplist Conjunctive Normal 
     *            Form.
     * @exception CheckerException If this formula is not in CNF or if it
     *            still contains any implications.
     */
    public Formula simplifyCnf() throws CheckerException {
	return (new Conj(this, True.VALUE)).simplifyCnf();
    }

    /** Returns the negation of this Formula. 
     *
     * Subclasses should implement this method 'correctly' - that is
     * according to deMorgan's laws and the double-negative law.
     *
     * @return the negation of this <code>Formula</code>.
     */
    abstract protected Formula neg();
    
    /** Converts this formula to clausal form - only called by 
     * <code>simplifyCnf()</code> in the <code>Conj</code> class.
     * 
     * @return    this <code>Formula</code> in clausal form.
     * @exception CheckerException If this formula is not in CNF or if it
     *            still contains any implications.
     *
     * @see       Conj#simplifyCnf()
     * @see       java.util.Vector
     */
    abstract protected Vector toClause() throws CheckerException;

    /** Returns a textual description of this <code>Formula</code>.
     *
     * This is used for pretty printing, when either of the 
     * <code>checker.Checker.DEBUG</code> or 
     * <code>checker.Checker.PARSE_DEBUG</code> switches are turned on.
     *
     * @return the textual representation.
     *
     * @see    checker.Checker#DEBUG
     * @see    checker.Checker#PARSE_DEBUG
     */
    abstract public String toString();

}
