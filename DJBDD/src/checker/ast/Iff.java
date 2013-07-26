package checker.ast;

import checker.error.*;
import java.util.Vector;

/** Specifies <code>&lt;=>&gt;</code> connectives.
 *
 * @author  Sarah Mount
 * @author  Your Name
 * @version 1.0
 */
public final class Iff extends Formula {

    /** The left hand side of this bi-implication. */
    private final Formula left;
    /** The right hand side of this bi-implication. */
    private final Formula right;

    /** Constructs a new bi-implication, with <code>Formula</code>s at its
     * left and right hand side.
     *
     * Specifies the left and right hand sides of this bi-implication.
     *
     * @param left  the left hand <code>Formula</code>.
     * @param right the right hand <code>Formula</code>.
     */
    public Iff(Formula left, Formula right) {
	this.left = left;
	this.right = right;
    }

    public Formula removeImplications() {
	Formula left = this.left.removeImplications();
	Formula right = this.right.removeImplications();
	return new Conj(new Disj(new Neg(left), right), 
			new Disj(left, new Neg(right)));
    }

    public Formula toNnf() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "iff term: cannot " +
						      "convert to NNF");
    }

    public Formula nnfToCnf() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "iff term: cannot " +
						      "convert to CNF");
    }

    public Formula simplifyCnf() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "iff term: cannot " +
						      "simplify CNF");
    }

    protected Vector toClause() throws CheckerException {
	throw new FormulaContainsImplicationException("Formula contains an " +
						      "iff term: cannot " +
						      "convert to clausal " +
						      "form.");
    }

    protected Formula neg() {
	return new Neg(this);
    }

    public String toString() {
	String iff = "";
	if (this.left instanceof Literal) {
	    iff = iff + this.left.toString();
	} else {
	    iff = iff + "(" + this.left.toString() + ")";
	}
	iff = iff + " <=> ";
	if (this.right instanceof Literal) {
	    iff = iff + this.right.toString();
	} else {
	    iff = iff + "(" + this.right.toString() + ")";
	}
	return iff;
    }
}
