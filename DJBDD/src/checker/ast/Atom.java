package checker.ast;

/** Specifies atomic <code>Literal</code>s.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
public final class Atom extends Literal {
    
    /** The name of this atomic <code>Formula</code>. */
    private final String name;

    /** Constructs a new <code>Atom</code> with a name.
     *
     * @param name the name.
     */
    public Atom(String name) {
	this.name = name;
    }

    /** Returns the name of this <code>Atom</code>.
     *
     * @return the name of this atomic Formula.
     */
    protected String getName() {
	return name; 
    }

    protected boolean isNeg(Formula formula) {
	if (formula instanceof Neg) {
	    Formula term = ((Neg)formula).getTerm();
	    if (term instanceof Atom) {
		return ((Atom)term).getName().equals(this.name);
	    }
	}
	return false;
    }

    protected Formula neg() {
	return new Neg(this);
    }

    public String toString() {
	return this.name;
    }
}
