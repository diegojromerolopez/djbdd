/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package djbdd;

import net.astesana.javaluator.*;

import java.util.Iterator;

 
/** An example of how to implement an evaluator from scratch.
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
  /** The logical double implication operator.*/
  public final static Operator DOUBLE_IMP = new Operator("<->", 2, Operator.Associativity.LEFT, 1);
  /** The logical double implication operator (version2).*/
  public final static Operator DOUBLE_IMP2 = new Operator("<=>", 2, Operator.Associativity.LEFT, 1);
  
  
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
    PARAMETERS.add(DOUBLE_IMP);
    PARAMETERS.add(DOUBLE_IMP2);
    // Add the parentheses
    PARAMETERS.addExpressionBracket(BracketPair.PARENTHESES);
  }
 
  public BooleanEvaluator() {
    super(PARAMETERS);
  }
 
  @Override
  protected Boolean toValue(String literal, Object evaluationContext) {
    //System.out.println("'"+literal+"'");
    return Boolean.valueOf(literal);
  }
 
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
      //System.out.println("'"+o1.toString()+"'");
      //System.out.println("'"+o2.toString()+"'");
      //System.out.println(o1 && o2);
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
      return (!o1 || o2) && (o1 || !o2);
    }
    
    return super.evaluate(operator, operands, evaluationContext);
  }
  
  public static boolean run(String expression){
    BooleanEvaluator evaluator = new BooleanEvaluator();
    boolean result = evaluator.evaluate(expression);
    //System.out.println (expression+" = "+result);
    return result;
    
  }
}
 