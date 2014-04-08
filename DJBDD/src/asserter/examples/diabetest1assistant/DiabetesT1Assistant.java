/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asserter.examples.diabetest1assistant;

import asserter.core.*;

/**
 *
 * @author diegoj
 */
public class DiabetesT1Assistant {
    
    /** Asseter that will be used to test if the  */
    private final Asserter asserter;
    
    private final Human human;
    
    private static final String[] VARIABLES = {
        "GL", // GlucoseLow
        "GN", // GlucoseNormal
        "GH1", // GlucoseHigh1
        "GH2", // GlucoseHigh2
        "GVH", // GlucoseVeryHigh
        "GTH", // GlucoseTooHigh
        "EN", // ExerciseNone
        "EL", // ExerciseLight
        "EM", // ExerciseMedium
        "EH", // ExerciseHard
        "INC", // InsulinNoCorrection
        "ILC", // InsulinLowCorrection
        "IMC", // InsulinMediumCorrection
        "IHC", // InsulinHighCorrection
        "MN", // MealNone
        "MS", // MealSupplement
        "M", //PrimaryMeal
    };

    private static final String RULES = ""+
            "(GL -> (INC && MS)) &&"+
            "(GO -> INC) &&"+
            "(GH1 -> (ILC && MN)) &&"+
            "(GH2 -> (IMC && MN)) &&"+
            "(GVH -> (IMC && MN)) &&"+
            "(GTH -> (IHC && MN)) &&"+
            "(EL -> (GH1 || GO)) &&"+
            "(EN -> GH1) &&"+
            "(EH -> (GH1 && MS)) &&"+
            "(M -> (ILC || IHC))";
            
    
    public DiabetesT1Assistant(){
        // Init of the assertion process
        Asserter.init(VARIABLES);
        this.human = new Human();
        this.asserter = new Asserter(RULES, this.human);
    }
    
    public void run(){
    
        this.human.eatTooMuch();
        if(!this.asserter.run()){
            System.out.println("You ate too much. Don't eat and try again");
        }
        
        this.human.tooMuchInsulin();
        if(!this.asserter.run()){
            System.out.println("You use too much insulin, reduce its levels and try again");
        }
    
    }
    
    /**
     * Test main.
     */
    public static void main(String args[]){
        
    }
    
}
