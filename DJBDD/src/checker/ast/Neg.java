package checker.ast;

import checker.error.*;
import java.util.Vector;

/** Specifies <code>NOT</code> connectives.
 *
 * @author  Sarah Mount
 * @author  Your Name
 * @version 1.0
 */
public final class Neg extends Formula {

    /** The term this object negates. */
    private final Formula term;

    /** Constructs a new negation, with a <code>Formula</code> as its
     * subject.
     *
     * Specifies the left and right hand sides of this conjunction.
     *
     * @param term  the <code>Formula</code> being negated.
     */
    public Neg(Formula term) {
	this.term = term;
    }

    /** Returns the subject of this negation.
     *
     * @return the subject of this negation.
     */
    protected Formula getTerm() {
	return this.term;
    }

    public Formula removeImplications() {
	return new Neg(this.term.removeImplications());
    }

    public Formula toNnf() throws CheckerException {
	if (this.term instanceof Conj ||
	    this.term instanceof Disj ||
	    this.term instanceof Neg) {
	    return this.term.neg().toNnf();
	} else if (this.term instanceof Literal) {
	    return this;
	}
	throw new FormulaContainsImplicationException("Formula contains " +
						      "an implication.");
    }

    public Formula nnfToCnf() throws CheckerException {
	if (this.term instanceof Literal) {
	    return this;
	}
	throw new FormulaNotInNnfException("Formula is not in " +
					   "Negative Normal Form.");
    }

    public Formula simplifyCnf() throws CheckerException {
	if (this.term instanceof Literal) {
	    return this;
	}
	throw new FormulaNotInCnfException("Formula is not in " +
					   "Conjunctive Normal Form.");
    }

    protected Vector toClause() throws CheckerException {
	Vector V = new Vector();
	V.addElement(this);
	return V;
    }

    protected Formula neg() {
	return this.term;
    }

    public String toString() {
	return "!" + term.toString();
    }
}
