/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package asserter.examples.diabetest1assistant;

/**
 * Implements the simulated source of the data that will be used by the assistant.
 * @author diegoj
 */
public class Human {
    
    /**************************************/
    /* GLUCOSE LEVELS */
    /** Low glucose (hypoglicaemia) */
    public static final int GL = 21;
    
    /** Normal blood glucose level */
    public static final int GN = 22;
    
    /** High glucose level */
    public static final int GH1 = 23;
    
    /** Higher glucose level */
    public static final int GH2 = 24;
    
    /** Very high glucose level */
    public static final int GVH = 25;
    
    /**
     * Too high glucose level, if there are no correction insulin, pacient can
     * suffer from diabetic coma.
     */
    public static final int GTH = 26;
    
    /** Glucose level in mg/dc */
    private int bloodGlucoseLevel;

    /**************************************/
    /* EXERCISE LEVELS */
    /** No exercise */
    public static final int EN = 31;
    
    /** Light exercise */
    public static final int EL = 32;
    
    /** Medium exercise */
    public static final int EM = 33;
    
    /** Hard exercise */
    public static final int EH = 34;
    
    /** Meal */
    private int exercise;
    
    /**************************************/
    /* MEAL TYPES */
    /** Primary meal (breakfast, lunch and dinner) */
    public static final int M = 41;
    
    /** Supplementary meal  */
    public static final int MS = 42;
    
    /** No meal  */
    public static final int MN = 43;
    
    /** Meal */
    private int meal;

    /**************************************/
    /* INSULIN CORRECTION LEVELS */
    /** InsulinNoCorrection */
    public static final int INC = 51;
    
    /** InsulinLightCorrection */
    public static final int ILC = 52;
    
    /** InsulinMediumCorrection */
    public static final int IMC = 53;
    
    /** InsulinHighCorrection */
    public static final int IHC = 54;
    
    /** Insulin */
    private int insulin;
    
    /**************************************************************************/
    /* METHODS */
    
    private static int initialGlucoseLevel(){
        int randomIndex = random.Random.randInt(0, 2);
        if(randomIndex == 0)
            return Human.GL;
        if(randomIndex == 1)
            return Human.GN;
        if(randomIndex == 2)
            return Human.GH1;
        return Human.GH2;
    }
    
    /**
     * Decrement the current glucose level.
     */
    public void decGlucoseLevel(){
        switch (this.bloodGlucoseLevel) {
            case Human.GL:
                this.bloodGlucoseLevel = Human.GL;
                break;
            case Human.GN:
                this.bloodGlucoseLevel = Human.GL;
                break;
            case Human.GH1:
                this.bloodGlucoseLevel = Human.GH1;
                break;
            case Human.GH2:
                this.bloodGlucoseLevel = Human.GH2;
                break;
            case Human.GVH:
                this.bloodGlucoseLevel = Human.GH2;
                break;
            case Human.GTH:
                this.bloodGlucoseLevel = Human.GVH;
                break;
        }    
    
    }

    /**
     * Increment the current glucose level.
     * This process should be based on the measurements of the
     * glucose continous monitor.
     */
    public void incGlucoseLevel(){
        switch(this.bloodGlucoseLevel){
                case Human.GL:
                    this.bloodGlucoseLevel = Human.GL;
                break; 
                case Human.GN:
                    this.bloodGlucoseLevel = Human.GH1;
                break;                
                case Human.GH1:
                    this.bloodGlucoseLevel = Human.GH2;
                break;
                case Human.GH2:
                    this.bloodGlucoseLevel = Human.GVH;
                break;
                case Human.GVH:
                    this.bloodGlucoseLevel = Human.GTH;
                break;
                case Human.GTH:
                    this.bloodGlucoseLevel = Human.GTH;
                break;
        }
    
    }
    
    /**
     * Executes an action
     * @param actionName Name of the action.
     */
    private void action(String actionName){
        try {
            Thread.sleep(250);
            System.out.println(actionName);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Simulate when the patient is going to eat a meal.
     */
    public void eatMeal(){
        this.bloodGlucoseLevel = Human.initialGlucoseLevel();
        this.exercise = Human.EN;
        this.meal = Human.M;
        this.insulin = Human.ILC;
        this.action("EAT standard meal");
    }

    /**
     * Simulate when the patient is going for a walk.
     */
    public void walk(){
        this.bloodGlucoseLevel = Human.GN;
        this.exercise = Human.EL;
        this.meal = Human.MN;
        this.insulin = Human.INC;       
        this.action("WALK");
    }

    /**
     * Simulate when the patient is going to work.
     */
    public void work(){
        this.bloodGlucoseLevel = Human.GN;
        this.exercise = Human.EN;
        this.meal = Human.MN;
        this.insulin = Human.INC;           
        this.action("WORK");
    }

   /**
     * Simulate when the patient is going to sleep.
     */
    public void sleep(){
        this.bloodGlucoseLevel = Human.GN;
        this.exercise = Human.EN;
        this.meal = Human.MN;
        this.insulin = Human.INC;       
        this.action("SLEEP");
    }    
    
   
    /**************************************************************************/
    /* Implementation of the variables as consultor methods */    

    public boolean EH(){ return this.exercise == EH;  }
    public boolean EL(){ return this.exercise == EL;  }
    public boolean EM(){ return this.exercise == EM;  }
    public boolean EN(){ return this.exercise == EN;  }
    
    public boolean GL(){ return this.bloodGlucoseLevel == GL;  }
    public boolean GN(){ return this.bloodGlucoseLevel == GN;  }
    public boolean GH1(){ return this.bloodGlucoseLevel == GH1;  }
    public boolean GH2(){ return this.bloodGlucoseLevel == GH2;  }
    public boolean GVH(){ return this.bloodGlucoseLevel == GVH;  }
    public boolean GTH(){ return this.bloodGlucoseLevel == GTH;  }
    
    public boolean INC(){ return this.insulin == INC;  }
    public boolean ILC(){ return this.insulin == ILC;  }
    public boolean IMC(){ return this.insulin == IMC;  }
    public boolean IHC(){ return this.insulin == IHC;  }

    public boolean M(){ return this.meal == M;  }
    public boolean MN(){ return this.meal == MN;  }
    public boolean MS(){ return this.meal == MS;  }
    
    /**************************************************************************/
    /* Convenience methods */  

    /**
     * Converts the blood glucose variable to string.
     * @return String representation of the blood glucose variable.
     */
    public String bloodGlucoseToString(){
        switch (this.bloodGlucoseLevel) {
            case Human.GL:
                return "GL";
            case Human.GN:
                return "GN";
            case Human.GH1:
                return "GH1";
            case Human.GH2:
                return "GH2";
            case Human.GVH:
                return "GVH";
            case Human.GTH:
                return "GTH";
        }
        return "";
    }
    
    /**
     * Converts the exercise variable to string.
     * @return String representation of the exercise variable.
     */
    public String exerciseToString(){
        switch (this.exercise) {
            case Human.EL:
                return "EL";
            case Human.EN:
                return "EN";
            case Human.EM:
                return "EM";
            case Human.EH:
                return "EH";
        }
        return "";
    }
    
    /**
     * Converts the insulin variable to string.
     * @return String representation of the insulin variable.
     */
    public String insulinToString(){
        switch (this.insulin) {
            case Human.IHC:
                return "IHC";
            case Human.ILC:
                return "ILC";
            case Human.IMC:
                return "IMC";
            case Human.INC:
                return "INC";
        }
        return "";
    }   
    
    /**
     * Converts the meal variable to string.
     * @return String representation of the meal variable.
     */
    public String mealToString(){
        switch (this.meal) {
            case Human.M:
                return "M";
            case Human.MS:
                return "MS";
            case Human.MN:
                return "MN";
        }
        return "";
    }       
    
    /**
     * Prints the state of the patient.
     */
    public void print(){
        System.out.println("BloodGlucose: "+this.bloodGlucoseToString());
        System.out.println("Exercise: "+this.exerciseToString());
        System.out.println("Insulin: "+this.insulinToString());
        System.out.println("Meal: "+this.mealToString());
    
    }
}
