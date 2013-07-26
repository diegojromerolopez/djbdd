package checker.ast;

/** Specifies the <code>FF</code> <code>Literal</code>.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
public final class False extends Literal {

    /** The only instantiation of this class.
     *
     * <code>VALUE</code> enables us to test a <code>Formula</code> for
     * falsehood using the double-equals operator. For example:<p>
     *
     * <code>if(myFormula == False.VALUE) { // do_whatever; }</code>
     */
    public static final False VALUE = new False();

    /** Constructs a new <code>False</code>.
     *
     * Making this constructor <code>private</code> ensures that 
     * <code>False</code> can never be instantiated outside this class.
     */
    private False() {}

    protected boolean isNeg(Formula formula) {
	return formula == True.VALUE;
    }

    protected Formula neg() {
	return True.VALUE;
    }
    
    public String toString() {
	return "FALSE";
    }
}
