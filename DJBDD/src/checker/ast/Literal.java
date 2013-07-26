package checker.ast;

import checker.error.CheckerException;
import java.util.Vector;

/** Superclass for <i>literal</i> <code>Formula</code>s - those that are not 
 * connectives.
 *
 * @author  Sarah Mount
 * @version 1.0
 *
 * @see     Atom
 * @see     True
 * @see     False
 */ 
public abstract class Literal extends Formula {

    public Formula removeImplications() {
	return this;
    }

    public Formula toNnf() throws CheckerException {
	return this;
    }

    public Formula nnfToCnf() throws CheckerException {
	return this;
    }

    public Formula simplifyCnf() throws CheckerException {
	return this;
    }

    protected Vector toClause() throws CheckerException {
	Vector V = new Vector();
	V.addElement(this);
	return V;
    }

    /** Tests whether <code>formula</code> is the negation of this Formula.
     *
     * Only called by <code>Conj.simplifyCnf()</code>.
     *
     * @param formula the <code>Formula</code> to be tested.
     * @return        <code>true</code> if <code>formula</code> is the 
     *                negation of this <code>Formula</code>.
     *
     * @see           Conj#simplifyCnf() 
     */
    abstract protected boolean isNeg(Formula formula);

}
