package checker.ast;

/** Specifies the <code>TT</code> <code>Literal</code>.
 *
 * @author  Sarah Mount
 * @version 1.0
 */
public final class True extends Literal {

    /** The only instantiation of this class.
     *
     * <code>VALUE</code> enables us to test a <code>Formula</code> for
     * truth using the double-equals operator. For example:<p>
     *
     * <code>if(myFormula == True.VALUE) { // do_whatever; }</code>
     */
    public static final True VALUE = new True();

    /** Constructs a new <code>True</code>.
     *
     * Making this constructor <code>private</code> ensures that 
     * <code>True</code> can never be instantiated outside this class.
     */
    private True() {}

    protected boolean isNeg(Formula formula) {
	return formula == False.VALUE;
    }

    protected Formula neg() {
	return False.VALUE;
    }

    public String toString() {
	return "TRUE";
    }
}
