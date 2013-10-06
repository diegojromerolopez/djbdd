package djbdd.core;

import net.astesana.javaluator.*;

import java.util.*;

 
/**
 * Evaluates a boolean expression.
 * Used in {@link BDD#evaluatePath }.
 * This code uses the Javaluator library: http://javaluator.sourceforge.net/en/home/
 * This code is inspired by the javaluator example: http://javaluator.sourceforge.net/en/doc/tutorial.php?chapter=creatingSimple
 * @author diegoj
 */
public class BooleanEvaluator extends AbstractEvaluator<Boolean> {
  
    /** The negate unary operator.*/
  public final static Operator NOT = new Operator("!", 1, Operator.Associativity.RIGHT, 5);
  
  /** The logical AND operator.*/
  private static final Operator AND = new Operator("&&", 2, Operator.Associativity.LEFT, 4);
  
  /** The logical OR operator.*/
  public final static Operator OR = new Operator("||", 2, Operator.Associativity.LEFT, 3);
  
  /** The logical Implication operator.*/
  public final static Operator IMP = new Operator("->", 2, Operator.Associativity.LEFT, 2);
  
  /** The logical Implication operator (version2).*/
  public final static Operator IMP2 = new Operator("=>", 2, Operator.Associativity.LEFT, 2);
  
  /** The logical not-implication operator.*/
  public final static Operator NOT_IMP = new Operator("!->", 2, Operator.Associativity.LEFT, 2);
  
  /** The logical not-implication operator (version2).*/
  public final static Operator NOT_IMP2 = new Operator("!=>", 2, Operator.Associativity.LEFT, 2);  
  
  /** The logical double implication operator.*/
  public final static Operator DOUBLE_IMP = new Operator("<->", 2, Operator.Associativity.LEFT, 1);

  /** The logical double implication operator (version2).*/
  public final static Operator DOUBLE_IMP2 = new Operator("<=>", 2, Operator.Associativity.LEFT, 1);
  
  /** The is different operator .*/
  public final static Operator IS_DIFFERENT = new Operator("!=", 2, Operator.Associativity.LEFT, 1);
  
  /** Evaluator parameters */
  private static final Parameters PARAMETERS;
 
  static {
    // Create the evaluator's parameters
    PARAMETERS = new Parameters();
    // Add the supported operators
    PARAMETERS.add(AND);
    PARAMETERS.add(OR);
    PARAMETERS.add(NOT);
    PARAMETERS.add(IMP);
    PARAMETERS.add(IMP2);
    PARAMETERS.add(NOT_IMP);
    PARAMETERS.add(NOT_IMP2);    
    PARAMETERS.add(DOUBLE_IMP);
    PARAMETERS.add(DOUBLE_IMP2);
    PARAMETERS.add(IS_DIFFERENT);
    // Add the parentheses
    PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
  }
 
  /**
   * Construct a boolean evaluator.
   */
  public BooleanEvaluator() {
    super(PARAMETERS);
  }

  /**
   * Gives the boolean value of a literal.
   * @param literal Litera of the string.
   * @param evaluationContext Context.
   * @return boolean evaluation of the literal.
   */
  @Override
  protected Boolean toValue(String literal, Object evaluationContext) {
    return Boolean.valueOf(literal);
  }
 
  /**
   * Evaluates one operand with one operand.
   * @param operator Current operator.
   * @param operands Operands of the operator.
   * @param evaluationContext Context.
   * @return boolean evaluation of the operator.
   */
  @Override
  protected Boolean evaluate(Operator operator, Iterator<Boolean> operands, Object evaluationContext) {
    
    if (operator == NOT) {
      return !operands.next();
    }
    
    if (operator == OR) {
      Boolean o1 = operands.next();
      Boolean o2 = operands.next();
      return o1 || o2;
    }
    
    if (operator == AND) {
      Boolean o1 = operands.next();
      Boolean o2 = operands.next();
      return o1 && o2;
    }
    
    if (operator == IMP || operator == IMP2) {
      Boolean o1 = operands.next();
      Boolean o2 = operands.next();
      return !o1 || o2;      
    }
    
    if (operator == DOUBLE_IMP || operator == DOUBLE_IMP2) {
      Boolean o1 = operands.next();
      Boolean o2 = operands.next();
      //return (!o1 || o2) && (o1 || !o2);
      return (o1 == o2);
    }
    
    if (operator == NOT_IMP || operator == NOT_IMP2) {
      Boolean o1 = operands.next();
      Boolean o2 = operands.next();
      return (o1 && !o2);
    }    
    
    if (operator == IS_DIFFERENT) {
      Boolean o1 = operands.next();
      Boolean o2 = operands.next();
      return (o1 != o2);
    }        
    
    return super.evaluate(operator, operands, evaluationContext);
  }
  
  /**
   * Run this process of evaluation for one expression.
   * @param expression Boolean expression to be evaluated.
   * @return boolean value for the expression.
   */
  public static boolean run(String expression){
        BooleanEvaluator evaluator = new BooleanEvaluator();
        boolean result = evaluator.evaluate(expression);
        return result;
  }
  
  /**
   * Evaluate a given boolean logic function.
   * @param function Boolean function to be evaluated.
   * @param variables present in the boolean function.
   * @param assignement Truth assignement for each variable. assignement[i]=B iff ith's variable boolean value is B (B in {true,false}).
   */
  public static boolean evaluateFunction(String function, ArrayList<String> variables, ArrayList<Boolean> assignement){
      String _function = function;
      for(int i=0; i<variables.size(); i++){
            String variable = variables.get(i);
            String value = assignement.get(i)?"true":"false";
            _function = _function.replaceAll(variable, value);
      }
      return BooleanEvaluator.run(_function);
  }
  
  /**
   * Negate a boolean logic operation.
   * Used to apply the De Morgan Laws.
   * @param op Operation to complement.
   * @return Complemented operation of op.
   * 
   */
  public static String neg(String op){
      if(op.equals("and"))
          return "or";
      if(op.equals("or"))
          return "and";
      if(op.equals("<->"))
        return "!=";
      if(op.equals("!="))
        return "<->";
      if(op.equals("->"))
        return "!->";
      if(op.equals("!->"))
        return "->";
      return null;
  }
  
}
 