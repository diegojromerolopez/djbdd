package asserter.examples.diabetest1assistant;

import asserter.core.*;

/**
 * Diabetes Type 1 Assistant
 * @author diegoj
 */
public class DiabetesT1Assistant {
    
    /** Asseter that will be used to test if the  */
    private final Asserter asserter;
    
    /** Reference where we get the glucose, insulin, meal and exercise data */
    private final Human human;

    /** Random seed. Defaults to 0 */
    private int randomSeed = 0;
    
    /**
     * Logical variables used.
     */
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

    /**
     * Set of logical rules that determine if a pacient action is right or wrong.
     */
    private static final String RULES = ""+
            "(GL && EN && ILC && M) || "+
            "(GL && EL && INC && MS) || "+
            "(GN && EL && INC && MN) ||"+
            "(GN && EN && INC && MN) || "+
            "(GN && EN && ILC && M) ||"+
            "(GH1 && EN && IMC && MN) || "+
            "(GH1 && EN && IHC && M) || "+
            "((GH2||GVH||GTH) && EN && IHC && MN) || "+
            "(GN && EL && INC && MS) || "+
            "(GH1 && EL && INC && MN) ";

            

    /**
     * Constructs a new assistant
     */
    public DiabetesT1Assistant(){
        // Init of the assertion process
        Asserter.init(VARIABLES);
        this.human = new Human();
        this.asserter = new Asserter(RULES, this.human);
    }
    
    private void printResult(){
        if(!this.asserter.run()){
            System.out.println("BAD DECISION");
        }
        else{
            System.out.println("GOOD DECISION");
        }
        this.human.print();
        System.out.println("");
    }
    
    /**
     * Run a very basic simulation of the life of a patient.
     */
    public void run(){
        System.out.println("=================================================");
        System.out.println("=================================================");
        System.out.println("SIMULATION OF A WEEK OF A DIABETIC TYPE 1 PATIENT");
        System.out.println("");
        random.Random.init(this.randomSeed);
        int i = 1;
        while(i < 7){
            System.out.println("=============================================");
            System.out.println("DAY "+i);
            // Breakfast
            this.human.eatMeal();
            this.printResult();
            this.human.incGlucoseLevel();
            
            // Going to work
            this.human.walk();
            this.printResult();
            this.human.decGlucoseLevel();
            
            // Working
            this.human.work();
            this.printResult();
            
            // Eating lunch
            this.human.eatMeal();
            this.printResult();
            
            // Working
            this.human.work();
            this.printResult();
            
            // Going back home
            this.human.walk();
            this.printResult();
            this.human.decGlucoseLevel();
            
            // Eating dinner
            this.human.eatMeal();
            this.printResult();
            this.human.incGlucoseLevel();
            
            // Sleeping
            this.human.sleep();
            this.printResult();

            i++;
        }
    
    }
    

    /**
     *  Main with the call to start simulation.
     */
    public static void main(String args[]){
        DiabetesT1Assistant assistant = new DiabetesT1Assistant();
        assistant.run();
    }
    
}
