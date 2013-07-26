package checker.ast;

import checker.error.*;
import java.util.Vector;

/** Specifies <code>AND</code> connectives.
 *
 * The bulk of the <code>simplifyCnf()</code> algorithm resides here,
 * since all CNF <code>Formula</code>s contain a <code>Conj</code> at their 
 * root.
 *
 * @author  Sarah Mount
 * @author  Your Name
 * @version 1.0
 *
 * @see     java.util.Vector
 */
public final class Conj extends Formula {

    /** The left hand side of this conjunction. */
    private final Formula left;
    /** The right hand side of this conjunction. */
    private final Formula right;

    /** Constructs a new conjunction, with <code>Formula</code>s at its
     * left and right hand side.
     *
     * Specifies the left and right hand sides of this conjunction.
     *
     * @param left  the left hand <code>Formula</code>.
     * @param right the right hand <code>Formula</code>.
     */
    public Conj(Formula left, Formula right) {
	this.left = left;
	this.right = right;
    }
    
    /** Returns the left hand side of this conjunction.
     * 
     * @return the left hand side of this conjunction.
     */
    protected Formula getLeft() {
	return this.left;
    }

    /** Returns the right hand side of this conjunction.
     *
     * @return the right hand side of this conjunction.
     */
    protected Formula getRight() {
	return this.right;
    }

    public Formula removeImplications() {
	return new Conj(this.left.removeImplications(), 
			this.right.removeImplications());
    }

    public Formula toNnf() throws CheckerException {
	return new Conj(this.left.toNnf(), this.right.toNnf());
    }

    public Formula nnfToCnf() throws CheckerException {
	return new Conj(this.left.nnfToCnf(), this.right.nnfToCnf());
    }

    /** Simplifies this CNF <code>Formula</code>. 
     *
     * Applies the following transformations to this <code>Formula</code>: <p>
     * &nbsp&nbsp&nbsp&nbsp <code>P OR NOT P                == TRUE</code><br>
     * &nbsp&nbsp&nbsp&nbsp <code>P AND (P OR Q)            == P</code><br>
     * &nbsp&nbsp&nbsp&nbsp <code>(P OR Q) AND (NOT P OR Q) == Q</code>.<p>
     *
     * In order to simplify the transformations, <code>Formula</code>s are
     * temporarily converted into <i>clausal</i> form. This is a list of 
     * conjunctions, where each conjunction is a list of disjunctions. For 
     * example, the <code>Formula</code>:<p>
     * 
     * <code>(L11 OR L12 OR ...) AND (L21 OR L22 OR ...) AND 
     * (L31 OR L32 OR...)</code><p>
     *
     * is represented by the following clause:<p>
     *
     * <code>[[L11, L12, ...], [L21, L22, ...], [L31, L32, ...], ...]</code><p>
     *
     * Here, clausal forms are implemented as a <code>Vector</code> of 
     * <code>Vector</code>s.
     *
     * @exception CheckerException if this <code>Formula</code> is not in CNF,
     *            or if it contains an implication.
     *
     * @see       #toClause()
     * @see       #fromClause(Vector clausal)
     * @see       #isSubset(Vector v1, Vector v2)
     * @see       java.util.Vector
     */
    public Formula simplifyCnf() throws CheckerException {
	// Convert this Conjunction to clausal form.
	Vector clausal = this.toClause();

	// Remove any clause containing a Literal and its negation.
	for (int i = 0; i < clausal.size(); i++) {
	    Vector curr_disj = (Vector)clausal.elementAt(i);
	    loop: for (int j = 0; j < curr_disj.size(); j++) {
		if (curr_disj.elementAt(j) instanceof Literal) {
		    Literal at_j = (Literal)curr_disj.elementAt(j);
		    for (int k = 0; k < curr_disj.size(); k++) {
			Formula at_k = (Formula)curr_disj.elementAt(k);
			if (at_j.isNeg(at_k)) {
			    clausal.removeElementAt(i--);
			    break loop;
			}
		    }
		}
	    }
	}

	// Remove any clause that is a superset of another clause.
	for (int i = 0; i < clausal.size() - 1; i++) {
	    for (int j = i+1; j < clausal.size(); j++) {
		if (isSubset((Vector)clausal.elementAt(i), 
			     (Vector)clausal.elementAt(j))) {
		    clausal.removeElementAt(j);
		} else if (isSubset((Vector)clausal.elementAt(j), 
				    (Vector)clausal.elementAt(i))) {
		    clausal.removeElementAt(i);
		}
	    }
	}

	// Return a Formula.
	return fromClause(clausal);
    }

    /** Returns <code>true</code> if <code>v1</code> is a subset of 
     * <code>v2</code>.
     * 
     * Only called by <code>Conj.simplifyCnf()</code>.
     *
     * @param v1 a possible subset of <code>v2</code>
     * @param v2 a possible superset of <code>v1</code>
     * @return   <code>true</code> if <code>v1</code> is a subset of 
     *           <code>v2</code>.
     *
     * @see      #simplifyCnf()
     * @see      java.util.Vector
     */
    private static boolean isSubset(Vector v1, Vector v2) {
	for (int i = 0; i < v1.size(); i++) {
	    if (!v2.contains(v1.elementAt(i))) {
		return false;
	    }
	}
	return true;
    }

    /** Converts <code>clausal</code> from clausal form to a 
     * <code>Formula</code>.
     * 
     * Only called by <code>Conj.simplifyCnf()</code>.
     * 
     * @param clausal a <code>Formula</code> in its clausal form.
     * @return        the abstract syntax tree representation of 
     *                <code>clausal</code>.
     *
     * @see           #simplifyCnf()
     * @see           java.util.Vector
     */
    private static Formula fromClause(Vector clausal) {
	Formula conj = True.VALUE;

	for (int i = clausal.size(); i > 0; i--) {
	    Formula disj = False.VALUE;
	    Vector curr_disj = (Vector)clausal.elementAt(i-1);

	    for (int j = curr_disj.size(); j > 0; j--) {
		Formula curr_lit = (Formula)curr_disj.elementAt(j-1);
		if (disj == False.VALUE) {
		    disj = curr_lit;
		} else {
		    disj = new Disj(disj, curr_lit);
		}
	    }

	    if (conj == True.VALUE) {
		conj = disj;
	    } else {
		conj = new Conj(disj, conj);
	    }
	}

	return conj;
    }

    protected Vector toClause() throws CheckerException {
	Vector clause  = new Vector();
	Vector c_left;
	Vector c_right;

	if (this.left instanceof Conj) {
	    c_left = this.left.toClause();
	    for (int i = 0; i < c_left.size(); i++) {
		clause.addElement(c_left.elementAt(i));
	    }
	} else {
	    c_left = this.left.toClause();
	    clause.addElement(c_left);
	}

	if (this.right instanceof Conj) {
	    c_right = this.right.toClause();
	    for (int i = 0; i < c_right.size(); i++) {
		clause.addElement(c_right.elementAt(i));
	    }
	} else {
	    c_right = this.right.toClause();
	    clause.addElement(c_right);
	}

	return clause;
    }

    protected Formula neg() {
	return new Disj(this.left.neg(), this.right.neg());
    }

    public String toString() {
	return ("(" + this.left.toString() + " /\\ "
		+ this.right.toString() + ")");
    }
}
