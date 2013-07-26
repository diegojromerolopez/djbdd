package checker.ast;

import checker.error.*;
import java.util.Vector;

/** Specifies <code>=&gt;</code> connectives.
 *
 * @author  Sarah Mount
 * @author  Your Name
 * @version 1.0
 */
public final class Impl extends Formula {

    /** The left hand side of this implication. */
    private final Formula left;
    /** The right hand side of this implication. */
    private final Formula right;

    /** Constructs a new implication, with <code>Formula</code>s at its
     * left and right hand side.
     *
     * Specifies the left and right hand sides of this implication.
     *
     * @param left  the left hand <code>Formula</code>.
     * @param right the right hand <code>Formula</code>.
     */
    public Impl(Formula left, Formula right) {
	this.left = left;
	this.right = right;
    }

    public Formula removeImplications() {	
	return new Disj(new Neg(this.left.removeImplications()), 
			this.right.removeImplications());
    }

    public Formula toNnf() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "implication: cannot " +
						      "convert to NNF");
    }

    public Formula nnfToCnf() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "implication: cannot " +
						      "convert to CNF");
    }

    public Formula simplifyCnf() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "implication: cannot " +
						      "convert to CNF");
    }

    protected Vector toClause() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "implication: cannot " +
						      "convert to clausal " +
						      "form.");
    }

    protected Formula neg() {
	return new Neg(this);
    }

    public String toString() {
	String impl = "";
	if (this.left instanceof Literal) {
	    impl = impl + this.left.toString();
	} else {
	    impl = impl + "(" + this.left.toString() + ")";
	}
	impl = impl + " => ";
	if (this.right instanceof Literal) {
	    impl = impl + this.right.toString();
	} else {
	    impl = impl + "(" + this.right.toString() + ")";
	}
	return impl;
    }
}
