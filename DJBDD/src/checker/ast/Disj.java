package checker.ast;

import checker.error.CheckerException;
import java.util.Vector;

/** Specifies <code>OR</code> connectives.
 *
 * @author  Sarah Mount
 * @author  Your Name
 * @version 1.0
 */
public final class Disj extends Formula {

    /** The left hand side of this disjunction. */
    private final Formula left;

    /** The right hand side of this disjunction. */
    private final Formula right;


    /** Constructs a new disjunction, with <code>Formula</code>s at its
     * left and right hand side.
     *
     * Specifies the left and right hand sides of this conjunction.
     *
     * @param left  the left hand <code>Formula</code>.
     * @param right the right hand <code>Formula</code>.
     */
    public Disj(Formula left, Formula right) {
	this.left = left;
	this.right = right;
    }

    /** Returns the left hand side of this disjunction.
     * 
     * @return the left hand side of this disjunction.
     */
    protected Formula getLeft() {
	return this.left;
    }

    /** Returns the right hand side of this disjunction.
     * 
     * @return the right hand side of this disjunction.
     */
    protected Formula getRight() {
	return this.right;
    }

    public Formula removeImplications() {
	return new Disj(this.left.removeImplications(),
			this.right.removeImplications());
    }

    public Formula toNnf() throws CheckerException {
	return new Disj(this.left.toNnf(), this.right.toNnf());
    }

    public Formula nnfToCnf() throws CheckerException {
	Formula left = this.left.nnfToCnf();
	Formula right = this.right.nnfToCnf();

	if (left instanceof Conj) {
	    Conj conj = (Conj)left;
	    return new Conj(new Disj(conj.getLeft(), right).nnfToCnf(),
			    new Disj(conj.getRight(), right).nnfToCnf());
	} 
	else if (right instanceof Conj) {
	    Conj conj = (Conj)right;
	    return new Conj(new Disj(left, conj.getLeft()).nnfToCnf(),
			    new Disj(left, conj.getRight()).nnfToCnf());
	}
	return new Disj(left, right);
    }

    protected Vector toClause() throws CheckerException {
	Vector clause  = new Vector();
	Vector c_left  = this.left.toClause();
	Vector c_right = this.right.toClause();

	for (int i = 0; i < c_left.size(); i++) {
	    clause.addElement(c_left.elementAt(i));
	}

	for (int i = 0; i < c_right.size(); i++) {
	    clause.addElement(c_right.elementAt(i));
	}
	
	return clause;
    }

    protected Formula neg() {
	return new Conj(this.left.neg(), this.right.neg());
    }

    public String toString() {
	return ("(" + this.left.toString() + " \\/ " 
		+ this.right.toString() + ")");
    }
}
